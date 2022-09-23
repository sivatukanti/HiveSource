// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.IOException;

public interface Parser
{
    void reset(final boolean p0);
    
    boolean isComplete();
    
    long parseAvailable() throws IOException;
    
    boolean isMoreInBuffer() throws IOException;
    
    boolean isIdle();
}
