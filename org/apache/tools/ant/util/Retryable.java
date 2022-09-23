// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;

public interface Retryable
{
    public static final int RETRY_FOREVER = -1;
    
    void execute() throws IOException;
}
