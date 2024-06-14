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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import software.amazon.cloudwatchlogs.emf.serializers.StorageResolutionFilter;
import software.amazon.cloudwatchlogs.emf.serializers.StorageResolutionSerializer;
import software.amazon.cloudwatchlogs.emf.serializers.UnitDeserializer;
import software.amazon.cloudwatchlogs.emf.serializers.UnitSerializer;

abstract class Metric {
    // @NonNull
    @Getter
    @JsonProperty("Name")
    protected String name;

    @Getter
    @JsonProperty("Unit")
    @JsonSerialize(using = UnitSerializer.class)
    @JsonDeserialize(using = UnitDeserializer.class)
    protected Unit unit;

    @Getter
    @JsonProperty("StorageResolution")
    @JsonInclude(
            value = JsonInclude.Include.CUSTOM,
            valueFilter =
                    StorageResolutionFilter.class) // Do not serialize when valueFilter is true
    @JsonSerialize(using = StorageResolutionSerializer.class)
    protected StorageResolution storageResolution;

    protected void setName(String name) {
        this.name = name;
    }
}

interface MetricBuilder {
    // @Getter
    // @Setter
    // Unit unit;

    // @Getter
    // @Setter
    // StorageResolution storageResolution;

    // @Getter
    // String name;

    // protected void setName(String name) {
    //     this.name = name;
    // }

    // MetricBuilder(Unit unit, StorageResolution storageResolution, double value) {
    //     this.unit = unit;
    //     this.storageResolution = storageResolution;
    //     addValue(value);
    // }

    // MetricBuilder(Unit unit, StorageResolution storageResolution) {
    //     this.unit = unit;
    //     this.storageResolution = storageResolution;
    // }

    // MetricBuilder(Unit unit, double value) {
    //     this(unit, StorageResolution.STANDARD, value);
    // }

    // MetricBuilder(double value) {
    //     this(Unit.NONE, StorageResolution.STANDARD, value);
    // }

    // MetricBuilder(Unit unit) {
    //     this(unit, StorageResolution.STANDARD);
    // }

    // MetricBuilder(StorageResolution storageResolution) {
    //     this(Unit.NONE, storageResolution);
    // }

    // MetricBuilder() {
    //     this(Unit.NONE, StorageResolution.STANDARD);
    // }

    void addValue(double value);

    Metric build();
}
