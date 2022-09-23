// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.ByteExpression;

public class ByteExpressionImpl<T> extends ComparableExpressionImpl<Byte> implements ByteExpression
{
    public ByteExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public ByteExpressionImpl(final Class<Byte> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public ByteExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
}
