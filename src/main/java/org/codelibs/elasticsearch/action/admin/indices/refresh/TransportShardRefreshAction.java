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

package org.codelibs.elasticsearch.action.admin.indices.refresh;

import org.codelibs.elasticsearch.action.support.ActionFilters;
import org.codelibs.elasticsearch.action.support.replication.BasicReplicationRequest;
import org.codelibs.elasticsearch.action.support.replication.ReplicationResponse;
import org.codelibs.elasticsearch.action.support.replication.TransportReplicationAction;
import org.codelibs.elasticsearch.cluster.action.shard.ShardStateAction;
import org.codelibs.elasticsearch.cluster.block.ClusterBlockLevel;
import org.codelibs.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.codelibs.elasticsearch.cluster.service.ClusterService;
import org.codelibs.elasticsearch.common.inject.Inject;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.index.shard.IndexShard;
import org.codelibs.elasticsearch.indices.IndicesService;
import org.codelibs.elasticsearch.threadpool.ThreadPool;
import org.codelibs.elasticsearch.transport.TransportService;


public class TransportShardRefreshAction
        extends TransportReplicationAction<BasicReplicationRequest, BasicReplicationRequest, ReplicationResponse> {

    public static final String NAME = RefreshAction.NAME + "[s]";

    @Inject
    public TransportShardRefreshAction(Settings settings, TransportService transportService, ClusterService clusterService,
                                       IndicesService indicesService, ThreadPool threadPool, ShardStateAction shardStateAction,
                                       ActionFilters actionFilters, IndexNameExpressionResolver indexNameExpressionResolver) {
        super(settings, NAME, transportService, clusterService, indicesService, threadPool, shardStateAction, actionFilters,
                indexNameExpressionResolver, BasicReplicationRequest::new, BasicReplicationRequest::new, ThreadPool.Names.REFRESH);
    }

    @Override
    protected ReplicationResponse newResponseInstance() {
        return new ReplicationResponse();
    }

    @Override
    protected PrimaryResult shardOperationOnPrimary(BasicReplicationRequest shardRequest, IndexShard primary) {
        primary.refresh("api");
        logger.trace("{} refresh request executed on primary", primary.shardId());
        return new PrimaryResult(shardRequest, new ReplicationResponse());
    }

    @Override
    protected ReplicaResult shardOperationOnReplica(BasicReplicationRequest request, IndexShard replica) {
        replica.refresh("api");
        logger.trace("{} refresh request executed on replica", replica.shardId());
        return new ReplicaResult();
    }

    @Override
    protected boolean shouldExecuteReplication(Settings settings) {
        return true;
    }
}
