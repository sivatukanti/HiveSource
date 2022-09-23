// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class SQLBooleanMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (args == null || args.size() != 1) {
            throw new NucleusUserException("Cannot invoke SQL_boolean() without a string argument");
        }
        final SQLExpression expr = args.get(0);
        if (!(expr instanceof StringLiteral)) {
            throw new NucleusUserException("Cannot use SQL_boolean() without string argument");
        }
        final String sql = (String)((StringLiteral)expr).getValue();
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, false);
        final BooleanExpression retExpr = new BooleanExpression(this.stmt, m, sql);
        return retExpr;
    }
}
