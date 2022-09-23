// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling.helper;

import java.io.IOException;

public interface Action extends Runnable
{
    boolean execute() throws IOException;
    
    void close();
    
    boolean isComplete();
}
