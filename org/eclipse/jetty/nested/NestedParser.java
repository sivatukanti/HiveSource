// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.nested;

import java.io.IOException;
import org.eclipse.jetty.http.Parser;

public class NestedParser implements Parser
{
    public void reset() {
    }
    
    public void returnBuffers() {
    }
    
    public boolean isComplete() {
        return false;
    }
    
    public boolean parseAvailable() throws IOException {
        return false;
    }
    
    public boolean isMoreInBuffer() throws IOException {
        return false;
    }
    
    public boolean isIdle() {
        return false;
    }
    
    public boolean isPersistent() {
        return false;
    }
    
    public void setPersistent(final boolean persistent) {
    }
}
