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

package org.codelibs.elasticsearch.search.aggregations.metrics.percentiles.tdigest;

import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class AbstractInternalTDigestPercentiles extends InternalNumericMetricsAggregation.MultiValue {

    protected final double[] keys;
    protected final TDigestState state;
    private final boolean keyed;

    public AbstractInternalTDigestPercentiles(String name, double[] keys, TDigestState state, boolean keyed, DocValueFormat formatter,
            List<PipelineAggregator> pipelineAggregators,
            Map<String, Object> metaData) {
        super(name, pipelineAggregators, metaData);
        this.keys = keys;
        this.state = state;
        this.keyed = keyed;
        this.format = formatter;
    }

    /**
     * Read from a stream.
     */
    protected AbstractInternalTDigestPercentiles(StreamInput in) throws IOException {
        super(in);
        format = in.readNamedWriteable(DocValueFormat.class);
        keys = in.readDoubleArray();
        state = TDigestState.read(in);
        keyed = in.readBoolean();
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeNamedWriteable(format);
        out.writeDoubleArray(keys);
        TDigestState.write(state, out);
        out.writeBoolean(keyed);
    }

    @Override
    public double value(String name) {
        return value(Double.parseDouble(name));
    }

    public abstract double value(double key);

    public long getEstimatedMemoryFootprint() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractInternalTDigestPercentiles doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        throw new UnsupportedOperationException();
    }

    protected abstract AbstractInternalTDigestPercentiles createReduced(String name, double[] keys, TDigestState merged, boolean keyed,
            List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData);

    @Override
    public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        if (keyed) {
            builder.startObject(CommonFields.VALUES);
            for (double key2 : keys) {
                String key = String.valueOf(key2);
                double value = value(key2);
                builder.field(key, value);
                if (format != DocValueFormat.RAW) {
                    builder.field(key + "_as_string", format.format(value));
                }
            }
            builder.endObject();
        } else {
            builder.startArray(CommonFields.VALUES);
            for (double key : keys) {
                double value = value(key);
                builder.startObject();
                builder.field(CommonFields.KEY, key);
                builder.field(CommonFields.VALUE, value);
                if (format != DocValueFormat.RAW) {
                    builder.field(CommonFields.VALUE_AS_STRING, format.format(value));
                }
                builder.endObject();
            }
            builder.endArray();
        }
        return builder;
    }
}
