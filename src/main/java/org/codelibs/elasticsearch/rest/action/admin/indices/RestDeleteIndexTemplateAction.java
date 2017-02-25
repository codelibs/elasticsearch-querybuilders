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

import org.codelibs.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.codelibs.elasticsearch.client.node.NodeClient;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.rest.BaseRestHandler;
import org.codelibs.elasticsearch.rest.RestController;
import org.codelibs.elasticsearch.rest.RestRequest;
import org.codelibs.elasticsearch.rest.action.AcknowledgedRestListener;

import java.io.IOException;

public class RestDeleteIndexTemplateAction extends BaseRestHandler {

    @Inject
    public RestDeleteIndexTemplateAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(RestRequest.Method.DELETE, "/_template/{name}", this);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        DeleteIndexTemplateRequest deleteIndexTemplateRequest = new DeleteIndexTemplateRequest(request.param("name"));
        deleteIndexTemplateRequest.masterNodeTimeout(request.paramAsTime("master_timeout", deleteIndexTemplateRequest.masterNodeTimeout()));
        return channel -> client.admin().indices().deleteTemplate(deleteIndexTemplateRequest, new AcknowledgedRestListener<>(channel));
    }
}
