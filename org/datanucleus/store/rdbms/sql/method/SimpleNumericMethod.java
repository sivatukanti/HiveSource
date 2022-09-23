// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public abstract class SimpleNumericMethod extends AbstractSQLMethod
{
    protected abstract String getFunctionName();
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr == null) {
            return new NumericExpression(this.stmt, this.getMappingForClass(this.getClassForMapping()), this.getFunctionName(), args);
        }
        throw new NucleusException(SimpleNumericMethod.LOCALISER.msg("060002", this.getFunctionName(), expr));
    }
    
    protected abstract Class getClassForMapping();
}
