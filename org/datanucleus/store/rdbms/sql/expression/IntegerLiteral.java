// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.math.BigInteger;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class IntegerLiteral extends NumericExpression implements SQLLiteral
{
    private final Number value;
    
    public IntegerLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else {
            if (!(value instanceof Number)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (Number)value;
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
        if (expr instanceof IntegerLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), new BigInteger(this.value.toString()).compareTo(new BigInteger(((IntegerLiteral)expr).value.toString())) == 0);
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
        if (expr instanceof IntegerLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), new BigInteger(this.value.toString()).compareTo(new BigInteger(((IntegerLiteral)expr).value.toString())) != 0);
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
        if (expr instanceof IntegerLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), new BigInteger(this.value.toString()).compareTo(new BigInteger(((IntegerLiteral)expr).value.toString())) < 0);
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
        if (expr instanceof IntegerLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), new BigInteger(this.value.toString()).compareTo(new BigInteger(((IntegerLiteral)expr).value.toString())) <= 0);
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
        if (expr instanceof IntegerLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), new BigInteger(this.value.toString()).compareTo(new BigInteger(((IntegerLiteral)expr).value.toString())) > 0);
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
        if (expr instanceof IntegerLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), new BigInteger(this.value.toString()).compareTo(new BigInteger(((IntegerLiteral)expr).value.toString())) >= 0);
        }
        if (expr instanceof CharacterExpression) {
            final CharacterLiteral literal = new CharacterLiteral(this.stmt, this.mapping, String.valueOf((char)this.value.intValue()), (String)null);
            return new BooleanExpression(literal, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr instanceof StringExpression) {
            if (this.isParameter()) {
                this.stmt.getQueryGenerator().useParameterExpressionAsLiteral(this);
            }
            final StringExpression strExpr = (StringExpression)this.stmt.getSQLExpressionFactory().invokeOperation("numericToString", this, null);
            return new StringExpression(strExpr, Expression.OP_CONCAT, expr);
        }
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_ADD, expr);
        }
        if (expr instanceof IntegerLiteral) {
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).add(new BigInteger(((IntegerLiteral)expr).value.toString())), null);
        }
        if (expr instanceof CharacterLiteral) {
            final int v = ((CharacterLiteral)expr).getValue().toString().charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).add(new BigInteger("" + v)), null);
        }
        return super.add(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_SUB, expr);
        }
        if (expr instanceof IntegerLiteral) {
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).subtract(new BigInteger(((IntegerLiteral)expr).value.toString())), null);
        }
        if (expr instanceof CharacterLiteral) {
            final int v = ((CharacterLiteral)expr).getValue().toString().charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).subtract(new BigInteger("" + v)), null);
        }
        return super.sub(expr);
    }
    
    @Override
    public SQLExpression mul(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_MUL, expr);
        }
        if (expr instanceof IntegerLiteral) {
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).multiply(new BigInteger(((IntegerLiteral)expr).value.toString())), null);
        }
        if (expr instanceof CharacterLiteral) {
            final int v = ((CharacterLiteral)expr).getValue().toString().charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).multiply(new BigInteger("" + v)), null);
        }
        return super.mul(expr);
    }
    
    @Override
    public SQLExpression div(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new NumericExpression(this, Expression.OP_DIV, expr);
        }
        if (expr instanceof IntegerLiteral) {
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).divide(new BigInteger(((IntegerLiteral)expr).value.toString())), null);
        }
        if (expr instanceof CharacterLiteral) {
            final int v = ((CharacterLiteral)expr).getValue().toString().charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).divide(new BigInteger("" + v)), null);
        }
        return super.div(expr);
    }
    
    @Override
    public SQLExpression mod(final SQLExpression expr) {
        if (expr instanceof IntegerLiteral) {
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).mod(new BigInteger(((IntegerLiteral)expr).value.toString())), null);
        }
        if (expr instanceof CharacterLiteral) {
            final int v = ((CharacterLiteral)expr).getValue().toString().charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).mod(new BigInteger("" + v)), null);
        }
        return super.mod(expr);
    }
    
    @Override
    public SQLExpression neg() {
        return new IntegerLiteral(this.stmt, this.mapping, new BigInteger(this.value.toString()).negate(), this.parameterName);
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
