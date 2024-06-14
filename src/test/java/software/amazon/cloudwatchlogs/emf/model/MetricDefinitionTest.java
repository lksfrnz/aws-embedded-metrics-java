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

package software.amazon.cloudwatchlogs.emf.model;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class MetricDefinitionTest {

    // @Test(expected = NullPointerException.class)
    // public void testThrowExceptionIfNameIsNull() {
    //     new MetricDefinition(null);
    // }

    @Test
    public void testSerializeMetricDefinitionWithoutUnitWithHighStorageResolution()
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MetricDefinitionBuilder metricDefinition =
                new MetricDefinitionBuilder(StorageResolution.HIGH, 10);
        metricDefinition.setName("Time");
        String metricString = objectMapper.writeValueAsString(metricDefinition);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"None\",\"StorageResolution\":1}", metricString);
    }

    @Test
    public void testSerializeMetricDefinitionWithUnitWithoutStorageResolution()
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MetricDefinitionBuilder metricDefinition = new MetricDefinitionBuilder(Unit.MILLISECONDS, 10);
        metricDefinition.setName("Time");
        String metricString = objectMapper.writeValueAsString(metricDefinition);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"Milliseconds\"}", metricString);
    }

    @Test
    public void testSerializeMetricDefinitionWithoutUnitWithStandardStorageResolution()
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MetricDefinitionBuilder metricDefinition =
                new MetricDefinitionBuilder(StorageResolution.STANDARD, 10);
        metricDefinition.setName("Time");
        String metricString = objectMapper.writeValueAsString(metricDefinition);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"None\"}", metricString);
    }

    @Test
    public void testSerializeMetricDefinitionWithoutUnit() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MetricDefinitionBuilder metricDefinition = new MetricDefinitionBuilder();
        metricDefinition.setName("Time");
        String metricString = objectMapper.writeValueAsString(metricDefinition);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"None\"}", metricString);
    }

    @Test
    public void testSerializeMetricDefinition() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MetricDefinitionBuilder metricDefinition =
                new MetricDefinitionBuilder(Unit.MILLISECONDS, StorageResolution.HIGH, 10);
        metricDefinition.setName("Time");
        String metricString = objectMapper.writeValueAsString(metricDefinition);

        assertEquals(
                "{\"Name\":\"Time\",\"Unit\":\"Milliseconds\",\"StorageResolution\":1}",
                metricString);
    }

    @Test
    public void testAddValue() {
        MetricDefinitionBuilder md = new MetricDefinitionBuilder(Unit.MICROSECONDS, 10);
        md.setName("Time");
        assertEquals(Collections.singletonList(10d), md.getValues());

        md.addValue(20);
        assertEquals(Arrays.asList(10d, 20d), md.getValues());
    }
}
