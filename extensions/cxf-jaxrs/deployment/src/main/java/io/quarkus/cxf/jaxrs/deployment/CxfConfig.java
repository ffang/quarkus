package io.quarkus.cxf.jaxrs.deployment;

import java.util.Map;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot
final class CxfConfig {

    /**
     * Set this to override the default path for JAX-RS resources if there are no
     * annotated application classes.
     */
    @ConfigItem(defaultValue = "/")
    String path;

    /**
     * Set a spring configuration file which can be used to initialize a CXF spring bus
     */
    @ConfigItem(defaultValue = "", name = "springbus")
    String springBus;

    /**
     * Choose the path of each jaxrs web services.
     */
    @ConfigItem(name = "webservice")
    Map<String, String> webServicesPaths;
}
