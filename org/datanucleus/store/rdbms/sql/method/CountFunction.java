// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.AggregateNumericExpression;
import org.datanucleus.store.rdbms.sql.expression.ObjectExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class CountFunction extends AbstractSQLMethod
{
    protected String getFunctionName() {
        return "COUNT";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr != null) {
            throw new NucleusException(CountFunction.LOCALISER.msg("060002", "COUNT", expr));
        }
        if (args == null || args.size() != 1) {
            throw new NucleusException("COUNT is only supported with a single argument");
        }
        final SQLExpression argExpr = args.get(0);
        if (argExpr.getNumberOfSubExpressions() > 1 && argExpr instanceof ObjectExpression) {
            ((ObjectExpression)argExpr).useFirstColumnOnly();
        }
        return new AggregateNumericExpression(this.stmt, this.getMappingForClass(Long.TYPE), "COUNT", args);
    }
}
