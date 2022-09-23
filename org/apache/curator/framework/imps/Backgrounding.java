// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.zookeeper.KeeperException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.shaded.com.google.common.base.Throwables;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.BackgroundCallback;

class Backgrounding
{
    private final boolean inBackground;
    private final Object context;
    private final BackgroundCallback callback;
    private final UnhandledErrorListener errorListener;
    
    Backgrounding(final Object context) {
        this.inBackground = true;
        this.context = context;
        this.callback = null;
        this.errorListener = null;
    }
    
    Backgrounding(final BackgroundCallback callback) {
        this.inBackground = true;
        this.context = null;
        this.callback = callback;
        this.errorListener = null;
    }
    
    Backgrounding(final boolean inBackground) {
        this.inBackground = inBackground;
        this.context = null;
        this.callback = null;
        this.errorListener = null;
    }
    
    Backgrounding(final BackgroundCallback callback, final Object context) {
        this.inBackground = true;
        this.context = context;
        this.callback = callback;
        this.errorListener = null;
    }
    
    Backgrounding(final CuratorFrameworkImpl client, final BackgroundCallback callback, final Object context, final Executor executor) {
        this(wrapCallback(client, callback, executor), context);
    }
    
    Backgrounding(final CuratorFrameworkImpl client, final BackgroundCallback callback, final Executor executor) {
        this(wrapCallback(client, callback, executor));
    }
    
    Backgrounding(Backgrounding rhs, final UnhandledErrorListener errorListener) {
        if (rhs == null) {
            rhs = new Backgrounding();
        }
        this.inBackground = rhs.inBackground;
        this.context = rhs.context;
        this.callback = rhs.callback;
        this.errorListener = errorListener;
    }
    
    Backgrounding() {
        this.inBackground = false;
        this.context = null;
        this.callback = null;
        this.errorListener = null;
    }
    
    boolean inBackground() {
        return this.inBackground;
    }
    
    Object getContext() {
        return this.context;
    }
    
    BackgroundCallback getCallback() {
        return this.callback;
    }
    
    void checkError(final Throwable e) throws Exception {
        if (e != null) {
            if (this.errorListener != null) {
                this.errorListener.unhandledError("n/a", e);
            }
            else {
                if (e instanceof Exception) {
                    throw (Exception)e;
                }
                Throwables.propagate(e);
            }
        }
    }
    
    private static BackgroundCallback wrapCallback(final CuratorFrameworkImpl client, final BackgroundCallback callback, final Executor executor) {
        return new BackgroundCallback() {
            @Override
            public void processResult(final CuratorFramework dummy, final CuratorEvent event) throws Exception {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.processResult(client, event);
                        }
                        catch (Exception e) {
                            ThreadUtils.checkInterrupted(e);
                            if (e instanceof KeeperException) {
                                client.validateConnection(client.codeToState(((KeeperException)e).code()));
                            }
                            client.logError("Background operation result handling threw exception", e);
                        }
                    }
                });
            }
        };
    }
}
