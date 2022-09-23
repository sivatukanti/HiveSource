// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface Expression<T>
{
    BooleanExpression eq(final Expression p0);
    
    BooleanExpression eq(final T p0);
    
    BooleanExpression ne(final Expression p0);
    
    BooleanExpression ne(final T p0);
    
    NumericExpression<Long> count();
    
    NumericExpression<Long> countDistinct();
    
    BooleanExpression instanceOf(final Class p0);
    
    Expression cast(final Class p0);
}
