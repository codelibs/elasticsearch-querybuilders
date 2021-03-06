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

import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregations;
import org.codelibs.elasticsearch.search.aggregations.bucket.InternalSingleBucketAggregation;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Result of the {ReverseNestedAggregator}.
 */
public class InternalReverseNested extends InternalSingleBucketAggregation implements ReverseNested {
    public InternalReverseNested(String name, long docCount, InternalAggregations aggregations, List<PipelineAggregator> pipelineAggregators,
            Map<String, Object> metaData) {
        super(name, docCount, aggregations, pipelineAggregators, metaData);
    }

    /**
     * Read from a stream.
     */
    public InternalReverseNested(StreamInput in) throws IOException {
        super(in);
    }

    @Override
    public String getWriteableName() {
        return ReverseNestedAggregationBuilder.NAME;
    }

    @Override
    protected InternalSingleBucketAggregation newAggregation(String name, long docCount, InternalAggregations subAggregations) {
        return new InternalReverseNested(name, docCount, subAggregations, pipelineAggregators(), getMetaData());
    }
}
