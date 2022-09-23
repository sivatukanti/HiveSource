// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.NumericSubqueryExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.table.JoinTable;
import java.lang.reflect.Array;
import org.datanucleus.store.rdbms.sql.expression.ArrayLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class ArraySizeMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && args.size() > 0) {
            throw new NucleusException(ArraySizeMethod.LOCALISER.msg("060015", "size/length", "ArrayExpression"));
        }
        if (expr instanceof ArrayLiteral) {
            return this.exprFactory.newLiteral(this.stmt, this.exprFactory.getMappingForType(Integer.TYPE, false), Array.getLength(((ArrayLiteral)expr).getValue()));
        }
        final AbstractMemberMetaData ownerMmd = expr.getJavaTypeMapping().getMemberMetaData();
        final String elementType = ownerMmd.getArray().getElementType();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        JavaTypeMapping ownerMapping = null;
        Table arrayTbl = null;
        if (ownerMmd.getMappedBy() != null) {
            final AbstractMemberMetaData elementMmd = ownerMmd.getRelatedMemberMetaData(this.clr)[0];
            if (ownerMmd.getJoinMetaData() != null || elementMmd.getJoinMetaData() != null) {
                arrayTbl = storeMgr.getTable(ownerMmd);
                ownerMapping = ((JoinTable)arrayTbl).getOwnerMapping();
            }
            else {
                arrayTbl = storeMgr.getDatastoreClass(elementType, this.clr);
                ownerMapping = arrayTbl.getMemberMapping(elementMmd);
            }
        }
        else if (ownerMmd.getJoinMetaData() != null) {
            arrayTbl = storeMgr.getTable(ownerMmd);
            ownerMapping = ((JoinTable)arrayTbl).getOwnerMapping();
        }
        else {
            arrayTbl = storeMgr.getDatastoreClass(elementType, this.clr);
            ownerMapping = ((DatastoreClass)arrayTbl).getExternalMapping(ownerMmd, 5);
        }
        final SQLStatement subStmt = new SQLStatement(this.stmt, storeMgr, arrayTbl, null, null);
        subStmt.setClassLoaderResolver(this.clr);
        final JavaTypeMapping mapping = storeMgr.getMappingManager().getMappingWithDatastoreMapping(String.class, false, false, this.clr);
        final SQLExpression countExpr = this.exprFactory.newLiteral(subStmt, mapping, "COUNT(*)");
        ((StringLiteral)countExpr).generateStatementWithoutQuotes();
        subStmt.select(countExpr, null);
        final SQLExpression elementOwnerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
        final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, expr.getSQLTable(), expr.getSQLTable().getTable().getIdMapping());
        subStmt.whereAnd(elementOwnerExpr.eq(ownerIdExpr), true);
        final JavaTypeMapping subqMapping = this.exprFactory.getMappingForType(Integer.class, false);
        final SQLExpression subqExpr = new NumericSubqueryExpression(this.stmt, subStmt);
        subqExpr.setJavaTypeMapping(subqMapping);
        return subqExpr;
    }
}
