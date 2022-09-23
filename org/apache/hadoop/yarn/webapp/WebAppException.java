// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class WebAppException extends YarnRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public WebAppException(final String msg) {
        super(msg);
    }
    
    public WebAppException(final Throwable cause) {
        super(cause);
    }
    
    public WebAppException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
