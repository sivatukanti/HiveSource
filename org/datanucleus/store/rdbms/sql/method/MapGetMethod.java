// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.SubqueryExpression;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.query.compiler.CompilationComponent;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.NullLiteral;
import org.datanucleus.store.rdbms.sql.expression.SQLLiteral;
import org.datanucleus.store.rdbms.sql.expression.MapLiteral;
import org.datanucleus.store.rdbms.sql.expression.UnboundExpression;
import org.datanucleus.store.rdbms.sql.expression.MapExpression;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class MapGetMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args == null || args.size() == 0 || args.size() > 1) {
            throw new NucleusException(MapGetMethod.LOCALISER.msg("060016", "get", "MapExpression", 1));
        }
        final MapExpression mapExpr = (MapExpression)expr;
        final SQLExpression keyValExpr = args.get(0);
        if (keyValExpr instanceof UnboundExpression) {
            throw new NucleusException("Dont currently support binding of unbound variables using Map.get");
        }
        if (mapExpr instanceof MapLiteral && keyValExpr instanceof SQLLiteral) {
            final MapLiteral lit = (MapLiteral)expr;
            if (lit.getValue() == null) {
                return new NullLiteral(this.stmt, null, null, null);
            }
            return lit.getKeyLiteral().invoke("get", args);
        }
        else {
            if (mapExpr instanceof MapLiteral) {
                throw new NucleusUserException("We do not support MapLiteral.get(SQLExpression) since SQL doesnt allow such constructs");
            }
            if (this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.FILTER || this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.ORDERING) {
                return this.getAsInnerJoin(mapExpr, keyValExpr);
            }
            if (this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.RESULT || this.stmt.getQueryGenerator().getCompilationComponent() == CompilationComponent.HAVING) {
                return this.getAsSubquery(mapExpr, keyValExpr);
            }
            throw new NucleusException("Map.get() is not supported for " + mapExpr + " with argument " + keyValExpr + " for query component " + this.stmt.getQueryGenerator().getCompilationComponent());
        }
    }
    
    protected SQLExpression getAsSubquery(final MapExpression mapExpr, final SQLExpression keyValExpr) {
        final AbstractMemberMetaData mmd = mapExpr.getJavaTypeMapping().getMemberMetaData();
        final MapMetaData mapmd = mmd.getMap();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        JavaTypeMapping ownerMapping = null;
        JavaTypeMapping keyMapping = null;
        JavaTypeMapping valMapping = null;
        Table mapTbl = null;
        if (mapmd.getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            mapTbl = storeMgr.getTable(mmd);
            ownerMapping = ((MapTable)mapTbl).getOwnerMapping();
            keyMapping = ((MapTable)mapTbl).getKeyMapping();
            valMapping = ((MapTable)mapTbl).getValueMapping();
        }
        else if (mapmd.getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final AbstractClassMetaData valCmd = mapmd.getValueClassMetaData(this.clr, mmgr);
            mapTbl = storeMgr.getDatastoreClass(mmd.getMap().getValueType(), this.clr);
            if (mmd.getMappedBy() != null) {
                ownerMapping = mapTbl.getMemberMapping(valCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping = ((DatastoreClass)mapTbl).getExternalMapping(mmd, 5);
            }
            final String keyFieldName = mmd.getKeyMetaData().getMappedBy();
            final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(keyFieldName);
            keyMapping = mapTbl.getMemberMapping(valKeyMmd);
            valMapping = mapTbl.getIdMapping();
        }
        else {
            if (mapmd.getMapType() != MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
                throw new NucleusException("Invalid map for " + mapExpr + " in get() call");
            }
            final AbstractClassMetaData keyCmd = mapmd.getKeyClassMetaData(this.clr, mmgr);
            mapTbl = storeMgr.getDatastoreClass(mmd.getMap().getKeyType(), this.clr);
            if (mmd.getMappedBy() != null) {
                ownerMapping = mapTbl.getMemberMapping(keyCmd.getMetaDataForMember(mmd.getMappedBy()));
            }
            else {
                ownerMapping = ((DatastoreClass)mapTbl).getExternalMapping(mmd, 5);
            }
            keyMapping = mapTbl.getIdMapping();
            final String valFieldName = mmd.getValueMetaData().getMappedBy();
            final AbstractMemberMetaData keyValMmd = keyCmd.getMetaDataForMember(valFieldName);
            valMapping = mapTbl.getMemberMapping(keyValMmd);
        }
        final SQLStatement subStmt = new SQLStatement(this.stmt, storeMgr, mapTbl, null, null);
        subStmt.setClassLoaderResolver(this.clr);
        final SQLExpression valExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), valMapping);
        subStmt.select(valExpr, null);
        final SQLExpression elementOwnerExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), ownerMapping);
        final SQLExpression ownerIdExpr = this.exprFactory.newExpression(this.stmt, mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping());
        subStmt.whereAnd(elementOwnerExpr.eq(ownerIdExpr), true);
        final SQLExpression keyExpr = this.exprFactory.newExpression(subStmt, subStmt.getPrimaryTable(), keyMapping);
        subStmt.whereAnd(keyExpr.eq(keyValExpr), true);
        final SubqueryExpression subExpr = new SubqueryExpression(this.stmt, subStmt);
        subExpr.setJavaTypeMapping(valMapping);
        return subExpr;
    }
    
    protected SQLExpression getAsInnerJoin(final MapExpression mapExpr, final SQLExpression keyValExpr) {
        final JavaTypeMapping m = mapExpr.getJavaTypeMapping();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final MetaDataManager mmgr = storeMgr.getMetaDataManager();
        final AbstractMemberMetaData mmd = m.getMemberMetaData();
        if (mmd != null) {
            final MapMetaData mapmd = mmd.getMap();
            if (mapmd.getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
                final MapTable joinTbl = (MapTable)this.stmt.getRDBMSManager().getTable(mmd);
                final SQLTable joinSqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), joinTbl, null, joinTbl.getOwnerMapping(), null, null);
                final SQLExpression keyExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getKeyMapping());
                this.stmt.whereAnd(keyExpr.eq(keyValExpr), true);
                if (mapmd.getValueClassMetaData(this.clr, mmgr) != null) {
                    final DatastoreClass valTable = this.stmt.getRDBMSManager().getDatastoreClass(mapmd.getValueType(), this.clr);
                    final SQLTable valueSqlTbl = this.stmt.innerJoin(joinSqlTbl, joinTbl.getValueMapping(), valTable, null, valTable.getIdMapping(), null, null);
                    return this.exprFactory.newExpression(this.stmt, valueSqlTbl, valTable.getIdMapping());
                }
                final SQLExpression valueExpr = this.exprFactory.newExpression(this.stmt, joinSqlTbl, joinTbl.getValueMapping());
                return valueExpr;
            }
            else {
                if (mapmd.getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
                    final DatastoreClass valTable2 = this.stmt.getRDBMSManager().getDatastoreClass(mapmd.getValueType(), this.clr);
                    final AbstractClassMetaData valCmd = mapmd.getValueClassMetaData(this.clr, mmgr);
                    final SQLTable valSqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), valTable2, null, valTable2.getIdMapping(), null, null);
                    final String keyFieldName = mmd.getKeyMetaData().getMappedBy();
                    final AbstractMemberMetaData valKeyMmd = valCmd.getMetaDataForMember(keyFieldName);
                    final JavaTypeMapping keyMapping = valTable2.getMemberMapping(valKeyMmd);
                    final SQLExpression keyExpr2 = this.exprFactory.newExpression(this.stmt, valSqlTbl, keyMapping);
                    this.stmt.whereAnd(keyExpr2.eq(keyValExpr), true);
                    final SQLExpression valueExpr2 = this.exprFactory.newExpression(this.stmt, valSqlTbl, valTable2.getIdMapping());
                    return valueExpr2;
                }
                if (mapmd.getMapType() == MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
                    final DatastoreClass keyTable = this.stmt.getRDBMSManager().getDatastoreClass(mapmd.getKeyType(), this.clr);
                    final AbstractClassMetaData keyCmd = mapmd.getKeyClassMetaData(this.clr, mmgr);
                    final SQLTable keySqlTbl = this.stmt.innerJoin(mapExpr.getSQLTable(), mapExpr.getSQLTable().getTable().getIdMapping(), keyTable, null, keyTable.getIdMapping(), null, null);
                    final SQLExpression keyExpr3 = this.exprFactory.newExpression(this.stmt, keySqlTbl, keyTable.getIdMapping());
                    this.stmt.whereAnd(keyExpr3.eq(keyValExpr), true);
                    final String valueFieldName = mmd.getValueMetaData().getMappedBy();
                    final AbstractMemberMetaData valKeyMmd2 = keyCmd.getMetaDataForMember(valueFieldName);
                    final JavaTypeMapping valueMapping = keyTable.getMemberMapping(valKeyMmd2);
                    final SQLExpression valueExpr2 = this.exprFactory.newExpression(this.stmt, keySqlTbl, valueMapping);
                    return valueExpr2;
                }
            }
        }
        throw new NucleusException("Map.get() for the filter is not supported for " + mapExpr + " with an argument of " + keyValExpr + ". Why not contribute support for it?");
    }
}
