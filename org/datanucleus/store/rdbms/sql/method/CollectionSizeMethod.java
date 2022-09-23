// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.NumericSubqueryExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Collection;
import org.datanucleus.store.rdbms.sql.expression.CollectionLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class CollectionSizeMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && args.size() > 0) {
            throw new NucleusException(CollectionSizeMethod.LOCALISER.msg("060015", "size", "CollectionExpression"));
        }
        if (expr instanceof CollectionLiteral) {
            final Collection coll = (Collection)((CollectionLiteral)expr).getValue();
            return this.exprFactory.newLiteral(this.stmt, this.exprFactory.getMappingForType(Integer.TYPE, false), coll.size());
        }
        final AbstractMemberMetaData mmd = expr.getJavaTypeMapping().getMemberMetaData();
        if (mmd.isSerialized()) {
            throw new NucleusUserException("Cannot perform Collection.size when the collection is being serialised");
        }
        final ApiAdapter api = this.stmt.getRDBMSManager().getApiAdapter();
        final Class elementType = this.clr.classForName(mmd.getCollection().getElementType());
        if (!api.isPersistable(elementType) && mmd.getJoinMetaData() == null) {
            throw new NucleusUserException("Cannot perform Collection.size when the collection<Non-Persistable> is not in a join table");
        }
        final String elementType2 = mmd.getCollection().getElementType();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        JavaTypeMapping ownerMapping = null;
        Table collectionTbl = null;
        if (mmd.getMappedBy() != null) {
            final AbstractMemberMetaData elementMmd = mmd.getRelatedMemberMetaData(this.clr)[0];
            if (mmd.getJoinMetaData() != null || elementMmd.getJoinMetaData() != null) {
                collectionTbl = storeMgr.getTable(mmd);
                ownerMapping = ((JoinTable)collectionTbl).getOwnerMapping();
            }
            else {
                collectionTbl = storeMgr.getDatastoreClass(elementType2, this.clr);
                ownerMapping = collectionTbl.getMemberMapping(elementMmd);
            }
        }
        else if (mmd.getJoinMetaData() != null) {
            collectionTbl = storeMgr.getTable(mmd);
            ownerMapping = ((JoinTable)collectionTbl).getOwnerMapping();
        }
        else {
            collectionTbl = storeMgr.getDatastoreClass(elementType2, this.clr);
            ownerMapping = ((DatastoreClass)collectionTbl).getExternalMapping(mmd, 5);
        }
        final SQLStatement subStmt = new SQLStatement(this.stmt, storeMgr, collectionTbl, null, null);
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
