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

package org.codelibs.elasticsearch.client.support;

import org.codelibs.elasticsearch.action.Action;
import org.codelibs.elasticsearch.action.ActionFuture;
import org.codelibs.elasticsearch.action.ActionListener;
import org.codelibs.elasticsearch.action.ActionRequest;
import org.codelibs.elasticsearch.action.ActionRequestBuilder;
import org.codelibs.elasticsearch.action.ActionResponse;
import org.codelibs.elasticsearch.action.admin.cluster.allocation.ClusterAllocationExplainAction;
import org.codelibs.elasticsearch.action.admin.cluster.allocation.ClusterAllocationExplainRequest;
import org.codelibs.elasticsearch.action.admin.cluster.allocation.ClusterAllocationExplainRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.allocation.ClusterAllocationExplainResponse;
import org.codelibs.elasticsearch.action.admin.cluster.health.ClusterHealthAction;
import org.codelibs.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.codelibs.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.codelibs.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsRequest;
import org.codelibs.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsResponse;
import org.codelibs.elasticsearch.action.admin.cluster.node.info.NodesInfoAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.codelibs.elasticsearch.action.admin.cluster.node.info.NodesInfoRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.codelibs.elasticsearch.action.admin.cluster.node.stats.NodesStatsAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.codelibs.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksRequest;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksResponse;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskRequest;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskResponse;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksRequest;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryResponse;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryResponse;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryRequest;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryResponse;
import org.codelibs.elasticsearch.action.admin.cluster.reroute.ClusterRerouteAction;
import org.codelibs.elasticsearch.action.admin.cluster.reroute.ClusterRerouteRequest;
import org.codelibs.elasticsearch.action.admin.cluster.reroute.ClusterRerouteRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.reroute.ClusterRerouteResponse;
import org.codelibs.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsAction;
import org.codelibs.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequest;
import org.codelibs.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsResponse;
import org.codelibs.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsAction;
import org.codelibs.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsRequest;
import org.codelibs.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsResponse;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotResponse;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusRequest;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusResponse;
import org.codelibs.elasticsearch.action.admin.cluster.state.ClusterStateAction;
import org.codelibs.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.codelibs.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.codelibs.elasticsearch.action.admin.cluster.stats.ClusterStatsAction;
import org.codelibs.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.codelibs.elasticsearch.action.admin.cluster.stats.ClusterStatsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptRequest;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptResponse;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptRequest;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptResponse;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequest;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptResponse;
import org.codelibs.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksRequest;
import org.codelibs.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksRequestBuilder;
import org.codelibs.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksResponse;
import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.codelibs.elasticsearch.action.admin.indices.alias.exists.AliasesExistAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.exists.AliasesExistRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.codelibs.elasticsearch.action.admin.indices.alias.get.GetAliasesAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.codelibs.elasticsearch.action.admin.indices.alias.get.GetAliasesRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.codelibs.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.codelibs.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.codelibs.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.codelibs.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheAction;
import org.codelibs.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequest;
import org.codelibs.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheResponse;
import org.codelibs.elasticsearch.action.admin.indices.close.CloseIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.codelibs.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.codelibs.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.codelibs.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.codelibs.elasticsearch.action.admin.indices.delete.DeleteIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.codelibs.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.codelibs.elasticsearch.action.admin.indices.exists.indices.IndicesExistsAction;
import org.codelibs.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.codelibs.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsAction;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.codelibs.elasticsearch.action.admin.indices.flush.FlushAction;
import org.codelibs.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.codelibs.elasticsearch.action.admin.indices.flush.FlushRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.codelibs.elasticsearch.action.admin.indices.flush.SyncedFlushAction;
import org.codelibs.elasticsearch.action.admin.indices.flush.SyncedFlushRequest;
import org.codelibs.elasticsearch.action.admin.indices.flush.SyncedFlushRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.flush.SyncedFlushResponse;
import org.codelibs.elasticsearch.action.admin.indices.forcemerge.ForceMergeAction;
import org.codelibs.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.codelibs.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.codelibs.elasticsearch.action.admin.indices.get.GetIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.codelibs.elasticsearch.action.admin.indices.get.GetIndexRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetMappingsAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.codelibs.elasticsearch.action.admin.indices.mapping.put.PutMappingAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.codelibs.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.codelibs.elasticsearch.action.admin.indices.open.OpenIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.codelibs.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryAction;
import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryRequest;
import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryResponse;
import org.codelibs.elasticsearch.action.admin.indices.refresh.RefreshAction;
import org.codelibs.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.codelibs.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.codelibs.elasticsearch.action.admin.indices.rollover.RolloverAction;
import org.codelibs.elasticsearch.action.admin.indices.rollover.RolloverRequest;
import org.codelibs.elasticsearch.action.admin.indices.rollover.RolloverRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.rollover.RolloverResponse;
import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentResponse;
import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentsAction;
import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequest;
import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.settings.get.GetSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.codelibs.elasticsearch.action.admin.indices.settings.get.GetSettingsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.codelibs.elasticsearch.action.admin.indices.settings.put.UpdateSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.codelibs.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.codelibs.elasticsearch.action.admin.indices.shards.IndicesShardStoreRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.shards.IndicesShardStoresAction;
import org.codelibs.elasticsearch.action.admin.indices.shards.IndicesShardStoresRequest;
import org.codelibs.elasticsearch.action.admin.indices.shards.IndicesShardStoresResponse;
import org.codelibs.elasticsearch.action.admin.indices.shrink.ShrinkAction;
import org.codelibs.elasticsearch.action.admin.indices.shrink.ShrinkRequest;
import org.codelibs.elasticsearch.action.admin.indices.shrink.ShrinkRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.shrink.ShrinkResponse;
import org.codelibs.elasticsearch.action.admin.indices.stats.IndicesStatsAction;
import org.codelibs.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.codelibs.elasticsearch.action.admin.indices.stats.IndicesStatsRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.codelibs.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateAction;
import org.codelibs.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequest;
import org.codelibs.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateResponse;
import org.codelibs.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesAction;
import org.codelibs.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequest;
import org.codelibs.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesResponse;
import org.codelibs.elasticsearch.action.admin.indices.template.put.PutIndexTemplateAction;
import org.codelibs.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.codelibs.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.get.UpgradeStatusAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.get.UpgradeStatusRequest;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.get.UpgradeStatusRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.get.UpgradeStatusResponse;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.UpgradeAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.UpgradeRequest;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.UpgradeRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.UpgradeResponse;
import org.codelibs.elasticsearch.action.admin.indices.validate.query.ValidateQueryAction;
import org.codelibs.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequest;
import org.codelibs.elasticsearch.action.admin.indices.validate.query.ValidateQueryRequestBuilder;
import org.codelibs.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse;
import org.codelibs.elasticsearch.action.bulk.BulkAction;
import org.codelibs.elasticsearch.action.bulk.BulkRequest;
import org.codelibs.elasticsearch.action.bulk.BulkRequestBuilder;
import org.codelibs.elasticsearch.action.bulk.BulkResponse;
import org.codelibs.elasticsearch.action.delete.DeleteAction;
import org.codelibs.elasticsearch.action.delete.DeleteRequest;
import org.codelibs.elasticsearch.action.delete.DeleteRequestBuilder;
import org.codelibs.elasticsearch.action.delete.DeleteResponse;
import org.codelibs.elasticsearch.action.explain.ExplainAction;
import org.codelibs.elasticsearch.action.explain.ExplainRequest;
import org.codelibs.elasticsearch.action.explain.ExplainRequestBuilder;
import org.codelibs.elasticsearch.action.explain.ExplainResponse;
import org.codelibs.elasticsearch.action.fieldstats.FieldStatsAction;
import org.codelibs.elasticsearch.action.fieldstats.FieldStatsRequest;
import org.codelibs.elasticsearch.action.fieldstats.FieldStatsRequestBuilder;
import org.codelibs.elasticsearch.action.fieldstats.FieldStatsResponse;
import org.codelibs.elasticsearch.action.get.GetAction;
import org.codelibs.elasticsearch.action.get.GetRequest;
import org.codelibs.elasticsearch.action.get.GetRequestBuilder;
import org.codelibs.elasticsearch.action.get.GetResponse;
import org.codelibs.elasticsearch.action.get.MultiGetAction;
import org.codelibs.elasticsearch.action.get.MultiGetRequest;
import org.codelibs.elasticsearch.action.get.MultiGetRequestBuilder;
import org.codelibs.elasticsearch.action.get.MultiGetResponse;
import org.codelibs.elasticsearch.action.index.IndexAction;
import org.codelibs.elasticsearch.action.index.IndexRequest;
import org.codelibs.elasticsearch.action.index.IndexRequestBuilder;
import org.codelibs.elasticsearch.action.index.IndexResponse;
import org.codelibs.elasticsearch.action.ingest.DeletePipelineAction;
import org.codelibs.elasticsearch.action.ingest.DeletePipelineRequest;
import org.codelibs.elasticsearch.action.ingest.DeletePipelineRequestBuilder;
import org.codelibs.elasticsearch.action.ingest.GetPipelineAction;
import org.codelibs.elasticsearch.action.ingest.GetPipelineRequest;
import org.codelibs.elasticsearch.action.ingest.GetPipelineRequestBuilder;
import org.codelibs.elasticsearch.action.ingest.GetPipelineResponse;
import org.codelibs.elasticsearch.action.ingest.PutPipelineAction;
import org.codelibs.elasticsearch.action.ingest.PutPipelineRequest;
import org.codelibs.elasticsearch.action.ingest.PutPipelineRequestBuilder;
import org.codelibs.elasticsearch.action.ingest.SimulatePipelineAction;
import org.codelibs.elasticsearch.action.ingest.SimulatePipelineRequest;
import org.codelibs.elasticsearch.action.ingest.SimulatePipelineRequestBuilder;
import org.codelibs.elasticsearch.action.ingest.SimulatePipelineResponse;
import org.codelibs.elasticsearch.action.ingest.WritePipelineResponse;
import org.codelibs.elasticsearch.action.search.ClearScrollAction;
import org.codelibs.elasticsearch.action.search.ClearScrollRequest;
import org.codelibs.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.codelibs.elasticsearch.action.search.ClearScrollResponse;
import org.codelibs.elasticsearch.action.search.MultiSearchAction;
import org.codelibs.elasticsearch.action.search.MultiSearchRequest;
import org.codelibs.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.codelibs.elasticsearch.action.search.MultiSearchResponse;
import org.codelibs.elasticsearch.action.search.SearchAction;
import org.codelibs.elasticsearch.action.search.SearchRequest;
import org.codelibs.elasticsearch.action.search.SearchRequestBuilder;
import org.codelibs.elasticsearch.action.search.SearchResponse;
import org.codelibs.elasticsearch.action.search.SearchScrollAction;
import org.codelibs.elasticsearch.action.search.SearchScrollRequest;
import org.codelibs.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.codelibs.elasticsearch.action.support.PlainActionFuture;
import org.codelibs.elasticsearch.action.support.ThreadedActionListener;
import org.codelibs.elasticsearch.action.termvectors.MultiTermVectorsAction;
import org.codelibs.elasticsearch.action.termvectors.MultiTermVectorsRequest;
import org.codelibs.elasticsearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.codelibs.elasticsearch.action.termvectors.MultiTermVectorsResponse;
import org.codelibs.elasticsearch.action.termvectors.TermVectorsAction;
import org.codelibs.elasticsearch.action.termvectors.TermVectorsRequest;
import org.codelibs.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.codelibs.elasticsearch.action.termvectors.TermVectorsResponse;
import org.codelibs.elasticsearch.action.update.UpdateAction;
import org.codelibs.elasticsearch.action.update.UpdateRequest;
import org.codelibs.elasticsearch.action.update.UpdateRequestBuilder;
import org.codelibs.elasticsearch.action.update.UpdateResponse;
import org.codelibs.elasticsearch.client.AdminClient;
import org.codelibs.elasticsearch.client.Client;
import org.codelibs.elasticsearch.client.ClusterAdminClient;
import org.codelibs.elasticsearch.client.ElasticsearchClient;
import org.codelibs.elasticsearch.client.FilterClient;
import org.codelibs.elasticsearch.client.IndicesAdminClient;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.bytes.BytesReference;
import org.codelibs.elasticsearch.common.component.AbstractComponent;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.util.concurrent.ThreadContext;
import org.codelibs.elasticsearch.tasks.TaskId;
import org.codelibs.elasticsearch.threadpool.ThreadPool;

import java.util.Map;

/**
 *
 */
public abstract class AbstractClient extends AbstractComponent implements Client {

    private final ThreadPool threadPool;
    private final Admin admin;
    private final ThreadedActionListener.Wrapper threadedWrapper;

    public AbstractClient(Settings settings, ThreadPool threadPool) {
        super(settings);
        this.threadPool = threadPool;
        this.admin = new Admin(this);
        this.threadedWrapper = new ThreadedActionListener.Wrapper(logger, settings, threadPool);
    }

    @Override
    public final Settings settings() {
        return this.settings;
    }

    @Override
    public final ThreadPool threadPool() {
        return this.threadPool;
    }

    @Override
    public final AdminClient admin() {
        return admin;
    }

    @Override
    public final <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(
            final Action<Request, Response, RequestBuilder> action) {
        return action.newRequestBuilder(this);
    }

    @Override
    public final <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(
            Action<Request, Response, RequestBuilder> action, Request request) {
        PlainActionFuture<Response> actionFuture = PlainActionFuture.newFuture();
        execute(action, request, actionFuture);
        return actionFuture;
    }

    /**
     * This is the single execution point of *all* clients.
     */
    @Override
    public final <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(
            Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
        listener = threadedWrapper.wrap(listener);
        doExecute(action, request, listener);
    }

    protected abstract <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void doExecute(final Action<Request, Response, RequestBuilder> action, final Request request, ActionListener<Response> listener);

    @Override
    public ActionFuture<IndexResponse> index(final IndexRequest request) {
        return execute(IndexAction.INSTANCE, request);
    }

    @Override
    public void index(final IndexRequest request, final ActionListener<IndexResponse> listener) {
        execute(IndexAction.INSTANCE, request, listener);
    }

    @Override
    public IndexRequestBuilder prepareIndex() {
        return new IndexRequestBuilder(this, IndexAction.INSTANCE, null);
    }

    @Override
    public IndexRequestBuilder prepareIndex(String index, String type) {
        return prepareIndex(index, type, null);
    }

    @Override
    public IndexRequestBuilder prepareIndex(String index, String type, @Nullable String id) {
        return prepareIndex().setIndex(index).setType(type).setId(id);
    }

    @Override
    public ActionFuture<UpdateResponse> update(final UpdateRequest request) {
        return execute(UpdateAction.INSTANCE, request);
    }

    @Override
    public void update(final UpdateRequest request, final ActionListener<UpdateResponse> listener) {
        execute(UpdateAction.INSTANCE, request, listener);
    }

    @Override
    public UpdateRequestBuilder prepareUpdate() {
        return new UpdateRequestBuilder(this, UpdateAction.INSTANCE, null, null, null);
    }

    @Override
    public UpdateRequestBuilder prepareUpdate(String index, String type, String id) {
        return new UpdateRequestBuilder(this, UpdateAction.INSTANCE, index, type, id);
    }

    @Override
    public ActionFuture<DeleteResponse> delete(final DeleteRequest request) {
        return execute(DeleteAction.INSTANCE, request);
    }

    @Override
    public void delete(final DeleteRequest request, final ActionListener<DeleteResponse> listener) {
        execute(DeleteAction.INSTANCE, request, listener);
    }

    @Override
    public DeleteRequestBuilder prepareDelete() {
        return new DeleteRequestBuilder(this, DeleteAction.INSTANCE, null);
    }

    @Override
    public DeleteRequestBuilder prepareDelete(String index, String type, String id) {
        return prepareDelete().setIndex(index).setType(type).setId(id);
    }

    @Override
    public ActionFuture<BulkResponse> bulk(final BulkRequest request) {
        return execute(BulkAction.INSTANCE, request);
    }

    @Override
    public void bulk(final BulkRequest request, final ActionListener<BulkResponse> listener) {
        execute(BulkAction.INSTANCE, request, listener);
    }

    @Override
    public BulkRequestBuilder prepareBulk() {
        return new BulkRequestBuilder(this, BulkAction.INSTANCE);
    }

    @Override
    public ActionFuture<GetResponse> get(final GetRequest request) {
        return execute(GetAction.INSTANCE, request);
    }

    @Override
    public void get(final GetRequest request, final ActionListener<GetResponse> listener) {
        execute(GetAction.INSTANCE, request, listener);
    }

    @Override
    public GetRequestBuilder prepareGet() {
        return new GetRequestBuilder(this, GetAction.INSTANCE, null);
    }

    @Override
    public GetRequestBuilder prepareGet(String index, String type, String id) {
        return prepareGet().setIndex(index).setType(type).setId(id);
    }

    @Override
    public ActionFuture<MultiGetResponse> multiGet(final MultiGetRequest request) {
        return execute(MultiGetAction.INSTANCE, request);
    }

    @Override
    public void multiGet(final MultiGetRequest request, final ActionListener<MultiGetResponse> listener) {
        execute(MultiGetAction.INSTANCE, request, listener);
    }

    @Override
    public MultiGetRequestBuilder prepareMultiGet() {
        return new MultiGetRequestBuilder(this, MultiGetAction.INSTANCE);
    }

    @Override
    public ActionFuture<SearchResponse> search(final SearchRequest request) {
        return execute(SearchAction.INSTANCE, request);
    }

    @Override
    public void search(final SearchRequest request, final ActionListener<SearchResponse> listener) {
        execute(SearchAction.INSTANCE, request, listener);
    }

    @Override
    public SearchRequestBuilder prepareSearch(String... indices) {
        return new SearchRequestBuilder(this, SearchAction.INSTANCE).setIndices(indices);
    }

    @Override
    public ActionFuture<SearchResponse> searchScroll(final SearchScrollRequest request) {
        return execute(SearchScrollAction.INSTANCE, request);
    }

    @Override
    public void searchScroll(final SearchScrollRequest request, final ActionListener<SearchResponse> listener) {
        execute(SearchScrollAction.INSTANCE, request, listener);
    }

    @Override
    public SearchScrollRequestBuilder prepareSearchScroll(String scrollId) {
        return new SearchScrollRequestBuilder(this, SearchScrollAction.INSTANCE, scrollId);
    }

    @Override
    public ActionFuture<MultiSearchResponse> multiSearch(MultiSearchRequest request) {
        return execute(MultiSearchAction.INSTANCE, request);
    }

    @Override
    public void multiSearch(MultiSearchRequest request, ActionListener<MultiSearchResponse> listener) {
        execute(MultiSearchAction.INSTANCE, request, listener);
    }

    @Override
    public MultiSearchRequestBuilder prepareMultiSearch() {
        return new MultiSearchRequestBuilder(this, MultiSearchAction.INSTANCE);
    }

    @Override
    public ActionFuture<TermVectorsResponse> termVectors(final TermVectorsRequest request) {
        return execute(TermVectorsAction.INSTANCE, request);
    }

    @Override
    public void termVectors(final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener) {
        execute(TermVectorsAction.INSTANCE, request, listener);
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors() {
        return new TermVectorsRequestBuilder(this, TermVectorsAction.INSTANCE);
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors(String index, String type, String id) {
        return new TermVectorsRequestBuilder(this, TermVectorsAction.INSTANCE, index, type, id);
    }

    @Deprecated
    @Override
    public ActionFuture<TermVectorsResponse> termVector(final TermVectorsRequest request) {
        return termVectors(request);
    }

    @Deprecated
    @Override
    public void termVector(final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener) {
        termVectors(request, listener);
    }

    @Deprecated
    @Override
    public TermVectorsRequestBuilder prepareTermVector() {
        return prepareTermVectors();
    }

    @Deprecated
    @Override
    public TermVectorsRequestBuilder prepareTermVector(String index, String type, String id) {
        return prepareTermVectors(index, type, id);
    }

    @Override
    public ActionFuture<MultiTermVectorsResponse> multiTermVectors(final MultiTermVectorsRequest request) {
        return execute(MultiTermVectorsAction.INSTANCE, request);
    }

    @Override
    public void multiTermVectors(final MultiTermVectorsRequest request, final ActionListener<MultiTermVectorsResponse> listener) {
        execute(MultiTermVectorsAction.INSTANCE, request, listener);
    }

    @Override
    public MultiTermVectorsRequestBuilder prepareMultiTermVectors() {
        return new MultiTermVectorsRequestBuilder(this, MultiTermVectorsAction.INSTANCE);
    }

    @Override
    public ExplainRequestBuilder prepareExplain(String index, String type, String id) {
        return new ExplainRequestBuilder(this, ExplainAction.INSTANCE, index, type, id);
    }

    @Override
    public ActionFuture<ExplainResponse> explain(ExplainRequest request) {
        return execute(ExplainAction.INSTANCE, request);
    }

    @Override
    public void explain(ExplainRequest request, ActionListener<ExplainResponse> listener) {
        execute(ExplainAction.INSTANCE, request, listener);
    }

    @Override
    public void clearScroll(ClearScrollRequest request, ActionListener<ClearScrollResponse> listener) {
        execute(ClearScrollAction.INSTANCE, request, listener);
    }

    @Override
    public ActionFuture<ClearScrollResponse> clearScroll(ClearScrollRequest request) {
        return execute(ClearScrollAction.INSTANCE, request);
    }

    @Override
    public ClearScrollRequestBuilder prepareClearScroll() {
        return new ClearScrollRequestBuilder(this, ClearScrollAction.INSTANCE);
    }

    @Override
    public void fieldStats(FieldStatsRequest request, ActionListener<FieldStatsResponse> listener) {
        execute(FieldStatsAction.INSTANCE, request, listener);
    }

    @Override
    public ActionFuture<FieldStatsResponse> fieldStats(FieldStatsRequest request) {
        return execute(FieldStatsAction.INSTANCE, request);
    }

    @Override
    public FieldStatsRequestBuilder prepareFieldStats() {
        return new FieldStatsRequestBuilder(this, FieldStatsAction.INSTANCE);
    }

    static class Admin implements AdminClient {

        private final ClusterAdmin clusterAdmin;
        private final IndicesAdmin indicesAdmin;

        public Admin(ElasticsearchClient client) {
            this.clusterAdmin = new ClusterAdmin(client);
            this.indicesAdmin = new IndicesAdmin(client);
        }

        @Override
        public ClusterAdminClient cluster() {
            return clusterAdmin;
        }

        @Override
        public IndicesAdminClient indices() {
            return indicesAdmin;
        }
    }

    static class ClusterAdmin implements ClusterAdminClient {

        private final ElasticsearchClient client;

        public ClusterAdmin(ElasticsearchClient client) {
            this.client = client;
        }

        @Override
        public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(
                Action<Request, Response, RequestBuilder> action, Request request) {
            return client.execute(action, request);
        }

        @Override
        public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(
                Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
            client.execute(action, request, listener);
        }

        @Override
        public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(
                Action<Request, Response, RequestBuilder> action) {
            return client.prepareExecute(action);
        }

        @Override
        public ThreadPool threadPool() {
            return client.threadPool();
        }

        @Override
        public ActionFuture<ClusterHealthResponse> health(final ClusterHealthRequest request) {
            return execute(ClusterHealthAction.INSTANCE, request);
        }

        @Override
        public void health(final ClusterHealthRequest request, final ActionListener<ClusterHealthResponse> listener) {
            execute(ClusterHealthAction.INSTANCE, request, listener);
        }

        @Override
        public ClusterHealthRequestBuilder prepareHealth(String... indices) {
            return new ClusterHealthRequestBuilder(this, ClusterHealthAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<ClusterStateResponse> state(final ClusterStateRequest request) {
            return execute(ClusterStateAction.INSTANCE, request);
        }

        @Override
        public void state(final ClusterStateRequest request, final ActionListener<ClusterStateResponse> listener) {
            execute(ClusterStateAction.INSTANCE, request, listener);
        }

        @Override
        public ClusterStateRequestBuilder prepareState() {
            return new ClusterStateRequestBuilder(this, ClusterStateAction.INSTANCE);
        }

        @Override
        public ActionFuture<ClusterRerouteResponse> reroute(final ClusterRerouteRequest request) {
            return execute(ClusterRerouteAction.INSTANCE, request);
        }

        @Override
        public void reroute(final ClusterRerouteRequest request, final ActionListener<ClusterRerouteResponse> listener) {
            execute(ClusterRerouteAction.INSTANCE, request, listener);
        }

        @Override
        public ClusterRerouteRequestBuilder prepareReroute() {
            return new ClusterRerouteRequestBuilder(this, ClusterRerouteAction.INSTANCE);
        }

        @Override
        public ActionFuture<ClusterUpdateSettingsResponse> updateSettings(final ClusterUpdateSettingsRequest request) {
            return execute(ClusterUpdateSettingsAction.INSTANCE, request);
        }

        @Override
        public void updateSettings(final ClusterUpdateSettingsRequest request, final ActionListener<ClusterUpdateSettingsResponse> listener) {
            execute(ClusterUpdateSettingsAction.INSTANCE, request, listener);
        }

        @Override
        public ClusterUpdateSettingsRequestBuilder prepareUpdateSettings() {
            return new ClusterUpdateSettingsRequestBuilder(this, ClusterUpdateSettingsAction.INSTANCE);
        }

        @Override
        public ActionFuture<NodesInfoResponse> nodesInfo(final NodesInfoRequest request) {
            return execute(NodesInfoAction.INSTANCE, request);
        }

        @Override
        public void nodesInfo(final NodesInfoRequest request, final ActionListener<NodesInfoResponse> listener) {
            execute(NodesInfoAction.INSTANCE, request, listener);
        }

        @Override
        public NodesInfoRequestBuilder prepareNodesInfo(String... nodesIds) {
            return new NodesInfoRequestBuilder(this, NodesInfoAction.INSTANCE).setNodesIds(nodesIds);
        }

        @Override
        public ActionFuture<NodesStatsResponse> nodesStats(final NodesStatsRequest request) {
            return execute(NodesStatsAction.INSTANCE, request);
        }

        @Override
        public void nodesStats(final NodesStatsRequest request, final ActionListener<NodesStatsResponse> listener) {
            execute(NodesStatsAction.INSTANCE, request, listener);
        }

        @Override
        public NodesStatsRequestBuilder prepareNodesStats(String... nodesIds) {
            return new NodesStatsRequestBuilder(this, NodesStatsAction.INSTANCE).setNodesIds(nodesIds);
        }

        @Override
        public ActionFuture<ClusterStatsResponse> clusterStats(ClusterStatsRequest request) {
            return execute(ClusterStatsAction.INSTANCE, request);
        }

        @Override
        public void clusterStats(ClusterStatsRequest request, ActionListener<ClusterStatsResponse> listener) {
            execute(ClusterStatsAction.INSTANCE, request, listener);
        }

        @Override
        public ClusterStatsRequestBuilder prepareClusterStats() {
            return new ClusterStatsRequestBuilder(this, ClusterStatsAction.INSTANCE);
        }

        @Override
        public ActionFuture<NodesHotThreadsResponse> nodesHotThreads(NodesHotThreadsRequest request) {
            return execute(NodesHotThreadsAction.INSTANCE, request);
        }

        @Override
        public void nodesHotThreads(NodesHotThreadsRequest request, ActionListener<NodesHotThreadsResponse> listener) {
            execute(NodesHotThreadsAction.INSTANCE, request, listener);
        }

        @Override
        public NodesHotThreadsRequestBuilder prepareNodesHotThreads(String... nodesIds) {
            return new NodesHotThreadsRequestBuilder(this, NodesHotThreadsAction.INSTANCE).setNodesIds(nodesIds);
        }

        @Override
        public ActionFuture<ListTasksResponse> listTasks(final ListTasksRequest request) {
            return execute(ListTasksAction.INSTANCE, request);
        }

        @Override
        public void listTasks(final ListTasksRequest request, final ActionListener<ListTasksResponse> listener) {
            execute(ListTasksAction.INSTANCE, request, listener);
        }

        @Override
        public ListTasksRequestBuilder prepareListTasks(String... nodesIds) {
            return new ListTasksRequestBuilder(this, ListTasksAction.INSTANCE).setNodesIds(nodesIds);
        }

        @Override
        public ActionFuture<GetTaskResponse> getTask(final GetTaskRequest request) {
            return execute(GetTaskAction.INSTANCE, request);
        }

        @Override
        public void getTask(final GetTaskRequest request, final ActionListener<GetTaskResponse> listener) {
            execute(GetTaskAction.INSTANCE, request, listener);
        }

        @Override
        public GetTaskRequestBuilder prepareGetTask(String taskId) {
            return prepareGetTask(new TaskId(taskId));
        }

        @Override
        public GetTaskRequestBuilder prepareGetTask(TaskId taskId) {
            return new GetTaskRequestBuilder(this, GetTaskAction.INSTANCE).setTaskId(taskId);
        }

        @Override
        public ActionFuture<CancelTasksResponse> cancelTasks(CancelTasksRequest request) {
            return execute(CancelTasksAction.INSTANCE, request);
        }

        @Override
        public void cancelTasks(CancelTasksRequest request, ActionListener<CancelTasksResponse> listener) {
            execute(CancelTasksAction.INSTANCE, request, listener);
        }

        @Override
        public CancelTasksRequestBuilder prepareCancelTasks(String... nodesIds) {
            return new CancelTasksRequestBuilder(this, CancelTasksAction.INSTANCE).setNodesIds(nodesIds);
        }

        @Override
        public ActionFuture<ClusterSearchShardsResponse> searchShards(final ClusterSearchShardsRequest request) {
            return execute(ClusterSearchShardsAction.INSTANCE, request);
        }

        @Override
        public void searchShards(final ClusterSearchShardsRequest request, final ActionListener<ClusterSearchShardsResponse> listener) {
            execute(ClusterSearchShardsAction.INSTANCE, request, listener);
        }

        @Override
        public ClusterSearchShardsRequestBuilder prepareSearchShards() {
            return new ClusterSearchShardsRequestBuilder(this, ClusterSearchShardsAction.INSTANCE);
        }

        @Override
        public ClusterSearchShardsRequestBuilder prepareSearchShards(String... indices) {
            return new ClusterSearchShardsRequestBuilder(this, ClusterSearchShardsAction.INSTANCE).setIndices(indices);
        }

        @Override
        public PendingClusterTasksRequestBuilder preparePendingClusterTasks() {
            return new PendingClusterTasksRequestBuilder(this, PendingClusterTasksAction.INSTANCE);
        }

        @Override
        public ActionFuture<PendingClusterTasksResponse> pendingClusterTasks(PendingClusterTasksRequest request) {
            return execute(PendingClusterTasksAction.INSTANCE, request);
        }

        @Override
        public void pendingClusterTasks(PendingClusterTasksRequest request, ActionListener<PendingClusterTasksResponse> listener) {
            execute(PendingClusterTasksAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<PutRepositoryResponse> putRepository(PutRepositoryRequest request) {
            return execute(PutRepositoryAction.INSTANCE, request);
        }

        @Override
        public void putRepository(PutRepositoryRequest request, ActionListener<PutRepositoryResponse> listener) {
            execute(PutRepositoryAction.INSTANCE, request, listener);
        }

        @Override
        public PutRepositoryRequestBuilder preparePutRepository(String name) {
            return new PutRepositoryRequestBuilder(this, PutRepositoryAction.INSTANCE, name);
        }

        @Override
        public ActionFuture<CreateSnapshotResponse> createSnapshot(CreateSnapshotRequest request) {
            return execute(CreateSnapshotAction.INSTANCE, request);
        }

        @Override
        public void createSnapshot(CreateSnapshotRequest request, ActionListener<CreateSnapshotResponse> listener) {
            execute(CreateSnapshotAction.INSTANCE, request, listener);
        }

        @Override
        public CreateSnapshotRequestBuilder prepareCreateSnapshot(String repository, String name) {
            return new CreateSnapshotRequestBuilder(this, CreateSnapshotAction.INSTANCE, repository, name);
        }

        @Override
        public ActionFuture<GetSnapshotsResponse> getSnapshots(GetSnapshotsRequest request) {
            return execute(GetSnapshotsAction.INSTANCE, request);
        }

        @Override
        public void getSnapshots(GetSnapshotsRequest request, ActionListener<GetSnapshotsResponse> listener) {
            execute(GetSnapshotsAction.INSTANCE, request, listener);
        }

        @Override
        public GetSnapshotsRequestBuilder prepareGetSnapshots(String repository) {
            return new GetSnapshotsRequestBuilder(this, GetSnapshotsAction.INSTANCE, repository);
        }


        @Override
        public ActionFuture<DeleteSnapshotResponse> deleteSnapshot(DeleteSnapshotRequest request) {
            return execute(DeleteSnapshotAction.INSTANCE, request);
        }

        @Override
        public void deleteSnapshot(DeleteSnapshotRequest request, ActionListener<DeleteSnapshotResponse> listener) {
            execute(DeleteSnapshotAction.INSTANCE, request, listener);
        }

        @Override
        public DeleteSnapshotRequestBuilder prepareDeleteSnapshot(String repository, String name) {
            return new DeleteSnapshotRequestBuilder(this, DeleteSnapshotAction.INSTANCE, repository, name);
        }


        @Override
        public ActionFuture<DeleteRepositoryResponse> deleteRepository(DeleteRepositoryRequest request) {
            return execute(DeleteRepositoryAction.INSTANCE, request);
        }

        @Override
        public void deleteRepository(DeleteRepositoryRequest request, ActionListener<DeleteRepositoryResponse> listener) {
            execute(DeleteRepositoryAction.INSTANCE, request, listener);
        }

        @Override
        public DeleteRepositoryRequestBuilder prepareDeleteRepository(String name) {
            return new DeleteRepositoryRequestBuilder(this, DeleteRepositoryAction.INSTANCE, name);
        }

        @Override
        public ActionFuture<VerifyRepositoryResponse> verifyRepository(VerifyRepositoryRequest request) {
            return execute(VerifyRepositoryAction.INSTANCE, request);
        }

        @Override
        public void verifyRepository(VerifyRepositoryRequest request, ActionListener<VerifyRepositoryResponse> listener) {
            execute(VerifyRepositoryAction.INSTANCE, request, listener);
        }

        @Override
        public VerifyRepositoryRequestBuilder prepareVerifyRepository(String name) {
            return new VerifyRepositoryRequestBuilder(this, VerifyRepositoryAction.INSTANCE, name);
        }

        @Override
        public ActionFuture<GetRepositoriesResponse> getRepositories(GetRepositoriesRequest request) {
            return execute(GetRepositoriesAction.INSTANCE, request);
        }

        @Override
        public void getRepositories(GetRepositoriesRequest request, ActionListener<GetRepositoriesResponse> listener) {
            execute(GetRepositoriesAction.INSTANCE, request, listener);
        }

        @Override
        public GetRepositoriesRequestBuilder prepareGetRepositories(String... name) {
            return new GetRepositoriesRequestBuilder(this, GetRepositoriesAction.INSTANCE, name);
        }

        @Override
        public ActionFuture<RestoreSnapshotResponse> restoreSnapshot(RestoreSnapshotRequest request) {
            return execute(RestoreSnapshotAction.INSTANCE, request);
        }

        @Override
        public void restoreSnapshot(RestoreSnapshotRequest request, ActionListener<RestoreSnapshotResponse> listener) {
            execute(RestoreSnapshotAction.INSTANCE, request, listener);
        }

        @Override
        public RestoreSnapshotRequestBuilder prepareRestoreSnapshot(String repository, String snapshot) {
            return new RestoreSnapshotRequestBuilder(this, RestoreSnapshotAction.INSTANCE, repository, snapshot);
        }


        @Override
        public ActionFuture<SnapshotsStatusResponse> snapshotsStatus(SnapshotsStatusRequest request) {
            return execute(SnapshotsStatusAction.INSTANCE, request);
        }

        @Override
        public void snapshotsStatus(SnapshotsStatusRequest request, ActionListener<SnapshotsStatusResponse> listener) {
            execute(SnapshotsStatusAction.INSTANCE, request, listener);
        }

        @Override
        public SnapshotsStatusRequestBuilder prepareSnapshotStatus(String repository) {
            return new SnapshotsStatusRequestBuilder(this, SnapshotsStatusAction.INSTANCE, repository);
        }

        @Override
        public SnapshotsStatusRequestBuilder prepareSnapshotStatus() {
            return new SnapshotsStatusRequestBuilder(this, SnapshotsStatusAction.INSTANCE);
        }

        @Override
        public void putPipeline(PutPipelineRequest request, ActionListener<WritePipelineResponse> listener) {
            execute(PutPipelineAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<WritePipelineResponse> putPipeline(PutPipelineRequest request) {
            return execute(PutPipelineAction.INSTANCE, request);
        }

        @Override
        public PutPipelineRequestBuilder preparePutPipeline(String id, BytesReference source) {
            return new PutPipelineRequestBuilder(this, PutPipelineAction.INSTANCE, id, source);
        }

        @Override
        public void deletePipeline(DeletePipelineRequest request, ActionListener<WritePipelineResponse> listener) {
            execute(DeletePipelineAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<WritePipelineResponse> deletePipeline(DeletePipelineRequest request) {
            return execute(DeletePipelineAction.INSTANCE, request);
        }

        @Override
        public DeletePipelineRequestBuilder prepareDeletePipeline() {
            return new DeletePipelineRequestBuilder(this, DeletePipelineAction.INSTANCE);
        }

        @Override
        public DeletePipelineRequestBuilder prepareDeletePipeline(String id) {
            return new DeletePipelineRequestBuilder(this, DeletePipelineAction.INSTANCE, id);
        }

        @Override
        public void getPipeline(GetPipelineRequest request, ActionListener<GetPipelineResponse> listener) {
            execute(GetPipelineAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<GetPipelineResponse> getPipeline(GetPipelineRequest request) {
            return execute(GetPipelineAction.INSTANCE, request);
        }

        @Override
        public GetPipelineRequestBuilder prepareGetPipeline(String... ids) {
            return new GetPipelineRequestBuilder(this, GetPipelineAction.INSTANCE, ids);
        }

        @Override
        public void simulatePipeline(SimulatePipelineRequest request, ActionListener<SimulatePipelineResponse> listener) {
            execute(SimulatePipelineAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<SimulatePipelineResponse> simulatePipeline(SimulatePipelineRequest request) {
            return execute(SimulatePipelineAction.INSTANCE, request);
        }

        @Override
        public SimulatePipelineRequestBuilder prepareSimulatePipeline(BytesReference source) {
            return new SimulatePipelineRequestBuilder(this, SimulatePipelineAction.INSTANCE, source);
        }

        @Override
        public void allocationExplain(ClusterAllocationExplainRequest request, ActionListener<ClusterAllocationExplainResponse> listener) {
            execute(ClusterAllocationExplainAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<ClusterAllocationExplainResponse> allocationExplain(ClusterAllocationExplainRequest request) {
            return execute(ClusterAllocationExplainAction.INSTANCE, request);
        }

        @Override
        public ClusterAllocationExplainRequestBuilder prepareAllocationExplain() {
            return new ClusterAllocationExplainRequestBuilder(this, ClusterAllocationExplainAction.INSTANCE);
        }

        @Override
        public ActionFuture<GetStoredScriptResponse> getStoredScript(final GetStoredScriptRequest request) {
            return execute(GetStoredScriptAction.INSTANCE, request);
        }

        @Override
        public void getStoredScript(final GetStoredScriptRequest request, final ActionListener<GetStoredScriptResponse> listener) {
            execute(GetStoredScriptAction.INSTANCE, request, listener);
        }

        @Override
        public GetStoredScriptRequestBuilder prepareGetStoredScript() {
            return new GetStoredScriptRequestBuilder(this, GetStoredScriptAction.INSTANCE);
        }

        @Override
        public GetStoredScriptRequestBuilder prepareGetStoredScript(String scriptLang, String id) {
            return prepareGetStoredScript().setLang(scriptLang).setId(id);
        }

        @Override
        public PutStoredScriptRequestBuilder preparePutStoredScript() {
            return new PutStoredScriptRequestBuilder(this, PutStoredScriptAction.INSTANCE);
        }

        @Override
        public void putStoredScript(final PutStoredScriptRequest request, ActionListener<PutStoredScriptResponse> listener){
            execute(PutStoredScriptAction.INSTANCE, request, listener);

        }

        @Override
        public ActionFuture<PutStoredScriptResponse> putStoredScript(final PutStoredScriptRequest request){
            return execute(PutStoredScriptAction.INSTANCE, request);
        }

        @Override
        public void deleteStoredScript(DeleteStoredScriptRequest request, ActionListener<DeleteStoredScriptResponse> listener){
            execute(DeleteStoredScriptAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<DeleteStoredScriptResponse> deleteStoredScript(DeleteStoredScriptRequest request){
            return execute(DeleteStoredScriptAction.INSTANCE, request);
        }

        @Override
        public DeleteStoredScriptRequestBuilder prepareDeleteStoredScript(){
            return DeleteStoredScriptAction.INSTANCE.newRequestBuilder(this);
        }

        @Override
        public DeleteStoredScriptRequestBuilder prepareDeleteStoredScript(@Nullable String scriptLang, String id){
            return prepareDeleteStoredScript().setScriptLang(scriptLang).setId(id);
        }
    }

    static class IndicesAdmin implements IndicesAdminClient {

        private final ElasticsearchClient client;

        public IndicesAdmin(ElasticsearchClient client) {
            this.client = client;
        }

        @Override
        public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(
                Action<Request, Response, RequestBuilder> action, Request request) {
            return client.execute(action, request);
        }

        @Override
        public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(
                Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
            client.execute(action, request, listener);
        }

        @Override
        public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(
                Action<Request, Response, RequestBuilder> action) {
            return client.prepareExecute(action);
        }

        @Override
        public ThreadPool threadPool() {
            return client.threadPool();
        }

        @Override
        public ActionFuture<IndicesExistsResponse> exists(final IndicesExistsRequest request) {
            return execute(IndicesExistsAction.INSTANCE, request);
        }

        @Override
        public void exists(final IndicesExistsRequest request, final ActionListener<IndicesExistsResponse> listener) {
            execute(IndicesExistsAction.INSTANCE, request, listener);
        }

        @Override
        public IndicesExistsRequestBuilder prepareExists(String... indices) {
            return new IndicesExistsRequestBuilder(this, IndicesExistsAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<TypesExistsResponse> typesExists(TypesExistsRequest request) {
            return execute(TypesExistsAction.INSTANCE, request);
        }

        @Override
        public void typesExists(TypesExistsRequest request, ActionListener<TypesExistsResponse> listener) {
            execute(TypesExistsAction.INSTANCE, request, listener);
        }

        @Override
        public TypesExistsRequestBuilder prepareTypesExists(String... index) {
            return new TypesExistsRequestBuilder(this, TypesExistsAction.INSTANCE, index);
        }

        @Override
        public ActionFuture<IndicesAliasesResponse> aliases(final IndicesAliasesRequest request) {
            return execute(IndicesAliasesAction.INSTANCE, request);
        }

        @Override
        public void aliases(final IndicesAliasesRequest request, final ActionListener<IndicesAliasesResponse> listener) {
            execute(IndicesAliasesAction.INSTANCE, request, listener);
        }

        @Override
        public IndicesAliasesRequestBuilder prepareAliases() {
            return new IndicesAliasesRequestBuilder(this, IndicesAliasesAction.INSTANCE);
        }

        @Override
        public ActionFuture<GetAliasesResponse> getAliases(GetAliasesRequest request) {
            return execute(GetAliasesAction.INSTANCE, request);
        }

        @Override
        public void getAliases(GetAliasesRequest request, ActionListener<GetAliasesResponse> listener) {
            execute(GetAliasesAction.INSTANCE, request, listener);
        }

        @Override
        public GetAliasesRequestBuilder prepareGetAliases(String... aliases) {
            return new GetAliasesRequestBuilder(this, GetAliasesAction.INSTANCE, aliases);
        }

        @Override
        public ActionFuture<ClearIndicesCacheResponse> clearCache(final ClearIndicesCacheRequest request) {
            return execute(ClearIndicesCacheAction.INSTANCE, request);
        }

        @Override
        public void aliasesExist(GetAliasesRequest request, ActionListener<AliasesExistResponse> listener) {
            execute(AliasesExistAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<AliasesExistResponse> aliasesExist(GetAliasesRequest request) {
            return execute(AliasesExistAction.INSTANCE, request);
        }

        @Override
        public AliasesExistRequestBuilder prepareAliasesExist(String... aliases) {
            return new AliasesExistRequestBuilder(this, AliasesExistAction.INSTANCE, aliases);
        }

        @Override
        public ActionFuture<GetIndexResponse> getIndex(GetIndexRequest request) {
            return execute(GetIndexAction.INSTANCE, request);
        }

        @Override
        public void getIndex(GetIndexRequest request, ActionListener<GetIndexResponse> listener) {
            execute(GetIndexAction.INSTANCE, request, listener);
        }

        @Override
        public GetIndexRequestBuilder prepareGetIndex() {
            return new GetIndexRequestBuilder(this, GetIndexAction.INSTANCE);
        }

        @Override
        public void clearCache(final ClearIndicesCacheRequest request, final ActionListener<ClearIndicesCacheResponse> listener) {
            execute(ClearIndicesCacheAction.INSTANCE, request, listener);
        }

        @Override
        public ClearIndicesCacheRequestBuilder prepareClearCache(String... indices) {
            return new ClearIndicesCacheRequestBuilder(this, ClearIndicesCacheAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<CreateIndexResponse> create(final CreateIndexRequest request) {
            return execute(CreateIndexAction.INSTANCE, request);
        }

        @Override
        public void create(final CreateIndexRequest request, final ActionListener<CreateIndexResponse> listener) {
            execute(CreateIndexAction.INSTANCE, request, listener);
        }

        @Override
        public CreateIndexRequestBuilder prepareCreate(String index) {
            return new CreateIndexRequestBuilder(this, CreateIndexAction.INSTANCE, index);
        }

        @Override
        public ActionFuture<DeleteIndexResponse> delete(final DeleteIndexRequest request) {
            return execute(DeleteIndexAction.INSTANCE, request);
        }

        @Override
        public void delete(final DeleteIndexRequest request, final ActionListener<DeleteIndexResponse> listener) {
            execute(DeleteIndexAction.INSTANCE, request, listener);
        }

        @Override
        public DeleteIndexRequestBuilder prepareDelete(String... indices) {
            return new DeleteIndexRequestBuilder(this, DeleteIndexAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<CloseIndexResponse> close(final CloseIndexRequest request) {
            return execute(CloseIndexAction.INSTANCE, request);
        }

        @Override
        public void close(final CloseIndexRequest request, final ActionListener<CloseIndexResponse> listener) {
            execute(CloseIndexAction.INSTANCE, request, listener);
        }

        @Override
        public CloseIndexRequestBuilder prepareClose(String... indices) {
            return new CloseIndexRequestBuilder(this, CloseIndexAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<OpenIndexResponse> open(final OpenIndexRequest request) {
            return execute(OpenIndexAction.INSTANCE, request);
        }

        @Override
        public void open(final OpenIndexRequest request, final ActionListener<OpenIndexResponse> listener) {
            execute(OpenIndexAction.INSTANCE, request, listener);
        }

        @Override
        public OpenIndexRequestBuilder prepareOpen(String... indices) {
            return new OpenIndexRequestBuilder(this, OpenIndexAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<FlushResponse> flush(final FlushRequest request) {
            return execute(FlushAction.INSTANCE, request);
        }

        @Override
        public void flush(final FlushRequest request, final ActionListener<FlushResponse> listener) {
            execute(FlushAction.INSTANCE, request, listener);
        }

        @Override
        public FlushRequestBuilder prepareFlush(String... indices) {
            return new FlushRequestBuilder(this, FlushAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<SyncedFlushResponse> syncedFlush(SyncedFlushRequest request) {
            return execute(SyncedFlushAction.INSTANCE, request);
        }

        @Override
        public void syncedFlush(SyncedFlushRequest request, ActionListener<SyncedFlushResponse> listener) {
            execute(SyncedFlushAction.INSTANCE, request, listener);
        }

        @Override
        public SyncedFlushRequestBuilder prepareSyncedFlush(String... indices) {
            return new SyncedFlushRequestBuilder(this, SyncedFlushAction.INSTANCE).setIndices(indices);
        }

        @Override
        public void getMappings(GetMappingsRequest request, ActionListener<GetMappingsResponse> listener) {
            execute(GetMappingsAction.INSTANCE, request, listener);
        }

        @Override
        public void getFieldMappings(GetFieldMappingsRequest request, ActionListener<GetFieldMappingsResponse> listener) {
            execute(GetFieldMappingsAction.INSTANCE, request, listener);
        }

        @Override
        public GetMappingsRequestBuilder prepareGetMappings(String... indices) {
            return new GetMappingsRequestBuilder(this, GetMappingsAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<GetMappingsResponse> getMappings(GetMappingsRequest request) {
            return execute(GetMappingsAction.INSTANCE, request);
        }

        @Override
        public GetFieldMappingsRequestBuilder prepareGetFieldMappings(String... indices) {
            return new GetFieldMappingsRequestBuilder(this, GetFieldMappingsAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<GetFieldMappingsResponse> getFieldMappings(GetFieldMappingsRequest request) {
            return execute(GetFieldMappingsAction.INSTANCE, request);
        }

        @Override
        public ActionFuture<PutMappingResponse> putMapping(final PutMappingRequest request) {
            return execute(PutMappingAction.INSTANCE, request);
        }

        @Override
        public void putMapping(final PutMappingRequest request, final ActionListener<PutMappingResponse> listener) {
            execute(PutMappingAction.INSTANCE, request, listener);
        }

        @Override
        public PutMappingRequestBuilder preparePutMapping(String... indices) {
            return new PutMappingRequestBuilder(this, PutMappingAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<ForceMergeResponse> forceMerge(final ForceMergeRequest request) {
            return execute(ForceMergeAction.INSTANCE, request);
        }

        @Override
        public void forceMerge(final ForceMergeRequest request, final ActionListener<ForceMergeResponse> listener) {
            execute(ForceMergeAction.INSTANCE, request, listener);
        }

        @Override
        public ForceMergeRequestBuilder prepareForceMerge(String... indices) {
            return new ForceMergeRequestBuilder(this, ForceMergeAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<UpgradeResponse> upgrade(final UpgradeRequest request) {
            return execute(UpgradeAction.INSTANCE, request);
        }

        @Override
        public void upgrade(final UpgradeRequest request, final ActionListener<UpgradeResponse> listener) {
            execute(UpgradeAction.INSTANCE, request, listener);
        }

        @Override
        public UpgradeRequestBuilder prepareUpgrade(String... indices) {
            return new UpgradeRequestBuilder(this, UpgradeAction.INSTANCE).setIndices(indices);
        }


        @Override
        public ActionFuture<UpgradeStatusResponse> upgradeStatus(final UpgradeStatusRequest request) {
            return execute(UpgradeStatusAction.INSTANCE, request);
        }

        @Override
        public void upgradeStatus(final UpgradeStatusRequest request, final ActionListener<UpgradeStatusResponse> listener) {
            execute(UpgradeStatusAction.INSTANCE, request, listener);
        }

        @Override
        public UpgradeStatusRequestBuilder prepareUpgradeStatus(String... indices) {
            return new UpgradeStatusRequestBuilder(this, UpgradeStatusAction.INSTANCE).setIndices(indices);
        }
        @Override
        public ActionFuture<RefreshResponse> refresh(final RefreshRequest request) {
            return execute(RefreshAction.INSTANCE, request);
        }

        @Override
        public void refresh(final RefreshRequest request, final ActionListener<RefreshResponse> listener) {
            execute(RefreshAction.INSTANCE, request, listener);
        }

        @Override
        public RefreshRequestBuilder prepareRefresh(String... indices) {
            return new RefreshRequestBuilder(this, RefreshAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<IndicesStatsResponse> stats(final IndicesStatsRequest request) {
            return execute(IndicesStatsAction.INSTANCE, request);
        }

        @Override
        public void stats(final IndicesStatsRequest request, final ActionListener<IndicesStatsResponse> listener) {
            execute(IndicesStatsAction.INSTANCE, request, listener);
        }

        @Override
        public IndicesStatsRequestBuilder prepareStats(String... indices) {
            return new IndicesStatsRequestBuilder(this, IndicesStatsAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<RecoveryResponse> recoveries(final RecoveryRequest request) {
            return execute(RecoveryAction.INSTANCE, request);
        }

        @Override
        public void recoveries(final RecoveryRequest request, final ActionListener<RecoveryResponse> listener) {
            execute(RecoveryAction.INSTANCE, request, listener);
        }

        @Override
        public RecoveryRequestBuilder prepareRecoveries(String... indices) {
            return new RecoveryRequestBuilder(this, RecoveryAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<IndicesSegmentResponse> segments(final IndicesSegmentsRequest request) {
            return execute(IndicesSegmentsAction.INSTANCE, request);
        }

        @Override
        public void segments(final IndicesSegmentsRequest request, final ActionListener<IndicesSegmentResponse> listener) {
            execute(IndicesSegmentsAction.INSTANCE, request, listener);
        }

        @Override
        public IndicesSegmentsRequestBuilder prepareSegments(String... indices) {
            return new IndicesSegmentsRequestBuilder(this, IndicesSegmentsAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<IndicesShardStoresResponse> shardStores(IndicesShardStoresRequest request) {
            return execute(IndicesShardStoresAction.INSTANCE, request);
        }

        @Override
        public void shardStores(IndicesShardStoresRequest request, ActionListener<IndicesShardStoresResponse> listener) {
            execute(IndicesShardStoresAction.INSTANCE, request, listener);
        }

        @Override
        public IndicesShardStoreRequestBuilder prepareShardStores(String... indices) {
            return new IndicesShardStoreRequestBuilder(this, IndicesShardStoresAction.INSTANCE, indices);
        }

        @Override
        public ActionFuture<UpdateSettingsResponse> updateSettings(final UpdateSettingsRequest request) {
            return execute(UpdateSettingsAction.INSTANCE, request);
        }

        @Override
        public void updateSettings(final UpdateSettingsRequest request, final ActionListener<UpdateSettingsResponse> listener) {
            execute(UpdateSettingsAction.INSTANCE, request, listener);
        }

        @Override
        public UpdateSettingsRequestBuilder prepareUpdateSettings(String... indices) {
            return new UpdateSettingsRequestBuilder(this, UpdateSettingsAction.INSTANCE).setIndices(indices);
        }

        @Override
        public ActionFuture<AnalyzeResponse> analyze(final AnalyzeRequest request) {
            return execute(AnalyzeAction.INSTANCE, request);
        }

        @Override
        public void analyze(final AnalyzeRequest request, final ActionListener<AnalyzeResponse> listener) {
            execute(AnalyzeAction.INSTANCE, request, listener);
        }

        @Override
        public AnalyzeRequestBuilder prepareAnalyze(@Nullable String index, String text) {
            return new AnalyzeRequestBuilder(this, AnalyzeAction.INSTANCE, index, text);
        }

        @Override
        public AnalyzeRequestBuilder prepareAnalyze(String text) {
            return new AnalyzeRequestBuilder(this, AnalyzeAction.INSTANCE, null, text);
        }

        @Override
        public AnalyzeRequestBuilder prepareAnalyze() {
            return new AnalyzeRequestBuilder(this, AnalyzeAction.INSTANCE);
        }

        @Override
        public ActionFuture<PutIndexTemplateResponse> putTemplate(final PutIndexTemplateRequest request) {
            return execute(PutIndexTemplateAction.INSTANCE, request);
        }

        @Override
        public void putTemplate(final PutIndexTemplateRequest request, final ActionListener<PutIndexTemplateResponse> listener) {
            execute(PutIndexTemplateAction.INSTANCE, request, listener);
        }

        @Override
        public PutIndexTemplateRequestBuilder preparePutTemplate(String name) {
            return new PutIndexTemplateRequestBuilder(this, PutIndexTemplateAction.INSTANCE, name);
        }

        @Override
        public ActionFuture<GetIndexTemplatesResponse> getTemplates(final GetIndexTemplatesRequest request) {
            return execute(GetIndexTemplatesAction.INSTANCE, request);
        }

        @Override
        public void getTemplates(final GetIndexTemplatesRequest request, final ActionListener<GetIndexTemplatesResponse> listener) {
            execute(GetIndexTemplatesAction.INSTANCE, request, listener);
        }

        @Override
        public GetIndexTemplatesRequestBuilder prepareGetTemplates(String... names) {
            return new GetIndexTemplatesRequestBuilder(this, GetIndexTemplatesAction.INSTANCE, names);
        }

        @Override
        public ActionFuture<DeleteIndexTemplateResponse> deleteTemplate(final DeleteIndexTemplateRequest request) {
            return execute(DeleteIndexTemplateAction.INSTANCE, request);
        }

        @Override
        public void deleteTemplate(final DeleteIndexTemplateRequest request, final ActionListener<DeleteIndexTemplateResponse> listener) {
            execute(DeleteIndexTemplateAction.INSTANCE, request, listener);
        }

        @Override
        public DeleteIndexTemplateRequestBuilder prepareDeleteTemplate(String name) {
            return new DeleteIndexTemplateRequestBuilder(this, DeleteIndexTemplateAction.INSTANCE, name);
        }

        @Override
        public ActionFuture<ValidateQueryResponse> validateQuery(final ValidateQueryRequest request) {
            return execute(ValidateQueryAction.INSTANCE, request);
        }

        @Override
        public void validateQuery(final ValidateQueryRequest request, final ActionListener<ValidateQueryResponse> listener) {
            execute(ValidateQueryAction.INSTANCE, request, listener);
        }

        @Override
        public ValidateQueryRequestBuilder prepareValidateQuery(String... indices) {
            return new ValidateQueryRequestBuilder(this, ValidateQueryAction.INSTANCE).setIndices(indices);
        }

        @Override
        public GetSettingsRequestBuilder prepareGetSettings(String... indices) {
            return new GetSettingsRequestBuilder(this, GetSettingsAction.INSTANCE, indices);
        }

        @Override
        public ShrinkRequestBuilder prepareShrinkIndex(String sourceIndex, String targetIndex) {
            return new ShrinkRequestBuilder(this, ShrinkAction.INSTANCE).setSourceIndex(sourceIndex)
                .setTargetIndex(new CreateIndexRequest(targetIndex));
        }

        @Override
        public ActionFuture<ShrinkResponse> shrinkIndex(ShrinkRequest request) {
            return execute(ShrinkAction.INSTANCE, request);
        }

        @Override
        public void shrinkIndex(ShrinkRequest request, ActionListener<ShrinkResponse> listener) {
            execute(ShrinkAction.INSTANCE, request, listener);
        }

        @Override
        public RolloverRequestBuilder prepareRolloverIndex(String alias) {
            return new RolloverRequestBuilder(this, RolloverAction.INSTANCE).setAlias(alias);
        }

        @Override
        public ActionFuture<RolloverResponse> rolloversIndex(RolloverRequest request) {
            return execute(RolloverAction.INSTANCE, request);
        }

        @Override
        public void rolloverIndex(RolloverRequest request, ActionListener<RolloverResponse> listener) {
            execute(RolloverAction.INSTANCE, request, listener);
        }

        @Override
        public ActionFuture<GetSettingsResponse> getSettings(GetSettingsRequest request) {
            return execute(GetSettingsAction.INSTANCE, request);
        }

        @Override
        public void getSettings(GetSettingsRequest request, ActionListener<GetSettingsResponse> listener) {
            execute(GetSettingsAction.INSTANCE, request, listener);
        }
    }

    @Override
    public Client filterWithHeader(Map<String, String> headers) {
        return new FilterClient(this) {
            @Override
            protected <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void doExecute(Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
                ThreadContext threadContext = threadPool().getThreadContext();
                try (ThreadContext.StoredContext ctx = threadContext.stashAndMergeHeaders(headers)) {
                    super.doExecute(action, request, listener);
                }
            }
        };
    }
}
