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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Test;

public class MetadataTest {

    @Test
    public void testSerializeMetadata() throws JsonProcessingException {
        Metadata metadata = new Metadata();
        Instant now = Instant.now();
        metadata.setTimestamp(now);
        JsonMapper objectMapper = new JsonMapper();
        String output = objectMapper.writeValueAsString(metadata);

        Map<String, Object> metadata_map =
                objectMapper.readValue(output, new TypeReference<Map<String, Object>>() {});

        assertEquals(metadata_map.keySet().size(), 2);
        assertEquals(metadata_map.get("Timestamp"), now.toEpochMilli());
        assertEquals(metadata_map.get("CloudWatchMetrics"), new ArrayList());
    }

    @Test
    public void testSerializeMetadataWithCustomValue() throws JsonProcessingException {
        Metadata metadata = new Metadata();
        Instant now = Instant.now();
        metadata.setTimestamp(now);
        String property = "foo";
        String expectedValue = "bar";
        metadata.putCustomMetadata(property, expectedValue);

        JsonMapper objectMapper = new JsonMapper();
        String output = objectMapper.writeValueAsString(metadata);

        Map<String, Object> metadata_map =
                objectMapper.readValue(output, new TypeReference<Map<String, Object>>() {});

        assertEquals(metadata_map.keySet().size(), 3);
        assertEquals(metadata_map.get("Timestamp"), now.toEpochMilli());
        assertEquals(metadata_map.get("CloudWatchMetrics"), new ArrayList());
        assertEquals(metadata_map.get(property), expectedValue);
    }
}