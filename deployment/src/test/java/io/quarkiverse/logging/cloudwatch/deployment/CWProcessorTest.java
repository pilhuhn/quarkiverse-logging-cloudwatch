package io.quarkiverse.logging.cloudwatch.deployment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import io.quarkiverse.logging.cloudwatch.CWConfig;
import io.quarkiverse.logging.cloudwatch.CWHandlerValueFactory;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;

class CWProcessorTest {

    private final CWProcessor cwProcessor = new CWProcessor();

    @Test
    void shouldHaveLogCloudwatchFeatureName() {
        final FeatureBuildItem feature = cwProcessor.feature();
        assertEquals("log-cloudwatch", feature.getName());
    }

    @Test
    void shouldReturnEmptyBuildItemWhenCWConfigIsEmpty() {
        CWConfig cwConfig = new CWConfig();
        CWHandlerValueFactory cwHandlerValueFactory = new CWHandlerValueFactory();

        final LogHandlerBuildItem logHandlerBuildItem = cwProcessor.addCloudwatchLogHandler(cwConfig, cwHandlerValueFactory);
        assertFalse(logHandlerBuildItem.getHandlerValue().getValue().isPresent());
    }
}
