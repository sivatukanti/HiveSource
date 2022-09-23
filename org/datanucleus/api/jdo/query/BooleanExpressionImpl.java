// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.BooleanExpression;

public class BooleanExpressionImpl<T> extends ComparableExpressionImpl<Boolean> implements BooleanExpression
{
    public BooleanExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public BooleanExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public BooleanExpressionImpl(final Class<Boolean> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public BooleanExpression and(final BooleanExpression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_AND, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression not() {
        final org.datanucleus.query.expression.Expression rightQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(org.datanucleus.query.expression.Expression.OP_NOT, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression or(final BooleanExpression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_OR, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
}
