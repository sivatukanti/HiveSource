// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.slf4j.LoggerFactory;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.zookeeper.common.AtomicFileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.server.ZooKeeperServer;
import java.net.SocketException;
import java.io.FileNotFoundException;
import org.apache.zookeeper.server.util.ZxidUtils;
import java.util.Iterator;
import java.util.Set;
import org.apache.zookeeper.server.quorum.auth.NullQuorumAuthLearner;
import org.apache.zookeeper.server.quorum.auth.NullQuorumAuthServer;
import org.apache.zookeeper.server.quorum.auth.SaslQuorumAuthLearner;
import org.apache.zookeeper.server.quorum.auth.SaslQuorumAuthServer;
import java.util.HashSet;
import java.io.IOException;
import org.apache.zookeeper.server.quorum.flexible.QuorumMaj;
import java.io.File;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.ServerCnxnFactory;
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import java.util.Map;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.quorum.auth.QuorumAuthLearner;
import org.apache.zookeeper.server.quorum.auth.QuorumAuthServer;
import org.slf4j.Logger;
import org.apache.zookeeper.server.ZooKeeperThread;

public class QuorumPeer extends ZooKeeperThread implements QuorumStats.Provider
{
    private static final Logger LOG;
    QuorumBean jmxQuorumBean;
    LocalPeerBean jmxLocalPeerBean;
    LeaderElectionBean jmxLeaderElectionBean;
    QuorumCnxManager qcm;
    QuorumAuthServer authServer;
    QuorumAuthLearner authLearner;
    private boolean authInitialized;
    private ZKDatabase zkDb;
    static final long OBSERVER_ID = Long.MAX_VALUE;
    public long start_fle;
    public long end_fle;
    private LearnerType learnerType;
    protected Map<Long, QuorumServer> quorumPeers;
    private QuorumVerifier quorumConfig;
    private long myid;
    private volatile Vote currentVote;
    private volatile Vote bcVote;
    volatile boolean running;
    protected int tickTime;
    protected int minSessionTimeout;
    protected int maxSessionTimeout;
    protected int initLimit;
    protected int syncLimit;
    protected boolean syncEnabled;
    protected AtomicInteger tick;
    protected boolean quorumListenOnAllIPs;
    protected boolean quorumSaslEnableAuth;
    protected boolean quorumServerSaslAuthRequired;
    protected boolean quorumLearnerSaslAuthRequired;
    protected String quorumServicePrincipal;
    protected String quorumLearnerLoginContext;
    protected String quorumServerLoginContext;
    private static final int QUORUM_CNXN_THREADS_SIZE_DEFAULT_VALUE = 20;
    protected int quorumCnxnThreadsSize;
    private long electionTimeTaken;
    private ServerState state;
    DatagramSocket udpSocket;
    private InetSocketAddress myQuorumAddr;
    private int electionType;
    Election electionAlg;
    ServerCnxnFactory cnxnFactory;
    private FileTxnSnapLog logFactory;
    private final QuorumStats quorumStats;
    ResponderThread responder;
    public Follower follower;
    public Leader leader;
    public Observer observer;
    public static final String SYNC_ENABLED = "zookeeper.observer.syncEnabled";
    private long acceptedEpoch;
    private long currentEpoch;
    public static final String CURRENT_EPOCH_FILENAME = "currentEpoch";
    public static final String ACCEPTED_EPOCH_FILENAME = "acceptedEpoch";
    public static final String UPDATING_EPOCH_FILENAME = "updatingEpoch";
    
    public LearnerType getLearnerType() {
        return this.learnerType;
    }
    
    public void setLearnerType(final LearnerType p) {
        this.learnerType = p;
        if (this.quorumPeers.containsKey(this.myid)) {
            this.quorumPeers.get(this.myid).type = p;
        }
        else {
            QuorumPeer.LOG.error("Setting LearnerType to " + p + " but " + this.myid + " not in QuorumPeers. ");
        }
    }
    
    public int getQuorumSize() {
        return this.getVotingView().size();
    }
    
    @Override
    public long getId() {
        return this.myid;
    }
    
    public synchronized Vote getCurrentVote() {
        return this.currentVote;
    }
    
    public synchronized void setCurrentVote(final Vote v) {
        this.currentVote = v;
    }
    
    synchronized Vote getBCVote() {
        if (this.bcVote == null) {
            return this.currentVote;
        }
        return this.bcVote;
    }
    
    synchronized void setBCVote(final Vote v) {
        this.bcVote = v;
    }
    
    public synchronized void setPeerState(final ServerState newState) {
        this.state = newState;
    }
    
    public synchronized ServerState getPeerState() {
        return this.state;
    }
    
    public InetSocketAddress getQuorumAddress() {
        return this.myQuorumAddr;
    }
    
    public static QuorumPeer testingQuorumPeer() throws SaslException {
        return new QuorumPeer();
    }
    
    protected QuorumPeer() throws SaslException {
        super("QuorumPeer");
        this.authInitialized = false;
        this.learnerType = LearnerType.PARTICIPANT;
        this.running = true;
        this.minSessionTimeout = -1;
        this.maxSessionTimeout = -1;
        this.syncEnabled = true;
        this.tick = new AtomicInteger();
        this.quorumListenOnAllIPs = false;
        this.quorumCnxnThreadsSize = 20;
        this.electionTimeTaken = -1L;
        this.state = ServerState.LOOKING;
        this.logFactory = null;
        this.acceptedEpoch = -1L;
        this.currentEpoch = -1L;
        this.quorumStats = new QuorumStats(this);
        this.initialize();
    }
    
    public QuorumPeer(final Map<Long, QuorumServer> quorumPeers, final File dataDir, final File dataLogDir, final int electionType, final long myid, final int tickTime, final int initLimit, final int syncLimit, final ServerCnxnFactory cnxnFactory) throws IOException {
        this(quorumPeers, dataDir, dataLogDir, electionType, myid, tickTime, initLimit, syncLimit, false, cnxnFactory, new QuorumMaj(countParticipants(quorumPeers)));
    }
    
    public QuorumPeer(final Map<Long, QuorumServer> quorumPeers, final File dataDir, final File dataLogDir, final int electionType, final long myid, final int tickTime, final int initLimit, final int syncLimit, final boolean quorumListenOnAllIPs, final ServerCnxnFactory cnxnFactory, final QuorumVerifier quorumConfig) throws IOException {
        this();
        this.cnxnFactory = cnxnFactory;
        this.quorumPeers = quorumPeers;
        this.electionType = electionType;
        this.myid = myid;
        this.tickTime = tickTime;
        this.initLimit = initLimit;
        this.syncLimit = syncLimit;
        this.quorumListenOnAllIPs = quorumListenOnAllIPs;
        this.logFactory = new FileTxnSnapLog(dataLogDir, dataDir);
        this.zkDb = new ZKDatabase(this.logFactory);
        if (quorumConfig == null) {
            this.quorumConfig = new QuorumMaj(countParticipants(quorumPeers));
        }
        else {
            this.quorumConfig = quorumConfig;
        }
    }
    
    public void initialize() throws SaslException {
        if (this.isQuorumSaslAuthEnabled()) {
            final Set<String> authzHosts = new HashSet<String>();
            for (final QuorumServer qs : this.getView().values()) {
                authzHosts.add(qs.hostname);
            }
            this.authServer = new SaslQuorumAuthServer(this.isQuorumServerSaslAuthRequired(), this.quorumServerLoginContext, authzHosts);
            this.authLearner = new SaslQuorumAuthLearner(this.isQuorumLearnerSaslAuthRequired(), this.quorumServicePrincipal, this.quorumLearnerLoginContext);
            this.authInitialized = true;
        }
        else {
            this.authServer = new NullQuorumAuthServer();
            this.authLearner = new NullQuorumAuthLearner();
        }
    }
    
    QuorumStats quorumStats() {
        return this.quorumStats;
    }
    
    @Override
    public synchronized void start() {
        this.loadDataBase();
        this.cnxnFactory.start();
        this.startLeaderElection();
        super.start();
    }
    
    private void loadDataBase() {
        final File updating = new File(this.getTxnFactory().getSnapDir(), "updatingEpoch");
        try {
            this.zkDb.loadDataBase();
            final long lastProcessedZxid = this.zkDb.getDataTree().lastProcessedZxid;
            final long epochOfZxid = ZxidUtils.getEpochFromZxid(lastProcessedZxid);
            try {
                this.currentEpoch = this.readLongFromFile("currentEpoch");
                if (epochOfZxid > this.currentEpoch && updating.exists()) {
                    QuorumPeer.LOG.info("{} found. The server was terminated after taking a snapshot but before updating current epoch. Setting current epoch to {}.", "updatingEpoch", epochOfZxid);
                    this.setCurrentEpoch(epochOfZxid);
                    if (!updating.delete()) {
                        throw new IOException("Failed to delete " + updating.toString());
                    }
                }
            }
            catch (FileNotFoundException e) {
                this.currentEpoch = epochOfZxid;
                QuorumPeer.LOG.info("currentEpoch not found! Creating with a reasonable default of {}. This should only happen when you are upgrading your installation", (Object)this.currentEpoch);
                this.writeLongToFile("currentEpoch", this.currentEpoch);
            }
            if (epochOfZxid > this.currentEpoch) {
                throw new IOException("The current epoch, " + ZxidUtils.zxidToString(this.currentEpoch) + ", is older than the last zxid, " + lastProcessedZxid);
            }
            try {
                this.acceptedEpoch = this.readLongFromFile("acceptedEpoch");
            }
            catch (FileNotFoundException e) {
                this.acceptedEpoch = epochOfZxid;
                QuorumPeer.LOG.info("acceptedEpoch not found! Creating with a reasonable default of {}. This should only happen when you are upgrading your installation", (Object)this.acceptedEpoch);
                this.writeLongToFile("acceptedEpoch", this.acceptedEpoch);
            }
            if (this.acceptedEpoch < this.currentEpoch) {
                throw new IOException("The accepted epoch, " + ZxidUtils.zxidToString(this.acceptedEpoch) + " is less than the current epoch, " + ZxidUtils.zxidToString(this.currentEpoch));
            }
        }
        catch (IOException ie) {
            QuorumPeer.LOG.error("Unable to load database on disk", ie);
            throw new RuntimeException("Unable to run quorum server ", ie);
        }
    }
    
    public synchronized void stopLeaderElection() {
        this.responder.running = false;
        this.responder.interrupt();
    }
    
    public synchronized void startLeaderElection() {
        try {
            this.currentVote = new Vote(this.myid, this.getLastLoggedZxid(), this.getCurrentEpoch());
        }
        catch (IOException e) {
            final RuntimeException re = new RuntimeException(e.getMessage());
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
        for (final QuorumServer p : this.getView().values()) {
            if (p.id == this.myid) {
                this.myQuorumAddr = p.addr;
                break;
            }
        }
        if (this.myQuorumAddr == null) {
            throw new RuntimeException("My id " + this.myid + " not in the peer list");
        }
        if (this.electionType == 0) {
            try {
                this.udpSocket = new DatagramSocket(this.myQuorumAddr.getPort());
                (this.responder = new ResponderThread()).start();
            }
            catch (SocketException e2) {
                throw new RuntimeException(e2);
            }
        }
        this.electionAlg = this.createElectionAlgorithm(this.electionType);
    }
    
    protected static int countParticipants(final Map<Long, QuorumServer> peers) {
        int count = 0;
        for (final QuorumServer q : peers.values()) {
            if (q.type == LearnerType.PARTICIPANT) {
                ++count;
            }
        }
        return count;
    }
    
    public QuorumPeer(final Map<Long, QuorumServer> quorumPeers, final File snapDir, final File logDir, final int clientPort, final int electionAlg, final long myid, final int tickTime, final int initLimit, final int syncLimit) throws IOException {
        this(quorumPeers, snapDir, logDir, electionAlg, myid, tickTime, initLimit, syncLimit, false, ServerCnxnFactory.createFactory(new InetSocketAddress(clientPort), -1), new QuorumMaj(countParticipants(quorumPeers)));
    }
    
    public QuorumPeer(final Map<Long, QuorumServer> quorumPeers, final File snapDir, final File logDir, final int clientPort, final int electionAlg, final long myid, final int tickTime, final int initLimit, final int syncLimit, final QuorumVerifier quorumConfig) throws IOException {
        this(quorumPeers, snapDir, logDir, electionAlg, myid, tickTime, initLimit, syncLimit, false, ServerCnxnFactory.createFactory(new InetSocketAddress(clientPort), -1), quorumConfig);
    }
    
    public long getLastLoggedZxid() {
        if (!this.zkDb.isInitialized()) {
            this.loadDataBase();
        }
        return this.zkDb.getDataTreeLastProcessedZxid();
    }
    
    protected Follower makeFollower(final FileTxnSnapLog logFactory) throws IOException {
        return new Follower(this, new FollowerZooKeeperServer(logFactory, this, new ZooKeeperServer.BasicDataTreeBuilder(), this.zkDb));
    }
    
    protected Leader makeLeader(final FileTxnSnapLog logFactory) throws IOException {
        return new Leader(this, new LeaderZooKeeperServer(logFactory, this, new ZooKeeperServer.BasicDataTreeBuilder(), this.zkDb));
    }
    
    protected Observer makeObserver(final FileTxnSnapLog logFactory) throws IOException {
        return new Observer(this, new ObserverZooKeeperServer(logFactory, this, new ZooKeeperServer.BasicDataTreeBuilder(), this.zkDb));
    }
    
    protected Election createElectionAlgorithm(final int electionAlgorithm) {
        Election le = null;
        switch (electionAlgorithm) {
            case 0: {
                le = new LeaderElection(this);
                break;
            }
            case 1: {
                le = new AuthFastLeaderElection(this);
                break;
            }
            case 2: {
                le = new AuthFastLeaderElection(this, true);
                break;
            }
            case 3: {
                this.qcm = this.createCnxnManager();
                final QuorumCnxManager.Listener listener = this.qcm.listener;
                if (listener != null) {
                    listener.start();
                    le = new FastLeaderElection(this, this.qcm);
                    break;
                }
                QuorumPeer.LOG.error("Null listener when initializing cnx manager");
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        return le;
    }
    
    protected Election makeLEStrategy() {
        QuorumPeer.LOG.debug("Initializing leader election protocol...");
        if (this.getElectionType() == 0) {
            this.electionAlg = new LeaderElection(this);
        }
        return this.electionAlg;
    }
    
    protected synchronized void setLeader(final Leader newLeader) {
        this.leader = newLeader;
    }
    
    protected synchronized void setFollower(final Follower newFollower) {
        this.follower = newFollower;
    }
    
    protected synchronized void setObserver(final Observer newObserver) {
        this.observer = newObserver;
    }
    
    public synchronized ZooKeeperServer getActiveServer() {
        if (this.leader != null) {
            return this.leader.zk;
        }
        if (this.follower != null) {
            return this.follower.zk;
        }
        if (this.observer != null) {
            return this.observer.zk;
        }
        return null;
    }
    
    @Override
    public void run() {
        this.setName("QuorumPeer[myid=" + this.getId() + "]" + this.cnxnFactory.getLocalAddress());
        QuorumPeer.LOG.debug("Starting quorum peer");
        try {
            this.jmxQuorumBean = new QuorumBean(this);
            MBeanRegistry.getInstance().register(this.jmxQuorumBean, null);
            for (final QuorumServer s : this.getView().values()) {
                if (this.getId() == s.id) {
                    final LocalPeerBean jmxLocalPeerBean = new LocalPeerBean(this);
                    this.jmxLocalPeerBean = jmxLocalPeerBean;
                    final ZKMBeanInfo p = jmxLocalPeerBean;
                    try {
                        MBeanRegistry.getInstance().register(p, this.jmxQuorumBean);
                    }
                    catch (Exception e) {
                        QuorumPeer.LOG.warn("Failed to register with JMX", e);
                        this.jmxLocalPeerBean = null;
                    }
                }
                else {
                    final ZKMBeanInfo p = new RemotePeerBean(s);
                    try {
                        MBeanRegistry.getInstance().register(p, this.jmxQuorumBean);
                    }
                    catch (Exception e) {
                        QuorumPeer.LOG.warn("Failed to register with JMX", e);
                    }
                }
            }
        }
        catch (Exception e2) {
            QuorumPeer.LOG.warn("Failed to register with JMX", e2);
            this.jmxQuorumBean = null;
        }
        try {
            while (this.running) {
                switch (this.getPeerState()) {
                    case LOOKING: {
                        QuorumPeer.LOG.info("LOOKING");
                        if (Boolean.getBoolean("readonlymode.enabled")) {
                            QuorumPeer.LOG.info("Attempting to start ReadOnlyZooKeeperServer");
                            final ReadOnlyZooKeeperServer roZk = new ReadOnlyZooKeeperServer(this.logFactory, this, new ZooKeeperServer.BasicDataTreeBuilder(), this.zkDb);
                            final Thread roZkMgr = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(Math.max(2000, QuorumPeer.this.tickTime));
                                        if (ServerState.LOOKING.equals(QuorumPeer.this.getPeerState())) {
                                            roZk.startup();
                                        }
                                    }
                                    catch (InterruptedException e2) {
                                        QuorumPeer.LOG.info("Interrupted while attempting to start ReadOnlyZooKeeperServer, not started");
                                    }
                                    catch (Exception e) {
                                        QuorumPeer.LOG.error("FAILED to start ReadOnlyZooKeeperServer", e);
                                    }
                                }
                            };
                            try {
                                roZkMgr.start();
                                this.setBCVote(null);
                                this.setCurrentVote(this.makeLEStrategy().lookForLeader());
                            }
                            catch (Exception e3) {
                                QuorumPeer.LOG.warn("Unexpected exception", e3);
                                this.setPeerState(ServerState.LOOKING);
                            }
                            finally {
                                roZkMgr.interrupt();
                                roZk.shutdown();
                            }
                            continue;
                        }
                        try {
                            this.setBCVote(null);
                            this.setCurrentVote(this.makeLEStrategy().lookForLeader());
                        }
                        catch (Exception e2) {
                            QuorumPeer.LOG.warn("Unexpected exception", e2);
                            this.setPeerState(ServerState.LOOKING);
                        }
                        continue;
                    }
                    case OBSERVING: {
                        try {
                            QuorumPeer.LOG.info("OBSERVING");
                            this.setObserver(this.makeObserver(this.logFactory));
                            this.observer.observeLeader();
                        }
                        catch (Exception e2) {
                            QuorumPeer.LOG.warn("Unexpected exception", e2);
                        }
                        finally {
                            this.observer.shutdown();
                            this.setObserver(null);
                            this.setPeerState(ServerState.LOOKING);
                        }
                        continue;
                    }
                    case FOLLOWING: {
                        try {
                            QuorumPeer.LOG.info("FOLLOWING");
                            this.setFollower(this.makeFollower(this.logFactory));
                            this.follower.followLeader();
                        }
                        catch (Exception e2) {
                            QuorumPeer.LOG.warn("Unexpected exception", e2);
                        }
                        finally {
                            this.follower.shutdown();
                            this.setFollower(null);
                            this.setPeerState(ServerState.LOOKING);
                        }
                        continue;
                    }
                    case LEADING: {
                        QuorumPeer.LOG.info("LEADING");
                        try {
                            this.setLeader(this.makeLeader(this.logFactory));
                            this.leader.lead();
                            this.setLeader(null);
                        }
                        catch (Exception e2) {
                            QuorumPeer.LOG.warn("Unexpected exception", e2);
                        }
                        finally {
                            if (this.leader != null) {
                                this.leader.shutdown("Forcing shutdown");
                                this.setLeader(null);
                            }
                            this.setPeerState(ServerState.LOOKING);
                        }
                        continue;
                    }
                }
            }
        }
        finally {
            QuorumPeer.LOG.warn("QuorumPeer main thread exited");
            try {
                MBeanRegistry.getInstance().unregisterAll();
            }
            catch (Exception e4) {
                QuorumPeer.LOG.warn("Failed to unregister with JMX", e4);
            }
            this.jmxQuorumBean = null;
            this.jmxLocalPeerBean = null;
        }
    }
    
    public void shutdown() {
        this.running = false;
        if (this.leader != null) {
            this.leader.shutdown("quorum Peer shutdown");
        }
        if (this.follower != null) {
            this.follower.shutdown();
        }
        this.cnxnFactory.shutdown();
        if (this.udpSocket != null) {
            this.udpSocket.close();
        }
        if (this.getElectionAlg() != null) {
            this.interrupt();
            this.getElectionAlg().shutdown();
        }
        try {
            this.zkDb.close();
        }
        catch (IOException ie) {
            QuorumPeer.LOG.warn("Error closing logs ", ie);
        }
    }
    
    public Map<Long, QuorumServer> getView() {
        return Collections.unmodifiableMap((Map<? extends Long, ? extends QuorumServer>)this.quorumPeers);
    }
    
    public Map<Long, QuorumServer> getVotingView() {
        return viewToVotingView(this.getView());
    }
    
    static Map<Long, QuorumServer> viewToVotingView(final Map<Long, QuorumServer> view) {
        final Map<Long, QuorumServer> ret = new HashMap<Long, QuorumServer>();
        for (final QuorumServer server : view.values()) {
            if (server.type == LearnerType.PARTICIPANT) {
                ret.put(server.id, server);
            }
        }
        return ret;
    }
    
    public Map<Long, QuorumServer> getObservingView() {
        final Map<Long, QuorumServer> ret = new HashMap<Long, QuorumServer>();
        final Map<Long, QuorumServer> view = this.getView();
        for (final QuorumServer server : view.values()) {
            if (server.type == LearnerType.OBSERVER) {
                ret.put(server.id, server);
            }
        }
        return ret;
    }
    
    public boolean viewContains(final Long sid) {
        return this.quorumPeers.containsKey(sid);
    }
    
    @Override
    public String[] getQuorumPeers() {
        final List<String> l = new ArrayList<String>();
        synchronized (this) {
            if (this.leader != null) {
                for (final LearnerHandler fh : this.leader.getLearners()) {
                    if (fh.getSocket() != null) {
                        String s = fh.getSocket().getRemoteSocketAddress().toString();
                        if (this.leader.isLearnerSynced(fh)) {
                            s += "*";
                        }
                        l.add(s);
                    }
                }
            }
            else if (this.follower != null) {
                l.add(this.follower.sock.getRemoteSocketAddress().toString());
            }
        }
        return l.toArray(new String[0]);
    }
    
    @Override
    public String getServerState() {
        switch (this.getPeerState()) {
            case LOOKING: {
                return "leaderelection";
            }
            case LEADING: {
                return "leading";
            }
            case FOLLOWING: {
                return "following";
            }
            case OBSERVING: {
                return "observing";
            }
            default: {
                return "unknown";
            }
        }
    }
    
    public long getMyid() {
        return this.myid;
    }
    
    public void setMyid(final long myid) {
        this.myid = myid;
    }
    
    public int getTickTime() {
        return this.tickTime;
    }
    
    public void setTickTime(final int tickTime) {
        QuorumPeer.LOG.info("tickTime set to " + tickTime);
        this.tickTime = tickTime;
    }
    
    public int getMaxClientCnxnsPerHost() {
        final ServerCnxnFactory fac = this.getCnxnFactory();
        if (fac == null) {
            return -1;
        }
        return fac.getMaxClientCnxnsPerHost();
    }
    
    public int getMinSessionTimeout() {
        return (this.minSessionTimeout == -1) ? (this.tickTime * 2) : this.minSessionTimeout;
    }
    
    public void setMinSessionTimeout(final int min) {
        QuorumPeer.LOG.info("minSessionTimeout set to " + min);
        this.minSessionTimeout = min;
    }
    
    public int getMaxSessionTimeout() {
        return (this.maxSessionTimeout == -1) ? (this.tickTime * 20) : this.maxSessionTimeout;
    }
    
    public void setMaxSessionTimeout(final int max) {
        QuorumPeer.LOG.info("maxSessionTimeout set to " + max);
        this.maxSessionTimeout = max;
    }
    
    public int getInitLimit() {
        return this.initLimit;
    }
    
    public void setInitLimit(final int initLimit) {
        QuorumPeer.LOG.info("initLimit set to " + initLimit);
        this.initLimit = initLimit;
    }
    
    public int getTick() {
        return this.tick.get();
    }
    
    public QuorumVerifier getQuorumVerifier() {
        return this.quorumConfig;
    }
    
    public void setQuorumVerifier(final QuorumVerifier quorumConfig) {
        this.quorumConfig = quorumConfig;
    }
    
    public Election getElectionAlg() {
        return this.electionAlg;
    }
    
    public int getSyncLimit() {
        return this.syncLimit;
    }
    
    public void setSyncLimit(final int syncLimit) {
        this.syncLimit = syncLimit;
    }
    
    public boolean getSyncEnabled() {
        if (System.getProperty("zookeeper.observer.syncEnabled") != null) {
            QuorumPeer.LOG.info("zookeeper.observer.syncEnabled=" + Boolean.getBoolean("zookeeper.observer.syncEnabled"));
            return Boolean.getBoolean("zookeeper.observer.syncEnabled");
        }
        return this.syncEnabled;
    }
    
    public void setSyncEnabled(final boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }
    
    public int getElectionType() {
        return this.electionType;
    }
    
    public void setElectionType(final int electionType) {
        this.electionType = electionType;
    }
    
    public boolean getQuorumListenOnAllIPs() {
        return this.quorumListenOnAllIPs;
    }
    
    public void setQuorumListenOnAllIPs(final boolean quorumListenOnAllIPs) {
        this.quorumListenOnAllIPs = quorumListenOnAllIPs;
    }
    
    public ServerCnxnFactory getCnxnFactory() {
        return this.cnxnFactory;
    }
    
    public void setCnxnFactory(final ServerCnxnFactory cnxnFactory) {
        this.cnxnFactory = cnxnFactory;
    }
    
    public void setQuorumPeers(final Map<Long, QuorumServer> quorumPeers) {
        this.quorumPeers = quorumPeers;
    }
    
    public int getClientPort() {
        return this.cnxnFactory.getLocalPort();
    }
    
    public void setClientPortAddress(final InetSocketAddress addr) {
    }
    
    public void setTxnFactory(final FileTxnSnapLog factory) {
        this.logFactory = factory;
    }
    
    public FileTxnSnapLog getTxnFactory() {
        return this.logFactory;
    }
    
    public void setZKDatabase(final ZKDatabase database) {
        this.zkDb = database;
    }
    
    protected ZKDatabase getZkDb() {
        return this.zkDb;
    }
    
    public void setRunning(final boolean running) {
        this.running = running;
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public QuorumCnxManager getQuorumCnxManager() {
        return this.qcm;
    }
    
    private long readLongFromFile(final String name) throws IOException {
        final File file = new File(this.logFactory.getSnapDir(), name);
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        try {
            line = br.readLine();
            return Long.parseLong(line);
        }
        catch (NumberFormatException e) {
            throw new IOException("Found " + line + " in " + file);
        }
        finally {
            br.close();
        }
    }
    
    private void writeLongToFile(final String name, final long value) throws IOException {
        final File file = new File(this.logFactory.getSnapDir(), name);
        final AtomicFileOutputStream out = new AtomicFileOutputStream(file);
        final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
        boolean aborted = false;
        try {
            bw.write(Long.toString(value));
            bw.flush();
            out.flush();
        }
        catch (IOException e) {
            QuorumPeer.LOG.error("Failed to write new file " + file, e);
            aborted = true;
            out.abort();
            throw e;
        }
        finally {
            if (!aborted) {
                out.close();
            }
        }
    }
    
    public long getCurrentEpoch() throws IOException {
        if (this.currentEpoch == -1L) {
            this.currentEpoch = this.readLongFromFile("currentEpoch");
        }
        return this.currentEpoch;
    }
    
    public long getAcceptedEpoch() throws IOException {
        if (this.acceptedEpoch == -1L) {
            this.acceptedEpoch = this.readLongFromFile("acceptedEpoch");
        }
        return this.acceptedEpoch;
    }
    
    public void setCurrentEpoch(final long e) throws IOException {
        this.writeLongToFile("currentEpoch", this.currentEpoch = e);
    }
    
    public void setAcceptedEpoch(final long e) throws IOException {
        this.writeLongToFile("acceptedEpoch", this.acceptedEpoch = e);
    }
    
    protected void updateElectionVote(final long newEpoch) {
        final Vote currentVote = this.getCurrentVote();
        this.setBCVote(currentVote);
        if (currentVote != null) {
            this.setCurrentVote(new Vote(currentVote.getId(), currentVote.getZxid(), currentVote.getElectionEpoch(), newEpoch, currentVote.getState()));
        }
    }
    
    void setQuorumServerSaslRequired(final boolean serverSaslRequired) {
        this.quorumServerSaslAuthRequired = serverSaslRequired;
        QuorumPeer.LOG.info("{} set to {}", "quorum.auth.serverRequireSasl", serverSaslRequired);
    }
    
    void setQuorumLearnerSaslRequired(final boolean learnerSaslRequired) {
        this.quorumLearnerSaslAuthRequired = learnerSaslRequired;
        QuorumPeer.LOG.info("{} set to {}", "quorum.auth.learnerRequireSasl", learnerSaslRequired);
    }
    
    void setQuorumSaslEnabled(final boolean enableAuth) {
        if (!(this.quorumSaslEnableAuth = enableAuth)) {
            QuorumPeer.LOG.info("QuorumPeer communication is not secured!");
        }
        else {
            QuorumPeer.LOG.info("{} set to {}", "quorum.auth.enableSasl", enableAuth);
        }
    }
    
    void setQuorumServicePrincipal(final String servicePrincipal) {
        this.quorumServicePrincipal = servicePrincipal;
        QuorumPeer.LOG.info("{} set to {}", "quorum.auth.kerberos.servicePrincipal", this.quorumServicePrincipal);
    }
    
    void setQuorumLearnerLoginContext(final String learnerContext) {
        this.quorumLearnerLoginContext = learnerContext;
        QuorumPeer.LOG.info("{} set to {}", "quorum.auth.learner.saslLoginContext", this.quorumLearnerLoginContext);
    }
    
    void setQuorumServerLoginContext(final String serverContext) {
        this.quorumServerLoginContext = serverContext;
        QuorumPeer.LOG.info("{} set to {}", "quorum.auth.server.saslLoginContext", this.quorumServerLoginContext);
    }
    
    void setQuorumCnxnThreadsSize(final int qCnxnThreadsSize) {
        if (qCnxnThreadsSize > 20) {
            this.quorumCnxnThreadsSize = qCnxnThreadsSize;
        }
        QuorumPeer.LOG.info("quorum.cnxn.threads.size set to {}", (Object)this.quorumCnxnThreadsSize);
    }
    
    boolean isQuorumSaslAuthEnabled() {
        return this.quorumSaslEnableAuth;
    }
    
    private boolean isQuorumServerSaslAuthRequired() {
        return this.quorumServerSaslAuthRequired;
    }
    
    private boolean isQuorumLearnerSaslAuthRequired() {
        return this.quorumLearnerSaslAuthRequired;
    }
    
    public boolean hasAuthInitialized() {
        return this.authInitialized;
    }
    
    public QuorumCnxManager createCnxnManager() {
        return new QuorumCnxManager(this.getId(), this.getView(), this.authServer, this.authLearner, this.tickTime * this.syncLimit, this.getQuorumListenOnAllIPs(), this.quorumCnxnThreadsSize, this.isQuorumSaslAuthEnabled());
    }
    
    void setElectionTimeTaken(final long electionTimeTaken) {
        this.electionTimeTaken = electionTimeTaken;
    }
    
    long getElectionTimeTaken() {
        return this.electionTimeTaken;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumPeer.class);
    }
    
    public static class QuorumServer
    {
        public InetSocketAddress addr;
        public InetSocketAddress electionAddr;
        public String hostname;
        public int port;
        public int electionPort;
        public long id;
        public LearnerType type;
        
        private QuorumServer(final long id, final InetSocketAddress addr, final InetSocketAddress electionAddr) {
            this.port = 2888;
            this.electionPort = -1;
            this.type = LearnerType.PARTICIPANT;
            this.id = id;
            this.addr = addr;
            this.electionAddr = electionAddr;
        }
        
        public QuorumServer(final long id, final InetSocketAddress addr) {
            this.port = 2888;
            this.electionPort = -1;
            this.type = LearnerType.PARTICIPANT;
            this.id = id;
            this.addr = addr;
            this.electionAddr = null;
        }
        
        private QuorumServer(final long id, final InetSocketAddress addr, final InetSocketAddress electionAddr, final LearnerType type) {
            this.port = 2888;
            this.electionPort = -1;
            this.type = LearnerType.PARTICIPANT;
            this.id = id;
            this.addr = addr;
            this.electionAddr = electionAddr;
            this.type = type;
        }
        
        public QuorumServer(final long id, final String hostname, final Integer port, final Integer electionPort, final LearnerType type) {
            this.port = 2888;
            this.electionPort = -1;
            this.type = LearnerType.PARTICIPANT;
            this.id = id;
            this.hostname = hostname;
            if (port != null) {
                this.port = port;
            }
            if (electionPort != null) {
                this.electionPort = electionPort;
            }
            if (type != null) {
                this.type = type;
            }
            this.recreateSocketAddresses();
        }
        
        public void recreateSocketAddresses() {
            InetAddress address = null;
            try {
                int ipReachableTimeout = 0;
                final String ipReachableValue = System.getProperty("zookeeper.ipReachableTimeout");
                if (ipReachableValue != null) {
                    try {
                        ipReachableTimeout = Integer.parseInt(ipReachableValue);
                    }
                    catch (NumberFormatException e) {
                        QuorumPeer.LOG.error("{} is not a valid number", ipReachableValue);
                    }
                }
                if (ipReachableTimeout <= 0) {
                    address = InetAddress.getByName(this.hostname);
                }
                else {
                    address = this.getReachableAddress(this.hostname, ipReachableTimeout);
                }
                QuorumPeer.LOG.info("Resolved hostname: {} to address: {}", this.hostname, address);
                this.addr = new InetSocketAddress(address, this.port);
                if (this.electionPort > 0) {
                    this.electionAddr = new InetSocketAddress(address, this.electionPort);
                }
            }
            catch (UnknownHostException ex) {
                QuorumPeer.LOG.warn("Failed to resolve address: {}", this.hostname, ex);
                if (this.addr != null) {
                    return;
                }
                this.addr = InetSocketAddress.createUnresolved(this.hostname, this.port);
                if (this.electionPort > 0) {
                    this.electionAddr = InetSocketAddress.createUnresolved(this.hostname, this.electionPort);
                }
            }
        }
        
        public InetAddress getReachableAddress(final String hostname, final int timeout) throws UnknownHostException {
            final InetAddress[] allByName;
            final InetAddress[] addresses = allByName = InetAddress.getAllByName(hostname);
            for (final InetAddress a : allByName) {
                try {
                    if (a.isReachable(timeout)) {
                        return a;
                    }
                }
                catch (IOException e) {
                    QuorumPeer.LOG.warn("IP address {} is unreachable", a);
                }
            }
            return addresses[0];
        }
    }
    
    public enum ServerState
    {
        LOOKING, 
        FOLLOWING, 
        LEADING, 
        OBSERVING;
    }
    
    public enum LearnerType
    {
        PARTICIPANT, 
        OBSERVER;
    }
    
    @Deprecated
    class ResponderThread extends ZooKeeperThread
    {
        volatile boolean running;
        
        ResponderThread() {
            super("ResponderThread");
            this.running = true;
        }
        
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     2: newarray        B
            //     4: astore_1        /* b */
            //     5: aload_1         /* b */
            //     6: invokestatic    java/nio/ByteBuffer.wrap:([B)Ljava/nio/ByteBuffer;
            //     9: astore_2        /* responseBuffer */
            //    10: new             Ljava/net/DatagramPacket;
            //    13: dup            
            //    14: aload_1         /* b */
            //    15: aload_1         /* b */
            //    16: arraylength    
            //    17: invokespecial   java/net/DatagramPacket.<init>:([BI)V
            //    20: astore_3        /* packet */
            //    21: aload_0         /* this */
            //    22: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.running:Z
            //    25: ifeq            301
            //    28: aload_0         /* this */
            //    29: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //    32: getfield        org/apache/zookeeper/server/quorum/QuorumPeer.udpSocket:Ljava/net/DatagramSocket;
            //    35: aload_3         /* packet */
            //    36: invokevirtual   java/net/DatagramSocket.receive:(Ljava/net/DatagramPacket;)V
            //    39: aload_3         /* packet */
            //    40: invokevirtual   java/net/DatagramPacket.getLength:()I
            //    43: iconst_4       
            //    44: if_icmpeq       80
            //    47: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //    50: new             Ljava/lang/StringBuilder;
            //    53: dup            
            //    54: invokespecial   java/lang/StringBuilder.<init>:()V
            //    57: ldc             "Got more than just an xid! Len = "
            //    59: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    62: aload_3         /* packet */
            //    63: invokevirtual   java/net/DatagramPacket.getLength:()I
            //    66: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
            //    69: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //    72: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;)V
            //    77: goto            292
            //    80: aload_2         /* responseBuffer */
            //    81: invokevirtual   java/nio/ByteBuffer.clear:()Ljava/nio/Buffer;
            //    84: pop            
            //    85: aload_2         /* responseBuffer */
            //    86: invokevirtual   java/nio/ByteBuffer.getInt:()I
            //    89: pop            
            //    90: aload_2         /* responseBuffer */
            //    91: aload_0         /* this */
            //    92: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //    95: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$100:(Lorg/apache/zookeeper/server/quorum/QuorumPeer;)J
            //    98: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   101: pop            
            //   102: aload_0         /* this */
            //   103: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   106: invokevirtual   org/apache/zookeeper/server/quorum/QuorumPeer.getCurrentVote:()Lorg/apache/zookeeper/server/quorum/Vote;
            //   109: astore          current
            //   111: getstatic       org/apache/zookeeper/server/quorum/QuorumPeer$2.$SwitchMap$org$apache$zookeeper$server$quorum$QuorumPeer$ServerState:[I
            //   114: aload_0         /* this */
            //   115: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   118: invokevirtual   org/apache/zookeeper/server/quorum/QuorumPeer.getPeerState:()Lorg/apache/zookeeper/server/quorum/QuorumPeer$ServerState;
            //   121: invokevirtual   org/apache/zookeeper/server/quorum/QuorumPeer$ServerState.ordinal:()I
            //   124: iaload         
            //   125: tableswitch {
            //                2: 156
            //                3: 179
            //                4: 243
            //                5: 276
            //          default: 276
            //        }
            //   156: aload_2         /* responseBuffer */
            //   157: aload           current
            //   159: invokevirtual   org/apache/zookeeper/server/quorum/Vote.getId:()J
            //   162: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   165: pop            
            //   166: aload_2         /* responseBuffer */
            //   167: aload           current
            //   169: invokevirtual   org/apache/zookeeper/server/quorum/Vote.getZxid:()J
            //   172: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   175: pop            
            //   176: goto            276
            //   179: aload_2         /* responseBuffer */
            //   180: aload_0         /* this */
            //   181: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   184: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$100:(Lorg/apache/zookeeper/server/quorum/QuorumPeer;)J
            //   187: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   190: pop            
            //   191: aload_0         /* this */
            //   192: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   195: getfield        org/apache/zookeeper/server/quorum/QuorumPeer.leader:Lorg/apache/zookeeper/server/quorum/Leader;
            //   198: dup            
            //   199: astore          7
            //   201: monitorenter   
            //   202: aload_0         /* this */
            //   203: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   206: getfield        org/apache/zookeeper/server/quorum/QuorumPeer.leader:Lorg/apache/zookeeper/server/quorum/Leader;
            //   209: getfield        org/apache/zookeeper/server/quorum/Leader.lastProposed:J
            //   212: lstore          proposed
            //   214: aload           7
            //   216: monitorexit    
            //   217: goto            228
            //   220: astore          8
            //   222: aload           7
            //   224: monitorexit    
            //   225: aload           8
            //   227: athrow         
            //   228: aload_2         /* responseBuffer */
            //   229: lload           proposed
            //   231: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   234: pop            
            //   235: goto            276
            //   238: astore          5
            //   240: goto            276
            //   243: aload_2         /* responseBuffer */
            //   244: aload           current
            //   246: invokevirtual   org/apache/zookeeper/server/quorum/Vote.getId:()J
            //   249: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   252: pop            
            //   253: aload_2         /* responseBuffer */
            //   254: aload_0         /* this */
            //   255: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   258: getfield        org/apache/zookeeper/server/quorum/QuorumPeer.follower:Lorg/apache/zookeeper/server/quorum/Follower;
            //   261: invokevirtual   org/apache/zookeeper/server/quorum/Follower.getZxid:()J
            //   264: invokevirtual   java/nio/ByteBuffer.putLong:(J)Ljava/nio/ByteBuffer;
            //   267: pop            
            //   268: goto            276
            //   271: astore          5
            //   273: goto            276
            //   276: aload_3         /* packet */
            //   277: aload_1         /* b */
            //   278: invokevirtual   java/net/DatagramPacket.setData:([B)V
            //   281: aload_0         /* this */
            //   282: getfield        org/apache/zookeeper/server/quorum/QuorumPeer$ResponderThread.this$0:Lorg/apache/zookeeper/server/quorum/QuorumPeer;
            //   285: getfield        org/apache/zookeeper/server/quorum/QuorumPeer.udpSocket:Ljava/net/DatagramSocket;
            //   288: aload_3         /* packet */
            //   289: invokevirtual   java/net/DatagramSocket.send:(Ljava/net/DatagramPacket;)V
            //   292: aload_3         /* packet */
            //   293: aload_1         /* b */
            //   294: arraylength    
            //   295: invokevirtual   java/net/DatagramPacket.setLength:(I)V
            //   298: goto            21
            //   301: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //   304: ldc             "QuorumPeer responder thread exited"
            //   306: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;)V
            //   311: goto            379
            //   314: astore_1        /* e */
            //   315: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //   318: ldc             "Unexpected runtime exception in ResponderThread"
            //   320: aload_1         /* e */
            //   321: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
            //   326: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //   329: ldc             "QuorumPeer responder thread exited"
            //   331: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;)V
            //   336: goto            379
            //   339: astore_1        /* e */
            //   340: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //   343: ldc             "Unexpected IO exception in ResponderThread"
            //   345: aload_1         /* e */
            //   346: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;Ljava/lang/Throwable;)V
            //   351: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //   354: ldc             "QuorumPeer responder thread exited"
            //   356: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;)V
            //   361: goto            379
            //   364: astore          9
            //   366: invokestatic    org/apache/zookeeper/server/quorum/QuorumPeer.access$000:()Lorg/slf4j/Logger;
            //   369: ldc             "QuorumPeer responder thread exited"
            //   371: invokeinterface org/slf4j/Logger.warn:(Ljava/lang/String;)V
            //   376: aload           9
            //   378: athrow         
            //   379: return         
            //    StackMapTable: 00 10 FE 00 15 07 00 4A 07 00 4B 07 00 4C 3A FC 00 4B 07 00 4D 16 FF 00 28 00 08 07 00 4E 07 00 4A 07 00 4B 07 00 4C 07 00 4D 00 00 07 00 4F 00 01 07 00 50 FF 00 07 00 06 07 00 4E 07 00 4A 07 00 4B 07 00 4C 07 00 4D 04 00 00 FF 00 09 00 05 07 00 4E 07 00 4A 07 00 4B 07 00 4C 07 00 4D 00 01 07 00 51 04 5B 07 00 51 04 FA 00 0F F8 00 08 4C 07 00 52 58 07 00 53 58 07 00 50 0E
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                            
            //  -----  -----  -----  -----  --------------------------------
            //  202    217    220    228    Any
            //  220    225    220    228    Any
            //  191    235    238    243    Ljava/lang/NullPointerException;
            //  253    268    271    276    Ljava/lang/NullPointerException;
            //  0      301    314    339    Ljava/lang/RuntimeException;
            //  0      301    339    364    Ljava/io/IOException;
            //  0      301    364    379    Any
            //  314    326    364    379    Any
            //  339    351    364    379    Any
            //  364    366    364    379    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index 1 out of bounds for length 1
            //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
            //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
            //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
            //     at java.base/java.util.Objects.checkIndex(Objects.java:372)
            //     at java.base/java.util.ArrayList.get(ArrayList.java:458)
            //     at com.strobel.assembler.Collection.get(Collection.java:43)
            //     at java.base/java.util.Collections$UnmodifiableList.get(Collections.java:1308)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCallCore(AstMethodBodyBuilder.java:1313)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.adjustArgumentsForMethodCall(AstMethodBodyBuilder.java:1286)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1197)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:718)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:494)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:480)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:441)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:425)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:494)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:576)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
    }
}
