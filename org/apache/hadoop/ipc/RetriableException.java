// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.classification.InterfaceStability;
import java.io.IOException;

@InterfaceStability.Evolving
public class RetriableException extends IOException
{
    private static final long serialVersionUID = 1915561725516487301L;
    
    public RetriableException(final Exception e) {
        super(e);
    }
    
    public RetriableException(final String msg) {
        super(msg);
    }
}
