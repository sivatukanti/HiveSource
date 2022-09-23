// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.ArrayList;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.List;

public class ArrayExpression extends SQLExpression
{
    protected List<SQLExpression> elementExpressions;
    
    public ArrayExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public ArrayExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final SQLExpression[] exprs) {
        super(stmt, null, mapping);
        this.elementExpressions = new ArrayList<SQLExpression>();
        for (int i = 0; i < exprs.length; ++i) {
            this.elementExpressions.add(exprs[i]);
        }
    }
    
    public List<SQLExpression> getElementExpressions() {
        return this.elementExpressions;
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, "ARRAY", methodName, this, args);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        return super.ne(expr);
    }
}
