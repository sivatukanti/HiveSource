// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.rolling.helper.Action;

public interface RolloverDescription
{
    String getActiveFileName();
    
    boolean getAppend();
    
    Action getSynchronous();
    
    Action getAsynchronous();
}
