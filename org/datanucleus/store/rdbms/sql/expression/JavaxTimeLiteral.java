// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public abstract class JavaxTimeLiteral extends StringTemporalExpression implements SQLLiteral
{
    public JavaxTimeLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof JavaxTimeLiteral) {
            return super.eq(((JavaxTimeLiteral)expr).delegate);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (expr instanceof JavaxTimeLiteral) {
            return super.ge(((JavaxTimeLiteral)expr).delegate);
        }
        return super.ge(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (expr instanceof JavaxTimeLiteral) {
            return super.gt(((JavaxTimeLiteral)expr).delegate);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (expr instanceof JavaxTimeLiteral) {
            return super.le(((JavaxTimeLiteral)expr).delegate);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (expr instanceof JavaxTimeLiteral) {
            return super.lt(((JavaxTimeLiteral)expr).delegate);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof JavaxTimeLiteral) {
            return super.ne(((JavaxTimeLiteral)expr).delegate);
        }
        return super.ne(expr);
    }
    
    @Override
    public boolean isParameter() {
        return this.delegate.isParameter();
    }
    
    @Override
    public void setNotParameter() {
        ((SQLLiteral)this.delegate).setNotParameter();
    }
}
