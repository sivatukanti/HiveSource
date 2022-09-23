// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import java.util.List;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class CharacterLiteral extends CharacterExpression implements SQLLiteral
{
    private final String value;
    
    public CharacterLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else if (value instanceof Character) {
            this.value = ((Character)value).toString();
        }
        else {
            if (!(value instanceof String)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (String)value;
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
        if (expr instanceof ByteExpression) {
            return expr.eq(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.equals(((CharacterLiteral)expr).value));
        }
        return super.eq(expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof ByteExpression) {
            return expr.ne(this);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), !this.value.equals(((CharacterLiteral)expr).value));
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((CharacterLiteral)expr).value) < 0);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((CharacterLiteral)expr).value) <= 0);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((CharacterLiteral)expr).value) > 0);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof CharacterLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((CharacterLiteral)expr).value) >= 0);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr instanceof CharacterLiteral) {
            final int v = this.value.charAt(0) + ((CharacterLiteral)expr).value.charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, v, null);
        }
        if (expr instanceof IntegerLiteral) {
            final int v = this.value.charAt(0) + ((Number)((IntegerLiteral)expr).getValue()).intValue();
            return new IntegerLiteral(this.stmt, this.mapping, v, null);
        }
        return super.add(expr);
    }
    
    @Override
    public SQLExpression sub(final SQLExpression expr) {
        if (expr instanceof CharacterLiteral) {
            final int v = this.value.charAt(0) - ((CharacterLiteral)expr).value.charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, v, null);
        }
        if (expr instanceof IntegerLiteral) {
            final int v = this.value.charAt(0) - ((Number)((IntegerLiteral)expr).getValue()).intValue();
            return new IntegerLiteral(this.stmt, this.mapping, v, null);
        }
        return super.sub(expr);
    }
    
    @Override
    public SQLExpression mod(final SQLExpression expr) {
        if (expr instanceof CharacterLiteral) {
            final int v = this.value.charAt(0) % ((CharacterLiteral)expr).value.charAt(0);
            return new IntegerLiteral(this.stmt, this.mapping, v, null);
        }
        if (expr instanceof IntegerLiteral) {
            final int v = this.value.charAt(0) % ((Number)((IntegerLiteral)expr).getValue()).intValue();
            return new IntegerLiteral(this.stmt, this.mapping, v, null);
        }
        return super.mod(expr);
    }
    
    @Override
    public SQLExpression neg() {
        final int v = -this.value.charAt(0);
        return new IntegerLiteral(this.stmt, this.mapping, v, null);
    }
    
    @Override
    public SQLExpression com() {
        final int v = ~this.value.charAt(0);
        return new IntegerLiteral(this.stmt, this.mapping, v, null);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        if (methodName.equals("toUpperCase")) {
            return new CharacterLiteral(this.stmt, this.mapping, this.value.toUpperCase(), this.parameterName);
        }
        if (methodName.equals("toLowerCase")) {
            return new CharacterLiteral(this.stmt, this.mapping, this.value.toLowerCase(), this.parameterName);
        }
        return super.invoke(methodName, args);
    }
    
    @Override
    public Object getValue() {
        if (this.value == null) {
            return null;
        }
        return this.value.charAt(0);
    }
    
    @Override
    public void setJavaTypeMapping(final JavaTypeMapping m) {
        super.setJavaTypeMapping(m);
        if (!this.isParameter()) {
            this.setStatement();
        }
    }
    
    @Override
    public void setNotParameter() {
        if (this.parameterName == null) {
            return;
        }
        this.parameterName = null;
        this.setStatement();
    }
    
    protected void setStatement() {
        this.st.clearStatement();
        final DatastoreMapping colMapping = this.mapping.getDatastoreMapping(0);
        if (colMapping.isIntegerBased()) {
            this.st.append("" + (int)this.value.charAt(0));
        }
        else {
            this.st.append('\'').append(this.value).append('\'');
        }
    }
}
