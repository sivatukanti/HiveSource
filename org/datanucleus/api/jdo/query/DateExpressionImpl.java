// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.typesafe.PersistableExpression;
import org.datanucleus.query.typesafe.DateExpression;
import java.util.Date;

public class DateExpressionImpl<T> extends ComparableExpressionImpl<Date> implements DateExpression<Date>
{
    public DateExpressionImpl(final PersistableExpression parent, final String name) {
        super(parent, name);
    }
    
    public DateExpressionImpl(final Class<Date> cls, final String name, final ExpressionType type) {
        super(cls, name, type);
    }
    
    public DateExpressionImpl(final org.datanucleus.query.expression.Expression queryExpr) {
        super(queryExpr);
    }
    
    public NumericExpression<Integer> getDay() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "getDay", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
    
    public NumericExpression<Integer> getMonth() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "getMonth", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
    
    public NumericExpression<Integer> getYear() {
        final org.datanucleus.query.expression.Expression invokeExpr = new InvokeExpression(this.queryExpr, "getYear", null);
        return new NumericExpressionImpl<Integer>(invokeExpr);
    }
}
