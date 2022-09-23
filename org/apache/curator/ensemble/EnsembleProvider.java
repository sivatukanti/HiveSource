// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.ensemble;

import java.io.IOException;
import java.io.Closeable;

public interface EnsembleProvider extends Closeable
{
    void start() throws Exception;
    
    String getConnectionString();
    
    void close() throws IOException;
}
