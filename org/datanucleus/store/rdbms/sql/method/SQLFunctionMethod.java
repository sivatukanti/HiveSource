// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.ObjectExpression;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class SQLFunctionMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (args == null || args.size() < 1) {
            throw new NucleusUserException("Cannot invoke SQL_function() without first argument defining the function");
        }
        final SQLExpression expr = args.get(0);
        if (!(expr instanceof StringLiteral)) {
            throw new NucleusUserException("Cannot use SQL_function() without first argument defining the function");
        }
        final String sql = (String)((StringLiteral)expr).getValue();
        final List<SQLExpression> funcArgs = new ArrayList<SQLExpression>();
        if (args.size() > 1) {
            funcArgs.addAll(args.subList(1, args.size()));
        }
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Object.class, false);
        final ObjectExpression retExpr = new ObjectExpression(this.stmt, m, sql, funcArgs);
        return retExpr;
    }
}
