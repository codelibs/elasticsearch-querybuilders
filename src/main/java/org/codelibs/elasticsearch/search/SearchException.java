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

package org.codelibs.elasticsearch.search;

import org.codelibs.elasticsearch.ElasticsearchException;
import org.codelibs.elasticsearch.ElasticsearchWrapperException;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

/**
 *
 */
public class SearchException extends ElasticsearchException implements ElasticsearchWrapperException {

    public SearchException() {
        super((Throwable)null);
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    public SearchException(StreamInput in) throws IOException {
        super(in);
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }
}
