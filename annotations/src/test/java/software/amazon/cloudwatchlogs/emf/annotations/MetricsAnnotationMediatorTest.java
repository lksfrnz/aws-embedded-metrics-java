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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.cloudwatchlogs.emf.environment.Environment;
import software.amazon.cloudwatchlogs.emf.environment.EnvironmentProvider;
import software.amazon.cloudwatchlogs.emf.exception.DimensionSetExceededException;
import software.amazon.cloudwatchlogs.emf.exception.InvalidDimensionException;
import software.amazon.cloudwatchlogs.emf.logger.MetricsLogger;
import software.amazon.cloudwatchlogs.emf.sinks.SinkShunt;

import java.util.Random;

class MetricAnnotationMediatorTest {
    private MetricsLogger logger;
    private EnvironmentProvider envProvider;
    private SinkShunt sink;
    private Environment environment;

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
    }

    @Test
    void testCountMetricAnnotation()
            throws InvalidDimensionException, DimensionSetExceededException {
        MetricAnnotationMediator annotationLogger = MetricAnnotationMediator.getInstance();
        annotationLogger.loggers.put("_defaultLogger", logger);

        for (int i = 0; i < 10; i++) {
            count();
        }

        MetricAnnotationMediator.getDefaultLogger().flush();

        System.out.println("Sink test");
        for (String log : sink.getLogEvents()) {
            System.out.println(log);
        } 
        assertTrue(false);
    }

    @CountMetric
    void count() {
        System.out.println("triggered count");
    }

    @CountMetric(logger="example logger")
    void countExampleLogger() {
        System.out.println("triggered count");
    }

    @TimeMetric
    void time() {
        Random random = new Random();
        int waitTime = random.nextInt(181) + 20; // Random number between 20 and 200
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            // Handle interruption if needed
            Thread.currentThread().interrupt();
        }
    }
}
