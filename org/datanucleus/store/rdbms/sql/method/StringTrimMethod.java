// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringTrimMethod extends SimpleStringMethod
{
    @Override
    protected String getFunctionName() {
        return "TRIM";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!expr.isParameter() && expr instanceof StringLiteral) {
            String val = (String)((StringLiteral)expr).getValue();
            if (val != null) {
                val = val.trim();
            }
            return new StringLiteral(this.stmt, expr.getJavaTypeMapping(), val, (String)null);
        }
        return super.getExpression(expr, args);
    }
}
