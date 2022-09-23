// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;

public class PersistableExpressionImpl<T> extends ExpressionImpl<T> implements PersistableExpression<T>
{
    public PersistableExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public PersistableExpressionImpl(final Class<T> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public Expression jdoObjectId() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "JDOHelper.getObjectId", args);
        return new CharacterExpressionImpl(invokeExpr);
    }
    
    public Expression jdoVersion() {
        final List<org.datanucleus.query.expression.Expression> args = new ArrayList<org.datanucleus.query.expression.Expression>();
        args.add(this.queryExpr);
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(null, "JDOHelper.getVersion", args);
        return new CharacterExpressionImpl(invokeExpr);
    }
}
