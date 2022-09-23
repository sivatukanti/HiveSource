// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.RetryLoop;
import org.apache.curator.utils.InternalACLProvider;
import org.apache.curator.CuratorZookeeperClient;
import java.util.concurrent.Callable;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.PathUtils;
import java.util.concurrent.atomic.AtomicBoolean;

class NamespaceImpl
{
    private final CuratorFrameworkImpl client;
    private final String namespace;
    private final AtomicBoolean ensurePathNeeded;
    
    NamespaceImpl(final CuratorFrameworkImpl client, final String namespace) {
        if (namespace != null) {
            try {
                PathUtils.validatePath("/" + namespace);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid namespace: " + namespace + ", " + e.getMessage());
            }
        }
        this.client = client;
        this.namespace = namespace;
        this.ensurePathNeeded = new AtomicBoolean(namespace != null);
    }
    
    String getNamespace() {
        return this.namespace;
    }
    
    String unfixForNamespace(String path) {
        if (this.namespace != null && path != null) {
            final String namespacePath = ZKPaths.makePath(this.namespace, null);
            if (!namespacePath.equals("/") && path.startsWith(namespacePath)) {
                path = ((path.length() > namespacePath.length()) ? path.substring(namespacePath.length()) : "/");
            }
        }
        return path;
    }
    
    String fixForNamespace(final String path, final boolean isSequential) {
        if (this.ensurePathNeeded.get()) {
            try {
                final CuratorZookeeperClient zookeeperClient = this.client.getZookeeperClient();
                RetryLoop.callWithRetry(zookeeperClient, (Callable<Object>)new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        ZKPaths.mkdirs(zookeeperClient.getZooKeeper(), ZKPaths.makePath("/", NamespaceImpl.this.namespace), true, NamespaceImpl.this.client.getAclProvider(), true);
                        return null;
                    }
                });
                this.ensurePathNeeded.set(false);
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                this.client.logError("Ensure path threw exception", e);
            }
        }
        return ZKPaths.fixForNamespace(this.namespace, path, isSequential);
    }
    
    EnsurePath newNamespaceAwareEnsurePath(final String path) {
        return new EnsurePath(this.fixForNamespace(path, false), this.client.getAclProvider());
    }
}
