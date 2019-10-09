package io.quarkus.cxf.jaxrs.deployment;

import java.util.Map;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * A build item that represents the configuration of the CXF JAXRS server.
 */
public final class CxfJaxrsServerConfigBuildItem extends SimpleBuildItem {

    private final String path;

    private final Map<String, String> initParameters;

    public CxfJaxrsServerConfigBuildItem(String path, Map<String, String> initParameters) {
        this.path = path;
        this.initParameters = initParameters;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }
}