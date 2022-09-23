// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

import java.util.Comparator;

public interface DBComparator extends Comparator<byte[]>
{
    String name();
    
    byte[] findShortestSeparator(final byte[] p0, final byte[] p1);
    
    byte[] findShortSuccessor(final byte[] p0);
}
