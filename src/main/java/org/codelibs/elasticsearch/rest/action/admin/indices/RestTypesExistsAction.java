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

import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.codelibs.elasticsearch.action.support.IndicesOptions;
import org.codelibs.elasticsearch.client.node.NodeClient;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.bytes.BytesArray;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.rest.BaseRestHandler;
import org.codelibs.elasticsearch.rest.BytesRestResponse;
import org.codelibs.elasticsearch.rest.RestController;
import org.codelibs.elasticsearch.rest.RestRequest;
import org.codelibs.elasticsearch.rest.RestResponse;
import org.codelibs.elasticsearch.rest.action.RestResponseListener;

import java.io.IOException;

import static org.codelibs.elasticsearch.rest.RestRequest.Method.HEAD;
import static org.codelibs.elasticsearch.rest.RestStatus.NOT_FOUND;
import static org.codelibs.elasticsearch.rest.RestStatus.OK;

/**
 * Rest api for checking if a type exists.
 */
public class RestTypesExistsAction extends BaseRestHandler {

    @Inject
    public RestTypesExistsAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerWithDeprecatedHandler(
                HEAD, "/{index}/_mapping/{type}", this,
                HEAD, "/{index}/{type}", deprecationLogger);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        TypesExistsRequest typesExistsRequest = new TypesExistsRequest(
                Strings.splitStringByCommaToArray(request.param("index")), Strings.splitStringByCommaToArray(request.param("type"))
        );
        typesExistsRequest.local(request.paramAsBoolean("local", typesExistsRequest.local()));
        typesExistsRequest.indicesOptions(IndicesOptions.fromRequest(request, typesExistsRequest.indicesOptions()));
        return channel -> client.admin().indices().typesExists(typesExistsRequest, new RestResponseListener<TypesExistsResponse>(channel) {
            @Override
            public RestResponse buildResponse(TypesExistsResponse response) throws Exception {
                if (response.isExists()) {
                    return new BytesRestResponse(OK, BytesRestResponse.TEXT_CONTENT_TYPE, BytesArray.EMPTY);
                } else {
                    return new BytesRestResponse(NOT_FOUND, BytesRestResponse.TEXT_CONTENT_TYPE, BytesArray.EMPTY);
                }
            }
        });
    }
}
