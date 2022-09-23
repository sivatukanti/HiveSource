// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

import java.util.List;

public interface ListExpression<T extends List<E>, E> extends CollectionExpression<T, E>
{
    Expression get(final NumericExpression<Integer> p0);
    
    Expression get(final int p0);
}
