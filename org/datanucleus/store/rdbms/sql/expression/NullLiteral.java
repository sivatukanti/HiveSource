// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.NullMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class NullLiteral extends SQLExpression implements SQLLiteral
{
    public NullLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, new NullMapping(stmt.getRDBMSManager()));
        this.st.append("NULL");
    }
    
    @Override
    public Object getValue() {
        return null;
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        return this;
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), true);
        }
        if (expr instanceof ObjectExpression) {
            return expr.eq(this);
        }
        if (this.stmt.getQueryGenerator() != null && this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.UPDATE) {
            return new BooleanExpression(expr, Expression.OP_EQ, this);
        }
        return new BooleanExpression(expr, Expression.OP_IS, this);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), false);
        }
        if (expr instanceof ObjectExpression) {
            return expr.ne(this);
        }
        return new BooleanExpression(expr, Expression.OP_ISNOT, this);
    }
    
    @Override
    public void setNotParameter() {
        if (this.parameterName == null) {
            return;
        }
        this.parameterName = null;
    }
}
