// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.types.converters.TypeConverter;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class StringLiteral extends StringExpression implements SQLLiteral
{
    private final String value;
    
    public StringLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else if (value instanceof String) {
            this.value = (String)value;
        }
        else if (value instanceof Character) {
            this.value = ((Character)value).toString();
        }
        else {
            Class type = value.getClass();
            if (mapping != null) {
                type = mapping.getJavaType();
            }
            final TypeConverter converter = stmt.getRDBMSManager().getNucleusContext().getTypeManager().getTypeConverterForType(type, String.class);
            if (converter == null) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = converter.toDatastoreType(value);
        }
        if (parameterName != null) {
            this.st.appendParameter(parameterName, mapping, this.value);
        }
        else {
            this.setStatement();
        }
    }
    
    public void generateStatementWithoutQuotes() {
        this.st.clearStatement();
        this.st.append(this.value.replace("'", "''"));
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof StringLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.equals(((StringLiteral)expr).value));
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
        if (expr instanceof StringLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), !this.value.equals(((StringLiteral)expr).value));
        }
        return super.ne(expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        if (expr instanceof StringLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((StringLiteral)expr).value) < 0);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        if (expr instanceof StringLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((StringLiteral)expr).value) <= 0);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        if (expr instanceof StringLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((StringLiteral)expr).value) > 0);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        if (expr instanceof StringLiteral) {
            return new BooleanLiteral(this.stmt, this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false), this.value.compareTo(((StringLiteral)expr).value) >= 0);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression add(final SQLExpression expr) {
        if (expr.isParameter() || this.isParameter()) {
            return super.add(expr);
        }
        if (expr instanceof StringLiteral) {
            return new StringLiteral(this.stmt, this.mapping, this.value.concat(((StringLiteral)expr).value), (String)null);
        }
        if (expr instanceof CharacterLiteral) {
            return new StringLiteral(this.stmt, this.mapping, this.value.concat(((SQLLiteral)expr).getValue().toString()), (String)null);
        }
        if (expr instanceof IntegerLiteral || expr instanceof FloatingPointLiteral || expr instanceof BooleanLiteral) {
            return new StringLiteral(this.stmt, this.mapping, this.value.concat(((SQLLiteral)expr).getValue().toString()), (String)null);
        }
        return super.add(expr);
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
        if (this.value == null) {
            this.st.append('\'').append('\'');
        }
        else {
            this.st.append('\'').append(this.value.replace("'", "''")).append('\'');
        }
    }
}
