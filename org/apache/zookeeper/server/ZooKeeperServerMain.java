// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import javax.management.JMException;
import org.apache.zookeeper.jmx.ManagedUtil;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class ZooKeeperServerMain
{
    private static final Logger LOG;
    private static final String USAGE = "Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]";
    private ServerCnxnFactory cnxnFactory;
    
    public static void main(final String[] args) {
        final ZooKeeperServerMain main = new ZooKeeperServerMain();
        try {
            main.initializeAndRun(args);
        }
        catch (IllegalArgumentException e) {
            ZooKeeperServerMain.LOG.error("Invalid arguments, exiting abnormally", e);
            ZooKeeperServerMain.LOG.info("Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]");
            System.err.println("Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]");
            System.exit(2);
        }
        catch (QuorumPeerConfig.ConfigException e2) {
            ZooKeeperServerMain.LOG.error("Invalid config, exiting abnormally", e2);
            System.err.println("Invalid config, exiting abnormally");
            System.exit(2);
        }
        catch (Exception e3) {
            ZooKeeperServerMain.LOG.error("Unexpected exception, exiting abnormally", e3);
            System.exit(1);
        }
        ZooKeeperServerMain.LOG.info("Exiting normally");
        System.exit(0);
    }
    
    protected void initializeAndRun(final String[] args) throws QuorumPeerConfig.ConfigException, IOException {
        try {
            ManagedUtil.registerLog4jMBeans();
        }
        catch (JMException e) {
            ZooKeeperServerMain.LOG.warn("Unable to register log4j JMX control", e);
        }
        final ServerConfig config = new ServerConfig();
        if (args.length == 1) {
            config.parse(args[0]);
        }
        else {
            config.parse(args);
        }
        this.runFromConfig(config);
    }
    
    public void runFromConfig(final ServerConfig config) throws IOException {
        ZooKeeperServerMain.LOG.info("Starting server");
        FileTxnSnapLog txnLog = null;
        try {
            final ZooKeeperServer zkServer = new ZooKeeperServer();
            final CountDownLatch shutdownLatch = new CountDownLatch(1);
            zkServer.registerServerShutdownHandler(new ZooKeeperServerShutdownHandler(shutdownLatch));
            txnLog = new FileTxnSnapLog(new File(config.dataLogDir), new File(config.dataDir));
            txnLog.setServerStats(zkServer.serverStats());
            zkServer.setTxnLogFactory(txnLog);
            zkServer.setTickTime(config.tickTime);
            zkServer.setMinSessionTimeout(config.minSessionTimeout);
            zkServer.setMaxSessionTimeout(config.maxSessionTimeout);
            (this.cnxnFactory = ServerCnxnFactory.createFactory()).configure(config.getClientPortAddress(), config.getMaxClientCnxns());
            this.cnxnFactory.startup(zkServer);
            shutdownLatch.await();
            this.shutdown();
            this.cnxnFactory.join();
            if (zkServer.canShutdown()) {
                zkServer.shutdown(true);
            }
        }
        catch (InterruptedException e) {
            ZooKeeperServerMain.LOG.warn("Server interrupted", e);
        }
        finally {
            if (txnLog != null) {
                txnLog.close();
            }
        }
    }
    
    protected void shutdown() {
        if (this.cnxnFactory != null) {
            this.cnxnFactory.shutdown();
        }
    }
    
    ServerCnxnFactory getCnxnFactory() {
        return this.cnxnFactory;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZooKeeperServerMain.class);
    }
}
