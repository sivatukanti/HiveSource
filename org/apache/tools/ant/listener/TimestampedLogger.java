// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.listener;

import org.apache.tools.ant.DefaultLogger;

public class TimestampedLogger extends DefaultLogger
{
    public static final String SPACER = " - at ";
    
    @Override
    protected String getBuildFailedMessage() {
        return super.getBuildFailedMessage() + " - at " + this.getTimestamp();
    }
    
    @Override
    protected String getBuildSuccessfulMessage() {
        return super.getBuildSuccessfulMessage() + " - at " + this.getTimestamp();
    }
}
