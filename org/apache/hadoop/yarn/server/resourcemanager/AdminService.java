// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeRequest;
import java.util.Collection;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsRequest;
import com.google.common.annotations.VisibleForTesting;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeResourceUpdateEvent;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshServiceAclsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshAdminAclsRequest;
import org.apache.hadoop.security.Groups;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshUserToGroupsMappingsRequest;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshSuperUserGroupsConfigurationRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshNodesRequest;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystem;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RefreshQueuesRequest;
import org.apache.hadoop.ha.HAServiceStatus;
import org.apache.hadoop.ha.ServiceFailedException;
import org.apache.hadoop.ha.HealthCheckFailedException;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;
import com.google.protobuf.BlockingService;
import org.apache.hadoop.ha.proto.HAServiceProtocolProtos;
import org.apache.hadoop.ha.protocolPB.HAServiceProtocolServerSideTranslatorPB;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ha.protocolPB.HAServiceProtocolPB;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.yarn.server.resourcemanager.security.authorize.RMPolicyProvider;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.security.authorize.AccessControlList;
import java.net.InetSocketAddress;
import org.apache.hadoop.ipc.RPC;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocol;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.service.CompositeService;

public class AdminService extends CompositeService implements HAServiceProtocol, ResourceManagerAdministrationProtocol
{
    private static final Log LOG;
    private final RMContext rmContext;
    private final ResourceManager rm;
    private String rmId;
    private boolean autoFailoverEnabled;
    private EmbeddedElectorService embeddedElector;
    private RPC.Server server;
    private InetSocketAddress masterServiceBindAddress;
    private AccessControlList adminAcl;
    private final RecordFactory recordFactory;
    
    public AdminService(final ResourceManager rm, final RMContext rmContext) {
        super(AdminService.class.getName());
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.rm = rm;
        this.rmContext = rmContext;
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        if (this.rmContext.isHAEnabled()) {
            this.autoFailoverEnabled = HAUtil.isAutomaticFailoverEnabled(conf);
            if (this.autoFailoverEnabled && HAUtil.isAutomaticFailoverEmbedded(conf)) {
                this.addIfService(this.embeddedElector = this.createEmbeddedElectorService());
            }
        }
        this.masterServiceBindAddress = conf.getSocketAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.admin.address", "0.0.0.0:8033", 8033);
        this.adminAcl = new AccessControlList(conf.get("yarn.admin.acl", "*"));
        this.rmId = conf.get("yarn.resourcemanager.ha.id");
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.startServer();
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.stopServer();
        super.serviceStop();
    }
    
    protected void startServer() throws Exception {
        final Configuration conf = this.getConfig();
        final YarnRPC rpc = YarnRPC.create(conf);
        this.server = (RPC.Server)rpc.getServer(ResourceManagerAdministrationProtocol.class, this, this.masterServiceBindAddress, conf, null, conf.getInt("yarn.resourcemanager.admin.client.thread-count", 1));
        if (conf.getBoolean("hadoop.security.authorization", false)) {
            this.refreshServiceAcls(this.getConfiguration(conf, "hadoop-policy.xml"), RMPolicyProvider.getInstance());
        }
        if (this.rmContext.isHAEnabled()) {
            RPC.setProtocolEngine(conf, HAServiceProtocolPB.class, ProtobufRpcEngine.class);
            final HAServiceProtocolServerSideTranslatorPB haServiceProtocolXlator = new HAServiceProtocolServerSideTranslatorPB(this);
            final BlockingService haPbService = HAServiceProtocolProtos.HAServiceProtocolService.newReflectiveBlockingService(haServiceProtocolXlator);
            this.server.addProtocol(RPC.RpcKind.RPC_PROTOCOL_BUFFER, HAServiceProtocol.class, haPbService);
        }
        this.server.start();
        conf.updateConnectAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.admin.address", "0.0.0.0:8033", this.server.getListenerAddress());
    }
    
    protected void stopServer() throws Exception {
        if (this.server != null) {
            this.server.stop();
        }
    }
    
    protected EmbeddedElectorService createEmbeddedElectorService() {
        return new EmbeddedElectorService(this.rmContext);
    }
    
    @InterfaceAudience.Private
    void resetLeaderElection() {
        if (this.embeddedElector != null) {
            this.embeddedElector.resetLeaderElection();
        }
    }
    
    private UserGroupInformation checkAccess(final String method) throws IOException {
        return RMServerUtils.verifyAccess(this.adminAcl, method, AdminService.LOG);
    }
    
    private UserGroupInformation checkAcls(final String method) throws YarnException {
        try {
            return this.checkAccess(method);
        }
        catch (IOException ioe) {
            throw RPCUtil.getRemoteException(ioe);
        }
    }
    
    private void checkHaStateChange(final StateChangeRequestInfo req) throws AccessControlException {
        switch (req.getSource()) {
            case REQUEST_BY_USER: {
                if (this.autoFailoverEnabled) {
                    throw new AccessControlException("Manual failover for this ResourceManager is disallowed, because automatic failover is enabled.");
                }
                break;
            }
            case REQUEST_BY_USER_FORCED: {
                if (this.autoFailoverEnabled) {
                    AdminService.LOG.warn("Allowing manual failover from " + Server.getRemoteAddress() + " even though automatic failover is enabled, because the user " + "specified the force flag");
                    break;
                }
                break;
            }
            case REQUEST_BY_ZKFC: {
                if (!this.autoFailoverEnabled) {
                    throw new AccessControlException("Request from ZK failover controller at " + Server.getRemoteAddress() + " denied " + "since automatic failover is not enabled");
                }
                break;
            }
        }
    }
    
    private synchronized boolean isRMActive() {
        return HAServiceState.ACTIVE == this.rmContext.getHAServiceState();
    }
    
    private void throwStandbyException() throws StandbyException {
        throw new StandbyException("ResourceManager " + this.rmId + " is not Active!");
    }
    
    @Override
    public synchronized void monitorHealth() throws IOException {
        this.checkAccess("monitorHealth");
        if (this.isRMActive() && !this.rm.areActiveServicesRunning()) {
            throw new HealthCheckFailedException("Active ResourceManager services are not running!");
        }
    }
    
    @Override
    public synchronized void transitionToActive(final StateChangeRequestInfo reqInfo) throws IOException {
        try {
            this.refreshAdminAcls(false);
        }
        catch (YarnException ex) {
            throw new ServiceFailedException("Can not execute refreshAdminAcls", ex);
        }
        final UserGroupInformation user = this.checkAccess("transitionToActive");
        this.checkHaStateChange(reqInfo);
        try {
            this.rm.transitionToActive();
            this.refreshAll();
            RMAuditLogger.logSuccess(user.getShortUserName(), "transitionToActive", "RMHAProtocolService");
        }
        catch (Exception e) {
            RMAuditLogger.logFailure(user.getShortUserName(), "transitionToActive", this.adminAcl.toString(), "RMHAProtocolService", "Exception transitioning to active");
            throw new ServiceFailedException("Error when transitioning to Active mode", e);
        }
    }
    
    @Override
    public synchronized void transitionToStandby(final StateChangeRequestInfo reqInfo) throws IOException {
        try {
            this.refreshAdminAcls(false);
        }
        catch (YarnException ex) {
            throw new ServiceFailedException("Can not execute refreshAdminAcls", ex);
        }
        final UserGroupInformation user = this.checkAccess("transitionToStandby");
        this.checkHaStateChange(reqInfo);
        try {
            this.rm.transitionToStandby(true);
            RMAuditLogger.logSuccess(user.getShortUserName(), "transitionToStandby", "RMHAProtocolService");
        }
        catch (Exception e) {
            RMAuditLogger.logFailure(user.getShortUserName(), "transitionToStandby", this.adminAcl.toString(), "RMHAProtocolService", "Exception transitioning to standby");
            throw new ServiceFailedException("Error when transitioning to Standby mode", e);
        }
    }
    
    @Override
    public synchronized HAServiceStatus getServiceStatus() throws IOException {
        this.checkAccess("getServiceState");
        final HAServiceState haState = this.rmContext.getHAServiceState();
        final HAServiceStatus ret = new HAServiceStatus(haState);
        if (this.isRMActive() || haState == HAServiceState.STANDBY) {
            ret.setReadyToBecomeActive();
        }
        else {
            ret.setNotReadyToBecomeActive("State is " + haState);
        }
        return ret;
    }
    
    @Override
    public RefreshQueuesResponse refreshQueues(final RefreshQueuesRequest request) throws YarnException, StandbyException {
        final String argName = "refreshQueues";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not refresh queues.");
            this.throwStandbyException();
        }
        final RefreshQueuesResponse response = this.recordFactory.newRecordInstance(RefreshQueuesResponse.class);
        try {
            this.rmContext.getScheduler().reinitialize(this.getConfig(), this.rmContext);
            final ReservationSystem rSystem = this.rmContext.getReservationSystem();
            if (rSystem != null) {
                rSystem.reinitialize(this.getConfig(), this.rmContext);
            }
            RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
            return response;
        }
        catch (IOException ioe) {
            AdminService.LOG.info("Exception refreshing queues ", ioe);
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "Exception refreshing queues");
            throw RPCUtil.getRemoteException(ioe);
        }
    }
    
    @Override
    public RefreshNodesResponse refreshNodes(final RefreshNodesRequest request) throws YarnException, StandbyException {
        final String argName = "refreshNodes";
        final UserGroupInformation user = this.checkAcls("refreshNodes");
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not refresh nodes.");
            this.throwStandbyException();
        }
        try {
            final Configuration conf = this.getConfiguration(new Configuration(false), "yarn-site.xml");
            this.rmContext.getNodesListManager().refreshNodes(conf);
            RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
            return this.recordFactory.newRecordInstance(RefreshNodesResponse.class);
        }
        catch (IOException ioe) {
            AdminService.LOG.info("Exception refreshing nodes ", ioe);
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "Exception refreshing nodes");
            throw RPCUtil.getRemoteException(ioe);
        }
    }
    
    @Override
    public RefreshSuperUserGroupsConfigurationResponse refreshSuperUserGroupsConfiguration(final RefreshSuperUserGroupsConfigurationRequest request) throws YarnException, IOException {
        final String argName = "refreshSuperUserGroupsConfiguration";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not refresh super-user-groups.");
            this.throwStandbyException();
        }
        final Configuration conf = this.getConfiguration(new Configuration(false), "core-site.xml", "yarn-site.xml");
        RMServerUtils.processRMProxyUsersConf(conf);
        ProxyUsers.refreshSuperUserGroupsConfiguration(conf);
        RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
        return this.recordFactory.newRecordInstance(RefreshSuperUserGroupsConfigurationResponse.class);
    }
    
    @Override
    public RefreshUserToGroupsMappingsResponse refreshUserToGroupsMappings(final RefreshUserToGroupsMappingsRequest request) throws YarnException, IOException {
        final String argName = "refreshUserToGroupsMappings";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not refresh user-groups.");
            this.throwStandbyException();
        }
        Groups.getUserToGroupsMappingService(this.getConfiguration(new Configuration(false), "core-site.xml")).refresh();
        RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
        return this.recordFactory.newRecordInstance(RefreshUserToGroupsMappingsResponse.class);
    }
    
    @Override
    public RefreshAdminAclsResponse refreshAdminAcls(final RefreshAdminAclsRequest request) throws YarnException, IOException {
        return this.refreshAdminAcls(true);
    }
    
    private RefreshAdminAclsResponse refreshAdminAcls(final boolean checkRMHAState) throws YarnException, IOException {
        final String argName = "refreshAdminAcls";
        final UserGroupInformation user = this.checkAcls(argName);
        if (checkRMHAState && !this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not refresh user-groups.");
            this.throwStandbyException();
        }
        final Configuration conf = this.getConfiguration(new Configuration(false), "yarn-site.xml");
        this.adminAcl = new AccessControlList(conf.get("yarn.admin.acl", "*"));
        RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
        return this.recordFactory.newRecordInstance(RefreshAdminAclsResponse.class);
    }
    
    @Override
    public RefreshServiceAclsResponse refreshServiceAcls(final RefreshServiceAclsRequest request) throws YarnException, IOException {
        if (!this.getConfig().getBoolean("hadoop.security.authorization", false)) {
            throw RPCUtil.getRemoteException(new IOException("Service Authorization (hadoop.security.authorization) not enabled."));
        }
        final String argName = "refreshServiceAcls";
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(UserGroupInformation.getCurrentUser().getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not refresh Service ACLs.");
            this.throwStandbyException();
        }
        final PolicyProvider policyProvider = RMPolicyProvider.getInstance();
        final Configuration conf = this.getConfiguration(new Configuration(false), "hadoop-policy.xml");
        this.refreshServiceAcls(conf, policyProvider);
        this.rmContext.getClientRMService().refreshServiceAcls(conf, policyProvider);
        this.rmContext.getApplicationMasterService().refreshServiceAcls(conf, policyProvider);
        this.rmContext.getResourceTrackerService().refreshServiceAcls(conf, policyProvider);
        return this.recordFactory.newRecordInstance(RefreshServiceAclsResponse.class);
    }
    
    private synchronized void refreshServiceAcls(final Configuration configuration, final PolicyProvider policyProvider) {
        this.server.refreshServiceAclWithLoadedConfiguration(configuration, policyProvider);
    }
    
    @Override
    public String[] getGroupsForUser(final String user) throws IOException {
        return UserGroupInformation.createRemoteUser(user).getGroupNames();
    }
    
    @Override
    public UpdateNodeResourceResponse updateNodeResource(final UpdateNodeResourceRequest request) throws YarnException, IOException {
        final String argName = "updateNodeResource";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not update node resource.");
            this.throwStandbyException();
        }
        final Map<NodeId, ResourceOption> nodeResourceMap = request.getNodeResourceMap();
        final Set<NodeId> nodeIds = nodeResourceMap.keySet();
        for (final NodeId nodeId : nodeIds) {
            final RMNode node = this.rmContext.getRMNodes().get(nodeId);
            if (node == null) {
                AdminService.LOG.error("Resource update get failed on all nodes due to change resource on an unrecognized node: " + nodeId);
                throw RPCUtil.getRemoteException("Resource update get failed on all nodes due to change resource on an unrecognized node: " + nodeId);
            }
        }
        boolean allSuccess = true;
        for (final Map.Entry<NodeId, ResourceOption> entry : nodeResourceMap.entrySet()) {
            final ResourceOption newResourceOption = entry.getValue();
            final NodeId nodeId2 = entry.getKey();
            final RMNode node2 = this.rmContext.getRMNodes().get(nodeId2);
            if (node2 == null) {
                AdminService.LOG.warn("Resource update get failed on an unrecognized node: " + nodeId2);
                allSuccess = false;
            }
            else {
                this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeResourceUpdateEvent(nodeId2, newResourceOption));
                AdminService.LOG.info("Update resource on node(" + node2.getNodeID() + ") with resource(" + newResourceOption.toString() + ")");
            }
        }
        if (allSuccess) {
            RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
        }
        final UpdateNodeResourceResponse response = UpdateNodeResourceResponse.newInstance();
        return response;
    }
    
    private synchronized Configuration getConfiguration(final Configuration conf, final String... confFileNames) throws YarnException, IOException {
        for (final String confFileName : confFileNames) {
            final InputStream confFileInputStream = this.rmContext.getConfigurationProvider().getConfigurationInputStream(conf, confFileName);
            if (confFileInputStream != null) {
                conf.addResource(confFileInputStream);
            }
        }
        return conf;
    }
    
    private void refreshAll() throws ServiceFailedException {
        try {
            this.refreshQueues(RefreshQueuesRequest.newInstance());
            this.refreshNodes(RefreshNodesRequest.newInstance());
            this.refreshSuperUserGroupsConfiguration(RefreshSuperUserGroupsConfigurationRequest.newInstance());
            this.refreshUserToGroupsMappings(RefreshUserToGroupsMappingsRequest.newInstance());
            if (this.getConfig().getBoolean("hadoop.security.authorization", false)) {
                this.refreshServiceAcls(RefreshServiceAclsRequest.newInstance());
            }
        }
        catch (Exception ex) {
            throw new ServiceFailedException(ex.getMessage());
        }
    }
    
    @VisibleForTesting
    public AccessControlList getAccessControlList() {
        return this.adminAcl;
    }
    
    @VisibleForTesting
    public RPC.Server getServer() {
        return this.server;
    }
    
    @Override
    public AddToClusterNodeLabelsResponse addToClusterNodeLabels(final AddToClusterNodeLabelsRequest request) throws YarnException, IOException {
        final String argName = "addToClusterNodeLabels";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not add labels.");
            this.throwStandbyException();
        }
        final AddToClusterNodeLabelsResponse response = this.recordFactory.newRecordInstance(AddToClusterNodeLabelsResponse.class);
        try {
            this.rmContext.getNodeLabelManager().addToCluserNodeLabels(request.getNodeLabels());
            RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
            return response;
        }
        catch (IOException ioe) {
            AdminService.LOG.info("Exception add labels", ioe);
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "Exception add label");
            throw RPCUtil.getRemoteException(ioe);
        }
    }
    
    @Override
    public RemoveFromClusterNodeLabelsResponse removeFromClusterNodeLabels(final RemoveFromClusterNodeLabelsRequest request) throws YarnException, IOException {
        final String argName = "removeFromClusterNodeLabels";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not remove labels.");
            this.throwStandbyException();
        }
        final RemoveFromClusterNodeLabelsResponse response = this.recordFactory.newRecordInstance(RemoveFromClusterNodeLabelsResponse.class);
        try {
            this.rmContext.getNodeLabelManager().removeFromClusterNodeLabels(request.getNodeLabels());
            RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
            return response;
        }
        catch (IOException ioe) {
            AdminService.LOG.info("Exception remove labels", ioe);
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "Exception remove label");
            throw RPCUtil.getRemoteException(ioe);
        }
    }
    
    @Override
    public ReplaceLabelsOnNodeResponse replaceLabelsOnNode(final ReplaceLabelsOnNodeRequest request) throws YarnException, IOException {
        final String argName = "replaceLabelsOnNode";
        final UserGroupInformation user = this.checkAcls(argName);
        if (!this.isRMActive()) {
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "ResourceManager is not active. Can not set node to labels.");
            this.throwStandbyException();
        }
        final ReplaceLabelsOnNodeResponse response = this.recordFactory.newRecordInstance(ReplaceLabelsOnNodeResponse.class);
        try {
            this.rmContext.getNodeLabelManager().replaceLabelsOnNode(request.getNodeToLabels());
            RMAuditLogger.logSuccess(user.getShortUserName(), argName, "AdminService");
            return response;
        }
        catch (IOException ioe) {
            AdminService.LOG.info("Exception set node to labels. ", ioe);
            RMAuditLogger.logFailure(user.getShortUserName(), argName, this.adminAcl.toString(), "AdminService", "Exception set node to labels.");
            throw RPCUtil.getRemoteException(ioe);
        }
    }
    
    static {
        LOG = LogFactory.getLog(AdminService.class);
    }
}
