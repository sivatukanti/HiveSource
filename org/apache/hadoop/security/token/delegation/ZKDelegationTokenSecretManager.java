// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation;

import org.apache.curator.framework.api.CreateModable;
import java.util.Collections;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.ACL;
import java.util.Map;
import java.util.HashMap;
import javax.security.auth.login.AppConfigurationEntry;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.security.token.Token;
import org.apache.zookeeper.data.Stat;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.curator.framework.recipes.shared.VersionedValue;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import java.util.concurrent.TimeUnit;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.utils.EnsurePath;
import java.util.concurrent.Executor;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import java.util.concurrent.Executors;
import java.io.IOException;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.DefaultACLProvider;
import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ExecutorService;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class ZKDelegationTokenSecretManager<TokenIdent extends AbstractDelegationTokenIdentifier> extends AbstractDelegationTokenSecretManager<TokenIdent>
{
    private static final String ZK_CONF_PREFIX = "zk-dt-secret-manager.";
    public static final String ZK_DTSM_ZK_NUM_RETRIES = "zk-dt-secret-manager.zkNumRetries";
    public static final String ZK_DTSM_ZK_SESSION_TIMEOUT = "zk-dt-secret-manager.zkSessionTimeout";
    public static final String ZK_DTSM_ZK_CONNECTION_TIMEOUT = "zk-dt-secret-manager.zkConnectionTimeout";
    public static final String ZK_DTSM_ZK_SHUTDOWN_TIMEOUT = "zk-dt-secret-manager.zkShutdownTimeout";
    public static final String ZK_DTSM_ZNODE_WORKING_PATH = "zk-dt-secret-manager.znodeWorkingPath";
    public static final String ZK_DTSM_ZK_AUTH_TYPE = "zk-dt-secret-manager.zkAuthType";
    public static final String ZK_DTSM_ZK_CONNECTION_STRING = "zk-dt-secret-manager.zkConnectionString";
    public static final String ZK_DTSM_ZK_KERBEROS_KEYTAB = "zk-dt-secret-manager.kerberos.keytab";
    public static final String ZK_DTSM_ZK_KERBEROS_PRINCIPAL = "zk-dt-secret-manager.kerberos.principal";
    public static final int ZK_DTSM_ZK_NUM_RETRIES_DEFAULT = 3;
    public static final int ZK_DTSM_ZK_SESSION_TIMEOUT_DEFAULT = 10000;
    public static final int ZK_DTSM_ZK_CONNECTION_TIMEOUT_DEFAULT = 10000;
    public static final int ZK_DTSM_ZK_SHUTDOWN_TIMEOUT_DEFAULT = 10000;
    public static final String ZK_DTSM_ZNODE_WORKING_PATH_DEAFULT = "zkdtsm";
    private static Logger LOG;
    private static final String JAAS_LOGIN_ENTRY_NAME = "ZKDelegationTokenSecretManagerClient";
    private static final String ZK_DTSM_NAMESPACE = "ZKDTSMRoot";
    private static final String ZK_DTSM_SEQNUM_ROOT = "/ZKDTSMSeqNumRoot";
    private static final String ZK_DTSM_KEYID_ROOT = "/ZKDTSMKeyIdRoot";
    private static final String ZK_DTSM_TOKENS_ROOT = "/ZKDTSMTokensRoot";
    private static final String ZK_DTSM_MASTER_KEY_ROOT = "/ZKDTSMMasterKeyRoot";
    private static final String DELEGATION_KEY_PREFIX = "DK_";
    private static final String DELEGATION_TOKEN_PREFIX = "DT_";
    private static final ThreadLocal<CuratorFramework> CURATOR_TL;
    private final boolean isExternalClient;
    private final CuratorFramework zkClient;
    private SharedCount delTokSeqCounter;
    private SharedCount keyIdSeqCounter;
    private PathChildrenCache keyCache;
    private PathChildrenCache tokenCache;
    private ExecutorService listenerThreadPool;
    private final long shutdownTimeout;
    
    public static void setCurator(final CuratorFramework curator) {
        ZKDelegationTokenSecretManager.CURATOR_TL.set(curator);
    }
    
    public ZKDelegationTokenSecretManager(final Configuration conf) {
        super(conf.getLong("delegation-token.update-interval.sec", 86400L) * 1000L, conf.getLong("delegation-token.max-lifetime.sec", 604800L) * 1000L, conf.getLong("delegation-token.renew-interval.sec", 86400L) * 1000L, conf.getLong("delegation-token.removal-scan-interval.sec", 3600L) * 1000L);
        this.shutdownTimeout = conf.getLong("zk-dt-secret-manager.zkShutdownTimeout", 10000L);
        if (ZKDelegationTokenSecretManager.CURATOR_TL.get() != null) {
            this.zkClient = ZKDelegationTokenSecretManager.CURATOR_TL.get().usingNamespace(conf.get("zk-dt-secret-manager.znodeWorkingPath", "zkdtsm") + "/" + "ZKDTSMRoot");
            this.isExternalClient = true;
        }
        else {
            final String connString = conf.get("zk-dt-secret-manager.zkConnectionString");
            Preconditions.checkNotNull(connString, (Object)"Zookeeper connection string cannot be null");
            final String authType = conf.get("zk-dt-secret-manager.zkAuthType");
            Preconditions.checkNotNull(authType, (Object)"Zookeeper authType cannot be null !!");
            Preconditions.checkArgument(authType.equals("sasl") || authType.equals("none"), (Object)"Zookeeper authType must be one of [none, sasl]");
            CuratorFrameworkFactory.Builder builder = null;
            try {
                ACLProvider aclProvider = null;
                if (authType.equals("sasl")) {
                    ZKDelegationTokenSecretManager.LOG.info("Connecting to ZooKeeper with SASL/Kerberosand using 'sasl' ACLs");
                    final String principal = this.setJaasConfiguration(conf);
                    System.setProperty("zookeeper.sasl.clientconfig", "ZKDelegationTokenSecretManagerClient");
                    System.setProperty("zookeeper.authProvider.1", "org.apache.zookeeper.server.auth.SASLAuthenticationProvider");
                    aclProvider = new SASLOwnerACLProvider(principal);
                }
                else {
                    ZKDelegationTokenSecretManager.LOG.info("Connecting to ZooKeeper without authentication");
                    aclProvider = new DefaultACLProvider();
                }
                final int sessionT = conf.getInt("zk-dt-secret-manager.zkSessionTimeout", 10000);
                final int numRetries = conf.getInt("zk-dt-secret-manager.zkNumRetries", 3);
                builder = CuratorFrameworkFactory.builder().aclProvider(aclProvider).namespace(conf.get("zk-dt-secret-manager.znodeWorkingPath", "zkdtsm") + "/" + "ZKDTSMRoot").sessionTimeoutMs(sessionT).connectionTimeoutMs(conf.getInt("zk-dt-secret-manager.zkConnectionTimeout", 10000)).retryPolicy(new RetryNTimes(numRetries, sessionT / numRetries));
            }
            catch (Exception ex) {
                throw new RuntimeException("Could not Load ZK acls or auth");
            }
            this.zkClient = builder.ensembleProvider(new FixedEnsembleProvider(connString)).build();
            this.isExternalClient = false;
        }
    }
    
    private String setJaasConfiguration(final Configuration config) throws Exception {
        final String keytabFile = config.get("zk-dt-secret-manager.kerberos.keytab", "").trim();
        if (keytabFile == null || keytabFile.length() == 0) {
            throw new IllegalArgumentException("zk-dt-secret-manager.kerberos.keytab must be specified");
        }
        final String principal = config.get("zk-dt-secret-manager.kerberos.principal", "").trim();
        if (principal == null || principal.length() == 0) {
            throw new IllegalArgumentException("zk-dt-secret-manager.kerberos.principal must be specified");
        }
        final JaasConfiguration jConf = new JaasConfiguration("ZKDelegationTokenSecretManagerClient", principal, keytabFile);
        javax.security.auth.login.Configuration.setConfiguration(jConf);
        return principal.split("[/@]")[0];
    }
    
    @Override
    public void startThreads() throws IOException {
        Label_0101: {
            if (!this.isExternalClient) {
                try {
                    this.zkClient.start();
                    break Label_0101;
                }
                catch (Exception e) {
                    throw new IOException("Could not start Curator Framework", e);
                }
            }
            final CuratorFramework nullNsFw = this.zkClient.usingNamespace(null);
            final EnsurePath ensureNs = nullNsFw.newNamespaceAwareEnsurePath("/" + this.zkClient.getNamespace());
            try {
                ensureNs.ensure(nullNsFw.getZookeeperClient());
            }
            catch (Exception e2) {
                throw new IOException("Could not create namespace", e2);
            }
        }
        this.listenerThreadPool = Executors.newSingleThreadExecutor();
        try {
            this.delTokSeqCounter = new SharedCount(this.zkClient, "/ZKDTSMSeqNumRoot", 0);
            if (this.delTokSeqCounter != null) {
                this.delTokSeqCounter.start();
            }
        }
        catch (Exception e) {
            throw new IOException("Could not start Sequence Counter", e);
        }
        try {
            this.keyIdSeqCounter = new SharedCount(this.zkClient, "/ZKDTSMKeyIdRoot", 0);
            if (this.keyIdSeqCounter != null) {
                this.keyIdSeqCounter.start();
            }
        }
        catch (Exception e) {
            throw new IOException("Could not start KeyId Counter", e);
        }
        try {
            this.createPersistentNode("/ZKDTSMMasterKeyRoot");
            this.createPersistentNode("/ZKDTSMTokensRoot");
        }
        catch (Exception e) {
            throw new RuntimeException("Could not create ZK paths");
        }
        try {
            this.keyCache = new PathChildrenCache(this.zkClient, "/ZKDTSMMasterKeyRoot", true);
            if (this.keyCache != null) {
                this.keyCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
                this.keyCache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(final CuratorFramework client, final PathChildrenCacheEvent event) throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED: {
                                ZKDelegationTokenSecretManager.this.processKeyAddOrUpdate(event.getData().getData());
                                break;
                            }
                            case CHILD_UPDATED: {
                                ZKDelegationTokenSecretManager.this.processKeyAddOrUpdate(event.getData().getData());
                                break;
                            }
                            case CHILD_REMOVED: {
                                ZKDelegationTokenSecretManager.this.processKeyRemoved(event.getData().getPath());
                                break;
                            }
                        }
                    }
                }, this.listenerThreadPool);
                this.loadFromZKCache(false);
            }
        }
        catch (Exception e) {
            throw new IOException("Could not start PathChildrenCache for keys", e);
        }
        try {
            this.tokenCache = new PathChildrenCache(this.zkClient, "/ZKDTSMTokensRoot", true);
            if (this.tokenCache != null) {
                this.tokenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
                this.tokenCache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(final CuratorFramework client, final PathChildrenCacheEvent event) throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED: {
                                ZKDelegationTokenSecretManager.this.processTokenAddOrUpdate(event.getData());
                                break;
                            }
                            case CHILD_UPDATED: {
                                ZKDelegationTokenSecretManager.this.processTokenAddOrUpdate(event.getData());
                                break;
                            }
                            case CHILD_REMOVED: {
                                ZKDelegationTokenSecretManager.this.processTokenRemoved(event.getData());
                                break;
                            }
                        }
                    }
                }, this.listenerThreadPool);
                this.loadFromZKCache(true);
            }
        }
        catch (Exception e) {
            throw new IOException("Could not start PathChildrenCache for tokens", e);
        }
        super.startThreads();
    }
    
    private void loadFromZKCache(final boolean isTokenCache) {
        final String cacheName = isTokenCache ? "token" : "key";
        ZKDelegationTokenSecretManager.LOG.info("Starting to load {} cache.", cacheName);
        List<ChildData> children;
        if (isTokenCache) {
            children = this.tokenCache.getCurrentData();
        }
        else {
            children = this.keyCache.getCurrentData();
        }
        int count = 0;
        for (final ChildData child : children) {
            try {
                if (isTokenCache) {
                    this.processTokenAddOrUpdate(child);
                }
                else {
                    this.processKeyAddOrUpdate(child.getData());
                }
            }
            catch (Exception e) {
                ZKDelegationTokenSecretManager.LOG.info("Ignoring node {} because it failed to load.", child.getPath());
                ZKDelegationTokenSecretManager.LOG.debug("Failure exception:", e);
                ++count;
            }
        }
        if (count > 0) {
            ZKDelegationTokenSecretManager.LOG.warn("Ignored {} nodes while loading {} cache.", (Object)count, cacheName);
        }
        ZKDelegationTokenSecretManager.LOG.info("Loaded {} cache.", cacheName);
    }
    
    private void processKeyAddOrUpdate(final byte[] data) throws IOException {
        final ByteArrayInputStream bin = new ByteArrayInputStream(data);
        final DataInputStream din = new DataInputStream(bin);
        final DelegationKey key = new DelegationKey();
        key.readFields(din);
        synchronized (this) {
            this.allKeys.put(key.getKeyId(), key);
        }
    }
    
    private void processKeyRemoved(final String path) {
        final int i = path.lastIndexOf(47);
        if (i > 0) {
            final String tokSeg = path.substring(i + 1);
            final int j = tokSeg.indexOf(95);
            if (j > 0) {
                final int keyId = Integer.parseInt(tokSeg.substring(j + 1));
                synchronized (this) {
                    this.allKeys.remove(keyId);
                }
            }
        }
    }
    
    private void processTokenAddOrUpdate(final ChildData data) throws IOException {
        final ByteArrayInputStream bin = new ByteArrayInputStream(data.getData());
        final DataInputStream din = new DataInputStream(bin);
        final TokenIdent ident = this.createIdentifier();
        ident.readFields(din);
        final long renewDate = din.readLong();
        final int pwdLen = din.readInt();
        final byte[] password = new byte[pwdLen];
        final int numRead = din.read(password, 0, pwdLen);
        if (numRead > -1) {
            final DelegationTokenInformation tokenInfo = new DelegationTokenInformation(renewDate, password);
            synchronized (this) {
                this.currentTokens.put(ident, tokenInfo);
                this.notifyAll();
            }
        }
    }
    
    private void processTokenRemoved(final ChildData data) throws IOException {
        final ByteArrayInputStream bin = new ByteArrayInputStream(data.getData());
        final DataInputStream din = new DataInputStream(bin);
        final TokenIdent ident = this.createIdentifier();
        ident.readFields(din);
        synchronized (this) {
            this.currentTokens.remove(ident);
            this.notifyAll();
        }
    }
    
    @Override
    public void stopThreads() {
        super.stopThreads();
        try {
            if (this.tokenCache != null) {
                this.tokenCache.close();
            }
        }
        catch (Exception e) {
            ZKDelegationTokenSecretManager.LOG.error("Could not stop Delegation Token Cache", e);
        }
        try {
            if (this.delTokSeqCounter != null) {
                this.delTokSeqCounter.close();
            }
        }
        catch (Exception e) {
            ZKDelegationTokenSecretManager.LOG.error("Could not stop Delegation Token Counter", e);
        }
        try {
            if (this.keyIdSeqCounter != null) {
                this.keyIdSeqCounter.close();
            }
        }
        catch (Exception e) {
            ZKDelegationTokenSecretManager.LOG.error("Could not stop Key Id Counter", e);
        }
        try {
            if (this.keyCache != null) {
                this.keyCache.close();
            }
        }
        catch (Exception e) {
            ZKDelegationTokenSecretManager.LOG.error("Could not stop KeyCache", e);
        }
        try {
            if (!this.isExternalClient && this.zkClient != null) {
                this.zkClient.close();
            }
        }
        catch (Exception e) {
            ZKDelegationTokenSecretManager.LOG.error("Could not stop Curator Framework", e);
        }
        if (this.listenerThreadPool != null) {
            this.listenerThreadPool.shutdown();
            try {
                if (!this.listenerThreadPool.awaitTermination(this.shutdownTimeout, TimeUnit.MILLISECONDS)) {
                    ZKDelegationTokenSecretManager.LOG.error("Forcing Listener threadPool to shutdown !!");
                    this.listenerThreadPool.shutdownNow();
                }
            }
            catch (InterruptedException ie) {
                this.listenerThreadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void createPersistentNode(final String nodePath) throws Exception {
        try {
            ((CreateModable<ACLBackgroundPathAndBytesable>)this.zkClient.create()).withMode(CreateMode.PERSISTENT).forPath(nodePath);
        }
        catch (KeeperException.NodeExistsException ne) {
            ZKDelegationTokenSecretManager.LOG.debug(nodePath + " znode already exists !!");
        }
        catch (Exception e) {
            throw new IOException(nodePath + " znode could not be created !!", e);
        }
    }
    
    @Override
    protected int getDelegationTokenSeqNum() {
        return this.delTokSeqCounter.getCount();
    }
    
    private void incrSharedCount(final SharedCount sharedCount) throws Exception {
        VersionedValue<Integer> versionedValue;
        do {
            versionedValue = sharedCount.getVersionedValue();
        } while (!sharedCount.trySetCount(versionedValue, versionedValue.getValue() + 1));
    }
    
    @Override
    protected int incrementDelegationTokenSeqNum() {
        try {
            this.incrSharedCount(this.delTokSeqCounter);
        }
        catch (InterruptedException e) {
            ZKDelegationTokenSecretManager.LOG.debug("Thread interrupted while performing token counter increment", e);
            Thread.currentThread().interrupt();
        }
        catch (Exception e2) {
            throw new RuntimeException("Could not increment shared counter !!", e2);
        }
        return this.delTokSeqCounter.getCount();
    }
    
    @Override
    protected void setDelegationTokenSeqNum(final int seqNum) {
        try {
            this.delTokSeqCounter.setCount(seqNum);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not set shared counter !!", e);
        }
    }
    
    @Override
    protected int getCurrentKeyId() {
        return this.keyIdSeqCounter.getCount();
    }
    
    @Override
    protected int incrementCurrentKeyId() {
        try {
            this.incrSharedCount(this.keyIdSeqCounter);
        }
        catch (InterruptedException e) {
            ZKDelegationTokenSecretManager.LOG.debug("Thread interrupted while performing keyId increment", e);
            Thread.currentThread().interrupt();
        }
        catch (Exception e2) {
            throw new RuntimeException("Could not increment shared keyId counter !!", e2);
        }
        return this.keyIdSeqCounter.getCount();
    }
    
    @Override
    protected DelegationKey getDelegationKey(final int keyId) {
        DelegationKey key = this.allKeys.get(keyId);
        if (key == null) {
            try {
                key = this.getKeyFromZK(keyId);
                if (key != null) {
                    this.allKeys.put(keyId, key);
                }
            }
            catch (IOException e) {
                ZKDelegationTokenSecretManager.LOG.error("Error retrieving key [" + keyId + "] from ZK", e);
            }
        }
        return key;
    }
    
    private DelegationKey getKeyFromZK(final int keyId) throws IOException {
        final String nodePath = getNodePath("/ZKDTSMMasterKeyRoot", "DK_" + keyId);
        try {
            final byte[] data = this.zkClient.getData().forPath(nodePath);
            if (data == null || data.length == 0) {
                return null;
            }
            final ByteArrayInputStream bin = new ByteArrayInputStream(data);
            final DataInputStream din = new DataInputStream(bin);
            final DelegationKey key = new DelegationKey();
            key.readFields(din);
            return key;
        }
        catch (KeeperException.NoNodeException e) {
            ZKDelegationTokenSecretManager.LOG.error("No node in path [" + nodePath + "]");
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
        return null;
    }
    
    @Override
    protected DelegationTokenInformation getTokenInfo(final TokenIdent ident) {
        DelegationTokenInformation tokenInfo = this.currentTokens.get(ident);
        if (tokenInfo == null) {
            try {
                tokenInfo = this.getTokenInfoFromZK(ident);
                if (tokenInfo != null) {
                    this.currentTokens.put(ident, tokenInfo);
                }
            }
            catch (IOException e) {
                ZKDelegationTokenSecretManager.LOG.error("Error retrieving tokenInfo [" + ident.getSequenceNumber() + "] from ZK", e);
            }
        }
        return tokenInfo;
    }
    
    private synchronized void syncLocalCacheWithZk(final TokenIdent ident) {
        try {
            final DelegationTokenInformation tokenInfo = this.getTokenInfoFromZK(ident);
            if (tokenInfo != null && !this.currentTokens.containsKey(ident)) {
                this.currentTokens.put(ident, tokenInfo);
            }
            else if (tokenInfo == null && this.currentTokens.containsKey(ident)) {
                this.currentTokens.remove(ident);
            }
        }
        catch (IOException e) {
            ZKDelegationTokenSecretManager.LOG.error("Error retrieving tokenInfo [" + ident.getSequenceNumber() + "] from ZK", e);
        }
    }
    
    private DelegationTokenInformation getTokenInfoFromZK(final TokenIdent ident) throws IOException {
        return this.getTokenInfoFromZK(ident, false);
    }
    
    private DelegationTokenInformation getTokenInfoFromZK(final TokenIdent ident, final boolean quiet) throws IOException {
        final String nodePath = getNodePath("/ZKDTSMTokensRoot", "DT_" + ident.getSequenceNumber());
        try {
            final byte[] data = this.zkClient.getData().forPath(nodePath);
            if (data == null || data.length == 0) {
                return null;
            }
            final ByteArrayInputStream bin = new ByteArrayInputStream(data);
            final DataInputStream din = new DataInputStream(bin);
            this.createIdentifier().readFields(din);
            final long renewDate = din.readLong();
            final int pwdLen = din.readInt();
            final byte[] password = new byte[pwdLen];
            final int numRead = din.read(password, 0, pwdLen);
            if (numRead > -1) {
                final DelegationTokenInformation tokenInfo = new DelegationTokenInformation(renewDate, password);
                return tokenInfo;
            }
        }
        catch (KeeperException.NoNodeException e) {
            if (!quiet) {
                ZKDelegationTokenSecretManager.LOG.error("No node in path [" + nodePath + "]");
            }
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
        return null;
    }
    
    @Override
    protected void storeDelegationKey(final DelegationKey key) throws IOException {
        this.addOrUpdateDelegationKey(key, false);
    }
    
    @Override
    protected void updateDelegationKey(final DelegationKey key) throws IOException {
        this.addOrUpdateDelegationKey(key, true);
    }
    
    private void addOrUpdateDelegationKey(final DelegationKey key, final boolean isUpdate) throws IOException {
        final String nodeCreatePath = getNodePath("/ZKDTSMMasterKeyRoot", "DK_" + key.getKeyId());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final DataOutputStream fsOut = new DataOutputStream(os);
        if (ZKDelegationTokenSecretManager.LOG.isDebugEnabled()) {
            ZKDelegationTokenSecretManager.LOG.debug("Storing ZKDTSMDelegationKey_" + key.getKeyId());
        }
        key.write(fsOut);
        try {
            if (this.zkClient.checkExists().forPath(nodeCreatePath) != null) {
                this.zkClient.setData().forPath(nodeCreatePath, os.toByteArray()).setVersion(-1);
                if (!isUpdate) {
                    ZKDelegationTokenSecretManager.LOG.debug("Key with path [" + nodeCreatePath + "] already exists.. Updating !!");
                }
            }
            else {
                ((CreateModable<ACLBackgroundPathAndBytesable>)this.zkClient.create()).withMode(CreateMode.PERSISTENT).forPath(nodeCreatePath, os.toByteArray());
                if (isUpdate) {
                    ZKDelegationTokenSecretManager.LOG.debug("Updating non existent Key path [" + nodeCreatePath + "].. Adding new !!");
                }
            }
        }
        catch (KeeperException.NodeExistsException ne) {
            ZKDelegationTokenSecretManager.LOG.debug(nodeCreatePath + " znode already exists !!");
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
        finally {
            os.close();
        }
    }
    
    @Override
    protected void removeStoredMasterKey(final DelegationKey key) {
        final String nodeRemovePath = getNodePath("/ZKDTSMMasterKeyRoot", "DK_" + key.getKeyId());
        if (ZKDelegationTokenSecretManager.LOG.isDebugEnabled()) {
            ZKDelegationTokenSecretManager.LOG.debug("Removing ZKDTSMDelegationKey_" + key.getKeyId());
        }
        try {
            if (this.zkClient.checkExists().forPath(nodeRemovePath) != null) {
                while (this.zkClient.checkExists().forPath(nodeRemovePath) != null) {
                    try {
                        this.zkClient.delete().guaranteed().forPath(nodeRemovePath);
                    }
                    catch (KeeperException.NoNodeException nne) {
                        ZKDelegationTokenSecretManager.LOG.debug("Node already deleted by peer " + nodeRemovePath);
                    }
                }
            }
            else {
                ZKDelegationTokenSecretManager.LOG.debug("Attempted to delete a non-existing znode " + nodeRemovePath);
            }
        }
        catch (Exception e) {
            ZKDelegationTokenSecretManager.LOG.debug(nodeRemovePath + " znode could not be removed!!");
        }
    }
    
    @Override
    protected void storeToken(final TokenIdent ident, final DelegationTokenInformation tokenInfo) throws IOException {
        try {
            this.addOrUpdateToken(ident, tokenInfo, false);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void updateToken(final TokenIdent ident, final DelegationTokenInformation tokenInfo) throws IOException {
        final String nodeRemovePath = getNodePath("/ZKDTSMTokensRoot", "DT_" + ident.getSequenceNumber());
        try {
            if (this.zkClient.checkExists().forPath(nodeRemovePath) != null) {
                this.addOrUpdateToken(ident, tokenInfo, true);
            }
            else {
                this.addOrUpdateToken(ident, tokenInfo, false);
                ZKDelegationTokenSecretManager.LOG.debug("Attempted to update a non-existing znode " + nodeRemovePath);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not update Stored Token ZKDTSMDelegationToken_" + ident.getSequenceNumber(), e);
        }
    }
    
    @Override
    protected void removeStoredToken(final TokenIdent ident) throws IOException {
        final String nodeRemovePath = getNodePath("/ZKDTSMTokensRoot", "DT_" + ident.getSequenceNumber());
        if (ZKDelegationTokenSecretManager.LOG.isDebugEnabled()) {
            ZKDelegationTokenSecretManager.LOG.debug("Removing ZKDTSMDelegationToken_" + ident.getSequenceNumber());
        }
        try {
            if (this.zkClient.checkExists().forPath(nodeRemovePath) != null) {
                while (this.zkClient.checkExists().forPath(nodeRemovePath) != null) {
                    try {
                        this.zkClient.delete().guaranteed().forPath(nodeRemovePath);
                    }
                    catch (KeeperException.NoNodeException nne) {
                        ZKDelegationTokenSecretManager.LOG.debug("Node already deleted by peer " + nodeRemovePath);
                    }
                }
            }
            else {
                ZKDelegationTokenSecretManager.LOG.debug("Attempted to remove a non-existing znode " + nodeRemovePath);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Could not remove Stored Token ZKDTSMDelegationToken_" + ident.getSequenceNumber(), e);
        }
    }
    
    @Override
    public synchronized TokenIdent cancelToken(final Token<TokenIdent> token, final String canceller) throws IOException {
        final ByteArrayInputStream buf = new ByteArrayInputStream(token.getIdentifier());
        final DataInputStream in = new DataInputStream(buf);
        final TokenIdent id = this.createIdentifier();
        id.readFields(in);
        this.syncLocalCacheWithZk(id);
        return super.cancelToken(token, canceller);
    }
    
    private void addOrUpdateToken(final TokenIdent ident, final DelegationTokenInformation info, final boolean isUpdate) throws Exception {
        final String nodeCreatePath = getNodePath("/ZKDTSMTokensRoot", "DT_" + ident.getSequenceNumber());
        try (final ByteArrayOutputStream tokenOs = new ByteArrayOutputStream();
             final DataOutputStream tokenOut = new DataOutputStream(tokenOs)) {
            ident.write(tokenOut);
            tokenOut.writeLong(info.getRenewDate());
            tokenOut.writeInt(info.getPassword().length);
            tokenOut.write(info.getPassword());
            if (ZKDelegationTokenSecretManager.LOG.isDebugEnabled()) {
                ZKDelegationTokenSecretManager.LOG.debug((isUpdate ? "Updating " : "Storing ") + "ZKDTSMDelegationToken_" + ident.getSequenceNumber());
            }
            if (isUpdate) {
                this.zkClient.setData().forPath(nodeCreatePath, tokenOs.toByteArray()).setVersion(-1);
            }
            else {
                ((CreateModable<ACLBackgroundPathAndBytesable>)this.zkClient.create()).withMode(CreateMode.PERSISTENT).forPath(nodeCreatePath, tokenOs.toByteArray());
            }
        }
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    static String getNodePath(final String root, final String nodeName) {
        return root + "/" + nodeName;
    }
    
    @VisibleForTesting
    public ExecutorService getListenerThreadPool() {
        return this.listenerThreadPool;
    }
    
    @VisibleForTesting
    DelegationTokenInformation getTokenInfoFromMemory(final TokenIdent ident) {
        return this.currentTokens.get(ident);
    }
    
    static {
        ZKDelegationTokenSecretManager.LOG = LoggerFactory.getLogger(ZKDelegationTokenSecretManager.class);
        CURATOR_TL = new ThreadLocal<CuratorFramework>();
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
}
