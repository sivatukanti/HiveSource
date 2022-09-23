// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework;

import java.util.concurrent.atomic.AtomicBoolean;

public class EnsureContainers
{
    private final CuratorFramework client;
    private final String path;
    private final AtomicBoolean ensureNeeded;
    
    public EnsureContainers(final CuratorFramework client, final String path) {
        this.ensureNeeded = new AtomicBoolean(true);
        this.client = client;
        this.path = path;
    }
    
    public void ensure() throws Exception {
        if (this.ensureNeeded.get()) {
            this.internalEnsure();
        }
    }
    
    private synchronized void internalEnsure() throws Exception {
        if (this.ensureNeeded.compareAndSet(true, false)) {
            this.client.createContainers(this.path);
        }
    }
}
