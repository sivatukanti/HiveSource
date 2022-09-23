// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class ParameterLiteral extends SQLExpression implements SQLLiteral
{
    protected String name;
    protected Object value;
    
    public ParameterLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        this.value = value;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.add(expr);
        }
        return expr.add(this);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.eq(expr);
        }
        return expr.eq(this);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.ge(expr);
        }
        return expr.lt(this);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.gt(expr);
        }
        return expr.le(this);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.le(expr);
        }
        return expr.gt(this);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.lt(expr);
        }
        return expr.ge(this);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof ParameterLiteral) {
            return super.ne(expr);
        }
        return expr.ne(this);
    }
    
    @Override
    public void setNotParameter() {
    }
}
