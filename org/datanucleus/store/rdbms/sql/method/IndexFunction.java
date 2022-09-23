// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.table.ClassTable;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class IndexFunction extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression ignore, final List args) {
        if (ignore != null) {
            throw new NucleusException(IndexFunction.LOCALISER.msg("060002", "INDEX", ignore));
        }
        if (args == null || args.size() != 2) {
            throw new NucleusException("INDEX can only be used with 2 arguments - the element expression, and the collection expression");
        }
        final SQLExpression elemSqlExpr = args.get(0);
        final SQLExpression collSqlExpr = args.get(1);
        final AbstractMemberMetaData mmd = collSqlExpr.getJavaTypeMapping().getMemberMetaData();
        if (!mmd.hasCollection()) {
            throw new NucleusException("INDEX expression for field " + mmd.getFullFieldName() + " does not represent a collection!");
        }
        if (!mmd.getOrderMetaData().isIndexedList()) {
            throw new NucleusException("INDEX expression for field " + mmd.getFullFieldName() + " does not represent an indexed list!");
        }
        JavaTypeMapping orderMapping = null;
        SQLTable orderTable = null;
        final Table joinTbl = this.stmt.getRDBMSManager().getTable(mmd);
        if (joinTbl != null) {
            final CollectionTable collTable = (CollectionTable)joinTbl;
            orderTable = this.stmt.getTableForDatastoreContainer(collTable);
            orderMapping = collTable.getOrderMapping();
        }
        else {
            orderTable = elemSqlExpr.getSQLTable();
            orderMapping = ((ClassTable)elemSqlExpr.getSQLTable().getTable()).getExternalMapping(mmd, 4);
        }
        return new NumericExpression(this.stmt, orderTable, orderMapping);
    }
}
