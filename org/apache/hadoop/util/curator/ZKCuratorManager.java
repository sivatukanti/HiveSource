// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.curator;

import org.apache.curator.framework.api.CreateModable;
import org.apache.curator.framework.api.Versionable;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.ACLPathAndBytesable;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import java.nio.charset.Charset;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.zookeeper.data.Stat;
import java.util.Iterator;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.framework.AuthInfo;
import java.util.ArrayList;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.util.ZKUtil;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class ZKCuratorManager
{
    private static final Logger LOG;
    private final Configuration conf;
    private CuratorFramework curator;
    
    public ZKCuratorManager(final Configuration config) throws IOException {
        this.conf = config;
    }
    
    public CuratorFramework getCurator() {
        return this.curator;
    }
    
    public void close() {
        if (this.curator != null) {
            this.curator.close();
        }
    }
    
    public static List<ACL> getZKAcls(final Configuration conf) throws IOException {
        String zkAclConf = conf.get("hadoop.zk.acl", "world:anyone:rwcda");
        try {
            zkAclConf = ZKUtil.resolveConfIndirection(zkAclConf);
            return ZKUtil.parseACLs(zkAclConf);
        }
        catch (IOException | ZKUtil.BadAclFormatException ex2) {
            final Exception ex;
            final Exception e = ex;
            ZKCuratorManager.LOG.error("Couldn't read ACLs based on {}", "hadoop.zk.acl");
            throw e;
        }
    }
    
    public static List<ZKUtil.ZKAuthInfo> getZKAuths(final Configuration conf) throws IOException {
        return SecurityUtil.getZKAuthInfos(conf, "hadoop.zk.auth");
    }
    
    public void start() throws IOException {
        this.start(new ArrayList<AuthInfo>());
    }
    
    public void start(List<AuthInfo> authInfos) throws IOException {
        final String zkHostPort = this.conf.get("hadoop.zk.address");
        if (zkHostPort == null) {
            throw new IOException("hadoop.zk.address is not configured.");
        }
        final int numRetries = this.conf.getInt("hadoop.zk.num-retries", 1000);
        final int zkSessionTimeout = this.conf.getInt("hadoop.zk.timeout-ms", 10000);
        final int zkRetryInterval = this.conf.getInt("hadoop.zk.retry-interval-ms", 1000);
        final RetryNTimes retryPolicy = new RetryNTimes(numRetries, zkRetryInterval);
        final List<ZKUtil.ZKAuthInfo> zkAuths = getZKAuths(this.conf);
        if (authInfos == null) {
            authInfos = new ArrayList<AuthInfo>();
        }
        for (final ZKUtil.ZKAuthInfo zkAuth : zkAuths) {
            authInfos.add(new AuthInfo(zkAuth.getScheme(), zkAuth.getAuth()));
        }
        final CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkHostPort).sessionTimeoutMs(zkSessionTimeout).retryPolicy(retryPolicy).authorization(authInfos).build();
        client.start();
        this.curator = client;
    }
    
    public List<ACL> getACL(final String path) throws Exception {
        return this.curator.getACL().forPath(path);
    }
    
    public byte[] getData(final String path) throws Exception {
        return this.curator.getData().forPath(path);
    }
    
    public byte[] getData(final String path, final Stat stat) throws Exception {
        return this.curator.getData().storingStatIn(stat).forPath(path);
    }
    
    public String getStringData(final String path) throws Exception {
        final byte[] bytes = this.getData(path);
        if (bytes != null) {
            return new String(bytes, Charset.forName("UTF-8"));
        }
        return null;
    }
    
    public String getStringData(final String path, final Stat stat) throws Exception {
        final byte[] bytes = this.getData(path, stat);
        if (bytes != null) {
            return new String(bytes, Charset.forName("UTF-8"));
        }
        return null;
    }
    
    public void setData(final String path, final byte[] data, final int version) throws Exception {
        ((Versionable<BackgroundPathAndBytesable>)this.curator.setData()).withVersion(version).forPath(path, data);
    }
    
    public void setData(final String path, final String data, final int version) throws Exception {
        final byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
        this.setData(path, bytes, version);
    }
    
    public List<String> getChildren(final String path) throws Exception {
        return this.curator.getChildren().forPath(path);
    }
    
    public boolean exists(final String path) throws Exception {
        return this.curator.checkExists().forPath(path) != null;
    }
    
    public boolean create(final String path) throws Exception {
        return this.create(path, null);
    }
    
    public boolean create(final String path, final List<ACL> zkAcl) throws Exception {
        boolean created = false;
        if (!this.exists(path)) {
            ((BackgroundPathAndBytesable)((CreateModable<ACLBackgroundPathAndBytesable>)this.curator.create()).withMode(CreateMode.PERSISTENT).withACL(zkAcl)).forPath(path, null);
            created = true;
        }
        return created;
    }
    
    public void createRootDirRecursively(final String path) throws Exception {
        this.createRootDirRecursively(path, null);
    }
    
    public void createRootDirRecursively(final String path, final List<ACL> zkAcl) throws Exception {
        final String[] pathParts = path.split("/");
        Preconditions.checkArgument(pathParts.length >= 1 && pathParts[0].isEmpty(), "Invalid path: %s", path);
        final StringBuilder sb = new StringBuilder();
        for (int i = 1; i < pathParts.length; ++i) {
            sb.append("/").append(pathParts[i]);
            this.create(sb.toString(), zkAcl);
        }
    }
    
    public boolean delete(final String path) throws Exception {
        if (this.exists(path)) {
            this.curator.delete().deletingChildrenIfNeeded().forPath(path);
            return true;
        }
        return false;
    }
    
    public static String getNodePath(final String root, final String nodeName) {
        return root + "/" + nodeName;
    }
    
    public void safeCreate(final String path, final byte[] data, final List<ACL> acl, final CreateMode mode, final List<ACL> fencingACL, final String fencingNodePath) throws Exception {
        if (!this.exists(path)) {
            final SafeTransaction transaction = this.createTransaction(fencingACL, fencingNodePath);
            transaction.create(path, data, acl, mode);
            transaction.commit();
        }
    }
    
    public void safeDelete(final String path, final List<ACL> fencingACL, final String fencingNodePath) throws Exception {
        if (this.exists(path)) {
            final SafeTransaction transaction = this.createTransaction(fencingACL, fencingNodePath);
            transaction.delete(path);
            transaction.commit();
        }
    }
    
    public void safeSetData(final String path, final byte[] data, final int version, final List<ACL> fencingACL, final String fencingNodePath) throws Exception {
        final SafeTransaction transaction = this.createTransaction(fencingACL, fencingNodePath);
        transaction.setData(path, data, version);
        transaction.commit();
    }
    
    public SafeTransaction createTransaction(final List<ACL> fencingACL, final String fencingNodePath) throws Exception {
        return new SafeTransaction(fencingACL, fencingNodePath);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZKCuratorManager.class);
    }
    
    public class SafeTransaction
    {
        private CuratorTransactionFinal transactionFinal;
        private String fencingNodePath;
        
        SafeTransaction(final List<ACL> fencingACL, final String fencingNodePath) throws Exception {
            this.fencingNodePath = fencingNodePath;
            final CuratorTransaction transaction = ZKCuratorManager.this.curator.inTransaction();
            this.transactionFinal = transaction.create().withMode(CreateMode.PERSISTENT).withACL(fencingACL).forPath(fencingNodePath, new byte[0]).and();
        }
        
        public void commit() throws Exception {
            (this.transactionFinal = this.transactionFinal.delete().forPath(this.fencingNodePath).and()).commit();
        }
        
        public void create(final String path, final byte[] data, final List<ACL> acl, final CreateMode mode) throws Exception {
            this.transactionFinal = this.transactionFinal.create().withMode(mode).withACL(acl).forPath(path, data).and();
        }
        
        public void delete(final String path) throws Exception {
            this.transactionFinal = this.transactionFinal.delete().forPath(path).and();
        }
        
        public void setData(final String path, final byte[] data, final int version) throws Exception {
            this.transactionFinal = this.transactionFinal.setData().withVersion(version).forPath(path, data).and();
        }
    }
}
