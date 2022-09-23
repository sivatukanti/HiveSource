// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.logaggregation;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public enum ContainerLogsRetentionPolicy
{
    APPLICATION_MASTER_ONLY, 
    AM_AND_FAILED_CONTAINERS_ONLY, 
    ALL_CONTAINERS;
}
