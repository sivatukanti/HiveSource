// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.List;

public interface ExpressionEngine
{
     <T> List<QueryResult<T>> query(final T p0, final String p1, final NodeHandler<T> p2);
    
     <T> String nodeKey(final T p0, final String p1, final NodeHandler<T> p2);
    
    String attributeKey(final String p0, final String p1);
    
     <T> String canonicalKey(final T p0, final String p1, final NodeHandler<T> p2);
    
     <T> NodeAddData<T> prepareAdd(final T p0, final String p1, final NodeHandler<T> p2);
}
