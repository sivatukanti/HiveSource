// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.LocalConfigurationProvider;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.NullRMStateStore;
import org.apache.hadoop.yarn.util.SystemClock;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.conf.ConfigurationProvider;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystem;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.DelegationTokenRenewer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.ContainerAllocationExpirer;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AMLivelinessMonitor;
import org.apache.hadoop.ha.HAServiceProtocol;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.event.Dispatcher;

public class RMContextImpl implements RMContext
{
    private Dispatcher rmDispatcher;
    private final ConcurrentMap<ApplicationId, RMApp> applications;
    private final ConcurrentMap<NodeId, RMNode> nodes;
    private final ConcurrentMap<String, RMNode> inactiveNodes;
    private final ConcurrentMap<ApplicationId, ByteBuffer> systemCredentials;
    private boolean isHAEnabled;
    private boolean isWorkPreservingRecoveryEnabled;
    private HAServiceProtocol.HAServiceState haServiceState;
    private AMLivelinessMonitor amLivelinessMonitor;
    private AMLivelinessMonitor amFinishingMonitor;
    private RMStateStore stateStore;
    private ContainerAllocationExpirer containerAllocationExpirer;
    private DelegationTokenRenewer delegationTokenRenewer;
    private AMRMTokenSecretManager amRMTokenSecretManager;
    private RMContainerTokenSecretManager containerTokenSecretManager;
    private NMTokenSecretManagerInRM nmTokenSecretManager;
    private ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager;
    private AdminService adminService;
    private ClientRMService clientRMService;
    private RMDelegationTokenSecretManager rmDelegationTokenSecretManager;
    private ResourceScheduler scheduler;
    private ReservationSystem reservationSystem;
    private NodesListManager nodesListManager;
    private ResourceTrackerService resourceTrackerService;
    private ApplicationMasterService applicationMasterService;
    private RMApplicationHistoryWriter rmApplicationHistoryWriter;
    private SystemMetricsPublisher systemMetricsPublisher;
    private ConfigurationProvider configurationProvider;
    private RMNodeLabelsManager nodeLabelManager;
    private long epoch;
    private Clock systemClock;
    private long schedulerRecoveryStartTime;
    private long schedulerRecoveryWaitTime;
    private boolean printLog;
    private boolean isSchedulerReady;
    private static final Log LOG;
    
    public RMContextImpl() {
        this.applications = new ConcurrentHashMap<ApplicationId, RMApp>();
        this.nodes = new ConcurrentHashMap<NodeId, RMNode>();
        this.inactiveNodes = new ConcurrentHashMap<String, RMNode>();
        this.systemCredentials = new ConcurrentHashMap<ApplicationId, ByteBuffer>();
        this.haServiceState = HAServiceProtocol.HAServiceState.INITIALIZING;
        this.stateStore = null;
        this.systemClock = new SystemClock();
        this.schedulerRecoveryStartTime = 0L;
        this.schedulerRecoveryWaitTime = 0L;
        this.printLog = true;
        this.isSchedulerReady = false;
    }
    
    @VisibleForTesting
    public RMContextImpl(final Dispatcher rmDispatcher, final ContainerAllocationExpirer containerAllocationExpirer, final AMLivelinessMonitor amLivelinessMonitor, final AMLivelinessMonitor amFinishingMonitor, final DelegationTokenRenewer delegationTokenRenewer, final AMRMTokenSecretManager appTokenSecretManager, final RMContainerTokenSecretManager containerTokenSecretManager, final NMTokenSecretManagerInRM nmTokenSecretManager, final ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager, final RMApplicationHistoryWriter rmApplicationHistoryWriter) {
        this();
        this.setDispatcher(rmDispatcher);
        this.setContainerAllocationExpirer(containerAllocationExpirer);
        this.setAMLivelinessMonitor(amLivelinessMonitor);
        this.setAMFinishingMonitor(amFinishingMonitor);
        this.setDelegationTokenRenewer(delegationTokenRenewer);
        this.setAMRMTokenSecretManager(appTokenSecretManager);
        this.setContainerTokenSecretManager(containerTokenSecretManager);
        this.setNMTokenSecretManager(nmTokenSecretManager);
        this.setClientToAMTokenSecretManager(clientToAMTokenSecretManager);
        this.setRMApplicationHistoryWriter(rmApplicationHistoryWriter);
        final RMStateStore nullStore = new NullRMStateStore();
        nullStore.setRMDispatcher(rmDispatcher);
        try {
            nullStore.init(new YarnConfiguration());
            this.setStateStore(nullStore);
        }
        catch (Exception e) {
            assert false;
        }
        final ConfigurationProvider provider = new LocalConfigurationProvider();
        this.setConfigurationProvider(provider);
    }
    
    @Override
    public Dispatcher getDispatcher() {
        return this.rmDispatcher;
    }
    
    @Override
    public RMStateStore getStateStore() {
        return this.stateStore;
    }
    
    @Override
    public ConcurrentMap<ApplicationId, RMApp> getRMApps() {
        return this.applications;
    }
    
    @Override
    public ConcurrentMap<NodeId, RMNode> getRMNodes() {
        return this.nodes;
    }
    
    @Override
    public ConcurrentMap<String, RMNode> getInactiveRMNodes() {
        return this.inactiveNodes;
    }
    
    @Override
    public ContainerAllocationExpirer getContainerAllocationExpirer() {
        return this.containerAllocationExpirer;
    }
    
    @Override
    public AMLivelinessMonitor getAMLivelinessMonitor() {
        return this.amLivelinessMonitor;
    }
    
    @Override
    public AMLivelinessMonitor getAMFinishingMonitor() {
        return this.amFinishingMonitor;
    }
    
    @Override
    public DelegationTokenRenewer getDelegationTokenRenewer() {
        return this.delegationTokenRenewer;
    }
    
    @Override
    public AMRMTokenSecretManager getAMRMTokenSecretManager() {
        return this.amRMTokenSecretManager;
    }
    
    @Override
    public RMContainerTokenSecretManager getContainerTokenSecretManager() {
        return this.containerTokenSecretManager;
    }
    
    @Override
    public NMTokenSecretManagerInRM getNMTokenSecretManager() {
        return this.nmTokenSecretManager;
    }
    
    @Override
    public ResourceScheduler getScheduler() {
        return this.scheduler;
    }
    
    @Override
    public ReservationSystem getReservationSystem() {
        return this.reservationSystem;
    }
    
    @Override
    public NodesListManager getNodesListManager() {
        return this.nodesListManager;
    }
    
    @Override
    public ClientToAMTokenSecretManagerInRM getClientToAMTokenSecretManager() {
        return this.clientToAMTokenSecretManager;
    }
    
    @Override
    public AdminService getRMAdminService() {
        return this.adminService;
    }
    
    @VisibleForTesting
    public void setStateStore(final RMStateStore store) {
        this.stateStore = store;
    }
    
    @Override
    public ClientRMService getClientRMService() {
        return this.clientRMService;
    }
    
    @Override
    public ApplicationMasterService getApplicationMasterService() {
        return this.applicationMasterService;
    }
    
    @Override
    public ResourceTrackerService getResourceTrackerService() {
        return this.resourceTrackerService;
    }
    
    void setHAEnabled(final boolean isHAEnabled) {
        this.isHAEnabled = isHAEnabled;
    }
    
    void setHAServiceState(final HAServiceProtocol.HAServiceState haServiceState) {
        synchronized (haServiceState) {
            this.haServiceState = haServiceState;
        }
    }
    
    void setDispatcher(final Dispatcher dispatcher) {
        this.rmDispatcher = dispatcher;
    }
    
    void setRMAdminService(final AdminService adminService) {
        this.adminService = adminService;
    }
    
    @Override
    public void setClientRMService(final ClientRMService clientRMService) {
        this.clientRMService = clientRMService;
    }
    
    @Override
    public RMDelegationTokenSecretManager getRMDelegationTokenSecretManager() {
        return this.rmDelegationTokenSecretManager;
    }
    
    @Override
    public void setRMDelegationTokenSecretManager(final RMDelegationTokenSecretManager delegationTokenSecretManager) {
        this.rmDelegationTokenSecretManager = delegationTokenSecretManager;
    }
    
    void setContainerAllocationExpirer(final ContainerAllocationExpirer containerAllocationExpirer) {
        this.containerAllocationExpirer = containerAllocationExpirer;
    }
    
    void setAMLivelinessMonitor(final AMLivelinessMonitor amLivelinessMonitor) {
        this.amLivelinessMonitor = amLivelinessMonitor;
    }
    
    void setAMFinishingMonitor(final AMLivelinessMonitor amFinishingMonitor) {
        this.amFinishingMonitor = amFinishingMonitor;
    }
    
    void setContainerTokenSecretManager(final RMContainerTokenSecretManager containerTokenSecretManager) {
        this.containerTokenSecretManager = containerTokenSecretManager;
    }
    
    void setNMTokenSecretManager(final NMTokenSecretManagerInRM nmTokenSecretManager) {
        this.nmTokenSecretManager = nmTokenSecretManager;
    }
    
    void setScheduler(final ResourceScheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    void setReservationSystem(final ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
    }
    
    void setDelegationTokenRenewer(final DelegationTokenRenewer delegationTokenRenewer) {
        this.delegationTokenRenewer = delegationTokenRenewer;
    }
    
    void setClientToAMTokenSecretManager(final ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager) {
        this.clientToAMTokenSecretManager = clientToAMTokenSecretManager;
    }
    
    void setAMRMTokenSecretManager(final AMRMTokenSecretManager amRMTokenSecretManager) {
        this.amRMTokenSecretManager = amRMTokenSecretManager;
    }
    
    void setNodesListManager(final NodesListManager nodesListManager) {
        this.nodesListManager = nodesListManager;
    }
    
    void setApplicationMasterService(final ApplicationMasterService applicationMasterService) {
        this.applicationMasterService = applicationMasterService;
    }
    
    void setResourceTrackerService(final ResourceTrackerService resourceTrackerService) {
        this.resourceTrackerService = resourceTrackerService;
    }
    
    @Override
    public boolean isHAEnabled() {
        return this.isHAEnabled;
    }
    
    @Override
    public HAServiceProtocol.HAServiceState getHAServiceState() {
        synchronized (this.haServiceState) {
            return this.haServiceState;
        }
    }
    
    public void setWorkPreservingRecoveryEnabled(final boolean enabled) {
        this.isWorkPreservingRecoveryEnabled = enabled;
    }
    
    @Override
    public boolean isWorkPreservingRecoveryEnabled() {
        return this.isWorkPreservingRecoveryEnabled;
    }
    
    @Override
    public RMApplicationHistoryWriter getRMApplicationHistoryWriter() {
        return this.rmApplicationHistoryWriter;
    }
    
    @Override
    public void setSystemMetricsPublisher(final SystemMetricsPublisher systemMetricsPublisher) {
        this.systemMetricsPublisher = systemMetricsPublisher;
    }
    
    @Override
    public SystemMetricsPublisher getSystemMetricsPublisher() {
        return this.systemMetricsPublisher;
    }
    
    @Override
    public void setRMApplicationHistoryWriter(final RMApplicationHistoryWriter rmApplicationHistoryWriter) {
        this.rmApplicationHistoryWriter = rmApplicationHistoryWriter;
    }
    
    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return this.configurationProvider;
    }
    
    public void setConfigurationProvider(final ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }
    
    @Override
    public long getEpoch() {
        return this.epoch;
    }
    
    void setEpoch(final long epoch) {
        this.epoch = epoch;
    }
    
    @Override
    public RMNodeLabelsManager getNodeLabelManager() {
        return this.nodeLabelManager;
    }
    
    @Override
    public void setNodeLabelManager(final RMNodeLabelsManager mgr) {
        this.nodeLabelManager = mgr;
    }
    
    public void setSchedulerRecoveryStartAndWaitTime(final long waitTime) {
        this.schedulerRecoveryStartTime = this.systemClock.getTime();
        this.schedulerRecoveryWaitTime = waitTime;
    }
    
    @Override
    public boolean isSchedulerReadyForAllocatingContainers() {
        if (this.isSchedulerReady) {
            return this.isSchedulerReady;
        }
        this.isSchedulerReady = (this.systemClock.getTime() - this.schedulerRecoveryStartTime > this.schedulerRecoveryWaitTime);
        if (!this.isSchedulerReady && this.printLog) {
            RMContextImpl.LOG.info("Skip allocating containers. Scheduler is waiting for recovery.");
            this.printLog = false;
        }
        if (this.isSchedulerReady) {
            RMContextImpl.LOG.info("Scheduler recovery is done. Start allocating new containers.");
        }
        return this.isSchedulerReady;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public void setSystemClock(final Clock clock) {
        this.systemClock = clock;
    }
    
    @Override
    public ConcurrentMap<ApplicationId, ByteBuffer> getSystemCredentialsForApps() {
        return this.systemCredentials;
    }
    
    static {
        LOG = LogFactory.getLog(RMContextImpl.class);
    }
}
