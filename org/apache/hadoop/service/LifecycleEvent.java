// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class LifecycleEvent implements Serializable
{
    private static final long serialVersionUID = 1648576996238247836L;
    public long time;
    public Service.STATE state;
}
