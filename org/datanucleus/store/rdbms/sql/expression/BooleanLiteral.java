// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class BooleanLiteral extends BooleanExpression implements SQLLiteral
{
    private final Boolean value;
    
    public BooleanLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
        }
        else {
            if (!(value instanceof Boolean)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            this.value = (Boolean)value;
        }
        if (parameterName != null) {
            this.st.appendParameter(parameterName, mapping, this.value);
        }
        else {
            this.setStatement();
        }
    }
    
    public BooleanLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Boolean value) {
        super(stmt, null, mapping);
        this.value = value;
        this.hasClosure = true;
        this.setStatement();
    }
    
    @Override
    public void setJavaTypeMapping(final JavaTypeMapping mapping) {
        super.setJavaTypeMapping(mapping);
        this.st.clearStatement();
        if (this.parameterName != null) {
            this.st.appendParameter(this.parameterName, mapping, this.value);
        }
        else {
            this.setStatement();
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public BooleanExpression and(final SQLExpression expr) {
        if (expr instanceof BooleanExpression) {
            return this.value ? ((BooleanExpression)expr) : this;
        }
        return super.and(expr);
    }
    
    @Override
    public BooleanExpression eor(final SQLExpression expr) {
        if (expr instanceof BooleanExpression) {
            return (BooleanExpression)(this.value ? expr.not() : expr);
        }
        return super.eor(expr);
    }
    
    @Override
    public BooleanExpression ior(final SQLExpression expr) {
        if (expr instanceof BooleanExpression) {
            return ((boolean)this.value) ? this : ((BooleanExpression)expr);
        }
        return super.ior(expr);
    }
    
    @Override
    public BooleanExpression not() {
        if (this.hasClosure) {
            return new BooleanLiteral(this.stmt, this.mapping, !this.value);
        }
        return new BooleanLiteral(this.stmt, this.mapping, !this.value, null);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof BooleanLiteral) {
            final BooleanLiteral exprLit = (BooleanLiteral)expr;
            return new BooleanLiteral(this.stmt, this.mapping, this.value == exprLit.value);
        }
        if (!(expr instanceof BooleanExpression)) {
            return super.eq(expr);
        }
        final DatastoreMapping datastoreMapping = expr.mapping.getDatastoreMapping(0);
        if (datastoreMapping.isStringBased()) {
            return new BooleanExpression(expr, Expression.OP_EQ, new CharacterLiteral(this.stmt, this.mapping, this.value ? "Y" : "N", (String)null));
        }
        if (datastoreMapping.isIntegerBased() || (datastoreMapping.isBitBased() && !this.stmt.getDatastoreAdapter().supportsOption("BitIsReallyBoolean"))) {
            return new BooleanExpression(expr, Expression.OP_EQ, new IntegerLiteral(this.stmt, this.mapping, (int)(((boolean)this.value) ? 1 : 0), null));
        }
        if (this.stmt.getDatastoreAdapter().supportsOption("BooleanExpression")) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        return this.and(expr).ior(this.not().and(expr.not()));
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        if (expr instanceof BooleanLiteral) {
            final BooleanLiteral exprLit = (BooleanLiteral)expr;
            return new BooleanLiteral(this.stmt, this.mapping, this.value != exprLit.value);
        }
        if (!(expr instanceof BooleanExpression)) {
            return super.ne(expr);
        }
        final DatastoreMapping datastoreMapping = expr.mapping.getDatastoreMapping(0);
        if (datastoreMapping.isStringBased()) {
            return new BooleanExpression(expr, Expression.OP_NOTEQ, new CharacterLiteral(this.stmt, this.mapping, this.value ? "Y" : "N", (String)null));
        }
        if (datastoreMapping.isIntegerBased() || (datastoreMapping.isBitBased() && !this.stmt.getDatastoreAdapter().supportsOption("BitIsReallyBoolean"))) {
            return new BooleanExpression(expr, Expression.OP_NOTEQ, new IntegerLiteral(this.stmt, this.mapping, (int)(((boolean)this.value) ? 1 : 0), null));
        }
        if (this.stmt.getDatastoreAdapter().supportsOption("BooleanExpression")) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        return this.and(expr.not()).ior(this.not().and(expr));
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
        if (this.hasClosure) {
            this.st.append(this.value ? "TRUE" : "(1=0)");
        }
        else {
            final DatastoreMapping datastoreMapping = this.mapping.getDatastoreMapping(0);
            if (datastoreMapping.isStringBased()) {
                this.st.append(this.value ? "'Y'" : "'N'");
            }
            else if (datastoreMapping.isIntegerBased() || (datastoreMapping.isBitBased() && !this.stmt.getDatastoreAdapter().supportsOption("BitIsReallyBoolean"))) {
                this.st.append(this.value ? "1" : "0");
            }
            else {
                this.st.append(this.value ? "TRUE" : "(1=0)");
            }
        }
    }
}
