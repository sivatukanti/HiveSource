// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.List;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class ByteExpression extends NumericExpression
{
    public ByteExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public ByteExpression(final Expression.MonadicOperator op, final SQLExpression expr1) {
        super(op, expr1);
    }
    
    public ByteExpression(final SQLExpression expr1, final Expression.DyadicOperator op, final SQLExpression expr2) {
        super(expr1, op, expr2);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (expr instanceof ColumnExpression) {
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
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.lt(this);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.le(this);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.gt(this);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.ge(this);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, Byte.class.getName(), methodName, this, args);
    }
}
