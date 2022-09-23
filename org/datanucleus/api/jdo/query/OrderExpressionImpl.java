// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.typesafe.Expression;
import org.datanucleus.query.typesafe.OrderExpression;

public class OrderExpressionImpl<T> implements OrderExpression<T>
{
    protected Expression orderExpr;
    protected OrderDirection direction;
    
    public OrderExpressionImpl(final Expression<T> expr, final OrderDirection dir) {
        this.orderExpr = expr;
        this.direction = dir;
    }
    
    public OrderDirection getDirection() {
        return this.direction;
    }
    
    public Expression<T> getExpression() {
        return (Expression<T>)this.orderExpr;
    }
}
