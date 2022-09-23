// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringToUpperMethod extends SimpleStringMethod
{
    @Override
    protected String getFunctionName() {
        return "UPPER";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && !args.isEmpty()) {
            throw new NucleusException(StringToUpperMethod.LOCALISER.msg("060015", "toUpperCase", "StringExpression"));
        }
        if (!expr.isParameter() && expr instanceof StringLiteral) {
            String val = (String)((StringLiteral)expr).getValue();
            if (val != null) {
                val = val.toUpperCase();
            }
            return new StringLiteral(this.stmt, expr.getJavaTypeMapping(), val, (String)null);
        }
        return super.getExpression(expr, null);
    }
}
