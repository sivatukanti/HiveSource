// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.Literal;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.ListExpression;
import java.util.List;

public class ListExpressionImpl<T extends List<E>, E> extends CollectionExpressionImpl<T, E> implements ListExpression<T, E>
{
    public ListExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public ListExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public Expression get(final int pos) {
        final List args = new ArrayList();
        args.add(new Literal(pos));
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "get", args);
        return new ExpressionImpl(invokeExpr);
    }
    
    public Expression get(final NumericExpression<Integer> posExpr) {
        final List args = new ArrayList();
        args.add(((ExpressionImpl)posExpr).getQueryExpression());
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "get", args);
        return new ExpressionImpl(invokeExpr);
    }
}
