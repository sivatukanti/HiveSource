// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.typesafe.NumericExpression;
import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.Expression;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.BooleanExpression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.MapExpression;
import java.util.Map;

public class MapExpressionImpl<T extends Map<K, V>, K, V> extends ExpressionImpl<T> implements MapExpression<T, K, V>
{
    public MapExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public MapExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public BooleanExpression containsEntry(final Map.Entry<K, V> entry) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(entry));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "containsEntry", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression containsEntry(final Expression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "containsEntry", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression containsKey(final Expression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "containsKey", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression containsKey(final K key) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(key));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "containsKey", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression containsValue(final Expression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "containsValue", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression containsValue(final V value) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(value));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "containsValue", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression isEmpty() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "isEmpty", null);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public NumericExpression<Integer> size() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "size", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
}
