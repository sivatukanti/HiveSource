// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class RMFatalEvent extends AbstractEvent<RMFatalEventType>
{
    private String cause;
    
    public RMFatalEvent(final RMFatalEventType rmFatalEventType, final String cause) {
        super(rmFatalEventType);
        this.cause = cause;
    }
    
    public RMFatalEvent(final RMFatalEventType rmFatalEventType, final Exception cause) {
        super(rmFatalEventType);
        this.cause = StringUtils.stringifyException(cause);
    }
    
    public String getCause() {
        return this.cause;
    }
}
