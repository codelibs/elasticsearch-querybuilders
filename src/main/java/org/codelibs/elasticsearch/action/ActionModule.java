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

package org.codelibs.elasticsearch.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.action.admin.cluster.allocation.ClusterAllocationExplainAction;
import org.codelibs.elasticsearch.action.admin.cluster.allocation.TransportClusterAllocationExplainAction;
import org.codelibs.elasticsearch.action.admin.cluster.health.ClusterHealthAction;
import org.codelibs.elasticsearch.action.admin.cluster.health.TransportClusterHealthAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.hotthreads.NodesHotThreadsAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.hotthreads.TransportNodesHotThreadsAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.info.NodesInfoAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.info.TransportNodesInfoAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.liveness.TransportLivenessAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.stats.NodesStatsAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.stats.TransportNodesStatsAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.cancel.CancelTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.cancel.TransportCancelTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.get.TransportGetTaskAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.node.tasks.list.TransportListTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.delete.TransportDeleteRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.get.TransportGetRepositoriesAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.put.TransportPutRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.verify.TransportVerifyRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryAction;
import org.codelibs.elasticsearch.action.admin.cluster.reroute.ClusterRerouteAction;
import org.codelibs.elasticsearch.action.admin.cluster.reroute.TransportClusterRerouteAction;
import org.codelibs.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsAction;
import org.codelibs.elasticsearch.action.admin.cluster.settings.TransportClusterUpdateSettingsAction;
import org.codelibs.elasticsearch.action.admin.cluster.shards.ClusterSearchShardsAction;
import org.codelibs.elasticsearch.action.admin.cluster.shards.TransportClusterSearchShardsAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.create.TransportCreateSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.delete.TransportDeleteSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.get.TransportGetSnapshotsAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.restore.TransportRestoreSnapshotAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusAction;
import org.codelibs.elasticsearch.action.admin.cluster.snapshots.status.TransportSnapshotsStatusAction;
import org.codelibs.elasticsearch.action.admin.cluster.state.ClusterStateAction;
import org.codelibs.elasticsearch.action.admin.cluster.state.TransportClusterStateAction;
import org.codelibs.elasticsearch.action.admin.cluster.stats.ClusterStatsAction;
import org.codelibs.elasticsearch.action.admin.cluster.stats.TransportClusterStatsAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.DeleteStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.TransportDeleteStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.TransportGetStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.storedscripts.TransportPutStoredScriptAction;
import org.codelibs.elasticsearch.action.admin.cluster.tasks.PendingClusterTasksAction;
import org.codelibs.elasticsearch.action.admin.cluster.tasks.TransportPendingClusterTasksAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.IndicesAliasesAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.TransportIndicesAliasesAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.exists.AliasesExistAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.exists.TransportAliasesExistAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.get.GetAliasesAction;
import org.codelibs.elasticsearch.action.admin.indices.alias.get.TransportGetAliasesAction;
import org.codelibs.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.codelibs.elasticsearch.action.admin.indices.analyze.TransportAnalyzeAction;
import org.codelibs.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheAction;
import org.codelibs.elasticsearch.action.admin.indices.cache.clear.TransportClearIndicesCacheAction;
import org.codelibs.elasticsearch.action.admin.indices.close.CloseIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.close.TransportCloseIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.create.TransportCreateIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.delete.DeleteIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.delete.TransportDeleteIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.exists.indices.IndicesExistsAction;
import org.codelibs.elasticsearch.action.admin.indices.exists.indices.TransportIndicesExistsAction;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TransportTypesExistsAction;
import org.codelibs.elasticsearch.action.admin.indices.exists.types.TypesExistsAction;
import org.codelibs.elasticsearch.action.admin.indices.flush.FlushAction;
import org.codelibs.elasticsearch.action.admin.indices.flush.SyncedFlushAction;
import org.codelibs.elasticsearch.action.admin.indices.flush.TransportFlushAction;
import org.codelibs.elasticsearch.action.admin.indices.flush.TransportSyncedFlushAction;
import org.codelibs.elasticsearch.action.admin.indices.forcemerge.ForceMergeAction;
import org.codelibs.elasticsearch.action.admin.indices.forcemerge.TransportForceMergeAction;
import org.codelibs.elasticsearch.action.admin.indices.get.GetIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.get.TransportGetIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.GetMappingsAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.TransportGetFieldMappingsAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.TransportGetFieldMappingsIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.get.TransportGetMappingsAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.put.PutMappingAction;
import org.codelibs.elasticsearch.action.admin.indices.mapping.put.TransportPutMappingAction;
import org.codelibs.elasticsearch.action.admin.indices.open.OpenIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.open.TransportOpenIndexAction;
import org.codelibs.elasticsearch.action.admin.indices.recovery.RecoveryAction;
import org.codelibs.elasticsearch.action.admin.indices.recovery.TransportRecoveryAction;
import org.codelibs.elasticsearch.action.admin.indices.refresh.RefreshAction;
import org.codelibs.elasticsearch.action.admin.indices.refresh.TransportRefreshAction;
import org.codelibs.elasticsearch.action.admin.indices.rollover.RolloverAction;
import org.codelibs.elasticsearch.action.admin.indices.rollover.TransportRolloverAction;
import org.codelibs.elasticsearch.action.admin.indices.segments.IndicesSegmentsAction;
import org.codelibs.elasticsearch.action.admin.indices.segments.TransportIndicesSegmentsAction;
import org.codelibs.elasticsearch.action.admin.indices.settings.get.GetSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.settings.get.TransportGetSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.settings.put.TransportUpdateSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.settings.put.UpdateSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.shards.IndicesShardStoresAction;
import org.codelibs.elasticsearch.action.admin.indices.shards.TransportIndicesShardStoresAction;
import org.codelibs.elasticsearch.action.admin.indices.shrink.ShrinkAction;
import org.codelibs.elasticsearch.action.admin.indices.shrink.TransportShrinkAction;
import org.codelibs.elasticsearch.action.admin.indices.stats.IndicesStatsAction;
import org.codelibs.elasticsearch.action.admin.indices.stats.TransportIndicesStatsAction;
import org.codelibs.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateAction;
import org.codelibs.elasticsearch.action.admin.indices.template.delete.TransportDeleteIndexTemplateAction;
import org.codelibs.elasticsearch.action.admin.indices.template.get.GetIndexTemplatesAction;
import org.codelibs.elasticsearch.action.admin.indices.template.get.TransportGetIndexTemplatesAction;
import org.codelibs.elasticsearch.action.admin.indices.template.put.PutIndexTemplateAction;
import org.codelibs.elasticsearch.action.admin.indices.template.put.TransportPutIndexTemplateAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.get.TransportUpgradeStatusAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.get.UpgradeStatusAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.TransportUpgradeAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.TransportUpgradeSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.UpgradeAction;
import org.codelibs.elasticsearch.action.admin.indices.upgrade.post.UpgradeSettingsAction;
import org.codelibs.elasticsearch.action.admin.indices.validate.query.TransportValidateQueryAction;
import org.codelibs.elasticsearch.action.admin.indices.validate.query.ValidateQueryAction;
import org.codelibs.elasticsearch.action.bulk.BulkAction;
import org.codelibs.elasticsearch.action.bulk.TransportBulkAction;
import org.codelibs.elasticsearch.action.bulk.TransportShardBulkAction;
import org.codelibs.elasticsearch.action.delete.DeleteAction;
import org.codelibs.elasticsearch.action.delete.TransportDeleteAction;
import org.codelibs.elasticsearch.action.explain.ExplainAction;
import org.codelibs.elasticsearch.action.explain.TransportExplainAction;
import org.codelibs.elasticsearch.action.fieldstats.FieldStatsAction;
import org.codelibs.elasticsearch.action.fieldstats.TransportFieldStatsAction;
import org.codelibs.elasticsearch.action.get.GetAction;
import org.codelibs.elasticsearch.action.get.MultiGetAction;
import org.codelibs.elasticsearch.action.get.TransportGetAction;
import org.codelibs.elasticsearch.action.get.TransportMultiGetAction;
import org.codelibs.elasticsearch.action.get.TransportShardMultiGetAction;
import org.codelibs.elasticsearch.action.index.IndexAction;
import org.codelibs.elasticsearch.action.index.TransportIndexAction;
import org.codelibs.elasticsearch.action.ingest.DeletePipelineAction;
import org.codelibs.elasticsearch.action.ingest.DeletePipelineTransportAction;
import org.codelibs.elasticsearch.action.ingest.GetPipelineAction;
import org.codelibs.elasticsearch.action.ingest.GetPipelineTransportAction;
import org.codelibs.elasticsearch.action.ingest.PutPipelineAction;
import org.codelibs.elasticsearch.action.ingest.PutPipelineTransportAction;
import org.codelibs.elasticsearch.action.ingest.SimulatePipelineAction;
import org.codelibs.elasticsearch.action.ingest.SimulatePipelineTransportAction;
import org.codelibs.elasticsearch.action.main.MainAction;
import org.codelibs.elasticsearch.action.main.TransportMainAction;
import org.codelibs.elasticsearch.action.search.ClearScrollAction;
import org.codelibs.elasticsearch.action.search.MultiSearchAction;
import org.codelibs.elasticsearch.action.search.SearchAction;
import org.codelibs.elasticsearch.action.search.SearchScrollAction;
import org.codelibs.elasticsearch.action.search.TransportClearScrollAction;
import org.codelibs.elasticsearch.action.search.TransportMultiSearchAction;
import org.codelibs.elasticsearch.action.search.TransportSearchAction;
import org.codelibs.elasticsearch.action.search.TransportSearchScrollAction;
import org.codelibs.elasticsearch.action.support.ActionFilter;
import org.codelibs.elasticsearch.action.support.ActionFilters;
import org.codelibs.elasticsearch.action.support.AutoCreateIndex;
import org.codelibs.elasticsearch.action.support.DestructiveOperations;
import org.codelibs.elasticsearch.action.support.TransportAction;
import org.codelibs.elasticsearch.action.termvectors.MultiTermVectorsAction;
import org.codelibs.elasticsearch.action.termvectors.TermVectorsAction;
import org.codelibs.elasticsearch.action.termvectors.TransportMultiTermVectorsAction;
import org.codelibs.elasticsearch.action.termvectors.TransportShardMultiTermsVectorAction;
import org.codelibs.elasticsearch.action.termvectors.TransportTermVectorsAction;
import org.codelibs.elasticsearch.action.update.TransportUpdateAction;
import org.codelibs.elasticsearch.action.update.UpdateAction;
import org.codelibs.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.codelibs.elasticsearch.common.NamedRegistry;
import org.codelibs.elasticsearch.common.inject.AbstractModule;
import org.codelibs.elasticsearch.common.inject.multibindings.MapBinder;
import org.codelibs.elasticsearch.common.inject.multibindings.Multibinder;
import org.codelibs.elasticsearch.common.logging.ESLoggerFactory;
import org.codelibs.elasticsearch.common.network.NetworkModule;
import org.codelibs.elasticsearch.common.settings.ClusterSettings;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.plugins.ActionPlugin;
import org.codelibs.elasticsearch.plugins.ActionPlugin.ActionHandler;
import org.codelibs.elasticsearch.rest.RestController;
import org.codelibs.elasticsearch.rest.RestHandler;
import org.codelibs.elasticsearch.rest.action.RestFieldStatsAction;
import org.codelibs.elasticsearch.rest.action.RestMainAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestCancelTasksAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterAllocationExplainAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterGetSettingsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterHealthAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterRerouteAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterSearchShardsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterStateAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterStatsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestClusterUpdateSettingsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestCreateSnapshotAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestDeleteRepositoryAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestDeleteSnapshotAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestDeleteStoredScriptAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestGetRepositoriesAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestGetSnapshotsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestGetStoredScriptAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestGetTaskAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestListTasksAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestNodesHotThreadsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestNodesInfoAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestNodesStatsAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestPendingClusterTasksAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestPutRepositoryAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestPutStoredScriptAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestRestoreSnapshotAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestSnapshotsStatusAction;
import org.codelibs.elasticsearch.rest.action.admin.cluster.RestVerifyRepositoryAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestAliasesExistAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestAnalyzeAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestClearIndicesCacheAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestCloseIndexAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestCreateIndexAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestDeleteIndexAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestDeleteIndexTemplateAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestFlushAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestForceMergeAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestGetAliasesAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestGetFieldMappingAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestGetIndexTemplateAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestGetIndicesAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestGetMappingAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestGetSettingsAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestHeadIndexTemplateAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndexDeleteAliasesAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndexPutAliasAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndicesAliasesAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndicesExistsAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndicesSegmentsAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndicesShardStoresAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestIndicesStatsAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestOpenIndexAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestPutIndexTemplateAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestPutMappingAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestRecoveryAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestRefreshAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestRolloverIndexAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestShrinkIndexAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestSyncedFlushAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestTypesExistsAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestUpdateSettingsAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestUpgradeAction;
import org.codelibs.elasticsearch.rest.action.admin.indices.RestValidateQueryAction;
import org.codelibs.elasticsearch.rest.action.cat.AbstractCatAction;
import org.codelibs.elasticsearch.rest.action.cat.RestAliasAction;
import org.codelibs.elasticsearch.rest.action.cat.RestAllocationAction;
import org.codelibs.elasticsearch.rest.action.cat.RestCatAction;
import org.codelibs.elasticsearch.rest.action.cat.RestFielddataAction;
import org.codelibs.elasticsearch.rest.action.cat.RestHealthAction;
import org.codelibs.elasticsearch.rest.action.cat.RestIndicesAction;
import org.codelibs.elasticsearch.rest.action.cat.RestMasterAction;
import org.codelibs.elasticsearch.rest.action.cat.RestNodeAttrsAction;
import org.codelibs.elasticsearch.rest.action.cat.RestNodesAction;
import org.codelibs.elasticsearch.rest.action.cat.RestPluginsAction;
import org.codelibs.elasticsearch.rest.action.cat.RestRepositoriesAction;
import org.codelibs.elasticsearch.rest.action.cat.RestSegmentsAction;
import org.codelibs.elasticsearch.rest.action.cat.RestShardsAction;
import org.codelibs.elasticsearch.rest.action.cat.RestSnapshotAction;
import org.codelibs.elasticsearch.rest.action.cat.RestTasksAction;
import org.codelibs.elasticsearch.rest.action.cat.RestTemplatesAction;
import org.codelibs.elasticsearch.rest.action.cat.RestThreadPoolAction;
import org.codelibs.elasticsearch.rest.action.document.RestBulkAction;
import org.codelibs.elasticsearch.rest.action.document.RestDeleteAction;
import org.codelibs.elasticsearch.rest.action.document.RestGetAction;
import org.codelibs.elasticsearch.rest.action.document.RestGetSourceAction;
import org.codelibs.elasticsearch.rest.action.document.RestHeadAction;
import org.codelibs.elasticsearch.rest.action.document.RestIndexAction;
import org.codelibs.elasticsearch.rest.action.document.RestMultiGetAction;
import org.codelibs.elasticsearch.rest.action.document.RestMultiTermVectorsAction;
import org.codelibs.elasticsearch.rest.action.document.RestTermVectorsAction;
import org.codelibs.elasticsearch.rest.action.document.RestUpdateAction;
import org.codelibs.elasticsearch.rest.action.ingest.RestDeletePipelineAction;
import org.codelibs.elasticsearch.rest.action.ingest.RestGetPipelineAction;
import org.codelibs.elasticsearch.rest.action.ingest.RestPutPipelineAction;
import org.codelibs.elasticsearch.rest.action.ingest.RestSimulatePipelineAction;
import org.codelibs.elasticsearch.rest.action.search.RestClearScrollAction;
import org.codelibs.elasticsearch.rest.action.search.RestExplainAction;
import org.codelibs.elasticsearch.rest.action.search.RestMultiSearchAction;
import org.codelibs.elasticsearch.rest.action.search.RestSearchAction;
import org.codelibs.elasticsearch.rest.action.search.RestSearchScrollAction;
import org.codelibs.elasticsearch.rest.action.search.RestSuggestAction;
import org.codelibs.elasticsearch.threadpool.ThreadPool;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * Builds and binds the generic action map, all {@link TransportAction}s, and {@link ActionFilters}.
 */
public class ActionModule extends AbstractModule {

    private static final Logger logger = ESLoggerFactory.getLogger(ActionModule.class);

    private final boolean transportClient;
    private final Settings settings;
    private final List<ActionPlugin> actionPlugins;
    private final Map<String, ActionHandler<?, ?>> actions;
    private final List<Class<? extends ActionFilter>> actionFilters;
    private final AutoCreateIndex autoCreateIndex;
    private final DestructiveOperations destructiveOperations;
    private final RestController restController;

    public ActionModule(boolean transportClient, Settings settings, IndexNameExpressionResolver resolver,
                        ClusterSettings clusterSettings, ThreadPool threadPool, List<ActionPlugin> actionPlugins) {
        this.transportClient = transportClient;
        this.settings = settings;
        this.actionPlugins = actionPlugins;
        actions = setupActions(actionPlugins);
        actionFilters = setupActionFilters(actionPlugins);
        autoCreateIndex = transportClient ? null : new AutoCreateIndex(settings, clusterSettings, resolver);
        destructiveOperations = new DestructiveOperations(settings, clusterSettings);
        Set<String> headers = actionPlugins.stream().flatMap(p -> p.getRestHeaders().stream()).collect(Collectors.toSet());
        UnaryOperator<RestHandler> restWrapper = null;
        for (ActionPlugin plugin : actionPlugins) {
            UnaryOperator<RestHandler> newRestWrapper = plugin.getRestHandlerWrapper(threadPool.getThreadContext());
            if (newRestWrapper != null) {
                logger.debug("Using REST wrapper from plugin " + plugin.getClass().getName());
                if (restWrapper != null) {
                    throw new IllegalArgumentException("Cannot have more than one plugin implementing a REST wrapper");
                }
                restWrapper = newRestWrapper;
            }
        }
        restController = new RestController(settings, headers, restWrapper);
    }

    public Map<String, ActionHandler<?, ?>> getActions() {
        return actions;
    }

    static Map<String, ActionHandler<?, ?>> setupActions(List<ActionPlugin> actionPlugins) {
        // Subclass NamedRegistry for easy registration
        class ActionRegistry extends NamedRegistry<ActionHandler<?, ?>> {
            public ActionRegistry() {
                super("action");
            }

            public void register(ActionHandler<?, ?> handler) {
                register(handler.getAction().name(), handler);
            }

            public <Request extends ActionRequest, Response extends ActionResponse> void register(
                    GenericAction<Request, Response> action, Class<? extends TransportAction<Request, Response>> transportAction,
                    Class<?>... supportTransportActions) {
                register(new ActionHandler<>(action, transportAction, supportTransportActions));
            }
        }
        ActionRegistry actions = new ActionRegistry();

        actions.register(MainAction.INSTANCE, TransportMainAction.class);
        actions.register(NodesInfoAction.INSTANCE, TransportNodesInfoAction.class);
        actions.register(NodesStatsAction.INSTANCE, TransportNodesStatsAction.class);
        actions.register(NodesHotThreadsAction.INSTANCE, TransportNodesHotThreadsAction.class);
        actions.register(ListTasksAction.INSTANCE, TransportListTasksAction.class);
        actions.register(GetTaskAction.INSTANCE, TransportGetTaskAction.class);
        actions.register(CancelTasksAction.INSTANCE, TransportCancelTasksAction.class);

        actions.register(ClusterAllocationExplainAction.INSTANCE, TransportClusterAllocationExplainAction.class);
        actions.register(ClusterStatsAction.INSTANCE, TransportClusterStatsAction.class);
        actions.register(ClusterStateAction.INSTANCE, TransportClusterStateAction.class);
        actions.register(ClusterHealthAction.INSTANCE, TransportClusterHealthAction.class);
        actions.register(ClusterUpdateSettingsAction.INSTANCE, TransportClusterUpdateSettingsAction.class);
        actions.register(ClusterRerouteAction.INSTANCE, TransportClusterRerouteAction.class);
        actions.register(ClusterSearchShardsAction.INSTANCE, TransportClusterSearchShardsAction.class);
        actions.register(PendingClusterTasksAction.INSTANCE, TransportPendingClusterTasksAction.class);
        actions.register(PutRepositoryAction.INSTANCE, TransportPutRepositoryAction.class);
        actions.register(GetRepositoriesAction.INSTANCE, TransportGetRepositoriesAction.class);
        actions.register(DeleteRepositoryAction.INSTANCE, TransportDeleteRepositoryAction.class);
        actions.register(VerifyRepositoryAction.INSTANCE, TransportVerifyRepositoryAction.class);
        actions.register(GetSnapshotsAction.INSTANCE, TransportGetSnapshotsAction.class);
        actions.register(DeleteSnapshotAction.INSTANCE, TransportDeleteSnapshotAction.class);
        actions.register(CreateSnapshotAction.INSTANCE, TransportCreateSnapshotAction.class);
        actions.register(RestoreSnapshotAction.INSTANCE, TransportRestoreSnapshotAction.class);
        actions.register(SnapshotsStatusAction.INSTANCE, TransportSnapshotsStatusAction.class);

        actions.register(IndicesStatsAction.INSTANCE, TransportIndicesStatsAction.class);
        actions.register(IndicesSegmentsAction.INSTANCE, TransportIndicesSegmentsAction.class);
        actions.register(IndicesShardStoresAction.INSTANCE, TransportIndicesShardStoresAction.class);
        actions.register(CreateIndexAction.INSTANCE, TransportCreateIndexAction.class);
        actions.register(ShrinkAction.INSTANCE, TransportShrinkAction.class);
        actions.register(RolloverAction.INSTANCE, TransportRolloverAction.class);
        actions.register(DeleteIndexAction.INSTANCE, TransportDeleteIndexAction.class);
        actions.register(GetIndexAction.INSTANCE, TransportGetIndexAction.class);
        actions.register(OpenIndexAction.INSTANCE, TransportOpenIndexAction.class);
        actions.register(CloseIndexAction.INSTANCE, TransportCloseIndexAction.class);
        actions.register(IndicesExistsAction.INSTANCE, TransportIndicesExistsAction.class);
        actions.register(TypesExistsAction.INSTANCE, TransportTypesExistsAction.class);
        actions.register(GetMappingsAction.INSTANCE, TransportGetMappingsAction.class);
        actions.register(GetFieldMappingsAction.INSTANCE, TransportGetFieldMappingsAction.class,
                TransportGetFieldMappingsIndexAction.class);
        actions.register(PutMappingAction.INSTANCE, TransportPutMappingAction.class);
        actions.register(IndicesAliasesAction.INSTANCE, TransportIndicesAliasesAction.class);
        actions.register(UpdateSettingsAction.INSTANCE, TransportUpdateSettingsAction.class);
        actions.register(AnalyzeAction.INSTANCE, TransportAnalyzeAction.class);
        actions.register(PutIndexTemplateAction.INSTANCE, TransportPutIndexTemplateAction.class);
        actions.register(GetIndexTemplatesAction.INSTANCE, TransportGetIndexTemplatesAction.class);
        actions.register(DeleteIndexTemplateAction.INSTANCE, TransportDeleteIndexTemplateAction.class);
        actions.register(ValidateQueryAction.INSTANCE, TransportValidateQueryAction.class);
        actions.register(RefreshAction.INSTANCE, TransportRefreshAction.class);
        actions.register(FlushAction.INSTANCE, TransportFlushAction.class);
        actions.register(SyncedFlushAction.INSTANCE, TransportSyncedFlushAction.class);
        actions.register(ForceMergeAction.INSTANCE, TransportForceMergeAction.class);
        actions.register(UpgradeAction.INSTANCE, TransportUpgradeAction.class);
        actions.register(UpgradeStatusAction.INSTANCE, TransportUpgradeStatusAction.class);
        actions.register(UpgradeSettingsAction.INSTANCE, TransportUpgradeSettingsAction.class);
        actions.register(ClearIndicesCacheAction.INSTANCE, TransportClearIndicesCacheAction.class);
        actions.register(GetAliasesAction.INSTANCE, TransportGetAliasesAction.class);
        actions.register(AliasesExistAction.INSTANCE, TransportAliasesExistAction.class);
        actions.register(GetSettingsAction.INSTANCE, TransportGetSettingsAction.class);

        actions.register(IndexAction.INSTANCE, TransportIndexAction.class);
        actions.register(GetAction.INSTANCE, TransportGetAction.class);
        actions.register(TermVectorsAction.INSTANCE, TransportTermVectorsAction.class);
        actions.register(MultiTermVectorsAction.INSTANCE, TransportMultiTermVectorsAction.class,
                TransportShardMultiTermsVectorAction.class);
        actions.register(DeleteAction.INSTANCE, TransportDeleteAction.class);
        actions.register(UpdateAction.INSTANCE, TransportUpdateAction.class);
        actions.register(MultiGetAction.INSTANCE, TransportMultiGetAction.class,
                TransportShardMultiGetAction.class);
        actions.register(BulkAction.INSTANCE, TransportBulkAction.class,
                TransportShardBulkAction.class);
        actions.register(SearchAction.INSTANCE, TransportSearchAction.class);
        actions.register(SearchScrollAction.INSTANCE, TransportSearchScrollAction.class);
        actions.register(MultiSearchAction.INSTANCE, TransportMultiSearchAction.class);
        actions.register(ExplainAction.INSTANCE, TransportExplainAction.class);
        actions.register(ClearScrollAction.INSTANCE, TransportClearScrollAction.class);
        actions.register(RecoveryAction.INSTANCE, TransportRecoveryAction.class);

        //Indexed scripts
        actions.register(PutStoredScriptAction.INSTANCE, TransportPutStoredScriptAction.class);
        actions.register(GetStoredScriptAction.INSTANCE, TransportGetStoredScriptAction.class);
        actions.register(DeleteStoredScriptAction.INSTANCE, TransportDeleteStoredScriptAction.class);

        actions.register(FieldStatsAction.INSTANCE, TransportFieldStatsAction.class);

        actions.register(PutPipelineAction.INSTANCE, PutPipelineTransportAction.class);
        actions.register(GetPipelineAction.INSTANCE, GetPipelineTransportAction.class);
        actions.register(DeletePipelineAction.INSTANCE, DeletePipelineTransportAction.class);
        actions.register(SimulatePipelineAction.INSTANCE, SimulatePipelineTransportAction.class);

        actionPlugins.stream().flatMap(p -> p.getActions().stream()).forEach(actions::register);

        return unmodifiableMap(actions.getRegistry());
    }

    private List<Class<? extends ActionFilter>> setupActionFilters(List<ActionPlugin> actionPlugins) {
        return unmodifiableList(actionPlugins.stream().flatMap(p -> p.getActionFilters().stream()).collect(Collectors.toList()));
    }

    static Set<Class<? extends RestHandler>> setupRestHandlers(List<ActionPlugin> actionPlugins) {
        Set<Class<? extends RestHandler>> handlers = new HashSet<>();
        registerRestHandler(handlers, RestMainAction.class);
        registerRestHandler(handlers, RestNodesInfoAction.class);
        registerRestHandler(handlers, RestNodesStatsAction.class);
        registerRestHandler(handlers, RestNodesHotThreadsAction.class);
        registerRestHandler(handlers, RestClusterAllocationExplainAction.class);
        registerRestHandler(handlers, RestClusterStatsAction.class);
        registerRestHandler(handlers, RestClusterStateAction.class);
        registerRestHandler(handlers, RestClusterHealthAction.class);
        registerRestHandler(handlers, RestClusterUpdateSettingsAction.class);
        registerRestHandler(handlers, RestClusterGetSettingsAction.class);
        registerRestHandler(handlers, RestClusterRerouteAction.class);
        registerRestHandler(handlers, RestClusterSearchShardsAction.class);
        registerRestHandler(handlers, RestPendingClusterTasksAction.class);
        registerRestHandler(handlers, RestPutRepositoryAction.class);
        registerRestHandler(handlers, RestGetRepositoriesAction.class);
        registerRestHandler(handlers, RestDeleteRepositoryAction.class);
        registerRestHandler(handlers, RestVerifyRepositoryAction.class);
        registerRestHandler(handlers, RestGetSnapshotsAction.class);
        registerRestHandler(handlers, RestCreateSnapshotAction.class);
        registerRestHandler(handlers, RestRestoreSnapshotAction.class);
        registerRestHandler(handlers, RestDeleteSnapshotAction.class);
        registerRestHandler(handlers, RestSnapshotsStatusAction.class);

        registerRestHandler(handlers, RestIndicesExistsAction.class);
        registerRestHandler(handlers, RestTypesExistsAction.class);
        registerRestHandler(handlers, RestGetIndicesAction.class);
        registerRestHandler(handlers, RestIndicesStatsAction.class);
        registerRestHandler(handlers, RestIndicesSegmentsAction.class);
        registerRestHandler(handlers, RestIndicesShardStoresAction.class);
        registerRestHandler(handlers, RestGetAliasesAction.class);
        registerRestHandler(handlers, RestAliasesExistAction.class);
        registerRestHandler(handlers, RestIndexDeleteAliasesAction.class);
        registerRestHandler(handlers, RestIndexPutAliasAction.class);
        registerRestHandler(handlers, RestIndicesAliasesAction.class);
        registerRestHandler(handlers, RestCreateIndexAction.class);
        registerRestHandler(handlers, RestShrinkIndexAction.class);
        registerRestHandler(handlers, RestRolloverIndexAction.class);
        registerRestHandler(handlers, RestDeleteIndexAction.class);
        registerRestHandler(handlers, RestCloseIndexAction.class);
        registerRestHandler(handlers, RestOpenIndexAction.class);

        registerRestHandler(handlers, RestUpdateSettingsAction.class);
        registerRestHandler(handlers, RestGetSettingsAction.class);

        registerRestHandler(handlers, RestAnalyzeAction.class);
        registerRestHandler(handlers, RestGetIndexTemplateAction.class);
        registerRestHandler(handlers, RestPutIndexTemplateAction.class);
        registerRestHandler(handlers, RestDeleteIndexTemplateAction.class);
        registerRestHandler(handlers, RestHeadIndexTemplateAction.class);

        registerRestHandler(handlers, RestPutMappingAction.class);
        registerRestHandler(handlers, RestGetMappingAction.class);
        registerRestHandler(handlers, RestGetFieldMappingAction.class);

        registerRestHandler(handlers, RestRefreshAction.class);
        registerRestHandler(handlers, RestFlushAction.class);
        registerRestHandler(handlers, RestSyncedFlushAction.class);
        registerRestHandler(handlers, RestForceMergeAction.class);
        registerRestHandler(handlers, RestUpgradeAction.class);
        registerRestHandler(handlers, RestClearIndicesCacheAction.class);

        registerRestHandler(handlers, RestIndexAction.class);
        registerRestHandler(handlers, RestGetAction.class);
        registerRestHandler(handlers, RestGetSourceAction.class);
        registerRestHandler(handlers, RestHeadAction.Document.class);
        registerRestHandler(handlers, RestHeadAction.Source.class);
        registerRestHandler(handlers, RestMultiGetAction.class);
        registerRestHandler(handlers, RestDeleteAction.class);
        registerRestHandler(handlers, org.codelibs.elasticsearch.rest.action.document.RestCountAction.class);
        registerRestHandler(handlers, RestSuggestAction.class);
        registerRestHandler(handlers, RestTermVectorsAction.class);
        registerRestHandler(handlers, RestMultiTermVectorsAction.class);
        registerRestHandler(handlers, RestBulkAction.class);
        registerRestHandler(handlers, RestUpdateAction.class);

        registerRestHandler(handlers, RestSearchAction.class);
        registerRestHandler(handlers, RestSearchScrollAction.class);
        registerRestHandler(handlers, RestClearScrollAction.class);
        registerRestHandler(handlers, RestMultiSearchAction.class);

        registerRestHandler(handlers, RestValidateQueryAction.class);

        registerRestHandler(handlers, RestExplainAction.class);

        registerRestHandler(handlers, RestRecoveryAction.class);

        // Scripts API
        registerRestHandler(handlers, RestGetStoredScriptAction.class);
        registerRestHandler(handlers, RestPutStoredScriptAction.class);
        registerRestHandler(handlers, RestDeleteStoredScriptAction.class);

        registerRestHandler(handlers, RestFieldStatsAction.class);

        // Tasks API
        registerRestHandler(handlers, RestListTasksAction.class);
        registerRestHandler(handlers, RestGetTaskAction.class);
        registerRestHandler(handlers, RestCancelTasksAction.class);

        // Ingest API
        registerRestHandler(handlers, RestPutPipelineAction.class);
        registerRestHandler(handlers, RestGetPipelineAction.class);
        registerRestHandler(handlers, RestDeletePipelineAction.class);
        registerRestHandler(handlers, RestSimulatePipelineAction.class);

        // CAT API
        registerRestHandler(handlers, RestCatAction.class);
        registerRestHandler(handlers, RestAllocationAction.class);
        registerRestHandler(handlers, RestShardsAction.class);
        registerRestHandler(handlers, RestMasterAction.class);
        registerRestHandler(handlers, RestNodesAction.class);
        registerRestHandler(handlers, RestTasksAction.class);
        registerRestHandler(handlers, RestIndicesAction.class);
        registerRestHandler(handlers, RestSegmentsAction.class);
        // Fully qualified to prevent interference with rest.action.count.RestCountAction
        registerRestHandler(handlers, org.codelibs.elasticsearch.rest.action.cat.RestCountAction.class);
        // Fully qualified to prevent interference with rest.action.indices.RestRecoveryAction
        registerRestHandler(handlers, org.codelibs.elasticsearch.rest.action.cat.RestRecoveryAction.class);
        registerRestHandler(handlers, RestHealthAction.class);
        registerRestHandler(handlers, org.codelibs.elasticsearch.rest.action.cat.RestPendingClusterTasksAction.class);
        registerRestHandler(handlers, RestAliasAction.class);
        registerRestHandler(handlers, RestThreadPoolAction.class);
        registerRestHandler(handlers, RestPluginsAction.class);
        registerRestHandler(handlers, RestFielddataAction.class);
        registerRestHandler(handlers, RestNodeAttrsAction.class);
        registerRestHandler(handlers, RestRepositoriesAction.class);
        registerRestHandler(handlers, RestSnapshotAction.class);
        registerRestHandler(handlers, RestTemplatesAction.class);
        for (ActionPlugin plugin : actionPlugins) {
            for (Class<? extends RestHandler> handler : plugin.getRestHandlers()) {
                registerRestHandler(handlers, handler);
            }
        }
        return handlers;
    }

    private static void registerRestHandler(Set<Class<? extends RestHandler>> handlers, Class<? extends RestHandler> handler) {
        if (handlers.contains(handler)) {
            throw new IllegalArgumentException("can't register the same [rest_handler] more than once for [" + handler.getName() + "]");
        }
        handlers.add(handler);
    }

    @Override
    protected void configure() {
        Multibinder<ActionFilter> actionFilterMultibinder = Multibinder.newSetBinder(binder(), ActionFilter.class);
        for (Class<? extends ActionFilter> actionFilter : actionFilters) {
            actionFilterMultibinder.addBinding().to(actionFilter);
        }
        bind(ActionFilters.class).asEagerSingleton();
        bind(DestructiveOperations.class).toInstance(destructiveOperations);

        if (false == transportClient) {
            // Supporting classes only used when not a transport client
            bind(AutoCreateIndex.class).toInstance(autoCreateIndex);
            bind(TransportLivenessAction.class).asEagerSingleton();

            // register GenericAction -> transportAction Map used by NodeClient
            @SuppressWarnings("rawtypes")
            MapBinder<GenericAction, TransportAction> transportActionsBinder
                    = MapBinder.newMapBinder(binder(), GenericAction.class, TransportAction.class);
            for (ActionHandler<?, ?> action : actions.values()) {
                // bind the action as eager singleton, so the map binder one will reuse it
                bind(action.getTransportAction()).asEagerSingleton();
                transportActionsBinder.addBinding(action.getAction()).to(action.getTransportAction()).asEagerSingleton();
                for (Class<?> supportAction : action.getSupportTransportActions()) {
                    bind(supportAction).asEagerSingleton();
                }
            }

            // Bind the RestController which is required (by Node) even if rest isn't enabled.
            bind(RestController.class).toInstance(restController);

            // Setup the RestHandlers
            if (NetworkModule.HTTP_ENABLED.get(settings)) {
                Multibinder<RestHandler> restHandlers = Multibinder.newSetBinder(binder(), RestHandler.class);
                Multibinder<AbstractCatAction> catHandlers = Multibinder.newSetBinder(binder(), AbstractCatAction.class);
                for (Class<? extends RestHandler> handler : setupRestHandlers(actionPlugins)) {
                    bind(handler).asEagerSingleton();
                    if (AbstractCatAction.class.isAssignableFrom(handler)) {
                        catHandlers.addBinding().to(handler.asSubclass(AbstractCatAction.class));
                    } else {
                        restHandlers.addBinding().to(handler);
                    }
                }
            }
        }
    }

    public RestController getRestController() {
        return restController;
    }
}
