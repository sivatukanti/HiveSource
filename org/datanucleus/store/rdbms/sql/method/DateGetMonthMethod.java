// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class DateGetMonthMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!(expr instanceof TemporalExpression)) {
            throw new NucleusException(DateGetMonthMethod.LOCALISER.msg("060001", "getMonth()", expr));
        }
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        final NumericExpression monthExpr = new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "MONTH", funcArgs);
        final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
        final NumericExpression numExpr = new NumericExpression(monthExpr, Expression.OP_SUB, one);
        numExpr.encloseInParentheses();
        return numExpr;
    }
}
