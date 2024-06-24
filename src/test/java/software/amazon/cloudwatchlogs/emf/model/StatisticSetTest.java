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
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class StatisticSetTest {
    @Test
    public void testSerializeStatisticSetWithoutUnitWithHighStorageResolution()
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StatisticSetBuilder statisticSet = new StatisticSetBuilder(StorageResolution.HIGH);
        statisticSet.addValue(10);
        statisticSet.setName("Time");
        String metricString = objectMapper.writeValueAsString(statisticSet);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"None\",\"StorageResolution\":1}", metricString);
    }

    @Test
    public void testSerializeStatisticSetWithUnitWithoutStorageResolution()
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StatisticSetBuilder statisticSet = new StatisticSetBuilder(Unit.MILLISECONDS);
        statisticSet.addValue(10);
        statisticSet.setName("Time");
        String metricString = objectMapper.writeValueAsString(statisticSet);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"Milliseconds\"}", metricString);
    }

    @Test
    public void testSerializeStatisticSetWithoutUnitWithStandardStorageResolution()
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StatisticSetBuilder statisticSet = new StatisticSetBuilder(StorageResolution.STANDARD);
        statisticSet.addValue(10);
        statisticSet.setName("Time");
        String metricString = objectMapper.writeValueAsString(statisticSet);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"None\"}", metricString);
    }

    @Test
    public void testSerializeStatisticSetWithoutUnit() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StatisticSetBuilder statisticSet = new StatisticSetBuilder();
        statisticSet.setName("Time");
        String metricString = objectMapper.writeValueAsString(statisticSet);

        assertEquals("{\"Name\":\"Time\",\"Unit\":\"None\"}", metricString);
    }

    @Test
    public void testSerializeStatisticSet() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StatisticSetBuilder statisticSet =
                new StatisticSetBuilder(Unit.MILLISECONDS, StorageResolution.HIGH);
        statisticSet.addValue(10);
        statisticSet.setName("Time");
        String metricString = objectMapper.writeValueAsString(statisticSet);

        assertEquals(
                "{\"Name\":\"Time\",\"Unit\":\"Milliseconds\",\"StorageResolution\":1}",
                metricString);
    }

    @Test
    public void testAddValues() {
        StatisticSetBuilder ssb = new StatisticSetBuilder();
        ssb.addValue(10);
        assertEquals(new Statistics(10., 10., 1, 10.), ssb.getValues());

        ssb.addValue(20);
        assertEquals(new Statistics(20., 10., 2, 30.), ssb.getValues());
    }

    @Test
    public void testManyAddValues() {
        StatisticSetBuilder ssb = new StatisticSetBuilder();
        for (int i = 1; i < 100; i++) {
            ssb.addValue(i);
            assertEquals(new Statistics(i, 1., i, i * (i + 1) / 2), ssb.getValues());
        }
    }

    @Test
    public void testBuildBuilder() {
        StatisticSetBuilder ssb = new StatisticSetBuilder();
        ssb.addValue(10);
        StatisticSet ss = ssb.build();
        assertEquals(ss.getValues(), ssb.getValues());

        assertEquals(ss.name, null);
        ss.setName("test");
        assertEquals(ss.name, "test");
    }

    @Test
    public void testCreateImmutableStatisticSet() {
        StatisticSet ss = new StatisticSet(Unit.NONE, StorageResolution.STANDARD, 10, 1, 11, 100);
        assertEquals(new Statistics(10, 1, 11, 100), ss.getValues());
    }

    @Test
    public void testImpossibleStatisticSet() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new StatisticSet(
                                Unit.NONE,
                                StorageResolution.STANDARD,
                                10,
                                1,
                                2,
                                100)); // Sum too big
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new StatisticSet(
                                Unit.NONE,
                                StorageResolution.STANDARD,
                                10,
                                1,
                                3,
                                11)); // Sum too small
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new StatisticSet(
                                Unit.NONE,
                                StorageResolution.STANDARD,
                                10,
                                1,
                                0,
                                100)); // Count == 0 for non-zero set
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new StatisticSet(
                                Unit.NONE, StorageResolution.STANDARD, 0, 1, 1, 0)); // min > max
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new StatisticSet(
                                Unit.NONE,
                                StorageResolution.STANDARD,
                                10,
                                1,
                                -1,
                                10)); // Negative count
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new StatisticSet(
                                Unit.NONE,
                                StorageResolution.STANDARD,
                                10,
                                1,
                                1,
                                1)); // different max and min for 1 count
    }
}