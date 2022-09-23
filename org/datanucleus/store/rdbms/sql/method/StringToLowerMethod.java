// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class StringToLowerMethod extends SimpleStringMethod
{
    @Override
    protected String getFunctionName() {
        return "LOWER";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && !args.isEmpty()) {
            throw new NucleusException(StringToLowerMethod.LOCALISER.msg("060015", "toLowerCase", "StringExpression"));
        }
        if (!expr.isParameter() && expr instanceof StringLiteral) {
            String val = (String)((StringLiteral)expr).getValue();
            if (val != null) {
                val = val.toLowerCase();
            }
            return new StringLiteral(this.stmt, expr.getJavaTypeMapping(), val, (String)null);
        }
        return super.getExpression(expr, null);
    }
}
