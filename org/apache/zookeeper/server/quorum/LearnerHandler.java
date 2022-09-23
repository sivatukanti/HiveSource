// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.KeeperException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.data.Id;
import java.util.List;
import org.apache.zookeeper.server.util.ZxidUtils;
import org.apache.zookeeper.server.ByteBufferInputStream;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import org.apache.jute.Record;
import org.apache.zookeeper.server.ZooTrace;
import javax.security.sasl.SaslException;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.BinaryInputArchive;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.Socket;
import org.slf4j.Logger;
import org.apache.zookeeper.server.ZooKeeperThread;

public class LearnerHandler extends ZooKeeperThread
{
    private static final Logger LOG;
    protected final Socket sock;
    final Leader leader;
    volatile long tickOfNextAckDeadline;
    protected long sid;
    protected int version;
    final LinkedBlockingQueue<QuorumPacket> queuedPackets;
    private SyncLimitCheck syncLimitCheck;
    private BinaryInputArchive ia;
    private BinaryOutputArchive oa;
    private final BufferedInputStream bufferedInput;
    private BufferedOutputStream bufferedOutput;
    final QuorumPacket proposalOfDeath;
    private QuorumPeer.LearnerType learnerType;
    
    public Socket getSocket() {
        return this.sock;
    }
    
    long getSid() {
        return this.sid;
    }
    
    int getVersion() {
        return this.version;
    }
    
    LearnerHandler(final Socket sock, final BufferedInputStream bufferedInput, final Leader leader) throws IOException {
        super("LearnerHandler-" + sock.getRemoteSocketAddress());
        this.sid = 0L;
        this.version = 1;
        this.queuedPackets = new LinkedBlockingQueue<QuorumPacket>();
        this.syncLimitCheck = new SyncLimitCheck();
        this.proposalOfDeath = new QuorumPacket();
        this.learnerType = QuorumPeer.LearnerType.PARTICIPANT;
        this.sock = sock;
        this.leader = leader;
        this.bufferedInput = bufferedInput;
        try {
            leader.self.authServer.authenticate(sock, new DataInputStream(bufferedInput));
        }
        catch (IOException e) {
            LearnerHandler.LOG.error("Server failed to authenticate quorum learner, addr: {}, closing connection", sock.getRemoteSocketAddress(), e);
            try {
                sock.close();
            }
            catch (IOException ie) {
                LearnerHandler.LOG.error("Exception while closing socket", ie);
            }
            throw new SaslException("Authentication failure: " + e.getMessage());
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LearnerHandler ").append(this.sock);
        sb.append(" tickOfNextAckDeadline:").append(this.tickOfNextAckDeadline());
        sb.append(" synced?:").append(this.synced());
        sb.append(" queuedPacketLength:").append(this.queuedPackets.size());
        return sb.toString();
    }
    
    public QuorumPeer.LearnerType getLearnerType() {
        return this.learnerType;
    }
    
    private void sendPackets() throws InterruptedException {
        long traceMask = 16L;
        try {
            while (true) {
                QuorumPacket p = this.queuedPackets.poll();
                if (p == null) {
                    this.bufferedOutput.flush();
                    p = this.queuedPackets.take();
                }
                if (p == this.proposalOfDeath) {
                    break;
                }
                if (p.getType() == 5) {
                    traceMask = 128L;
                }
                if (p.getType() == 2) {
                    this.syncLimitCheck.updateProposal(p.getZxid(), System.nanoTime());
                }
                if (LearnerHandler.LOG.isTraceEnabled()) {
                    ZooTrace.logQuorumPacket(LearnerHandler.LOG, traceMask, 'o', p);
                }
                this.oa.writeRecord(p, "packet");
            }
        }
        catch (IOException e) {
            if (!this.sock.isClosed()) {
                LearnerHandler.LOG.warn("Unexpected exception at " + this, e);
                try {
                    this.sock.close();
                }
                catch (IOException ie) {
                    LearnerHandler.LOG.warn("Error closing socket for handler " + this, ie);
                }
            }
        }
    }
    
    public static String packetToString(final QuorumPacket p) {
        String type = null;
        String mess = null;
        final Record txn = null;
        switch (p.getType()) {
            case 3: {
                type = "ACK";
                break;
            }
            case 4: {
                type = "COMMIT";
                break;
            }
            case 11: {
                type = "FOLLOWERINFO";
                break;
            }
            case 10: {
                type = "NEWLEADER";
                break;
            }
            case 5: {
                type = "PING";
                break;
            }
            case 2: {
                type = "PROPOSAL";
                final TxnHeader hdr = new TxnHeader();
                try {
                    SerializeUtils.deserializeTxn(p.getData(), hdr);
                }
                catch (IOException e) {
                    LearnerHandler.LOG.warn("Unexpected exception", e);
                }
                break;
            }
            case 1: {
                type = "REQUEST";
                break;
            }
            case 6: {
                type = "REVALIDATE";
                final ByteArrayInputStream bis = new ByteArrayInputStream(p.getData());
                final DataInputStream dis = new DataInputStream(bis);
                try {
                    final long id = dis.readLong();
                    mess = " sessionid = " + id;
                }
                catch (IOException e2) {
                    LearnerHandler.LOG.warn("Unexpected exception", e2);
                }
                break;
            }
            case 12: {
                type = "UPTODATE";
                break;
            }
            default: {
                type = "UNKNOWN" + p.getType();
                break;
            }
        }
        String entry = null;
        if (type != null) {
            entry = type + " " + Long.toHexString(p.getZxid()) + " " + mess;
        }
        return entry;
    }
    
    @Override
    public void run() {
        try {
            this.leader.addLearnerHandler(this);
            this.tickOfNextAckDeadline = this.leader.self.tick.get() + this.leader.self.initLimit + this.leader.self.syncLimit;
            this.ia = BinaryInputArchive.getArchive(this.bufferedInput);
            this.bufferedOutput = new BufferedOutputStream(this.sock.getOutputStream());
            this.oa = BinaryOutputArchive.getArchive(this.bufferedOutput);
            QuorumPacket qp = new QuorumPacket();
            this.ia.readRecord(qp, "packet");
            if (qp.getType() != 11 && qp.getType() != 16) {
                LearnerHandler.LOG.error("First packet " + qp.toString() + " is not FOLLOWERINFO or OBSERVERINFO!");
                return;
            }
            final byte[] learnerInfoData = qp.getData();
            if (learnerInfoData != null) {
                if (learnerInfoData.length == 8) {
                    final ByteBuffer bbsid = ByteBuffer.wrap(learnerInfoData);
                    this.sid = bbsid.getLong();
                }
                else {
                    final LearnerInfo li = new LearnerInfo();
                    ByteBufferInputStream.byteBuffer2Record(ByteBuffer.wrap(learnerInfoData), li);
                    this.sid = li.getServerid();
                    this.version = li.getProtocolVersion();
                }
            }
            else {
                this.sid = this.leader.followerCounter.getAndDecrement();
            }
            LearnerHandler.LOG.info("Follower sid: " + this.sid + " : info : " + this.leader.self.quorumPeers.get(this.sid));
            if (qp.getType() == 16) {
                this.learnerType = QuorumPeer.LearnerType.OBSERVER;
            }
            final long lastAcceptedEpoch = ZxidUtils.getEpochFromZxid(qp.getZxid());
            StateSummary ss = null;
            final long zxid = qp.getZxid();
            final long newEpoch = this.leader.getEpochToPropose(this.getSid(), lastAcceptedEpoch);
            if (this.getVersion() < 65536) {
                final long epoch = ZxidUtils.getEpochFromZxid(zxid);
                ss = new StateSummary(epoch, zxid);
                this.leader.waitForEpochAck(this.getSid(), ss);
            }
            else {
                final byte[] ver = new byte[4];
                ByteBuffer.wrap(ver).putInt(65536);
                final QuorumPacket newEpochPacket = new QuorumPacket(17, ZxidUtils.makeZxid(newEpoch, 0L), ver, null);
                this.oa.writeRecord(newEpochPacket, "packet");
                this.bufferedOutput.flush();
                final QuorumPacket ackEpochPacket = new QuorumPacket();
                this.ia.readRecord(ackEpochPacket, "packet");
                if (ackEpochPacket.getType() != 18) {
                    LearnerHandler.LOG.error(ackEpochPacket.toString() + " is not ACKEPOCH");
                    return;
                }
                final ByteBuffer bbepoch = ByteBuffer.wrap(ackEpochPacket.getData());
                ss = new StateSummary(bbepoch.getInt(), ackEpochPacket.getZxid());
                this.leader.waitForEpochAck(this.getSid(), ss);
            }
            final long peerLastZxid = ss.getLastZxid();
            int packetToSend = 15;
            long zxidToSend = 0L;
            long leaderLastZxid = 0L;
            long updates = peerLastZxid;
            final ReentrantReadWriteLock lock = this.leader.zk.getZKDatabase().getLogLock();
            final ReentrantReadWriteLock.ReadLock rl = lock.readLock();
            try {
                rl.lock();
                final long maxCommittedLog = this.leader.zk.getZKDatabase().getmaxCommittedLog();
                final long minCommittedLog = this.leader.zk.getZKDatabase().getminCommittedLog();
                LearnerHandler.LOG.info("Synchronizing with Follower sid: " + this.sid + " maxCommittedLog=0x" + Long.toHexString(maxCommittedLog) + " minCommittedLog=0x" + Long.toHexString(minCommittedLog) + " peerLastZxid=0x" + Long.toHexString(peerLastZxid));
                final LinkedList<Leader.Proposal> proposals = this.leader.zk.getZKDatabase().getCommittedLog();
                if (peerLastZxid == this.leader.zk.getZKDatabase().getDataTreeLastProcessedZxid()) {
                    LearnerHandler.LOG.info("leader and follower are in sync, zxid=0x{}", Long.toHexString(peerLastZxid));
                    packetToSend = 13;
                    zxidToSend = peerLastZxid;
                }
                else if (proposals.size() != 0) {
                    LearnerHandler.LOG.debug("proposal size is {}", (Object)proposals.size());
                    if (maxCommittedLog >= peerLastZxid && minCommittedLog <= peerLastZxid) {
                        LearnerHandler.LOG.debug("Sending proposals to follower");
                        long prevProposalZxid = minCommittedLog;
                        boolean firstPacket = true;
                        packetToSend = 13;
                        zxidToSend = maxCommittedLog;
                        for (final Leader.Proposal propose : proposals) {
                            if (propose.packet.getZxid() <= peerLastZxid) {
                                prevProposalZxid = propose.packet.getZxid();
                            }
                            else {
                                if (firstPacket) {
                                    firstPacket = false;
                                    if (prevProposalZxid < peerLastZxid) {
                                        packetToSend = 14;
                                        zxidToSend = (updates = prevProposalZxid);
                                    }
                                }
                                this.queuePacket(propose.packet);
                                final QuorumPacket qcommit = new QuorumPacket(4, propose.packet.getZxid(), null, null);
                                this.queuePacket(qcommit);
                            }
                        }
                    }
                    else if (peerLastZxid > maxCommittedLog) {
                        LearnerHandler.LOG.debug("Sending TRUNC to follower zxidToSend=0x{} updates=0x{}", Long.toHexString(maxCommittedLog), Long.toHexString(updates));
                        packetToSend = 14;
                        zxidToSend = (updates = maxCommittedLog);
                    }
                    else {
                        LearnerHandler.LOG.warn("Unhandled proposal scenario");
                    }
                }
                else {
                    LearnerHandler.LOG.debug("proposals is empty");
                }
                LearnerHandler.LOG.info("Sending " + Leader.getPacketType(packetToSend));
                leaderLastZxid = this.leader.startForwarding(this, updates);
            }
            finally {
                rl.unlock();
            }
            final QuorumPacket newLeaderQP = new QuorumPacket(10, ZxidUtils.makeZxid(newEpoch, 0L), null, null);
            if (this.getVersion() < 65536) {
                this.oa.writeRecord(newLeaderQP, "packet");
            }
            else {
                this.queuedPackets.add(newLeaderQP);
            }
            this.bufferedOutput.flush();
            if (packetToSend == 15) {
                zxidToSend = this.leader.zk.getZKDatabase().getDataTreeLastProcessedZxid();
            }
            this.oa.writeRecord(new QuorumPacket(packetToSend, zxidToSend, null, null), "packet");
            this.bufferedOutput.flush();
            if (packetToSend == 15) {
                LearnerHandler.LOG.info("Sending snapshot last zxid of peer is 0x" + Long.toHexString(peerLastZxid) + "  zxid of leader is 0x" + Long.toHexString(leaderLastZxid) + "sent zxid of db as 0x" + Long.toHexString(zxidToSend));
                this.leader.zk.getZKDatabase().serializeSnapshot(this.oa);
                this.oa.writeString("BenWasHere", "signature");
            }
            this.bufferedOutput.flush();
            new Thread() {
                @Override
                public void run() {
                    Thread.currentThread().setName("Sender-" + LearnerHandler.this.sock.getRemoteSocketAddress());
                    try {
                        LearnerHandler.this.sendPackets();
                    }
                    catch (InterruptedException e) {
                        LearnerHandler.LOG.warn("Unexpected interruption", e);
                    }
                }
            }.start();
            qp = new QuorumPacket();
            this.ia.readRecord(qp, "packet");
            if (qp.getType() != 3) {
                LearnerHandler.LOG.error("Next packet was supposed to be an ACK");
                return;
            }
            LearnerHandler.LOG.info("Received NEWLEADER-ACK message from " + this.getSid());
            this.leader.waitForNewLeaderAck(this.getSid(), qp.getZxid());
            this.syncLimitCheck.start();
            this.sock.setSoTimeout(this.leader.self.tickTime * this.leader.self.syncLimit);
            synchronized (this.leader.zk) {
                while (!this.leader.zk.isRunning() && !this.isInterrupted()) {
                    this.leader.zk.wait(20L);
                }
            }
            this.queuedPackets.add(new QuorumPacket(12, -1L, null, null));
            while (true) {
                qp = new QuorumPacket();
                this.ia.readRecord(qp, "packet");
                long traceMask = 16L;
                if (qp.getType() == 5) {
                    traceMask = 128L;
                }
                if (LearnerHandler.LOG.isTraceEnabled()) {
                    ZooTrace.logQuorumPacket(LearnerHandler.LOG, traceMask, 'i', qp);
                }
                this.tickOfNextAckDeadline = this.leader.self.tick.get() + this.leader.self.syncLimit;
                switch (qp.getType()) {
                    case 3: {
                        if (this.learnerType == QuorumPeer.LearnerType.OBSERVER && LearnerHandler.LOG.isDebugEnabled()) {
                            LearnerHandler.LOG.debug("Received ACK from Observer  " + this.sid);
                        }
                        this.syncLimitCheck.updateAck(qp.getZxid());
                        this.leader.processAck(this.sid, qp.getZxid(), this.sock.getLocalSocketAddress());
                        continue;
                    }
                    case 5: {
                        final ByteArrayInputStream bis = new ByteArrayInputStream(qp.getData());
                        final DataInputStream dis = new DataInputStream(bis);
                        while (dis.available() > 0) {
                            final long sess = dis.readLong();
                            final int to = dis.readInt();
                            this.leader.zk.touch(sess, to);
                        }
                        continue;
                    }
                    case 6: {
                        final ByteArrayInputStream bis = new ByteArrayInputStream(qp.getData());
                        final DataInputStream dis = new DataInputStream(bis);
                        final long id = dis.readLong();
                        final int to = dis.readInt();
                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        final DataOutputStream dos = new DataOutputStream(bos);
                        dos.writeLong(id);
                        final boolean valid = this.leader.zk.touch(id, to);
                        if (valid) {
                            try {
                                this.leader.zk.setOwner(id, this);
                            }
                            catch (KeeperException.SessionExpiredException e) {
                                LearnerHandler.LOG.error("Somehow session " + Long.toHexString(id) + " expired right after being renewed! (impossible)", e);
                            }
                        }
                        if (LearnerHandler.LOG.isTraceEnabled()) {
                            ZooTrace.logTraceMessage(LearnerHandler.LOG, 32L, "Session 0x" + Long.toHexString(id) + " is valid: " + valid);
                        }
                        dos.writeBoolean(valid);
                        qp.setData(bos.toByteArray());
                        this.queuedPackets.add(qp);
                        continue;
                    }
                    case 1: {
                        ByteBuffer bb = ByteBuffer.wrap(qp.getData());
                        final long sessionId = bb.getLong();
                        final int cxid = bb.getInt();
                        final int type = bb.getInt();
                        bb = bb.slice();
                        Request si;
                        if (type == 9) {
                            si = new LearnerSyncRequest(this, sessionId, cxid, type, bb, qp.getAuthinfo());
                        }
                        else {
                            si = new Request(null, sessionId, cxid, type, bb, qp.getAuthinfo());
                        }
                        si.setOwner(this);
                        this.leader.zk.submitRequest(si);
                        continue;
                    }
                    default: {
                        LearnerHandler.LOG.warn("unexpected quorum packet, type: {}", packetToString(qp));
                        continue;
                    }
                }
            }
        }
        catch (IOException e2) {
            if (this.sock != null && !this.sock.isClosed()) {
                LearnerHandler.LOG.error("Unexpected exception causing shutdown while sock still open", e2);
                try {
                    this.sock.close();
                }
                catch (IOException ex) {}
            }
        }
        catch (InterruptedException e3) {
            LearnerHandler.LOG.error("Unexpected exception causing shutdown", e3);
        }
        finally {
            LearnerHandler.LOG.warn("******* GOODBYE " + ((this.sock != null) ? this.sock.getRemoteSocketAddress() : "<null>") + " ********");
            this.shutdown();
        }
    }
    
    public void shutdown() {
        try {
            this.queuedPackets.put(this.proposalOfDeath);
        }
        catch (InterruptedException e) {
            LearnerHandler.LOG.warn("Ignoring unexpected exception", e);
        }
        try {
            if (this.sock != null && !this.sock.isClosed()) {
                this.sock.close();
            }
        }
        catch (IOException e2) {
            LearnerHandler.LOG.warn("Ignoring unexpected exception during socket close", e2);
        }
        this.interrupt();
        this.leader.removeLearnerHandler(this);
    }
    
    public long tickOfNextAckDeadline() {
        return this.tickOfNextAckDeadline;
    }
    
    public void ping() {
        if (this.syncLimitCheck.check(System.nanoTime())) {
            final long id;
            synchronized (this.leader) {
                id = this.leader.lastProposed;
            }
            final QuorumPacket ping = new QuorumPacket(5, id, null, null);
            this.queuePacket(ping);
        }
        else {
            LearnerHandler.LOG.warn("Closing connection to peer due to transaction timeout.");
            this.shutdown();
        }
    }
    
    void queuePacket(final QuorumPacket p) {
        this.queuedPackets.add(p);
    }
    
    public boolean synced() {
        return this.isAlive() && this.leader.self.tick.get() <= this.tickOfNextAckDeadline;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LearnerHandler.class);
    }
    
    private class SyncLimitCheck
    {
        private boolean started;
        private long currentZxid;
        private long currentTime;
        private long nextZxid;
        private long nextTime;
        
        private SyncLimitCheck() {
            this.started = false;
            this.currentZxid = 0L;
            this.currentTime = 0L;
            this.nextZxid = 0L;
            this.nextTime = 0L;
        }
        
        public synchronized void start() {
            this.started = true;
        }
        
        public synchronized void updateProposal(final long zxid, final long time) {
            if (!this.started) {
                return;
            }
            if (this.currentTime == 0L) {
                this.currentTime = time;
                this.currentZxid = zxid;
            }
            else {
                this.nextTime = time;
                this.nextZxid = zxid;
            }
        }
        
        public synchronized void updateAck(final long zxid) {
            if (this.currentZxid == zxid) {
                this.currentTime = this.nextTime;
                this.currentZxid = this.nextZxid;
                this.nextTime = 0L;
                this.nextZxid = 0L;
            }
            else if (this.nextZxid == zxid) {
                LearnerHandler.LOG.warn("ACK for " + zxid + " received before ACK for " + this.currentZxid + "!!!!");
                this.nextTime = 0L;
                this.nextZxid = 0L;
            }
        }
        
        public synchronized boolean check(final long time) {
            if (this.currentTime == 0L) {
                return true;
            }
            final long msDelay = (time - this.currentTime) / 1000000L;
            return msDelay < LearnerHandler.this.leader.self.tickTime * LearnerHandler.this.leader.self.syncLimit;
        }
    }
}
