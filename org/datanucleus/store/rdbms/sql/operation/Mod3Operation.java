// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class Mod3Operation extends AbstractSQLOperation
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final SQLExpression expr2) {
        final List args = new ArrayList();
        final List types = new ArrayList();
        types.add("BIGINT");
        final List argsOp1 = new ArrayList();
        argsOp1.add(expr);
        args.add(new NumericExpression(expr.getSQLStatement(), this.getMappingForClass(Integer.TYPE), "CAST", argsOp1, types));
        final List argsOp2 = new ArrayList();
        argsOp2.add(expr2);
        args.add(new NumericExpression(expr.getSQLStatement(), this.getMappingForClass(Integer.TYPE), "CAST", argsOp2, types));
        return new NumericExpression(expr.getSQLStatement(), this.getMappingForClass(Integer.TYPE), "MOD", args);
    }
}
