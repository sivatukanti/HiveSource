// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public interface Deserializer<T>
{
    void open(final InputStream p0) throws IOException;
    
    T deserialize(final T p0) throws IOException;
    
    void close() throws IOException;
}
