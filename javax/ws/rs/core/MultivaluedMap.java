// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import java.util.List;
import java.util.Map;

public interface MultivaluedMap<K, V> extends Map<K, List<V>>
{
    void putSingle(final K p0, final V p1);
    
    void add(final K p0, final V p1);
    
    V getFirst(final K p0);
}
