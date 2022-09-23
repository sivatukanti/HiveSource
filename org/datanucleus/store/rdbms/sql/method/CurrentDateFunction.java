// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import java.util.Date;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class CurrentDateFunction extends AbstractSQLMethod
{
    protected String getFunctionName() {
        return "CURRENT_DATE";
    }
    
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (expr == null) {
            final SQLExpression dateExpr = new TemporalExpression(this.stmt, this.getMappingForClass(this.getClassForMapping()), this.getFunctionName(), args);
            dateExpr.toSQLText().clearStatement();
            dateExpr.toSQLText().append(this.getFunctionName());
            return dateExpr;
        }
        throw new NucleusException(CurrentDateFunction.LOCALISER.msg("060002", this.getFunctionName(), expr));
    }
    
    protected Class getClassForMapping() {
        return Date.class;
    }
}
