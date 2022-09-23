// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface ComparableExpression<T> extends Expression<T>
{
    BooleanExpression lt(final ComparableExpression p0);
    
    BooleanExpression lt(final T p0);
    
    BooleanExpression lteq(final ComparableExpression p0);
    
    BooleanExpression lteq(final T p0);
    
    BooleanExpression gt(final ComparableExpression p0);
    
    BooleanExpression gt(final T p0);
    
    BooleanExpression gteq(final ComparableExpression p0);
    
    BooleanExpression gteq(final T p0);
    
    NumericExpression min();
    
    NumericExpression max();
    
    OrderExpression asc();
    
    OrderExpression desc();
}
