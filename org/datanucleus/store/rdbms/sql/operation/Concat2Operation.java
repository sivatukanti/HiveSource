// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.operation;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class Concat2Operation extends AbstractSQLOperation
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final SQLExpression expr2) {
        final RDBMSStoreManager storeMgr = expr.getSQLStatement().getRDBMSManager();
        final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(String.class, false);
        final ArrayList args = new ArrayList();
        args.add(expr);
        args.add(expr2);
        return new StringExpression(expr.getSQLStatement(), m, "CONCAT", args);
    }
}
