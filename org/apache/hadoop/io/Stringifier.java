// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Stringifier<T> extends Closeable
{
    String toString(final T p0) throws IOException;
    
    T fromString(final String p0) throws IOException;
    
    void close() throws IOException;
}
