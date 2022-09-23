// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.commons.logging.LogFactory;
import javax.ws.rs.DELETE;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.DelegationToken;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CredentialsInfo;
import org.apache.hadoop.security.Credentials;
import java.io.DataOutputStream;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LocalResourceInfo;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.commons.codec.binary.Base64;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceInfo;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationRequest;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationResponse;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationSubmissionContextInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NewApplication;
import java.security.Principal;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlException;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;
import javax.ws.rs.POST;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeLabelsInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeToLabelsInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.yarn.server.resourcemanager.RMAuditLogger;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppState;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptInfo;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppAttemptsInfo;
import java.util.HashMap;
import java.util.Arrays;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.StatisticsItemInfo;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ApplicationStatisticsInfo;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppInfo;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.util.HashSet;
import java.io.IOException;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.webapp.BadRequestException;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppsInfo;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import javax.ws.rs.PathParam;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.RMServerUtils;
import java.util.EnumSet;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodesInfo;
import javax.ws.rs.QueryParam;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.SchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FifoSchedulerInfo;
import org.apache.hadoop.yarn.webapp.NotFoundException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.SchedulerTypeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterMetricsInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterInfo;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import com.google.inject.Inject;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.commons.logging.Log;
import javax.ws.rs.Path;
import com.google.inject.Singleton;

@Singleton
@Path("/ws/v1/cluster")
public class RMWebServices
{
    private static final Log LOG;
    private static final String EMPTY = "";
    private static final String ANY = "*";
    private final ResourceManager rm;
    private static RecordFactory recordFactory;
    private final Configuration conf;
    @Context
    private HttpServletResponse response;
    public static final String DELEGATION_TOKEN_HEADER = "Hadoop-YARN-RM-Delegation-Token";
    
    @Inject
    public RMWebServices(final ResourceManager rm, final Configuration conf) {
        this.rm = rm;
        this.conf = conf;
    }
    
    protected Boolean hasAccess(final RMApp app, final HttpServletRequest hsr) {
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI != null && !this.rm.getApplicationACLsManager().checkAccess(callerUGI, ApplicationAccessType.VIEW_APP, app.getUser(), app.getApplicationId()) && !this.rm.getQueueACLsManager().checkAccess(callerUGI, QueueACL.ADMINISTER_QUEUE, app.getQueue())) {
            return false;
        }
        return true;
    }
    
    private void init() {
        this.response.setContentType(null);
    }
    
    @GET
    @Produces({ "application/json", "application/xml" })
    public ClusterInfo get() {
        return this.getClusterInfo();
    }
    
    @GET
    @Path("/info")
    @Produces({ "application/json", "application/xml" })
    public ClusterInfo getClusterInfo() {
        this.init();
        return new ClusterInfo(this.rm);
    }
    
    @GET
    @Path("/metrics")
    @Produces({ "application/json", "application/xml" })
    public ClusterMetricsInfo getClusterMetricsInfo() {
        this.init();
        return new ClusterMetricsInfo(this.rm, this.rm.getRMContext());
    }
    
    @GET
    @Path("/scheduler")
    @Produces({ "application/json", "application/xml" })
    public SchedulerTypeInfo getSchedulerInfo() {
        this.init();
        final ResourceScheduler rs = this.rm.getResourceScheduler();
        SchedulerInfo sinfo;
        if (rs instanceof CapacityScheduler) {
            final CapacityScheduler cs = (CapacityScheduler)rs;
            final CSQueue root = cs.getRootQueue();
            sinfo = new CapacitySchedulerInfo(root);
        }
        else if (rs instanceof FairScheduler) {
            final FairScheduler fs = (FairScheduler)rs;
            sinfo = new FairSchedulerInfo(fs);
        }
        else {
            if (!(rs instanceof FifoScheduler)) {
                throw new NotFoundException("Unknown scheduler configured");
            }
            sinfo = new FifoSchedulerInfo(this.rm);
        }
        return new SchedulerTypeInfo(sinfo);
    }
    
    @GET
    @Path("/nodes")
    @Produces({ "application/json", "application/xml" })
    public NodesInfo getNodes(@QueryParam("states") final String states) {
        this.init();
        final ResourceScheduler sched = this.rm.getResourceScheduler();
        if (sched == null) {
            throw new NotFoundException("Null ResourceScheduler instance");
        }
        EnumSet<NodeState> acceptedStates;
        if (states == null) {
            acceptedStates = EnumSet.allOf(NodeState.class);
        }
        else {
            acceptedStates = EnumSet.noneOf(NodeState.class);
            for (final String stateStr : states.split(",")) {
                acceptedStates.add(NodeState.valueOf(stateStr.toUpperCase()));
            }
        }
        final Collection<RMNode> rmNodes = RMServerUtils.queryRMNodes(this.rm.getRMContext(), acceptedStates);
        final NodesInfo nodesInfo = new NodesInfo();
        for (final RMNode rmNode : rmNodes) {
            final NodeInfo nodeInfo = new NodeInfo(rmNode, sched);
            if (EnumSet.of(NodeState.LOST, NodeState.DECOMMISSIONED, NodeState.REBOOTED).contains(rmNode.getState())) {
                nodeInfo.setNodeHTTPAddress("");
            }
            nodesInfo.add(nodeInfo);
        }
        return nodesInfo;
    }
    
    @GET
    @Path("/nodes/{nodeId}")
    @Produces({ "application/json", "application/xml" })
    public NodeInfo getNode(@PathParam("nodeId") final String nodeId) {
        this.init();
        if (nodeId == null || nodeId.isEmpty()) {
            throw new NotFoundException("nodeId, " + nodeId + ", is empty or null");
        }
        final ResourceScheduler sched = this.rm.getResourceScheduler();
        if (sched == null) {
            throw new NotFoundException("Null ResourceScheduler instance");
        }
        final NodeId nid = ConverterUtils.toNodeId(nodeId);
        RMNode ni = this.rm.getRMContext().getRMNodes().get(nid);
        boolean isInactive = false;
        if (ni == null) {
            ni = this.rm.getRMContext().getInactiveRMNodes().get(nid.getHost());
            if (ni == null) {
                throw new NotFoundException("nodeId, " + nodeId + ", is not found");
            }
            isInactive = true;
        }
        final NodeInfo nodeInfo = new NodeInfo(ni, sched);
        if (isInactive) {
            nodeInfo.setNodeHTTPAddress("");
        }
        return nodeInfo;
    }
    
    @GET
    @Path("/apps")
    @Produces({ "application/json", "application/xml" })
    public AppsInfo getApps(@Context final HttpServletRequest hsr, @QueryParam("state") final String stateQuery, @QueryParam("states") final Set<String> statesQuery, @QueryParam("finalStatus") final String finalStatusQuery, @QueryParam("user") final String userQuery, @QueryParam("queue") final String queueQuery, @QueryParam("limit") final String count, @QueryParam("startedTimeBegin") final String startedBegin, @QueryParam("startedTimeEnd") final String startedEnd, @QueryParam("finishedTimeBegin") final String finishBegin, @QueryParam("finishedTimeEnd") final String finishEnd, @QueryParam("applicationTypes") final Set<String> applicationTypes, @QueryParam("applicationTags") final Set<String> applicationTags) {
        boolean checkCount = false;
        boolean checkStart = false;
        boolean checkEnd = false;
        boolean checkAppTypes = false;
        boolean checkAppStates = false;
        boolean checkAppTags = false;
        long countNum = 0L;
        long sBegin = 0L;
        long sEnd = Long.MAX_VALUE;
        long fBegin = 0L;
        long fEnd = Long.MAX_VALUE;
        this.init();
        if (count != null && !count.isEmpty()) {
            checkCount = true;
            countNum = Long.parseLong(count);
            if (countNum <= 0L) {
                throw new BadRequestException("limit value must be greater then 0");
            }
        }
        if (startedBegin != null && !startedBegin.isEmpty()) {
            checkStart = true;
            sBegin = Long.parseLong(startedBegin);
            if (sBegin < 0L) {
                throw new BadRequestException("startedTimeBegin must be greater than 0");
            }
        }
        if (startedEnd != null && !startedEnd.isEmpty()) {
            checkStart = true;
            sEnd = Long.parseLong(startedEnd);
            if (sEnd < 0L) {
                throw new BadRequestException("startedTimeEnd must be greater than 0");
            }
        }
        if (sBegin > sEnd) {
            throw new BadRequestException("startedTimeEnd must be greater than startTimeBegin");
        }
        if (finishBegin != null && !finishBegin.isEmpty()) {
            checkEnd = true;
            fBegin = Long.parseLong(finishBegin);
            if (fBegin < 0L) {
                throw new BadRequestException("finishTimeBegin must be greater than 0");
            }
        }
        if (finishEnd != null && !finishEnd.isEmpty()) {
            checkEnd = true;
            fEnd = Long.parseLong(finishEnd);
            if (fEnd < 0L) {
                throw new BadRequestException("finishTimeEnd must be greater than 0");
            }
        }
        if (fBegin > fEnd) {
            throw new BadRequestException("finishTimeEnd must be greater than finishTimeBegin");
        }
        final Set<String> appTypes = parseQueries(applicationTypes, false);
        if (!appTypes.isEmpty()) {
            checkAppTypes = true;
        }
        final Set<String> appTags = parseQueries(applicationTags, false);
        if (!appTags.isEmpty()) {
            checkAppTags = true;
        }
        if (stateQuery != null && !stateQuery.isEmpty()) {
            statesQuery.add(stateQuery);
        }
        final Set<String> appStates = parseQueries(statesQuery, true);
        if (!appStates.isEmpty()) {
            checkAppStates = true;
        }
        final GetApplicationsRequest request = GetApplicationsRequest.newInstance();
        if (checkStart) {
            request.setStartRange(sBegin, sEnd);
        }
        if (checkEnd) {
            request.setFinishRange(fBegin, fEnd);
        }
        if (checkCount) {
            request.setLimit(countNum);
        }
        if (checkAppTypes) {
            request.setApplicationTypes(appTypes);
        }
        if (checkAppTags) {
            request.setApplicationTags(appTags);
        }
        if (checkAppStates) {
            request.setApplicationStates(appStates);
        }
        if (queueQuery != null && !queueQuery.isEmpty()) {
            final ResourceScheduler rs = this.rm.getResourceScheduler();
            if (rs instanceof CapacityScheduler) {
                final CapacityScheduler cs = (CapacityScheduler)rs;
                try {
                    cs.getQueueInfo(queueQuery, false, false);
                }
                catch (IOException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
            final Set<String> queues = new HashSet<String>(1);
            queues.add(queueQuery);
            request.setQueues(queues);
        }
        if (userQuery != null && !userQuery.isEmpty()) {
            final Set<String> users = new HashSet<String>(1);
            users.add(userQuery);
            request.setUsers(users);
        }
        List<ApplicationReport> appReports = null;
        try {
            appReports = this.rm.getClientRMService().getApplications(request, false).getApplicationList();
        }
        catch (YarnException e2) {
            RMWebServices.LOG.error("Unable to retrieve apps from ClientRMService", e2);
            throw new YarnRuntimeException("Unable to retrieve apps from ClientRMService", e2);
        }
        final ConcurrentMap<ApplicationId, RMApp> apps = this.rm.getRMContext().getRMApps();
        final AppsInfo allApps = new AppsInfo();
        for (final ApplicationReport report : appReports) {
            final RMApp rmapp = apps.get(report.getApplicationId());
            if (finalStatusQuery != null && !finalStatusQuery.isEmpty()) {
                FinalApplicationStatus.valueOf(finalStatusQuery);
                if (!rmapp.getFinalApplicationStatus().toString().equalsIgnoreCase(finalStatusQuery)) {
                    continue;
                }
            }
            final AppInfo app = new AppInfo(rmapp, this.hasAccess(rmapp, hsr), WebAppUtils.getHttpSchemePrefix(this.conf));
            allApps.add(app);
        }
        return allApps;
    }
    
    @GET
    @Path("/appstatistics")
    @Produces({ "application/json", "application/xml" })
    public ApplicationStatisticsInfo getAppStatistics(@Context final HttpServletRequest hsr, @QueryParam("states") final Set<String> stateQueries, @QueryParam("applicationTypes") final Set<String> typeQueries) {
        this.init();
        final Set<String> states = parseQueries(stateQueries, true);
        final Set<String> types = parseQueries(typeQueries, false);
        if (types.size() == 0) {
            types.add("*");
        }
        else if (types.size() != 1) {
            throw new BadRequestException("# of applicationTypes = " + types.size() + ", we temporarily support at most one applicationType");
        }
        if (states.size() == 0) {
            for (final YarnApplicationState state : YarnApplicationState.values()) {
                states.add(state.toString().toLowerCase());
            }
        }
        final Map<YarnApplicationState, Map<String, Long>> scoreboard = buildScoreboard(states, types);
        final ConcurrentMap<ApplicationId, RMApp> apps = this.rm.getRMContext().getRMApps();
        for (final RMApp rmapp : apps.values()) {
            final YarnApplicationState state2 = rmapp.createApplicationState();
            final String type = rmapp.getApplicationType().trim().toLowerCase();
            if (states.contains(state2.toString().toLowerCase())) {
                if (types.contains("*")) {
                    countApp(scoreboard, state2, "*");
                }
                else {
                    if (!types.contains(type)) {
                        continue;
                    }
                    countApp(scoreboard, state2, type);
                }
            }
        }
        final ApplicationStatisticsInfo appStatInfo = new ApplicationStatisticsInfo();
        for (final Map.Entry<YarnApplicationState, Map<String, Long>> partScoreboard : scoreboard.entrySet()) {
            for (final Map.Entry<String, Long> statEntry : partScoreboard.getValue().entrySet()) {
                final StatisticsItemInfo statItem = new StatisticsItemInfo(partScoreboard.getKey(), statEntry.getKey(), statEntry.getValue());
                appStatInfo.add(statItem);
            }
        }
        return appStatInfo;
    }
    
    private static Set<String> parseQueries(final Set<String> queries, final boolean isState) {
        final Set<String> params = new HashSet<String>();
        if (!queries.isEmpty()) {
            for (final String query : queries) {
                if (query != null && !query.trim().isEmpty()) {
                    final String[] arr$;
                    final String[] paramStrs = arr$ = query.split(",");
                    for (final String paramStr : arr$) {
                        if (paramStr != null && !paramStr.trim().isEmpty()) {
                            if (isState) {
                                try {
                                    YarnApplicationState.valueOf(paramStr.trim().toUpperCase());
                                }
                                catch (RuntimeException e) {
                                    final YarnApplicationState[] stateArray = YarnApplicationState.values();
                                    final String allAppStates = Arrays.toString(stateArray);
                                    throw new BadRequestException("Invalid application-state " + paramStr.trim() + " specified. It should be one of " + allAppStates);
                                }
                            }
                            params.add(paramStr.trim().toLowerCase());
                        }
                    }
                }
            }
        }
        return params;
    }
    
    private static Map<YarnApplicationState, Map<String, Long>> buildScoreboard(final Set<String> states, final Set<String> types) {
        final Map<YarnApplicationState, Map<String, Long>> scoreboard = new HashMap<YarnApplicationState, Map<String, Long>>();
        assert !states.isEmpty();
        for (final String state : states) {
            final Map<String, Long> partScoreboard = new HashMap<String, Long>();
            scoreboard.put(YarnApplicationState.valueOf(state.toUpperCase()), partScoreboard);
            for (final String type : types) {
                partScoreboard.put(type, 0L);
            }
        }
        return scoreboard;
    }
    
    private static void countApp(final Map<YarnApplicationState, Map<String, Long>> scoreboard, final YarnApplicationState state, final String type) {
        final Map<String, Long> partScoreboard = scoreboard.get(state);
        final Long count = partScoreboard.get(type);
        partScoreboard.put(type, count + 1L);
    }
    
    @GET
    @Path("/apps/{appid}")
    @Produces({ "application/json", "application/xml" })
    public AppInfo getApp(@Context final HttpServletRequest hsr, @PathParam("appid") final String appId) {
        this.init();
        if (appId == null || appId.isEmpty()) {
            throw new NotFoundException("appId, " + appId + ", is empty or null");
        }
        final ApplicationId id = ConverterUtils.toApplicationId(RMWebServices.recordFactory, appId);
        if (id == null) {
            throw new NotFoundException("appId is null");
        }
        final RMApp app = this.rm.getRMContext().getRMApps().get(id);
        if (app == null) {
            throw new NotFoundException("app with id: " + appId + " not found");
        }
        return new AppInfo(app, this.hasAccess(app, hsr), hsr.getScheme() + "://");
    }
    
    @GET
    @Path("/apps/{appid}/appattempts")
    @Produces({ "application/json", "application/xml" })
    public AppAttemptsInfo getAppAttempts(@PathParam("appid") final String appId) {
        this.init();
        if (appId == null || appId.isEmpty()) {
            throw new NotFoundException("appId, " + appId + ", is empty or null");
        }
        final ApplicationId id = ConverterUtils.toApplicationId(RMWebServices.recordFactory, appId);
        if (id == null) {
            throw new NotFoundException("appId is null");
        }
        final RMApp app = this.rm.getRMContext().getRMApps().get(id);
        if (app == null) {
            throw new NotFoundException("app with id: " + appId + " not found");
        }
        final AppAttemptsInfo appAttemptsInfo = new AppAttemptsInfo();
        for (final RMAppAttempt attempt : app.getAppAttempts().values()) {
            final AppAttemptInfo attemptInfo = new AppAttemptInfo(attempt, app.getUser());
            appAttemptsInfo.add(attemptInfo);
        }
        return appAttemptsInfo;
    }
    
    @GET
    @Path("/apps/{appid}/state")
    @Produces({ "application/json", "application/xml" })
    public AppState getAppState(@Context final HttpServletRequest hsr, @PathParam("appid") final String appId) throws AuthorizationException {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        String userName = "";
        if (callerUGI != null) {
            userName = callerUGI.getUserName();
        }
        RMApp app = null;
        try {
            app = this.getRMAppForAppId(appId);
        }
        catch (NotFoundException e) {
            RMAuditLogger.logFailure(userName, "Kill Application Request", "UNKNOWN", "RMWebService", "Trying to get state of an absent application " + appId);
            throw e;
        }
        final AppState ret = new AppState();
        ret.setState(app.getState().toString());
        return ret;
    }
    
    @PUT
    @Path("/apps/{appid}/state")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public Response updateAppState(final AppState targetState, @Context final HttpServletRequest hsr, @PathParam("appid") final String appId) throws AuthorizationException, YarnException, InterruptedException, IOException {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            final String msg = "Unable to obtain user name, user not authenticated";
            throw new AuthorizationException(msg);
        }
        if (UserGroupInformation.isSecurityEnabled() && this.isStaticUser(callerUGI)) {
            final String msg = "The default static user cannot carry out this operation.";
            return Response.status(Response.Status.FORBIDDEN).entity(msg).build();
        }
        final String userName = callerUGI.getUserName();
        RMApp app = null;
        try {
            app = this.getRMAppForAppId(appId);
        }
        catch (NotFoundException e) {
            RMAuditLogger.logFailure(userName, "Kill Application Request", "UNKNOWN", "RMWebService", "Trying to kill/move an absent application " + appId);
            throw e;
        }
        if (app.getState().toString().equals(targetState.getState())) {
            final AppState ret = new AppState();
            ret.setState(app.getState().toString());
            return Response.status(Response.Status.OK).entity(ret).build();
        }
        if (targetState.getState().equals(YarnApplicationState.KILLED.toString())) {
            return this.killApp(app, callerUGI, hsr);
        }
        throw new BadRequestException("Only '" + YarnApplicationState.KILLED.toString() + "' is allowed as a target state.");
    }
    
    @GET
    @Path("/get-node-to-labels")
    @Produces({ "application/json", "application/xml" })
    public NodeToLabelsInfo getNodeToLabels(@Context final HttpServletRequest hsr) throws IOException {
        this.init();
        final NodeToLabelsInfo ntl = new NodeToLabelsInfo();
        final HashMap<String, NodeLabelsInfo> ntlMap = ntl.getNodeToLabels();
        final Map<NodeId, Set<String>> nodeIdToLabels = this.rm.getRMContext().getNodeLabelManager().getNodeLabels();
        for (final Map.Entry<NodeId, Set<String>> nitle : nodeIdToLabels.entrySet()) {
            ntlMap.put(nitle.getKey().toString(), new NodeLabelsInfo(nitle.getValue()));
        }
        return ntl;
    }
    
    @POST
    @Path("/replace-node-to-labels")
    @Produces({ "application/json", "application/xml" })
    public Response replaceLabelsOnNodes(final NodeToLabelsInfo newNodeToLabels, @Context final HttpServletRequest hsr) throws IOException {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            final String msg = "Unable to obtain user name, user not authenticated for post to .../replace-node-to-labels";
            throw new AuthorizationException(msg);
        }
        if (!this.rm.getRMContext().getNodeLabelManager().checkAccess(callerUGI)) {
            final String msg = "User " + callerUGI.getShortUserName() + " not authorized" + " for post to .../replace-node-to-labels ";
            throw new AuthorizationException(msg);
        }
        final Map<NodeId, Set<String>> nodeIdToLabels = new HashMap<NodeId, Set<String>>();
        for (final Map.Entry<String, NodeLabelsInfo> nitle : newNodeToLabels.getNodeToLabels().entrySet()) {
            nodeIdToLabels.put(ConverterUtils.toNodeIdWithDefaultPort(nitle.getKey()), new HashSet<String>(nitle.getValue().getNodeLabels()));
        }
        this.rm.getRMContext().getNodeLabelManager().replaceLabelsOnNode(nodeIdToLabels);
        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("/get-node-labels")
    @Produces({ "application/json", "application/xml" })
    public NodeLabelsInfo getClusterNodeLabels(@Context final HttpServletRequest hsr) throws IOException {
        this.init();
        final NodeLabelsInfo ret = new NodeLabelsInfo(this.rm.getRMContext().getNodeLabelManager().getClusterNodeLabels());
        return ret;
    }
    
    @POST
    @Path("/add-node-labels")
    @Produces({ "application/json", "application/xml" })
    public Response addToClusterNodeLabels(final NodeLabelsInfo newNodeLabels, @Context final HttpServletRequest hsr) throws Exception {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            final String msg = "Unable to obtain user name, user not authenticated for post to .../add-node-labels";
            throw new AuthorizationException(msg);
        }
        if (!this.rm.getRMContext().getNodeLabelManager().checkAccess(callerUGI)) {
            final String msg = "User " + callerUGI.getShortUserName() + " not authorized" + " for post to .../add-node-labels ";
            throw new AuthorizationException(msg);
        }
        this.rm.getRMContext().getNodeLabelManager().addToCluserNodeLabels(new HashSet<String>(newNodeLabels.getNodeLabels()));
        return Response.status(Response.Status.OK).build();
    }
    
    @POST
    @Path("/remove-node-labels")
    @Produces({ "application/json", "application/xml" })
    public Response removeFromCluserNodeLabels(final NodeLabelsInfo oldNodeLabels, @Context final HttpServletRequest hsr) throws Exception {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            final String msg = "Unable to obtain user name, user not authenticated for post to .../remove-node-labels";
            throw new AuthorizationException(msg);
        }
        if (!this.rm.getRMContext().getNodeLabelManager().checkAccess(callerUGI)) {
            final String msg = "User " + callerUGI.getShortUserName() + " not authorized" + " for post to .../remove-node-labels ";
            throw new AuthorizationException(msg);
        }
        this.rm.getRMContext().getNodeLabelManager().removeFromClusterNodeLabels(new HashSet<String>(oldNodeLabels.getNodeLabels()));
        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("/nodes/{nodeId}/get-labels")
    @Produces({ "application/json", "application/xml" })
    public NodeLabelsInfo getLabelsOnNode(@Context final HttpServletRequest hsr, @PathParam("nodeId") final String nodeId) throws IOException {
        this.init();
        final NodeId nid = ConverterUtils.toNodeIdWithDefaultPort(nodeId);
        return new NodeLabelsInfo(this.rm.getRMContext().getNodeLabelManager().getLabelsOnNode(nid));
    }
    
    @POST
    @Path("/nodes/{nodeId}/replace-labels")
    @Produces({ "application/json", "application/xml" })
    public Response replaceLabelsOnNode(final NodeLabelsInfo newNodeLabelsInfo, @Context final HttpServletRequest hsr, @PathParam("nodeId") final String nodeId) throws Exception {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            final String msg = "Unable to obtain user name, user not authenticated for post to .../nodes/nodeid/replace-labels";
            throw new AuthorizationException(msg);
        }
        if (!this.rm.getRMContext().getNodeLabelManager().checkAccess(callerUGI)) {
            final String msg = "User " + callerUGI.getShortUserName() + " not authorized" + " for post to .../nodes/nodeid/replace-labels";
            throw new AuthorizationException(msg);
        }
        final NodeId nid = ConverterUtils.toNodeIdWithDefaultPort(nodeId);
        final Map<NodeId, Set<String>> newLabelsForNode = new HashMap<NodeId, Set<String>>();
        newLabelsForNode.put(nid, new HashSet<String>(newNodeLabelsInfo.getNodeLabels()));
        this.rm.getRMContext().getNodeLabelManager().replaceLabelsOnNode(newLabelsForNode);
        return Response.status(Response.Status.OK).build();
    }
    
    protected Response killApp(final RMApp app, final UserGroupInformation callerUGI, final HttpServletRequest hsr) throws IOException, InterruptedException {
        if (app == null) {
            throw new IllegalArgumentException("app cannot be null");
        }
        final String userName = callerUGI.getUserName();
        final ApplicationId appid = app.getApplicationId();
        KillApplicationResponse resp = null;
        try {
            resp = callerUGI.doAs((PrivilegedExceptionAction<KillApplicationResponse>)new PrivilegedExceptionAction<KillApplicationResponse>() {
                @Override
                public KillApplicationResponse run() throws IOException, YarnException {
                    final KillApplicationRequest req = KillApplicationRequest.newInstance(appid);
                    return RMWebServices.this.rm.getClientRMService().forceKillApplication(req);
                }
            });
        }
        catch (UndeclaredThrowableException ue) {
            if (!(ue.getCause() instanceof YarnException)) {
                throw ue;
            }
            final YarnException ye = (YarnException)ue.getCause();
            if (ye.getCause() instanceof AccessControlException) {
                final String appId = app.getApplicationId().toString();
                final String msg = "Unauthorized attempt to kill appid " + appId + " by remote user " + userName;
                return Response.status(Response.Status.FORBIDDEN).entity(msg).build();
            }
            throw ue;
        }
        final AppState ret = new AppState();
        ret.setState(app.getState().toString());
        if (resp.getIsKillCompleted()) {
            RMAuditLogger.logSuccess(userName, "Kill Application Request", "RMWebService", app.getApplicationId());
            return Response.status(Response.Status.OK).entity(ret).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(ret).header("Location", hsr.getRequestURL()).build();
    }
    
    private RMApp getRMAppForAppId(final String appId) {
        if (appId == null || appId.isEmpty()) {
            throw new NotFoundException("appId, " + appId + ", is empty or null");
        }
        ApplicationId id;
        try {
            id = ConverterUtils.toApplicationId(RMWebServices.recordFactory, appId);
        }
        catch (NumberFormatException e) {
            throw new NotFoundException("appId is invalid");
        }
        if (id == null) {
            throw new NotFoundException("appId is invalid");
        }
        final RMApp app = this.rm.getRMContext().getRMApps().get(id);
        if (app == null) {
            throw new NotFoundException("app with id: " + appId + " not found");
        }
        return app;
    }
    
    private UserGroupInformation getCallerUserGroupInformation(final HttpServletRequest hsr, final boolean usePrincipal) {
        String remoteUser = hsr.getRemoteUser();
        if (usePrincipal) {
            final Principal princ = hsr.getUserPrincipal();
            remoteUser = ((princ == null) ? null : princ.getName());
        }
        UserGroupInformation callerUGI = null;
        if (remoteUser != null) {
            callerUGI = UserGroupInformation.createRemoteUser(remoteUser);
        }
        return callerUGI;
    }
    
    private boolean isStaticUser(final UserGroupInformation callerUGI) {
        final String staticUser = this.conf.get("hadoop.http.staticuser.user", "dr.who");
        return staticUser.equals(callerUGI.getUserName());
    }
    
    @POST
    @Path("/apps/new-application")
    @Produces({ "application/json", "application/xml" })
    public Response createNewApplication(@Context final HttpServletRequest hsr) throws AuthorizationException, IOException, InterruptedException {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            throw new AuthorizationException("Unable to obtain user name, user not authenticated");
        }
        if (UserGroupInformation.isSecurityEnabled() && this.isStaticUser(callerUGI)) {
            final String msg = "The default static user cannot carry out this operation.";
            return Response.status(Response.Status.FORBIDDEN).entity(msg).build();
        }
        final NewApplication appId = this.createNewApplication();
        return Response.status(Response.Status.OK).entity(appId).build();
    }
    
    @POST
    @Path("/apps")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public Response submitApplication(final ApplicationSubmissionContextInfo newApp, @Context final HttpServletRequest hsr) throws AuthorizationException, IOException, InterruptedException {
        this.init();
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            throw new AuthorizationException("Unable to obtain user name, user not authenticated");
        }
        if (UserGroupInformation.isSecurityEnabled() && this.isStaticUser(callerUGI)) {
            final String msg = "The default static user cannot carry out this operation.";
            return Response.status(Response.Status.FORBIDDEN).entity(msg).build();
        }
        final ApplicationSubmissionContext appContext = this.createAppSubmissionContext(newApp);
        final SubmitApplicationRequest req = SubmitApplicationRequest.newInstance(appContext);
        try {
            callerUGI.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<SubmitApplicationResponse>() {
                @Override
                public SubmitApplicationResponse run() throws IOException, YarnException {
                    return RMWebServices.this.rm.getClientRMService().submitApplication(req);
                }
            });
        }
        catch (UndeclaredThrowableException ue) {
            if (ue.getCause() instanceof YarnException) {
                throw new BadRequestException(ue.getCause().getMessage());
            }
            RMWebServices.LOG.info("Submit app request failed", ue);
            throw ue;
        }
        final String url = (Object)hsr.getRequestURL() + "/" + newApp.getApplicationId();
        return Response.status(Response.Status.ACCEPTED).header("Location", url).build();
    }
    
    private NewApplication createNewApplication() {
        final GetNewApplicationRequest req = RMWebServices.recordFactory.newRecordInstance(GetNewApplicationRequest.class);
        GetNewApplicationResponse resp;
        try {
            resp = this.rm.getClientRMService().getNewApplication(req);
        }
        catch (YarnException e) {
            final String msg = "Unable to create new app from RM web service";
            RMWebServices.LOG.error(msg, e);
            throw new YarnRuntimeException(msg, e);
        }
        final NewApplication appId = new NewApplication(resp.getApplicationId().toString(), new ResourceInfo(resp.getMaximumResourceCapability()));
        return appId;
    }
    
    protected ApplicationSubmissionContext createAppSubmissionContext(final ApplicationSubmissionContextInfo newApp) throws IOException {
        final String error = "Could not parse application id " + newApp.getApplicationId();
        ApplicationId appid;
        try {
            appid = ConverterUtils.toApplicationId(RMWebServices.recordFactory, newApp.getApplicationId());
        }
        catch (Exception e) {
            throw new BadRequestException(error);
        }
        final ApplicationSubmissionContext appContext = ApplicationSubmissionContext.newInstance(appid, newApp.getApplicationName(), newApp.getQueue(), Priority.newInstance(newApp.getPriority()), this.createContainerLaunchContext(newApp), newApp.getUnmanagedAM(), newApp.getCancelTokensWhenComplete(), newApp.getMaxAppAttempts(), this.createAppSubmissionContextResource(newApp), newApp.getApplicationType(), newApp.getKeepContainersAcrossApplicationAttempts(), newApp.getAppNodeLabelExpression(), newApp.getAMContainerNodeLabelExpression());
        appContext.setApplicationTags(newApp.getApplicationTags());
        return appContext;
    }
    
    protected Resource createAppSubmissionContextResource(final ApplicationSubmissionContextInfo newApp) throws BadRequestException {
        if (newApp.getResource().getvCores() > this.rm.getConfig().getInt("yarn.scheduler.maximum-allocation-vcores", 4)) {
            final String msg = "Requested more cores than configured max";
            throw new BadRequestException(msg);
        }
        if (newApp.getResource().getMemory() > this.rm.getConfig().getInt("yarn.scheduler.maximum-allocation-mb", 8192)) {
            final String msg = "Requested more memory than configured max";
            throw new BadRequestException(msg);
        }
        final Resource r = Resource.newInstance(newApp.getResource().getMemory(), newApp.getResource().getvCores());
        return r;
    }
    
    protected ContainerLaunchContext createContainerLaunchContext(final ApplicationSubmissionContextInfo newApp) throws BadRequestException, IOException {
        final HashMap<String, ByteBuffer> hmap = new HashMap<String, ByteBuffer>();
        for (final Map.Entry<String, String> entry : newApp.getContainerLaunchContextInfo().getAuxillaryServiceData().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                final Base64 decoder = new Base64(0, null, true);
                final byte[] data = decoder.decode(entry.getValue());
                hmap.put(entry.getKey(), ByteBuffer.wrap(data));
            }
        }
        final HashMap<String, LocalResource> hlr = new HashMap<String, LocalResource>();
        for (final Map.Entry<String, LocalResourceInfo> entry2 : newApp.getContainerLaunchContextInfo().getResources().entrySet()) {
            final LocalResourceInfo l = entry2.getValue();
            final LocalResource lr = LocalResource.newInstance(ConverterUtils.getYarnUrlFromURI(l.getUrl()), l.getType(), l.getVisibility(), l.getSize(), l.getTimestamp());
            hlr.put(entry2.getKey(), lr);
        }
        final DataOutputBuffer out = new DataOutputBuffer();
        final Credentials cs = this.createCredentials(newApp.getContainerLaunchContextInfo().getCredentials());
        cs.writeTokenStorageToStream(out);
        final ByteBuffer tokens = ByteBuffer.wrap(out.getData());
        final ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(hlr, newApp.getContainerLaunchContextInfo().getEnvironment(), newApp.getContainerLaunchContextInfo().getCommands(), hmap, tokens, newApp.getContainerLaunchContextInfo().getAcls());
        return ctx;
    }
    
    private Credentials createCredentials(final CredentialsInfo credentials) {
        final Credentials ret = new Credentials();
        try {
            for (final Map.Entry<String, String> entry : credentials.getTokens().entrySet()) {
                final Text alias = new Text(entry.getKey());
                final Token<TokenIdentifier> token = new Token<TokenIdentifier>();
                token.decodeFromUrlString(entry.getValue());
                ret.addToken(alias, token);
            }
            for (final Map.Entry<String, String> entry : credentials.getSecrets().entrySet()) {
                final Text alias = new Text(entry.getKey());
                final Base64 decoder = new Base64(0, null, true);
                final byte[] secret = decoder.decode(entry.getValue());
                ret.addSecretKey(alias, secret);
            }
        }
        catch (IOException ie) {
            throw new BadRequestException("Could not parse credentials data; exception message = " + ie.getMessage());
        }
        return ret;
    }
    
    private UserGroupInformation createKerberosUserGroupInformation(final HttpServletRequest hsr) throws AuthorizationException, YarnException {
        final UserGroupInformation callerUGI = this.getCallerUserGroupInformation(hsr, true);
        if (callerUGI == null) {
            final String msg = "Unable to obtain user name, user not authenticated";
            throw new AuthorizationException(msg);
        }
        final String authType = hsr.getAuthType();
        if (!"kerberos".equalsIgnoreCase(authType)) {
            final String msg2 = "Delegation token operations can only be carried out on a Kerberos authenticated channel. Expected auth type is kerberos, got type " + authType;
            throw new YarnException(msg2);
        }
        if (hsr.getAttribute("hadoop.security.delegation-token.ugi") != null) {
            final String msg2 = "Delegation token operations cannot be carried out using delegation token authentication.";
            throw new YarnException(msg2);
        }
        callerUGI.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
        return callerUGI;
    }
    
    @POST
    @Path("/delegation-token")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public Response postDelegationToken(final DelegationToken tokenData, @Context final HttpServletRequest hsr) throws AuthorizationException, IOException, InterruptedException, Exception {
        this.init();
        UserGroupInformation callerUGI;
        try {
            callerUGI = this.createKerberosUserGroupInformation(hsr);
        }
        catch (YarnException ye) {
            return Response.status(Response.Status.FORBIDDEN).entity(ye.getMessage()).build();
        }
        return this.createDelegationToken(tokenData, hsr, callerUGI);
    }
    
    @POST
    @Path("/delegation-token/expiration")
    @Produces({ "application/json", "application/xml" })
    @Consumes({ "application/json", "application/xml" })
    public Response postDelegationTokenExpiration(@Context final HttpServletRequest hsr) throws AuthorizationException, IOException, InterruptedException, Exception {
        this.init();
        UserGroupInformation callerUGI;
        try {
            callerUGI = this.createKerberosUserGroupInformation(hsr);
        }
        catch (YarnException ye) {
            return Response.status(Response.Status.FORBIDDEN).entity(ye.getMessage()).build();
        }
        final DelegationToken requestToken = new DelegationToken();
        requestToken.setToken(this.extractToken(hsr).encodeToUrlString());
        return this.renewDelegationToken(requestToken, hsr, callerUGI);
    }
    
    private Response createDelegationToken(final DelegationToken tokenData, final HttpServletRequest hsr, final UserGroupInformation callerUGI) throws AuthorizationException, IOException, InterruptedException, Exception {
        final String renewer = tokenData.getRenewer();
        GetDelegationTokenResponse resp;
        try {
            resp = callerUGI.doAs((PrivilegedExceptionAction<GetDelegationTokenResponse>)new PrivilegedExceptionAction<GetDelegationTokenResponse>() {
                @Override
                public GetDelegationTokenResponse run() throws IOException, YarnException {
                    final GetDelegationTokenRequest createReq = GetDelegationTokenRequest.newInstance(renewer);
                    return RMWebServices.this.rm.getClientRMService().getDelegationToken(createReq);
                }
            });
        }
        catch (Exception e) {
            RMWebServices.LOG.info("Create delegation token request failed", e);
            throw e;
        }
        final Token<RMDelegationTokenIdentifier> tk = new Token<RMDelegationTokenIdentifier>(resp.getRMDelegationToken().getIdentifier().array(), resp.getRMDelegationToken().getPassword().array(), new Text(resp.getRMDelegationToken().getKind()), new Text(resp.getRMDelegationToken().getService()));
        final RMDelegationTokenIdentifier identifier = tk.decodeIdentifier();
        final long currentExpiration = this.rm.getRMContext().getRMDelegationTokenSecretManager().getRenewDate(identifier);
        final DelegationToken respToken = new DelegationToken(tk.encodeToUrlString(), renewer, identifier.getOwner().toString(), tk.getKind().toString(), currentExpiration, identifier.getMaxDate());
        return Response.status(Response.Status.OK).entity(respToken).build();
    }
    
    private Response renewDelegationToken(final DelegationToken tokenData, final HttpServletRequest hsr, final UserGroupInformation callerUGI) throws AuthorizationException, IOException, InterruptedException, Exception {
        final Token<RMDelegationTokenIdentifier> token = this.extractToken(tokenData.getToken());
        final org.apache.hadoop.yarn.api.records.Token dToken = BuilderUtils.newDelegationToken(token.getIdentifier(), token.getKind().toString(), token.getPassword(), token.getService().toString());
        final RenewDelegationTokenRequest req = RenewDelegationTokenRequest.newInstance(dToken);
        RenewDelegationTokenResponse resp;
        try {
            resp = callerUGI.doAs((PrivilegedExceptionAction<RenewDelegationTokenResponse>)new PrivilegedExceptionAction<RenewDelegationTokenResponse>() {
                @Override
                public RenewDelegationTokenResponse run() throws IOException, YarnException {
                    return RMWebServices.this.rm.getClientRMService().renewDelegationToken(req);
                }
            });
        }
        catch (UndeclaredThrowableException ue) {
            if (!(ue.getCause() instanceof YarnException)) {
                RMWebServices.LOG.info("Renew delegation token request failed", ue);
                throw ue;
            }
            if (ue.getCause().getCause() instanceof SecretManager.InvalidToken) {
                throw new BadRequestException(ue.getCause().getCause().getMessage());
            }
            if (ue.getCause().getCause() instanceof org.apache.hadoop.security.AccessControlException) {
                return Response.status(Response.Status.FORBIDDEN).entity(ue.getCause().getCause().getMessage()).build();
            }
            RMWebServices.LOG.info("Renew delegation token request failed", ue);
            throw ue;
        }
        catch (Exception e) {
            RMWebServices.LOG.info("Renew delegation token request failed", e);
            throw e;
        }
        final long renewTime = resp.getNextExpirationTime();
        final DelegationToken respToken = new DelegationToken();
        respToken.setNextExpirationTime(renewTime);
        return Response.status(Response.Status.OK).entity(respToken).build();
    }
    
    @DELETE
    @Path("/delegation-token")
    @Produces({ "application/json", "application/xml" })
    public Response cancelDelegationToken(@Context final HttpServletRequest hsr) throws AuthorizationException, IOException, InterruptedException, Exception {
        this.init();
        UserGroupInformation callerUGI;
        try {
            callerUGI = this.createKerberosUserGroupInformation(hsr);
        }
        catch (YarnException ye) {
            return Response.status(Response.Status.FORBIDDEN).entity(ye.getMessage()).build();
        }
        final Token<RMDelegationTokenIdentifier> token = this.extractToken(hsr);
        final org.apache.hadoop.yarn.api.records.Token dToken = BuilderUtils.newDelegationToken(token.getIdentifier(), token.getKind().toString(), token.getPassword(), token.getService().toString());
        final CancelDelegationTokenRequest req = CancelDelegationTokenRequest.newInstance(dToken);
        try {
            callerUGI.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<CancelDelegationTokenResponse>() {
                @Override
                public CancelDelegationTokenResponse run() throws IOException, YarnException {
                    return RMWebServices.this.rm.getClientRMService().cancelDelegationToken(req);
                }
            });
        }
        catch (UndeclaredThrowableException ue) {
            if (!(ue.getCause() instanceof YarnException)) {
                RMWebServices.LOG.info("Renew delegation token request failed", ue);
                throw ue;
            }
            if (ue.getCause().getCause() instanceof SecretManager.InvalidToken) {
                throw new BadRequestException(ue.getCause().getCause().getMessage());
            }
            if (ue.getCause().getCause() instanceof org.apache.hadoop.security.AccessControlException) {
                return Response.status(Response.Status.FORBIDDEN).entity(ue.getCause().getCause().getMessage()).build();
            }
            RMWebServices.LOG.info("Renew delegation token request failed", ue);
            throw ue;
        }
        catch (Exception e) {
            RMWebServices.LOG.info("Renew delegation token request failed", e);
            throw e;
        }
        return Response.status(Response.Status.OK).build();
    }
    
    private Token<RMDelegationTokenIdentifier> extractToken(final HttpServletRequest request) {
        final String encodedToken = request.getHeader("Hadoop-YARN-RM-Delegation-Token");
        if (encodedToken == null) {
            final String msg = "Header 'Hadoop-YARN-RM-Delegation-Token' containing encoded token not found";
            throw new BadRequestException(msg);
        }
        return this.extractToken(encodedToken);
    }
    
    private Token<RMDelegationTokenIdentifier> extractToken(final String encodedToken) {
        final Token<RMDelegationTokenIdentifier> token = new Token<RMDelegationTokenIdentifier>();
        try {
            token.decodeFromUrlString(encodedToken);
        }
        catch (Exception ie) {
            final String msg = "Could not decode encoded token";
            throw new BadRequestException(msg);
        }
        return token;
    }
    
    static {
        LOG = LogFactory.getLog(RMWebServices.class.getName());
        RMWebServices.recordFactory = RecordFactoryProvider.getRecordFactory(null);
    }
}
