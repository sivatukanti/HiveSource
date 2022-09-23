// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import org.apache.tools.ant.util.StringUtils;

public class NoBannerLogger extends DefaultLogger
{
    protected String targetName;
    
    @Override
    public synchronized void targetStarted(final BuildEvent event) {
        this.targetName = this.extractTargetName(event);
    }
    
    protected String extractTargetName(final BuildEvent event) {
        return event.getTarget().getName();
    }
    
    @Override
    public synchronized void targetFinished(final BuildEvent event) {
        this.targetName = null;
    }
    
    @Override
    public void messageLogged(final BuildEvent event) {
        if (event.getPriority() > this.msgOutputLevel || null == event.getMessage() || "".equals(event.getMessage().trim())) {
            return;
        }
        synchronized (this) {
            if (null != this.targetName) {
                this.out.println(StringUtils.LINE_SEP + this.targetName + ":");
                this.targetName = null;
            }
        }
        super.messageLogged(event);
    }
}
