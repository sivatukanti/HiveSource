// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.EnumExpression;

public class EnumExpressionImpl<T> extends ComparableExpressionImpl<Enum> implements EnumExpression<Enum>
{
    public EnumExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public EnumExpressionImpl(final Class<Enum> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public EnumExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public NumericExpression ordinal() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "ordinal", null);
        return new NumericExpressionImpl(invokeExpr);
    }
}
