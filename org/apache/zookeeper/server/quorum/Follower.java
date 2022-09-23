// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.jute.Record;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.IOException;
import org.apache.zookeeper.server.util.ZxidUtils;
import org.apache.zookeeper.server.ZooKeeperServerBean;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.common.Time;

public class Follower extends Learner
{
    private long lastQueued;
    final FollowerZooKeeperServer fzk;
    
    Follower(final QuorumPeer self, final FollowerZooKeeperServer zk) {
        this.self = self;
        this.zk = zk;
        this.fzk = zk;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Follower ").append(this.sock);
        sb.append(" lastQueuedZxid:").append(this.lastQueued);
        sb.append(" pendingRevalidationCount:").append(this.pendingRevalidations.size());
        return sb.toString();
    }
    
    void followLeader() throws InterruptedException {
        this.self.end_fle = Time.currentElapsedTime();
        final long electionTimeTaken = this.self.end_fle - this.self.start_fle;
        this.self.setElectionTimeTaken(electionTimeTaken);
        Follower.LOG.info("FOLLOWING - LEADER ELECTION TOOK - {}", (Object)electionTimeTaken);
        this.self.start_fle = 0L;
        this.self.end_fle = 0L;
        this.fzk.registerJMX(new FollowerBean(this, this.zk), this.self.jmxLocalPeerBean);
        try {
            final QuorumPeer.QuorumServer leaderServer = this.findLeader();
            try {
                this.connectToLeader(leaderServer.addr, leaderServer.hostname);
                final long newEpochZxid = this.registerWithLeader(11);
                final long newEpoch = ZxidUtils.getEpochFromZxid(newEpochZxid);
                if (newEpoch < this.self.getAcceptedEpoch()) {
                    Follower.LOG.error("Proposed leader epoch " + ZxidUtils.zxidToString(newEpochZxid) + " is less than our accepted epoch " + ZxidUtils.zxidToString(this.self.getAcceptedEpoch()));
                    throw new IOException("Error: Epoch of leader is lower");
                }
                this.syncWithLeader(newEpochZxid);
                final QuorumPacket qp = new QuorumPacket();
                while (this.isRunning()) {
                    this.readPacket(qp);
                    this.processPacket(qp);
                }
            }
            catch (Exception e) {
                Follower.LOG.warn("Exception when following the leader", e);
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
                final TxnHeader hdr = new TxnHeader();
                final Record txn = SerializeUtils.deserializeTxn(qp.getData(), hdr);
                if (hdr.getZxid() != this.lastQueued + 1L) {
                    Follower.LOG.warn("Got zxid 0x" + Long.toHexString(hdr.getZxid()) + " expected 0x" + Long.toHexString(this.lastQueued + 1L));
                }
                this.lastQueued = hdr.getZxid();
                this.fzk.logRequest(hdr, txn);
                break;
            }
            case 4: {
                this.fzk.commit(qp.getZxid());
                break;
            }
            case 12: {
                Follower.LOG.error("Received an UPTODATE message after Follower started");
                break;
            }
            case 6: {
                this.revalidate(qp);
                break;
            }
            case 7: {
                this.fzk.sync();
                break;
            }
            default: {
                Follower.LOG.error("Invalid packet type: {} received by Observer", (Object)qp.getType());
                break;
            }
        }
    }
    
    public long getZxid() {
        try {
            synchronized (this.fzk) {
                return this.fzk.getZxid();
            }
        }
        catch (NullPointerException e) {
            Follower.LOG.warn("error getting zxid", e);
            return -1L;
        }
    }
    
    protected long getLastQueued() {
        return this.lastQueued;
    }
    
    @Override
    public void shutdown() {
        Follower.LOG.info("shutdown called", new Exception("shutdown Follower"));
        super.shutdown();
    }
}
