// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.List;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class BooleanExpression extends SQLExpression
{
    boolean hasClosure;
    
    public BooleanExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String sql) {
        super(stmt, null, mapping);
        this.hasClosure = false;
        this.st.clearStatement();
        this.st.append(sql);
    }
    
    public BooleanExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
        this.hasClosure = false;
    }
    
    public BooleanExpression(final SQLStatement stmt, final JavaTypeMapping mapping) {
        super(stmt, null, mapping);
        this.hasClosure = false;
        this.hasClosure = true;
    }
    
    public BooleanExpression(final Expression.MonadicOperator op, final SQLExpression expr1) {
        super(op, expr1);
        this.hasClosure = false;
        this.hasClosure = true;
    }
    
    public BooleanExpression(final SQLExpression expr1, final Expression.DyadicOperator op, final SQLExpression expr2) {
        super(expr1, op, expr2);
        this.hasClosure = false;
        if (op == Expression.OP_EQ || op == Expression.OP_GT || op == Expression.OP_GTEQ || op == Expression.OP_NOTEQ || op == Expression.OP_LT || op == Expression.OP_LTEQ) {
            this.mapping = this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false);
        }
        else if ((op == Expression.OP_IS || op == Expression.OP_ISNOT) && (expr1 instanceof NullLiteral || expr2 instanceof NullLiteral)) {
            this.mapping = this.stmt.getSQLExpressionFactory().getMappingForType(Boolean.TYPE, false);
        }
        this.hasClosure = true;
    }
    
    public boolean hasClosure() {
        return this.hasClosure;
    }
    
    @Override
    public BooleanExpression and(final SQLExpression expr) {
        if (expr instanceof BooleanLiteral) {
            return expr.and(this);
        }
        if (expr instanceof BooleanExpression) {
            BooleanExpression left = this;
            BooleanExpression right = (BooleanExpression)expr;
            if (!left.hasClosure()) {
                left = left.eq(new BooleanLiteral(this.stmt, this.mapping, Boolean.TRUE));
            }
            if (!right.hasClosure()) {
                right = right.eq(new BooleanLiteral(this.stmt, this.mapping, Boolean.TRUE));
            }
            return new BooleanExpression(left, Expression.OP_AND, right);
        }
        return super.and(expr);
    }
    
    @Override
    public BooleanExpression eor(final SQLExpression expr) {
        if (expr instanceof BooleanLiteral) {
            return expr.eor(this);
        }
        if (!(expr instanceof BooleanExpression)) {
            return super.eor(expr);
        }
        if (this.stmt.getDatastoreAdapter().supportsOption("BooleanExpression")) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        return this.and(expr.not()).ior(this.not().and(expr));
    }
    
    @Override
    public BooleanExpression ior(final SQLExpression expr) {
        if (expr instanceof BooleanLiteral) {
            return expr.ior(this);
        }
        if (expr instanceof BooleanExpression) {
            BooleanExpression left = this;
            BooleanExpression right = (BooleanExpression)expr;
            if (!left.hasClosure()) {
                left = left.eq(new BooleanLiteral(this.stmt, this.mapping, Boolean.TRUE));
            }
            if (!right.hasClosure()) {
                right = right.eq(new BooleanLiteral(this.stmt, this.mapping, Boolean.TRUE));
            }
            return new BooleanExpression(left, Expression.OP_OR, right);
        }
        return super.ior(expr);
    }
    
    @Override
    public BooleanExpression not() {
        if (!this.hasClosure) {
            return new BooleanExpression(this, Expression.OP_EQ, new BooleanLiteral(this.stmt, this.mapping, Boolean.FALSE, null));
        }
        return new BooleanExpression(Expression.OP_NOT, this);
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        if (this.isParameter() || expr.isParameter()) {
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        if (expr instanceof BooleanLiteral || expr instanceof NullLiteral) {
            return expr.eq(this);
        }
        if (!(expr instanceof BooleanExpression)) {
            return super.eq(expr);
        }
        final DatastoreMapping datastoreMapping = this.mapping.getDatastoreMapping(0);
        if (datastoreMapping.isStringBased()) {
            return new BooleanExpression(new CharacterExpression(this.stmt, this.table, this.mapping), Expression.OP_EQ, new CharacterExpression(this.stmt, expr.table, expr.mapping));
        }
        if (datastoreMapping.isIntegerBased() || (datastoreMapping.isBitBased() && !this.stmt.getDatastoreAdapter().supportsOption("BitIsReallyBoolean"))) {
            return new BooleanExpression(new NumericExpression(this.stmt, this.table, this.mapping), Expression.OP_EQ, new NumericExpression(this.stmt, expr.table, expr.mapping));
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
        if (expr instanceof BooleanLiteral || expr instanceof NullLiteral) {
            return expr.ne(this);
        }
        if (!(expr instanceof BooleanExpression)) {
            return super.ne(expr);
        }
        final DatastoreMapping datastoreMapping = this.mapping.getDatastoreMapping(0);
        if (datastoreMapping.isStringBased()) {
            return new BooleanExpression(new CharacterExpression(this.stmt, this.table, this.mapping), Expression.OP_NOTEQ, new CharacterExpression(this.stmt, expr.table, expr.mapping));
        }
        if (datastoreMapping.isIntegerBased() || (datastoreMapping.isBitBased() && !this.stmt.getDatastoreAdapter().supportsOption("BitIsReallyBoolean"))) {
            return new BooleanExpression(new NumericExpression(this.stmt, this.table, this.mapping), Expression.OP_NOTEQ, new NumericExpression(this.stmt, expr.table, expr.mapping));
        }
        if (this.stmt.getDatastoreAdapter().supportsOption("BooleanExpression")) {
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        return this.and(expr.not()).ior(this.not().and(expr));
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        final DatastoreMapping datastoreMapping = this.mapping.getDatastoreMapping(0);
        if (datastoreMapping.isStringBased()) {
            return new BooleanExpression(new CharacterExpression(this.stmt, this.table, this.mapping), not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
        }
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, Boolean.class.getName(), methodName, this, args);
    }
    
    @Override
    public BooleanExpression neg() {
        return new BooleanExpression(Expression.OP_NEG, this);
    }
}
