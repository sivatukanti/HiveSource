// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import java.util.List;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;

public class LocateRequest extends Request
{
    private String statementUnlocked;
    private String statementLocked;
    private StatementClassMapping mappingDefinition;
    
    public LocateRequest(final DatastoreClass table) {
        super(table);
        final RDBMSStoreManager storeMgr = table.getStoreManager();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final SQLStatement sqlStatement = new SQLStatement(storeMgr, table, null, null);
        this.mappingDefinition = new StatementClassMapping();
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        final JavaTypeMapping m = storeMgr.getMappingManager().getMapping(Integer.class);
        sqlStatement.select(exprFactory.newLiteral(sqlStatement, m, 1), null);
        final AbstractClassMetaData cmd = storeMgr.getMetaDataManager().getMetaDataForClass(table.getType(), clr);
        int inputParamNum = 1;
        if (table.getIdentityType() == IdentityType.DATASTORE) {
            final JavaTypeMapping datastoreIdMapping = table.getDatastoreObjectIdMapping();
            final SQLExpression expr = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), datastoreIdMapping);
            final SQLExpression val = exprFactory.newLiteralParameter(sqlStatement, datastoreIdMapping, null, "ID");
            sqlStatement.whereAnd(expr.eq(val), true);
            StatementMappingIndex datastoreIdx = this.mappingDefinition.getMappingForMemberPosition(-1);
            if (datastoreIdx == null) {
                datastoreIdx = new StatementMappingIndex(datastoreIdMapping);
                this.mappingDefinition.addMappingForMember(-1, datastoreIdx);
            }
            datastoreIdx.addParameterOccurrence(new int[] { inputParamNum });
        }
        else if (table.getIdentityType() == IdentityType.APPLICATION) {
            final int[] pkNums = cmd.getPKMemberPositions();
            for (int i = 0; i < pkNums.length; ++i) {
                final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[i]);
                JavaTypeMapping pkMapping = table.getMemberMappingInDatastoreClass(mmd);
                if (pkMapping == null) {
                    pkMapping = table.getMemberMapping(mmd);
                }
                final SQLExpression expr2 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), pkMapping);
                final SQLExpression val2 = exprFactory.newLiteralParameter(sqlStatement, pkMapping, null, "PK" + i);
                sqlStatement.whereAnd(expr2.eq(val2), true);
                StatementMappingIndex pkIdx = this.mappingDefinition.getMappingForMemberPosition(pkNums[i]);
                if (pkIdx == null) {
                    pkIdx = new StatementMappingIndex(pkMapping);
                    this.mappingDefinition.addMappingForMember(pkNums[i], pkIdx);
                }
                final int[] inputParams = new int[pkMapping.getNumberOfDatastoreMappings()];
                for (int j = 0; j < pkMapping.getNumberOfDatastoreMappings(); ++j) {
                    inputParams[j] = inputParamNum++;
                }
                pkIdx.addParameterOccurrence(inputParams);
            }
        }
        if (table.getMultitenancyMapping() != null) {
            final JavaTypeMapping tenantMapping = table.getMultitenancyMapping();
            final SQLExpression tenantExpr = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), tenantMapping);
            final SQLExpression tenantVal = exprFactory.newLiteral(sqlStatement, tenantMapping, storeMgr.getStringProperty("datanucleus.TenantID"));
            sqlStatement.whereAnd(tenantExpr.eq(tenantVal), true);
        }
        this.statementUnlocked = sqlStatement.getSelectStatement().toSQL();
        sqlStatement.addExtension("lock-for-update", Boolean.TRUE);
        this.statementLocked = sqlStatement.getSelectStatement().toSQL();
    }
    
    @Override
    public void execute(final ObjectProvider op) {
        if (this.statementLocked != null) {
            final ExecutionContext ec = op.getExecutionContext();
            final RDBMSStoreManager storeMgr = this.table.getStoreManager();
            boolean locked = ec.getSerializeReadForClass(op.getClassMetaData().getFullClassName());
            final short lockType = ec.getLockManager().getLockMode(op.getInternalObjectId());
            if (lockType != 0 && (lockType == 3 || lockType == 4)) {
                locked = true;
            }
            final String statement = locked ? this.statementLocked : this.statementUnlocked;
            try {
                final ManagedConnection mconn = storeMgr.getConnection(ec);
                final SQLController sqlControl = storeMgr.getSQLController();
                try {
                    final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, statement);
                    final AbstractClassMetaData cmd = op.getClassMetaData();
                    try {
                        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                            final StatementMappingIndex datastoreIdx = this.mappingDefinition.getMappingForMemberPosition(-1);
                            for (int i = 0; i < datastoreIdx.getNumberOfParameterOccurrences(); ++i) {
                                this.table.getDatastoreObjectIdMapping().setObject(ec, ps, datastoreIdx.getParameterPositionsForOccurrence(i), op.getInternalObjectId());
                            }
                        }
                        else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                            op.provideFields(cmd.getPKMemberPositions(), storeMgr.getFieldManagerForStatementGeneration(op, ps, this.mappingDefinition));
                        }
                        final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, statement, ps);
                        try {
                            if (!rs.next()) {
                                NucleusLogger.DATASTORE_RETRIEVE.info(LocateRequest.LOCALISER.msg("050018", op.getInternalObjectId()));
                                throw new NucleusObjectNotFoundException("No such database row", op.getInternalObjectId());
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
            catch (SQLException sqle) {
                final String msg = LocateRequest.LOCALISER.msg("052220", op.getObjectAsPrintable(), statement, sqle.getMessage());
                NucleusLogger.DATASTORE_RETRIEVE.warn(msg);
                final List exceptions = new ArrayList();
                exceptions.add(sqle);
                while ((sqle = sqle.getNextException()) != null) {
                    exceptions.add(sqle);
                }
                throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]));
            }
        }
    }
}
