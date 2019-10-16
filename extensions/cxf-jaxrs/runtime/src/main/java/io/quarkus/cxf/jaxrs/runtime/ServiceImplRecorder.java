package io.quarkus.cxf.jaxrs.runtime;

import java.util.Map;
import java.util.Map.Entry;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class ServiceImplRecorder {

    public void addJaxrsService(Map<String, String> webServicesPaths) {
        for (Entry<String, String> webServicesByPath : webServicesPaths.entrySet()) {

            CXFJaxrsQuarkusServlet.publish(webServicesByPath.getKey(), webServicesByPath.getValue());
        }
    }

}