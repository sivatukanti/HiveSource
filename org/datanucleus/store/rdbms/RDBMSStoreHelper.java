// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.util.Iterator;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import java.util.List;
import org.datanucleus.metadata.DiscriminatorMetaData;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.query.RDBMSQueryUtils;
import org.datanucleus.store.rdbms.mapping.java.PersistableIdMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;

public class RDBMSStoreHelper
{
    public static String getClassNameForIdUsingDiscriminator(final RDBMSStoreManager storeMgr, final ExecutionContext ec, final Object id, final AbstractClassMetaData cmd) {
        if (cmd == null || id == null) {
            return null;
        }
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final DatastoreClass primaryTable = storeMgr.getDatastoreClass(cmd.getFullClassName(), clr);
        final DiscriminatorStatementGenerator stmtGen = new DiscriminatorStatementGenerator(storeMgr, clr, clr.classForName(cmd.getFullClassName()), true, null, null);
        stmtGen.setOption("restrictDiscriminator");
        final SQLStatement sqlStmt = stmtGen.getStatement();
        final JavaTypeMapping discrimMapping = primaryTable.getDiscriminatorMapping(true);
        final SQLTable discrimSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStmt, sqlStmt.getPrimaryTable(), discrimMapping);
        sqlStmt.select(discrimSqlTbl, discrimMapping, null);
        final JavaTypeMapping idMapping = primaryTable.getIdMapping();
        final JavaTypeMapping idParamMapping = new PersistableIdMapping((PersistableMapping)idMapping);
        final SQLExpression sqlFldExpr = exprFactory.newExpression(sqlStmt, sqlStmt.getPrimaryTable(), idMapping);
        final SQLExpression sqlFldVal = exprFactory.newLiteralParameter(sqlStmt, idParamMapping, id, "ID");
        sqlStmt.whereAnd(sqlFldExpr.eq(sqlFldVal), true);
        try {
            final ManagedConnection mconn = storeMgr.getConnection(ec);
            final SQLController sqlControl = storeMgr.getSQLController();
            if (ec.getSerializeReadForClass(cmd.getFullClassName())) {
                sqlStmt.addExtension("lock-for-update", true);
            }
            try {
                final PreparedStatement ps = SQLStatementHelper.getPreparedStatementForSQLStatement(sqlStmt, ec, mconn, null, null);
                final String statement = sqlStmt.getSelectStatement().toSQL();
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, statement, ps);
                    try {
                        if (rs != null && rs.next()) {
                            final DiscriminatorMetaData dismd = discrimMapping.getTable().getDiscriminatorMetaData();
                            return RDBMSQueryUtils.getClassNameFromDiscriminatorResultSetRow(discrimMapping, dismd, rs, ec);
                        }
                    }
                    finally {
                        rs.close();
                    }
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException sqe) {
            NucleusLogger.DATASTORE.error("Exception thrown on querying of discriminator for id", sqe);
            throw new NucleusDataStoreException(sqe.toString(), sqe);
        }
        return null;
    }
    
    public static String getClassNameForIdUsingUnion(final RDBMSStoreManager storeMgr, final ExecutionContext ec, final Object id, final List<AbstractClassMetaData> rootCmds) {
        if (rootCmds == null || rootCmds.isEmpty() || id == null) {
            return null;
        }
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final Iterator<AbstractClassMetaData> rootCmdIter = rootCmds.iterator();
        AbstractClassMetaData sampleCmd = null;
        SQLStatement sqlStmtMain = null;
        while (rootCmdIter.hasNext()) {
            final AbstractClassMetaData rootCmd = rootCmdIter.next();
            final DatastoreClass rootTbl = storeMgr.getDatastoreClass(rootCmd.getFullClassName(), clr);
            if (rootTbl == null) {
                final AbstractClassMetaData[] subcmds = storeMgr.getClassesManagingTableForClass(rootCmd, clr);
                if (subcmds == null) {
                    continue;
                }
                if (subcmds.length == 0) {
                    continue;
                }
                for (int i = 0; i < subcmds.length; ++i) {
                    final UnionStatementGenerator stmtGen = new UnionStatementGenerator(storeMgr, clr, clr.classForName(subcmds[i].getFullClassName()), true, null, null);
                    stmtGen.setOption("selectNucleusType");
                    if (sqlStmtMain == null) {
                        sampleCmd = subcmds[i];
                        sqlStmtMain = stmtGen.getStatement();
                    }
                    else {
                        final SQLStatement sqlStmt = stmtGen.getStatement();
                        sqlStmtMain.union(sqlStmt);
                    }
                }
            }
            else {
                final UnionStatementGenerator stmtGen2 = new UnionStatementGenerator(storeMgr, clr, clr.classForName(rootCmd.getFullClassName()), true, null, null);
                stmtGen2.setOption("selectNucleusType");
                if (sqlStmtMain == null) {
                    sampleCmd = rootCmd;
                    sqlStmtMain = stmtGen2.getStatement();
                }
                else {
                    final SQLStatement sqlStmt2 = stmtGen2.getStatement();
                    sqlStmtMain.union(sqlStmt2);
                }
            }
        }
        final JavaTypeMapping idMapping = sqlStmtMain.getPrimaryTable().getTable().getIdMapping();
        final JavaTypeMapping idParamMapping = new PersistableIdMapping((PersistableMapping)idMapping);
        final SQLExpression fieldExpr = exprFactory.newExpression(sqlStmtMain, sqlStmtMain.getPrimaryTable(), idMapping);
        final SQLExpression fieldVal = exprFactory.newLiteralParameter(sqlStmtMain, idParamMapping, id, "ID");
        sqlStmtMain.whereAnd(fieldExpr.eq(fieldVal), true);
        try {
            final ManagedConnection mconn = storeMgr.getConnection(ec);
            final SQLController sqlControl = storeMgr.getSQLController();
            if (ec.getSerializeReadForClass(sampleCmd.getFullClassName())) {
                sqlStmtMain.addExtension("lock-for-update", true);
            }
            try {
                final PreparedStatement ps = SQLStatementHelper.getPreparedStatementForSQLStatement(sqlStmtMain, ec, mconn, null, null);
                final String statement = sqlStmtMain.getSelectStatement().toSQL();
                try {
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, statement, ps);
                    try {
                        if (rs != null) {
                            while (rs.next()) {
                                try {
                                    return rs.getString("NUCLEUS_TYPE").trim();
                                }
                                catch (SQLException sqle) {
                                    continue;
                                }
                                break;
                            }
                        }
                    }
                    finally {
                        rs.close();
                    }
                }
                finally {
                    sqlControl.closeStatement(mconn, ps);
                }
            }
            finally {
                mconn.release();
            }
        }
        catch (SQLException sqe) {
            NucleusLogger.DATASTORE.error(sqe);
            throw new NucleusDataStoreException(sqe.toString());
        }
        return null;
    }
}
