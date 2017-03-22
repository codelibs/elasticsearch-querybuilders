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

package org.codelibs.elasticsearch.search.aggregations.bucket.nested;

import org.codelibs.elasticsearch.search.aggregations.Aggregator;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactories;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactory;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation.Type;
import org.codelibs.elasticsearch.search.aggregations.NonCollectingAggregator;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NestedAggregatorFactory extends AggregatorFactory<NestedAggregatorFactory> {

    public NestedAggregatorFactory(String name, Type type,
            SearchContext context, AggregatorFactory<?> parent, AggregatorFactories.Builder subFactories,
                                   Map<String, Object> metaData) throws IOException {
        super(name, type, context, parent, subFactories, metaData);
    }

    @Override
    public Aggregator createInternal(Aggregator parent, boolean collectsFromSingleBucket, List<PipelineAggregator> pipelineAggregators,
            Map<String, Object> metaData) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    private static final class Unmapped extends NonCollectingAggregator {

        public Unmapped(String name, SearchContext context, Aggregator parent, List<PipelineAggregator> pipelineAggregators,
                Map<String, Object> metaData) throws IOException {
            super(name, context, parent, pipelineAggregators, metaData);
        }

        @Override
        public InternalAggregation buildEmptyAggregation() {
            return new InternalNested(name, 0, buildEmptySubAggregations(), pipelineAggregators(), metaData());
        }
    }

}
