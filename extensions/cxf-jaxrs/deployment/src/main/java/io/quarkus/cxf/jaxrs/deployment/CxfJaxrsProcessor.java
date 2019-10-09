package io.quarkus.cxf.jaxrs.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class CxfJaxrsProcessor {

    private static final String FEATURE = "cxf-jaxrs";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

}
