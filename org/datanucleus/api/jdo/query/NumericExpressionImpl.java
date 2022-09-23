// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import java.util.ArrayList;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.NumericExpression;

public class NumericExpressionImpl<T> extends ComparableExpressionImpl<Number> implements NumericExpression<T>
{
    public NumericExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public NumericExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public NumericExpressionImpl(final Class<Number> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public NumericExpression add(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_ADD, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression add(final Number num) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(num);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_ADD, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression mul(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_MUL, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression mul(final Number num) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(num);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_MUL, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression sub(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_SUB, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression sub(final Number num) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(num);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_SUB, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression div(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_DIV, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression div(final Number num) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(num);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_DIV, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression mod(final Expression expr) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = ((ExpressionImpl)expr).getQueryExpression();
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_MOD, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression mod(final Number num) {
        final org.datanucleus.query.expression.Expression leftQueryExpr = this.queryExpr;
        final org.datanucleus.query.expression.Expression rightQueryExpr = new Literal(num);
        final org.datanucleus.query.expression.Expression queryExpr = new DyadicExpression(leftQueryExpr, org.datanucleus.query.expression.Expression.OP_MOD, rightQueryExpr);
        return new NumericExpressionImpl(queryExpr);
    }
    
    public NumericExpression<T> avg() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "avg", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression<T> sum() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "sum", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression<T> abs() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "abs", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression<T> sqrt() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "asin", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression acos() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "acos", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression asin() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "atan", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression atan() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "sqrt", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression sin() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "sin", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression cos() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "cos", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression tan() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "tan", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression exp() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "exp", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression log() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "log", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression ceil() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "ceil", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression floor() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "floor", args);
        return new NumericExpressionImpl(invokeExpr);
    }
}
