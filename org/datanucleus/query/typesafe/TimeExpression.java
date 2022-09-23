// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface TimeExpression<T> extends TemporalExpression<T>
{
    NumericExpression<Integer> getHour();
    
    NumericExpression<Integer> getMinute();
    
    NumericExpression<Integer> getSecond();
}
