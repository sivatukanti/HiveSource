// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import java.io.IOException;

public interface JettyShims
{
    Server startServer(final String p0, final int p1) throws IOException;
    
    public interface Server
    {
        void addWar(final String p0, final String p1);
        
        void start() throws Exception;
        
        void join() throws InterruptedException;
        
        void stop() throws Exception;
    }
}
