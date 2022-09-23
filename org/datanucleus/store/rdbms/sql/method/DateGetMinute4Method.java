// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class DateGetMinute4Method extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (!(expr instanceof TemporalExpression)) {
            throw new NucleusException(DateGetMinute4Method.LOCALISER.msg("060001", "getMinute()", expr));
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final JavaTypeMapping mapping = storeMgr.getMappingManager().getMapping(String.class);
        final SQLExpression mi = this.exprFactory.newLiteral(this.stmt, mapping, "mi");
        ((StringLiteral)mi).generateStatementWithoutQuotes();
        final ArrayList funcArgs = new ArrayList();
        funcArgs.add(mi);
        funcArgs.add(expr);
        return new NumericExpression(this.stmt, this.getMappingForClass(Integer.TYPE), "DATEPART", funcArgs);
    }
}
