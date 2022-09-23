// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLText;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringTrim3Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && args.size() > 1) {
            throw new NucleusException("TRIM has incorrect number of args");
        }
        SQLExpression trimCharExpr = null;
        if (args != null && args.size() > 0) {
            trimCharExpr = args.get(0);
        }
        final List trimArgs = new ArrayList();
        if (trimCharExpr == null) {
            trimArgs.add(expr);
        }
        else {
            final StringExpression argExpr = new StringExpression(this.stmt, expr.getJavaTypeMapping(), "NULL", null);
            final SQLText sql = argExpr.toSQLText();
            sql.clearStatement();
            sql.append(this.getTrimSpecKeyword() + " ");
            sql.append(trimCharExpr);
            sql.append(" FROM ");
            sql.append(expr);
            trimArgs.add(argExpr);
        }
        final StringExpression trimExpr = new StringExpression(this.stmt, expr.getJavaTypeMapping(), "TRIM", trimArgs);
        return trimExpr;
    }
    
    protected String getTrimSpecKeyword() {
        return "BOTH";
    }
}
