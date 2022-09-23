// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Set;
import java.nio.ByteBuffer;

public interface Trie<V>
{
    boolean put(final String p0, final V p1);
    
    boolean put(final V p0);
    
    V remove(final String p0);
    
    V get(final String p0);
    
    V get(final String p0, final int p1, final int p2);
    
    V get(final ByteBuffer p0);
    
    V get(final ByteBuffer p0, final int p1, final int p2);
    
    V getBest(final String p0);
    
    V getBest(final String p0, final int p1, final int p2);
    
    V getBest(final byte[] p0, final int p1, final int p2);
    
    V getBest(final ByteBuffer p0, final int p1, final int p2);
    
    Set<String> keySet();
    
    boolean isFull();
    
    boolean isCaseInsensitive();
    
    void clear();
}
