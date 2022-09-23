// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import org.datanucleus.store.rdbms.sql.SQLText;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class Concat3Operation extends AbstractSQLOperation
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final SQLExpression expr2) {
        final JavaTypeMapping m = this.exprFactory.getMappingForType(String.class, false);
        final List types = new ArrayList();
        types.add("VARCHAR(4000)");
        final List argsOp1 = new ArrayList();
        argsOp1.add(expr);
        final SQLExpression firstExpr = new StringExpression(expr.getSQLStatement(), m, "CAST", argsOp1, types).encloseInParentheses();
        final List argsOp2 = new ArrayList();
        argsOp2.add(expr2);
        final SQLExpression secondExpr = new StringExpression(expr.getSQLStatement(), m, "CAST", argsOp2, types).encloseInParentheses();
        final StringExpression concatExpr = new StringExpression(expr.getSQLStatement(), null, null);
        final SQLText sql = concatExpr.toSQLText();
        sql.clearStatement();
        sql.append(firstExpr);
        sql.append("||");
        sql.append(secondExpr);
        final List args = new ArrayList();
        args.add(concatExpr);
        return new StringExpression(expr.getSQLStatement(), m, "CAST", args, types);
    }
}
