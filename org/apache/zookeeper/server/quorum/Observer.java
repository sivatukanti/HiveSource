// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.jute.Record;
import org.apache.zookeeper.data.Id;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.IOException;
import org.apache.zookeeper.server.ZooKeeperServerBean;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ObserverBean;

public class Observer extends Learner
{
    Observer(final QuorumPeer self, final ObserverZooKeeperServer observerZooKeeperServer) {
        this.self = self;
        this.zk = observerZooKeeperServer;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Observer ").append(this.sock);
        sb.append(" pendingRevalidationCount:").append(this.pendingRevalidations.size());
        return sb.toString();
    }
    
    void observeLeader() throws InterruptedException {
        this.zk.registerJMX(new ObserverBean(this, this.zk), this.self.jmxLocalPeerBean);
        try {
            final QuorumPeer.QuorumServer leaderServer = this.findLeader();
            Observer.LOG.info("Observing " + leaderServer.addr);
            try {
                this.connectToLeader(leaderServer.addr, leaderServer.hostname);
                final long newLeaderZxid = this.registerWithLeader(16);
                this.syncWithLeader(newLeaderZxid);
                final QuorumPacket qp = new QuorumPacket();
                while (this.isRunning()) {
                    this.readPacket(qp);
                    this.processPacket(qp);
                }
            }
            catch (Exception e) {
                Observer.LOG.warn("Exception when observing the leader", e);
                try {
                    this.sock.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
                this.pendingRevalidations.clear();
            }
        }
        finally {
            this.zk.unregisterJMX(this);
        }
    }
    
    protected void processPacket(final QuorumPacket qp) throws IOException {
        switch (qp.getType()) {
            case 5: {
                this.ping(qp);
                break;
            }
            case 2: {
                Observer.LOG.warn("Ignoring proposal");
                break;
            }
            case 4: {
                Observer.LOG.warn("Ignoring commit");
                break;
            }
            case 12: {
                Observer.LOG.error("Received an UPTODATE message after Observer started");
                break;
            }
            case 6: {
                this.revalidate(qp);
                break;
            }
            case 7: {
                ((ObserverZooKeeperServer)this.zk).sync();
                break;
            }
            case 8: {
                final TxnHeader hdr = new TxnHeader();
                final Record txn = SerializeUtils.deserializeTxn(qp.getData(), hdr);
                final Request request = new Request(null, hdr.getClientId(), hdr.getCxid(), hdr.getType(), null, null);
                request.txn = txn;
                request.hdr = hdr;
                final ObserverZooKeeperServer obs = (ObserverZooKeeperServer)this.zk;
                obs.commitRequest(request);
                break;
            }
            default: {
                Observer.LOG.error("Invalid packet type: {} received by Observer", (Object)qp.getType());
                break;
            }
        }
    }
    
    @Override
    public void shutdown() {
        Observer.LOG.info("shutdown called", new Exception("shutdown Observer"));
        super.shutdown();
    }
}
