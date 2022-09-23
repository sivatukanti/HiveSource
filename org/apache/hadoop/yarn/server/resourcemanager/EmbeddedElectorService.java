// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.hadoop.util.StringUtils;
import java.io.IOException;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.ha.ServiceFailedException;
import org.apache.hadoop.util.ZKUtil;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ha.ActiveStandbyElector;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class EmbeddedElectorService extends AbstractService implements ActiveStandbyElector.ActiveStandbyElectorCallback
{
    private static final Log LOG;
    private static final HAServiceProtocol.StateChangeRequestInfo req;
    private RMContext rmContext;
    private byte[] localActiveNodeInfo;
    private ActiveStandbyElector elector;
    
    EmbeddedElectorService(final RMContext rmContext) {
        super(EmbeddedElectorService.class.getName());
        this.rmContext = rmContext;
    }
    
    @Override
    protected void serviceInit(Configuration conf) throws Exception {
        conf = ((conf instanceof YarnConfiguration) ? conf : new YarnConfiguration(conf));
        final String zkQuorum = conf.get("yarn.resourcemanager.zk-address");
        if (zkQuorum == null) {
            throw new YarnRuntimeException("Embedded automatic failover is enabled, but yarn.resourcemanager.zk-address is not set");
        }
        final String rmId = HAUtil.getRMHAId(conf);
        final String clusterId = YarnConfiguration.getClusterId(conf);
        this.localActiveNodeInfo = createActiveNodeInfo(clusterId, rmId);
        final String zkBasePath = conf.get("yarn.resourcemanager.ha.automatic-failover.zk-base-path", "/yarn-leader-election");
        final String electionZNode = zkBasePath + "/" + clusterId;
        final long zkSessionTimeout = conf.getLong("yarn.resourcemanager.zk-timeout-ms", 10000L);
        final List<ACL> zkAcls = RMZKUtils.getZKAcls(conf);
        final List<ZKUtil.ZKAuthInfo> zkAuths = RMZKUtils.getZKAuths(conf);
        final int maxRetryNum = conf.getInt("ha.failover-controller.active-standby-elector.zk.op.retries", 3);
        (this.elector = new ActiveStandbyElector(zkQuorum, (int)zkSessionTimeout, electionZNode, zkAcls, zkAuths, this, maxRetryNum)).ensureParentZNode();
        if (!this.isParentZnodeSafe(clusterId)) {
            this.notifyFatalError(electionZNode + " znode has invalid data! " + "Might need formatting!");
        }
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.elector.joinElection(this.localActiveNodeInfo);
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.elector != null) {
            this.elector.quitElection(false);
            this.elector.terminateConnection();
        }
        super.serviceStop();
    }
    
    @Override
    public void becomeActive() throws ServiceFailedException {
        try {
            this.rmContext.getRMAdminService().transitionToActive(EmbeddedElectorService.req);
        }
        catch (Exception e) {
            throw new ServiceFailedException("RM could not transition to Active", e);
        }
    }
    
    @Override
    public void becomeStandby() {
        try {
            this.rmContext.getRMAdminService().transitionToStandby(EmbeddedElectorService.req);
        }
        catch (Exception e) {
            EmbeddedElectorService.LOG.error("RM could not transition to Standby", e);
        }
    }
    
    @Override
    public void enterNeutralMode() {
    }
    
    @Override
    public void notifyFatalError(final String errorMessage) {
        this.rmContext.getDispatcher().getEventHandler().handle(new RMFatalEvent(RMFatalEventType.EMBEDDED_ELECTOR_FAILED, errorMessage));
    }
    
    @Override
    public void fenceOldActive(final byte[] oldActiveData) {
        if (EmbeddedElectorService.LOG.isDebugEnabled()) {
            EmbeddedElectorService.LOG.debug("Request to fence old active being ignored, as embedded leader election doesn't support fencing");
        }
    }
    
    private static byte[] createActiveNodeInfo(final String clusterId, final String rmId) throws IOException {
        return YarnServerResourceManagerServiceProtos.ActiveRMInfoProto.newBuilder().setClusterId(clusterId).setRmId(rmId).build().toByteArray();
    }
    
    private boolean isParentZnodeSafe(final String clusterId) throws InterruptedException, IOException, KeeperException {
        byte[] data;
        try {
            data = this.elector.getActiveData();
        }
        catch (ActiveStandbyElector.ActiveNotFoundException e) {
            return true;
        }
        YarnServerResourceManagerServiceProtos.ActiveRMInfoProto proto;
        try {
            proto = YarnServerResourceManagerServiceProtos.ActiveRMInfoProto.parseFrom(data);
        }
        catch (InvalidProtocolBufferException e2) {
            EmbeddedElectorService.LOG.error("Invalid data in ZK: " + StringUtils.byteToHexString(data));
            return false;
        }
        if (!proto.getClusterId().equals(clusterId)) {
            EmbeddedElectorService.LOG.error("Mismatched cluster! The other RM seems to be from a different cluster. Current cluster = " + clusterId + "Other RM's cluster = " + proto.getClusterId());
            return false;
        }
        return true;
    }
    
    public void resetLeaderElection() {
        this.elector.quitElection(false);
        this.elector.joinElection(this.localActiveNodeInfo);
    }
    
    static {
        LOG = LogFactory.getLog(EmbeddedElectorService.class.getName());
        req = new HAServiceProtocol.StateChangeRequestInfo(HAServiceProtocol.RequestSource.REQUEST_BY_ZKFC);
    }
}
