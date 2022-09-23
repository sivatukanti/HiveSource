// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.amlauncher;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptLaunchFailedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptImpl;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Map;
import java.io.DataOutputStream;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.net.InetSocketAddress;
import java.security.PrivilegedAction;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.SerializedException;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.commons.logging.Log;

public class AMLauncher implements Runnable
{
    private static final Log LOG;
    private ContainerManagementProtocol containerMgrProxy;
    private final RMAppAttempt application;
    private final Configuration conf;
    private final AMLauncherEventType eventType;
    private final RMContext rmContext;
    private final Container masterContainer;
    private final EventHandler handler;
    
    public AMLauncher(final RMContext rmContext, final RMAppAttempt application, final AMLauncherEventType eventType, final Configuration conf) {
        this.application = application;
        this.conf = conf;
        this.eventType = eventType;
        this.rmContext = rmContext;
        this.handler = rmContext.getDispatcher().getEventHandler();
        this.masterContainer = application.getMasterContainer();
    }
    
    private void connect() throws IOException {
        final ContainerId masterContainerID = this.masterContainer.getId();
        this.containerMgrProxy = this.getContainerMgrProxy(masterContainerID);
    }
    
    private void launch() throws IOException, YarnException {
        this.connect();
        final ContainerId masterContainerID = this.masterContainer.getId();
        final ApplicationSubmissionContext applicationContext = this.application.getSubmissionContext();
        AMLauncher.LOG.info("Setting up container " + this.masterContainer + " for AM " + this.application.getAppAttemptId());
        final ContainerLaunchContext launchContext = this.createAMContainerLaunchContext(applicationContext, masterContainerID);
        final StartContainerRequest scRequest = StartContainerRequest.newInstance(launchContext, this.masterContainer.getContainerToken());
        final List<StartContainerRequest> list = new ArrayList<StartContainerRequest>();
        list.add(scRequest);
        final StartContainersRequest allRequests = StartContainersRequest.newInstance(list);
        final StartContainersResponse response = this.containerMgrProxy.startContainers(allRequests);
        if (response.getFailedRequests() != null && response.getFailedRequests().containsKey(masterContainerID)) {
            final Throwable t = response.getFailedRequests().get(masterContainerID).deSerialize();
            this.parseAndThrowException(t);
        }
        else {
            AMLauncher.LOG.info("Done launching container " + this.masterContainer + " for AM " + this.application.getAppAttemptId());
        }
    }
    
    private void cleanup() throws IOException, YarnException {
        this.connect();
        final ContainerId containerId = this.masterContainer.getId();
        final List<ContainerId> containerIds = new ArrayList<ContainerId>();
        containerIds.add(containerId);
        final StopContainersRequest stopRequest = StopContainersRequest.newInstance(containerIds);
        final StopContainersResponse response = this.containerMgrProxy.stopContainers(stopRequest);
        if (response.getFailedRequests() != null && response.getFailedRequests().containsKey(containerId)) {
            final Throwable t = response.getFailedRequests().get(containerId).deSerialize();
            this.parseAndThrowException(t);
        }
    }
    
    protected ContainerManagementProtocol getContainerMgrProxy(final ContainerId containerId) {
        final NodeId node = this.masterContainer.getNodeId();
        final InetSocketAddress containerManagerBindAddress = NetUtils.createSocketAddrForHost(node.getHost(), node.getPort());
        final YarnRPC rpc = YarnRPC.create(this.conf);
        final UserGroupInformation currentUser = UserGroupInformation.createRemoteUser(containerId.getApplicationAttemptId().toString());
        final String user = this.rmContext.getRMApps().get(containerId.getApplicationAttemptId().getApplicationId()).getUser();
        final Token token = this.rmContext.getNMTokenSecretManager().createNMToken(containerId.getApplicationAttemptId(), node, user);
        currentUser.addToken(ConverterUtils.convertFromYarn(token, containerManagerBindAddress));
        return currentUser.doAs((PrivilegedAction<ContainerManagementProtocol>)new PrivilegedAction<ContainerManagementProtocol>() {
            @Override
            public ContainerManagementProtocol run() {
                return (ContainerManagementProtocol)rpc.getProxy(ContainerManagementProtocol.class, containerManagerBindAddress, AMLauncher.this.conf);
            }
        });
    }
    
    private ContainerLaunchContext createAMContainerLaunchContext(final ApplicationSubmissionContext applicationMasterContext, final ContainerId containerID) throws IOException {
        final ContainerLaunchContext container = applicationMasterContext.getAMContainerSpec();
        AMLauncher.LOG.info("Command to launch container " + containerID + " : " + StringUtils.arrayToString(container.getCommands().toArray(new String[0])));
        this.setupTokens(container, containerID);
        return container;
    }
    
    private void setupTokens(final ContainerLaunchContext container, final ContainerId containerID) throws IOException {
        final Map<String, String> environment = container.getEnvironment();
        environment.put("APPLICATION_WEB_PROXY_BASE", this.application.getWebProxyBase());
        final ApplicationId applicationId = this.application.getAppAttemptId().getApplicationId();
        environment.put("APP_SUBMIT_TIME_ENV", String.valueOf(this.rmContext.getRMApps().get(applicationId).getSubmitTime()));
        environment.put("MAX_APP_ATTEMPTS", String.valueOf(this.rmContext.getRMApps().get(applicationId).getMaxAppAttempts()));
        final Credentials credentials = new Credentials();
        final DataInputByteBuffer dibb = new DataInputByteBuffer();
        if (container.getTokens() != null) {
            dibb.reset(container.getTokens());
            credentials.readTokenStorageStream(dibb);
        }
        final org.apache.hadoop.security.token.Token<AMRMTokenIdentifier> amrmToken = this.createAndSetAMRMToken();
        if (amrmToken != null) {
            credentials.addToken(amrmToken.getService(), amrmToken);
        }
        final DataOutputBuffer dob = new DataOutputBuffer();
        credentials.writeTokenStorageToStream(dob);
        container.setTokens(ByteBuffer.wrap(dob.getData(), 0, dob.getLength()));
    }
    
    @VisibleForTesting
    protected org.apache.hadoop.security.token.Token<AMRMTokenIdentifier> createAndSetAMRMToken() {
        final org.apache.hadoop.security.token.Token<AMRMTokenIdentifier> amrmToken = this.rmContext.getAMRMTokenSecretManager().createAndGetAMRMToken(this.application.getAppAttemptId());
        ((RMAppAttemptImpl)this.application).setAMRMToken(amrmToken);
        return amrmToken;
    }
    
    @Override
    public void run() {
        switch (this.eventType) {
            case LAUNCH: {
                try {
                    AMLauncher.LOG.info("Launching master" + this.application.getAppAttemptId());
                    this.launch();
                    this.handler.handle(new RMAppAttemptEvent(this.application.getAppAttemptId(), RMAppAttemptEventType.LAUNCHED));
                }
                catch (Exception ie) {
                    final String message = "Error launching " + this.application.getAppAttemptId() + ". Got exception: " + StringUtils.stringifyException(ie);
                    AMLauncher.LOG.info(message);
                    this.handler.handle(new RMAppAttemptLaunchFailedEvent(this.application.getAppAttemptId(), message));
                }
                break;
            }
            case CLEANUP: {
                try {
                    AMLauncher.LOG.info("Cleaning master " + this.application.getAppAttemptId());
                    this.cleanup();
                }
                catch (IOException ie2) {
                    AMLauncher.LOG.info("Error cleaning master ", ie2);
                }
                catch (YarnException e) {
                    final StringBuilder sb = new StringBuilder("Container ");
                    sb.append(this.masterContainer.getId().toString());
                    sb.append(" is not handled by this NodeManager");
                    if (!e.getMessage().contains(sb.toString())) {
                        AMLauncher.LOG.info("Error cleaning master ", e);
                    }
                }
                break;
            }
            default: {
                AMLauncher.LOG.warn("Received unknown event-type " + this.eventType + ". Ignoring.");
                break;
            }
        }
    }
    
    private void parseAndThrowException(final Throwable t) throws YarnException, IOException {
        if (t instanceof YarnException) {
            throw (YarnException)t;
        }
        if (t instanceof SecretManager.InvalidToken) {
            throw (SecretManager.InvalidToken)t;
        }
        throw (IOException)t;
    }
    
    static {
        LOG = LogFactory.getLog(AMLauncher.class);
    }
}
