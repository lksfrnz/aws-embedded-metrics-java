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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

/** Represents the MetricDefinition of the EMF schema. */
// @AllArgsConstructor
class MetricDefinition extends Metric {
    @JsonIgnore @NonNull @Getter protected List<Double> values;

    MetricDefinition(Unit unit, StorageResolution storageResolution, List<Double> values) {
        this.unit = unit;
        this.storageResolution = StorageResolution.STANDARD;
        this.values = values;
    }

    MetricDefinition(
            String name, Unit unit, StorageResolution storageResolution, List<Double> values) {
        this.unit = unit;
        this.storageResolution = StorageResolution.STANDARD;
        this.values = values;
        this.name = name;
    }
}

public class MetricDefinitionBuilder extends MetricDefinition implements MetricBuilder {

    MetricDefinitionBuilder(Unit unit, StorageResolution storageResolution, List<Double> values) {
        super(unit, storageResolution, values);
    }

    protected MetricDefinitionBuilder(
            String Name, Unit unit, StorageResolution storageResolution, Double value) {
        this(unit, storageResolution, Arrays.asList(value));
        setName(name);
    }

    MetricDefinitionBuilder(Unit unit, StorageResolution storageResolution) {
        this(unit, storageResolution, new ArrayList<>());
    }

    MetricDefinitionBuilder() {
        this(Unit.NONE, StorageResolution.STANDARD, new ArrayList<>());
    }

    MetricDefinitionBuilder(double value) {
        this(Unit.NONE, StorageResolution.STANDARD, value);
    }

    MetricDefinitionBuilder(Unit unit, double value) {
        this(unit, StorageResolution.STANDARD, new ArrayList<>(Arrays.asList(value)));
    }

    MetricDefinitionBuilder(StorageResolution storageResolution, double value) {
        this(Unit.NONE, storageResolution, new ArrayList<>(Arrays.asList(value)));
    }

    MetricDefinitionBuilder(Unit unit, StorageResolution storageResolution, double value) {
        this(unit, storageResolution, new ArrayList<>(Arrays.asList(value)));
    }

    public MetricDefinition build() {
        return (MetricDefinition) this;
    }

    public void addValue(double value) {
        this.values.add(value);
    }
}
