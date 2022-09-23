// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class BadFencingConfigurationException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public BadFencingConfigurationException(final String msg) {
        super(msg);
    }
    
    public BadFencingConfigurationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
