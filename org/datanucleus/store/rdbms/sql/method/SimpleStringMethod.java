// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.exceptions.NucleusException;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public abstract class SimpleStringMethod extends AbstractSQLMethod
{
    protected abstract String getFunctionName();
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr == null) {
            return new StringExpression(this.stmt, this.getMappingForClass(String.class), this.getFunctionName(), args);
        }
        if (expr instanceof StringExpression) {
            final ArrayList functionArgs = new ArrayList();
            functionArgs.add(expr);
            return new StringExpression(this.stmt, this.getMappingForClass(String.class), this.getFunctionName(), functionArgs);
        }
        throw new NucleusException(SimpleStringMethod.LOCALISER.msg("060002", this.getFunctionName(), expr));
    }
}
