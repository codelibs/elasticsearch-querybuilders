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

package org.codelibs.elasticsearch.action.support;

import org.codelibs.elasticsearch.ElasticsearchException;
import org.codelibs.elasticsearch.ExceptionsHelper;
import org.codelibs.elasticsearch.client.Requests;
import org.codelibs.elasticsearch.common.bytes.BytesReference;
import org.codelibs.elasticsearch.common.xcontent.ToXContent;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentFactory;
import org.codelibs.elasticsearch.common.xcontent.XContentType;

/**
 * Base class for {ToXContent} implementation that also support conversion to {BytesReference} for serialization purposes
 */
public abstract class ToXContentToBytes implements ToXContent {

    private final XContentType defaultType;

    protected ToXContentToBytes() {
        this.defaultType = Requests.CONTENT_TYPE;
    }

    protected ToXContentToBytes(XContentType defaultType) {
        this.defaultType = defaultType;
    }

    /**
     * Returns a {org.codelibs.elasticsearch.common.bytes.BytesReference}
     * containing the {ToXContent} output in binary format.
     * Builds the request based on the default {XContentType}, either {Requests#CONTENT_TYPE} or provided as a constructor argument
     */
    public final BytesReference buildAsBytes() {
        return buildAsBytes(defaultType);
    }

    /**
     * Returns a {org.codelibs.elasticsearch.common.bytes.BytesReference}
     * containing the {ToXContent} output in binary format.
     * Builds the request as the provided <code>contentType</code>
     */
    public final BytesReference buildAsBytes(XContentType contentType) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(contentType);
            toXContent(builder, ToXContent.EMPTY_PARAMS);
            return builder.bytes();
        } catch (Exception e) {
            throw new ElasticsearchException("Failed to build ToXContent", e);
        }
    }

    @Override
    public final String toString() {
        return toString(EMPTY_PARAMS);
    }

    public final String toString(Params params) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            if (params.paramAsBoolean("pretty", true)) {
                builder.prettyPrint();
            }
            toXContent(builder, params);
            return builder.string();
        } catch (Exception e) {
            // So we have a stack trace logged somewhere
            return "{ \"error\" : \"" + ExceptionsHelper.detailedMessage(e) + "\"}";
        }
    }
}
