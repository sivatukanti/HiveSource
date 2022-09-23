// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class NullIfFunction extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr == null) {
            Class cls = Integer.class;
            int clsLevel = 0;
            for (int i = 0; i < args.size(); ++i) {
                final SQLExpression argExpr = args.get(i);
                final Class argType = argExpr.getJavaTypeMapping().getJavaType();
                if (clsLevel < 5 && (argType == Double.TYPE || argType == Double.class)) {
                    cls = Double.class;
                    clsLevel = 5;
                }
                else if (clsLevel < 4 && (argType == Float.TYPE || argType == Float.class)) {
                    cls = Float.class;
                    clsLevel = 4;
                }
                else if (clsLevel < 3 && argType == BigDecimal.class) {
                    cls = BigDecimal.class;
                    clsLevel = 3;
                }
                else if (clsLevel < 2 && argType == BigInteger.class) {
                    cls = BigInteger.class;
                    clsLevel = 2;
                }
                else if (clsLevel < 1 && (argType == Long.TYPE || argType == Long.class)) {
                    cls = Long.class;
                    clsLevel = 1;
                }
            }
            return new NumericExpression(this.stmt, this.getMappingForClass(cls), "NULLIF", args);
        }
        throw new NucleusException(NullIfFunction.LOCALISER.msg("060002", "NULLIF", expr));
    }
}
