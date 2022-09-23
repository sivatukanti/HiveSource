// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Stable
public class UnresolvedLinkException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public UnresolvedLinkException() {
    }
    
    public UnresolvedLinkException(final String msg) {
        super(msg);
    }
}
