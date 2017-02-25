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

package org.codelibs.elasticsearch.rest.action.search;

import org.codelibs.elasticsearch.action.search.ClearScrollRequest;
import org.codelibs.elasticsearch.action.search.ClearScrollResponse;
import org.codelibs.elasticsearch.client.node.NodeClient;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.bytes.BytesReference;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentFactory;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.rest.BaseRestHandler;
import org.codelibs.elasticsearch.rest.RestController;
import org.codelibs.elasticsearch.rest.RestRequest;
import org.codelibs.elasticsearch.rest.action.RestStatusToXContentListener;

import java.io.IOException;
import java.util.Arrays;

import static org.codelibs.elasticsearch.rest.RestRequest.Method.DELETE;

public class RestClearScrollAction extends BaseRestHandler {

    @Inject
    public RestClearScrollAction(Settings settings, RestController controller) {
        super(settings);

        controller.registerHandler(DELETE, "/_search/scroll", this);
        controller.registerHandler(DELETE, "/_search/scroll/{scroll_id}", this);
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        String scrollIds = request.param("scroll_id");
        ClearScrollRequest clearRequest = new ClearScrollRequest();
        clearRequest.setScrollIds(Arrays.asList(splitScrollIds(scrollIds)));
        BytesReference body = request.contentOrSourceParam();
        if (body.length() > 0) {
            if (XContentFactory.xContentType(body) == null) {
                scrollIds = body.utf8ToString();
                clearRequest.setScrollIds(Arrays.asList(splitScrollIds(scrollIds)));
            } else {
                // NOTE: if rest request with xcontent body has request parameters, these parameters does not override xcontent value
                clearRequest.setScrollIds(null);
                try (XContentParser parser = request.contentOrSourceParamParser()) {
                    buildFromContent(parser, clearRequest);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed to parse request body", e);
                }
            }
        }

        return channel -> client.clearScroll(clearRequest, new RestStatusToXContentListener<ClearScrollResponse>(channel));
    }

    public static String[] splitScrollIds(String scrollIds) {
        if (scrollIds == null) {
            return Strings.EMPTY_ARRAY;
        }
        return Strings.splitStringByCommaToArray(scrollIds);
    }

    public static void buildFromContent(XContentParser parser, ClearScrollRequest clearScrollRequest) throws IOException {
        if (parser.nextToken() != XContentParser.Token.START_OBJECT) {
            throw new IllegalArgumentException("Malformed content, must start with an object");
        } else {
            XContentParser.Token token;
            String currentFieldName = null;
            while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                if (token == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if ("scroll_id".equals(currentFieldName) && token == XContentParser.Token.START_ARRAY) {
                    while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                        if (token.isValue() == false) {
                            throw new IllegalArgumentException("scroll_id array element should only contain scroll_id");
                        }
                        clearScrollRequest.addScrollId(parser.text());
                    }
                } else {
                    throw new IllegalArgumentException("Unknown parameter [" + currentFieldName
                            + "] in request body or parameter is of the wrong type[" + token + "] ");
                }
            }
        }
    }

}