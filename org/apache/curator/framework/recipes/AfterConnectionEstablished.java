// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes;

import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import org.apache.curator.utils.ThreadUtils;
import java.util.concurrent.Future;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class AfterConnectionEstablished
{
    private static final Logger log;
    
    public static Future<?> execute(final CuratorFramework client, final Runnable runAfterConnection) throws Exception {
        final ExecutorService executor = ThreadUtils.newSingleThreadExecutor(ThreadUtils.getProcessName(runAfterConnection.getClass()));
        final Runnable internalCall = new Runnable() {
            @Override
            public void run() {
                try {
                    client.blockUntilConnected();
                    runAfterConnection.run();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    AfterConnectionEstablished.log.error("An error occurred blocking until a connection is available", e);
                }
                finally {
                    executor.shutdown();
                }
            }
        };
        return executor.submit(internalCall);
    }
    
    private AfterConnectionEstablished() {
    }
    
    static {
        log = LoggerFactory.getLogger(AfterConnectionEstablished.class);
    }
}
