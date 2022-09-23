// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringEqualsIgnoreCaseMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() != 1) {
            throw new NucleusException(StringEqualsIgnoreCaseMethod.LOCALISER.msg("060003", "endsWith", "StringExpression", 0, "StringExpression/CharacterExpression/ParameterLiteral"));
        }
        final StringExpression strExpr1 = (StringExpression)expr;
        final StringExpression strExpr2 = args.get(0);
        final SQLExpression str1Upper = strExpr1.invoke("toUpperCase", null);
        final SQLExpression str2Upper = strExpr2.invoke("toUpperCase", null);
        return str1Upper.eq(str2Upper);
    }
}
