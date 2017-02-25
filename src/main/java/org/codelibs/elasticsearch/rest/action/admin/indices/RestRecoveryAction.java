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

import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryRequest;
import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryResponse;
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

/**
 * REST handler to report on index recoveries.
 */
public class RestRecoveryAction extends BaseRestHandler {

    @Inject
    public RestRecoveryAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(GET, "/_recovery", this);
        controller.registerHandler(GET, "/{index}/_recovery", this);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {

        final RecoveryRequest recoveryRequest = new RecoveryRequest(Strings.splitStringByCommaToArray(request.param("index")));
        recoveryRequest.detailed(request.paramAsBoolean("detailed", false));
        recoveryRequest.activeOnly(request.paramAsBoolean("active_only", false));
        recoveryRequest.indicesOptions(IndicesOptions.fromRequest(request, recoveryRequest.indicesOptions()));

        return channel -> client.admin().indices().recoveries(recoveryRequest, new RestBuilderListener<RecoveryResponse>(channel) {
            @Override
            public RestResponse buildResponse(RecoveryResponse response, XContentBuilder builder) throws Exception {
                response.detailed(recoveryRequest.detailed());
                builder.startObject();
                response.toXContent(builder, request);
                builder.endObject();
                return new BytesRestResponse(OK, builder);
            }
        });

    }
}

