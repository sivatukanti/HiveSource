// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.method;

import org.datanucleus.metadata.AbstractClassMetaData;
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
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.store.types.simple.Map;
import org.datanucleus.store.rdbms.sql.expression.MapLiteral;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;

public class MapSizeMethod extends AbstractSQLMethod
{
    @Override
    public SQLExpression getExpression(final SQLExpression expr, final List args) {
        if (args != null && args.size() > 0) {
            throw new NucleusException(MapSizeMethod.LOCALISER.msg("060015", "size", "MapExpression"));
        }
        if (expr instanceof MapLiteral) {
            final Map map = (Map)((MapLiteral)expr).getValue();
            return this.exprFactory.newLiteral(this.stmt, this.exprFactory.getMappingForType(Integer.TYPE, false), map.size());
        }
        final AbstractMemberMetaData ownerMmd = expr.getJavaTypeMapping().getMemberMetaData();
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        JavaTypeMapping ownerMapping = null;
        Table mapTbl = null;
        if (ownerMmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_JOIN) {
            mapTbl = storeMgr.getTable(ownerMmd);
            ownerMapping = ((JoinTable)mapTbl).getOwnerMapping();
        }
        else if (ownerMmd.getMap().getMapType() == MapMetaData.MapType.MAP_TYPE_KEY_IN_VALUE) {
            final AbstractClassMetaData valueCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(ownerMmd.getMap().getValueType(), this.clr);
            mapTbl = storeMgr.getDatastoreClass(ownerMmd.getMap().getValueType(), this.clr);
            if (ownerMmd.getMappedBy() != null) {
                ownerMapping = mapTbl.getMemberMapping(valueCmd.getMetaDataForMember(ownerMmd.getMappedBy()));
            }
            else {
                ownerMapping = ((DatastoreClass)mapTbl).getExternalMapping(ownerMmd, 5);
            }
        }
        else {
            if (ownerMmd.getMap().getMapType() != MapMetaData.MapType.MAP_TYPE_VALUE_IN_KEY) {
                throw new NucleusException("Invalid map for " + expr + " in size() call");
            }
            final AbstractClassMetaData keyCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(ownerMmd.getMap().getKeyType(), this.clr);
            mapTbl = storeMgr.getDatastoreClass(ownerMmd.getMap().getKeyType(), this.clr);
            if (ownerMmd.getMappedBy() != null) {
                ownerMapping = mapTbl.getMemberMapping(keyCmd.getMetaDataForMember(ownerMmd.getMappedBy()));
            }
            else {
                ownerMapping = ((DatastoreClass)mapTbl).getExternalMapping(ownerMmd, 5);
            }
        }
        final SQLStatement subStmt = new SQLStatement(this.stmt, storeMgr, mapTbl, null, null);
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
