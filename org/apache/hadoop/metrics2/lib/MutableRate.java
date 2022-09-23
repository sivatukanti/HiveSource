// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableRate extends MutableStat
{
    MutableRate(final String name, final String description, final boolean extended) {
        super(name, description, "Ops", "Time", extended);
    }
}
