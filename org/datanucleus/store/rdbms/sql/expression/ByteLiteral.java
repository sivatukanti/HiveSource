// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.math.BigInteger;

public class ByteLiteral extends NumericExpression implements SQLLiteral
{
    private final BigInteger value;
    
    public ByteLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else if (value instanceof BigInteger) {
            this.value = (BigInteger)value;
        }
        else {
            if (!(value instanceof Byte)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = BigInteger.valueOf((long)value);
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
        if (expr instanceof ByteLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((ByteLiteral)expr).value) == 0);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((ByteLiteral)expr).value) != 0);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof ByteLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((ByteLiteral)expr).value) < 0);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((ByteLiteral)expr).value) <= 0);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof ByteLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((ByteLiteral)expr).value) > 0);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof ByteLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((ByteLiteral)expr).value) >= 0);
        }
        if (expr instanceof CharacterExpression) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr instanceof ByteLiteral) {
            return new ByteLiteral(this.stmt, this.mapping, this.value.add(((ByteLiteral)expr).value), null);
        }
        return super.add(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        if (expr instanceof ByteLiteral) {
            return new ByteLiteral(this.stmt, this.mapping, this.value.subtract(((ByteLiteral)expr).value), null);
        }
        return super.sub(expr);
    }
    
    @Override
    public SQLExpression mul(final SQLExpression expr) {
        if (expr instanceof ByteLiteral) {
            return new ByteLiteral(this.stmt, this.mapping, this.value.multiply(((ByteLiteral)expr).value), null);
        }
        return super.mul(expr);
    }
    
    @Override
    public SQLExpression div(final SQLExpression expr) {
        if (expr instanceof ByteLiteral) {
            return new ByteLiteral(this.stmt, this.mapping, this.value.divide(((ByteLiteral)expr).value), null);
        }
        return super.div(expr);
    }
    
    @Override
    public SQLExpression mod(final SQLExpression expr) {
        if (expr instanceof ByteLiteral) {
            return new ByteLiteral(this.stmt, this.mapping, this.value.mod(((ByteLiteral)expr).value), null);
        }
        return super.mod(expr);
    }
    
    @Override
    public SQLExpression neg() {
        return new ByteLiteral(this.stmt, this.mapping, this.value.negate(), null);
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
        this.st.append(String.valueOf(this.value));
    }
}
