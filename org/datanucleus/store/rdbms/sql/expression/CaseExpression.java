// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;

public class CaseExpression extends SQLExpression
{
    public CaseExpression(final SQLExpression[] whenExprs, final SQLExpression[] actionExprs, final SQLExpression elseExpr) {
        super(whenExprs[0].getSQLStatement(), null, null);
        this.st.clearStatement();
        this.st.append("CASE");
        if (actionExprs == null || whenExprs.length != actionExprs.length || whenExprs.length == 0) {
            throw new IllegalArgumentException("CaseExpression must have equal number of WHEN and THEN expressions");
        }
        this.mapping = actionExprs[0].getJavaTypeMapping();
        for (int i = 0; i < whenExprs.length; ++i) {
            this.st.append(" WHEN ").append(whenExprs[i]).append(" THEN ").append(actionExprs[i]);
        }
        if (elseExpr != null) {
            this.st.append(" ELSE ").append(elseExpr);
        }
        this.st.append(" END");
        this.st.encloseInParentheses();
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_EQ, expr);
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_GTEQ, expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_GT, expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_LTEQ, expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        return new BooleanExpression(this, Expression.OP_LT, expr);
    }
}
