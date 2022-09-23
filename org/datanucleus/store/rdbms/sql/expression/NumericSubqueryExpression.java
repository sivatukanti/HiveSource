// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.query.expression.Expression;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;

public class NumericSubqueryExpression extends NumericExpression implements SubqueryExpressionComponent
{
    SQLStatement subStatement;
    
    public NumericSubqueryExpression(final SQLStatement stmt, final SQLStatement subStmt) {
        super(stmt, null, (JavaTypeMapping)null);
        this.subStatement = subStmt;
        this.st.append("(");
        this.st.append(subStmt);
        this.st.append(")");
    }
    
    @Override
    public SQLStatement getSubqueryStatement() {
        return this.subStatement;
    }
    
    public void setKeyword(final String keyword) {
        this.st.clearStatement();
        this.st.append(keyword).append(" (").append(this.subStatement).append(")");
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        final BooleanExpression eqExpr = super.eq(expr);
        eqExpr.encloseInParentheses();
        return eqExpr;
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        final BooleanExpression eqExpr = super.ne(expr);
        eqExpr.encloseInParentheses();
        return eqExpr;
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        final BooleanExpression eqExpr = super.lt(expr);
        eqExpr.encloseInParentheses();
        return eqExpr;
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        final BooleanExpression eqExpr = super.le(expr);
        eqExpr.encloseInParentheses();
        return eqExpr;
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        final BooleanExpression eqExpr = super.gt(expr);
        eqExpr.encloseInParentheses();
        return eqExpr;
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        final BooleanExpression eqExpr = super.ge(expr);
        eqExpr.encloseInParentheses();
        return eqExpr;
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        if (methodName.equals("contains")) {
            final SQLExpression sqlExpr = args.get(0);
            return new BooleanExpression(sqlExpr, Expression.OP_IN, this);
        }
        return super.invoke(methodName, args);
    }
}
