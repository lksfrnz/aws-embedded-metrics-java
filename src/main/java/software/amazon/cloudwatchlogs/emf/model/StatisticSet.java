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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

/** Represents the StatisticSet of the EMF schema. */
public class StatisticSet extends Metric<Statistics> {

    StatisticSet(
            Unit unit,
            StorageResolution storageResolution,
            double max,
            double min,
            int count,
            double sum) {
        this(unit, storageResolution, new Statistics(max, min, count, sum));
    }

    protected StatisticSet(
            @NonNull String name,
            Unit unit,
            StorageResolution storageResolution,
            Statistics statistics) {
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
    protected StatisticSet getMetricValuesUnderSize(int size) {
        return this;
    }

    @Override
    protected StatisticSet getMetricValuesOverSize(int size) {
        return null;
    }

    @Override
    public boolean hasValidValues() {
        return values != null && values.count > 0;
    }

    public static StatisticSetBuilder builder() {
        return new StatisticSetBuilder();
    }

    public static class StatisticSetBuilder
            extends Metric.MetricBuilder<Statistics, StatisticSetBuilder> {

        @Override
        protected StatisticSetBuilder getThis() {
            return this;
        }

        public StatisticSetBuilder() {
            values = new Statistics();
        }

        @Override
        public StatisticSetBuilder addValue(double value) {
            this.values.addValue(value);
            return this;
        }

        public StatisticSetBuilder values(@NonNull Statistics values) {
            this.values = values;
            return this;
        }

        @Override
        public StatisticSet build() {
            if (name == null) {
                return new StatisticSet(unit, storageResolution, values);
            }
            return new StatisticSet(name, unit, storageResolution, values);
        }
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
