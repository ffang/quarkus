package io.quarkus.cxf.jaxrs.deployment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.cxf.jaxrs.runtime.CXFJaxrsQuarkusServlet;
import io.quarkus.cxf.jaxrs.runtime.ServiceImplRecorder;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.substrate.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.substrate.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.substrate.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.builditem.substrate.SubstrateProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.substrate.SubstrateResourceBuildItem;
import io.quarkus.undertow.deployment.ServletBuildItem;
import io.quarkus.undertow.deployment.ServletInitParamBuildItem;

class CxfJaxrsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CxfJaxrsProcessor.class);

    private static final String JAX_RS_CXF_SERVLET = "org.apache.cxf.transport.servlet.CXFNonSpringServlet";

    private static final DotName JAXRS_WEBSERVICE_ANNOTATION = DotName.createSimple("javax.ws.rs.Path");

    /**
     * JAX-RS configuration.
     */
    CxfConfig cxfConfig;

    @BuildStep
    public void build(
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchy,
            BuildProducer<SubstrateProxyDefinitionBuildItem> proxyDefinition,
            BuildProducer<SubstrateResourceBuildItem> resource,
            BuildProducer<RuntimeInitializedClassBuildItem> runtimeClasses,
            BuildProducer<BytecodeTransformerBuildItem> transformers,
            BuildProducer<CxfJaxrsServerConfigBuildItem> cxfServerConfig,
            BuildProducer<UnremovableBeanBuildItem> unremovableBeans,
            CombinedIndexBuildItem combinedIndexBuildItem,
            BeanArchiveIndexBuildItem beanArchiveIndexBuildItem) throws Exception {
        IndexView index = combinedIndexBuildItem.getIndex();

        for (AnnotationInstance annotation : index.getAnnotations(JAXRS_WEBSERVICE_ANNOTATION)) {
            if (annotation.target().kind() == AnnotationTarget.Kind.CLASS) {
                reflectiveClass
                        .produce(new ReflectiveClassBuildItem(true, true, annotation.target().asClass().name().toString()));
            }
        }

        Map<String, String> cxfInitParameters = new HashMap<>();

        cxfServerConfig.produce(new CxfJaxrsServerConfigBuildItem(cxfConfig.path, cxfInitParameters));
    }

    @BuildStep
    public void build(
            Optional<CxfJaxrsServerConfigBuildItem> cxfServerConfig,
            BuildProducer<FeatureBuildItem> feature,
            BuildProducer<ServletBuildItem> servlet,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<ServletInitParamBuildItem> servletInitParameters) throws Exception {
        feature.produce(new FeatureBuildItem(FeatureBuildItem.CXF_JAXRS));

        LOGGER.info("======>build steps");

        if (cxfServerConfig.isPresent()) {
            String path = cxfServerConfig.get().getPath();

            String mappingPath = getMappingPath(path);

            servlet.produce(ServletBuildItem.builder(JAX_RS_CXF_SERVLET, CXFJaxrsQuarkusServlet.class.getName())
                    .setLoadOnStartup(1).addMapping(mappingPath).setAsyncSupported(true).build());
            reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, CXFJaxrsQuarkusServlet.class.getName()));

            for (Entry<String, String> initParameter : cxfServerConfig.get().getInitParameters().entrySet()) {
                servletInitParameters.produce(new ServletInitParamBuildItem(initParameter.getKey(), initParameter.getValue()));
            }

        }
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    public void buildServiceImplList(ServiceImplRecorder recorder) {
        LOGGER.info("=====invoke recorder");
        recorder.addJaxrsService(cxfConfig.webServicesPaths, cxfConfig.springBus);
    }

    private String getMappingPath(String path) {
        String mappingPath;
        if (path.endsWith("/")) {
            mappingPath = path + "*";
        } else {
            mappingPath = path + "/*";
        }
        return mappingPath;
    }

}
