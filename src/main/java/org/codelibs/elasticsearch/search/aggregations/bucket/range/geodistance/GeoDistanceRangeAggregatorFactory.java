/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.codelibs.elasticsearch.search.aggregations.bucket.range.geodistance;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedNumericDocValues;
import org.codelibs.elasticsearch.common.geo.GeoDistance;
import org.codelibs.elasticsearch.common.geo.GeoDistance.FixedSourceDistance;
import org.codelibs.elasticsearch.common.geo.GeoPoint;
import org.codelibs.elasticsearch.common.unit.DistanceUnit;
import org.codelibs.elasticsearch.index.fielddata.MultiGeoPointValues;
import org.codelibs.elasticsearch.index.fielddata.SortedBinaryDocValues;
import org.codelibs.elasticsearch.index.fielddata.SortedNumericDoubleValues;
import org.codelibs.elasticsearch.search.aggregations.Aggregator;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactories;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactory;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation.Type;
import org.codelibs.elasticsearch.search.aggregations.bucket.range.InternalRange;
import org.codelibs.elasticsearch.search.aggregations.bucket.range.RangeAggregator;
import org.codelibs.elasticsearch.search.aggregations.bucket.range.RangeAggregator.Unmapped;
import org.codelibs.elasticsearch.search.aggregations.bucket.range.geodistance.GeoDistanceAggregationBuilder.Range;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSource;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSourceConfig;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GeoDistanceRangeAggregatorFactory
        extends ValuesSourceAggregatorFactory<ValuesSource.GeoPoint, GeoDistanceRangeAggregatorFactory> {

    private final InternalRange.Factory<InternalGeoDistance.Bucket, InternalGeoDistance> rangeFactory = InternalGeoDistance.FACTORY;
    private final GeoPoint origin;
    private final Range[] ranges;
    private final DistanceUnit unit;
    private final GeoDistance distanceType;
    private final boolean keyed;

    public GeoDistanceRangeAggregatorFactory(String name, Type type, ValuesSourceConfig<ValuesSource.GeoPoint> config, GeoPoint origin,
            Range[] ranges, DistanceUnit unit, GeoDistance distanceType, boolean keyed, SearchContext context,
            AggregatorFactory<?> parent, AggregatorFactories.Builder subFactoriesBuilder, Map<String, Object> metaData) throws IOException {
        super(name, type, config, context, parent, subFactoriesBuilder, metaData);
        this.origin = origin;
        this.ranges = ranges;
        this.unit = unit;
        this.distanceType = distanceType;
        this.keyed = keyed;
    }

    @Override
    protected Aggregator createUnmapped(Aggregator parent, List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData)
            throws IOException {
        return new Unmapped<>(name, ranges, keyed, config.format(), context, parent, rangeFactory, pipelineAggregators, metaData);
    }

    @Override
    protected Aggregator doCreateInternal(final ValuesSource.GeoPoint valuesSource, Aggregator parent, boolean collectsFromSingleBucket,
            List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) throws IOException {
        DistanceSource distanceSource = new DistanceSource(valuesSource, distanceType, origin, unit);
        return new RangeAggregator(name, factories, distanceSource, config.format(), rangeFactory, ranges, keyed, context,
                parent,
                pipelineAggregators, metaData);
    }

    private static class DistanceSource extends ValuesSource.Numeric {

        private final ValuesSource.GeoPoint source;
        private final GeoDistance distanceType;
        private final DistanceUnit unit;
        private final org.codelibs.elasticsearch.common.geo.GeoPoint origin;

        public DistanceSource(ValuesSource.GeoPoint source, GeoDistance distanceType, org.codelibs.elasticsearch.common.geo.GeoPoint origin,
                DistanceUnit unit) {
            this.source = source;
            // even if the geo points are unique, there's no guarantee the
            // distances are
            this.distanceType = distanceType;
            this.unit = unit;
            this.origin = origin;
        }

        @Override
        public boolean isFloatingPoint() {
            return true;
        }

        @Override
        public SortedNumericDocValues longValues(LeafReaderContext ctx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SortedNumericDoubleValues doubleValues(LeafReaderContext ctx) {
            final MultiGeoPointValues geoValues = source.geoPointValues(ctx);
            final FixedSourceDistance distance = distanceType.fixedSourceDistance(origin.getLat(), origin.getLon(), unit);
            return GeoDistance.distanceValues(geoValues, distance);
        }

        @Override
        public SortedBinaryDocValues bytesValues(LeafReaderContext ctx) {
            throw new UnsupportedOperationException();
        }

    }

}
