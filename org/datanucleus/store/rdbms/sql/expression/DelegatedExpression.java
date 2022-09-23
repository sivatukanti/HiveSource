// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.sql.SQLText;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public abstract class DelegatedExpression extends SQLExpression
{
    protected SQLExpression delegate;
    
    public DelegatedExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        return this.delegate.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        return this.delegate.ne(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        return this.delegate.add(expr);
    }
    
    @Override
    public SQLExpression div(final SQLExpression expr) {
        return this.delegate.div(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        return this.delegate.ge(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        return this.delegate.gt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        return this.delegate.le(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        return this.delegate.lt(expr);
    }
    
    @Override
    public SQLExpression mod(final SQLExpression expr) {
        return this.delegate.mod(expr);
    }
    
    @Override
    public SQLExpression mul(final SQLExpression expr) {
        return this.delegate.mul(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        return this.delegate.sub(expr);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, this.mapping.getJavaType().getName(), methodName, this, args);
    }
    
    @Override
    public SQLText toSQLText() {
        return this.delegate.toSQLText();
    }
    
    public SQLExpression getDelegate() {
        return this.delegate;
    }
}
