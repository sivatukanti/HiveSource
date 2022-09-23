// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class HadoopIllegalArgumentException extends IllegalArgumentException
{
    private static final long serialVersionUID = 1L;
    
    public HadoopIllegalArgumentException(final String message) {
        super(message);
    }
}
