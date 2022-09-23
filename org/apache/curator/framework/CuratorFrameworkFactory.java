// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework;

import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import java.util.Arrays;
import org.apache.curator.framework.imps.CuratorTempFrameworkImpl;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.utils.ZookeeperFactory;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.ensemble.EnsembleProvider;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.imps.GzipCompressionProvider;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.utils.DefaultZookeeperFactory;
import org.apache.curator.framework.api.CompressionProvider;

public class CuratorFrameworkFactory
{
    private static final int DEFAULT_SESSION_TIMEOUT_MS;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS;
    private static final byte[] LOCAL_ADDRESS;
    private static final CompressionProvider DEFAULT_COMPRESSION_PROVIDER;
    private static final DefaultZookeeperFactory DEFAULT_ZOOKEEPER_FACTORY;
    private static final DefaultACLProvider DEFAULT_ACL_PROVIDER;
    private static final long DEFAULT_INACTIVE_THRESHOLD_MS;
    private static final int DEFAULT_CLOSE_WAIT_MS;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static CuratorFramework newClient(final String connectString, final RetryPolicy retryPolicy) {
        return newClient(connectString, CuratorFrameworkFactory.DEFAULT_SESSION_TIMEOUT_MS, CuratorFrameworkFactory.DEFAULT_CONNECTION_TIMEOUT_MS, retryPolicy);
    }
    
    public static CuratorFramework newClient(final String connectString, final int sessionTimeoutMs, final int connectionTimeoutMs, final RetryPolicy retryPolicy) {
        return builder().connectString(connectString).sessionTimeoutMs(sessionTimeoutMs).connectionTimeoutMs(connectionTimeoutMs).retryPolicy(retryPolicy).build();
    }
    
    public static byte[] getLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress().getBytes();
        }
        catch (UnknownHostException ex) {
            return new byte[0];
        }
    }
    
    private CuratorFrameworkFactory() {
    }
    
    static {
        DEFAULT_SESSION_TIMEOUT_MS = Integer.getInteger("curator-default-session-timeout", 60000);
        DEFAULT_CONNECTION_TIMEOUT_MS = Integer.getInteger("curator-default-connection-timeout", 15000);
        LOCAL_ADDRESS = getLocalAddress();
        DEFAULT_COMPRESSION_PROVIDER = new GzipCompressionProvider();
        DEFAULT_ZOOKEEPER_FACTORY = new DefaultZookeeperFactory();
        DEFAULT_ACL_PROVIDER = new DefaultACLProvider();
        DEFAULT_INACTIVE_THRESHOLD_MS = (int)TimeUnit.MINUTES.toMillis(3L);
        DEFAULT_CLOSE_WAIT_MS = (int)TimeUnit.SECONDS.toMillis(1L);
    }
    
    public static class Builder
    {
        private EnsembleProvider ensembleProvider;
        private int sessionTimeoutMs;
        private int connectionTimeoutMs;
        private int maxCloseWaitMs;
        private RetryPolicy retryPolicy;
        private ThreadFactory threadFactory;
        private String namespace;
        private List<AuthInfo> authInfos;
        private byte[] defaultData;
        private CompressionProvider compressionProvider;
        private ZookeeperFactory zookeeperFactory;
        private ACLProvider aclProvider;
        private boolean canBeReadOnly;
        private boolean useContainerParentsIfAvailable;
        
        public CuratorFramework build() {
            return new CuratorFrameworkImpl(this);
        }
        
        public CuratorTempFramework buildTemp() {
            return this.buildTemp(CuratorFrameworkFactory.DEFAULT_INACTIVE_THRESHOLD_MS, TimeUnit.MILLISECONDS);
        }
        
        public CuratorTempFramework buildTemp(final long inactiveThreshold, final TimeUnit unit) {
            return new CuratorTempFrameworkImpl(this, unit.toMillis(inactiveThreshold));
        }
        
        public Builder authorization(final String scheme, final byte[] auth) {
            return this.authorization(ImmutableList.of(new AuthInfo(scheme, (byte[])((auth != null) ? Arrays.copyOf(auth, auth.length) : null))));
        }
        
        public Builder authorization(final List<AuthInfo> authInfos) {
            this.authInfos = (List<AuthInfo>)ImmutableList.copyOf((Collection<?>)authInfos);
            return this;
        }
        
        public Builder connectString(final String connectString) {
            this.ensembleProvider = new FixedEnsembleProvider(connectString);
            return this;
        }
        
        public Builder ensembleProvider(final EnsembleProvider ensembleProvider) {
            this.ensembleProvider = ensembleProvider;
            return this;
        }
        
        public Builder defaultData(final byte[] defaultData) {
            this.defaultData = (byte[])((defaultData != null) ? Arrays.copyOf(defaultData, defaultData.length) : null);
            return this;
        }
        
        public Builder namespace(final String namespace) {
            this.namespace = namespace;
            return this;
        }
        
        public Builder sessionTimeoutMs(final int sessionTimeoutMs) {
            this.sessionTimeoutMs = sessionTimeoutMs;
            return this;
        }
        
        public Builder connectionTimeoutMs(final int connectionTimeoutMs) {
            this.connectionTimeoutMs = connectionTimeoutMs;
            return this;
        }
        
        public Builder maxCloseWaitMs(final int maxCloseWaitMs) {
            this.maxCloseWaitMs = maxCloseWaitMs;
            return this;
        }
        
        public Builder retryPolicy(final RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }
        
        public Builder threadFactory(final ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }
        
        public Builder compressionProvider(final CompressionProvider compressionProvider) {
            this.compressionProvider = compressionProvider;
            return this;
        }
        
        public Builder zookeeperFactory(final ZookeeperFactory zookeeperFactory) {
            this.zookeeperFactory = zookeeperFactory;
            return this;
        }
        
        public Builder aclProvider(final ACLProvider aclProvider) {
            this.aclProvider = aclProvider;
            return this;
        }
        
        public Builder canBeReadOnly(final boolean canBeReadOnly) {
            this.canBeReadOnly = canBeReadOnly;
            return this;
        }
        
        public Builder dontUseContainerParents() {
            this.useContainerParentsIfAvailable = false;
            return this;
        }
        
        public ACLProvider getAclProvider() {
            return this.aclProvider;
        }
        
        public ZookeeperFactory getZookeeperFactory() {
            return this.zookeeperFactory;
        }
        
        public CompressionProvider getCompressionProvider() {
            return this.compressionProvider;
        }
        
        public ThreadFactory getThreadFactory() {
            return this.threadFactory;
        }
        
        public EnsembleProvider getEnsembleProvider() {
            return this.ensembleProvider;
        }
        
        public int getSessionTimeoutMs() {
            return this.sessionTimeoutMs;
        }
        
        public int getConnectionTimeoutMs() {
            return this.connectionTimeoutMs;
        }
        
        public int getMaxCloseWaitMs() {
            return this.maxCloseWaitMs;
        }
        
        public RetryPolicy getRetryPolicy() {
            return this.retryPolicy;
        }
        
        public String getNamespace() {
            return this.namespace;
        }
        
        public boolean useContainerParentsIfAvailable() {
            return this.useContainerParentsIfAvailable;
        }
        
        @Deprecated
        public String getAuthScheme() {
            final int qty = (this.authInfos != null) ? this.authInfos.size() : 0;
            switch (qty) {
                case 0: {
                    return null;
                }
                case 1: {
                    return this.authInfos.get(0).scheme;
                }
                default: {
                    throw new IllegalStateException("More than 1 auth has been added");
                }
            }
        }
        
        @Deprecated
        public byte[] getAuthValue() {
            final int qty = (this.authInfos != null) ? this.authInfos.size() : 0;
            switch (qty) {
                case 0: {
                    return null;
                }
                case 1: {
                    final byte[] bytes = this.authInfos.get(0).getAuth();
                    return (byte[])((bytes != null) ? Arrays.copyOf(bytes, bytes.length) : null);
                }
                default: {
                    throw new IllegalStateException("More than 1 auth has been added");
                }
            }
        }
        
        public List<AuthInfo> getAuthInfos() {
            return this.authInfos;
        }
        
        public byte[] getDefaultData() {
            return this.defaultData;
        }
        
        public boolean canBeReadOnly() {
            return this.canBeReadOnly;
        }
        
        private Builder() {
            this.sessionTimeoutMs = CuratorFrameworkFactory.DEFAULT_SESSION_TIMEOUT_MS;
            this.connectionTimeoutMs = CuratorFrameworkFactory.DEFAULT_CONNECTION_TIMEOUT_MS;
            this.maxCloseWaitMs = CuratorFrameworkFactory.DEFAULT_CLOSE_WAIT_MS;
            this.threadFactory = null;
            this.authInfos = null;
            this.defaultData = CuratorFrameworkFactory.LOCAL_ADDRESS;
            this.compressionProvider = CuratorFrameworkFactory.DEFAULT_COMPRESSION_PROVIDER;
            this.zookeeperFactory = CuratorFrameworkFactory.DEFAULT_ZOOKEEPER_FACTORY;
            this.aclProvider = CuratorFrameworkFactory.DEFAULT_ACL_PROVIDER;
            this.canBeReadOnly = false;
            this.useContainerParentsIfAvailable = true;
        }
    }
}
