package io.quarkiverse.logging.cloudwatch;

import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Logger;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.CreateLogStreamResult;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.LogStream;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class CWHandlerValueFactory {

    Logger log = Logger.getLogger("LoggingCloudWatch");

    public RuntimeValue<Optional<Handler>> create(final CWConfig config) {

        if (!config.enabled) {
            log.fine("--- LogCloudwatch is not enabled ---");
            return new RuntimeValue<>(Optional.empty());
        }

        // Init CloudWatch

        AWSLogsClientBuilder clientBuilder = AWSLogsClientBuilder.standard();
        clientBuilder.setCredentials(new CWCredentialsProvider(config));
        clientBuilder.setRegion(config.region);

        AWSLogs awsLogs = clientBuilder.build();
        String token = createLogStreamIfNeeded(awsLogs, config);

        CWHandler handler = new CWHandler(awsLogs, config.logGroup, config.logStreamName, token);
        handler.setLevel(config.level);
        handler.setAppLabel(config.appLabel.orElse(""));
        return new RuntimeValue<>(Optional.of(handler));
    }

    private String createLogStreamIfNeeded(AWSLogs awsLogs, CWConfig config) {

        String token = null;

        DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest(config.logGroup);
        // We need to filter down, as CW returns by default only 50 streams and ours may not be in it.
        describeLogStreamsRequest.withLogStreamNamePrefix(config.logStreamName);
        List<LogStream> logStreams = awsLogs.describeLogStreams(describeLogStreamsRequest).getLogStreams();

        boolean found = false;
        for (LogStream ls : logStreams) {
            if (ls.getLogStreamName().equals(config.logStreamName)) {
                found = true;
                token = ls.getUploadSequenceToken();
            }
        }

        if (!found) {
            CreateLogStreamResult logStream = awsLogs
                    .createLogStream(new CreateLogStreamRequest(config.logGroup, config.logStreamName));
        }
        return token;
    }

}
