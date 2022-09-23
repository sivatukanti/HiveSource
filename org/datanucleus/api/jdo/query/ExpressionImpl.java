// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.typesafe.BooleanExpression;
import javax.jdo.JDOException;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.ParameterExpression;
import java.util.List;
import java.util.Collection;
import org.datanucleus.query.expression.PrimaryExpression;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.Expression;

public class ExpressionImpl<T> implements Expression<T>
{
    org.datanucleus.query.expression.Expression queryExpr;
    ExpressionType exprType;
    
    public ExpressionImpl(final PersistableExpression parent, final String name) {
        this.exprType = ExpressionType.PATH;
        final List<String> tuples = new ArrayList<String>();
        if (parent != null) {
            final org.datanucleus.query.expression.Expression parentQueryExpr = ((ExpressionImpl)parent).getQueryExpression();
            if (parentQueryExpr instanceof PrimaryExpression) {
                tuples.addAll(((PrimaryExpression)parentQueryExpr).getTuples());
                tuples.add(name);
                this.queryExpr = new PrimaryExpression(tuples);
            }
            else {
                tuples.add(name);
                this.queryExpr = new PrimaryExpression(parentQueryExpr, tuples);
            }
        }
        else {
            tuples.add(name);
            this.queryExpr = new PrimaryExpression(tuples);
        }
    }
    
    public ExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        this.exprType = ExpressionType.PATH;
        if (type == ExpressionType.PARAMETER || type == ExpressionType.VARIABLE) {
            this.exprType = type;
            if (this.exprType == ExpressionType.PARAMETER) {
                this.queryExpr = new ParameterExpression(name, cls);
            }
            else if (this.exprType == ExpressionType.VARIABLE) {
                this.queryExpr = new VariableExpression(name, cls);
            }
            return;
        }
        throw new JDOException("Should not have called this constructor of ExpressionImpl!");
    }
    
    public ExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        this.exprType = ExpressionType.PATH;
        this.queryExpr = queryExpr;
    }
    
    public org.datanucleus.query.expression.Expression getQueryExpression() {
        return this.queryExpr;
    }
    
    public boolean isParameter() {
        return this.exprType == ExpressionType.PARAMETER;
    }
    
    public boolean isVariable() {
        return this.exprType == ExpressionType.VARIABLE;
    }
    
    public BooleanExpression eq(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_EQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression eq(final T t) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(t);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_EQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression ne(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_NOTEQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression ne(final T t) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(t);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_NOTEQ, rightQueryExpr);
        return new BooleanExpressionImpl<Object>(queryExpr);
    }
    
    public BooleanExpression instanceOf(final Class cls) {
        throw new UnsupportedOperationException("instanceOf not yet supported");
    }
    
    public Expression cast(final Class cls) {
        throw new UnsupportedOperationException("cast not yet supported");
    }
    
    public NumericExpression<Long> count() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "count", args);
        return new NumericExpressionImpl<Long>(invokeExpr);
    }
    
    public NumericExpression<Long> countDistinct() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new DyadicExpression(org.datanucleus.query.expression.Expression.OP_DISTINCT, this.queryExpr));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "count", args);
        return new NumericExpressionImpl<Long>(invokeExpr);
    }
}
