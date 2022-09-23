// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class DateGetYear3Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!(expr instanceof TemporalExpression)) {
            throw new NucleusException(DateGetYear3Method.LOCALISER.msg("060001", "getYear()", expr));
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final JavaTypeMapping mapping = storeMgr.getMappingManager().getMapping(String.class);
        final SQLExpression day = this.exprFactory.newLiteral(this.stmt, mapping, "year");
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(day);
        funcArgs.add(expr);
        return new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "date_part", funcArgs);
    }
}
