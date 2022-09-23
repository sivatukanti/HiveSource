// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.util.StringUtils;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.math.BigDecimal;

public class FloatingPointLiteral extends NumericExpression implements SQLLiteral
{
    private final BigDecimal value;
    
    public FloatingPointLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else if (value instanceof Float) {
            this.value = new BigDecimal(((Float)value).toString());
        }
        else if (value instanceof Double) {
            this.value = new BigDecimal(((Double)value).toString());
        }
        else {
            if (!(value instanceof BigDecimal)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (BigDecimal)value;
        }
        if (parameterName != null) {
            this.st.appendParameter(parameterName, mapping, this.value);
        }
        else {
            this.setStatement();
        }
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((FloatingPointLiteral)expr).value) == 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(expr, Expression.OP_EQ, literal);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((FloatingPointLiteral)expr).value) != 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(expr, Expression.OP_NOTEQ, literal);
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((FloatingPointLiteral)expr).value) < 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(literal, Expression.OP_LT, expr);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((FloatingPointLiteral)expr).value) <= 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(literal, Expression.OP_LTEQ, expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((FloatingPointLiteral)expr).value) > 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(literal, Expression.OP_GT, expr);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((FloatingPointLiteral)expr).value) >= 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(literal, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_ADD, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new FloatingPointLiteral(this.stmt, this.mapping, this.value.add(((FloatingPointLiteral)expr).value), null);
        }
        return super.add(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_SUB, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new FloatingPointLiteral(this.stmt, this.mapping, this.value.subtract(((FloatingPointLiteral)expr).value), null);
        }
        return super.sub(expr);
    }
    
    @Override
    public SQLExpression mul(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_MUL, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new FloatingPointLiteral(this.stmt, this.mapping, this.value.multiply(((FloatingPointLiteral)expr).value), null);
        }
        return super.mul(expr);
    }
    
    @Override
    public SQLExpression div(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_DIV, expr);
        }
        if (expr instanceof FloatingPointLiteral) {
            return new FloatingPointLiteral(this.stmt, this.mapping, this.value.divide(((FloatingPointLiteral)expr).value, 1), null);
        }
        return super.div(expr);
    }
    
    @Override
    public SQLExpression neg() {
        return new FloatingPointLiteral(this.stmt, this.mapping, this.value.negate(), null);
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void setNotParameter() {
        if (this.parameterName == null) {
            return;
        }
        this.parameterName = null;
        this.st.clearStatement();
        this.setStatement();
    }
    
    protected void setStatement() {
        this.st.append(StringUtils.exponentialFormatBigDecimal(this.value));
    }
}
