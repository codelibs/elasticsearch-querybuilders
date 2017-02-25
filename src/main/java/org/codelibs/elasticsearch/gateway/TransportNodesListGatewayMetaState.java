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

package org.codelibs.elasticsearch.gateway;

import org.codelibs.elasticsearch.ElasticsearchException;
import org.codelibs.elasticsearch.action.ActionFuture;
import org.codelibs.elasticsearch.action.FailedNodeException;
import org.codelibs.elasticsearch.action.support.ActionFilters;
import org.codelibs.elasticsearch.action.support.nodes.BaseNodeRequest;
import org.codelibs.elasticsearch.action.support.nodes.BaseNodeResponse;
import org.codelibs.elasticsearch.action.support.nodes.BaseNodesRequest;
import org.codelibs.elasticsearch.action.support.nodes.BaseNodesResponse;
import org.codelibs.elasticsearch.action.support.nodes.TransportNodesAction;
import org.codelibs.elasticsearch.cluster.ClusterName;
import org.codelibs.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.codelibs.elasticsearch.cluster.metadata.MetaData;
import org.codelibs.elasticsearch.cluster.node.DiscoveryNode;
import org.codelibs.elasticsearch.cluster.service.ClusterService;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.unit.TimeValue;
import org.codelibs.elasticsearch.threadpool.ThreadPool;
import org.codelibs.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class TransportNodesListGatewayMetaState extends TransportNodesAction<TransportNodesListGatewayMetaState.Request,
                                                                             TransportNodesListGatewayMetaState.NodesGatewayMetaState,
                                                                             TransportNodesListGatewayMetaState.NodeRequest,
                                                                             TransportNodesListGatewayMetaState.NodeGatewayMetaState> {

    public static final String ACTION_NAME = "internal:gateway/local/meta_state";

    private GatewayMetaState metaState;

    @Inject
    public TransportNodesListGatewayMetaState(Settings settings, ThreadPool threadPool,
                                              ClusterService clusterService, TransportService transportService,
                                              ActionFilters actionFilters, IndexNameExpressionResolver indexNameExpressionResolver) {
        super(settings, ACTION_NAME, threadPool, clusterService, transportService, actionFilters,
              indexNameExpressionResolver, Request::new, NodeRequest::new, ThreadPool.Names.GENERIC, NodeGatewayMetaState.class);
    }

    TransportNodesListGatewayMetaState init(GatewayMetaState metaState) {
        this.metaState = metaState;
        return this;
    }

    public ActionFuture<NodesGatewayMetaState> list(String[] nodesIds, @Nullable TimeValue timeout) {
        return execute(new Request(nodesIds).timeout(timeout));
    }

    @Override
    protected boolean transportCompress() {
        return true; // compress since the metadata can become large
    }

    @Override
    protected NodeRequest newNodeRequest(String nodeId, Request request) {
        return new NodeRequest(nodeId);
    }

    @Override
    protected NodeGatewayMetaState newNodeResponse() {
        return new NodeGatewayMetaState();
    }

    @Override
    protected NodesGatewayMetaState newResponse(Request request, List<NodeGatewayMetaState> responses, List<FailedNodeException> failures) {
        return new NodesGatewayMetaState(clusterService.getClusterName(), responses, failures);
    }

    @Override
    protected NodeGatewayMetaState nodeOperation(NodeRequest request) {
        try {
            return new NodeGatewayMetaState(clusterService.localNode(), metaState.loadMetaState());
        } catch (Exception e) {
            throw new ElasticsearchException("failed to load metadata", e);
        }
    }

    @Override
    protected boolean accumulateExceptions() {
        return true;
    }

    public static class Request extends BaseNodesRequest<Request> {

        public Request() {
        }

        public Request(String... nodesIds) {
            super(nodesIds);
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
        }
    }

    public static class NodesGatewayMetaState extends BaseNodesResponse<NodeGatewayMetaState> {

        NodesGatewayMetaState() {
        }

        public NodesGatewayMetaState(ClusterName clusterName, List<NodeGatewayMetaState> nodes, List<FailedNodeException> failures) {
            super(clusterName, nodes, failures);
        }

        @Override
        protected List<NodeGatewayMetaState> readNodesFrom(StreamInput in) throws IOException {
            return in.readStreamableList(NodeGatewayMetaState::new);
        }

        @Override
        protected void writeNodesTo(StreamOutput out, List<NodeGatewayMetaState> nodes) throws IOException {
            out.writeStreamableList(nodes);
        }
    }

    public static class NodeRequest extends BaseNodeRequest {

        public NodeRequest() {
        }

        NodeRequest(String nodeId) {
            super(nodeId);
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
        }
    }

    public static class NodeGatewayMetaState extends BaseNodeResponse {

        private MetaData metaData;

        NodeGatewayMetaState() {
        }

        public NodeGatewayMetaState(DiscoveryNode node, MetaData metaData) {
            super(node);
            this.metaData = metaData;
        }

        public MetaData metaData() {
            return metaData;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            if (in.readBoolean()) {
                metaData = MetaData.readFrom(in);
            }
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            if (metaData == null) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                metaData.writeTo(out);
            }
        }
    }
}
