// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public interface Serialization<T>
{
    boolean accept(final Class<?> p0);
    
    Serializer<T> getSerializer(final Class<T> p0);
    
    Deserializer<T> getDeserializer(final Class<T> p0);
}
