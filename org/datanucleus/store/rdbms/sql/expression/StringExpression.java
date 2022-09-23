// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class StringExpression extends SQLExpression
{
    public StringExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public StringExpression(final Expression.MonadicOperator op, final SQLExpression expr1) {
        super(op, expr1);
    }
    
    public StringExpression(final SQLExpression expr1, final Expression.DyadicOperator op, final SQLExpression expr2) {
        super(expr1, op, expr2);
    }
    
    public StringExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args) {
        super(stmt, mapping, functionName, args, null);
    }
    
    public StringExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args, final List types) {
        super(stmt, mapping, functionName, args, types);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof EnumExpression) {
            return expr.eq(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            final int value = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_EQ, literal);
        }
        if (expr instanceof IntegerLiteral) {
            final int value = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_EQ, literal);
        }
        if (expr instanceof FloatingPointLiteral) {
            final int value = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_EQ, literal);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof EnumLiteral) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof ColumnExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof EnumExpression) {
            return expr.ne(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            final int value = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_NOTEQ, literal);
        }
        if (expr instanceof IntegerLiteral) {
            final int value = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_NOTEQ, literal);
        }
        if (expr instanceof FloatingPointLiteral) {
            final int value = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_NOTEQ, literal);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof TemporalExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof EnumLiteral) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
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
        if (expr instanceof NullLiteral) {
            return expr.lt(this);
        }
        if (expr instanceof EnumExpression) {
            return expr.ge(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof ByteLiteral) {
            final int value = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_LT, literal);
        }
        if (expr instanceof IntegerLiteral) {
            final int value = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_LT, literal);
        }
        if (expr instanceof FloatingPointLiteral) {
            final int value = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_LT, literal);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
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
        if (expr instanceof NullLiteral) {
            return expr.le(this);
        }
        if (expr instanceof EnumExpression) {
            return expr.gt(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            final int value = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_LTEQ, literal);
        }
        if (expr instanceof IntegerLiteral) {
            final int value = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_LTEQ, literal);
        }
        if (expr instanceof FloatingPointLiteral) {
            final int value = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_LTEQ, literal);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
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
        if (expr instanceof NullLiteral) {
            return expr.gt(this);
        }
        if (expr instanceof EnumExpression) {
            return expr.le(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof ByteLiteral) {
            final int value = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_GT, literal);
        }
        if (expr instanceof IntegerLiteral) {
            final int value = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_GT, literal);
        }
        if (expr instanceof FloatingPointLiteral) {
            final int value = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_GT, literal);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
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
        if (expr instanceof NullLiteral) {
            return expr.ge(this);
        }
        if (expr instanceof EnumExpression) {
            return expr.lt(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            final int value = ((BigInteger)((ByteLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_GTEQ, literal);
        }
        if (expr instanceof IntegerLiteral) {
            final int value = ((Number)((IntegerLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_GTEQ, literal);
        }
        if (expr instanceof FloatingPointLiteral) {
            final int value = ((BigDecimal)((FloatingPointLiteral)expr).getValue()).intValue();
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)value), (String)null);
            return new BooleanExpression(this, Expression.OP_GTEQ, literal);
        }
        if (expr instanceof NumericExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (this instanceof SQLLiteral && this.isParameter() && expr instanceof SQLLiteral && expr.isParameter()) {
            this.stmt.getQueryGenerator().useParameterExpressionAsLiteral((SQLLiteral)this);
            this.stmt.getQueryGenerator().useParameterExpressionAsLiteral((SQLLiteral)expr);
            return new StringExpression(this, Expression.OP_CONCAT, expr).encloseInParentheses();
        }
        if (expr.isParameter()) {
            return new StringExpression(this, Expression.OP_CONCAT, expr).encloseInParentheses();
        }
        if (expr instanceof StringLiteral) {
            return new StringExpression(this, Expression.OP_CONCAT, new StringLiteral(this.stmt, expr.mapping, ((StringLiteral)expr).getValue(), null)).encloseInParentheses();
        }
        if (expr instanceof StringExpression) {
            return new StringExpression(this, Expression.OP_CONCAT, expr).encloseInParentheses();
        }
        if (expr instanceof CharacterExpression) {
            return new StringExpression(this, Expression.OP_CONCAT, expr).encloseInParentheses();
        }
        if (expr instanceof NumericExpression) {
            final StringExpression strExpr = (StringExpression)this.stmt.getSQLExpressionFactory().invokeOperation("numericToString", expr, null).encloseInParentheses();
            return new StringExpression(this, Expression.OP_CONCAT, strExpr);
        }
        if (expr instanceof NullLiteral) {
            return expr;
        }
        return new StringExpression(this, Expression.OP_CONCAT, expr).encloseInParentheses();
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, String.class.getName(), methodName, this, args);
    }
}
