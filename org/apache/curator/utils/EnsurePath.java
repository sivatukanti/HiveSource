// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.curator.CuratorZookeeperClient;
import java.util.concurrent.atomic.AtomicReference;

@Deprecated
public class EnsurePath
{
    private final String path;
    private final boolean makeLastNode;
    private final InternalACLProvider aclProvider;
    private final AtomicReference<Helper> helper;
    private static final Helper doNothingHelper;
    
    public EnsurePath(final String path) {
        this(path, null, true, null);
    }
    
    public EnsurePath(final String path, final InternalACLProvider aclProvider) {
        this(path, null, true, aclProvider);
    }
    
    public void ensure(final CuratorZookeeperClient client) throws Exception {
        final Helper localHelper = this.helper.get();
        localHelper.ensure(client, this.path, this.makeLastNode);
    }
    
    public EnsurePath excludingLast() {
        return new EnsurePath(this.path, this.helper, false, this.aclProvider);
    }
    
    protected EnsurePath(final String path, final AtomicReference<Helper> helper, final boolean makeLastNode, final InternalACLProvider aclProvider) {
        this.path = path;
        this.makeLastNode = makeLastNode;
        this.aclProvider = aclProvider;
        this.helper = ((helper != null) ? helper : new AtomicReference<Helper>(new InitialHelper()));
    }
    
    public String getPath() {
        return this.path;
    }
    
    protected boolean asContainers() {
        return false;
    }
    
    static {
        doNothingHelper = new Helper() {
            @Override
            public void ensure(final CuratorZookeeperClient client, final String path, final boolean makeLastNode) throws Exception {
            }
        };
    }
    
    private class InitialHelper implements Helper
    {
        private boolean isSet;
        
        private InitialHelper() {
            this.isSet = false;
        }
        
        @Override
        public synchronized void ensure(final CuratorZookeeperClient client, final String path, final boolean makeLastNode) throws Exception {
            if (!this.isSet) {
                RetryLoop.callWithRetry(client, (Callable<Object>)new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        ZKPaths.mkdirs(client.getZooKeeper(), path, makeLastNode, EnsurePath.this.aclProvider, EnsurePath.this.asContainers());
                        EnsurePath.this.helper.set(EnsurePath.doNothingHelper);
                        InitialHelper.this.isSet = true;
                        return null;
                    }
                });
            }
        }
    }
    
    interface Helper
    {
        void ensure(final CuratorZookeeperClient p0, final String p1, final boolean p2) throws Exception;
    }
}
