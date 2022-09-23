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
import org.datanucleus.query.typesafe.CollectionExpression;
import java.util.Collection;

public class CollectionExpressionImpl<T extends Collection<E>, E> extends ExpressionImpl<T> implements CollectionExpression<T, E>
{
    public CollectionExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public CollectionExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public BooleanExpression contains(final E elem) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(new Literal(elem));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "contains", args);
        return new BooleanExpressionImpl<Object>(invokeExpr);
    }
    
    public BooleanExpression contains(final Expression expr) {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(((ExpressionImpl)expr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "contains", args);
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
