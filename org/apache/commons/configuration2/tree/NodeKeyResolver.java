// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.Map;
import java.util.List;

public interface NodeKeyResolver<T>
{
    List<QueryResult<T>> resolveKey(final T p0, final String p1, final NodeHandler<T> p2);
    
    List<T> resolveNodeKey(final T p0, final String p1, final NodeHandler<T> p2);
    
    NodeAddData<T> resolveAddKey(final T p0, final String p1, final NodeHandler<T> p2);
    
    NodeUpdateData<T> resolveUpdateKey(final T p0, final String p1, final Object p2, final NodeHandler<T> p3);
    
    String nodeKey(final T p0, final Map<T, String> p1, final NodeHandler<T> p2);
}
