// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringTrim2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr instanceof StringLiteral) {
            final String val = (String)((StringLiteral)expr).getValue();
            return new StringLiteral(this.stmt, expr.getJavaTypeMapping(), val.trim(), (String)null);
        }
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        final StringExpression strExpr = new StringExpression(this.stmt, this.getMappingForClass(String.class), "RTRIM", funcArgs);
        args.clear();
        args.add(strExpr);
        return new StringExpression(this.stmt, this.getMappingForClass(String.class), "LTRIM", args);
    }
}
