// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

import java.util.Map;

public interface MapExpression<T extends Map<K, V>, K, V> extends Expression<T>
{
    BooleanExpression containsKey(final Expression p0);
    
    BooleanExpression containsKey(final K p0);
    
    BooleanExpression containsValue(final Expression p0);
    
    BooleanExpression containsValue(final V p0);
    
    BooleanExpression containsEntry(final Expression p0);
    
    BooleanExpression containsEntry(final Map.Entry<K, V> p0);
    
    BooleanExpression isEmpty();
    
    NumericExpression<Integer> size();
}
