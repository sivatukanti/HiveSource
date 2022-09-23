// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.typesafe.OrderExpression;
import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.typesafe.BooleanExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.ComparableExpression;

public class ComparableExpressionImpl<T> extends ExpressionImpl<T> implements ComparableExpression<T>
{
    public ComparableExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public ComparableExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public ComparableExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public BooleanExpression gt(final ComparableExpression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_GT, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression gt(final T t) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(t);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_GT, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression gteq(final ComparableExpression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_GTEQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression gteq(final T t) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(t);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_GTEQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression lt(final ComparableExpression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_LT, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression lt(final T t) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(t);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_LT, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression lteq(final ComparableExpression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_LTEQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression lteq(final T t) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(t);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_LTEQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public NumericExpression max() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "max", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression min() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "min", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public OrderExpression asc() {
        return new OrderExpressionImpl(this, OrderExpression.OrderDirection.ASC);
    }
    
    public OrderExpression desc() {
        return new OrderExpressionImpl(this, OrderExpression.OrderDirection.DESC);
    }
}
