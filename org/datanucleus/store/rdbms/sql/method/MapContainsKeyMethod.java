// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.store.rdbms.sql.SQLJoin;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.store.rdbms.sql.expression.BooleanLiteral;
import java.util.Map;
import org.datanucleus.store.rdbms.sql.expression.MapLiteral;
import org.datanucleus.store.rdbms.sql.expression.MapExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class MapContainsKeyMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 1) {
            throw new NucleusException(MapContainsKeyMethod.LOCALISER.msg("060016", "containsKey", "MapExpression", 1));
        }
        final MapExpression mapExpr = (MapExpression)expr;
        final SQLExpression keyExpr = args.get(0);
        if (keyExpr.isParameter()) {
            final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
            if (mmd != null && mmd.getMap() != null) {
                final Class keyCls = this.stmt.getQueryGenerator().getClassLoaderResolver().classForName(mmd.getMap().getKeyType());
                this.stmt.getQueryGenerator().bindParameter(keyExpr.getParameterName(), keyCls);
            }
        }
        if (expr instanceof MapLiteral) {
            final MapLiteral lit = (MapLiteral)expr;
            final Map map = (Map)lit.getValue();
            if (map == null || map.size() == 0) {
                final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
                return new BooleanLiteral(this.stmt, m, Boolean.FALSE);
            }
            final MapLiteral.MapKeyLiteral mapKeyLiteral = lit.getKeyLiteral();
            BooleanExpression bExpr = null;
            final List<SQLExpression> elementExprs = mapKeyLiteral.getKeyExpressions();
            for (int i = 0; i < elementExprs.size(); ++i) {
                if (bExpr == null) {
                    bExpr = elementExprs.get(i).eq(keyExpr);
                }
                else {
                    bExpr = bExpr.ior(elementExprs.get(i).eq(keyExpr));
                }
            }
            bExpr.encloseInParentheses();
            return bExpr;
        }
        else {
            if (this.stmt.getQueryGenerator().getCompilationComponent() != CompilationComponent.FILTER) {
                return this.containsAsSubquery(mapExpr, keyExpr);
            }
            final boolean needsSubquery = this.getNeedsSubquery();
            if (needsSubquery) {
                NucleusLogger.QUERY.debug("map.containsKey on " + mapExpr + "(" + keyExpr + ") using SUBQUERY");
                return this.containsAsSubquery(mapExpr, keyExpr);
            }
            NucleusLogger.QUERY.debug("map.containsKey on " + mapExpr + "(" + keyExpr + ") using INNERJOIN");
            return this.containsAsInnerJoin(mapExpr, keyExpr);
        }
    }
    
    protected boolean getNeedsSubquery() {
        boolean needsSubquery = false;
        final Boolean hasOR = (Boolean)this.stmt.getQueryGenerator().getProperty("Filter.OR");
        if (hasOR != null && hasOR) {
            needsSubquery = true;
        }
        final Boolean hasNOT = (Boolean)this.stmt.getQueryGenerator().getProperty("Filter.NOT");
        if (hasNOT != null && hasNOT) {
            needsSubquery = true;
        }
        return needsSubquery;
    }
    
    protected SQLExpression containsAsInnerJoin(final MapExpression mapExpr, SQLExpression keyExpr) {
        boolean keyIsUnbound = keyExpr instanceof UnboundExpression;
        String varName = null;
        String keyAlias = null;
        if (keyIsUnbound) {
            varName = ((UnboundExpression)keyExpr).getVariableName();
            NucleusLogger.QUERY.debug("map.containsKey(" + keyExpr + ") binding unbound variable " + varName + " using INNER JOIN");
        }
        else if (!this.stmt.getQueryGenerator().hasExplicitJoins()) {
            final SQLJoin.JoinType joinType = this.stmt.getJoinTypeForTable(keyExpr.getSQLTable());
            if (joinType == SQLJoin.JoinType.CROSS_JOIN) {
                keyAlias = this.stmt.removeCrossJoin(keyExpr.getSQLTable());
                keyIsUnbound = true;
                NucleusLogger.QUERY.debug("map.containsKey(" + keyExpr + ") was previously bound as CROSS JOIN but changing to INNER JOIN");
            }
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData keyCmd = mmd.getMap().getKeyClassMetaData(this.clr, mmgr);
        if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            final MapTable mapTbl = (MapTable)storeMgr.getTable(mmd);
            final SQLTable joinSqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), mapTbl, null, mapTbl.getOwnerMapping(), null, null);
            if (keyCmd != null) {
                if (keyIsUnbound) {
                    final DatastoreClass keyTbl = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
                    final SQLTable keySqlTbl = this.stmt.innerJoin(joinSqlTbl, mapTbl.getKeyMapping(), keyTbl, keyAlias, keyTbl.getIdMapping(), null, null);
                    keyExpr = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTbl.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression keyIdExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getKeyMapping());
                    this.stmt.whereAnd(keyIdExpr.eq(keyExpr), true);
                }
            }
            else if (keyIsUnbound) {
                keyExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getKeyMapping());
                this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
            }
            else {
                final SQLExpression keyIdExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getKeyMapping());
                this.stmt.whereAnd(keyIdExpr.eq(keyExpr), true);
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final AbstractClassMetaData valCmd = mmd.getMap().getValueClassMetaData(this.clr, mmgr);
            final DatastoreClass valTbl = storeMgr.getDatastoreClass(valCmd.getFullClassName(), this.clr);
            final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(mmd.getKeyMetaData().getMappedBy());
            JavaTypeMapping ownerMapping = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping = valTbl.getMemberMapping(valCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping = valTbl.getExternalMapping(mmd, 5);
            }
            final SQLTable valSqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), valTbl, null, ownerMapping, null, null);
            if (keyCmd != null) {
                final DatastoreClass keyTbl2 = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
                final SQLTable keySqlTbl2 = this.stmt.innerJoin(valSqlTbl, valTbl.getMemberMapping(valKeyMmd), keyTbl2, keyAlias, keyTbl2.getIdMapping(), null, null);
                if (keyIsUnbound) {
                    keyExpr = this.exprFactory.newExpression(this.stmt, keySqlTbl2, keyTbl2.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression keyIdExpr2 = this.exprFactory.newExpression(this.stmt, keySqlTbl2, keyTbl2.getIdMapping());
                    this.stmt.whereAnd(keyIdExpr2.eq(keyExpr), true);
                }
            }
            else if (keyIsUnbound) {
                keyExpr = this.exprFactory.newExpression(this.stmt, valSqlTbl, valTbl.getMemberMapping(valKeyMmd));
                this.stmt.getQueryGenerator().bindVariable(varName, null, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
            }
            else {
                final SQLExpression keyIdExpr3 = this.exprFactory.newExpression(this.stmt, valSqlTbl, valTbl.getMemberMapping(valKeyMmd));
                this.stmt.whereAnd(keyIdExpr3.eq(keyExpr), true);
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            final DatastoreClass keyTbl3 = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
            JavaTypeMapping ownerMapping2 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping2 = keyTbl3.getMemberMapping(keyCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping2 = keyTbl3.getExternalMapping(mmd, 5);
            }
            final SQLTable keySqlTbl3 = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), keyTbl3, keyAlias, ownerMapping2, null, null);
            if (keyIsUnbound) {
                keyExpr = this.exprFactory.newExpression(this.stmt, keySqlTbl3, keyTbl3.getIdMapping());
                this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
            }
            else {
                final SQLExpression keyIdExpr4 = this.exprFactory.newExpression(this.stmt, keySqlTbl3, keyTbl3.getIdMapping());
                this.stmt.whereAnd(keyIdExpr4.eq(keyExpr), true);
            }
        }
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
        return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, true));
    }
    
    protected SQLExpression containsAsSubquery(final MapExpression mapExpr, SQLExpression keyExpr) {
        final boolean keyIsUnbound = keyExpr instanceof UnboundExpression;
        String varName = null;
        if (keyIsUnbound) {
            varName = ((UnboundExpression)keyExpr).getVariableName();
            NucleusLogger.QUERY.debug("map.containsKey binding unbound variable " + varName + " using SUBQUERY");
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData keyCmd = mmd.getMap().getKeyClassMetaData(this.clr, mmgr);
        final MapTable joinTbl = (MapTable)storeMgr.getTable(mmd);
        SQLStatement subStmt = null;
        if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            if (keyCmd == null) {
                subStmt = new SQLStatement(this.stmt, storeMgr, joinTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping, 1), null);
                final JavaTypeMapping ownerMapping = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
                final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr.eq(ownerIdExpr), true);
                if (keyIsUnbound) {
                    keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getKeyMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression elemIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getKeyMapping());
                    subStmt.whereAnd(elemIdExpr.eq(keyExpr), true);
                }
            }
            else {
                final DatastoreClass keyTbl = storeMgr.getDatastoreClass(mmd.getMap().getKeyType(), this.clr);
                subStmt = new SQLStatement(this.stmt, storeMgr, keyTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping2 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping2, 1), null);
                final SQLTable joinSqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), keyTbl.getIdMapping(), joinTbl, null, joinTbl.getKeyMapping(), null, null);
                final JavaTypeMapping ownerMapping2 = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr2 = this.exprFactory.newExpression(subStmt, joinSqlTbl, ownerMapping2);
                final SQLExpression ownerIdExpr2 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr2.eq(ownerIdExpr2), true);
                if (keyIsUnbound) {
                    keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression keyIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl.getIdMapping());
                    subStmt.whereAnd(keyIdExpr.eq(keyExpr), true);
                }
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final AbstractClassMetaData valCmd = mmd.getMap().getValueClassMetaData(this.clr, mmgr);
            final DatastoreClass valTbl = storeMgr.getDatastoreClass(mmd.getMap().getValueType(), this.clr);
            JavaTypeMapping ownerMapping3 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping3 = valTbl.getMemberMapping(valCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping3 = valTbl.getExternalMapping(mmd, 5);
            }
            final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(mmd.getKeyMetaData().getMappedBy());
            if (keyCmd == null) {
                subStmt = new SQLStatement(this.stmt, storeMgr, valTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping3 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping3, 1), null);
                final SQLExpression ownerExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping3);
                final SQLExpression ownerIdExpr3 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr3.eq(ownerIdExpr3), true);
                if (keyIsUnbound) {
                    keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl.getMemberMapping(valKeyMmd));
                    this.stmt.getQueryGenerator().bindVariable(varName, null, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
                }
                else {
                    final JavaTypeMapping keyMapping = valTbl.getMemberMapping(valKeyMmd);
                    final SQLExpression elemIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyMapping);
                    subStmt.whereAnd(elemIdExpr2.eq(keyExpr), true);
                }
            }
            else {
                final DatastoreClass keyTbl2 = storeMgr.getDatastoreClass(mmd.getMap().getKeyType(), this.clr);
                subStmt = new SQLStatement(this.stmt, storeMgr, keyTbl2, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping4 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping4, 1), null);
                final SQLTable valSqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), keyTbl2.getIdMapping(), valTbl, null, valTbl.getMemberMapping(valKeyMmd), null, null);
                final SQLExpression ownerExpr4 = this.exprFactory.newExpression(subStmt, valSqlTbl, ownerMapping3);
                final SQLExpression ownerIdExpr4 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr4.eq(ownerIdExpr4), true);
                if (keyIsUnbound) {
                    keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl2.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression keyIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl2.getIdMapping());
                    subStmt.whereAnd(keyIdExpr2.eq(keyExpr), true);
                }
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            final DatastoreClass keyTbl = storeMgr.getDatastoreClass(mmd.getMap().getKeyType(), this.clr);
            JavaTypeMapping ownerMapping = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping = keyTbl.getMemberMapping(keyCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping = keyTbl.getExternalMapping(mmd, 5);
            }
            subStmt = new SQLStatement(this.stmt, storeMgr, keyTbl, null, null);
            subStmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping oneMapping5 = storeMgr.getMappingManager().getMapping(Integer.class);
            subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping5, 1), null);
            final SQLExpression ownerExpr5 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
            final SQLExpression ownerIdExpr5 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
            subStmt.whereAnd(ownerExpr5.eq(ownerIdExpr5), true);
            if (keyIsUnbound) {
                keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl.getIdMapping());
                this.stmt.getQueryGenerator().bindVariable(varName, keyCmd, keyExpr.getSQLTable(), keyExpr.getJavaTypeMapping());
            }
            else {
                final JavaTypeMapping keyMapping2 = keyTbl.getIdMapping();
                final SQLExpression keyIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyMapping2);
                subStmt.whereAnd(keyIdExpr.eq(keyExpr), true);
            }
        }
        return new BooleanSubqueryExpression(this.stmt, "EXISTS", subStmt);
    }
}
