// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.store.rdbms.sql.expression.ParameterLiteral;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.ExpressionUtils;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringSubstringMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 2) {
            throw new NucleusException(StringSubstringMethod.LOCALISER.msg("060003", "substring", "StringExpression", 0, "NumericExpression/IntegerLiteral/ParameterLiteral"));
        }
        if (args.size() == 1) {
            final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
            final SQLExpression startExpr = args.get(0);
            if (!(startExpr instanceof NumericExpression) && !(startExpr instanceof IntegerLiteral) && !(startExpr instanceof ParameterLiteral)) {
                throw new NucleusException(StringSubstringMethod.LOCALISER.msg("060003", "substring", "StringExpression", 0, "NumericExpression/IntegerLiteral/ParameterLiteral"));
            }
            final StringExpression strExpr = new StringExpression(this.stmt, null, null);
            final SQLText sql = strExpr.toSQLText();
            sql.append("SUBSTRING(").append(expr).append(" FROM ").append(startExpr.add(one)).append(')');
            return strExpr;
        }
        else {
            final SQLExpression one = ExpressionUtils.getLiteralForOne(this.stmt);
            final SQLExpression startExpr = args.get(0);
            if (!(startExpr instanceof NumericExpression)) {
                throw new NucleusException(StringSubstringMethod.LOCALISER.msg("060003", "substring", "StringExpression", 0, "NumericExpression"));
            }
            final SQLExpression endExpr = args.get(1);
            if (!(endExpr instanceof NumericExpression)) {
                throw new NucleusException(StringSubstringMethod.LOCALISER.msg("060003", "substring", "StringExpression", 1, "NumericExpression"));
            }
            final StringExpression strExpr2 = new StringExpression(this.stmt, null, null);
            final SQLText sql2 = strExpr2.toSQLText();
            sql2.append("SUBSTRING(").append(expr).append(" FROM ").append(startExpr.add(one)).append(" FOR ").append(endExpr.sub(startExpr)).append(')');
            return strExpr2;
        }
    }
}
