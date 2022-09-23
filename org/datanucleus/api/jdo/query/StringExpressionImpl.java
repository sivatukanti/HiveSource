// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.typesafe.BooleanExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.CharacterExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.StringExpression;

public class StringExpressionImpl extends ComparableExpressionImpl<String> implements StringExpression
{
    public StringExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public StringExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public StringExpressionImpl(final Class<String> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public StringExpression add(final Expression expr) {
        return null;
    }
    
    public CharacterExpression charAt(final int pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(pos));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "charAt", args);
        return new CharacterExpressionImpl<Object>(invokeExpr);
    }
    
    public CharacterExpression charAt(final NumericExpression pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)pos).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "charAt", args);
        return new CharacterExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression endsWith(final String str) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(str));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "endsWith", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression endsWith(final StringExpression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "endsWith", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression equalsIgnoreCase(final String str) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(str));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "equalsIgnoreCase", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression equalsIgnoreCase(final StringExpression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "equalsIgnoreCase", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public NumericExpression indexOf(final String str, final int pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(str));
        args.add(new Literal(pos));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "indexOf", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression indexOf(final String str, final NumericExpression pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(str));
        args.add(((ExpressionImpl)pos).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "indexOf", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression indexOf(final String str) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(str));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "indexOf", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression indexOf(final StringExpression expr, final int pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        args.add(new Literal(pos));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "indexOf", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression indexOf(final StringExpression expr, final NumericExpression pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        args.add(((ExpressionImpl)pos).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "indexOf", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression indexOf(final StringExpression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "indexOf", args);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public NumericExpression length() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "length", null);
        return new NumericExpressionImpl(invokeExpr);
    }
    
    public BooleanExpression startsWith(final String str) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(str));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "startsWith", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression startsWith(final StringExpression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "startsWith", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public StringExpression substring(final int startPos, final int endPos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(startPos));
        args.add(new Literal(endPos));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "substring", args);
        return new StringExpressionImpl(invokeExpr);
    }
    
    public StringExpression substring(final int pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(pos));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "substring", args);
        return new StringExpressionImpl(invokeExpr);
    }
    
    public StringExpression substring(final NumericExpression startPos, final NumericExpression endPos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)startPos).getQueryExpression());
        args.add(((ExpressionImpl)endPos).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "substring", args);
        return new StringExpressionImpl(invokeExpr);
    }
    
    public StringExpression substring(final NumericExpression pos) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)pos).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "substring", args);
        return new StringExpressionImpl(invokeExpr);
    }
    
    public StringExpression toLowerCase() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "toLowerCase", null);
        return new StringExpressionImpl(invokeExpr);
    }
    
    public StringExpression toUpperCase() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "toUpperCase", null);
        return new StringExpressionImpl(invokeExpr);
    }
    
    public StringExpression trim() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "trim", null);
        return new StringExpressionImpl(invokeExpr);
    }
}
