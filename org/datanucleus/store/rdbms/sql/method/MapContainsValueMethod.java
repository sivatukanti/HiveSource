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
import org.datanucleus.store.rdbms.sql.SQLJoin;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
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

public class MapContainsValueMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 1) {
            throw new NucleusException(MapContainsValueMethod.LOCALISER.msg("060016", "containsValue", "MapExpression", 1));
        }
        final MapExpression mapExpr = (MapExpression)expr;
        final SQLExpression valExpr = args.get(0);
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
            final MapLiteral.MapValueLiteral mapValueLiteral = lit.getValueLiteral();
            BooleanExpression bExpr = null;
            final List<SQLExpression> elementExprs = mapValueLiteral.getValueExpressions();
            for (int i = 0; i < elementExprs.size(); ++i) {
                if (bExpr == null) {
                    bExpr = elementExprs.get(i).eq(valExpr);
                }
                else {
                    bExpr = bExpr.ior(elementExprs.get(i).eq(valExpr));
                }
            }
            bExpr.encloseInParentheses();
            return bExpr;
        }
        else {
            if (this.stmt.getQueryGenerator().getCompilationComponent() != CompilationComponent.FILTER) {
                return this.containsAsSubquery(mapExpr, valExpr);
            }
            final boolean needsSubquery = this.getNeedsSubquery();
            if (needsSubquery) {
                NucleusLogger.QUERY.debug("map.containsValue on " + mapExpr + "(" + valExpr + ") using SUBQUERY");
                return this.containsAsSubquery(mapExpr, valExpr);
            }
            NucleusLogger.QUERY.debug("map.containsValue on " + mapExpr + "(" + valExpr + ") using INNERJOIN");
            return this.containsAsInnerJoin(mapExpr, valExpr);
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
    
    protected SQLExpression containsAsInnerJoin(final MapExpression mapExpr, SQLExpression valExpr) {
        boolean valIsUnbound = valExpr instanceof UnboundExpression;
        String varName = null;
        String valAlias = null;
        if (valIsUnbound) {
            varName = ((UnboundExpression)valExpr).getVariableName();
            NucleusLogger.QUERY.debug("map.containsValue(" + valExpr + ") binding unbound variable " + varName + " using INNER JOIN");
        }
        else if (!this.stmt.getQueryGenerator().hasExplicitJoins()) {
            final SQLJoin.JoinType joinType = this.stmt.getJoinTypeForTable(valExpr.getSQLTable());
            if (joinType == SQLJoin.JoinType.CROSS_JOIN) {
                valAlias = this.stmt.removeCrossJoin(valExpr.getSQLTable());
                valIsUnbound = true;
                NucleusLogger.QUERY.debug("map.containsValue(" + valExpr + ") was previously bound as CROSS JOIN but changing to INNER JOIN");
            }
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData valCmd = mmd.getMap().getValueClassMetaData(this.clr, mmgr);
        if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            final MapTable mapTbl = (MapTable)storeMgr.getTable(mmd);
            final SQLTable joinSqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), mapTbl, null, mapTbl.getOwnerMapping(), null, null);
            if (valCmd != null) {
                if (valIsUnbound) {
                    final DatastoreClass valTbl = storeMgr.getDatastoreClass(valCmd.getFullClassName(), this.clr);
                    final SQLTable valSqlTbl = this.stmt.innerJoin(joinSqlTbl, mapTbl.getValueMapping(), valTbl, valAlias, valTbl.getIdMapping(), null, null);
                    valExpr = this.exprFactory.newExpression(this.stmt, valSqlTbl, valSqlTbl.getTable().getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression valIdExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getValueMapping());
                    this.stmt.whereAnd(valIdExpr.eq(valExpr), true);
                }
            }
            else if (valIsUnbound) {
                valExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getValueMapping());
                this.stmt.getQueryGenerator().bindVariable(varName, null, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
            }
            else {
                final SQLExpression valIdExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, mapTbl.getValueMapping());
                this.stmt.whereAnd(valIdExpr.eq(valExpr), true);
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
            final SQLTable valSqlTbl2 = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), valTbl2, valAlias, ownerMapping, null, null);
            if (valIsUnbound) {
                valExpr = this.exprFactory.newExpression(this.stmt, valSqlTbl2, valTbl2.getIdMapping());
                this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
            }
            else {
                final SQLExpression valIdExpr2 = this.exprFactory.newExpression(this.stmt, valSqlTbl2, valTbl2.getIdMapping());
                this.stmt.whereAnd(valIdExpr2.eq(valExpr), true);
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            final AbstractClassMetaData keyCmd = mmd.getMap().getKeyClassMetaData(this.clr, mmgr);
            final DatastoreClass keyTbl = storeMgr.getDatastoreClass(keyCmd.getFullClassName(), this.clr);
            final AbstractMemberMetaData keyValMmd = keyCmd.getMetaDataForMember(mmd.getValueMetaData().getMappedBy());
            JavaTypeMapping ownerMapping2 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping2 = keyTbl.getMemberMapping(keyCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping2 = keyTbl.getExternalMapping(mmd, 5);
            }
            final SQLTable keySqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), keyTbl, null, ownerMapping2, null, null);
            if (valCmd != null) {
                final DatastoreClass valTbl3 = storeMgr.getDatastoreClass(valCmd.getFullClassName(), this.clr);
                final SQLTable valSqlTbl3 = this.stmt.innerJoin(keySqlTbl, keyTbl.getMemberMapping(keyValMmd), valTbl3, valAlias, valTbl3.getIdMapping(), null, null);
                if (valIsUnbound) {
                    valExpr = this.exprFactory.newExpression(this.stmt, valSqlTbl3, valTbl3.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression valIdExpr3 = this.exprFactory.newExpression(this.stmt, valSqlTbl3, valTbl3.getIdMapping());
                    this.stmt.whereAnd(valIdExpr3.eq(valExpr), true);
                }
            }
            else if (valIsUnbound) {
                valExpr = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTbl.getMemberMapping(keyValMmd));
                this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
            }
            else {
                final SQLExpression valIdExpr4 = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTbl.getMemberMapping(keyValMmd));
                this.stmt.whereAnd(valIdExpr4.eq(valExpr), true);
            }
        }
        final JavaTypeMapping m = this.exprFactory.getMappingForType(Boolean.TYPE, true);
        return this.exprFactory.newLiteral(this.stmt, m, true).eq(this.exprFactory.newLiteral(this.stmt, m, true));
    }
    
    protected SQLExpression containsAsSubquery(final MapExpression mapExpr, SQLExpression valExpr) {
        final boolean valIsUnbound = valExpr instanceof UnboundExpression;
        String varName = null;
        if (valIsUnbound) {
            varName = ((UnboundExpression)valExpr).getVariableName();
            NucleusLogger.QUERY.debug("map.containsValue binding unbound variable " + varName + " using SUBQUERY");
        }
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final AbstractClassMetaData valCmd = mmd.getMap().getValueClassMetaData(this.clr, mmgr);
        final MapTable joinTbl = (MapTable)storeMgr.getTable(mmd);
        SQLStatement subStmt = null;
        if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            if (valCmd == null) {
                subStmt = new SQLStatement(this.stmt, storeMgr, joinTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping, 1), null);
                final JavaTypeMapping ownerMapping = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
                final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr.eq(ownerIdExpr), true);
                if (valIsUnbound) {
                    valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getValueMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, null, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression valIdExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), joinTbl.getValueMapping());
                    subStmt.whereAnd(valIdExpr.eq(valExpr), true);
                }
            }
            else {
                final DatastoreClass valTbl = storeMgr.getDatastoreClass(mmd.getMap().getValueType(), this.clr);
                subStmt = new SQLStatement(this.stmt, storeMgr, valTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping2 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping2, 1), null);
                final SQLTable joinSqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), valTbl.getIdMapping(), joinTbl, null, joinTbl.getValueMapping(), null, null);
                final JavaTypeMapping ownerMapping2 = joinTbl.getOwnerMapping();
                final SQLExpression ownerExpr2 = this.exprFactory.newExpression(subStmt, joinSqlTbl, ownerMapping2);
                final SQLExpression ownerIdExpr2 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr2.eq(ownerIdExpr2), true);
                if (valIsUnbound) {
                    valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression valIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl.getIdMapping());
                    subStmt.whereAnd(valIdExpr2.eq(valExpr), true);
                }
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final DatastoreClass valTbl = storeMgr.getDatastoreClass(mmd.getMap().getValueType(), this.clr);
            JavaTypeMapping ownerMapping = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping = valTbl.getMemberMapping(valCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping = valTbl.getExternalMapping(mmd, 5);
            }
            subStmt = new SQLStatement(this.stmt, storeMgr, valTbl, null, null);
            subStmt.setClassLoaderResolver(this.clr);
            final JavaTypeMapping oneMapping3 = storeMgr.getMappingManager().getMapping(Integer.class);
            subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping3, 1), null);
            final SQLExpression ownerExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
            final SQLExpression ownerIdExpr3 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
            subStmt.whereAnd(ownerExpr3.eq(ownerIdExpr3), true);
            if (valIsUnbound) {
                valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl.getIdMapping());
                this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
            }
            else {
                final JavaTypeMapping valMapping = valTbl.getIdMapping();
                final SQLExpression valIdExpr2 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valMapping);
                subStmt.whereAnd(valIdExpr2.eq(valExpr), true);
            }
        }
        else if (mmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
            final AbstractClassMetaData keyCmd = mmd.getMap().getKeyClassMetaData(this.clr, mmgr);
            final DatastoreClass keyTbl = storeMgr.getDatastoreClass(mmd.getMap().getKeyType(), this.clr);
            JavaTypeMapping ownerMapping3 = null;
            if (mmd.getMappedBy() != null) {
                ownerMapping3 = keyTbl.getMemberMapping(keyCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping3 = keyTbl.getExternalMapping(mmd, 5);
            }
            final AbstractMemberMetaData keyValMmd = keyCmd.getMetaDataForMember(mmd.getValueMetaData().getMappedBy());
            if (valCmd == null) {
                subStmt = new SQLStatement(this.stmt, storeMgr, keyTbl, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping4 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping4, 1), null);
                final SQLExpression ownerExpr4 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping3);
                final SQLExpression ownerIdExpr4 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr4.eq(ownerIdExpr4), true);
                if (valIsUnbound) {
                    valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyTbl.getMemberMapping(keyValMmd));
                    this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
                }
                else {
                    final JavaTypeMapping valMapping2 = keyTbl.getMemberMapping(keyValMmd);
                    final SQLExpression valIdExpr3 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valMapping2);
                    subStmt.whereAnd(valIdExpr3.eq(valExpr), true);
                }
            }
            else {
                final DatastoreClass valTbl2 = storeMgr.getDatastoreClass(mmd.getMap().getValueType(), this.clr);
                subStmt = new SQLStatement(this.stmt, storeMgr, valTbl2, null, null);
                subStmt.setClassLoaderResolver(this.clr);
                final JavaTypeMapping oneMapping5 = storeMgr.getMappingManager().getMapping(Integer.class);
                subStmt.select(this.exprFactory.newLiteral(subStmt, oneMapping5, 1), null);
                final SQLTable keySqlTbl = subStmt.innerJoin(subStmt.getPrimaryTable(), valTbl2.getIdMapping(), keyTbl, null, keyTbl.getMemberMapping(keyValMmd), null, null);
                final SQLExpression ownerExpr5 = this.exprFactory.newExpression(subStmt, keySqlTbl, ownerMapping3);
                final SQLExpression ownerIdExpr5 = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
                subStmt.whereAnd(ownerExpr5.eq(ownerIdExpr5), true);
                if (valIsUnbound) {
                    valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl2.getIdMapping());
                    this.stmt.getQueryGenerator().bindVariable(varName, valCmd, valExpr.getSQLTable(), valExpr.getJavaTypeMapping());
                }
                else {
                    final SQLExpression valIdExpr4 = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valTbl2.getIdMapping());
                    subStmt.whereAnd(valIdExpr4.eq(valExpr), true);
                }
            }
        }
        return new BooleanSubqueryExpression(this.stmt, "EXISTS", subStmt);
    }
}
