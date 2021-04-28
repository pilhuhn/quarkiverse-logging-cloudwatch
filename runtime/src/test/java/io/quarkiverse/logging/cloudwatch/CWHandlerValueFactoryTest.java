package io.quarkiverse.logging.cloudwatch;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.logging.Handler;

import org.junit.jupiter.api.Test;

import io.quarkus.runtime.RuntimeValue;

class CWHandlerValueFactoryTest {

    private final CWHandlerValueFactory testee = new CWHandlerValueFactory();

    @Test
    void shouldReturnEmptyRuntimeValueWhenConfigIsEmptyAndHasEnabledFalseByDefault() {
        CWConfig config = new CWConfig();

        final RuntimeValue<Optional<Handler>> actualRuntimeValue = testee.create(config);

        assertFalse(actualRuntimeValue.getValue().isPresent());
    }

    @Test
    void shouldReturnEmptyRuneTimeValueWhenConfigIsNotEnabled() {
        CWConfig config = new CWConfig();
        config.enabled = false;

        final RuntimeValue<Optional<Handler>> actualRuntimeValue = testee.create(config);

        assertFalse(actualRuntimeValue.getValue().isPresent());
    }
}
