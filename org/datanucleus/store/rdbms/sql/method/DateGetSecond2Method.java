// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.StringExpression;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class DateGetSecond2Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!(expr instanceof TemporalExpression)) {
            throw new NucleusException(DateGetSecond2Method.LOCALISER.msg("060001", "getSecond()", expr));
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final JavaTypeMapping mapping = storeMgr.getMappingManager().getMapping(String.class);
        final SQLExpression hh = this.exprFactory.newLiteral(this.stmt, mapping, "SS");
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(expr);
        funcArgs.add(hh);
        final ArrayList funcArgs2 = new ArrayList();
        funcArgs2.add(new StringExpression(this.stmt, mapping, "TO_CHAR", funcArgs));
        return new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "TO_NUMBER", funcArgs2);
    }
}
