// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.component.LifeCycle;

public interface HandlerContainer extends LifeCycle
{
    void addHandler(final Handler p0);
    
    void removeHandler(final Handler p0);
    
    Handler[] getChildHandlers();
    
    Handler[] getChildHandlersByClass(final Class p0);
    
    Handler getChildHandlerByClass(final Class p0);
}
