// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringCharAtMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 1) {
            throw new NucleusException(StringCharAtMethod.LOCALISER.msg("060003", "charAt", "StringExpression", 0, "NumericExpression/IntegerLiteral/ParameterLiteral"));
        }
        final SQLExpression startExpr = args.get(0);
        if (!(startExpr instanceof NumericExpression) && !(startExpr instanceof IntegerLiteral) && !(startExpr instanceof ParameterLiteral)) {
            throw new NucleusException(StringCharAtMethod.LOCALISER.msg("060003", "charAt", "StringExpression", 0, "NumericExpression/IntegerLiteral/ParameterLiteral"));
        }
        final SQLExpression endExpr = startExpr.add(ExpressionUtils.getLiteralForOne(this.stmt));
        final List<SQLExpression> newArgs = new ArrayList<SQLExpression>(2);
        newArgs.add(startExpr);
        newArgs.add(endExpr);
        return this.exprFactory.invokeMethod(this.stmt, String.class.getName(), "substring", expr, newArgs);
    }
}
