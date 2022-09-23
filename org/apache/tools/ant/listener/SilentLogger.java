// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

public class SilentLogger extends DefaultLogger
{
    @Override
    public void buildStarted(final BuildEvent event) {
    }
    
    @Override
    public void buildFinished(final BuildEvent event) {
        if (event.getException() != null) {
            super.buildFinished(event);
        }
    }
    
    @Override
    public void targetStarted(final BuildEvent event) {
    }
    
    @Override
    public void targetFinished(final BuildEvent event) {
    }
    
    @Override
    public void taskStarted(final BuildEvent event) {
    }
    
    @Override
    public void taskFinished(final BuildEvent event) {
    }
}
