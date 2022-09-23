// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.server;

import org.apache.curator.framework.api.Backgroundable;
import org.apache.curator.framework.api.Watchable;
import org.apache.commons.cli.HelpFormatter;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.WatchedEvent;
import org.apache.commons.logging.LogFactory;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.hadoop.hive.common.LogUtils;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.Pathable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.hive.ql.exec.spark.session.SparkSessionManagerImpl;
import org.apache.hadoop.hive.ql.exec.tez.TezSessionPoolManager;
import org.apache.hadoop.hive.shims.Utils;
import java.io.IOException;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.hive.common.util.HiveVersionInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.CuratorFrameworkFactory;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import org.apache.hadoop.hive.ql.util.ZooKeeperHiveHelper;
import org.apache.hive.service.cli.thrift.ThriftBinaryCLIService;
import org.apache.hive.service.cli.thrift.ThriftHttpCLIService;
import org.apache.hive.service.Service;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.Collection;
import org.apache.zookeeper.ZooDefs;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.ArrayList;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.hive.service.cli.thrift.ThriftCLIService;
import org.apache.hive.service.cli.CLIService;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.logging.Log;
import org.apache.hive.service.CompositeService;

public class HiveServer2 extends CompositeService
{
    private static final Log LOG;
    private static CountDownLatch deleteSignal;
    private CLIService cliService;
    private ThriftCLIService thriftCLIService;
    private PersistentEphemeralNode znode;
    private String znodePath;
    private CuratorFramework zooKeeperClient;
    private boolean registeredWithZooKeeper;
    private final ACLProvider zooKeeperAclProvider;
    
    public HiveServer2() {
        super(HiveServer2.class.getSimpleName());
        this.registeredWithZooKeeper = false;
        this.zooKeeperAclProvider = new ACLProvider() {
            List<ACL> nodeAcls = new ArrayList<ACL>();
            
            @Override
            public List<ACL> getDefaultAcl() {
                if (UserGroupInformation.isSecurityEnabled()) {
                    this.nodeAcls.addAll(ZooDefs.Ids.READ_ACL_UNSAFE);
                    this.nodeAcls.add(new ACL(31, ZooDefs.Ids.AUTH_IDS));
                }
                else {
                    this.nodeAcls.addAll(ZooDefs.Ids.OPEN_ACL_UNSAFE);
                }
                return this.nodeAcls;
            }
            
            @Override
            public List<ACL> getAclForPath(final String path) {
                return this.getDefaultAcl();
            }
        };
        HiveConf.setLoadHiveServer2Config(true);
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        this.addService(this.cliService = new CLIService(this));
        if (isHTTPTransportMode(hiveConf)) {
            this.thriftCLIService = new ThriftHttpCLIService(this.cliService);
        }
        else {
            this.thriftCLIService = new ThriftBinaryCLIService(this.cliService);
        }
        this.addService(this.thriftCLIService);
        super.init(hiveConf);
        final HiveServer2 hiveServer2 = this;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hiveServer2.stop();
            }
        });
    }
    
    public static boolean isHTTPTransportMode(final HiveConf hiveConf) {
        String transportMode = System.getenv("HIVE_SERVER2_TRANSPORT_MODE");
        if (transportMode == null) {
            transportMode = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_TRANSPORT_MODE);
        }
        return transportMode != null && transportMode.equalsIgnoreCase("http");
    }
    
    private void addServerInstanceToZooKeeper(final HiveConf hiveConf) throws Exception {
        final String zooKeeperEnsemble = ZooKeeperHiveHelper.getQuorumServers(hiveConf);
        final String rootNamespace = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_ZOOKEEPER_NAMESPACE);
        final String instanceURI = this.getServerInstanceURI(hiveConf);
        final byte[] znodeDataUTF8 = instanceURI.getBytes(Charset.forName("UTF-8"));
        this.setUpZooKeeperAuth(hiveConf);
        final int sessionTimeout = (int)hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_ZOOKEEPER_SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        final int baseSleepTime = (int)hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_ZOOKEEPER_CONNECTION_BASESLEEPTIME, TimeUnit.MILLISECONDS);
        final int maxRetries = hiveConf.getIntVar(HiveConf.ConfVars.HIVE_ZOOKEEPER_CONNECTION_MAX_RETRIES);
        (this.zooKeeperClient = CuratorFrameworkFactory.builder().connectString(zooKeeperEnsemble).sessionTimeoutMs(sessionTimeout).aclProvider(this.zooKeeperAclProvider).retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries)).build()).start();
        try {
            this.zooKeeperClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + rootNamespace);
            HiveServer2.LOG.info("Created the root name space: " + rootNamespace + " on ZooKeeper for HiveServer2");
        }
        catch (KeeperException e) {
            if (e.code() != KeeperException.Code.NODEEXISTS) {
                HiveServer2.LOG.fatal("Unable to create HiveServer2 namespace: " + rootNamespace + " on ZooKeeper", e);
                throw e;
            }
        }
        try {
            final String pathPrefix = "/" + rootNamespace + "/" + "serverUri=" + instanceURI + ";" + "version=" + HiveVersionInfo.getVersion() + ";" + "sequence=";
            (this.znode = new PersistentEphemeralNode(this.zooKeeperClient, PersistentEphemeralNode.Mode.EPHEMERAL_SEQUENTIAL, pathPrefix, znodeDataUTF8)).start();
            final long znodeCreationTimeout = 120L;
            if (!this.znode.waitForInitialCreate(znodeCreationTimeout, TimeUnit.SECONDS)) {
                throw new Exception("Max znode creation wait time: " + znodeCreationTimeout + "s exhausted");
            }
            this.setRegisteredWithZooKeeper(true);
            this.znodePath = this.znode.getActualPath();
            if (((Watchable<BackgroundPathable>)this.zooKeeperClient.checkExists()).usingWatcher(new DeRegisterWatcher()).forPath(this.znodePath) == null) {
                throw new Exception("Unable to create znode for this HiveServer2 instance on ZooKeeper.");
            }
            HiveServer2.LOG.info("Created a znode on ZooKeeper for HiveServer2 uri: " + instanceURI);
        }
        catch (Exception e2) {
            HiveServer2.LOG.fatal("Unable to create a znode for this server instance", e2);
            if (this.znode != null) {
                this.znode.close();
            }
            throw e2;
        }
    }
    
    private void setUpZooKeeperAuth(final HiveConf hiveConf) throws Exception {
        if (UserGroupInformation.isSecurityEnabled()) {
            final String principal = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_KERBEROS_PRINCIPAL);
            if (principal.isEmpty()) {
                throw new IOException("HiveServer2 Kerberos principal is empty");
            }
            final String keyTabFile = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_KERBEROS_KEYTAB);
            if (keyTabFile.isEmpty()) {
                throw new IOException("HiveServer2 Kerberos keytab is empty");
            }
            Utils.setZookeeperClientKerberosJaasConfig(principal, keyTabFile);
        }
    }
    
    private void removeServerInstanceFromZooKeeper() throws Exception {
        this.setRegisteredWithZooKeeper(false);
        if (this.znode != null) {
            this.znode.close();
        }
        this.zooKeeperClient.close();
        HiveServer2.LOG.info("Server instance removed from ZooKeeper.");
    }
    
    public boolean isRegisteredWithZooKeeper() {
        return this.registeredWithZooKeeper;
    }
    
    private void setRegisteredWithZooKeeper(final boolean registeredWithZooKeeper) {
        this.registeredWithZooKeeper = registeredWithZooKeeper;
    }
    
    private String getServerInstanceURI(final HiveConf hiveConf) throws Exception {
        if (this.thriftCLIService == null || this.thriftCLIService.getServerIPAddress() == null) {
            throw new Exception("Unable to get the server address; it hasn't been initialized yet.");
        }
        return this.thriftCLIService.getServerIPAddress().getHostName() + ":" + this.thriftCLIService.getPortNumber();
    }
    
    @Override
    public synchronized void start() {
        super.start();
    }
    
    @Override
    public synchronized void stop() {
        HiveServer2.LOG.info("Shutting down HiveServer2");
        final HiveConf hiveConf = this.getHiveConf();
        super.stop();
        if (hiveConf != null && hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_SUPPORT_DYNAMIC_SERVICE_DISCOVERY)) {
            try {
                this.removeServerInstanceFromZooKeeper();
            }
            catch (Exception e) {
                HiveServer2.LOG.error("Error removing znode for this HiveServer2 instance from ZooKeeper.", e);
            }
        }
        if (hiveConf != null && hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_TEZ_INITIALIZE_DEFAULT_SESSIONS)) {
            try {
                TezSessionPoolManager.getInstance().stop();
            }
            catch (Exception e) {
                HiveServer2.LOG.error("Tez session pool manager stop had an error during stop of HiveServer2. Shutting down HiveServer2 anyway.", e);
            }
        }
        if (hiveConf != null && hiveConf.getVar(HiveConf.ConfVars.HIVE_EXECUTION_ENGINE).equals("spark")) {
            try {
                SparkSessionManagerImpl.getInstance().shutdown();
            }
            catch (Exception ex) {
                HiveServer2.LOG.error("Spark session pool manager failed to stop during HiveServer2 shutdown.", ex);
            }
        }
    }
    
    private static void startHiveServer2() throws Throwable {
        long attempts = 0L;
        long maxAttempts = 1L;
        while (true) {
            HiveServer2.LOG.info("Starting HiveServer2");
            final HiveConf hiveConf = new HiveConf();
            maxAttempts = hiveConf.getLongVar(HiveConf.ConfVars.HIVE_SERVER2_MAX_START_ATTEMPTS);
            HiveServer2 server = null;
            try {
                server = new HiveServer2();
                server.init(hiveConf);
                server.start();
                ShimLoader.getHadoopShims().startPauseMonitor(hiveConf);
                if (hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_SUPPORT_DYNAMIC_SERVICE_DISCOVERY)) {
                    server.addServerInstanceToZooKeeper(hiveConf);
                }
                if (hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_TEZ_INITIALIZE_DEFAULT_SESSIONS)) {
                    final TezSessionPoolManager sessionPool = TezSessionPoolManager.getInstance();
                    sessionPool.setupPool(hiveConf);
                    sessionPool.startPool();
                }
                if (hiveConf.getVar(HiveConf.ConfVars.HIVE_EXECUTION_ENGINE).equals("spark")) {
                    SparkSessionManagerImpl.getInstance().setup(hiveConf);
                }
            }
            catch (Throwable throwable) {
                if (server != null) {
                    try {
                        server.stop();
                    }
                    catch (Throwable t) {
                        HiveServer2.LOG.info("Exception caught when calling stop of HiveServer2 before retrying start", t);
                    }
                    finally {
                        server = null;
                    }
                }
                if (++attempts >= maxAttempts) {
                    throw new Error("Max start attempts " + maxAttempts + " exhausted", throwable);
                }
                HiveServer2.LOG.warn("Error starting HiveServer2 on attempt " + attempts + ", will retry in 60 seconds", throwable);
                try {
                    Thread.sleep(60000L);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            break;
        }
    }
    
    static void deleteServerInstancesFromZooKeeper(final String versionNumber) throws Exception {
        final HiveConf hiveConf = new HiveConf();
        final String zooKeeperEnsemble = ZooKeeperHiveHelper.getQuorumServers(hiveConf);
        final String rootNamespace = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_ZOOKEEPER_NAMESPACE);
        final int baseSleepTime = (int)hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_ZOOKEEPER_CONNECTION_BASESLEEPTIME, TimeUnit.MILLISECONDS);
        final int maxRetries = hiveConf.getIntVar(HiveConf.ConfVars.HIVE_ZOOKEEPER_CONNECTION_MAX_RETRIES);
        final CuratorFramework zooKeeperClient = CuratorFrameworkFactory.builder().connectString(zooKeeperEnsemble).retryPolicy(new ExponentialBackoffRetry(baseSleepTime, maxRetries)).build();
        zooKeeperClient.start();
        final List<String> znodePaths = zooKeeperClient.getChildren().forPath("/" + rootNamespace);
        for (int i = 0; i < znodePaths.size(); ++i) {
            final String znodePath = znodePaths.get(i);
            HiveServer2.deleteSignal = new CountDownLatch(1);
            if (znodePath.contains("version=" + versionNumber + ";")) {
                final String fullZnodePath = "/" + rootNamespace + "/" + znodePath;
                HiveServer2.LOG.warn("Will attempt to remove the znode: " + fullZnodePath + " from ZooKeeper");
                System.out.println("Will attempt to remove the znode: " + fullZnodePath + " from ZooKeeper");
                ((Backgroundable<Pathable>)zooKeeperClient.delete().guaranteed()).inBackground(new DeleteCallBack()).forPath(fullZnodePath);
                HiveServer2.deleteSignal.await();
                final List<String> znodePathsUpdated = zooKeeperClient.getChildren().forPath("/" + rootNamespace);
                znodePathsUpdated.removeAll(znodePaths);
                znodePaths.addAll(znodePathsUpdated);
            }
        }
        zooKeeperClient.close();
    }
    
    public static void main(final String[] args) {
        HiveConf.setLoadHiveServer2Config(true);
        try {
            final ServerOptionsProcessor oproc = new ServerOptionsProcessor("hiveserver2");
            final ServerOptionsProcessorResponse oprocResponse = oproc.parse(args);
            final String initLog4jMessage = LogUtils.initHiveLog4j();
            HiveServer2.LOG.debug(initLog4jMessage);
            HiveStringUtils.startupShutdownMessage(HiveServer2.class, args, HiveServer2.LOG);
            HiveServer2.LOG.debug(oproc.getDebugMessage().toString());
            oprocResponse.getServerOptionsExecutor().execute();
        }
        catch (LogUtils.LogInitializationException e) {
            HiveServer2.LOG.error("Error initializing log: " + e.getMessage(), e);
            System.exit(-1);
        }
    }
    
    static {
        LOG = LogFactory.getLog(HiveServer2.class);
    }
    
    private class DeRegisterWatcher implements Watcher
    {
        @Override
        public void process(final WatchedEvent event) {
            if (event.getType().equals(Event.EventType.NodeDeleted) && HiveServer2.this.znode != null) {
                try {
                    HiveServer2.this.znode.close();
                    HiveServer2.LOG.warn("This HiveServer2 instance is now de-registered from ZooKeeper. The server will be shut down after the last client sesssion completes.");
                }
                catch (IOException e) {
                    HiveServer2.LOG.error("Failed to close the persistent ephemeral znode", e);
                }
                finally {
                    HiveServer2.this.setRegisteredWithZooKeeper(false);
                    if (HiveServer2.this.cliService.getSessionManager().getOpenSessionCount() == 0) {
                        HiveServer2.LOG.warn("This instance of HiveServer2 has been removed from the list of server instances available for dynamic service discovery. The last client session has ended - will shutdown now.");
                        HiveServer2.this.stop();
                    }
                }
            }
        }
    }
    
    private static class DeleteCallBack implements BackgroundCallback
    {
        @Override
        public void processResult(final CuratorFramework zooKeeperClient, final CuratorEvent event) throws Exception {
            if (event.getType() == CuratorEventType.DELETE) {
                HiveServer2.deleteSignal.countDown();
            }
        }
    }
    
    static class ServerOptionsProcessor
    {
        private final Options options;
        private CommandLine commandLine;
        private final String serverName;
        private final StringBuilder debugMessage;
        
        ServerOptionsProcessor(final String serverName) {
            this.options = new Options();
            this.debugMessage = new StringBuilder();
            this.serverName = serverName;
            final Options options = this.options;
            OptionBuilder.withValueSeparator();
            OptionBuilder.hasArgs(2);
            OptionBuilder.withArgName("property=value");
            OptionBuilder.withLongOpt("hiveconf");
            OptionBuilder.withDescription("Use value for given property");
            options.addOption(OptionBuilder.create());
            final Options options2 = this.options;
            OptionBuilder.hasArgs(1);
            OptionBuilder.withArgName("versionNumber");
            OptionBuilder.withLongOpt("deregister");
            OptionBuilder.withDescription("Deregister all instances of given version from dynamic service discovery");
            options2.addOption(OptionBuilder.create());
            this.options.addOption(new Option("H", "help", false, "Print help information"));
        }
        
        ServerOptionsProcessorResponse parse(final String[] argv) {
            try {
                this.commandLine = new GnuParser().parse(this.options, argv);
                final Properties confProps = this.commandLine.getOptionProperties("hiveconf");
                for (final String propKey : confProps.stringPropertyNames()) {
                    this.debugMessage.append("Setting " + propKey + "=" + confProps.getProperty(propKey) + ";\n");
                    System.setProperty(propKey, confProps.getProperty(propKey));
                }
                if (this.commandLine.hasOption('H')) {
                    return new ServerOptionsProcessorResponse(new HelpOptionExecutor(this.serverName, this.options));
                }
                if (this.commandLine.hasOption("deregister")) {
                    return new ServerOptionsProcessorResponse(new DeregisterOptionExecutor(this.commandLine.getOptionValue("deregister")));
                }
            }
            catch (ParseException e) {
                System.err.println("Error starting HiveServer2 with given arguments: ");
                System.err.println(e.getMessage());
                System.exit(-1);
            }
            return new ServerOptionsProcessorResponse(new StartOptionExecutor());
        }
        
        StringBuilder getDebugMessage() {
            return this.debugMessage;
        }
    }
    
    static class ServerOptionsProcessorResponse
    {
        private final ServerOptionsExecutor serverOptionsExecutor;
        
        ServerOptionsProcessorResponse(final ServerOptionsExecutor serverOptionsExecutor) {
            this.serverOptionsExecutor = serverOptionsExecutor;
        }
        
        ServerOptionsExecutor getServerOptionsExecutor() {
            return this.serverOptionsExecutor;
        }
    }
    
    static class HelpOptionExecutor implements ServerOptionsExecutor
    {
        private final Options options;
        private final String serverName;
        
        HelpOptionExecutor(final String serverName, final Options options) {
            this.options = options;
            this.serverName = serverName;
        }
        
        @Override
        public void execute() {
            new HelpFormatter().printHelp(this.serverName, this.options);
            System.exit(0);
        }
    }
    
    static class StartOptionExecutor implements ServerOptionsExecutor
    {
        @Override
        public void execute() {
            try {
                startHiveServer2();
            }
            catch (Throwable t) {
                HiveServer2.LOG.fatal("Error starting HiveServer2", t);
                System.exit(-1);
            }
        }
    }
    
    static class DeregisterOptionExecutor implements ServerOptionsExecutor
    {
        private final String versionNumber;
        
        DeregisterOptionExecutor(final String versionNumber) {
            this.versionNumber = versionNumber;
        }
        
        @Override
        public void execute() {
            try {
                HiveServer2.deleteServerInstancesFromZooKeeper(this.versionNumber);
            }
            catch (Exception e) {
                HiveServer2.LOG.fatal("Error deregistering HiveServer2 instances for version: " + this.versionNumber + " from ZooKeeper", e);
                System.out.println("Error deregistering HiveServer2 instances for version: " + this.versionNumber + " from ZooKeeper." + e);
                System.exit(-1);
            }
            System.exit(0);
        }
    }
    
    interface ServerOptionsExecutor
    {
        void execute();
    }
}
