// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import org.apache.zookeeper.server.util.ZxidUtils;
import java.net.ConnectException;
import org.apache.jute.BinaryOutputArchive;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import java.io.BufferedInputStream;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import org.apache.zookeeper.server.Request;
import org.apache.jute.Record;
import java.io.IOException;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.data.Id;
import java.util.List;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.zookeeper.server.ServerCnxn;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.jute.OutputArchive;
import org.apache.jute.InputArchive;
import java.net.Socket;
import java.io.BufferedOutputStream;

public class Learner
{
    QuorumPeer self;
    LearnerZooKeeperServer zk;
    protected BufferedOutputStream bufferedOutput;
    protected Socket sock;
    protected InputArchive leaderIs;
    protected OutputArchive leaderOs;
    protected int leaderProtocolVersion;
    protected static final Logger LOG;
    private static final boolean nodelay;
    final ConcurrentHashMap<Long, ServerCnxn> pendingRevalidations;
    
    public Learner() {
        this.leaderProtocolVersion = 1;
        this.pendingRevalidations = new ConcurrentHashMap<Long, ServerCnxn>();
    }
    
    public Socket getSocket() {
        return this.sock;
    }
    
    public int getPendingRevalidationsCount() {
        return this.pendingRevalidations.size();
    }
    
    void validateSession(final ServerCnxn cnxn, final long clientId, final int timeout) throws IOException {
        Learner.LOG.info("Revalidating client: 0x" + Long.toHexString(clientId));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(clientId);
        dos.writeInt(timeout);
        dos.close();
        final QuorumPacket qp = new QuorumPacket(6, -1L, baos.toByteArray(), null);
        this.pendingRevalidations.put(clientId, cnxn);
        if (Learner.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(Learner.LOG, 32L, "To validate session 0x" + Long.toHexString(clientId));
        }
        this.writePacket(qp, true);
    }
    
    void writePacket(final QuorumPacket pp, final boolean flush) throws IOException {
        synchronized (this.leaderOs) {
            if (pp != null) {
                this.leaderOs.writeRecord(pp, "packet");
            }
            if (flush) {
                this.bufferedOutput.flush();
            }
        }
    }
    
    void readPacket(final QuorumPacket pp) throws IOException {
        synchronized (this.leaderIs) {
            this.leaderIs.readRecord(pp, "packet");
        }
        long traceMask = 16L;
        if (pp.getType() == 5) {
            traceMask = 128L;
        }
        if (Learner.LOG.isTraceEnabled()) {
            ZooTrace.logQuorumPacket(Learner.LOG, traceMask, 'i', pp);
        }
    }
    
    void request(final Request request) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream oa = new DataOutputStream(baos);
        oa.writeLong(request.sessionId);
        oa.writeInt(request.cxid);
        oa.writeInt(request.type);
        if (request.request != null) {
            request.request.rewind();
            final int len = request.request.remaining();
            final byte[] b = new byte[len];
            request.request.get(b);
            request.request.rewind();
            oa.write(b);
        }
        oa.close();
        final QuorumPacket qp = new QuorumPacket(1, -1L, baos.toByteArray(), request.authInfo);
        this.writePacket(qp, true);
    }
    
    protected QuorumPeer.QuorumServer findLeader() {
        QuorumPeer.QuorumServer leaderServer = null;
        final Vote current = this.self.getCurrentVote();
        for (final QuorumPeer.QuorumServer s : this.self.getView().values()) {
            if (s.id == current.getId()) {
                s.recreateSocketAddresses();
                leaderServer = s;
                break;
            }
        }
        if (leaderServer == null) {
            Learner.LOG.warn("Couldn't find the leader with id = " + current.getId());
        }
        return leaderServer;
    }
    
    protected void connectToLeader(final InetSocketAddress addr, final String hostname) throws IOException, ConnectException, InterruptedException {
        (this.sock = new Socket()).setSoTimeout(this.self.tickTime * this.self.initLimit);
        int tries = 0;
        while (tries < 5) {
            try {
                this.sock.connect(addr, this.self.tickTime * this.self.syncLimit);
                this.sock.setTcpNoDelay(Learner.nodelay);
            }
            catch (IOException e) {
                if (tries == 4) {
                    Learner.LOG.error("Unexpected exception", e);
                    throw e;
                }
                Learner.LOG.warn("Unexpected exception, tries=" + tries + ", connecting to " + addr, e);
                (this.sock = new Socket()).setSoTimeout(this.self.tickTime * this.self.initLimit);
                Thread.sleep(1000L);
                ++tries;
                continue;
            }
            break;
        }
        this.self.authLearner.authenticate(this.sock, hostname);
        this.leaderIs = BinaryInputArchive.getArchive(new BufferedInputStream(this.sock.getInputStream()));
        this.bufferedOutput = new BufferedOutputStream(this.sock.getOutputStream());
        this.leaderOs = BinaryOutputArchive.getArchive(this.bufferedOutput);
    }
    
    protected long registerWithLeader(final int pktType) throws IOException {
        final long lastLoggedZxid = this.self.getLastLoggedZxid();
        final QuorumPacket qp = new QuorumPacket();
        qp.setType(pktType);
        qp.setZxid(ZxidUtils.makeZxid(this.self.getAcceptedEpoch(), 0L));
        final LearnerInfo li = new LearnerInfo(this.self.getId(), 65536);
        final ByteArrayOutputStream bsid = new ByteArrayOutputStream();
        final BinaryOutputArchive boa = BinaryOutputArchive.getArchive(bsid);
        boa.writeRecord(li, "LearnerInfo");
        qp.setData(bsid.toByteArray());
        this.writePacket(qp, true);
        this.readPacket(qp);
        final long newEpoch = ZxidUtils.getEpochFromZxid(qp.getZxid());
        if (qp.getType() == 17) {
            this.leaderProtocolVersion = ByteBuffer.wrap(qp.getData()).getInt();
            final byte[] epochBytes = new byte[4];
            final ByteBuffer wrappedEpochBytes = ByteBuffer.wrap(epochBytes);
            if (newEpoch > this.self.getAcceptedEpoch()) {
                wrappedEpochBytes.putInt((int)this.self.getCurrentEpoch());
                this.self.setAcceptedEpoch(newEpoch);
            }
            else {
                if (newEpoch != this.self.getAcceptedEpoch()) {
                    throw new IOException("Leaders epoch, " + newEpoch + " is less than accepted epoch, " + this.self.getAcceptedEpoch());
                }
                wrappedEpochBytes.putInt(-1);
            }
            final QuorumPacket ackNewEpoch = new QuorumPacket(18, lastLoggedZxid, epochBytes, null);
            this.writePacket(ackNewEpoch, true);
            return ZxidUtils.makeZxid(newEpoch, 0L);
        }
        if (newEpoch > this.self.getAcceptedEpoch()) {
            this.self.setAcceptedEpoch(newEpoch);
        }
        if (qp.getType() != 10) {
            Learner.LOG.error("First packet should have been NEWLEADER");
            throw new IOException("First packet should have been NEWLEADER");
        }
        return qp.getZxid();
    }
    
    protected void syncWithLeader(final long newLeaderZxid) throws IOException, InterruptedException {
        final QuorumPacket ack = new QuorumPacket(3, 0L, null, null);
        final QuorumPacket qp = new QuorumPacket();
        final long newEpoch = ZxidUtils.getEpochFromZxid(newLeaderZxid);
        boolean snapshotNeeded = true;
        this.readPacket(qp);
        final LinkedList<Long> packetsCommitted = new LinkedList<Long>();
        final LinkedList<PacketInFlight> packetsNotCommitted = new LinkedList<PacketInFlight>();
        synchronized (this.zk) {
            if (qp.getType() == 13) {
                Learner.LOG.info("Getting a diff from the leader 0x{}", Long.toHexString(qp.getZxid()));
                snapshotNeeded = false;
            }
            else if (qp.getType() == 15) {
                Learner.LOG.info("Getting a snapshot from leader 0x" + Long.toHexString(qp.getZxid()));
                this.zk.getZKDatabase().clear();
                this.zk.getZKDatabase().deserializeSnapshot(this.leaderIs);
                final String signature = this.leaderIs.readString("signature");
                if (!signature.equals("BenWasHere")) {
                    Learner.LOG.error("Missing signature. Got " + signature);
                    throw new IOException("Missing signature");
                }
                this.zk.getZKDatabase().setlastProcessedZxid(qp.getZxid());
            }
            else if (qp.getType() == 14) {
                Learner.LOG.warn("Truncating log to get in sync with the leader 0x" + Long.toHexString(qp.getZxid()));
                final boolean truncated = this.zk.getZKDatabase().truncateLog(qp.getZxid());
                if (!truncated) {
                    Learner.LOG.error("Not able to truncate the log " + Long.toHexString(qp.getZxid()));
                    System.exit(13);
                }
                this.zk.getZKDatabase().setlastProcessedZxid(qp.getZxid());
            }
            else {
                Learner.LOG.error("Got unexpected packet from leader " + qp.getType() + " exiting ... ");
                System.exit(13);
            }
            this.zk.createSessionTracker();
            long lastQueued = 0L;
            boolean isPreZAB1_0 = true;
            boolean writeToTxnLog = !snapshotNeeded;
        Label_1125:
            while (this.self.isRunning()) {
                this.readPacket(qp);
                switch (qp.getType()) {
                    case 2: {
                        final PacketInFlight pif = new PacketInFlight();
                        pif.hdr = new TxnHeader();
                        pif.rec = SerializeUtils.deserializeTxn(qp.getData(), pif.hdr);
                        if (pif.hdr.getZxid() != lastQueued + 1L) {
                            Learner.LOG.warn("Got zxid 0x" + Long.toHexString(pif.hdr.getZxid()) + " expected 0x" + Long.toHexString(lastQueued + 1L));
                        }
                        lastQueued = pif.hdr.getZxid();
                        packetsNotCommitted.add(pif);
                        continue;
                    }
                    case 4: {
                        if (writeToTxnLog) {
                            packetsCommitted.add(qp.getZxid());
                            continue;
                        }
                        final PacketInFlight pif = packetsNotCommitted.peekFirst();
                        if (pif.hdr.getZxid() != qp.getZxid()) {
                            Learner.LOG.warn("Committing " + qp.getZxid() + ", but next proposal is " + pif.hdr.getZxid());
                            continue;
                        }
                        this.zk.processTxn(pif.hdr, pif.rec);
                        packetsNotCommitted.remove();
                        continue;
                    }
                    case 8: {
                        final PacketInFlight packet = new PacketInFlight();
                        packet.hdr = new TxnHeader();
                        packet.rec = SerializeUtils.deserializeTxn(qp.getData(), packet.hdr);
                        if (packet.hdr.getZxid() != lastQueued + 1L) {
                            Learner.LOG.warn("Got zxid 0x" + Long.toHexString(packet.hdr.getZxid()) + " expected 0x" + Long.toHexString(lastQueued + 1L));
                        }
                        lastQueued = packet.hdr.getZxid();
                        if (!writeToTxnLog) {
                            this.zk.processTxn(packet.hdr, packet.rec);
                            continue;
                        }
                        packetsNotCommitted.add(packet);
                        packetsCommitted.add(qp.getZxid());
                        continue;
                    }
                    case 12: {
                        if (isPreZAB1_0) {
                            this.zk.takeSnapshot();
                            this.self.setCurrentEpoch(newEpoch);
                        }
                        this.self.cnxnFactory.setZooKeeperServer(this.zk);
                        break Label_1125;
                    }
                    case 10: {
                        final File updating = new File(this.self.getTxnFactory().getSnapDir(), "updatingEpoch");
                        if (!updating.exists() && !updating.createNewFile()) {
                            throw new IOException("Failed to create " + updating.toString());
                        }
                        if (snapshotNeeded) {
                            this.zk.takeSnapshot();
                        }
                        this.self.setCurrentEpoch(newEpoch);
                        if (!updating.delete()) {
                            throw new IOException("Failed to delete " + updating.toString());
                        }
                        writeToTxnLog = true;
                        isPreZAB1_0 = false;
                        this.writePacket(new QuorumPacket(3, newLeaderZxid, null, null), true);
                        continue;
                    }
                }
            }
        }
        ack.setZxid(ZxidUtils.makeZxid(newEpoch, 0L));
        this.writePacket(ack, true);
        this.sock.setSoTimeout(this.self.tickTime * this.self.syncLimit);
        this.zk.startup();
        this.self.updateElectionVote(newEpoch);
        if (this.zk instanceof FollowerZooKeeperServer) {
            final FollowerZooKeeperServer fzk = (FollowerZooKeeperServer)this.zk;
            for (final PacketInFlight p : packetsNotCommitted) {
                fzk.logRequest(p.hdr, p.rec);
            }
            for (final Long zxid : packetsCommitted) {
                fzk.commit(zxid);
            }
        }
        else {
            if (!(this.zk instanceof ObserverZooKeeperServer)) {
                throw new UnsupportedOperationException("Unknown server type");
            }
            final ObserverZooKeeperServer ozk = (ObserverZooKeeperServer)this.zk;
            for (final PacketInFlight p : packetsNotCommitted) {
                final Long zxid2 = packetsCommitted.peekFirst();
                if (p.hdr.getZxid() != zxid2) {
                    Learner.LOG.warn("Committing " + Long.toHexString(zxid2) + ", but next proposal is " + Long.toHexString(p.hdr.getZxid()));
                }
                else {
                    packetsCommitted.remove();
                    final Request request = new Request(null, p.hdr.getClientId(), p.hdr.getCxid(), p.hdr.getType(), null, null);
                    request.txn = p.rec;
                    request.hdr = p.hdr;
                    ozk.commitRequest(request);
                }
            }
        }
    }
    
    protected void revalidate(final QuorumPacket qp) throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(qp.getData());
        final DataInputStream dis = new DataInputStream(bis);
        final long sessionId = dis.readLong();
        final boolean valid = dis.readBoolean();
        final ServerCnxn cnxn = this.pendingRevalidations.remove(sessionId);
        if (cnxn == null) {
            Learner.LOG.warn("Missing session 0x" + Long.toHexString(sessionId) + " for validation");
        }
        else {
            this.zk.finishSessionInit(cnxn, valid);
        }
        if (Learner.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(Learner.LOG, 32L, "Session 0x" + Long.toHexString(sessionId) + " is valid: " + valid);
        }
    }
    
    protected void ping(final QuorumPacket qp) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        final HashMap<Long, Integer> touchTable = this.zk.getTouchSnapshot();
        for (final Map.Entry<Long, Integer> entry : touchTable.entrySet()) {
            dos.writeLong(entry.getKey());
            dos.writeInt(entry.getValue());
        }
        qp.setData(bos.toByteArray());
        this.writePacket(qp, true);
    }
    
    public void shutdown() {
        this.self.cnxnFactory.setZooKeeperServer(null);
        this.self.cnxnFactory.closeAll();
        if (this.zk != null) {
            this.zk.shutdown();
        }
    }
    
    boolean isRunning() {
        return this.self.isRunning() && this.zk.isRunning();
    }
    
    static {
        LOG = LoggerFactory.getLogger(Learner.class);
        nodelay = System.getProperty("follower.nodelay", "true").equals("true");
        Learner.LOG.info("TCP NoDelay set to: " + Learner.nodelay);
    }
    
    static class PacketInFlight
    {
        TxnHeader hdr;
        Record rec;
    }
}
