// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.TimeExpression;
import java.util.Date;

public class TimeExpressionImpl<T> extends ComparableExpressionImpl<Date> implements TimeExpression<Date>
{
    public TimeExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public TimeExpressionImpl(final Class<Date> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public TimeExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public NumericExpression<Integer> getHour() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "getHour", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
    
    public NumericExpression<Integer> getMinute() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "getMinute", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
    
    public NumericExpression<Integer> getSecond() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "getSecond", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
}
