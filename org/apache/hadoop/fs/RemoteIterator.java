// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;

public interface RemoteIterator<E>
{
    boolean hasNext() throws IOException;
    
    E next() throws IOException;
}
