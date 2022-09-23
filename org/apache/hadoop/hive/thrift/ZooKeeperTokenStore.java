// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.delegation.HiveDelegationTokenSupport;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.io.Writable;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.zookeeper.data.Id;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import java.io.IOException;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import java.util.Arrays;
import org.apache.zookeeper.ZooDefs;
import org.apache.hadoop.conf.Configuration;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class ZooKeeperTokenStore implements DelegationTokenStore
{
    private static final Logger LOGGER;
    protected static final String ZK_SEQ_FORMAT = "%010d";
    private static final String NODE_KEYS = "/keys";
    private static final String NODE_TOKENS = "/tokens";
    private String rootNode;
    private volatile CuratorFramework zkSession;
    private String zkConnectString;
    private int connectTimeoutMillis;
    private List<ACL> newNodeAcl;
    private final ACLProvider aclDefaultProvider;
    private HadoopThriftAuthBridge.Server.ServerMode serverMode;
    private final String WHEN_ZK_DSTORE_MSG;
    private Configuration conf;
    
    protected ZooKeeperTokenStore() {
        this.rootNode = "";
        this.newNodeAcl = Arrays.asList(new ACL(31, ZooDefs.Ids.AUTH_IDS));
        this.aclDefaultProvider = new ACLProvider() {
            @Override
            public List<ACL> getDefaultAcl() {
                return ZooKeeperTokenStore.this.newNodeAcl;
            }
            
            @Override
            public List<ACL> getAclForPath(final String path) {
                return this.getDefaultAcl();
            }
        };
        this.WHEN_ZK_DSTORE_MSG = "when zookeeper based delegation token storage is enabled(hive.cluster.delegation.token.store.class=" + ZooKeeperTokenStore.class.getName() + ")";
    }
    
    private CuratorFramework getSession() {
        if (this.zkSession == null || this.zkSession.getState() == CuratorFrameworkState.STOPPED) {
            synchronized (this) {
                if (this.zkSession == null || this.zkSession.getState() == CuratorFrameworkState.STOPPED) {
                    (this.zkSession = CuratorFrameworkFactory.builder().connectString(this.zkConnectString).connectionTimeoutMs(this.connectTimeoutMillis).aclProvider(this.aclDefaultProvider).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()).start();
                }
            }
        }
        return this.zkSession;
    }
    
    private void setupJAASConfig(final Configuration conf) throws IOException {
        if (!UserGroupInformation.getLoginUser().isFromKeytab()) {
            ZooKeeperTokenStore.LOGGER.warn("Login is not from keytab");
            return;
        }
        String principal = null;
        String keytab = null;
        switch (this.serverMode) {
            case METASTORE: {
                principal = this.getNonEmptyConfVar(conf, "hive.metastore.kerberos.principal");
                keytab = this.getNonEmptyConfVar(conf, "hive.metastore.kerberos.keytab.file");
                break;
            }
            case HIVESERVER2: {
                principal = this.getNonEmptyConfVar(conf, "hive.server2.authentication.kerberos.principal");
                keytab = this.getNonEmptyConfVar(conf, "hive.server2.authentication.kerberos.keytab");
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected server mode " + this.serverMode));
            }
        }
        Utils.setZookeeperClientKerberosJaasConfig(principal, keytab);
    }
    
    private String getNonEmptyConfVar(final Configuration conf, final String param) throws IOException {
        final String val = conf.get(param);
        if (val == null || val.trim().isEmpty()) {
            throw new IOException("Configuration parameter " + param + " should be set, " + this.WHEN_ZK_DSTORE_MSG);
        }
        return val;
    }
    
    public void ensurePath(final String path, final List<ACL> acl) throws TokenStoreException {
        try {
            final CuratorFramework zk = this.getSession();
            final String node = zk.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(acl).forPath(path);
            ZooKeeperTokenStore.LOGGER.info("Created path: {} ", node);
        }
        catch (KeeperException.NodeExistsException ex) {}
        catch (Exception e) {
            throw new TokenStoreException("Error creating path " + path, e);
        }
    }
    
    public static int getPermFromString(final String permString) {
        int perm = 0;
        for (int i = 0; i < permString.length(); ++i) {
            switch (permString.charAt(i)) {
                case 'r': {
                    perm |= 0x1;
                    break;
                }
                case 'w': {
                    perm |= 0x2;
                    break;
                }
                case 'c': {
                    perm |= 0x4;
                    break;
                }
                case 'd': {
                    perm |= 0x8;
                    break;
                }
                case 'a': {
                    perm |= 0x10;
                    break;
                }
                default: {
                    ZooKeeperTokenStore.LOGGER.error("Unknown perm type: " + permString.charAt(i));
                    break;
                }
            }
        }
        return perm;
    }
    
    public static List<ACL> parseACLs(final String aclString) {
        final String[] aclComps = StringUtils.splitByWholeSeparator(aclString, ",");
        final List<ACL> acl = new ArrayList<ACL>(aclComps.length);
        for (String a : aclComps) {
            if (!StringUtils.isBlank(a)) {
                a = a.trim();
                final int firstColon = a.indexOf(58);
                final int lastColon = a.lastIndexOf(58);
                if (firstColon == -1 || lastColon == -1 || firstColon == lastColon) {
                    ZooKeeperTokenStore.LOGGER.error(a + " does not have the form scheme:id:perm");
                }
                else {
                    final ACL newAcl = new ACL();
                    newAcl.setId(new Id(a.substring(0, firstColon), a.substring(firstColon + 1, lastColon)));
                    newAcl.setPerms(getPermFromString(a.substring(lastColon + 1)));
                    acl.add(newAcl);
                }
            }
        }
        return acl;
    }
    
    private void initClientAndPaths() {
        if (this.zkSession != null) {
            this.zkSession.close();
        }
        try {
            this.ensurePath(this.rootNode + "/keys", this.newNodeAcl);
            this.ensurePath(this.rootNode + "/tokens", this.newNodeAcl);
        }
        catch (TokenStoreException e) {
            throw e;
        }
    }
    
    @Override
    public void setConf(final Configuration conf) {
        if (conf == null) {
            throw new IllegalArgumentException("conf is null");
        }
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return null;
    }
    
    private Map<Integer, byte[]> getAllKeys() throws KeeperException, InterruptedException {
        final String masterKeyNode = this.rootNode + "/keys";
        final List<String> nodes = this.zkGetChildren(masterKeyNode);
        final Map<Integer, byte[]> result = new HashMap<Integer, byte[]>();
        for (final String node : nodes) {
            final String nodePath = masterKeyNode + "/" + node;
            final byte[] data = this.zkGetData(nodePath);
            if (data != null) {
                result.put(this.getSeq(node), data);
            }
        }
        return result;
    }
    
    private List<String> zkGetChildren(final String path) {
        final CuratorFramework zk = this.getSession();
        try {
            return zk.getChildren().forPath(path);
        }
        catch (Exception e) {
            throw new TokenStoreException("Error getting children for " + path, e);
        }
    }
    
    private byte[] zkGetData(final String nodePath) {
        final CuratorFramework zk = this.getSession();
        try {
            return zk.getData().forPath(nodePath);
        }
        catch (KeeperException.NoNodeException ex) {
            return null;
        }
        catch (Exception e) {
            throw new TokenStoreException("Error reading " + nodePath, e);
        }
    }
    
    private int getSeq(final String path) {
        final String[] pathComps = path.split("/");
        return Integer.parseInt(pathComps[pathComps.length - 1]);
    }
    
    @Override
    public int addMasterKey(final String s) {
        final String keysPath = this.rootNode + "/keys" + "/";
        final CuratorFramework zk = this.getSession();
        String newNode;
        try {
            newNode = zk.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).withACL(this.newNodeAcl).forPath(keysPath, s.getBytes());
        }
        catch (Exception e) {
            throw new TokenStoreException("Error creating new node with path " + keysPath, e);
        }
        ZooKeeperTokenStore.LOGGER.info("Added key {}", newNode);
        return this.getSeq(newNode);
    }
    
    @Override
    public void updateMasterKey(final int keySeq, final String s) {
        final CuratorFramework zk = this.getSession();
        final String keyPath = this.rootNode + "/keys" + "/" + String.format("%010d", keySeq);
        try {
            zk.setData().forPath(keyPath, s.getBytes());
        }
        catch (Exception e) {
            throw new TokenStoreException("Error setting data in " + keyPath, e);
        }
    }
    
    @Override
    public boolean removeMasterKey(final int keySeq) {
        final String keyPath = this.rootNode + "/keys" + "/" + String.format("%010d", keySeq);
        this.zkDelete(keyPath);
        return true;
    }
    
    private void zkDelete(final String path) {
        final CuratorFramework zk = this.getSession();
        try {
            zk.delete().forPath(path);
        }
        catch (KeeperException.NoNodeException ex) {}
        catch (Exception e) {
            throw new TokenStoreException("Error deleting " + path, e);
        }
    }
    
    @Override
    public String[] getMasterKeys() {
        try {
            final Map<Integer, byte[]> allKeys = this.getAllKeys();
            final String[] result = new String[allKeys.size()];
            int resultIdx = 0;
            for (final byte[] keyBytes : allKeys.values()) {
                result[resultIdx++] = new String(keyBytes);
            }
            return result;
        }
        catch (KeeperException ex) {
            throw new TokenStoreException(ex);
        }
        catch (InterruptedException ex2) {
            throw new TokenStoreException(ex2);
        }
    }
    
    private String getTokenPath(final DelegationTokenIdentifier tokenIdentifier) {
        try {
            return this.rootNode + "/tokens" + "/" + TokenStoreDelegationTokenSecretManager.encodeWritable(tokenIdentifier);
        }
        catch (IOException ex) {
            throw new TokenStoreException("Failed to encode token identifier", ex);
        }
    }
    
    @Override
    public boolean addToken(final DelegationTokenIdentifier tokenIdentifier, final AbstractDelegationTokenSecretManager.DelegationTokenInformation token) {
        final byte[] tokenBytes = HiveDelegationTokenSupport.encodeDelegationTokenInformation(token);
        final String tokenPath = this.getTokenPath(tokenIdentifier);
        final CuratorFramework zk = this.getSession();
        String newNode;
        try {
            newNode = zk.create().withMode(CreateMode.PERSISTENT).withACL(this.newNodeAcl).forPath(tokenPath, tokenBytes);
        }
        catch (Exception e) {
            throw new TokenStoreException("Error creating new node with path " + tokenPath, e);
        }
        ZooKeeperTokenStore.LOGGER.info("Added token: {}", newNode);
        return true;
    }
    
    @Override
    public boolean removeToken(final DelegationTokenIdentifier tokenIdentifier) {
        final String tokenPath = this.getTokenPath(tokenIdentifier);
        this.zkDelete(tokenPath);
        return true;
    }
    
    @Override
    public AbstractDelegationTokenSecretManager.DelegationTokenInformation getToken(final DelegationTokenIdentifier tokenIdentifier) {
        final byte[] tokenBytes = this.zkGetData(this.getTokenPath(tokenIdentifier));
        try {
            return HiveDelegationTokenSupport.decodeDelegationTokenInformation(tokenBytes);
        }
        catch (Exception ex) {
            throw new TokenStoreException("Failed to decode token", ex);
        }
    }
    
    @Override
    public List<DelegationTokenIdentifier> getAllDelegationTokenIdentifiers() {
        final String containerNode = this.rootNode + "/tokens";
        final List<String> nodes = this.zkGetChildren(containerNode);
        final List<DelegationTokenIdentifier> result = new ArrayList<DelegationTokenIdentifier>(nodes.size());
        for (final String node : nodes) {
            final DelegationTokenIdentifier id = new DelegationTokenIdentifier();
            try {
                TokenStoreDelegationTokenSecretManager.decodeWritable(id, node);
                result.add(id);
            }
            catch (Exception e) {
                ZooKeeperTokenStore.LOGGER.warn("Failed to decode token '{}'", node);
            }
        }
        return result;
    }
    
    @Override
    public void close() throws IOException {
        if (this.zkSession != null) {
            this.zkSession.close();
        }
    }
    
    @Override
    public void init(final Object objectStore, final HadoopThriftAuthBridge.Server.ServerMode smode) {
        this.serverMode = smode;
        this.zkConnectString = this.conf.get("hive.cluster.delegation.token.store.zookeeper.connectString", null);
        if (this.zkConnectString == null || this.zkConnectString.trim().isEmpty()) {
            this.zkConnectString = this.conf.get("hive.zookeeper.quorum", null);
            if (this.zkConnectString == null || this.zkConnectString.trim().isEmpty()) {
                throw new IllegalArgumentException("Zookeeper connect string has to be specifed through either hive.cluster.delegation.token.store.zookeeper.connectString or hive.zookeeper.quorum" + this.WHEN_ZK_DSTORE_MSG);
            }
        }
        this.connectTimeoutMillis = this.conf.getInt("hive.cluster.delegation.token.store.zookeeper.connectTimeoutMillis", CuratorFrameworkFactory.builder().getConnectionTimeoutMs());
        final String aclStr = this.conf.get("hive.cluster.delegation.token.store.zookeeper.acl", null);
        if (StringUtils.isNotBlank(aclStr)) {
            this.newNodeAcl = parseACLs(aclStr);
        }
        this.rootNode = this.conf.get("hive.cluster.delegation.token.store.zookeeper.znode", "/hivedelegation") + this.serverMode;
        try {
            this.setupJAASConfig(this.conf);
        }
        catch (IOException e) {
            throw new TokenStoreException("Error setting up JAAS configuration for zookeeper client " + e.getMessage(), e);
        }
        this.initClientAndPaths();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(ZooKeeperTokenStore.class.getName());
    }
}
