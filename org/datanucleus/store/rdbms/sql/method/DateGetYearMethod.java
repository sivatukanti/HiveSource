// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class DateGetYearMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!(expr instanceof TemporalExpression)) {
            throw new NucleusException(DateGetYearMethod.LOCALISER.msg("060001", "getYear()", expr));
        }
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        return new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "YEAR", funcArgs);
    }
}
