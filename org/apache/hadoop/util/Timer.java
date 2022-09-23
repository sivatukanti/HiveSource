// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class Timer
{
    public long now() {
        return Time.now();
    }
    
    public long monotonicNow() {
        return Time.monotonicNow();
    }
    
    public long monotonicNowNanos() {
        return Time.monotonicNowNanos();
    }
}
