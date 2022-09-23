// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.FinalRequestProcessor;
import org.apache.zookeeper.server.RequestProcessor;
import java.net.Socket;
import javax.security.sasl.SaslException;
import java.net.SocketException;
import java.io.BufferedInputStream;
import org.apache.zookeeper.server.ZooKeeperThread;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import java.util.Collections;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.server.Request;
import java.util.Iterator;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.util.ZxidUtils;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.common.Time;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import org.slf4j.Logger;

public class Leader
{
    private static final Logger LOG;
    private static final boolean nodelay;
    final LeaderZooKeeperServer zk;
    final QuorumPeer self;
    protected boolean quorumFormed;
    LearnerCnxAcceptor cnxAcceptor;
    private final HashSet<LearnerHandler> learners;
    private final HashSet<LearnerHandler> forwardingFollowers;
    private final ProposalStats proposalStats;
    private final HashSet<LearnerHandler> observingLearners;
    private final HashMap<Long, List<LearnerSyncRequest>> pendingSyncs;
    final AtomicLong followerCounter;
    ServerSocket ss;
    static final int DIFF = 13;
    static final int TRUNC = 14;
    static final int SNAP = 15;
    static final int OBSERVERINFO = 16;
    static final int NEWLEADER = 10;
    static final int FOLLOWERINFO = 11;
    static final int UPTODATE = 12;
    public static final int LEADERINFO = 17;
    public static final int ACKEPOCH = 18;
    static final int REQUEST = 1;
    public static final int PROPOSAL = 2;
    static final int ACK = 3;
    static final int COMMIT = 4;
    static final int PING = 5;
    static final int REVALIDATE = 6;
    static final int SYNC = 7;
    static final int INFORM = 8;
    ConcurrentMap<Long, Proposal> outstandingProposals;
    ConcurrentLinkedQueue<Proposal> toBeApplied;
    Proposal newLeaderProposal;
    StateSummary leaderStateSummary;
    long epoch;
    boolean waitingForNewEpoch;
    volatile boolean readyToStart;
    boolean isShutdown;
    long lastCommitted;
    long lastProposed;
    protected Set<Long> connectingFollowers;
    protected Set<Long> electingFollowers;
    protected boolean electionFinished;
    
    public List<LearnerHandler> getLearners() {
        synchronized (this.learners) {
            return new ArrayList<LearnerHandler>(this.learners);
        }
    }
    
    public ProposalStats getProposalStats() {
        return this.proposalStats;
    }
    
    public List<LearnerHandler> getForwardingFollowers() {
        synchronized (this.forwardingFollowers) {
            return new ArrayList<LearnerHandler>(this.forwardingFollowers);
        }
    }
    
    private void addForwardingFollower(final LearnerHandler lh) {
        synchronized (this.forwardingFollowers) {
            this.forwardingFollowers.add(lh);
        }
    }
    
    public List<LearnerHandler> getObservingLearners() {
        synchronized (this.observingLearners) {
            return new ArrayList<LearnerHandler>(this.observingLearners);
        }
    }
    
    private void addObserverLearnerHandler(final LearnerHandler lh) {
        synchronized (this.observingLearners) {
            this.observingLearners.add(lh);
        }
    }
    
    public synchronized int getNumPendingSyncs() {
        return this.pendingSyncs.size();
    }
    
    void addLearnerHandler(final LearnerHandler learner) {
        synchronized (this.learners) {
            this.learners.add(learner);
        }
    }
    
    void removeLearnerHandler(final LearnerHandler peer) {
        synchronized (this.forwardingFollowers) {
            this.forwardingFollowers.remove(peer);
        }
        synchronized (this.learners) {
            this.learners.remove(peer);
        }
        synchronized (this.observingLearners) {
            this.observingLearners.remove(peer);
        }
    }
    
    boolean isLearnerSynced(final LearnerHandler peer) {
        synchronized (this.forwardingFollowers) {
            return this.forwardingFollowers.contains(peer);
        }
    }
    
    Leader(final QuorumPeer self, final LeaderZooKeeperServer zk) throws IOException {
        this.quorumFormed = false;
        this.learners = new HashSet<LearnerHandler>();
        this.forwardingFollowers = new HashSet<LearnerHandler>();
        this.observingLearners = new HashSet<LearnerHandler>();
        this.pendingSyncs = new HashMap<Long, List<LearnerSyncRequest>>();
        this.followerCounter = new AtomicLong(-1L);
        this.outstandingProposals = new ConcurrentHashMap<Long, Proposal>();
        this.toBeApplied = new ConcurrentLinkedQueue<Proposal>();
        this.newLeaderProposal = new Proposal();
        this.epoch = -1L;
        this.waitingForNewEpoch = true;
        this.readyToStart = false;
        this.lastCommitted = -1L;
        this.connectingFollowers = new HashSet<Long>();
        this.electingFollowers = new HashSet<Long>();
        this.electionFinished = false;
        this.self = self;
        this.proposalStats = new ProposalStats();
        try {
            if (self.getQuorumListenOnAllIPs()) {
                this.ss = new ServerSocket(self.getQuorumAddress().getPort());
            }
            else {
                this.ss = new ServerSocket();
            }
            this.ss.setReuseAddress(true);
            if (!self.getQuorumListenOnAllIPs()) {
                this.ss.bind(self.getQuorumAddress());
            }
        }
        catch (BindException e) {
            if (self.getQuorumListenOnAllIPs()) {
                Leader.LOG.error("Couldn't bind to port " + self.getQuorumAddress().getPort(), e);
            }
            else {
                Leader.LOG.error("Couldn't bind to " + self.getQuorumAddress(), e);
            }
            throw e;
        }
        this.zk = zk;
    }
    
    void lead() throws IOException, InterruptedException {
        this.self.end_fle = Time.currentElapsedTime();
        final long electionTimeTaken = this.self.end_fle - this.self.start_fle;
        this.self.setElectionTimeTaken(electionTimeTaken);
        Leader.LOG.info("LEADING - LEADER ELECTION TOOK - {}", (Object)electionTimeTaken);
        this.self.start_fle = 0L;
        this.self.end_fle = 0L;
        this.zk.registerJMX(new LeaderBean(this, this.zk), this.self.jmxLocalPeerBean);
        try {
            this.self.tick.set(0);
            this.zk.loadData();
            this.leaderStateSummary = new StateSummary(this.self.getCurrentEpoch(), this.zk.getLastProcessedZxid());
            (this.cnxAcceptor = new LearnerCnxAcceptor()).start();
            this.readyToStart = true;
            final long epoch = this.getEpochToPropose(this.self.getId(), this.self.getAcceptedEpoch());
            this.zk.setZxid(ZxidUtils.makeZxid(epoch, 0L));
            synchronized (this) {
                this.lastProposed = this.zk.getZxid();
            }
            this.newLeaderProposal.packet = new QuorumPacket(10, this.zk.getZxid(), null, null);
            if ((this.newLeaderProposal.packet.getZxid() & 0xFFFFFFFFL) != 0x0L) {
                Leader.LOG.info("NEWLEADER proposal has Zxid of " + Long.toHexString(this.newLeaderProposal.packet.getZxid()));
            }
            this.waitForEpochAck(this.self.getId(), this.leaderStateSummary);
            this.self.setCurrentEpoch(epoch);
            try {
                this.waitForNewLeaderAck(this.self.getId(), this.zk.getZxid());
            }
            catch (InterruptedException e) {
                this.shutdown("Waiting for a quorum of followers, only synced with sids: [ " + this.getSidSetString(this.newLeaderProposal.ackSet) + " ]");
                final HashSet<Long> followerSet = new HashSet<Long>();
                for (final LearnerHandler f : this.learners) {
                    followerSet.add(f.getSid());
                }
                if (this.self.getQuorumVerifier().containsQuorum(followerSet)) {
                    Leader.LOG.warn("Enough followers present. Perhaps the initTicks need to be increased.");
                }
                Thread.sleep(this.self.tickTime);
                this.self.tick.incrementAndGet();
                return;
            }
            this.startZkServer();
            final String initialZxid = System.getProperty("zookeeper.testingonly.initialZxid");
            if (initialZxid != null) {
                final long zxid = Long.parseLong(initialZxid);
                this.zk.setZxid((this.zk.getZxid() & 0xFFFFFFFF00000000L) | zxid);
            }
            if (!System.getProperty("zookeeper.leaderServes", "yes").equals("no")) {
                this.self.cnxnFactory.setZooKeeperServer(this.zk);
            }
            boolean tickSkip = true;
            while (true) {
                Thread.sleep(this.self.tickTime / 2);
                if (!tickSkip) {
                    this.self.tick.incrementAndGet();
                }
                final HashSet<Long> syncedSet = new HashSet<Long>();
                syncedSet.add(this.self.getId());
                for (final LearnerHandler f2 : this.getLearners()) {
                    if (f2.synced() && f2.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
                        syncedSet.add(f2.getSid());
                    }
                    f2.ping();
                }
                if (!this.isRunning()) {
                    this.shutdown("Unexpected internal error");
                    return;
                }
                if (!tickSkip && !this.self.getQuorumVerifier().containsQuorum(syncedSet)) {
                    this.shutdown("Not sufficient followers synced, only synced with sids: [ " + this.getSidSetString(syncedSet) + " ]");
                    return;
                }
                tickSkip = !tickSkip;
            }
        }
        finally {
            this.zk.unregisterJMX(this);
        }
    }
    
    void shutdown(final String reason) {
        Leader.LOG.info("Shutting down");
        if (this.isShutdown) {
            return;
        }
        Leader.LOG.info("Shutdown called", new Exception("shutdown Leader! reason: " + reason));
        if (this.cnxAcceptor != null) {
            this.cnxAcceptor.halt();
        }
        this.self.cnxnFactory.setZooKeeperServer(null);
        try {
            this.ss.close();
        }
        catch (IOException e) {
            Leader.LOG.warn("Ignoring unexpected exception during close", e);
        }
        this.self.cnxnFactory.closeAll();
        if (this.zk != null) {
            this.zk.shutdown();
        }
        synchronized (this.learners) {
            final Iterator<LearnerHandler> it = this.learners.iterator();
            while (it.hasNext()) {
                final LearnerHandler f = it.next();
                it.remove();
                f.shutdown();
            }
        }
        this.isShutdown = true;
    }
    
    public synchronized void processAck(final long sid, final long zxid, final SocketAddress followerAddr) {
        if (Leader.LOG.isTraceEnabled()) {
            Leader.LOG.trace("Ack zxid: 0x{}", Long.toHexString(zxid));
            for (final Proposal p : this.outstandingProposals.values()) {
                final long packetZxid = p.packet.getZxid();
                Leader.LOG.trace("outstanding proposal: 0x{}", Long.toHexString(packetZxid));
            }
            Leader.LOG.trace("outstanding proposals all");
        }
        if ((zxid & 0xFFFFFFFFL) == 0x0L) {
            return;
        }
        if (this.outstandingProposals.size() == 0) {
            if (Leader.LOG.isDebugEnabled()) {
                Leader.LOG.debug("outstanding is 0");
            }
            return;
        }
        if (this.lastCommitted >= zxid) {
            if (Leader.LOG.isDebugEnabled()) {
                Leader.LOG.debug("proposal has already been committed, pzxid: 0x{} zxid: 0x{}", Long.toHexString(this.lastCommitted), Long.toHexString(zxid));
            }
            return;
        }
        final Proposal p2 = this.outstandingProposals.get(zxid);
        if (p2 == null) {
            Leader.LOG.warn("Trying to commit future proposal: zxid 0x{} from {}", Long.toHexString(zxid), followerAddr);
            return;
        }
        p2.ackSet.add(sid);
        if (Leader.LOG.isDebugEnabled()) {
            Leader.LOG.debug("Count for zxid: 0x{} is {}", Long.toHexString(zxid), p2.ackSet.size());
        }
        if (this.self.getQuorumVerifier().containsQuorum(p2.ackSet)) {
            if (zxid != this.lastCommitted + 1L) {
                Leader.LOG.warn("Commiting zxid 0x{} from {} not first!", Long.toHexString(zxid), followerAddr);
                Leader.LOG.warn("First is 0x{}", Long.toHexString(this.lastCommitted + 1L));
            }
            this.outstandingProposals.remove(zxid);
            if (p2.request != null) {
                this.toBeApplied.add(p2);
            }
            if (p2.request == null) {
                Leader.LOG.warn("Going to commmit null request for proposal: {}", p2);
            }
            this.commit(zxid);
            this.inform(p2);
            this.zk.commitProcessor.commit(p2.request);
            if (this.pendingSyncs.containsKey(zxid)) {
                for (final LearnerSyncRequest r : this.pendingSyncs.remove(zxid)) {
                    this.sendSync(r);
                }
            }
        }
    }
    
    void sendPacket(final QuorumPacket qp) {
        synchronized (this.forwardingFollowers) {
            for (final LearnerHandler f : this.forwardingFollowers) {
                f.queuePacket(qp);
            }
        }
    }
    
    void sendObserverPacket(final QuorumPacket qp) {
        for (final LearnerHandler f : this.getObservingLearners()) {
            f.queuePacket(qp);
        }
    }
    
    public void commit(final long zxid) {
        synchronized (this) {
            this.lastCommitted = zxid;
        }
        final QuorumPacket qp = new QuorumPacket(4, zxid, null, null);
        this.sendPacket(qp);
    }
    
    public void inform(final Proposal proposal) {
        final QuorumPacket qp = new QuorumPacket(8, proposal.request.zxid, proposal.packet.getData(), null);
        this.sendObserverPacket(qp);
    }
    
    public long getEpoch() {
        return ZxidUtils.getEpochFromZxid(this.lastProposed);
    }
    
    public Proposal propose(final Request request) throws XidRolloverException {
        if ((request.zxid & 0xFFFFFFFFL) == 0xFFFFFFFFL) {
            final String msg = "zxid lower 32 bits have rolled over, forcing re-election, and therefore new epoch start";
            this.shutdown(msg);
            throw new XidRolloverException(msg);
        }
        final byte[] data = SerializeUtils.serializeRequest(request);
        this.proposalStats.setLastProposalSize(data.length);
        final QuorumPacket pp = new QuorumPacket(2, request.zxid, data, null);
        final Proposal p = new Proposal();
        p.packet = pp;
        p.request = request;
        synchronized (this) {
            if (Leader.LOG.isDebugEnabled()) {
                Leader.LOG.debug("Proposing:: " + request);
            }
            this.lastProposed = p.packet.getZxid();
            this.outstandingProposals.put(this.lastProposed, p);
            this.sendPacket(pp);
        }
        return p;
    }
    
    public synchronized void processSync(final LearnerSyncRequest r) {
        if (this.outstandingProposals.isEmpty()) {
            this.sendSync(r);
        }
        else {
            List<LearnerSyncRequest> l = this.pendingSyncs.get(this.lastProposed);
            if (l == null) {
                l = new ArrayList<LearnerSyncRequest>();
            }
            l.add(r);
            this.pendingSyncs.put(this.lastProposed, l);
        }
    }
    
    public void sendSync(final LearnerSyncRequest r) {
        final QuorumPacket qp = new QuorumPacket(7, 0L, null, null);
        r.fh.queuePacket(qp);
    }
    
    public synchronized long startForwarding(final LearnerHandler handler, final long lastSeenZxid) {
        if (this.lastProposed > lastSeenZxid) {
            for (final Proposal p : this.toBeApplied) {
                if (p.packet.getZxid() <= lastSeenZxid) {
                    continue;
                }
                handler.queuePacket(p.packet);
                final QuorumPacket qp = new QuorumPacket(4, p.packet.getZxid(), null, null);
                handler.queuePacket(qp);
            }
            if (handler.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
                final List<Long> zxids = new ArrayList<Long>((Collection<? extends Long>)this.outstandingProposals.keySet());
                Collections.sort(zxids);
                for (final Long zxid : zxids) {
                    if (zxid <= lastSeenZxid) {
                        continue;
                    }
                    handler.queuePacket(this.outstandingProposals.get(zxid).packet);
                }
            }
        }
        if (handler.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
            this.addForwardingFollower(handler);
        }
        else {
            this.addObserverLearnerHandler(handler);
        }
        return this.lastProposed;
    }
    
    public long getEpochToPropose(final long sid, final long lastAcceptedEpoch) throws InterruptedException, IOException {
        synchronized (this.connectingFollowers) {
            if (!this.waitingForNewEpoch) {
                return this.epoch;
            }
            if (lastAcceptedEpoch >= this.epoch) {
                this.epoch = lastAcceptedEpoch + 1L;
            }
            if (this.isParticipant(sid)) {
                this.connectingFollowers.add(sid);
            }
            final QuorumVerifier verifier = this.self.getQuorumVerifier();
            if (this.connectingFollowers.contains(this.self.getId()) && verifier.containsQuorum(this.connectingFollowers)) {
                this.waitingForNewEpoch = false;
                this.self.setAcceptedEpoch(this.epoch);
                this.connectingFollowers.notifyAll();
            }
            else {
                long cur;
                for (long start = cur = Time.currentElapsedTime(), end = start + this.self.getInitLimit() * this.self.getTickTime(); this.waitingForNewEpoch && cur < end; cur = Time.currentElapsedTime()) {
                    this.connectingFollowers.wait(end - cur);
                }
                if (this.waitingForNewEpoch) {
                    throw new InterruptedException("Timeout while waiting for epoch from quorum");
                }
            }
            return this.epoch;
        }
    }
    
    public void waitForEpochAck(final long id, final StateSummary ss) throws IOException, InterruptedException {
        synchronized (this.electingFollowers) {
            if (this.electionFinished) {
                return;
            }
            if (ss.getCurrentEpoch() != -1L) {
                if (ss.isMoreRecentThan(this.leaderStateSummary)) {
                    throw new IOException("Follower is ahead of the leader, leader summary: " + this.leaderStateSummary.getCurrentEpoch() + " (current epoch), " + this.leaderStateSummary.getLastZxid() + " (last zxid)");
                }
                if (this.isParticipant(id)) {
                    this.electingFollowers.add(id);
                }
            }
            final QuorumVerifier verifier = this.self.getQuorumVerifier();
            if (this.electingFollowers.contains(this.self.getId()) && verifier.containsQuorum(this.electingFollowers)) {
                this.electionFinished = true;
                this.electingFollowers.notifyAll();
            }
            else {
                long cur;
                for (long start = cur = Time.currentElapsedTime(), end = start + this.self.getInitLimit() * this.self.getTickTime(); !this.electionFinished && cur < end; cur = Time.currentElapsedTime()) {
                    this.electingFollowers.wait(end - cur);
                }
                if (!this.electionFinished) {
                    throw new InterruptedException("Timeout while waiting for epoch to be acked by quorum");
                }
            }
        }
    }
    
    private String getSidSetString(final Set<Long> sidSet) {
        final StringBuilder sids = new StringBuilder();
        final Iterator<Long> iter = sidSet.iterator();
        while (iter.hasNext()) {
            sids.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            sids.append(",");
        }
        return sids.toString();
    }
    
    private synchronized void startZkServer() {
        this.lastCommitted = this.zk.getZxid();
        Leader.LOG.info("Have quorum of supporters, sids: [ " + this.getSidSetString(this.newLeaderProposal.ackSet) + " ]; starting up and setting last processed zxid: 0x{}", Long.toHexString(this.zk.getZxid()));
        this.zk.startup();
        this.self.updateElectionVote(this.getEpoch());
        this.zk.getZKDatabase().setlastProcessedZxid(this.zk.getZxid());
    }
    
    public void waitForNewLeaderAck(final long sid, final long zxid) throws InterruptedException {
        synchronized (this.newLeaderProposal.ackSet) {
            if (this.quorumFormed) {
                return;
            }
            final long currentZxid = this.newLeaderProposal.packet.getZxid();
            if (zxid != currentZxid) {
                Leader.LOG.error("NEWLEADER ACK from sid: " + sid + " is from a different epoch - current 0x" + Long.toHexString(currentZxid) + " receieved 0x" + Long.toHexString(zxid));
                return;
            }
            if (this.isParticipant(sid)) {
                this.newLeaderProposal.ackSet.add(sid);
            }
            if (this.self.getQuorumVerifier().containsQuorum(this.newLeaderProposal.ackSet)) {
                this.quorumFormed = true;
                this.newLeaderProposal.ackSet.notifyAll();
            }
            else {
                long cur;
                for (long start = cur = Time.currentElapsedTime(), end = start + this.self.getInitLimit() * this.self.getTickTime(); !this.quorumFormed && cur < end; cur = Time.currentElapsedTime()) {
                    this.newLeaderProposal.ackSet.wait(end - cur);
                }
                if (!this.quorumFormed) {
                    throw new InterruptedException("Timeout while waiting for NEWLEADER to be acked by quorum");
                }
            }
        }
    }
    
    public static String getPacketType(final int packetType) {
        switch (packetType) {
            case 13: {
                return "DIFF";
            }
            case 14: {
                return "TRUNC";
            }
            case 15: {
                return "SNAP";
            }
            case 16: {
                return "OBSERVERINFO";
            }
            case 10: {
                return "NEWLEADER";
            }
            case 11: {
                return "FOLLOWERINFO";
            }
            case 12: {
                return "UPTODATE";
            }
            case 17: {
                return "LEADERINFO";
            }
            case 18: {
                return "ACKEPOCH";
            }
            case 1: {
                return "REQUEST";
            }
            case 2: {
                return "PROPOSAL";
            }
            case 3: {
                return "ACK";
            }
            case 4: {
                return "COMMIT";
            }
            case 5: {
                return "PING";
            }
            case 6: {
                return "REVALIDATE";
            }
            case 7: {
                return "SYNC";
            }
            case 8: {
                return "INFORM";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    private boolean isRunning() {
        return this.self.isRunning() && this.zk.isRunning();
    }
    
    private boolean isParticipant(final long sid) {
        return this.self.getVotingView().containsKey(sid);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Leader.class);
        nodelay = System.getProperty("leader.nodelay", "true").equals("true");
        Leader.LOG.info("TCP NoDelay set to: " + Leader.nodelay);
    }
    
    public static class Proposal
    {
        public QuorumPacket packet;
        public HashSet<Long> ackSet;
        public Request request;
        
        public Proposal() {
            this.ackSet = new HashSet<Long>();
        }
        
        @Override
        public String toString() {
            return this.packet.getType() + ", " + this.packet.getZxid() + ", " + this.request;
        }
    }
    
    class LearnerCnxAcceptor extends ZooKeeperThread
    {
        private volatile boolean stop;
        
        public LearnerCnxAcceptor() {
            super("LearnerCnxAcceptor-" + Leader.this.ss.getLocalSocketAddress());
            this.stop = false;
        }
        
        @Override
        public void run() {
            try {
                while (!this.stop) {
                    try {
                        final Socket s = Leader.this.ss.accept();
                        s.setSoTimeout(Leader.this.self.tickTime * Leader.this.self.initLimit);
                        s.setTcpNoDelay(Leader.nodelay);
                        final BufferedInputStream is = new BufferedInputStream(s.getInputStream());
                        final LearnerHandler fh = new LearnerHandler(s, is, Leader.this);
                        fh.start();
                    }
                    catch (SocketException e) {
                        if (!this.stop) {
                            throw e;
                        }
                        Leader.LOG.info("exception while shutting down acceptor: " + e);
                        this.stop = true;
                    }
                    catch (SaslException e2) {
                        Leader.LOG.error("Exception while connecting to quorum learner", e2);
                    }
                }
            }
            catch (Exception e3) {
                Leader.LOG.warn("Exception while accepting follower", e3);
            }
        }
        
        public void halt() {
            this.stop = true;
        }
    }
    
    static class ToBeAppliedRequestProcessor implements RequestProcessor
    {
        private RequestProcessor next;
        private ConcurrentLinkedQueue<Proposal> toBeApplied;
        
        ToBeAppliedRequestProcessor(final RequestProcessor next, final ConcurrentLinkedQueue<Proposal> toBeApplied) {
            if (!(next instanceof FinalRequestProcessor)) {
                throw new RuntimeException(ToBeAppliedRequestProcessor.class.getName() + " must be connected to " + FinalRequestProcessor.class.getName() + " not " + next.getClass().getName());
            }
            this.toBeApplied = toBeApplied;
            this.next = next;
        }
        
        @Override
        public void processRequest(final Request request) throws RequestProcessorException {
            this.next.processRequest(request);
            final Proposal p = this.toBeApplied.peek();
            if (p != null && p.request != null && p.request.zxid == request.zxid) {
                this.toBeApplied.remove();
            }
        }
        
        @Override
        public void shutdown() {
            Leader.LOG.info("Shutting down");
            this.next.shutdown();
        }
    }
    
    public static class XidRolloverException extends Exception
    {
        public XidRolloverException(final String message) {
            super(message);
        }
    }
}
