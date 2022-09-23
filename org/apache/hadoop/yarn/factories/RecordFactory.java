// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.factories;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
@InterfaceStability.Unstable
public interface RecordFactory
{
     <T> T newRecordInstance(final Class<T> p0);
}
