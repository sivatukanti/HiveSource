// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.SubqueryExpression;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.store.rdbms.sql.expression.CollectionLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.sql.expression.CollectionExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class ListGetMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 1) {
            throw new NucleusException(ListGetMethod.LOCALISER.msg("060016", "get", "CollectionExpression", 1));
        }
        final CollectionExpression listExpr = (CollectionExpression)expr;
        final AbstractMemberMetaData mmd = listExpr.getJavaTypeMapping().getMemberMetaData();
        if (!List.class.isAssignableFrom(mmd.getType())) {
            throw new UnsupportedOperationException("Query contains " + expr + ".get(int) yet the field is not a List!");
        }
        if (mmd.getOrderMetaData() != null && !mmd.getOrderMetaData().isIndexedList()) {
            throw new UnsupportedOperationException("Query contains " + expr + ".get(int) yet the field is not an 'indexed' List!");
        }
        final SQLExpression idxExpr = args.get(0);
        if (!(idxExpr instanceof SQLLiteral)) {
            throw new UnsupportedOperationException("Query contains " + expr + ".get(int) yet the index is not a numeric literal so not yet supported");
        }
        if (!(((SQLLiteral)idxExpr).getValue() instanceof Number)) {
            throw new UnsupportedOperationException("Query contains " + expr + ".get(int) yet the index is not a numeric literal so not yet supported");
        }
        if (listExpr instanceof CollectionLiteral && idxExpr instanceof SQLLiteral) {
            final CollectionLiteral lit = (CollectionLiteral)expr;
            if (lit.getValue() == null) {
                return new NullLiteral(this.stmt, null, null, null);
            }
            return lit.invoke("get", args);
        }
        else {
            if (this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.FILTER) {
                return this.getAsInnerJoin(listExpr, idxExpr);
            }
            if (this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.ORDERING || this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.RESULT || this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.HAVING) {
                return this.getAsSubquery(listExpr, idxExpr);
            }
            throw new NucleusException("List.get() is not supported for " + listExpr + " with argument " + idxExpr + " for query component " + this.stmt.getQueryGenerator().getCompilationComponent());
        }
    }
    
    protected SQLExpression getAsSubquery(final CollectionExpression listExpr, final SQLExpression idxExpr) {
        final AbstractMemberMetaData mmd = listExpr.getJavaTypeMapping().getMemberMetaData();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        JavaTypeMapping ownerMapping = null;
        JavaTypeMapping indexMapping = null;
        JavaTypeMapping elemMapping = null;
        Table listTbl = null;
        if (mmd != null) {
            final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(this.clr);
            if (mmd.getJoinMetaData() != null || (relatedMmds != null && relatedMmds[0].getJoinMetaData() != null)) {
                listTbl = storeMgr.getTable(mmd);
                ownerMapping = ((CollectionTable)listTbl).getOwnerMapping();
                indexMapping = ((CollectionTable)listTbl).getOrderMapping();
                elemMapping = ((CollectionTable)listTbl).getElementMapping();
            }
            else {
                final DatastoreClass elemTbl = (DatastoreClass)(listTbl = storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr));
                if (relatedMmds != null) {
                    ownerMapping = elemTbl.getMemberMapping(relatedMmds[0]);
                    indexMapping = elemTbl.getExternalMapping(mmd, 4);
                    elemMapping = elemTbl.getIdMapping();
                }
                else {
                    ownerMapping = elemTbl.getExternalMapping(mmd, 5);
                    indexMapping = elemTbl.getExternalMapping(mmd, 4);
                    elemMapping = elemTbl.getIdMapping();
                }
            }
        }
        final SQLStatement subStmt = new SQLStatement(this.stmt, storeMgr, listTbl, null, null);
        subStmt.setClassLoaderResolver(this.clr);
        final SQLExpression valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), elemMapping);
        subStmt.select(valExpr, null);
        final SQLExpression elementOwnerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
        final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, listExpr.getSQLTable(), listExpr.getSQLTable().getTable().getIdMapping());
        subStmt.whereAnd(elementOwnerExpr.eq(ownerIdExpr), true);
        final SQLExpression keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), indexMapping);
        subStmt.whereAnd(keyExpr.eq(idxExpr), true);
        final SubqueryExpression subExpr = new SubqueryExpression(this.stmt, subStmt);
        subExpr.setJavaTypeMapping(elemMapping);
        return subExpr;
    }
    
    protected SQLExpression getAsInnerJoin(final CollectionExpression listExpr, final SQLExpression idxExpr) {
        final JavaTypeMapping m = listExpr.getJavaTypeMapping();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final AbstractMemberMetaData mmd = m.getMemberMetaData();
        final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(this.clr);
        if (mmd.getJoinMetaData() != null || (relatedMmds != null && relatedMmds[0].getJoinMetaData() != null)) {
            final CollectionTable joinTbl = (CollectionTable)storeMgr.getTable(mmd);
            final SQLTable joinSqlTbl = this.stmt.innerJoin(listExpr.getSQLTable(), listExpr.getSQLTable().getTable().getIdMapping(), joinTbl, null, joinTbl.getOwnerMapping(), null, null);
            final SQLExpression idxSqlExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getOrderMapping());
            this.stmt.whereAnd(idxSqlExpr.eq(idxExpr), true);
            final SQLExpression valueExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getElementMapping());
            return valueExpr;
        }
        final DatastoreClass elementTbl = storeMgr.getDatastoreClass(mmd.getCollection().getElementType(), this.clr);
        JavaTypeMapping targetMapping = null;
        JavaTypeMapping orderMapping = null;
        if (relatedMmds != null) {
            targetMapping = elementTbl.getMemberMapping(relatedMmds[0]);
            orderMapping = elementTbl.getExternalMapping(mmd, 4);
        }
        else {
            targetMapping = elementTbl.getExternalMapping(mmd, 5);
            orderMapping = elementTbl.getExternalMapping(mmd, 4);
        }
        final SQLTable elemSqlTbl = this.stmt.innerJoin(listExpr.getSQLTable(), listExpr.getSQLTable().getTable().getIdMapping(), elementTbl, null, targetMapping, null, null);
        final SQLExpression idxSqlExpr2 = this.exprFactory.newExpression(this.stmt, elemSqlTbl, orderMapping);
        this.stmt.whereAnd(idxSqlExpr2.eq(idxExpr), true);
        return this.exprFactory.newExpression(this.stmt, elemSqlTbl, elementTbl.getIdMapping());
    }
}
