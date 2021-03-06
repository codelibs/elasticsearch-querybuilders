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

package org.codelibs.elasticsearch.search.profile.aggregation;

import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.io.stream.Writeable;
import org.codelibs.elasticsearch.common.xcontent.ToXContent;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.search.profile.ProfileResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container class to hold the profile results for a single shard in the request.
 * Contains a list of query profiles, a collector tree and a total rewrite tree.
 */
public final class AggregationProfileShardResult implements Writeable, ToXContent {

    private final List<ProfileResult> aggProfileResults;

    public AggregationProfileShardResult(List<ProfileResult> aggProfileResults) {
        this.aggProfileResults = aggProfileResults;
    }

    /**
     * Read from a stream.
     */
    public AggregationProfileShardResult(StreamInput in) throws IOException {
        int profileSize = in.readVInt();
        aggProfileResults = new ArrayList<>(profileSize);
        for (int j = 0; j < profileSize; j++) {
            aggProfileResults.add(new ProfileResult(in));
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeVInt(aggProfileResults.size());
        for (ProfileResult p : aggProfileResults) {
            p.writeTo(out);
        }
    }


    public List<ProfileResult> getProfileResults() {
        return Collections.unmodifiableList(aggProfileResults);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startArray("aggregations");
        for (ProfileResult p : aggProfileResults) {
            p.toXContent(builder, params);
        }
        builder.endArray();
        return builder;
    }
}
