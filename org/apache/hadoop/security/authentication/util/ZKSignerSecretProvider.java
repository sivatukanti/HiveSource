// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import org.apache.curator.framework.api.Versionable;
import java.util.Map;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.Collections;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.slf4j.LoggerFactory;
import javax.security.auth.login.Configuration;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.zookeeper.data.Stat;
import java.nio.ByteBuffer;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import org.apache.zookeeper.KeeperException;
import javax.servlet.ServletContext;
import java.util.Properties;
import com.google.common.annotations.VisibleForTesting;
import java.security.SecureRandom;
import org.apache.curator.framework.CuratorFramework;
import java.util.Random;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public class ZKSignerSecretProvider extends RolloverSignerSecretProvider
{
    private static final String CONFIG_PREFIX = "signer.secret.provider.zookeeper.";
    public static final String ZOOKEEPER_CONNECTION_STRING = "signer.secret.provider.zookeeper.connection.string";
    public static final String ZOOKEEPER_PATH = "signer.secret.provider.zookeeper.path";
    public static final String ZOOKEEPER_AUTH_TYPE = "signer.secret.provider.zookeeper.auth.type";
    public static final String ZOOKEEPER_KERBEROS_KEYTAB = "signer.secret.provider.zookeeper.kerberos.keytab";
    public static final String ZOOKEEPER_KERBEROS_PRINCIPAL = "signer.secret.provider.zookeeper.kerberos.principal";
    public static final String DISCONNECT_FROM_ZOOKEEPER_ON_SHUTDOWN = "signer.secret.provider.zookeeper.disconnect.on.shutdown";
    public static final String ZOOKEEPER_SIGNER_SECRET_PROVIDER_CURATOR_CLIENT_ATTRIBUTE = "signer.secret.provider.zookeeper.curator.client";
    private static final String JAAS_LOGIN_ENTRY_NAME = "ZKSignerSecretProviderClient";
    private static Logger LOG;
    private String path;
    private volatile byte[] nextSecret;
    private final Random rand;
    private int zkVersion;
    private long nextRolloverDate;
    private long tokenValidity;
    private CuratorFramework client;
    private boolean shouldDisconnect;
    private static int INT_BYTES;
    private static int LONG_BYTES;
    private static int DATA_VERSION;
    
    public ZKSignerSecretProvider() {
        this.rand = new SecureRandom();
    }
    
    @VisibleForTesting
    public ZKSignerSecretProvider(final long seed) {
        this.rand = new Random(seed);
    }
    
    @Override
    public void init(final Properties config, final ServletContext servletContext, final long tokenValidity) throws Exception {
        final Object curatorClientObj = servletContext.getAttribute("signer.secret.provider.zookeeper.curator.client");
        if (curatorClientObj != null && curatorClientObj instanceof CuratorFramework) {
            this.client = (CuratorFramework)curatorClientObj;
        }
        else {
            servletContext.setAttribute("signer.secret.provider.zookeeper.curator.client", this.client = this.createCuratorClient(config));
        }
        this.tokenValidity = tokenValidity;
        this.shouldDisconnect = Boolean.parseBoolean(config.getProperty("signer.secret.provider.zookeeper.disconnect.on.shutdown", "true"));
        this.path = config.getProperty("signer.secret.provider.zookeeper.path");
        if (this.path == null) {
            throw new IllegalArgumentException("signer.secret.provider.zookeeper.path must be specified");
        }
        try {
            this.nextRolloverDate = System.currentTimeMillis() + tokenValidity;
            this.client.create().creatingParentsIfNeeded().forPath(this.path, this.generateZKData(this.generateRandomSecret(), this.generateRandomSecret(), null));
            this.zkVersion = 0;
            ZKSignerSecretProvider.LOG.info("Creating secret znode");
        }
        catch (KeeperException.NodeExistsException nee) {
            ZKSignerSecretProvider.LOG.info("The secret znode already exists, retrieving data");
        }
        this.pullFromZK(true);
        long initialDelay = this.nextRolloverDate - System.currentTimeMillis();
        if (initialDelay < 1L) {
            for (int i = 1; initialDelay < 1L; initialDelay = this.nextRolloverDate + tokenValidity * i - System.currentTimeMillis(), ++i) {}
        }
        super.startScheduler(initialDelay, tokenValidity);
    }
    
    @Override
    public void destroy() {
        if (this.shouldDisconnect && this.client != null) {
            this.client.close();
        }
        super.destroy();
    }
    
    @Override
    protected synchronized void rollSecret() {
        super.rollSecret();
        this.nextRolloverDate += this.tokenValidity;
        final byte[][] secrets = super.getAllSecrets();
        this.pushToZK(this.generateRandomSecret(), secrets[0], secrets[1]);
        this.pullFromZK(false);
    }
    
    @Override
    protected byte[] generateNewSecret() {
        return this.nextSecret;
    }
    
    private synchronized void pushToZK(final byte[] newSecret, final byte[] currentSecret, final byte[] previousSecret) {
        final byte[] bytes = this.generateZKData(newSecret, currentSecret, previousSecret);
        try {
            ((Versionable<BackgroundPathAndBytesable>)this.client.setData()).withVersion(this.zkVersion).forPath(this.path, bytes);
        }
        catch (KeeperException.BadVersionException bve) {
            ZKSignerSecretProvider.LOG.debug("Unable to push to znode; another server already did it");
        }
        catch (Exception ex) {
            ZKSignerSecretProvider.LOG.error("An unexpected exception occurred pushing data to ZooKeeper", ex);
        }
    }
    
    private synchronized byte[] generateZKData(final byte[] newSecret, final byte[] currentSecret, final byte[] previousSecret) {
        final int newSecretLength = newSecret.length;
        final int currentSecretLength = currentSecret.length;
        int previousSecretLength = 0;
        if (previousSecret != null) {
            previousSecretLength = previousSecret.length;
        }
        final ByteBuffer bb = ByteBuffer.allocate(ZKSignerSecretProvider.INT_BYTES + ZKSignerSecretProvider.INT_BYTES + newSecretLength + ZKSignerSecretProvider.INT_BYTES + currentSecretLength + ZKSignerSecretProvider.INT_BYTES + previousSecretLength + ZKSignerSecretProvider.LONG_BYTES);
        bb.putInt(ZKSignerSecretProvider.DATA_VERSION);
        bb.putInt(newSecretLength);
        bb.put(newSecret);
        bb.putInt(currentSecretLength);
        bb.put(currentSecret);
        bb.putInt(previousSecretLength);
        if (previousSecretLength > 0) {
            bb.put(previousSecret);
        }
        bb.putLong(this.nextRolloverDate);
        return bb.array();
    }
    
    private synchronized void pullFromZK(final boolean isInit) {
        try {
            final Stat stat = new Stat();
            final byte[] bytes = this.client.getData().storingStatIn(stat).forPath(this.path);
            final ByteBuffer bb = ByteBuffer.wrap(bytes);
            final int dataVersion = bb.getInt();
            if (dataVersion > ZKSignerSecretProvider.DATA_VERSION) {
                throw new IllegalStateException("Cannot load data from ZooKeeper; itwas written with a newer version");
            }
            final int nextSecretLength = bb.getInt();
            final byte[] nextSecret = new byte[nextSecretLength];
            bb.get(nextSecret);
            this.nextSecret = nextSecret;
            this.zkVersion = stat.getVersion();
            if (isInit) {
                final int currentSecretLength = bb.getInt();
                final byte[] currentSecret = new byte[currentSecretLength];
                bb.get(currentSecret);
                final int previousSecretLength = bb.getInt();
                byte[] previousSecret = null;
                if (previousSecretLength > 0) {
                    previousSecret = new byte[previousSecretLength];
                    bb.get(previousSecret);
                }
                super.initSecrets(currentSecret, previousSecret);
                this.nextRolloverDate = bb.getLong();
            }
        }
        catch (Exception ex) {
            ZKSignerSecretProvider.LOG.error("An unexpected exception occurred while pulling data fromZooKeeper", ex);
        }
    }
    
    @VisibleForTesting
    protected byte[] generateRandomSecret() {
        final byte[] secret = new byte[32];
        this.rand.nextBytes(secret);
        return secret;
    }
    
    protected CuratorFramework createCuratorClient(final Properties config) throws Exception {
        final String connectionString = config.getProperty("signer.secret.provider.zookeeper.connection.string", "localhost:2181");
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        final String authType = config.getProperty("signer.secret.provider.zookeeper.auth.type", "none");
        ACLProvider aclProvider;
        if (authType.equals("sasl")) {
            ZKSignerSecretProvider.LOG.info("Connecting to ZooKeeper with SASL/Kerberosand using 'sasl' ACLs");
            final String principal = this.setJaasConfiguration(config);
            System.setProperty("zookeeper.sasl.clientconfig", "ZKSignerSecretProviderClient");
            System.setProperty("zookeeper.authProvider.1", "org.apache.zookeeper.server.auth.SASLAuthenticationProvider");
            aclProvider = new SASLOwnerACLProvider(principal);
        }
        else {
            ZKSignerSecretProvider.LOG.info("Connecting to ZooKeeper without authentication");
            aclProvider = new DefaultACLProvider();
        }
        final CuratorFramework cf = CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(retryPolicy).aclProvider(aclProvider).build();
        cf.start();
        return cf;
    }
    
    private String setJaasConfiguration(final Properties config) throws Exception {
        final String keytabFile = config.getProperty("signer.secret.provider.zookeeper.kerberos.keytab").trim();
        if (keytabFile == null || keytabFile.length() == 0) {
            throw new IllegalArgumentException("signer.secret.provider.zookeeper.kerberos.keytab must be specified");
        }
        final String principal = config.getProperty("signer.secret.provider.zookeeper.kerberos.principal").trim();
        if (principal == null || principal.length() == 0) {
            throw new IllegalArgumentException("signer.secret.provider.zookeeper.kerberos.principal must be specified");
        }
        final JaasConfiguration jConf = new JaasConfiguration("ZKSignerSecretProviderClient", principal, keytabFile);
        Configuration.setConfiguration(jConf);
        return principal.split("[/@]")[0];
    }
    
    static {
        ZKSignerSecretProvider.LOG = LoggerFactory.getLogger(ZKSignerSecretProvider.class);
        ZKSignerSecretProvider.INT_BYTES = 4;
        ZKSignerSecretProvider.LONG_BYTES = 8;
        ZKSignerSecretProvider.DATA_VERSION = 0;
    }
    
    private static class SASLOwnerACLProvider implements ACLProvider
    {
        private final List<ACL> saslACL;
        
        private SASLOwnerACLProvider(final String principal) {
            this.saslACL = Collections.singletonList(new ACL(31, new Id("sasl", principal)));
        }
        
        @Override
        public List<ACL> getDefaultAcl() {
            return this.saslACL;
        }
        
        @Override
        public List<ACL> getAclForPath(final String path) {
            return this.saslACL;
        }
    }
    
    @InterfaceAudience.Private
    public static class JaasConfiguration extends Configuration
    {
        private final Configuration baseConfig;
        private static AppConfigurationEntry[] entry;
        private String entryName;
        
        public JaasConfiguration(final String entryName, final String principal, final String keytab) {
            this.baseConfig = Configuration.getConfiguration();
            this.entryName = entryName;
            final Map<String, String> options = new HashMap<String, String>();
            options.put("keyTab", keytab);
            options.put("principal", principal);
            options.put("useKeyTab", "true");
            options.put("storeKey", "true");
            options.put("useTicketCache", "false");
            options.put("refreshKrb5Config", "true");
            final String jaasEnvVar = System.getenv("HADOOP_JAAS_DEBUG");
            if (jaasEnvVar != null && "true".equalsIgnoreCase(jaasEnvVar)) {
                options.put("debug", "true");
            }
            JaasConfiguration.entry = new AppConfigurationEntry[] { new AppConfigurationEntry(this.getKrb5LoginModuleName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
            return (AppConfigurationEntry[])(this.entryName.equals(name) ? JaasConfiguration.entry : ((this.baseConfig != null) ? this.baseConfig.getAppConfigurationEntry(name) : null));
        }
        
        private String getKrb5LoginModuleName() {
            String krb5LoginModuleName;
            if (System.getProperty("java.vendor").contains("IBM")) {
                krb5LoginModuleName = "com.ibm.security.auth.module.Krb5LoginModule";
            }
            else {
                krb5LoginModuleName = "com.sun.security.auth.module.Krb5LoginModule";
            }
            return krb5LoginModuleName;
        }
    }
}
