// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

import java.util.Collection;

public interface CollectionExpression<T extends Collection<E>, E> extends Expression<T>
{
    BooleanExpression contains(final Expression p0);
    
    BooleanExpression contains(final E p0);
    
    BooleanExpression isEmpty();
    
    NumericExpression<Integer> size();
}
