// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.ObjectExpression;

public class ObjectExpressionImpl<T> extends ExpressionImpl<T> implements ObjectExpression<T>
{
    public ObjectExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public ObjectExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public ObjectExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
}
