// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.SocketTimeoutException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ConnectTimeoutException extends SocketTimeoutException
{
    private static final long serialVersionUID = 1L;
    
    public ConnectTimeoutException(final String msg) {
        super(msg);
    }
}
