package io.quarkiverse.logging.cloudwatch.deployment;

import io.quarkiverse.logging.cloudwatch.CWConfig;
import io.quarkiverse.logging.cloudwatch.CWHandlerValueFactory;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;

class CWProcessor {

    private static final String FEATURE = "log-cloudwatch";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    LogHandlerBuildItem addCloudwatchLogHandler(final CWConfig cwConfig,
            final CWHandlerValueFactory lokiHandlerValueFactory) {
        return new LogHandlerBuildItem(lokiHandlerValueFactory.create(cwConfig));
    }

    //    @BuildStep
    //    ExtensionSslNativeSupportBuildItem activateSslNativeSupport() {
    //        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    //    }

}
