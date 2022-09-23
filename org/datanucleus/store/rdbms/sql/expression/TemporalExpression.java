// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Date;
import org.datanucleus.query.expression.Expression;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class TemporalExpression extends SQLExpression
{
    public TemporalExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public TemporalExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args) {
        super(stmt, mapping, functionName, args, null);
    }
    
    public TemporalExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args, final List types) {
        super(stmt, mapping, functionName, args, types);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, Date.class.getName(), methodName, this, args);
    }
}
