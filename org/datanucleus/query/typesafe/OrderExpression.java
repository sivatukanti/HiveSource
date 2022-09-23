// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.typesafe;

public interface OrderExpression<T>
{
    OrderDirection getDirection();
    
    Expression<T> getExpression();
    
    public enum OrderDirection
    {
        ASC, 
        DESC;
    }
}
