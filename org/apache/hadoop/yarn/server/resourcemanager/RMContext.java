// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystem;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.conf.ConfigurationProvider;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMDelegationTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.ClientToAMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.AMRMTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.DelegationTokenRenewer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.ContainerAllocationExpirer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AMLivelinessMonitor;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.yarn.event.Dispatcher;

public interface RMContext
{
    Dispatcher getDispatcher();
    
    boolean isHAEnabled();
    
    HAServiceProtocol.HAServiceState getHAServiceState();
    
    RMStateStore getStateStore();
    
    ConcurrentMap<ApplicationId, RMApp> getRMApps();
    
    ConcurrentMap<ApplicationId, ByteBuffer> getSystemCredentialsForApps();
    
    ConcurrentMap<String, RMNode> getInactiveRMNodes();
    
    ConcurrentMap<NodeId, RMNode> getRMNodes();
    
    AMLivelinessMonitor getAMLivelinessMonitor();
    
    AMLivelinessMonitor getAMFinishingMonitor();
    
    ContainerAllocationExpirer getContainerAllocationExpirer();
    
    DelegationTokenRenewer getDelegationTokenRenewer();
    
    AMRMTokenSecretManager getAMRMTokenSecretManager();
    
    RMContainerTokenSecretManager getContainerTokenSecretManager();
    
    NMTokenSecretManagerInRM getNMTokenSecretManager();
    
    ResourceScheduler getScheduler();
    
    NodesListManager getNodesListManager();
    
    ClientToAMTokenSecretManagerInRM getClientToAMTokenSecretManager();
    
    AdminService getRMAdminService();
    
    ClientRMService getClientRMService();
    
    ApplicationMasterService getApplicationMasterService();
    
    ResourceTrackerService getResourceTrackerService();
    
    void setClientRMService(final ClientRMService p0);
    
    RMDelegationTokenSecretManager getRMDelegationTokenSecretManager();
    
    void setRMDelegationTokenSecretManager(final RMDelegationTokenSecretManager p0);
    
    RMApplicationHistoryWriter getRMApplicationHistoryWriter();
    
    void setRMApplicationHistoryWriter(final RMApplicationHistoryWriter p0);
    
    void setSystemMetricsPublisher(final SystemMetricsPublisher p0);
    
    SystemMetricsPublisher getSystemMetricsPublisher();
    
    ConfigurationProvider getConfigurationProvider();
    
    boolean isWorkPreservingRecoveryEnabled();
    
    RMNodeLabelsManager getNodeLabelManager();
    
    void setNodeLabelManager(final RMNodeLabelsManager p0);
    
    long getEpoch();
    
    ReservationSystem getReservationSystem();
    
    boolean isSchedulerReadyForAllocatingContainers();
}
