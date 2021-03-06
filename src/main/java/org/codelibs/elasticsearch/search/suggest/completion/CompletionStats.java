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
package org.codelibs.elasticsearch.search.suggest.completion;

import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.io.stream.Streamable;
import org.codelibs.elasticsearch.common.unit.ByteSizeValue;
import org.codelibs.elasticsearch.common.xcontent.ToXContent;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 *
 */
public class CompletionStats implements Streamable, ToXContent {

    private long sizeInBytes;

    public CompletionStats() {
    }

    public CompletionStats(long size) {
        this.sizeInBytes = size;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public ByteSizeValue getSize() {
        return new ByteSizeValue(sizeInBytes);
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        sizeInBytes = in.readVLong();
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeVLong(sizeInBytes);
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    public void add(CompletionStats completion) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }
}
