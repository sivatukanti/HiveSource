// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class Mod2Operation extends AbstractSQLOperation
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final SQLExpression expr2) {
        final ArrayList args = new ArrayList();
        args.add(expr);
        args.add(expr2);
        return new NumericExpression(expr.getSQLStatement(), this.getMappingForClass(Integer.TYPE), "MOD", args);
    }
}
