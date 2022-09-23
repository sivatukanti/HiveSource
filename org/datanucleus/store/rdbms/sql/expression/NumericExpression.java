// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.math.BigInteger;
import java.util.List;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class NumericExpression extends SQLExpression
{
    public NumericExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String sql) {
        super(stmt, null, mapping);
        this.st.clearStatement();
        this.st.append(sql);
    }
    
    public NumericExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public NumericExpression(final Expression.MonadicOperator op, final SQLExpression expr1) {
        super(op, expr1);
    }
    
    public NumericExpression(final SQLExpression expr1, final Expression.DyadicOperator op, final SQLExpression expr2) {
        super(expr1, op, expr2);
    }
    
    public NumericExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args) {
        super(stmt, mapping, functionName, args, null);
    }
    
    public NumericExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args, final List types) {
        super(stmt, mapping, functionName, args, types);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, ExpressionUtils.getNumericExpression(expr));
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, ExpressionUtils.getNumericExpression(expr));
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_LT, ExpressionUtils.getNumericExpression(expr));
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, ExpressionUtils.getNumericExpression(expr));
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_GT, ExpressionUtils.getNumericExpression(expr));
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, ExpressionUtils.getNumericExpression(expr));
        }
        return super.ge(expr);
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr instanceof NumericExpression) {
            return new NumericExpression(this, Expression.OP_ADD, expr).encloseInParentheses();
        }
        if (expr instanceof StringExpression) {
            final StringExpression strExpr = (StringExpression)this.stmt.getSQLExpressionFactory().invokeOperation("numericToString", this, null);
            return new StringExpression(strExpr, Expression.OP_CONCAT, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(this, Expression.OP_ADD, ExpressionUtils.getNumericExpression(expr)).encloseInParentheses();
        }
        if (expr instanceof NullLiteral) {
            return expr;
        }
        return super.add(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        if (expr instanceof NumericExpression) {
            return new NumericExpression(this, Expression.OP_SUB, expr).encloseInParentheses();
        }
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(this, Expression.OP_SUB, ExpressionUtils.getNumericExpression(expr)).encloseInParentheses();
        }
        return super.sub(expr);
    }
    
    @Override
    public SQLExpression mul(final SQLExpression expr) {
        if (expr instanceof NumericExpression) {
            return new NumericExpression(this, Expression.OP_MUL, expr).encloseInParentheses();
        }
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(this, Expression.OP_MUL, ExpressionUtils.getNumericExpression(expr)).encloseInParentheses();
        }
        return super.mul(expr);
    }
    
    @Override
    public SQLExpression div(final SQLExpression expr) {
        if (expr instanceof NumericExpression) {
            return new NumericExpression(this, Expression.OP_DIV, expr).encloseInParentheses();
        }
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(this, Expression.OP_DIV, ExpressionUtils.getNumericExpression(expr)).encloseInParentheses();
        }
        return super.div(expr);
    }
    
    @Override
    public SQLExpression mod(final SQLExpression expr) {
        try {
            if (expr instanceof NumericExpression) {
                return this.stmt.getSQLExpressionFactory().invokeOperation("mod", this, expr).encloseInParentheses();
            }
            if (expr instanceof CharacterExpression) {
                return this.stmt.getSQLExpressionFactory().invokeOperation("mod", this, ExpressionUtils.getNumericExpression(expr)).encloseInParentheses();
            }
        }
        catch (UnsupportedOperationException ex) {}
        return new NumericExpression(this, Expression.OP_MOD, expr);
    }
    
    @Override
    public SQLExpression neg() {
        return new NumericExpression(Expression.OP_NEG, this);
    }
    
    @Override
    public SQLExpression com() {
        return this.neg().sub(new IntegerLiteral(this.stmt, this.mapping, BigInteger.ONE, this.parameterName));
    }
}
