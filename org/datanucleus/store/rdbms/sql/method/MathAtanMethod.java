// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.IllegalExpressionOperationException;
import java.math.BigDecimal;
import org.datanucleus.store.rdbms.sql.expression.FloatingPointLiteral;
import org.datanucleus.store.rdbms.sql.expression.IntegerLiteral;
import java.math.BigInteger;
import org.datanucleus.store.rdbms.sql.expression.ByteLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class MathAtanMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (args == null || args.size() == 0) {
            throw new NucleusUserException("Cannot invoke Math.atan without an argument");
        }
        final SQLExpression expr = args.get(0);
        if (expr == null) {
            return new NullLiteral(this.stmt, null, null, null);
        }
        if (!(expr instanceof SQLLiteral)) {
            return this.exprFactory.invokeMethod(this.stmt, null, "atan", null, args);
        }
        if (expr instanceof ByteLiteral) {
            final int originalValue = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final BigInteger absValue = new BigInteger(String.valueOf(Math.atan(originalValue)));
            return new ByteLiteral(this.stmt, expr.getJavaTypeMapping(), absValue, null);
        }
        if (expr instanceof IntegerLiteral) {
            final int originalValue = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final Double absValue2 = new Double(Math.atan(originalValue));
            return new FloatingPointLiteral(this.stmt, expr.getJavaTypeMapping(), absValue2, null);
        }
        if (expr instanceof FloatingPointLiteral) {
            final double originalValue2 = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).doubleValue();
            final Double absValue3 = new Double(Math.atan(originalValue2));
            return new FloatingPointLiteral(this.stmt, expr.getJavaTypeMapping(), absValue3, null);
        }
        throw new IllegalExpressionOperationException("Math.atan()", expr);
    }
}
