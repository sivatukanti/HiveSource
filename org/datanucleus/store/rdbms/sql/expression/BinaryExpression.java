// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class BinaryExpression extends SQLExpression
{
    public BinaryExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public BinaryExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List<SQLExpression> args, final List types) {
        super(stmt, mapping, functionName, args, types);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (expr instanceof BinaryExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        return super.eq(expr);
    }
    
    public BooleanExpression noteq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        if (expr instanceof BinaryExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (expr instanceof BinaryExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        return super.lt(expr);
    }
    
    public BooleanExpression lteq(final SQLExpression expr) {
        if (expr instanceof BinaryExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (expr instanceof BinaryExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        return super.gt(expr);
    }
    
    public BooleanExpression gteq(final SQLExpression expr) {
        if (expr instanceof BinaryExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
}
