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
package org.codelibs.elasticsearch.search.aggregations.metrics.tophits;

import org.apache.lucene.search.TopDocs;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.lucene.Lucene;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.metrics.InternalMetricsAggregation;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Results of the {TopHitsAggregator}.
 */
public class InternalTopHits extends InternalMetricsAggregation implements TopHits {
    private int from;
    private int size;
    private TopDocs topDocs;

    public InternalTopHits(String name, int from, int size, TopDocs topDocs,
            List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) {
        super(name, pipelineAggregators, metaData);
        this.from = from;
        this.size = size;
        this.topDocs = topDocs;
    }

    /**
     * Read from a stream.
     */
    public InternalTopHits(StreamInput in) throws IOException {
        super(in);
        from = in.readVInt();
        size = in.readVInt();
        topDocs = Lucene.readTopDocs(in);
        assert topDocs != null;
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeVInt(from);
        out.writeVInt(size);
        Lucene.writeTopDocs(out, topDocs);
    }

    @Override
    public String getWriteableName() {
        return TopHitsAggregationBuilder.NAME;
    }

    @Override
    public InternalAggregation doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public Object getProperty(List<String> path) {
        if (path.isEmpty()) {
            return this;
        } else {
            throw new IllegalArgumentException("path not supported for [" + getName() + "]: " + path);
        }
    }

    @Override
    public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }
}
