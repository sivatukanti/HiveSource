// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class SQLNumericMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (args == null || args.size() != 1) {
            throw new NucleusUserException("Cannot invoke SQL_numeric() without a string argument");
        }
        final SQLExpression expr = args.get(0);
        if (!(expr instanceof StringLiteral)) {
            throw new NucleusUserException("Cannot use SQL_numeric() without string argument");
        }
        final String sql = (String)((StringLiteral)expr).getValue();
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, false);
        final NumericExpression retExpr = new NumericExpression(this.stmt, m, sql);
        return retExpr;
    }
}
