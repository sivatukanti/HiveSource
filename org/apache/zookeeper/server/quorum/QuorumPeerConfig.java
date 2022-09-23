// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import org.slf4j.MDC;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.zookeeper.server.quorum.flexible.QuorumMaj;
import org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical;
import java.net.InetAddress;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import java.util.HashMap;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class QuorumPeerConfig
{
    private static final Logger LOG;
    protected InetSocketAddress clientPortAddress;
    protected String dataDir;
    protected String dataLogDir;
    protected int tickTime;
    protected int maxClientCnxns;
    protected int minSessionTimeout;
    protected int maxSessionTimeout;
    protected int initLimit;
    protected int syncLimit;
    protected int electionAlg;
    protected int electionPort;
    protected boolean quorumListenOnAllIPs;
    protected final HashMap<Long, QuorumPeer.QuorumServer> servers;
    protected final HashMap<Long, QuorumPeer.QuorumServer> observers;
    protected long serverId;
    protected HashMap<Long, Long> serverWeight;
    protected HashMap<Long, Long> serverGroup;
    protected int numGroups;
    protected QuorumVerifier quorumVerifier;
    protected int snapRetainCount;
    protected int purgeInterval;
    protected boolean syncEnabled;
    protected QuorumPeer.LearnerType peerType;
    protected boolean quorumServerRequireSasl;
    protected boolean quorumLearnerRequireSasl;
    protected boolean quorumEnableSasl;
    protected String quorumServicePrincipal;
    protected String quorumLearnerLoginContext;
    protected String quorumServerLoginContext;
    protected int quorumCnxnThreadsSize;
    private final int MIN_SNAP_RETAIN_COUNT = 3;
    
    public QuorumPeerConfig() {
        this.tickTime = 3000;
        this.maxClientCnxns = 60;
        this.minSessionTimeout = -1;
        this.maxSessionTimeout = -1;
        this.electionAlg = 3;
        this.electionPort = 2182;
        this.quorumListenOnAllIPs = false;
        this.servers = new HashMap<Long, QuorumPeer.QuorumServer>();
        this.observers = new HashMap<Long, QuorumPeer.QuorumServer>();
        this.serverWeight = new HashMap<Long, Long>();
        this.serverGroup = new HashMap<Long, Long>();
        this.numGroups = 0;
        this.snapRetainCount = 3;
        this.purgeInterval = 0;
        this.syncEnabled = true;
        this.peerType = QuorumPeer.LearnerType.PARTICIPANT;
        this.quorumServerRequireSasl = false;
        this.quorumLearnerRequireSasl = false;
        this.quorumEnableSasl = false;
        this.quorumServicePrincipal = "zkquorum/localhost";
        this.quorumLearnerLoginContext = "QuorumLearner";
        this.quorumServerLoginContext = "QuorumServer";
    }
    
    private static String[] splitWithLeadingHostname(final String s) throws ConfigException {
        if (!s.startsWith("[")) {
            return s.split(":");
        }
        final int i = s.indexOf("]:");
        if (i < 0) {
            throw new ConfigException(s + " starts with '[' but has no matching ']:'");
        }
        final String[] sa = s.substring(i + 2).split(":");
        final String[] nsa = new String[sa.length + 1];
        nsa[0] = s.substring(1, i);
        System.arraycopy(sa, 0, nsa, 1, sa.length);
        return nsa;
    }
    
    public void parse(final String path) throws ConfigException {
        final File configFile = new File(path);
        QuorumPeerConfig.LOG.info("Reading configuration from: " + configFile);
        try {
            if (!configFile.exists()) {
                throw new IllegalArgumentException(configFile.toString() + " file is missing");
            }
            final Properties cfg = new Properties();
            final FileInputStream in = new FileInputStream(configFile);
            try {
                cfg.load(in);
            }
            finally {
                in.close();
            }
            this.parseProperties(cfg);
        }
        catch (IOException e) {
            throw new ConfigException("Error processing " + path, e);
        }
        catch (IllegalArgumentException e2) {
            throw new ConfigException("Error processing " + path, e2);
        }
    }
    
    public void parseProperties(final Properties zkProp) throws IOException, ConfigException {
        int clientPort = 0;
        String clientPortAddress = null;
        for (final Map.Entry<Object, Object> entry : zkProp.entrySet()) {
            final String key = entry.getKey().toString().trim();
            final String value = entry.getValue().toString().trim();
            if (key.equals("dataDir")) {
                this.dataDir = value;
            }
            else if (key.equals("dataLogDir")) {
                this.dataLogDir = value;
            }
            else if (key.equals("clientPort")) {
                clientPort = Integer.parseInt(value);
            }
            else if (key.equals("clientPortAddress")) {
                clientPortAddress = value.trim();
            }
            else if (key.equals("tickTime")) {
                this.tickTime = Integer.parseInt(value);
            }
            else if (key.equals("maxClientCnxns")) {
                this.maxClientCnxns = Integer.parseInt(value);
            }
            else if (key.equals("minSessionTimeout")) {
                this.minSessionTimeout = Integer.parseInt(value);
            }
            else if (key.equals("maxSessionTimeout")) {
                this.maxSessionTimeout = Integer.parseInt(value);
            }
            else if (key.equals("initLimit")) {
                this.initLimit = Integer.parseInt(value);
            }
            else if (key.equals("syncLimit")) {
                this.syncLimit = Integer.parseInt(value);
            }
            else if (key.equals("electionAlg")) {
                this.electionAlg = Integer.parseInt(value);
            }
            else if (key.equals("quorumListenOnAllIPs")) {
                this.quorumListenOnAllIPs = Boolean.parseBoolean(value);
            }
            else if (key.equals("peerType")) {
                if (value.toLowerCase().equals("observer")) {
                    this.peerType = QuorumPeer.LearnerType.OBSERVER;
                }
                else {
                    if (!value.toLowerCase().equals("participant")) {
                        throw new ConfigException("Unrecognised peertype: " + value);
                    }
                    this.peerType = QuorumPeer.LearnerType.PARTICIPANT;
                }
            }
            else if (key.equals("syncEnabled")) {
                this.syncEnabled = Boolean.parseBoolean(value);
            }
            else if (key.equals("autopurge.snapRetainCount")) {
                this.snapRetainCount = Integer.parseInt(value);
            }
            else if (key.equals("autopurge.purgeInterval")) {
                this.purgeInterval = Integer.parseInt(value);
            }
            else if (key.startsWith("server.")) {
                final int dot = key.indexOf(46);
                final long sid = Long.parseLong(key.substring(dot + 1));
                final String[] parts = splitWithLeadingHostname(value);
                if (parts.length != 2 && parts.length != 3 && parts.length != 4) {
                    QuorumPeerConfig.LOG.error(value + " does not have the form host:port or host:port:port  or host:port:port:type");
                }
                QuorumPeer.LearnerType type = null;
                final String hostname = parts[0];
                final Integer port = Integer.parseInt(parts[1]);
                Integer electionPort = null;
                if (parts.length > 2) {
                    electionPort = Integer.parseInt(parts[2]);
                }
                if (parts.length > 3) {
                    if (parts[3].toLowerCase().equals("observer")) {
                        type = QuorumPeer.LearnerType.OBSERVER;
                    }
                    else {
                        if (!parts[3].toLowerCase().equals("participant")) {
                            throw new ConfigException("Unrecognised peertype: " + value);
                        }
                        type = QuorumPeer.LearnerType.PARTICIPANT;
                    }
                }
                if (type == QuorumPeer.LearnerType.OBSERVER) {
                    this.observers.put(sid, new QuorumPeer.QuorumServer(sid, hostname, port, electionPort, type));
                }
                else {
                    this.servers.put(sid, new QuorumPeer.QuorumServer(sid, hostname, port, electionPort, type));
                }
            }
            else if (key.startsWith("group")) {
                final int dot = key.indexOf(46);
                final long gid = Long.parseLong(key.substring(dot + 1));
                ++this.numGroups;
                final String[] split;
                final String[] parts = split = value.split(":");
                for (final String s : split) {
                    final long sid2 = Long.parseLong(s);
                    if (this.serverGroup.containsKey(sid2)) {
                        throw new ConfigException("Server " + sid2 + "is in multiple groups");
                    }
                    this.serverGroup.put(sid2, gid);
                }
            }
            else if (key.startsWith("weight")) {
                final int dot = key.indexOf(46);
                final long sid = Long.parseLong(key.substring(dot + 1));
                this.serverWeight.put(sid, Long.parseLong(value));
            }
            else if (key.equals("quorum.auth.enableSasl")) {
                this.quorumEnableSasl = Boolean.parseBoolean(value);
            }
            else if (key.equals("quorum.auth.serverRequireSasl")) {
                this.quorumServerRequireSasl = Boolean.parseBoolean(value);
            }
            else if (key.equals("quorum.auth.learnerRequireSasl")) {
                this.quorumLearnerRequireSasl = Boolean.parseBoolean(value);
            }
            else if (key.equals("quorum.auth.learner.saslLoginContext")) {
                this.quorumLearnerLoginContext = value;
            }
            else if (key.equals("quorum.auth.server.saslLoginContext")) {
                this.quorumServerLoginContext = value;
            }
            else if (key.equals("quorum.auth.kerberos.servicePrincipal")) {
                this.quorumServicePrincipal = value;
            }
            else if (key.equals("quorum.cnxn.threads.size")) {
                this.quorumCnxnThreadsSize = Integer.parseInt(value);
            }
            else {
                System.setProperty("zookeeper." + key, value);
            }
        }
        if (!this.quorumEnableSasl && this.quorumServerRequireSasl) {
            throw new IllegalArgumentException("quorum.auth.enableSasl is disabled, so cannot enable quorum.auth.serverRequireSasl");
        }
        if (!this.quorumEnableSasl && this.quorumLearnerRequireSasl) {
            throw new IllegalArgumentException("quorum.auth.enableSasl is disabled, so cannot enable quorum.auth.learnerRequireSasl");
        }
        if (!this.quorumLearnerRequireSasl && this.quorumServerRequireSasl) {
            throw new IllegalArgumentException("quorum.auth.learnerRequireSasl is disabled, so cannot enable quorum.auth.serverRequireSasl");
        }
        if (this.snapRetainCount < 3) {
            QuorumPeerConfig.LOG.warn("Invalid autopurge.snapRetainCount: " + this.snapRetainCount + ". Defaulting to " + 3);
            this.snapRetainCount = 3;
        }
        if (this.dataDir == null) {
            throw new IllegalArgumentException("dataDir is not set");
        }
        if (this.dataLogDir == null) {
            this.dataLogDir = this.dataDir;
        }
        if (clientPort == 0) {
            throw new IllegalArgumentException("clientPort is not set");
        }
        if (clientPortAddress != null) {
            this.clientPortAddress = new InetSocketAddress(InetAddress.getByName(clientPortAddress), clientPort);
        }
        else {
            this.clientPortAddress = new InetSocketAddress(clientPort);
        }
        if (this.tickTime == 0) {
            throw new IllegalArgumentException("tickTime is not set");
        }
        if (this.minSessionTimeout > this.maxSessionTimeout) {
            throw new IllegalArgumentException("minSessionTimeout must not be larger than maxSessionTimeout");
        }
        if (this.servers.size() != 0) {
            if (this.servers.size() == 1) {
                if (this.observers.size() > 0) {
                    throw new IllegalArgumentException("Observers w/o quorum is an invalid configuration");
                }
                QuorumPeerConfig.LOG.error("Invalid configuration, only one server specified (ignoring)");
                this.servers.clear();
            }
            else if (this.servers.size() > 1) {
                if (this.servers.size() == 2) {
                    QuorumPeerConfig.LOG.warn("No server failure will be tolerated. You need at least 3 servers.");
                }
                else if (this.servers.size() % 2 == 0) {
                    QuorumPeerConfig.LOG.warn("Non-optimial configuration, consider an odd number of servers.");
                }
                if (this.initLimit == 0) {
                    throw new IllegalArgumentException("initLimit is not set");
                }
                if (this.syncLimit == 0) {
                    throw new IllegalArgumentException("syncLimit is not set");
                }
                if (this.electionAlg != 0) {
                    for (final QuorumPeer.QuorumServer s2 : this.servers.values()) {
                        if (s2.electionAddr == null) {
                            throw new IllegalArgumentException("Missing election port for server: " + s2.id);
                        }
                    }
                }
                if (this.serverGroup.size() > 0) {
                    if (this.servers.size() != this.serverGroup.size()) {
                        throw new ConfigException("Every server must be in exactly one group");
                    }
                    for (final QuorumPeer.QuorumServer s2 : this.servers.values()) {
                        if (!this.serverWeight.containsKey(s2.id)) {
                            this.serverWeight.put(s2.id, 1L);
                        }
                    }
                    this.quorumVerifier = new QuorumHierarchical(this.numGroups, this.serverWeight, this.serverGroup);
                }
                else {
                    QuorumPeerConfig.LOG.info("Defaulting to majority quorums");
                    this.quorumVerifier = new QuorumMaj(this.servers.size());
                }
                this.servers.putAll(this.observers);
                final File myIdFile = new File(this.dataDir, "myid");
                if (!myIdFile.exists()) {
                    throw new IllegalArgumentException(myIdFile.toString() + " file is missing");
                }
                final BufferedReader br = new BufferedReader(new FileReader(myIdFile));
                String myIdString;
                try {
                    myIdString = br.readLine();
                }
                finally {
                    br.close();
                }
                try {
                    this.serverId = Long.parseLong(myIdString);
                    MDC.put("myid", myIdString);
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException("serverid " + myIdString + " is not a number");
                }
                final QuorumPeer.LearnerType roleByServersList = this.observers.containsKey(this.serverId) ? QuorumPeer.LearnerType.OBSERVER : QuorumPeer.LearnerType.PARTICIPANT;
                if (roleByServersList != this.peerType) {
                    QuorumPeerConfig.LOG.warn("Peer type from servers list (" + roleByServersList + ") doesn't match peerType (" + this.peerType + "). Defaulting to servers list.");
                    this.peerType = roleByServersList;
                }
            }
            return;
        }
        if (this.observers.size() > 0) {
            throw new IllegalArgumentException("Observers w/o participants is an invalid configuration");
        }
    }
    
    public InetSocketAddress getClientPortAddress() {
        return this.clientPortAddress;
    }
    
    public String getDataDir() {
        return this.dataDir;
    }
    
    public String getDataLogDir() {
        return this.dataLogDir;
    }
    
    public int getTickTime() {
        return this.tickTime;
    }
    
    public int getMaxClientCnxns() {
        return this.maxClientCnxns;
    }
    
    public int getMinSessionTimeout() {
        return this.minSessionTimeout;
    }
    
    public int getMaxSessionTimeout() {
        return this.maxSessionTimeout;
    }
    
    public int getInitLimit() {
        return this.initLimit;
    }
    
    public int getSyncLimit() {
        return this.syncLimit;
    }
    
    public int getElectionAlg() {
        return this.electionAlg;
    }
    
    public int getElectionPort() {
        return this.electionPort;
    }
    
    public int getSnapRetainCount() {
        return this.snapRetainCount;
    }
    
    public int getPurgeInterval() {
        return this.purgeInterval;
    }
    
    public boolean getSyncEnabled() {
        return this.syncEnabled;
    }
    
    public QuorumVerifier getQuorumVerifier() {
        return this.quorumVerifier;
    }
    
    public Map<Long, QuorumPeer.QuorumServer> getServers() {
        return Collections.unmodifiableMap((Map<? extends Long, ? extends QuorumPeer.QuorumServer>)this.servers);
    }
    
    public long getServerId() {
        return this.serverId;
    }
    
    public boolean isDistributed() {
        return this.servers.size() > 1;
    }
    
    public QuorumPeer.LearnerType getPeerType() {
        return this.peerType;
    }
    
    public Boolean getQuorumListenOnAllIPs() {
        return this.quorumListenOnAllIPs;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumPeerConfig.class);
    }
    
    public static class ConfigException extends Exception
    {
        public ConfigException(final String msg) {
            super(msg);
        }
        
        public ConfigException(final String msg, final Exception e) {
            super(msg, e);
        }
    }
}
