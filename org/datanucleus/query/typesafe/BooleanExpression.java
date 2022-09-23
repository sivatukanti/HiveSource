// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface BooleanExpression extends ComparableExpression<Boolean>
{
    BooleanExpression and(final BooleanExpression p0);
    
    BooleanExpression or(final BooleanExpression p0);
    
    BooleanExpression not();
}
