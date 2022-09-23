// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.util.ZxidUtils;
import org.apache.zookeeper.server.ZooKeeperThread;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.common.Time;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import java.io.IOException;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;

public class FastLeaderElection implements Election
{
    private static final Logger LOG;
    static final int finalizeWait = 200;
    static final int maxNotificationInterval = 60000;
    QuorumCnxManager manager;
    LinkedBlockingQueue<ToSend> sendqueue;
    LinkedBlockingQueue<Notification> recvqueue;
    QuorumPeer self;
    Messenger messenger;
    AtomicLong logicalclock;
    long proposedLeader;
    long proposedZxid;
    long proposedEpoch;
    volatile boolean stop;
    
    static ByteBuffer buildMsg(final int state, final long leader, final long zxid, final long electionEpoch, final long epoch) {
        final byte[] requestBytes = new byte[40];
        final ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
        requestBuffer.clear();
        requestBuffer.putInt(state);
        requestBuffer.putLong(leader);
        requestBuffer.putLong(zxid);
        requestBuffer.putLong(electionEpoch);
        requestBuffer.putLong(epoch);
        requestBuffer.putInt(1);
        return requestBuffer;
    }
    
    public long getLogicalClock() {
        return this.logicalclock.get();
    }
    
    public FastLeaderElection(final QuorumPeer self, final QuorumCnxManager manager) {
        this.logicalclock = new AtomicLong();
        this.stop = false;
        this.starter(self, this.manager = manager);
    }
    
    private void starter(final QuorumPeer self, final QuorumCnxManager manager) {
        this.self = self;
        this.proposedLeader = -1L;
        this.proposedZxid = -1L;
        this.sendqueue = new LinkedBlockingQueue<ToSend>();
        this.recvqueue = new LinkedBlockingQueue<Notification>();
        this.messenger = new Messenger(manager);
    }
    
    private void leaveInstance(final Vote v) {
        if (FastLeaderElection.LOG.isDebugEnabled()) {
            FastLeaderElection.LOG.debug("About to leave FLE instance: leader=" + v.getId() + ", zxid=0x" + Long.toHexString(v.getZxid()) + ", my id=" + this.self.getId() + ", my state=" + this.self.getPeerState());
        }
        this.recvqueue.clear();
    }
    
    public QuorumCnxManager getCnxManager() {
        return this.manager;
    }
    
    @Override
    public void shutdown() {
        this.stop = true;
        FastLeaderElection.LOG.debug("Shutting down connection manager");
        this.manager.halt();
        FastLeaderElection.LOG.debug("Shutting down messenger");
        this.messenger.halt();
        FastLeaderElection.LOG.debug("FLE is down");
    }
    
    private void sendNotifications() {
        for (final QuorumPeer.QuorumServer server : this.self.getVotingView().values()) {
            final long sid = server.id;
            final ToSend notmsg = new ToSend(ToSend.mType.notification, this.proposedLeader, this.proposedZxid, this.logicalclock.get(), QuorumPeer.ServerState.LOOKING, sid, this.proposedEpoch);
            if (FastLeaderElection.LOG.isDebugEnabled()) {
                FastLeaderElection.LOG.debug("Sending Notification: " + this.proposedLeader + " (n.leader), 0x" + Long.toHexString(this.proposedZxid) + " (n.zxid), 0x" + Long.toHexString(this.logicalclock.get()) + " (n.round), " + sid + " (recipient), " + this.self.getId() + " (myid), 0x" + Long.toHexString(this.proposedEpoch) + " (n.peerEpoch)");
            }
            this.sendqueue.offer(notmsg);
        }
    }
    
    private void printNotification(final Notification n) {
        FastLeaderElection.LOG.info("Notification: " + n.toString() + this.self.getPeerState() + " (my state)");
    }
    
    protected boolean totalOrderPredicate(final long newId, final long newZxid, final long newEpoch, final long curId, final long curZxid, final long curEpoch) {
        FastLeaderElection.LOG.debug("id: " + newId + ", proposed id: " + curId + ", zxid: 0x" + Long.toHexString(newZxid) + ", proposed zxid: 0x" + Long.toHexString(curZxid));
        return this.self.getQuorumVerifier().getWeight(newId) != 0L && (newEpoch > curEpoch || (newEpoch == curEpoch && (newZxid > curZxid || (newZxid == curZxid && newId > curId))));
    }
    
    protected boolean termPredicate(final HashMap<Long, Vote> votes, final Vote vote) {
        final HashSet<Long> set = new HashSet<Long>();
        for (final Map.Entry<Long, Vote> entry : votes.entrySet()) {
            if (vote.equals(entry.getValue())) {
                set.add(entry.getKey());
            }
        }
        return this.self.getQuorumVerifier().containsQuorum(set);
    }
    
    protected boolean checkLeader(final HashMap<Long, Vote> votes, final long leader, final long electionEpoch) {
        boolean predicate = true;
        if (leader != this.self.getId()) {
            if (votes.get(leader) == null) {
                predicate = false;
            }
            else if (votes.get(leader).getState() != QuorumPeer.ServerState.LEADING) {
                predicate = false;
            }
        }
        else if (this.logicalclock.get() != electionEpoch) {
            predicate = false;
        }
        return predicate;
    }
    
    protected boolean ooePredicate(final HashMap<Long, Vote> recv, final HashMap<Long, Vote> ooe, final Notification n) {
        return this.termPredicate(recv, new Vote(n.version, n.leader, n.zxid, n.electionEpoch, n.peerEpoch, n.state)) && this.checkLeader(ooe, n.leader, n.electionEpoch);
    }
    
    synchronized void updateProposal(final long leader, final long zxid, final long epoch) {
        if (FastLeaderElection.LOG.isDebugEnabled()) {
            FastLeaderElection.LOG.debug("Updating proposal: " + leader + " (newleader), 0x" + Long.toHexString(zxid) + " (newzxid), " + this.proposedLeader + " (oldleader), 0x" + Long.toHexString(this.proposedZxid) + " (oldzxid)");
        }
        this.proposedLeader = leader;
        this.proposedZxid = zxid;
        this.proposedEpoch = epoch;
    }
    
    synchronized Vote getVote() {
        return new Vote(this.proposedLeader, this.proposedZxid, this.proposedEpoch);
    }
    
    private QuorumPeer.ServerState learningState() {
        if (this.self.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
            FastLeaderElection.LOG.debug("I'm a participant: " + this.self.getId());
            return QuorumPeer.ServerState.FOLLOWING;
        }
        FastLeaderElection.LOG.debug("I'm an observer: " + this.self.getId());
        return QuorumPeer.ServerState.OBSERVING;
    }
    
    private long getInitId() {
        if (this.self.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
            return this.self.getId();
        }
        return Long.MIN_VALUE;
    }
    
    private long getInitLastLoggedZxid() {
        if (this.self.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
            return this.self.getLastLoggedZxid();
        }
        return Long.MIN_VALUE;
    }
    
    private long getPeerEpoch() {
        if (this.self.getLearnerType() == QuorumPeer.LearnerType.PARTICIPANT) {
            try {
                return this.self.getCurrentEpoch();
            }
            catch (IOException e) {
                final RuntimeException re = new RuntimeException(e.getMessage());
                re.setStackTrace(e.getStackTrace());
                throw re;
            }
        }
        return Long.MIN_VALUE;
    }
    
    @Override
    public Vote lookForLeader() throws InterruptedException {
        try {
            this.self.jmxLeaderElectionBean = new LeaderElectionBean();
            MBeanRegistry.getInstance().register(this.self.jmxLeaderElectionBean, this.self.jmxLocalPeerBean);
        }
        catch (Exception e) {
            FastLeaderElection.LOG.warn("Failed to register with JMX", e);
            this.self.jmxLeaderElectionBean = null;
        }
        if (this.self.start_fle == 0L) {
            this.self.start_fle = Time.currentElapsedTime();
        }
        try {
            final HashMap<Long, Vote> recvset = new HashMap<Long, Vote>();
            final HashMap<Long, Vote> outofelection = new HashMap<Long, Vote>();
            int notTimeout = 200;
            synchronized (this) {
                this.logicalclock.incrementAndGet();
                this.updateProposal(this.getInitId(), this.getInitLastLoggedZxid(), this.getPeerEpoch());
            }
            FastLeaderElection.LOG.info("New election. My id =  " + this.self.getId() + ", proposed zxid=0x" + Long.toHexString(this.proposedZxid));
            this.sendNotifications();
            while (this.self.getPeerState() == QuorumPeer.ServerState.LOOKING && !this.stop) {
                Notification n = this.recvqueue.poll(notTimeout, TimeUnit.MILLISECONDS);
                if (n == null) {
                    if (this.manager.haveDelivered()) {
                        this.sendNotifications();
                    }
                    else {
                        this.manager.connectAll();
                    }
                    final int tmpTimeOut = notTimeout * 2;
                    notTimeout = ((tmpTimeOut < 60000) ? tmpTimeOut : 60000);
                    FastLeaderElection.LOG.info("Notification time out: " + notTimeout);
                }
                else if (this.validVoter(n.sid) && this.validVoter(n.leader)) {
                    switch (n.state) {
                        case LOOKING: {
                            if (n.electionEpoch > this.logicalclock.get()) {
                                this.logicalclock.set(n.electionEpoch);
                                recvset.clear();
                                if (this.totalOrderPredicate(n.leader, n.zxid, n.peerEpoch, this.getInitId(), this.getInitLastLoggedZxid(), this.getPeerEpoch())) {
                                    this.updateProposal(n.leader, n.zxid, n.peerEpoch);
                                }
                                else {
                                    this.updateProposal(this.getInitId(), this.getInitLastLoggedZxid(), this.getPeerEpoch());
                                }
                                this.sendNotifications();
                            }
                            else if (n.electionEpoch < this.logicalclock.get()) {
                                if (FastLeaderElection.LOG.isDebugEnabled()) {
                                    FastLeaderElection.LOG.debug("Notification election epoch is smaller than logicalclock. n.electionEpoch = 0x" + Long.toHexString(n.electionEpoch) + ", logicalclock=0x" + Long.toHexString(this.logicalclock.get()));
                                    continue;
                                }
                                continue;
                            }
                            else if (this.totalOrderPredicate(n.leader, n.zxid, n.peerEpoch, this.proposedLeader, this.proposedZxid, this.proposedEpoch)) {
                                this.updateProposal(n.leader, n.zxid, n.peerEpoch);
                                this.sendNotifications();
                            }
                            if (FastLeaderElection.LOG.isDebugEnabled()) {
                                FastLeaderElection.LOG.debug("Adding vote: from=" + n.sid + ", proposed leader=" + n.leader + ", proposed zxid=0x" + Long.toHexString(n.zxid) + ", proposed election epoch=0x" + Long.toHexString(n.electionEpoch));
                            }
                            recvset.put(n.sid, new Vote(n.leader, n.zxid, n.electionEpoch, n.peerEpoch));
                            if (!this.termPredicate(recvset, new Vote(this.proposedLeader, this.proposedZxid, this.logicalclock.get(), this.proposedEpoch))) {
                                continue;
                            }
                            while ((n = this.recvqueue.poll(200L, TimeUnit.MILLISECONDS)) != null) {
                                if (this.totalOrderPredicate(n.leader, n.zxid, n.peerEpoch, this.proposedLeader, this.proposedZxid, this.proposedEpoch)) {
                                    this.recvqueue.put(n);
                                    break;
                                }
                            }
                            if (n == null) {
                                this.self.setPeerState((this.proposedLeader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : this.learningState());
                                final Vote endVote = new Vote(this.proposedLeader, this.proposedZxid, this.logicalclock.get(), this.proposedEpoch);
                                this.leaveInstance(endVote);
                                return endVote;
                            }
                            continue;
                        }
                        case OBSERVING: {
                            FastLeaderElection.LOG.debug("Notification from observer: " + n.sid);
                            continue;
                        }
                        case FOLLOWING:
                        case LEADING: {
                            if (n.electionEpoch == this.logicalclock.get()) {
                                recvset.put(n.sid, new Vote(n.leader, n.zxid, n.electionEpoch, n.peerEpoch));
                                if (this.ooePredicate(recvset, outofelection, n)) {
                                    this.self.setPeerState((n.leader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : this.learningState());
                                    final Vote endVote = new Vote(n.leader, n.zxid, n.electionEpoch, n.peerEpoch);
                                    this.leaveInstance(endVote);
                                    return endVote;
                                }
                            }
                            outofelection.put(n.sid, new Vote(n.version, n.leader, n.zxid, n.electionEpoch, n.peerEpoch, n.state));
                            if (this.ooePredicate(outofelection, outofelection, n)) {
                                synchronized (this) {
                                    this.logicalclock.set(n.electionEpoch);
                                    this.self.setPeerState((n.leader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : this.learningState());
                                }
                                final Vote endVote = new Vote(n.leader, n.zxid, n.electionEpoch, n.peerEpoch);
                                this.leaveInstance(endVote);
                                return endVote;
                            }
                            continue;
                        }
                        default: {
                            FastLeaderElection.LOG.warn("Notification state unrecognized: {} (n.state), {} (n.sid)", n.state, n.sid);
                            continue;
                        }
                    }
                }
                else {
                    if (!this.validVoter(n.leader)) {
                        FastLeaderElection.LOG.warn("Ignoring notification for non-cluster member sid {} from sid {}", (Object)n.leader, n.sid);
                    }
                    if (this.validVoter(n.sid)) {
                        continue;
                    }
                    FastLeaderElection.LOG.warn("Ignoring notification for sid {} from non-quorum member sid {}", (Object)n.leader, n.sid);
                }
            }
            return null;
        }
        finally {
            try {
                if (this.self.jmxLeaderElectionBean != null) {
                    MBeanRegistry.getInstance().unregister(this.self.jmxLeaderElectionBean);
                }
            }
            catch (Exception e2) {
                FastLeaderElection.LOG.warn("Failed to unregister with JMX", e2);
            }
            this.self.jmxLeaderElectionBean = null;
            FastLeaderElection.LOG.debug("Number of connection processing threads: {}", (Object)this.manager.getConnectionThreadCount());
        }
    }
    
    private boolean validVoter(final long sid) {
        return this.self.getVotingView().containsKey(sid);
    }
    
    static {
        LOG = LoggerFactory.getLogger(FastLeaderElection.class);
    }
    
    public static class Notification
    {
        public static final int CURRENTVERSION = 1;
        int version;
        long leader;
        long zxid;
        long electionEpoch;
        QuorumPeer.ServerState state;
        long sid;
        long peerEpoch;
        
        @Override
        public String toString() {
            return Long.toHexString(this.version) + " (message format version), " + this.leader + " (n.leader), 0x" + Long.toHexString(this.zxid) + " (n.zxid), 0x" + Long.toHexString(this.electionEpoch) + " (n.round), " + this.state + " (n.state), " + this.sid + " (n.sid), 0x" + Long.toHexString(this.peerEpoch) + " (n.peerEpoch) ";
        }
    }
    
    public static class ToSend
    {
        long leader;
        long zxid;
        long electionEpoch;
        QuorumPeer.ServerState state;
        long sid;
        long peerEpoch;
        
        ToSend(final mType type, final long leader, final long zxid, final long electionEpoch, final QuorumPeer.ServerState state, final long sid, final long peerEpoch) {
            this.leader = leader;
            this.zxid = zxid;
            this.electionEpoch = electionEpoch;
            this.state = state;
            this.sid = sid;
            this.peerEpoch = peerEpoch;
        }
        
        enum mType
        {
            crequest, 
            challenge, 
            notification, 
            ack;
        }
    }
    
    protected class Messenger
    {
        WorkerSender ws;
        WorkerReceiver wr;
        
        Messenger(final QuorumCnxManager manager) {
            this.ws = new WorkerSender(manager);
            Thread t = new Thread(this.ws, "WorkerSender[myid=" + FastLeaderElection.this.self.getId() + "]");
            t.setDaemon(true);
            t.start();
            this.wr = new WorkerReceiver(manager);
            t = new Thread(this.wr, "WorkerReceiver[myid=" + FastLeaderElection.this.self.getId() + "]");
            t.setDaemon(true);
            t.start();
        }
        
        void halt() {
            this.ws.stop = true;
            this.wr.stop = true;
        }
        
        class WorkerReceiver extends ZooKeeperThread
        {
            volatile boolean stop;
            QuorumCnxManager manager;
            
            WorkerReceiver(final QuorumCnxManager manager) {
                super("WorkerReceiver");
                this.stop = false;
                this.manager = manager;
            }
            
            @Override
            public void run() {
                while (!this.stop) {
                    try {
                        final QuorumCnxManager.Message response = this.manager.pollRecvQueue(3000L, TimeUnit.MILLISECONDS);
                        if (response == null) {
                            continue;
                        }
                        if (!FastLeaderElection.this.validVoter(response.sid)) {
                            final Vote current = FastLeaderElection.this.self.getCurrentVote();
                            final ToSend notmsg = new ToSend(ToSend.mType.notification, current.getId(), current.getZxid(), FastLeaderElection.this.logicalclock.get(), FastLeaderElection.this.self.getPeerState(), response.sid, current.getPeerEpoch());
                            FastLeaderElection.this.sendqueue.offer(notmsg);
                        }
                        else {
                            if (FastLeaderElection.LOG.isDebugEnabled()) {
                                FastLeaderElection.LOG.debug("Receive new notification message. My id = " + FastLeaderElection.this.self.getId());
                            }
                            if (response.buffer.capacity() < 28) {
                                FastLeaderElection.LOG.error("Got a short response: " + response.buffer.capacity());
                            }
                            else {
                                final boolean backCompatibility = response.buffer.capacity() == 28;
                                response.buffer.clear();
                                final Notification n = new Notification();
                                QuorumPeer.ServerState ackstate = QuorumPeer.ServerState.LOOKING;
                                switch (response.buffer.getInt()) {
                                    case 0: {
                                        ackstate = QuorumPeer.ServerState.LOOKING;
                                        break;
                                    }
                                    case 1: {
                                        ackstate = QuorumPeer.ServerState.FOLLOWING;
                                        break;
                                    }
                                    case 2: {
                                        ackstate = QuorumPeer.ServerState.LEADING;
                                        break;
                                    }
                                    case 3: {
                                        ackstate = QuorumPeer.ServerState.OBSERVING;
                                        break;
                                    }
                                    default: {
                                        continue;
                                    }
                                }
                                n.leader = response.buffer.getLong();
                                n.zxid = response.buffer.getLong();
                                n.electionEpoch = response.buffer.getLong();
                                n.state = ackstate;
                                n.sid = response.sid;
                                if (!backCompatibility) {
                                    n.peerEpoch = response.buffer.getLong();
                                }
                                else {
                                    if (FastLeaderElection.LOG.isInfoEnabled()) {
                                        FastLeaderElection.LOG.info("Backward compatibility mode, server id=" + n.sid);
                                    }
                                    n.peerEpoch = ZxidUtils.getEpochFromZxid(n.zxid);
                                }
                                n.version = ((response.buffer.remaining() >= 4) ? response.buffer.getInt() : 0);
                                if (FastLeaderElection.LOG.isInfoEnabled()) {
                                    FastLeaderElection.this.printNotification(n);
                                }
                                if (FastLeaderElection.this.self.getPeerState() == QuorumPeer.ServerState.LOOKING) {
                                    FastLeaderElection.this.recvqueue.offer(n);
                                    if (ackstate != QuorumPeer.ServerState.LOOKING || n.electionEpoch >= FastLeaderElection.this.logicalclock.get()) {
                                        continue;
                                    }
                                    final Vote v = FastLeaderElection.this.getVote();
                                    final ToSend notmsg2 = new ToSend(ToSend.mType.notification, v.getId(), v.getZxid(), FastLeaderElection.this.logicalclock.get(), FastLeaderElection.this.self.getPeerState(), response.sid, v.getPeerEpoch());
                                    FastLeaderElection.this.sendqueue.offer(notmsg2);
                                }
                                else {
                                    final Vote current2 = FastLeaderElection.this.self.getCurrentVote();
                                    if (ackstate != QuorumPeer.ServerState.LOOKING) {
                                        continue;
                                    }
                                    if (FastLeaderElection.LOG.isDebugEnabled()) {
                                        FastLeaderElection.LOG.debug("Sending new notification. My id =  " + FastLeaderElection.this.self.getId() + " recipient=" + response.sid + " zxid=0x" + Long.toHexString(current2.getZxid()) + " leader=" + current2.getId());
                                    }
                                    ToSend notmsg2;
                                    if (n.version > 0) {
                                        notmsg2 = new ToSend(ToSend.mType.notification, current2.getId(), current2.getZxid(), current2.getElectionEpoch(), FastLeaderElection.this.self.getPeerState(), response.sid, current2.getPeerEpoch());
                                    }
                                    else {
                                        final Vote bcVote = FastLeaderElection.this.self.getBCVote();
                                        notmsg2 = new ToSend(ToSend.mType.notification, bcVote.getId(), bcVote.getZxid(), bcVote.getElectionEpoch(), FastLeaderElection.this.self.getPeerState(), response.sid, bcVote.getPeerEpoch());
                                    }
                                    FastLeaderElection.this.sendqueue.offer(notmsg2);
                                }
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        System.out.println("Interrupted Exception while waiting for new message" + e.toString());
                    }
                }
                FastLeaderElection.LOG.info("WorkerReceiver is down");
            }
        }
        
        class WorkerSender extends ZooKeeperThread
        {
            volatile boolean stop;
            QuorumCnxManager manager;
            
            WorkerSender(final QuorumCnxManager manager) {
                super("WorkerSender");
                this.stop = false;
                this.manager = manager;
            }
            
            @Override
            public void run() {
                while (!this.stop) {
                    try {
                        final ToSend m = FastLeaderElection.this.sendqueue.poll(3000L, TimeUnit.MILLISECONDS);
                        if (m == null) {
                            continue;
                        }
                        this.process(m);
                        continue;
                    }
                    catch (InterruptedException e) {}
                    break;
                }
                FastLeaderElection.LOG.info("WorkerSender is down");
            }
            
            void process(final ToSend m) {
                final ByteBuffer requestBuffer = FastLeaderElection.buildMsg(m.state.ordinal(), m.leader, m.zxid, m.electionEpoch, m.peerEpoch);
                this.manager.toSend(m.sid, requestBuffer);
            }
        }
    }
}
