// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.util.EventListener;

public interface BuildListener extends EventListener
{
    void buildStarted(final BuildEvent p0);
    
    void buildFinished(final BuildEvent p0);
    
    void targetStarted(final BuildEvent p0);
    
    void targetFinished(final BuildEvent p0);
    
    void taskStarted(final BuildEvent p0);
    
    void taskFinished(final BuildEvent p0);
    
    void messageLogged(final BuildEvent p0);
}
