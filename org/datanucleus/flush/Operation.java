// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.state.ObjectProvider;

public interface Operation
{
    ObjectProvider getObjectProvider();
    
    void perform();
}
