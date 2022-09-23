// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.net.SocketAddress;
import org.apache.zookeeper.common.Time;
import java.util.Random;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import org.apache.zookeeper.server.ZooKeeperThread;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import java.util.Collection;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;

@Deprecated
public class AuthFastLeaderElection implements Election
{
    private static final Logger LOG;
    static int sequencer;
    static int maxTag;
    static int finalizeWait;
    static int challengeCounter;
    private boolean authEnabled;
    LinkedBlockingQueue<ToSend> sendqueue;
    LinkedBlockingQueue<Notification> recvqueue;
    QuorumPeer self;
    int port;
    volatile long logicalclock;
    DatagramSocket mySocket;
    long proposedLeader;
    long proposedZxid;
    
    public AuthFastLeaderElection(final QuorumPeer self, final boolean auth) {
        this.authEnabled = false;
        this.authEnabled = auth;
        this.starter(self);
    }
    
    public AuthFastLeaderElection(final QuorumPeer self) {
        this.authEnabled = false;
        this.starter(self);
    }
    
    private void starter(final QuorumPeer self) {
        this.self = self;
        this.port = self.getVotingView().get(self.getId()).electionAddr.getPort();
        this.proposedLeader = -1L;
        this.proposedZxid = -1L;
        try {
            this.mySocket = new DatagramSocket(this.port);
        }
        catch (SocketException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        }
        this.sendqueue = new LinkedBlockingQueue<ToSend>(2 * self.getVotingView().size());
        this.recvqueue = new LinkedBlockingQueue<Notification>(2 * self.getVotingView().size());
        new Messenger(self.getVotingView().size() * 2, this.mySocket);
    }
    
    private void leaveInstance() {
        ++this.logicalclock;
    }
    
    private void sendNotifications() {
        for (final QuorumPeer.QuorumServer server : this.self.getView().values()) {
            final ToSend notmsg = new ToSend(ToSend.mType.notification, AuthFastLeaderElection.sequencer++, this.proposedLeader, this.proposedZxid, this.logicalclock, QuorumPeer.ServerState.LOOKING, this.self.getView().get(server.id).electionAddr);
            this.sendqueue.offer(notmsg);
        }
    }
    
    private boolean totalOrderPredicate(final long id, final long zxid) {
        return zxid > this.proposedZxid || (zxid == this.proposedZxid && id > this.proposedLeader);
    }
    
    private boolean termPredicate(final HashMap<InetSocketAddress, Vote> votes, final long l, final long zxid) {
        final Collection<Vote> votesCast = votes.values();
        int count = 0;
        for (final Vote v : votesCast) {
            if (v.getId() == l && v.getZxid() == zxid) {
                ++count;
            }
        }
        return count > this.self.getVotingView().size() / 2;
    }
    
    @Override
    public void shutdown() {
    }
    
    @Override
    public Vote lookForLeader() throws InterruptedException {
        try {
            this.self.jmxLeaderElectionBean = new LeaderElectionBean();
            MBeanRegistry.getInstance().register(this.self.jmxLeaderElectionBean, this.self.jmxLocalPeerBean);
        }
        catch (Exception e) {
            AuthFastLeaderElection.LOG.warn("Failed to register with JMX", e);
            this.self.jmxLeaderElectionBean = null;
        }
        try {
            final HashMap<InetSocketAddress, Vote> recvset = new HashMap<InetSocketAddress, Vote>();
            final HashMap<InetSocketAddress, Vote> outofelection = new HashMap<InetSocketAddress, Vote>();
            ++this.logicalclock;
            this.proposedLeader = this.self.getId();
            this.proposedZxid = this.self.getLastLoggedZxid();
            AuthFastLeaderElection.LOG.info("Election tally");
            this.sendNotifications();
            while (this.self.getPeerState() == QuorumPeer.ServerState.LOOKING) {
                final Notification n = this.recvqueue.poll(2 * AuthFastLeaderElection.finalizeWait, TimeUnit.MILLISECONDS);
                if (n == null) {
                    if (outofelection.isEmpty() && recvset.size() <= 1) {
                        continue;
                    }
                    this.sendNotifications();
                }
                else {
                    switch (n.state) {
                        case LOOKING: {
                            if (n.epoch > this.logicalclock) {
                                this.logicalclock = n.epoch;
                                recvset.clear();
                                if (this.totalOrderPredicate(n.leader, n.zxid)) {
                                    this.proposedLeader = n.leader;
                                    this.proposedZxid = n.zxid;
                                }
                                this.sendNotifications();
                            }
                            else {
                                if (n.epoch < this.logicalclock) {
                                    continue;
                                }
                                if (this.totalOrderPredicate(n.leader, n.zxid)) {
                                    this.proposedLeader = n.leader;
                                    this.proposedZxid = n.zxid;
                                    this.sendNotifications();
                                }
                            }
                            recvset.put(n.addr, new Vote(n.leader, n.zxid));
                            if (this.self.getVotingView().size() == recvset.size()) {
                                this.self.setPeerState((this.proposedLeader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : QuorumPeer.ServerState.FOLLOWING);
                                this.leaveInstance();
                                return new Vote(this.proposedLeader, this.proposedZxid);
                            }
                            if (!this.termPredicate(recvset, this.proposedLeader, this.proposedZxid)) {
                                continue;
                            }
                            AuthFastLeaderElection.LOG.info("Passed predicate");
                            Thread.sleep(AuthFastLeaderElection.finalizeWait);
                            while (!this.recvqueue.isEmpty() && !this.totalOrderPredicate(this.recvqueue.peek().leader, this.recvqueue.peek().zxid)) {
                                this.recvqueue.poll();
                            }
                            if (this.recvqueue.isEmpty()) {
                                this.self.setPeerState((this.proposedLeader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : QuorumPeer.ServerState.FOLLOWING);
                                this.leaveInstance();
                                return new Vote(this.proposedLeader, this.proposedZxid);
                            }
                            continue;
                        }
                        case LEADING: {
                            outofelection.put(n.addr, new Vote(n.leader, n.zxid));
                            if (this.termPredicate(outofelection, n.leader, n.zxid)) {
                                this.self.setPeerState((n.leader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : QuorumPeer.ServerState.FOLLOWING);
                                this.leaveInstance();
                                return new Vote(n.leader, n.zxid);
                            }
                            continue;
                        }
                        case FOLLOWING: {
                            outofelection.put(n.addr, new Vote(n.leader, n.zxid));
                            if (this.termPredicate(outofelection, n.leader, n.zxid)) {
                                this.self.setPeerState((n.leader == this.self.getId()) ? QuorumPeer.ServerState.LEADING : QuorumPeer.ServerState.FOLLOWING);
                                this.leaveInstance();
                                return new Vote(n.leader, n.zxid);
                            }
                            continue;
                        }
                    }
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
                AuthFastLeaderElection.LOG.warn("Failed to unregister with JMX", e2);
            }
            this.self.jmxLeaderElectionBean = null;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(AuthFastLeaderElection.class);
        AuthFastLeaderElection.sequencer = 0;
        AuthFastLeaderElection.maxTag = 0;
        AuthFastLeaderElection.finalizeWait = 100;
        AuthFastLeaderElection.challengeCounter = 0;
    }
    
    public static class Notification
    {
        long leader;
        long zxid;
        long epoch;
        QuorumPeer.ServerState state;
        InetSocketAddress addr;
    }
    
    public static class ToSend
    {
        int type;
        long leader;
        long zxid;
        long epoch;
        QuorumPeer.ServerState state;
        long tag;
        InetSocketAddress addr;
        
        ToSend(final mType type, final long tag, final long leader, final long zxid, final long epoch, final QuorumPeer.ServerState state, final InetSocketAddress addr) {
            switch (type) {
                case crequest: {
                    this.type = 0;
                    this.tag = tag;
                    this.leader = leader;
                    this.zxid = zxid;
                    this.epoch = epoch;
                    this.state = state;
                    this.addr = addr;
                    break;
                }
                case challenge: {
                    this.type = 1;
                    this.tag = tag;
                    this.leader = leader;
                    this.zxid = zxid;
                    this.epoch = epoch;
                    this.state = state;
                    this.addr = addr;
                    break;
                }
                case notification: {
                    this.type = 2;
                    this.leader = leader;
                    this.zxid = zxid;
                    this.epoch = epoch;
                    this.state = QuorumPeer.ServerState.LOOKING;
                    this.tag = tag;
                    this.addr = addr;
                    break;
                }
                case ack: {
                    this.type = 3;
                    this.tag = tag;
                    this.leader = leader;
                    this.zxid = zxid;
                    this.epoch = epoch;
                    this.state = state;
                    this.addr = addr;
                    break;
                }
            }
        }
        
        enum mType
        {
            crequest, 
            challenge, 
            notification, 
            ack;
        }
    }
    
    private class Messenger
    {
        final DatagramSocket mySocket;
        long lastProposedLeader;
        long lastProposedZxid;
        long lastEpoch;
        final Set<Long> ackset;
        final ConcurrentHashMap<Long, Long> challengeMap;
        final ConcurrentHashMap<Long, Semaphore> challengeMutex;
        final ConcurrentHashMap<Long, Semaphore> ackMutex;
        final ConcurrentHashMap<InetSocketAddress, ConcurrentHashMap<Long, Long>> addrChallengeMap;
        
        public boolean queueEmpty() {
            return AuthFastLeaderElection.this.sendqueue.isEmpty() || this.ackset.isEmpty() || AuthFastLeaderElection.this.recvqueue.isEmpty();
        }
        
        Messenger(final int threads, final DatagramSocket s) {
            this.mySocket = s;
            this.ackset = Collections.newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
            this.challengeMap = new ConcurrentHashMap<Long, Long>();
            this.challengeMutex = new ConcurrentHashMap<Long, Semaphore>();
            this.ackMutex = new ConcurrentHashMap<Long, Semaphore>();
            this.addrChallengeMap = new ConcurrentHashMap<InetSocketAddress, ConcurrentHashMap<Long, Long>>();
            this.lastProposedLeader = 0L;
            this.lastProposedZxid = 0L;
            this.lastEpoch = 0L;
            for (int i = 0; i < threads; ++i) {
                final Thread t = new ZooKeeperThread(new WorkerSender(3), "WorkerSender Thread: " + (i + 1));
                t.setDaemon(true);
                t.start();
            }
            for (final QuorumPeer.QuorumServer server : AuthFastLeaderElection.this.self.getVotingView().values()) {
                final InetSocketAddress saddr = new InetSocketAddress(server.addr.getAddress(), AuthFastLeaderElection.this.port);
                this.addrChallengeMap.put(saddr, new ConcurrentHashMap<Long, Long>());
            }
            final Thread t2 = new ZooKeeperThread(new WorkerReceiver(s, this), "WorkerReceiver-" + s.getRemoteSocketAddress());
            t2.start();
        }
        
        class WorkerReceiver implements Runnable
        {
            DatagramSocket mySocket;
            Messenger myMsg;
            
            WorkerReceiver(final DatagramSocket s, final Messenger msg) {
                this.mySocket = s;
                this.myMsg = msg;
            }
            
            boolean saveChallenge(final long tag, final long challenge) {
                final Semaphore s = Messenger.this.challengeMutex.get(tag);
                if (s != null) {
                    synchronized (Messenger.this) {
                        Messenger.this.challengeMap.put(tag, challenge);
                        Messenger.this.challengeMutex.remove(tag);
                    }
                    s.release();
                }
                else {
                    AuthFastLeaderElection.LOG.error("No challenge mutex object");
                }
                return true;
            }
            
            @Override
            public void run() {
                final byte[] responseBytes = new byte[48];
                final ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);
                final DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
                while (true) {
                    try {
                        responseBuffer.clear();
                        this.mySocket.receive(responsePacket);
                    }
                    catch (IOException e) {
                        AuthFastLeaderElection.LOG.warn("Ignoring exception receiving", e);
                    }
                    if (responsePacket.getLength() != responseBytes.length) {
                        AuthFastLeaderElection.LOG.warn("Got a short response: " + responsePacket.getLength() + " " + responsePacket.toString());
                    }
                    else {
                        responseBuffer.clear();
                        final int type = responseBuffer.getInt();
                        if (type > 3 || type < 0) {
                            AuthFastLeaderElection.LOG.warn("Got bad Msg type: " + type);
                        }
                        else {
                            final long tag = responseBuffer.getLong();
                            QuorumPeer.ServerState ackstate = QuorumPeer.ServerState.LOOKING;
                            switch (responseBuffer.getInt()) {
                                case 0: {
                                    ackstate = QuorumPeer.ServerState.LOOKING;
                                    break;
                                }
                                case 1: {
                                    ackstate = QuorumPeer.ServerState.LEADING;
                                    break;
                                }
                                case 2: {
                                    ackstate = QuorumPeer.ServerState.FOLLOWING;
                                    break;
                                }
                            }
                            final Vote current = AuthFastLeaderElection.this.self.getCurrentVote();
                            switch (type) {
                                case 0: {
                                    final ToSend c = new ToSend(ToSend.mType.challenge, tag, current.getId(), current.getZxid(), AuthFastLeaderElection.this.logicalclock, AuthFastLeaderElection.this.self.getPeerState(), (InetSocketAddress)responsePacket.getSocketAddress());
                                    AuthFastLeaderElection.this.sendqueue.offer(c);
                                    continue;
                                }
                                case 1: {
                                    final long challenge = responseBuffer.getLong();
                                    this.saveChallenge(tag, challenge);
                                    continue;
                                }
                                case 2: {
                                    final Notification n = new Notification();
                                    n.leader = responseBuffer.getLong();
                                    n.zxid = responseBuffer.getLong();
                                    n.epoch = responseBuffer.getLong();
                                    n.state = ackstate;
                                    n.addr = (InetSocketAddress)responsePacket.getSocketAddress();
                                    if (this.myMsg.lastEpoch <= n.epoch && (n.zxid > this.myMsg.lastProposedZxid || (n.zxid == this.myMsg.lastProposedZxid && n.leader > this.myMsg.lastProposedLeader))) {
                                        this.myMsg.lastProposedZxid = n.zxid;
                                        this.myMsg.lastProposedLeader = n.leader;
                                        this.myMsg.lastEpoch = n.epoch;
                                    }
                                    final InetSocketAddress addr = (InetSocketAddress)responsePacket.getSocketAddress();
                                    if (AuthFastLeaderElection.this.authEnabled) {
                                        final ConcurrentHashMap<Long, Long> tmpMap = Messenger.this.addrChallengeMap.get(addr);
                                        if (tmpMap == null) {
                                            continue;
                                        }
                                        if (tmpMap.get(tag) != null) {
                                            final long recChallenge = responseBuffer.getLong();
                                            if (tmpMap.get(tag) == recChallenge) {
                                                AuthFastLeaderElection.this.recvqueue.offer(n);
                                                final ToSend a = new ToSend(ToSend.mType.ack, tag, current.getId(), current.getZxid(), AuthFastLeaderElection.this.logicalclock, AuthFastLeaderElection.this.self.getPeerState(), addr);
                                                AuthFastLeaderElection.this.sendqueue.offer(a);
                                            }
                                            else {
                                                AuthFastLeaderElection.LOG.warn("Incorrect challenge: " + recChallenge + ", " + Messenger.this.addrChallengeMap.toString());
                                            }
                                        }
                                        else {
                                            AuthFastLeaderElection.LOG.warn("No challenge for host: " + addr + " " + tag);
                                        }
                                        continue;
                                    }
                                    AuthFastLeaderElection.this.recvqueue.offer(n);
                                    final ToSend a2 = new ToSend(ToSend.mType.ack, tag, current.getId(), current.getZxid(), AuthFastLeaderElection.this.logicalclock, AuthFastLeaderElection.this.self.getPeerState(), (InetSocketAddress)responsePacket.getSocketAddress());
                                    AuthFastLeaderElection.this.sendqueue.offer(a2);
                                    continue;
                                }
                                case 3: {
                                    final Semaphore s = Messenger.this.ackMutex.get(tag);
                                    if (s != null) {
                                        s.release();
                                    }
                                    else {
                                        AuthFastLeaderElection.LOG.error("Empty ack semaphore");
                                    }
                                    Messenger.this.ackset.add(tag);
                                    if (AuthFastLeaderElection.this.authEnabled) {
                                        final ConcurrentHashMap<Long, Long> tmpMap2 = Messenger.this.addrChallengeMap.get(responsePacket.getSocketAddress());
                                        if (tmpMap2 != null) {
                                            tmpMap2.remove(tag);
                                        }
                                        else {
                                            AuthFastLeaderElection.LOG.warn("No such address in the ensemble configuration " + responsePacket.getSocketAddress());
                                        }
                                    }
                                    if (ackstate != QuorumPeer.ServerState.LOOKING) {
                                        final Notification outofsync = new Notification();
                                        outofsync.leader = responseBuffer.getLong();
                                        outofsync.zxid = responseBuffer.getLong();
                                        outofsync.epoch = responseBuffer.getLong();
                                        outofsync.state = ackstate;
                                        outofsync.addr = (InetSocketAddress)responsePacket.getSocketAddress();
                                        AuthFastLeaderElection.this.recvqueue.offer(outofsync);
                                        continue;
                                    }
                                    continue;
                                }
                                default: {
                                    AuthFastLeaderElection.LOG.warn("Received message of incorrect type " + type);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        class WorkerSender implements Runnable
        {
            Random rand;
            int maxAttempts;
            int ackWait;
            
            WorkerSender(final int attempts) {
                this.ackWait = AuthFastLeaderElection.finalizeWait;
                this.maxAttempts = attempts;
                this.rand = new Random(Thread.currentThread().getId() + Time.currentElapsedTime());
            }
            
            long genChallenge() {
                final byte[] buf = { (byte)((AuthFastLeaderElection.challengeCounter & 0xFF000000) >>> 24), (byte)((AuthFastLeaderElection.challengeCounter & 0xFF0000) >>> 16), (byte)((AuthFastLeaderElection.challengeCounter & 0xFF00) >>> 8), (byte)(AuthFastLeaderElection.challengeCounter & 0xFF), 0, 0, 0, 0 };
                ++AuthFastLeaderElection.challengeCounter;
                final int secret = this.rand.nextInt(Integer.MAX_VALUE);
                buf[4] = (byte)((secret & 0xFF000000) >>> 24);
                buf[5] = (byte)((secret & 0xFF0000) >>> 16);
                buf[6] = (byte)((secret & 0xFF00) >>> 8);
                buf[7] = (byte)(secret & 0xFF);
                return ((long)(buf[0] & 0xFF) << 56) + ((long)(buf[1] & 0xFF) << 48) + ((long)(buf[2] & 0xFF) << 40) + ((long)(buf[3] & 0xFF) << 32) + ((long)(buf[4] & 0xFF) << 24) + ((long)(buf[5] & 0xFF) << 16) + ((long)(buf[6] & 0xFF) << 8) + (buf[7] & 0xFF);
            }
            
            @Override
            public void run() {
                try {
                    while (true) {
                        final ToSend m = AuthFastLeaderElection.this.sendqueue.take();
                        this.process(m);
                    }
                }
                catch (InterruptedException e) {}
            }
            
            private void process(final ToSend m) {
                int attempts = 0;
                final byte[] requestBytes = new byte[48];
                final DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length);
                final ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
                switch (m.type) {
                    case 0: {
                        requestBuffer.clear();
                        requestBuffer.putInt(ToSend.mType.crequest.ordinal());
                        requestBuffer.putLong(m.tag);
                        requestBuffer.putInt(m.state.ordinal());
                        final byte[] zeroes = new byte[32];
                        requestBuffer.put(zeroes);
                        requestPacket.setLength(48);
                        try {
                            requestPacket.setSocketAddress(m.addr);
                        }
                        catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Unable to set socket address on packet, msg:" + e.getMessage() + " with addr:" + m.addr, e);
                        }
                        try {
                            if (Messenger.this.challengeMap.get(m.tag) == null) {
                                Messenger.this.mySocket.send(requestPacket);
                            }
                        }
                        catch (IOException e2) {
                            AuthFastLeaderElection.LOG.warn("Exception while sending challenge: ", e2);
                        }
                        break;
                    }
                    case 1: {
                        final ConcurrentHashMap<Long, Long> tmpMap = Messenger.this.addrChallengeMap.get(m.addr);
                        if (tmpMap != null) {
                            final Long tmpLong = tmpMap.get(m.tag);
                            long newChallenge;
                            if (tmpLong != null) {
                                newChallenge = tmpLong;
                            }
                            else {
                                newChallenge = this.genChallenge();
                            }
                            tmpMap.put(m.tag, newChallenge);
                            requestBuffer.clear();
                            requestBuffer.putInt(ToSend.mType.challenge.ordinal());
                            requestBuffer.putLong(m.tag);
                            requestBuffer.putInt(m.state.ordinal());
                            requestBuffer.putLong(newChallenge);
                            final byte[] zeroes = new byte[24];
                            requestBuffer.put(zeroes);
                            requestPacket.setLength(48);
                            try {
                                requestPacket.setSocketAddress(m.addr);
                            }
                            catch (IllegalArgumentException e3) {
                                throw new IllegalArgumentException("Unable to set socket address on packet, msg:" + e3.getMessage() + " with addr:" + m.addr, e3);
                            }
                            try {
                                Messenger.this.mySocket.send(requestPacket);
                            }
                            catch (IOException e4) {
                                AuthFastLeaderElection.LOG.warn("Exception while sending challenge: ", e4);
                            }
                            break;
                        }
                        AuthFastLeaderElection.LOG.error("Address is not in the configuration: " + m.addr);
                        break;
                    }
                    case 2: {
                        requestBuffer.clear();
                        requestBuffer.putInt(m.type);
                        requestBuffer.putLong(m.tag);
                        requestBuffer.putInt(m.state.ordinal());
                        requestBuffer.putLong(m.leader);
                        requestBuffer.putLong(m.zxid);
                        requestBuffer.putLong(m.epoch);
                        final byte[] zeroes = new byte[8];
                        requestBuffer.put(zeroes);
                        requestPacket.setLength(48);
                        try {
                            requestPacket.setSocketAddress(m.addr);
                        }
                        catch (IllegalArgumentException e5) {
                            throw new IllegalArgumentException("Unable to set socket address on packet, msg:" + e5.getMessage() + " with addr:" + m.addr, e5);
                        }
                        boolean myChallenge = false;
                        boolean myAck = false;
                        while (attempts < this.maxAttempts) {
                            try {
                                if (!myChallenge && AuthFastLeaderElection.this.authEnabled) {
                                    final ToSend crequest = new ToSend(ToSend.mType.crequest, m.tag, m.leader, m.zxid, m.epoch, QuorumPeer.ServerState.LOOKING, m.addr);
                                    AuthFastLeaderElection.this.sendqueue.offer(crequest);
                                    try {
                                        final double timeout = this.ackWait * Math.pow(2.0, attempts);
                                        final Semaphore s = new Semaphore(0);
                                        synchronized (Messenger.this) {
                                            Messenger.this.challengeMutex.put(m.tag, s);
                                            s.tryAcquire((long)timeout, TimeUnit.MILLISECONDS);
                                            myChallenge = Messenger.this.challengeMap.containsKey(m.tag);
                                        }
                                    }
                                    catch (InterruptedException e6) {
                                        AuthFastLeaderElection.LOG.warn("Challenge request exception: ", e6);
                                    }
                                }
                                if (AuthFastLeaderElection.this.authEnabled && !myChallenge) {
                                    ++attempts;
                                    continue;
                                }
                                if (AuthFastLeaderElection.this.authEnabled) {
                                    requestBuffer.position(40);
                                    final Long tmpLong2 = Messenger.this.challengeMap.get(m.tag);
                                    if (tmpLong2 != null) {
                                        requestBuffer.putLong(tmpLong2);
                                    }
                                    else {
                                        AuthFastLeaderElection.LOG.warn("No challenge with tag: " + m.tag);
                                    }
                                }
                                Messenger.this.mySocket.send(requestPacket);
                                try {
                                    final Semaphore s2 = new Semaphore(0);
                                    final double timeout = this.ackWait * Math.pow(10.0, attempts);
                                    Messenger.this.ackMutex.put(m.tag, s2);
                                    s2.tryAcquire((int)timeout, TimeUnit.MILLISECONDS);
                                }
                                catch (InterruptedException e7) {
                                    AuthFastLeaderElection.LOG.warn("Ack exception: ", e7);
                                }
                                if (Messenger.this.ackset.remove(m.tag)) {
                                    myAck = true;
                                }
                            }
                            catch (IOException e8) {
                                AuthFastLeaderElection.LOG.warn("Sending exception: ", e8);
                            }
                            if (myAck) {
                                Messenger.this.challengeMap.remove(m.tag);
                                return;
                            }
                            ++attempts;
                        }
                        if (m.epoch == AuthFastLeaderElection.this.logicalclock) {
                            Messenger.this.challengeMap.remove(m.tag);
                            AuthFastLeaderElection.this.sendqueue.offer(m);
                            break;
                        }
                        break;
                    }
                    case 3: {
                        requestBuffer.clear();
                        requestBuffer.putInt(m.type);
                        requestBuffer.putLong(m.tag);
                        requestBuffer.putInt(m.state.ordinal());
                        requestBuffer.putLong(m.leader);
                        requestBuffer.putLong(m.zxid);
                        requestBuffer.putLong(m.epoch);
                        requestPacket.setLength(48);
                        try {
                            requestPacket.setSocketAddress(m.addr);
                        }
                        catch (IllegalArgumentException e9) {
                            throw new IllegalArgumentException("Unable to set socket address on packet, msg:" + e9.getMessage() + " with addr:" + m.addr, e9);
                        }
                        try {
                            Messenger.this.mySocket.send(requestPacket);
                        }
                        catch (IOException e8) {
                            AuthFastLeaderElection.LOG.warn("Exception while sending ack: ", e8);
                        }
                        break;
                    }
                }
            }
        }
    }
}
