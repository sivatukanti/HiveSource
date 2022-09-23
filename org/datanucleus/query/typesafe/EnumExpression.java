// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface EnumExpression<T> extends ComparableExpression<Enum>
{
    NumericExpression ordinal();
}
