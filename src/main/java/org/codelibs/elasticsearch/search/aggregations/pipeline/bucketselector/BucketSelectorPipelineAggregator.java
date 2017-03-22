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

package org.codelibs.elasticsearch.search.aggregations.pipeline.bucketselector;


import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.script.CompiledScript;
import org.codelibs.elasticsearch.script.ExecutableScript;
import org.codelibs.elasticsearch.script.Script;
import org.codelibs.elasticsearch.script.ScriptContext;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation.ReduceContext;
import org.codelibs.elasticsearch.search.aggregations.InternalMultiBucketAggregation;
import org.codelibs.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.codelibs.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.codelibs.elasticsearch.search.aggregations.pipeline.BucketHelpers.resolveBucketValue;

public class BucketSelectorPipelineAggregator extends PipelineAggregator {
    private GapPolicy gapPolicy;

    private Script script;

    private Map<String, String> bucketsPathsMap;

    public BucketSelectorPipelineAggregator(String name, Map<String, String> bucketsPathsMap, Script script, GapPolicy gapPolicy,
            Map<String, Object> metadata) {
        super(name, bucketsPathsMap.values().toArray(new String[bucketsPathsMap.size()]), metadata);
        this.bucketsPathsMap = bucketsPathsMap;
        this.script = script;
        this.gapPolicy = gapPolicy;
    }

    /**
     * Read from a stream.
     */
    @SuppressWarnings("unchecked")
    public BucketSelectorPipelineAggregator(StreamInput in) throws IOException {
        super(in);
        script = new Script(in);
        gapPolicy = GapPolicy.readFrom(in);
        bucketsPathsMap = (Map<String, String>) in.readGenericValue();
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        script.writeTo(out);
        gapPolicy.writeTo(out);
        out.writeGenericValue(bucketsPathsMap);
    }

    @Override
    public String getWriteableName() {
        return BucketSelectorPipelineAggregationBuilder.NAME;
    }

    @Override
    public InternalAggregation reduce(InternalAggregation aggregation, ReduceContext reduceContext) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }
}
