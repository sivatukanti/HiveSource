// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.io;

import java.io.IOException;
import org.apache.curator.shaded.com.google.common.annotations.Beta;

@Beta
public interface ByteProcessor<T>
{
    boolean processBytes(final byte[] p0, final int p1, final int p2) throws IOException;
    
    T getResult();
}
