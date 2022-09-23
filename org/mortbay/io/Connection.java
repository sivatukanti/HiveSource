// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.IOException;

public interface Connection
{
    void handle() throws IOException;
    
    boolean isIdle();
}
