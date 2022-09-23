// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FSError extends Error
{
    private static final long serialVersionUID = 1L;
    
    FSError(final Throwable cause) {
        super(cause);
    }
}
