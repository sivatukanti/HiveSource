// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

import java.io.Closeable;

public interface WriteBatch extends Closeable
{
    WriteBatch put(final byte[] p0, final byte[] p1);
    
    WriteBatch delete(final byte[] p0);
}
