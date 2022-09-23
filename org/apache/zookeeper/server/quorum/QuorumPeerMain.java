// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import java.io.File;
import org.apache.zookeeper.server.ServerCnxnFactory;
import javax.management.JMException;
import org.apache.zookeeper.jmx.ManagedUtil;
import java.io.IOException;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.DatadirCleanupManager;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class QuorumPeerMain
{
    private static final Logger LOG;
    private static final String USAGE = "Usage: QuorumPeerMain configfile";
    protected QuorumPeer quorumPeer;
    
    public static void main(final String[] args) {
        final QuorumPeerMain main = new QuorumPeerMain();
        try {
            main.initializeAndRun(args);
        }
        catch (IllegalArgumentException e) {
            QuorumPeerMain.LOG.error("Invalid arguments, exiting abnormally", e);
            QuorumPeerMain.LOG.info("Usage: QuorumPeerMain configfile");
            System.err.println("Usage: QuorumPeerMain configfile");
            System.exit(2);
        }
        catch (QuorumPeerConfig.ConfigException e2) {
            QuorumPeerMain.LOG.error("Invalid config, exiting abnormally", e2);
            System.err.println("Invalid config, exiting abnormally");
            System.exit(2);
        }
        catch (Exception e3) {
            QuorumPeerMain.LOG.error("Unexpected exception, exiting abnormally", e3);
            System.exit(1);
        }
        QuorumPeerMain.LOG.info("Exiting normally");
        System.exit(0);
    }
    
    protected void initializeAndRun(final String[] args) throws QuorumPeerConfig.ConfigException, IOException {
        final QuorumPeerConfig config = new QuorumPeerConfig();
        if (args.length == 1) {
            config.parse(args[0]);
        }
        final DatadirCleanupManager purgeMgr = new DatadirCleanupManager(config.getDataDir(), config.getDataLogDir(), config.getSnapRetainCount(), config.getPurgeInterval());
        purgeMgr.start();
        if (args.length == 1 && config.servers.size() > 0) {
            this.runFromConfig(config);
        }
        else {
            QuorumPeerMain.LOG.warn("Either no config or no quorum defined in config, running  in standalone mode");
            ZooKeeperServerMain.main(args);
        }
    }
    
    public void runFromConfig(final QuorumPeerConfig config) throws IOException {
        try {
            ManagedUtil.registerLog4jMBeans();
        }
        catch (JMException e) {
            QuorumPeerMain.LOG.warn("Unable to register log4j JMX control", e);
        }
        QuorumPeerMain.LOG.info("Starting quorum peer");
        try {
            final ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory();
            cnxnFactory.configure(config.getClientPortAddress(), config.getMaxClientCnxns());
            (this.quorumPeer = this.getQuorumPeer()).setQuorumPeers(config.getServers());
            this.quorumPeer.setTxnFactory(new FileTxnSnapLog(new File(config.getDataLogDir()), new File(config.getDataDir())));
            this.quorumPeer.setElectionType(config.getElectionAlg());
            this.quorumPeer.setMyid(config.getServerId());
            this.quorumPeer.setTickTime(config.getTickTime());
            this.quorumPeer.setInitLimit(config.getInitLimit());
            this.quorumPeer.setSyncLimit(config.getSyncLimit());
            this.quorumPeer.setQuorumListenOnAllIPs(config.getQuorumListenOnAllIPs());
            this.quorumPeer.setCnxnFactory(cnxnFactory);
            this.quorumPeer.setQuorumVerifier(config.getQuorumVerifier());
            this.quorumPeer.setClientPortAddress(config.getClientPortAddress());
            this.quorumPeer.setMinSessionTimeout(config.getMinSessionTimeout());
            this.quorumPeer.setMaxSessionTimeout(config.getMaxSessionTimeout());
            this.quorumPeer.setZKDatabase(new ZKDatabase(this.quorumPeer.getTxnFactory()));
            this.quorumPeer.setLearnerType(config.getPeerType());
            this.quorumPeer.setSyncEnabled(config.getSyncEnabled());
            this.quorumPeer.setQuorumSaslEnabled(config.quorumEnableSasl);
            if (this.quorumPeer.isQuorumSaslAuthEnabled()) {
                this.quorumPeer.setQuorumServerSaslRequired(config.quorumServerRequireSasl);
                this.quorumPeer.setQuorumLearnerSaslRequired(config.quorumLearnerRequireSasl);
                this.quorumPeer.setQuorumServicePrincipal(config.quorumServicePrincipal);
                this.quorumPeer.setQuorumServerLoginContext(config.quorumServerLoginContext);
                this.quorumPeer.setQuorumLearnerLoginContext(config.quorumLearnerLoginContext);
            }
            this.quorumPeer.setQuorumCnxnThreadsSize(config.quorumCnxnThreadsSize);
            this.quorumPeer.initialize();
            this.quorumPeer.start();
            this.quorumPeer.join();
        }
        catch (InterruptedException e2) {
            QuorumPeerMain.LOG.warn("Quorum Peer interrupted", e2);
        }
    }
    
    protected QuorumPeer getQuorumPeer() throws SaslException {
        return new QuorumPeer();
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumPeerMain.class);
    }
}
