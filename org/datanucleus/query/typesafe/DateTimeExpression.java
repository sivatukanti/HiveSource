// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface DateTimeExpression<T> extends TemporalExpression<T>
{
    NumericExpression<Integer> getYear();
    
    NumericExpression<Integer> getMonth();
    
    NumericExpression<Integer> getDay();
    
    NumericExpression<Integer> getHour();
    
    NumericExpression<Integer> getMinute();
    
    NumericExpression<Integer> getSecond();
}
