// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Profiler
{
    private static final Logger LOG;
    
    public static <T> T profile(final Operation<T> op, final long timeout, final String message) throws Exception {
        final long start = System.currentTimeMillis();
        final T res = op.execute();
        final long end = System.currentTimeMillis();
        if (end - start > timeout) {
            Profiler.LOG.info("Elapsed " + (end - start) + " ms: " + message);
        }
        return res;
    }
    
    static {
        LOG = LoggerFactory.getLogger(Profiler.class);
    }
    
    public interface Operation<T>
    {
        T execute() throws Exception;
    }
}
