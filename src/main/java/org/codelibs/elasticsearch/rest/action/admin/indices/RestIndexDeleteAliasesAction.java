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

import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.codelibs.elasticsearch.client.node.NodeClient;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.rest.BaseRestHandler;
import org.codelibs.elasticsearch.rest.RestController;
import org.codelibs.elasticsearch.rest.RestRequest;
import org.codelibs.elasticsearch.rest.action.AcknowledgedRestListener;

import java.io.IOException;

import static org.codelibs.elasticsearch.rest.RestRequest.Method.DELETE;

/**
 */
public class RestIndexDeleteAliasesAction extends BaseRestHandler {

    @Inject
    public RestIndexDeleteAliasesAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(DELETE, "/{index}/_alias/{name}", this);
        controller.registerHandler(DELETE, "/{index}/_aliases/{name}", this);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        final String[] indices = Strings.splitStringByCommaToArray(request.param("index"));
        final String[] aliases = Strings.splitStringByCommaToArray(request.param("name"));
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        indicesAliasesRequest.timeout(request.paramAsTime("timeout", indicesAliasesRequest.timeout()));
        indicesAliasesRequest.addAliasAction(AliasActions.remove().indices(indices).aliases(aliases));
        indicesAliasesRequest.masterNodeTimeout(request.paramAsTime("master_timeout", indicesAliasesRequest.masterNodeTimeout()));

        return channel -> client.admin().indices().aliases(indicesAliasesRequest, new AcknowledgedRestListener<>(channel));
    }
}
