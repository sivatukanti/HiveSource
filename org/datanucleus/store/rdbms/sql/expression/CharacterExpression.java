// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.math.BigInteger;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.expression.Expression;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class CharacterExpression extends SQLExpression
{
    public CharacterExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public CharacterExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args) {
        super(stmt, mapping, functionName, args, null);
    }
    
    public CharacterExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args, final List types) {
        super(stmt, mapping, functionName, args, types);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (expr instanceof ColumnExpression || expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof StringLiteral) {
            final Object value = ((StringLiteral)expr).getValue();
            if (value instanceof String && ((String)value).length() > 1) {
                throw new NucleusUserException("Can't perform equality comparison between a character and a String of more than 1 character (" + value + ") !");
            }
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        else {
            if (expr instanceof StringExpression) {
                return new BooleanExpression(this, Expression.OP_EQ, expr);
            }
            if (expr instanceof NumericExpression) {
                return ExpressionUtils.getNumericExpression(this).eq(expr);
            }
            return super.eq(expr);
        }
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        if (expr instanceof ColumnExpression || expr instanceof CharacterExpression || expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).ne(expr);
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.lt(this);
        }
        if (expr instanceof ColumnExpression || expr instanceof CharacterExpression || expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).lt(expr);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.le(this);
        }
        if (expr instanceof ColumnExpression || expr instanceof CharacterExpression || expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).le(expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.gt(this);
        }
        if (expr instanceof ColumnExpression || expr instanceof CharacterExpression || expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).gt(expr);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof NullLiteral) {
            return expr.ge(this);
        }
        if (expr instanceof ColumnExpression || expr instanceof CharacterExpression || expr instanceof StringExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).ge(expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_ADD, ExpressionUtils.getNumericExpression(expr));
        }
        if (expr instanceof NumericExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_ADD, expr);
        }
        return super.add(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_SUB, ExpressionUtils.getNumericExpression(expr));
        }
        if (expr instanceof NumericExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_SUB, expr);
        }
        return super.sub(expr);
    }
    
    @Override
    public SQLExpression mul(final SQLExpression expr) {
        if (expr instanceof NumericExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_MUL, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_MUL, ExpressionUtils.getNumericExpression(expr));
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).mul(expr);
        }
        return super.mul(expr);
    }
    
    @Override
    public SQLExpression div(final SQLExpression expr) {
        if (expr instanceof NumericExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_DIV, expr);
        }
        if (expr instanceof CharacterExpression) {
            return new NumericExpression(ExpressionUtils.getNumericExpression(this), Expression.OP_DIV, ExpressionUtils.getNumericExpression(expr));
        }
        if (expr instanceof NumericExpression) {
            return ExpressionUtils.getNumericExpression(this).div(expr);
        }
        return super.div(expr);
    }
    
    @Override
    public SQLExpression mod(final SQLExpression expr) {
        try {
            if (expr instanceof CharacterExpression) {
                return this.stmt.getSQLExpressionFactory().invokeOperation("mod", ExpressionUtils.getNumericExpression(this), ExpressionUtils.getNumericExpression(expr)).encloseInParentheses();
            }
            if (expr instanceof NumericExpression) {
                return this.stmt.getSQLExpressionFactory().invokeOperation("mod", ExpressionUtils.getNumericExpression(this), expr);
            }
        }
        catch (UnsupportedOperationException ex) {}
        return new NumericExpression(this, Expression.OP_MOD, expr);
    }
    
    @Override
    public SQLExpression neg() {
        return new NumericExpression(Expression.OP_NEG, ExpressionUtils.getNumericExpression(this));
    }
    
    @Override
    public SQLExpression com() {
        return ExpressionUtils.getNumericExpression(this).neg().sub(new IntegerLiteral(this.stmt, this.mapping, BigInteger.ONE, null));
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, Character.class.getName(), methodName, this, args);
    }
}
