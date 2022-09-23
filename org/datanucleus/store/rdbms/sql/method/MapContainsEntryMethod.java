// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.expression.BooleanSubqueryExpression;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
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

public class MapContainsEntryMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() != 2) {
            throw new NucleusException(MapContainsEntryMethod.LOCALISER.msg("060016", "containsValue", "MapExpression", 2));
        }
        final MapExpression mapExpr = (MapExpression)expr;
        final SQLExpression keyExpr = args.get(0);
        final SQLExpression valExpr = args.get(1);
        if (keyExpr.isParameter()) {
            final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
            if (mmd != null && mmd.getMap() != null) {
                final Class keyCls = this.stmt.getQueryGenerator().getClassLoaderResolver().classForName(mmd.getMap().getKeyType());
                this.stmt.getQueryGenerator().bindParameter(keyExpr.getParameterName(), keyCls);
            }
        }
        if (valExpr.isParameter()) {
            final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
            if (mmd != null && mmd.getMap() != null) {
                final Class valCls = this.stmt.getQueryGenerator().getClassLoaderResolver().classForName(mmd.getMap().getValueType());
                this.stmt.getQueryGenerator().bindParameter(valExpr.getParameterName(), valCls);
            }
        }
        if (mapExpr instanceof MapLiteral) {
            final MapLiteral lit = (MapLiteral)mapExpr;
            final Map map = (Map)lit.getValue();
            if (map == null || map.size() == 0) {
                return new BooleanLiteral(this.stmt, expr.getJavaTypeMapping(), Boolean.FALSE);
            }
            return lit.getValueLiteral().invoke("contains", args);
        }
        else {
            if (this.stmt.getQueryGenerator().getCompilationComponent() != CompilationComponent.FILTER) {
                return this.containsAsSubquery(mapExpr, keyExpr, valExpr);
            }
            final boolean needsSubquery = this.getNeedsSubquery();
            if (needsSubquery) {
                NucleusLogger.QUERY.debug("MapContainsEntry on " + mapExpr + "(" + keyExpr + "," + valExpr + ") using SUBQUERY");
                return this.containsAsSubquery(mapExpr, keyExpr, valExpr);
            }
            NucleusLogger.QUERY.debug("MapContainsEntry on " + mapExpr + "(" + keyExpr + "," + valExpr + ") using INNERJOIN");
            return this.containsAsInnerJoin(mapExpr, keyExpr, valExpr);
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
    
    protected SQLExpression containsAsInnerJoin(final MapExpression mapExpr, final SQLExpression keyExpr, final SQLExpression valExpr) {
        final boolean keyIsUnbound = keyExpr instanceof UnboundExpression;
        String keyVarName = null;
        if (keyIsUnbound) {
            keyVarName = ((UnboundExpression)keyExpr).getVariableName();
            NucleusLogger.QUERY.debug(">> Map.containsEntry binding unbound variable " + keyVarName + " using INNER JOIN");
        }
        final boolean valIsUnbound = valExpr instanceof UnboundExpression;
        String valVarName = null;
        if (valIsUnbound) {
            valVarName = ((UnboundExpression)valExpr).getVariableName();
            NucleusLogger.QUERY.debug(">> Map.containsEntry binding unbound variable " + valVarName + " using INNER JOIN");
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData keyCmd = mmd.getMap().getKeyClassMetaData(this.clr, mmgr);
        final AbstractClassMetaData valCmd = mmd.getMap().getValueClassMetaData(this.clr, mmgr);
        if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            final MapTable mapTbl = (MapTable)storeMgr.getTable(mmd);
            final SQLTable joinSqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), mapTbl, null, mapTbl.getOwnerMapping(), null, null);
            if (valCmd != null) {
                final DatastoreClass valTbl = storeMgr.getDatastoreClass(valCmd.getFullClassName(), this.clr);
                final SQLTable valSqlTbl = this.stmt.innerJoin(joinSqlTbl, mapTbl.getValueMapping(), valTbl, null, valTbl.getIdMapping(), null, null);
                final SQLExpression valIdExpr = this.exprFactory.newExpression(this.stmt, valSqlTbl, valTbl.getIdMapping());
                if (valIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr.getSQLTable(), valIdExpr.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(valIdExpr.eq(valExpr), true);
                }
            }
            else {
                final SQLExpression valIdExpr2 = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getValueMapping());
                if (valIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(valVarName, null, valIdExpr2.getSQLTable(), valIdExpr2.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(valIdExpr2.eq(valExpr), true);
                }
            }
            if (keyCmd != null) {
                final DatastoreClass keyTbl = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
                final SQLTable keySqlTbl = this.stmt.innerJoin(joinSqlTbl, mapTbl.getKeyMapping(), keyTbl, null, keyTbl.getIdMapping(), null, null);
                final SQLExpression keyIdExpr = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTbl.getIdMapping());
                if (keyIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr.getSQLTable(), keyIdExpr.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(keyIdExpr.eq(keyExpr), true);
                }
            }
            else {
                final SQLExpression keyIdExpr2 = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getKeyMapping());
                if (keyIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr2.getSQLTable(), keyIdExpr2.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(keyIdExpr2.eq(keyExpr), true);
                }
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final DatastoreClass valTbl2 = storeMgr.getDatastoreClass(valCmd.getFullClassName(), this.clr);
            JavaTypeMapping ownerMapping = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping = valTbl2.getMemberMapping(valCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping = valTbl2.getExternalMapping(mmd, 5);
            }
            final SQLTable valSqlTbl2 = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), valTbl2, null, ownerMapping, null, null);
            final SQLExpression valIdExpr3 = this.exprFactory.newExpression(this.stmt, valSqlTbl2, valTbl2.getIdMapping());
            if (valIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr3.getSQLTable(), valIdExpr3.getJavaTypeMapping());
            }
            else {
                this.stmt.whereAnd(valIdExpr3.eq(valExpr), true);
            }
            if (keyCmd != null) {
                final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(mmd.getKeyMetaData().getMappedBy());
                final DatastoreClass keyTbl2 = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
                final SQLTable keySqlTbl2 = this.stmt.innerJoin(valSqlTbl2, valTbl2.getMemberMapping(valKeyMmd), keyTbl2, null, keyTbl2.getIdMapping(), null, null);
                final SQLExpression keyIdExpr3 = this.exprFactory.newExpression(this.stmt, keySqlTbl2, keyTbl2.getIdMapping());
                if (keyIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr3.getSQLTable(), keyIdExpr3.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(keyIdExpr3.eq(keyExpr), true);
                }
            }
            else {
                final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(mmd.getKeyMetaData().getMappedBy());
                final SQLExpression keyIdExpr4 = this.exprFactory.newExpression(this.stmt, valSqlTbl2, valTbl2.getMemberMapping(valKeyMmd));
                if (keyIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr4.getSQLTable(), keyIdExpr4.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(keyIdExpr4.eq(keyExpr), true);
                }
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            final DatastoreClass keyTbl3 = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
            final AbstractMemberMetaData keyValMmd = keyCmd.getMetaDataForMember(mmd.getValueMetaData().getMappedBy());
            JavaTypeMapping ownerMapping2 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping2 = keyTbl3.getMemberMapping(keyCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping2 = keyTbl3.getExternalMapping(mmd, 5);
            }
            final SQLTable keySqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), keyTbl3, null, ownerMapping2, null, null);
            final SQLExpression keyIdExpr = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTbl3.getIdMapping());
            if (keyIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr.getSQLTable(), keyIdExpr.getJavaTypeMapping());
            }
            else {
                this.stmt.whereAnd(keyIdExpr.eq(keyExpr), true);
            }
            if (valCmd != null) {
                final DatastoreClass valTbl3 = storeMgr.getDatastoreClass(valCmd.getFullClassName(), this.clr);
                final SQLTable valSqlTbl3 = this.stmt.innerJoin(keySqlTbl, keyTbl3.getMemberMapping(keyValMmd), valTbl3, null, valTbl3.getIdMapping(), null, null);
                final SQLExpression valIdExpr4 = this.exprFactory.newExpression(this.stmt, valSqlTbl3, valTbl3.getIdMapping());
                if (valIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr4.getSQLTable(), valIdExpr4.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(valIdExpr4.eq(valExpr), true);
                }
            }
            else {
                final SQLExpression valIdExpr5 = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTbl3.getMemberMapping(keyValMmd));
                if (valIsUnbound) {
                    this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr5.getSQLTable(), valIdExpr5.getJavaTypeMapping());
                }
                else {
                    this.stmt.whereAnd(valIdExpr5.eq(valExpr), true);
                }
            }
        }
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
        return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, true));
    }
    
    protected SQLExpression containsAsSubquery(final MapExpression mapExpr, final SQLExpression keyExpr, final SQLExpression valExpr) {
        final boolean keyIsUnbound = keyExpr instanceof UnboundExpression;
        String keyVarName = null;
        if (keyIsUnbound) {
            keyVarName = ((UnboundExpression)keyExpr).getVariableName();
            NucleusLogger.QUERY.debug(">> Map.containsEntry binding unbound variable " + keyVarName + " using SUBQUERY");
        }
        final boolean valIsUnbound = valExpr instanceof UnboundExpression;
        String valVarName = null;
        if (valIsUnbound) {
            valVarName = ((UnboundExpression)valExpr).getVariableName();
            NucleusLogger.QUERY.debug(">> Map.containsEntry binding unbound variable " + valVarName + " using SUBQUERY");
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData keyCmd = mmd.getMap().getKeyClassMetaData(this.clr, mmgr);
        final AbstractClassMetaData valCmd = mmd.getMap().getValueClassMetaData(this.clr, mmgr);
        final MapTable joinTbl = (MapTable)storeMgr.getTable(mmd);
        SQLStatement subStmt = null;
        if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            subStmt = new SQLStatement(this.stmt, storeMgr, joinTbl, null, null);
            subStmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping oneMapping = storeMgr.getMappingManager().getMapping(Integer.class);
            subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping, 1), null);
            final JavaTypeMapping ownerMapping = joinTbl.getOwnerMapping();
            final SQLExpression ownerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
            final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
            subStmt.whereAnd(ownerExpr.eq(ownerIdExpr), true);
            final SQLExpression valIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getValueMapping());
            if (valIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr.getSQLTable(), valIdExpr.getJavaTypeMapping());
            }
            else {
                subStmt.whereAnd(valIdExpr.eq(valExpr), true);
            }
            final SQLExpression keyIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getKeyMapping());
            if (keyIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr.getSQLTable(), keyIdExpr.getJavaTypeMapping());
            }
            else {
                subStmt.whereAnd(keyIdExpr.eq(keyExpr), true);
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final DatastoreClass valTbl = storeMgr.getDatastoreClass(mmd.getMap().getValueType(), this.clr);
            final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(mmd.getKeyMetaData().getMappedBy());
            subStmt = new SQLStatement(this.stmt, storeMgr, valTbl, null, null);
            subStmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping oneMapping2 = storeMgr.getMappingManager().getMapping(Integer.class);
            subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping2, 1), null);
            JavaTypeMapping ownerMapping2 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping2 = valTbl.getMemberMapping(valCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping2 = valTbl.getExternalMapping(mmd, 5);
            }
            final SQLExpression ownerExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping2);
            final SQLExpression ownerIdExpr2 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
            subStmt.whereAnd(ownerExpr2.eq(ownerIdExpr2), true);
            final SQLExpression valIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl.getIdMapping());
            if (valIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr2.getSQLTable(), valIdExpr2.getJavaTypeMapping());
            }
            else {
                subStmt.whereAnd(valIdExpr2.eq(valExpr), true);
            }
            final SQLExpression keyIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl.getMemberMapping(valKeyMmd));
            if (keyIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr2.getSQLTable(), keyIdExpr2.getJavaTypeMapping());
            }
            else {
                subStmt.whereAnd(keyIdExpr2.eq(keyExpr), true);
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
            final AbstractMemberMetaData keyValMmd = keyCmd.getMetaDataForMember(mmd.getValueMetaData().getMappedBy());
            subStmt = new SQLStatement(this.stmt, storeMgr, keyTbl, null, null);
            subStmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping oneMapping3 = storeMgr.getMappingManager().getMapping(Integer.class);
            subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping3, 1), null);
            final SQLExpression ownerExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
            final SQLExpression ownerIdExpr2 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
            subStmt.whereAnd(ownerExpr2.eq(ownerIdExpr2), true);
            final SQLExpression valIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl.getMemberMapping(keyValMmd));
            if (valIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(valVarName, valCmd, valIdExpr2.getSQLTable(), valIdExpr2.getJavaTypeMapping());
            }
            else {
                subStmt.whereAnd(valIdExpr2.eq(valExpr), true);
            }
            final JavaTypeMapping keyMapping = keyTbl.getIdMapping();
            final SQLExpression keyIdExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyMapping);
            if (keyIsUnbound) {
                this.stmt.getQueryGenerator().bindVariable(keyVarName, keyCmd, keyIdExpr3.getSQLTable(), keyIdExpr3.getJavaTypeMapping());
            }
            else {
                subStmt.whereAnd(keyIdExpr3.eq(keyExpr), true);
            }
        }
        return new BooleanSubqueryExpression(this.stmt, "EXISTS", subStmt);
    }
}
