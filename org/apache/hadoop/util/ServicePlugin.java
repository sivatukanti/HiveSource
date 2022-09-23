// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface ServicePlugin extends Closeable
{
    void start(final Object p0);
    
    void stop();
}
