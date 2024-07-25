/*
 *   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package software.amazon.cloudwatchlogs.emf.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import software.amazon.cloudwatchlogs.emf.environment.Environment;
import software.amazon.cloudwatchlogs.emf.environment.EnvironmentProvider;
import software.amazon.cloudwatchlogs.emf.exception.DimensionSetExceededException;
import software.amazon.cloudwatchlogs.emf.exception.InvalidDimensionException;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.sinks.SinkShunt;

class MetricAnnotationMediatorTest {
    private MetricsLogger logger;
    private EnvironmentProvider envProvider;
    private SinkShunt sink;
    private Environment environment;
    private final Random random = new Random();

    @BeforeEach
    public void setUp() {
        envProvider = mock(EnvironmentProvider.class);
        environment = mock(Environment.class);
        sink = new SinkShunt();

        when(envProvider.resolveEnvironment())
                .thenReturn(CompletableFuture.completedFuture(environment));
        when(environment.getSink()).thenReturn(sink);
        when(environment.getLogGroupName()).thenReturn("test-log-group");
        when(environment.getName()).thenReturn("test-env-name");
        when(environment.getType()).thenReturn("test-env-type");

        logger = new MetricsLogger(envProvider);
        MetricAnnotationMediator.loggers.put("_defaultLogger", logger);
    }

    @Test
    void testCountMetricAnnotation()
            throws InvalidDimensionException, DimensionSetExceededException,
                    JsonProcessingException {
        for (int i = 0; i < 10; i++) {
            countMethod();
        }

        MetricAnnotationMediator.flushAll();

        for (String log : sink.getLogEvents()) {
            System.out.println(log);
            ArrayList<String> metricNames = parseMetricNames(log);
            Assertions.assertEquals(
                    "MetricAnnotationMediatorTest.countMethod.Count", metricNames.get(0));
            Assertions.assertEquals(
                    Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
                    (ArrayList<Double>) parseMetricByName(log, metricNames.get(0)));
        }
    }

    @Test
    void testTimeMetricAnnotation() throws JsonProcessingException {
        MetricAnnotationMediator.addLogger("example logger", new MetricsLogger(envProvider));

        for (int i = 0; i < 5; i++) {
            timeMethod();
        }

        multiAnnotationMethod();
        MetricAnnotationMediator.flushAll();

        for (String log : sink.getLogEvents()) {
            System.out.println(log);
            ArrayList<String> metricNames = parseMetricNames(log);
            Assertions.assertEquals("MetricAnnotationMediatorTest.timeMethod.Time", metricNames.get(0));
            ArrayList<Double> metricValues = (ArrayList<Double>) parseMetricByName(log, metricNames.get(0));
            assertTrue(metricValues.stream().allMatch(value -> value >= 20 && value <= 300)); // Add a little wiggle room for the timing of the method call
        }
    }

    @CountMetric
    void countMethod() {

    }

    @CountMetric(logger = "example logger")
    void countExampleLogger() {
    }

    @TimeMetric
    void timeMethod() {
        int waitTime = random.nextInt(181) + 20; // Random number between 20 and 200
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            // Handle interruption if needed
            Thread.currentThread().interrupt();
        }
    }

    @TimeMetric(logger="example logger")
    void multiAnnotationMethod() {
        int waitTime = random.nextInt(181) + 20; // Random number between 20 and 200
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            // Handle interruption if needed
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> parseMetricNames(String event) throws JsonProcessingException {
        Map<String, Object> rootNode = parseRootNode(event);
        Map<String, Object> metadata = (Map<String, Object>) rootNode.get("_aws");
        ArrayList<Map<String, Object>> metricDirectives =
                (ArrayList<Map<String, Object>>) metadata.get("CloudWatchMetrics");
        ArrayList<Map<String, String>> metrics =
                (ArrayList<Map<String, String>>) metricDirectives.get(0).get("Metrics");

        ArrayList<String> metricNames = new ArrayList<>();
        for (Map<String, String> metric : metrics) {
            metricNames.add(metric.get("Name"));
        }
        return metricNames;
    }

    @SuppressWarnings("unchecked")
    private Object parseMetricByName(String event, String name) throws JsonProcessingException {
        Map<String, Object> rootNode = parseRootNode(event);
        return rootNode.get(name);
    }

    private Map<String, Object> parseRootNode(String event) throws JsonProcessingException {
        return new JsonMapper().readValue(event, new TypeReference<Map<String, Object>>() {});
    }
}
