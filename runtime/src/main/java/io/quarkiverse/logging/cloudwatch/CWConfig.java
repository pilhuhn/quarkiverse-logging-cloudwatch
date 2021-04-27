package io.quarkiverse.logging.cloudwatch;

import java.util.Optional;
import java.util.logging.Level;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Configuration for Sentry logging.
 */
@ConfigRoot(phase = ConfigPhase.RUN_TIME, name = "log.cloudwatch")
public class CWConfig {

    /**
     * Determine whether to enable the Cloudwatch logging extension.
     */
    @ConfigItem(name = ConfigItem.PARENT)
    boolean enabled;

    /**
     * CW access key ID
     */
    @ConfigItem
    public String accessKeyId;

    /**
     * CW access key secret
     *
     */
    @ConfigItem
    public String accessKeySecret;

    /**
     * Region of deployment
     */
    @ConfigItem
    public String region;

    /**
     * CW log group
     */
    @ConfigItem
    public String logGroup;

    /**
     * CW log stream
     *
     */
    @ConfigItem
    public String logStreamName;

    /**
     * App label
     *
     * If present, a label of app=\<appLabel> is supplied
     */
    @ConfigItem
    public Optional<String> appLabel;

    /**
     * The CW log level.
     */
    @ConfigItem(defaultValue = "WARN")
    public Level level;

}
