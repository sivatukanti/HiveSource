// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface NumericExpression<T> extends ComparableExpression<Number>
{
    NumericExpression add(final Expression p0);
    
    NumericExpression add(final Number p0);
    
    NumericExpression sub(final Expression p0);
    
    NumericExpression sub(final Number p0);
    
    NumericExpression mul(final Expression p0);
    
    NumericExpression mul(final Number p0);
    
    NumericExpression div(final Expression p0);
    
    NumericExpression div(final Number p0);
    
    NumericExpression mod(final Expression p0);
    
    NumericExpression mod(final Number p0);
    
    NumericExpression avg();
    
    NumericExpression sum();
    
    NumericExpression abs();
    
    NumericExpression sqrt();
    
    NumericExpression acos();
    
    NumericExpression asin();
    
    NumericExpression atan();
    
    NumericExpression sin();
    
    NumericExpression cos();
    
    NumericExpression tan();
    
    NumericExpression exp();
    
    NumericExpression log();
    
    NumericExpression ceil();
    
    NumericExpression floor();
}
