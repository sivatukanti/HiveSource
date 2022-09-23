// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.classification.InterfaceStability;
import java.io.IOException;

@InterfaceStability.Evolving
public class StandbyException extends IOException
{
    static final long serialVersionUID = 78123814928L;
    
    public StandbyException(final String msg) {
        super(msg);
    }
}
