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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

/** Represents the StatisticSet of the EMF schema. */
class StatisticSet extends Metric {
    @JsonIgnore @NonNull protected Statistics values;

    StatisticSet(
            Unit unit,
            StorageResolution storageResolution,
            double max,
            double min,
            int count,
            double sum) {
        this.unit = unit;
        this.storageResolution = storageResolution;
        this.values = new Statistics(max, min, count, sum);
    }

    StatisticSet(
            String name, Unit unit, StorageResolution storageResolution, Statistics statistics) {
        this.unit = unit;
        this.storageResolution = storageResolution;
        this.values = statistics;
        this.name = name;
    }

    StatisticSet(Unit unit, StorageResolution storageResolution, Statistics statistics) {
        this.unit = unit;
        this.storageResolution = storageResolution;
        this.values = statistics;
    }

    @Override
    Statistics getValues() {
        return values;
    }

    @Override
    protected Metric getMetricValuesUnderSize(int size) {
        return this;
    }

    @Override
    protected Metric getMetricValuesOverSize(int size) {
        return null;
    }
}

/** Builds StatisticSet */
public class StatisticSetBuilder extends StatisticSet implements MetricBuilder {

    StatisticSetBuilder(
            Unit unit,
            StorageResolution storageResolution,
            double max,
            double min,
            int count,
            double sum) {
        super(unit, storageResolution, max, min, count, sum);
    }

    protected StatisticSetBuilder(String name, Unit unit, StorageResolution storageResolution) {
        super(name, unit, storageResolution, new Statistics());
    }

    StatisticSetBuilder(Unit unit, StorageResolution storageResolution) {
        super(unit, storageResolution, new Statistics());
    }

    StatisticSetBuilder(StorageResolution storageResolution) {
        this(Unit.NONE, storageResolution);
    }

    StatisticSetBuilder(Unit unit) {
        this(unit, StorageResolution.STANDARD);
    }

    StatisticSetBuilder() {
        this(Unit.NONE, StorageResolution.STANDARD);
    }

    /** @return a built version of this metric. */
    @Override
    public StatisticSet build() {
        return (StatisticSet) this;
    }

    /** @param value a value to add to the metric. */
    @Override
    public void addValue(double value) {
        values.addValue(value);
    }
}

class Statistics {
    Statistics(double max, double min, int count, double sum) {
        if (max < min
                || (count == 0 && sum != 0)
                || count < 0
                || (count == 1 && max != min)
                || min + max * (count - 1) < sum
                || max + min * (count - 1) > sum) {
            throw new IllegalArgumentException(
                    "This is an impossible statistic set, there is no set of values that can create these statistics.");
        }
        this.max = max;
        this.min = min;
        this.count = count;
        this.sum = sum;
    }

    Statistics() {
        count = 0;
        sum = 0.;
    };

    @JsonProperty("Max")
    public Double max;

    @JsonProperty("Min")
    public Double min;

    @JsonProperty("Count")
    public int count;

    @JsonProperty("Sum")
    public Double sum;

    void addValue(double value) {
        count++;
        sum += value;
        if (max == null || value > max) {
            max = value;
        }
        if (min == null || value < min) {
            min = value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return count == that.count
                && Double.compare(that.sum, sum) == 0
                && Double.compare(that.max, max) == 0
                && Double.compare(that.min, min) == 0;
    }
}
