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

package org.codelibs.elasticsearch.rest.action.admin.indices;

import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse;
import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequest;
import org.codelibs.elasticsearch.action.support.IndicesOptions;
import org.codelibs.elasticsearch.client.node.NodeClient;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.rest.BaseRestHandler;
import org.codelibs.elasticsearch.rest.BytesRestResponse;
import org.codelibs.elasticsearch.rest.RestController;
import org.codelibs.elasticsearch.rest.RestRequest;
import org.codelibs.elasticsearch.rest.RestResponse;
import org.codelibs.elasticsearch.rest.action.RestBuilderListener;

import java.io.IOException;

import static org.codelibs.elasticsearch.rest.RestRequest.Method.GET;
import static org.codelibs.elasticsearch.rest.RestStatus.OK;
import static org.codelibs.elasticsearch.rest.action.RestActions.buildBroadcastShardsHeader;

public class RestIndicesSegmentsAction extends BaseRestHandler {

    @Inject
    public RestIndicesSegmentsAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(GET, "/_segments", this);
        controller.registerHandler(GET, "/{index}/_segments", this);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        IndicesSegmentsRequest indicesSegmentsRequest = new IndicesSegmentsRequest(
                Strings.splitStringByCommaToArray(request.param("index")));
        indicesSegmentsRequest.verbose(request.paramAsBoolean("verbose", false));
        indicesSegmentsRequest.indicesOptions(IndicesOptions.fromRequest(request, indicesSegmentsRequest.indicesOptions()));
        return channel ->
            client.admin().indices().segments(indicesSegmentsRequest, new RestBuilderListener<IndicesSegmentResponse>(channel) {
                @Override
                public RestResponse buildResponse(IndicesSegmentResponse response, XContentBuilder builder) throws Exception {
                    builder.startObject();
                    buildBroadcastShardsHeader(builder, request, response);
                    response.toXContent(builder, request);
                    builder.endObject();
                    return new BytesRestResponse(OK, builder);
                }
            });
    }
}
