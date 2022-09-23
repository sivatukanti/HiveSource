// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class NumericToStringOperation extends AbstractSQLOperation
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final SQLExpression expr2) {
        final JavaTypeMapping m = this.exprFactory.getMappingForType(String.class, false);
        if (!(expr instanceof SQLLiteral)) {
            final List args = new ArrayList();
            args.add(expr);
            final List types = new ArrayList();
            types.add("VARCHAR(4000)");
            return new StringExpression(expr.getSQLStatement(), m, "CAST", args, types);
        }
        if (((SQLLiteral)expr).getValue() == null) {
            return new StringLiteral(expr.getSQLStatement(), m, null, (String)null);
        }
        return new StringLiteral(expr.getSQLStatement(), m, ((SQLLiteral)expr).getValue().toString(), (String)null);
    }
}
