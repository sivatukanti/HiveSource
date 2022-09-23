// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import java.io.IOException;
import java.io.Closeable;

public interface Lease extends Closeable
{
    void close() throws IOException;
    
    byte[] getData() throws Exception;
    
    String getNodeName();
}
