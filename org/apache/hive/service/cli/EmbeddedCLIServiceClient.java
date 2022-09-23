// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.auth.HiveAuthFactory;
import java.util.List;
import java.util.Map;

public class EmbeddedCLIServiceClient extends CLIServiceClient
{
    private final ICLIService cliService;
    
    public EmbeddedCLIServiceClient(final ICLIService cliService) {
        this.cliService = cliService;
    }
    
    @Override
    public SessionHandle openSession(final String username, final String password, final Map<String, String> configuration) throws HiveSQLException {
        return this.cliService.openSession(username, password, configuration);
    }
    
    @Override
    public SessionHandle openSessionWithImpersonation(final String username, final String password, final Map<String, String> configuration, final String delegationToken) throws HiveSQLException {
        throw new HiveSQLException("Impersonated session is not supported in the embedded mode");
    }
    
    @Override
    public void closeSession(final SessionHandle sessionHandle) throws HiveSQLException {
        this.cliService.closeSession(sessionHandle);
    }
    
    @Override
    public GetInfoValue getInfo(final SessionHandle sessionHandle, final GetInfoType getInfoType) throws HiveSQLException {
        return this.cliService.getInfo(sessionHandle, getInfoType);
    }
    
    @Override
    public OperationHandle executeStatement(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        return this.cliService.executeStatement(sessionHandle, statement, confOverlay);
    }
    
    @Override
    public OperationHandle executeStatementAsync(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        return this.cliService.executeStatementAsync(sessionHandle, statement, confOverlay);
    }
    
    @Override
    public OperationHandle getTypeInfo(final SessionHandle sessionHandle) throws HiveSQLException {
        return this.cliService.getTypeInfo(sessionHandle);
    }
    
    @Override
    public OperationHandle getCatalogs(final SessionHandle sessionHandle) throws HiveSQLException {
        return this.cliService.getCatalogs(sessionHandle);
    }
    
    @Override
    public OperationHandle getSchemas(final SessionHandle sessionHandle, final String catalogName, final String schemaName) throws HiveSQLException {
        return this.cliService.getSchemas(sessionHandle, catalogName, schemaName);
    }
    
    @Override
    public OperationHandle getTables(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String tableName, final List<String> tableTypes) throws HiveSQLException {
        return this.cliService.getTables(sessionHandle, catalogName, schemaName, tableName, tableTypes);
    }
    
    @Override
    public OperationHandle getTableTypes(final SessionHandle sessionHandle) throws HiveSQLException {
        return this.cliService.getTableTypes(sessionHandle);
    }
    
    @Override
    public OperationHandle getColumns(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String tableName, final String columnName) throws HiveSQLException {
        return this.cliService.getColumns(sessionHandle, catalogName, schemaName, tableName, columnName);
    }
    
    @Override
    public OperationHandle getFunctions(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String functionName) throws HiveSQLException {
        return this.cliService.getFunctions(sessionHandle, catalogName, schemaName, functionName);
    }
    
    @Override
    public OperationStatus getOperationStatus(final OperationHandle opHandle) throws HiveSQLException {
        return this.cliService.getOperationStatus(opHandle);
    }
    
    @Override
    public void cancelOperation(final OperationHandle opHandle) throws HiveSQLException {
        this.cliService.cancelOperation(opHandle);
    }
    
    @Override
    public void closeOperation(final OperationHandle opHandle) throws HiveSQLException {
        this.cliService.closeOperation(opHandle);
    }
    
    @Override
    public TableSchema getResultSetMetadata(final OperationHandle opHandle) throws HiveSQLException {
        return this.cliService.getResultSetMetadata(opHandle);
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle, final FetchOrientation orientation, final long maxRows, final FetchType fetchType) throws HiveSQLException {
        return this.cliService.fetchResults(opHandle, orientation, maxRows, fetchType);
    }
    
    @Override
    public String getDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String owner, final String renewer) throws HiveSQLException {
        return this.cliService.getDelegationToken(sessionHandle, authFactory, owner, renewer);
    }
    
    @Override
    public void cancelDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        this.cliService.cancelDelegationToken(sessionHandle, authFactory, tokenStr);
    }
    
    @Override
    public void renewDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        this.cliService.renewDelegationToken(sessionHandle, authFactory, tokenStr);
    }
}
