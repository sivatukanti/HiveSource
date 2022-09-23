// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import java.lang.reflect.Modifier;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
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
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.metadata.IdentityType;
import java.util.Collection;
import java.util.HashSet;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.table.AbstractClassTable;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.mapping.MappingCallbacks;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;

public class FetchRequest extends Request
{
    private String statementUnlocked;
    private String statementLocked;
    private int[] memberNumbersToFetch;
    private StatementClassMapping mappingDefinition;
    private final MappingCallbacks[] callbacks;
    private int numberOfFieldsToFetch;
    private final String fieldsToFetch;
    private boolean fetchingSurrogateVersion;
    private String versionFieldName;
    
    public FetchRequest(final DatastoreClass classTable, final AbstractMemberMetaData[] mmds, final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        super(classTable);
        this.memberNumbersToFetch = null;
        this.numberOfFieldsToFetch = 0;
        this.fetchingSurrogateVersion = false;
        this.versionFieldName = null;
        final RDBMSStoreManager storeMgr = classTable.getStoreManager();
        boolean found = false;
        DatastoreClass candidateTable = classTable;
        if (mmds != null) {
            while (candidateTable != null) {
                for (int i = 0; i < mmds.length; ++i) {
                    final JavaTypeMapping m = candidateTable.getMemberMappingInDatastoreClass(mmds[i]);
                    if (m != null) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
                candidateTable = candidateTable.getSuperDatastoreClass();
            }
        }
        if (candidateTable == null) {
            candidateTable = classTable;
        }
        this.table = candidateTable;
        this.key = ((AbstractClassTable)this.table).getPrimaryKey();
        for (DatastoreClass currentTable = this.table; currentTable != null; currentTable = currentTable.getSuperDatastoreClass()) {
            final VersionMetaData currentVermd = currentTable.getVersionMetaData();
            if (currentVermd != null) {
                if (currentVermd.getFieldName() == null) {
                    this.fetchingSurrogateVersion = true;
                }
                else {
                    this.versionFieldName = currentVermd.getFieldName();
                }
            }
        }
        SQLStatement sqlStatement = new SQLStatement(storeMgr, this.table, null, null);
        this.mappingDefinition = new StatementClassMapping();
        final Collection<MappingCallbacks> fetchCallbacks = new HashSet<MappingCallbacks>();
        this.numberOfFieldsToFetch = this.processMembersOfClass(sqlStatement, mmds, this.table, sqlStatement.getPrimaryTable(), this.mappingDefinition, fetchCallbacks, clr);
        this.callbacks = fetchCallbacks.toArray(new MappingCallbacks[fetchCallbacks.size()]);
        this.memberNumbersToFetch = this.mappingDefinition.getMemberNumbers();
        int inputParamNum = 1;
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            final JavaTypeMapping datastoreIdMapping = this.table.getDatastoreObjectIdMapping();
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
        else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            final int[] pkNums = cmd.getPKMemberPositions();
            for (int j = 0; j < pkNums.length; ++j) {
                final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkNums[j]);
                final JavaTypeMapping pkMapping = this.table.getMemberMapping(mmd);
                final SQLExpression expr2 = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), pkMapping);
                final SQLExpression val2 = exprFactory.newLiteralParameter(sqlStatement, pkMapping, null, "PK" + j);
                sqlStatement.whereAnd(expr2.eq(val2), true);
                StatementMappingIndex pkIdx = this.mappingDefinition.getMappingForMemberPosition(pkNums[j]);
                if (pkIdx == null) {
                    pkIdx = new StatementMappingIndex(pkMapping);
                    this.mappingDefinition.addMappingForMember(pkNums[j], pkIdx);
                }
                final int[] inputParams = new int[pkMapping.getNumberOfDatastoreMappings()];
                for (int k = 0; k < pkMapping.getNumberOfDatastoreMappings(); ++k) {
                    inputParams[k] = inputParamNum++;
                }
                pkIdx.addParameterOccurrence(inputParams);
            }
        }
        if (this.table.getMultitenancyMapping() != null) {
            final JavaTypeMapping tenantMapping = this.table.getMultitenancyMapping();
            final SQLExpression tenantExpr = exprFactory.newExpression(sqlStatement, sqlStatement.getPrimaryTable(), tenantMapping);
            final SQLExpression tenantVal = exprFactory.newLiteral(sqlStatement, tenantMapping, storeMgr.getStringProperty("datanucleus.TenantID"));
            sqlStatement.whereAnd(tenantExpr.eq(tenantVal), true);
        }
        final StringBuffer str = new StringBuffer();
        if (mmds != null) {
            for (int j = 0; j < mmds.length; ++j) {
                if (!mmds[j].isPrimaryKey()) {
                    if (str.length() > 0) {
                        str.append(',');
                    }
                    str.append(mmds[j].getName());
                }
            }
        }
        if (this.fetchingSurrogateVersion) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append("[VERSION]");
        }
        if (!this.fetchingSurrogateVersion && this.numberOfFieldsToFetch == 0) {
            this.fieldsToFetch = null;
            sqlStatement = null;
            this.mappingDefinition = null;
        }
        else {
            this.fieldsToFetch = str.toString();
            this.statementUnlocked = sqlStatement.getSelectStatement().toSQL();
            sqlStatement.addExtension("lock-for-update", Boolean.TRUE);
            this.statementLocked = sqlStatement.getSelectStatement().toSQL();
        }
    }
    
    @Override
    public void execute(final ObjectProvider op) {
        if (this.fieldsToFetch != null && NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(FetchRequest.LOCALISER.msg("052218", op.getObjectAsPrintable(), this.fieldsToFetch, this.table));
        }
        if (!this.isFetchingVersionOnly() || !this.isVersionLoaded(op)) {
            if (this.statementLocked != null) {
                final ExecutionContext ec = op.getExecutionContext();
                final RDBMSStoreManager storeMgr = this.table.getStoreManager();
                boolean locked = ec.getSerializeReadForClass(op.getClassMetaData().getFullClassName());
                final short lockType = ec.getLockManager().getLockMode(op.getInternalObjectId());
                if (lockType != 0 && (lockType == 3 || lockType == 4)) {
                    locked = true;
                }
                final String statement = locked ? this.statementLocked : this.statementUnlocked;
                final StatementClassMapping mappingDef = this.mappingDefinition;
                try {
                    final ManagedConnection mconn = storeMgr.getConnection(ec);
                    final SQLController sqlControl = storeMgr.getSQLController();
                    try {
                        final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, statement);
                        final AbstractClassMetaData cmd = op.getClassMetaData();
                        try {
                            if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                                final StatementMappingIndex datastoreIdx = mappingDef.getMappingForMemberPosition(-1);
                                for (int i = 0; i < datastoreIdx.getNumberOfParameterOccurrences(); ++i) {
                                    this.table.getDatastoreObjectIdMapping().setObject(ec, ps, datastoreIdx.getParameterPositionsForOccurrence(i), op.getInternalObjectId());
                                }
                            }
                            else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                                op.provideFields(cmd.getPKMemberPositions(), storeMgr.getFieldManagerForStatementGeneration(op, ps, mappingDef));
                            }
                            final ResultSet rs = sqlControl.executeStatementQuery(ec, mconn, statement, ps);
                            try {
                                if (!rs.next()) {
                                    if (NucleusLogger.DATASTORE_RETRIEVE.isInfoEnabled()) {
                                        NucleusLogger.DATASTORE_RETRIEVE.info(FetchRequest.LOCALISER.msg("050018", op.getInternalObjectId()));
                                    }
                                    throw new NucleusObjectNotFoundException("No such database row", op.getInternalObjectId());
                                }
                                op.replaceFields(this.memberNumbersToFetch, storeMgr.getFieldManagerForResultProcessing(op, rs, mappingDef));
                                if (op.getTransactionalVersion() == null) {
                                    Object datastoreVersion = null;
                                    if (this.fetchingSurrogateVersion) {
                                        final StatementMappingIndex verIdx = mappingDef.getMappingForMemberPosition(-2);
                                        datastoreVersion = this.table.getVersionMapping(true).getObject(ec, rs, verIdx.getColumnPositions());
                                    }
                                    else if (this.versionFieldName != null) {
                                        datastoreVersion = op.provideField(cmd.getAbsolutePositionOfMember(this.versionFieldName));
                                    }
                                    op.setVersion(datastoreVersion);
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
                    final String msg = FetchRequest.LOCALISER.msg("052219", op.getObjectAsPrintable(), statement, sqle.getMessage());
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
        for (int j = 0; j < this.callbacks.length; ++j) {
            this.callbacks[j].postFetch(op);
        }
    }
    
    private boolean isVersionLoaded(final ObjectProvider op) {
        return op.getObject() != null && op.getVersion() != null;
    }
    
    private boolean isFetchingVersionOnly() {
        return (this.fetchingSurrogateVersion || this.versionFieldName != null) && this.numberOfFieldsToFetch == 0;
    }
    
    protected int processMembersOfClass(final SQLStatement sqlStatement, final AbstractMemberMetaData[] mmds, final DatastoreClass table, final SQLTable sqlTbl, final StatementClassMapping mappingDef, final Collection fetchCallbacks, final ClassLoaderResolver clr) {
        int number = 0;
        if (mmds != null) {
            for (int i = 0; i < mmds.length; ++i) {
                final AbstractMemberMetaData mmd = mmds[i];
                final JavaTypeMapping mapping = table.getMemberMapping(mmd);
                if (mapping != null) {
                    if (!mmd.isPrimaryKey() && mapping.includeInFetchStatement()) {
                        int depth = 0;
                        if (mapping instanceof PersistableMapping) {
                            depth = 1;
                            if (Modifier.isAbstract(mmd.getType().getModifiers())) {
                                final DatastoreClass relTable = table.getStoreManager().getDatastoreClass(mmd.getTypeName(), clr);
                                if (relTable != null && relTable.getDiscriminatorMapping(false) == null) {
                                    final String[] subclasses = table.getStoreManager().getMetaDataManager().getSubclassesForClass(mmd.getTypeName(), false);
                                    if (subclasses != null && subclasses.length > 0) {
                                        depth = 0;
                                    }
                                }
                            }
                        }
                        SQLStatementHelper.selectMemberOfSourceInStatement(sqlStatement, mappingDef, null, sqlTbl, mmd, clr, depth);
                        ++number;
                    }
                    if (mapping instanceof MappingCallbacks) {
                        fetchCallbacks.add(mapping);
                    }
                }
            }
        }
        final JavaTypeMapping verMapping = table.getVersionMapping(true);
        if (verMapping != null) {
            final StatementMappingIndex verMapIdx = new StatementMappingIndex(verMapping);
            final SQLTable verSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(sqlStatement, sqlTbl, verMapping);
            final int[] cols = sqlStatement.select(verSqlTbl, verMapping, null);
            verMapIdx.setColumnPositions(cols);
            mappingDef.addMappingForMember(-2, verMapIdx);
        }
        return number;
    }
}
