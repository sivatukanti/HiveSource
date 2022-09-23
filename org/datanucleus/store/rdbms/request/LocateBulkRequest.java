// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import java.util.Iterator;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.identity.OID;
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
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.metadata.AbstractClassMetaData;

public class LocateBulkRequest extends BulkRequest
{
    AbstractClassMetaData cmd;
    private StatementClassMapping[] mappingDefinitions;
    private StatementClassMapping resultMapping;
    
    public LocateBulkRequest(final DatastoreClass table) {
        super(table);
        this.cmd = null;
    }
    
    protected String getStatement(final DatastoreClass table, final ObjectProvider[] ops, final boolean lock) {
        final RDBMSStoreManager storeMgr = table.getStoreManager();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        this.cmd = storeMgr.getMetaDataManager().getMetaDataForClass(table.getType(), clr);
        final SQLStatement sqlStatement = new SQLStatement(storeMgr, table, null, null);
        this.resultMapping = new StatementClassMapping();
        if (table.getIdentityType() == IdentityType.DATASTORE) {
            final JavaTypeMapping datastoreIdMapping = table.getDatastoreObjectIdMapping();
            final SQLExpression expr = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), datastoreIdMapping);
            final int[] cols = sqlStatement.select(expr, null);
            final StatementMappingIndex datastoreIdx = new StatementMappingIndex(datastoreIdMapping);
            datastoreIdx.setColumnPositions(cols);
            this.resultMapping.addMappingForMember(-1, datastoreIdx);
        }
        else {
            if (table.getIdentityType() != IdentityType.APPLICATION) {
                throw new NucleusUserException("Cannot locate objects using nondurable identity");
            }
            final int[] pkNums = this.cmd.getPKMemberPositions();
            for (int i = 0; i < pkNums.length; ++i) {
                final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[i]);
                JavaTypeMapping pkMapping = table.getMemberMappingInDatastoreClass(mmd);
                if (pkMapping == null) {
                    pkMapping = table.getMemberMapping(mmd);
                }
                final SQLExpression expr2 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), pkMapping);
                final int[] cols2 = sqlStatement.select(expr2, null);
                final StatementMappingIndex pkIdx = new StatementMappingIndex(pkMapping);
                pkIdx.setColumnPositions(cols2);
                this.resultMapping.addMappingForMember(mmd.getAbsoluteFieldNumber(), pkIdx);
            }
        }
        final JavaTypeMapping verMapping = table.getVersionMapping(false);
        if (verMapping != null) {
            final VersionMetaData currentVermd = table.getVersionMetaData();
            if (currentVermd != null && currentVermd.getFieldName() == null) {
                final SQLExpression expr3 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), verMapping);
                final int[] cols3 = sqlStatement.select(expr3, null);
                final StatementMappingIndex mapIdx = new StatementMappingIndex(verMapping);
                mapIdx.setColumnPositions(cols3);
                this.resultMapping.addMappingForMember(-2, mapIdx);
            }
        }
        final int[] nonPkFieldNums = this.cmd.getNonPKMemberPositions();
        if (nonPkFieldNums != null) {
            for (int j = 0; j < nonPkFieldNums.length; ++j) {
                final AbstractMemberMetaData mmd2 = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(nonPkFieldNums[j]);
                final JavaTypeMapping mapping = table.getMemberMapping(mmd2);
                if (mapping != null && mapping.includeInFetchStatement()) {
                    if (!(mapping instanceof PersistableMapping)) {
                        final SQLExpression expr4 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), mapping);
                        final int[] cols4 = sqlStatement.select(expr4, null);
                        final StatementMappingIndex mapIdx2 = new StatementMappingIndex(mapping);
                        mapIdx2.setColumnPositions(cols4);
                        this.resultMapping.addMappingForMember(mmd2.getAbsoluteFieldNumber(), mapIdx2);
                    }
                }
            }
        }
        if (table.getMultitenancyMapping() != null) {
            final JavaTypeMapping tenantMapping = table.getMultitenancyMapping();
            final SQLExpression tenantExpr = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), tenantMapping);
            final SQLExpression tenantVal = exprFactory.newLiteral(sqlStatement, tenantMapping, storeMgr.getStringProperty("datanucleus.TenantID"));
            sqlStatement.whereAnd(tenantExpr.eq(tenantVal), true);
        }
        this.mappingDefinitions = new StatementClassMapping[ops.length];
        int inputParamNum = 1;
        for (int k = 0; k < ops.length; ++k) {
            this.mappingDefinitions[k] = new StatementClassMapping();
            if (table.getIdentityType() == IdentityType.DATASTORE) {
                final JavaTypeMapping datastoreIdMapping2 = table.getDatastoreObjectIdMapping();
                final SQLExpression expr4 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), datastoreIdMapping2);
                final SQLExpression val = exprFactory.newLiteralParameter(sqlStatement, datastoreIdMapping2, null, "ID");
                sqlStatement.whereOr(expr4.eq(val), true);
                final StatementMappingIndex datastoreIdx2 = new StatementMappingIndex(datastoreIdMapping2);
                this.mappingDefinitions[k].addMappingForMember(-1, datastoreIdx2);
                datastoreIdx2.addParameterOccurrence(new int[] { inputParamNum++ });
            }
            else if (table.getIdentityType() == IdentityType.APPLICATION) {
                BooleanExpression pkExpr = null;
                final int[] pkNums2 = this.cmd.getPKMemberPositions();
                for (int l = 0; l < pkNums2.length; ++l) {
                    final AbstractMemberMetaData mmd3 = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums2[l]);
                    JavaTypeMapping pkMapping2 = table.getMemberMappingInDatastoreClass(mmd3);
                    if (pkMapping2 == null) {
                        pkMapping2 = table.getMemberMapping(mmd3);
                    }
                    final SQLExpression expr5 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), pkMapping2);
                    final SQLExpression val2 = exprFactory.newLiteralParameter(sqlStatement, pkMapping2, null, "PK" + l);
                    final BooleanExpression fieldEqExpr = expr5.eq(val2);
                    if (pkExpr == null) {
                        pkExpr = fieldEqExpr;
                    }
                    else {
                        pkExpr = pkExpr.and(fieldEqExpr);
                    }
                    final StatementMappingIndex pkIdx2 = new StatementMappingIndex(pkMapping2);
                    this.mappingDefinitions[k].addMappingForMember(mmd3.getAbsoluteFieldNumber(), pkIdx2);
                    final int[] inputParams = new int[pkMapping2.getNumberOfDatastoreMappings()];
                    for (int m = 0; m < pkMapping2.getNumberOfDatastoreMappings(); ++m) {
                        inputParams[m] = inputParamNum++;
                    }
                    pkIdx2.addParameterOccurrence(inputParams);
                }
                pkExpr = (BooleanExpression)pkExpr.encloseInParentheses();
                sqlStatement.whereOr(pkExpr, true);
            }
        }
        if (lock) {
            sqlStatement.addExtension("lock-for-update", Boolean.TRUE);
            return sqlStatement.getSelectStatement().toSQL();
        }
        return sqlStatement.getSelectStatement().toSQL();
    }
    
    @Override
    public void execute(final ObjectProvider[] ops) {
        if (ops == null || ops.length == 0) {
            return;
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            final StringBuffer str = new StringBuffer();
            for (int i = 0; i < ops.length; ++i) {
                if (i > 0) {
                    str.append(", ");
                }
                str.append(ops[i].getInternalObjectId());
            }
            NucleusLogger.PERSISTENCE.debug(LocateBulkRequest.LOCALISER.msg("052223", str.toString(), this.table));
        }
        final ExecutionContext ec = ops[0].getExecutionContext();
        final RDBMSStoreManager storeMgr = this.table.getStoreManager();
        final AbstractClassMetaData cmd = ops[0].getClassMetaData();
        boolean locked = ec.getSerializeReadForClass(cmd.getFullClassName());
        final short lockType = ec.getLockManager().getLockMode(ops[0].getInternalObjectId());
        if (lockType != 0 && (lockType == 3 || lockType == 4)) {
            locked = true;
        }
        final String statement = this.getStatement(this.table, ops, locked);
        try {
            final ManagedConnection mconn = storeMgr.getConnection(ec);
            final SQLController sqlControl = storeMgr.getSQLController();
            try {
                final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, statement);
                try {
                    for (int j = 0; j < ops.length; ++j) {
                        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                            final StatementMappingIndex datastoreIdx = this.mappingDefinitions[j].getMappingForMemberPosition(-1);
                            for (int k = 0; k < datastoreIdx.getNumberOfParameterOccurrences(); ++k) {
                                this.table.getDatastoreObjectIdMapping().setObject(ec, ps, datastoreIdx.getParameterPositionsForOccurrence(k), ops[j].getInternalObjectId());
                            }
                        }
                        else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                            ops[j].provideFields(cmd.getPKMemberPositions(), storeMgr.getFieldManagerForStatementGeneration(ops[j], ps, this.mappingDefinitions[j]));
                        }
                    }
                    final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, statement, ps);
                    try {
                        final ObjectProvider[] missingOps = this.processResults(rs, ops);
                        if (missingOps != null && missingOps.length > 0) {
                            final NucleusObjectNotFoundException[] nfes = new NucleusObjectNotFoundException[missingOps.length];
                            for (int l = 0; l < nfes.length; ++l) {
                                nfes[l] = new NucleusObjectNotFoundException("Object not found", missingOps[l].getInternalObjectId());
                            }
                            throw new NucleusObjectNotFoundException("Some objects were not found. Look at nested exceptions for details", nfes);
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
            final String msg = LocateBulkRequest.LOCALISER.msg("052220", ops[0].getObjectAsPrintable(), statement, sqle.getMessage());
            NucleusLogger.DATASTORE_RETRIEVE.warn(msg);
            final List exceptions = new ArrayList();
            exceptions.add(sqle);
            while ((sqle = sqle.getNextException()) != null) {
                exceptions.add(sqle);
            }
            throw new NucleusDataStoreException(msg, exceptions.toArray(new Throwable[exceptions.size()]));
        }
    }
    
    private ObjectProvider[] processResults(final ResultSet rs, final ObjectProvider[] ops) throws SQLException {
        final List<ObjectProvider> missingOps = new ArrayList<ObjectProvider>();
        for (int i = 0; i < ops.length; ++i) {
            missingOps.add(ops[i]);
        }
        final ExecutionContext ec = ops[0].getExecutionContext();
        while (rs.next()) {
            final FieldManager resultFM = this.table.getStoreManager().getFieldManagerForResultProcessing(ec, rs, this.resultMapping, this.cmd);
            Object id = null;
            Object key = null;
            if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
                final StatementMappingIndex idx = this.resultMapping.getMappingForMemberPosition(-1);
                final JavaTypeMapping idMapping = idx.getMapping();
                key = idMapping.getObject(ec, rs, idx.getColumnPositions());
                if (key instanceof OID) {
                    key = ((OID)key).getKeyValue();
                }
            }
            else if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
                if (this.cmd.usesSingleFieldIdentityClass()) {
                    final int[] pkFieldNums = this.cmd.getPKMemberPositions();
                    final AbstractMemberMetaData pkMmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[0]);
                    if (pkMmd.getType() == Integer.TYPE) {
                        key = resultFM.fetchIntField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Short.TYPE) {
                        key = resultFM.fetchShortField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Long.TYPE) {
                        key = resultFM.fetchLongField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Character.TYPE) {
                        key = resultFM.fetchCharField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Boolean.TYPE) {
                        key = resultFM.fetchBooleanField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Byte.TYPE) {
                        key = resultFM.fetchByteField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Double.TYPE) {
                        key = resultFM.fetchDoubleField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == Float.TYPE) {
                        key = resultFM.fetchFloatField(pkFieldNums[0]);
                    }
                    else if (pkMmd.getType() == String.class) {
                        key = resultFM.fetchStringField(pkFieldNums[0]);
                    }
                    else {
                        key = resultFM.fetchObjectField(pkFieldNums[0]);
                    }
                }
                else {
                    id = IdentityUtils.getApplicationIdentityForResultSetRow(ec, this.cmd, null, true, resultFM);
                }
            }
            ObjectProvider op = null;
            for (final ObjectProvider missingOp : missingOps) {
                final Object opId = missingOp.getInternalObjectId();
                if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
                    Object opKey = ((OID)opId).getKeyValue();
                    if (opKey.getClass() != key.getClass()) {
                        opKey = TypeConversionHelper.convertTo(opKey, key.getClass());
                    }
                    if (opKey.equals(key)) {
                        op = missingOp;
                        break;
                    }
                    continue;
                }
                else {
                    if (this.cmd.getIdentityType() != IdentityType.APPLICATION) {
                        continue;
                    }
                    if (this.cmd.usesSingleFieldIdentityClass()) {
                        final Object opKey = ec.getApiAdapter().getTargetKeyForSingleFieldIdentity(opId);
                        if (opKey.equals(key)) {
                            op = missingOp;
                            break;
                        }
                        continue;
                    }
                    else {
                        if (opId.equals(id)) {
                            op = missingOp;
                            break;
                        }
                        continue;
                    }
                }
            }
            if (op != null) {
                missingOps.remove(op);
                final int[] selectedMemberNums = this.resultMapping.getMemberNumbers();
                final int[] unloadedMemberNums = ClassUtils.getFlagsSetTo(op.getLoadedFields(), selectedMemberNums, false);
                if (unloadedMemberNums != null && unloadedMemberNums.length > 0) {
                    op.replaceFields(unloadedMemberNums, resultFM);
                }
                if (op.getTransactionalVersion() != null || this.table.getVersionMapping(false) == null) {
                    continue;
                }
                final VersionMetaData currentVermd = this.table.getVersionMetaData();
                Object datastoreVersion = null;
                if (currentVermd != null && currentVermd.getFieldName() == null) {
                    final StatementMappingIndex verIdx = this.resultMapping.getMappingForMemberPosition(-2);
                    datastoreVersion = this.table.getVersionMapping(true).getObject(ec, rs, verIdx.getColumnPositions());
                }
                else {
                    datastoreVersion = op.provideField(this.cmd.getAbsolutePositionOfMember(currentVermd.getFieldName()));
                }
                op.setVersion(datastoreVersion);
            }
        }
        if (!missingOps.isEmpty()) {
            return missingOps.toArray(new ObjectProvider[missingOps.size()]);
        }
        return null;
    }
}
