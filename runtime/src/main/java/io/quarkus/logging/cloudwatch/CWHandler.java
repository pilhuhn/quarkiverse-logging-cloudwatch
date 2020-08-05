/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.logging.cloudwatch;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.jboss.logmanager.ExtLogRecord;


/**
 * @author hrupp
 */
public class CWHandler extends Handler {

    private String appLabel;
    private final AWSLogs awsLogs;
    private final String logStreamName;
    private final String logGroupName;
    private String sequenceToken;
    private String environment;


    public CWHandler(AWSLogs awsLogs, String logGroup, String logStreamName, String token) {
        this.logGroupName = logGroup;
        this.awsLogs = awsLogs;
        this.logStreamName = logStreamName;
        sequenceToken = token;
    }

    @Override
    public void publish(LogRecord record) {

        // Skip messages that are below the configured threshold
        if (record.getLevel().intValue() < getLevel().intValue()) {
            return;
        }

        Map<String, String> tags = new HashMap<>();

        String host = record instanceof ExtLogRecord ? ((ExtLogRecord) record).getHostName() : null;
        if (record.getLoggerName().equals("__AccessLog")) {
            tags.put("type", "access");
        }
        if (host != null && !host.isEmpty()) {
            tags.put("host", host);
        }
        if (appLabel != null && !appLabel.isEmpty()) {
            tags.put("app", appLabel);
        }

        if (environment != null && !environment.isEmpty()) {
            tags.put("env",environment);
        }

        tags.put("level", record.getLevel().getName());

        String msg;
        if (record.getParameters() != null && record.getParameters().length > 0) {
            switch (((ExtLogRecord) record).getFormatStyle()) {
                case PRINTF:
                    msg = String.format(record.getMessage(), record.getParameters());
                    break;
                case MESSAGE_FORMAT:
                    msg = MessageFormat.format(record.getMessage(), record.getParameters());
                    break;
                default: // == NO_FORMAT
                    msg = record.getMessage();
            }
        } else {
            msg = record.getMessage();
        }

        if (record instanceof ExtLogRecord) {

            String tid = ((ExtLogRecord) record).getMdc("traceId");
            if (tid!=null) {
                msg = msg + ", traceId=" + tid;
            }
        }

        String body = assemblePayload(msg, tags);

        PutLogEventsRequest request = new PutLogEventsRequest();
        Collection<InputLogEvent> logEvents = new ArrayList<>();
        logEvents.add(new InputLogEvent()
                        .withMessage(body)
                        .withTimestamp(System.currentTimeMillis())); // TODO get from log record?
        request.setLogEvents(logEvents);
        request.setLogGroupName(logGroupName);
        request.setLogStreamName(logStreamName);
        request.setSequenceToken(sequenceToken);
        sequenceToken = awsLogs.putLogEvents(request).getNextSequenceToken();

    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    private String assemblePayload(String message, Map<String, String> tags) {

        StringBuilder sb = new StringBuilder(message);
        if (!tags.isEmpty()) {
            sb.append(", ");
            Iterator<Map.Entry<String, String>> iterator = tags.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }

    public void setAppLabel(String label) {
        if (label != null) {
            this.appLabel = label;
        }
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
