// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.io.IOException;

public interface Parser
{
    void returnBuffers();
    
    void reset();
    
    boolean isComplete();
    
    boolean parseAvailable() throws IOException;
    
    boolean isMoreInBuffer() throws IOException;
    
    boolean isIdle();
    
    boolean isPersistent();
    
    void setPersistent(final boolean p0);
}
