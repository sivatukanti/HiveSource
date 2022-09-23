// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import org.apache.hadoop.ipc.RPC;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class FailoverController
{
    private static final Logger LOG;
    private final int gracefulFenceTimeout;
    private final int rpcTimeoutToNewActive;
    private final Configuration conf;
    private final Configuration gracefulFenceConf;
    private final HAServiceProtocol.RequestSource requestSource;
    
    public FailoverController(final Configuration conf, final HAServiceProtocol.RequestSource source) {
        this.conf = conf;
        this.gracefulFenceConf = new Configuration(conf);
        this.requestSource = source;
        this.gracefulFenceTimeout = getGracefulFenceTimeout(conf);
        this.rpcTimeoutToNewActive = getRpcTimeoutToNewActive(conf);
        final int gracefulFenceConnectRetries = conf.getInt("ha.failover-controller.graceful-fence.connection.retries", 1);
        this.gracefulFenceConf.setInt("ipc.client.connect.max.retries", gracefulFenceConnectRetries);
        this.gracefulFenceConf.setInt("ipc.client.connect.max.retries.on.timeouts", gracefulFenceConnectRetries);
    }
    
    static int getGracefulFenceTimeout(final Configuration conf) {
        return conf.getInt("ha.failover-controller.graceful-fence.rpc-timeout.ms", 5000);
    }
    
    static int getRpcTimeoutToNewActive(final Configuration conf) {
        return conf.getInt("ha.failover-controller.new-active.rpc-timeout.ms", 60000);
    }
    
    private void preFailoverChecks(final HAServiceTarget from, final HAServiceTarget target, final boolean forceActive) throws FailoverFailedException {
        if (from.getAddress().equals(target.getAddress())) {
            throw new FailoverFailedException("Can't failover a service to itself");
        }
        HAServiceProtocol toSvc;
        HAServiceStatus toSvcStatus;
        try {
            toSvc = target.getProxy(this.conf, this.rpcTimeoutToNewActive);
            toSvcStatus = toSvc.getServiceStatus();
        }
        catch (IOException e) {
            final String msg = "Unable to get service state for " + target;
            FailoverController.LOG.error(msg, e);
            throw new FailoverFailedException(msg, e);
        }
        if (!toSvcStatus.getState().equals(HAServiceProtocol.HAServiceState.STANDBY)) {
            throw new FailoverFailedException("Can't failover to an active service");
        }
        if (!toSvcStatus.isReadyToBecomeActive()) {
            final String notReadyReason = toSvcStatus.getNotReadyReason();
            if (!forceActive) {
                throw new FailoverFailedException(target + " is not ready to become active: " + notReadyReason);
            }
            FailoverController.LOG.warn("Service is not ready to become active, but forcing: {}", notReadyReason);
        }
        try {
            HAServiceProtocolHelper.monitorHealth(toSvc, this.createReqInfo());
        }
        catch (HealthCheckFailedException hce) {
            throw new FailoverFailedException("Can't failover to an unhealthy service", hce);
        }
        catch (IOException e) {
            throw new FailoverFailedException("Got an IO exception", e);
        }
    }
    
    private HAServiceProtocol.StateChangeRequestInfo createReqInfo() {
        return new HAServiceProtocol.StateChangeRequestInfo(this.requestSource);
    }
    
    boolean tryGracefulFence(final HAServiceTarget svc) {
        HAServiceProtocol proxy = null;
        try {
            proxy = svc.getProxy(this.gracefulFenceConf, this.gracefulFenceTimeout);
            proxy.transitionToStandby(this.createReqInfo());
            return true;
        }
        catch (ServiceFailedException sfe) {
            FailoverController.LOG.warn("Unable to gracefully make {} standby ({})", svc, sfe.getMessage());
        }
        catch (IOException ioe) {
            FailoverController.LOG.warn("Unable to gracefully make {} standby (unable to connect)", svc, ioe);
        }
        finally {
            if (proxy != null) {
                RPC.stopProxy(proxy);
            }
        }
        return false;
    }
    
    public void failover(final HAServiceTarget fromSvc, final HAServiceTarget toSvc, final boolean forceFence, final boolean forceActive) throws FailoverFailedException {
        Preconditions.checkArgument(fromSvc.getFencer() != null, (Object)"failover requires a fencer");
        this.preFailoverChecks(fromSvc, toSvc, forceActive);
        boolean tryFence = true;
        if (this.tryGracefulFence(fromSvc)) {
            tryFence = forceFence;
        }
        if (tryFence && !fromSvc.getFencer().fence(fromSvc)) {
            throw new FailoverFailedException("Unable to fence " + fromSvc + ". Fencing failed.");
        }
        boolean failed = false;
        Throwable cause = null;
        try {
            HAServiceProtocolHelper.transitionToActive(toSvc.getProxy(this.conf, this.rpcTimeoutToNewActive), this.createReqInfo());
        }
        catch (ServiceFailedException sfe) {
            FailoverController.LOG.error("Unable to make {} active ({}). Failing back.", toSvc, sfe.getMessage());
            failed = true;
            cause = sfe;
        }
        catch (IOException ioe) {
            FailoverController.LOG.error("Unable to make {} active (unable to connect). Failing back.", toSvc, ioe);
            failed = true;
            cause = ioe;
        }
        if (failed) {
            String msg = "Unable to failover to " + toSvc;
            if (!tryFence) {
                try {
                    this.failover(toSvc, fromSvc, true, true);
                }
                catch (FailoverFailedException ffe) {
                    msg = msg + ". Failback to " + fromSvc + " failed (" + ffe.getMessage() + ")";
                    FailoverController.LOG.error(msg);
                }
            }
            throw new FailoverFailedException(msg, cause);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(FailoverController.class);
    }
}
