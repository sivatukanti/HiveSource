// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TException;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.FetchType;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.OperationStatus;
import java.util.List;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.GetInfoValue;
import org.apache.hive.service.cli.GetInfoType;
import org.apache.hive.service.cli.SessionHandle;
import java.util.Map;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.CLIServiceClient;

public class ThriftCLIServiceClient extends CLIServiceClient
{
    private final TCLIService.Iface cliService;
    
    public ThriftCLIServiceClient(final TCLIService.Iface cliService) {
        this.cliService = cliService;
    }
    
    public void checkStatus(final TStatus status) throws HiveSQLException {
        if (TStatusCode.ERROR_STATUS.equals(status.getStatusCode())) {
            throw new HiveSQLException(status);
        }
    }
    
    @Override
    public SessionHandle openSession(final String username, final String password, final Map<String, String> configuration) throws HiveSQLException {
        try {
            final TOpenSessionReq req = new TOpenSessionReq();
            req.setUsername(username);
            req.setPassword(password);
            req.setConfiguration(configuration);
            final TOpenSessionResp resp = this.cliService.OpenSession(req);
            this.checkStatus(resp.getStatus());
            return new SessionHandle(resp.getSessionHandle(), resp.getServerProtocolVersion());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public SessionHandle openSessionWithImpersonation(final String username, final String password, final Map<String, String> configuration, final String delegationToken) throws HiveSQLException {
        throw new HiveSQLException("open with impersonation operation is not supported in the client");
    }
    
    @Override
    public void closeSession(final SessionHandle sessionHandle) throws HiveSQLException {
        try {
            final TCloseSessionReq req = new TCloseSessionReq(sessionHandle.toTSessionHandle());
            final TCloseSessionResp resp = this.cliService.CloseSession(req);
            this.checkStatus(resp.getStatus());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public GetInfoValue getInfo(final SessionHandle sessionHandle, final GetInfoType infoType) throws HiveSQLException {
        try {
            final TGetInfoReq req = new TGetInfoReq(sessionHandle.toTSessionHandle(), infoType.toTGetInfoType());
            final TGetInfoResp resp = this.cliService.GetInfo(req);
            this.checkStatus(resp.getStatus());
            return new GetInfoValue(resp.getInfoValue());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle executeStatement(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        return this.executeStatementInternal(sessionHandle, statement, confOverlay, false);
    }
    
    @Override
    public OperationHandle executeStatementAsync(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        return this.executeStatementInternal(sessionHandle, statement, confOverlay, true);
    }
    
    private OperationHandle executeStatementInternal(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay, final boolean isAsync) throws HiveSQLException {
        try {
            final TExecuteStatementReq req = new TExecuteStatementReq(sessionHandle.toTSessionHandle(), statement);
            req.setConfOverlay(confOverlay);
            req.setRunAsync(isAsync);
            final TExecuteStatementResp resp = this.cliService.ExecuteStatement(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getTypeInfo(final SessionHandle sessionHandle) throws HiveSQLException {
        try {
            final TGetTypeInfoReq req = new TGetTypeInfoReq(sessionHandle.toTSessionHandle());
            final TGetTypeInfoResp resp = this.cliService.GetTypeInfo(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getCatalogs(final SessionHandle sessionHandle) throws HiveSQLException {
        try {
            final TGetCatalogsReq req = new TGetCatalogsReq(sessionHandle.toTSessionHandle());
            final TGetCatalogsResp resp = this.cliService.GetCatalogs(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getSchemas(final SessionHandle sessionHandle, final String catalogName, final String schemaName) throws HiveSQLException {
        try {
            final TGetSchemasReq req = new TGetSchemasReq(sessionHandle.toTSessionHandle());
            req.setCatalogName(catalogName);
            req.setSchemaName(schemaName);
            final TGetSchemasResp resp = this.cliService.GetSchemas(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getTables(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String tableName, final List<String> tableTypes) throws HiveSQLException {
        try {
            final TGetTablesReq req = new TGetTablesReq(sessionHandle.toTSessionHandle());
            req.setTableName(tableName);
            req.setTableTypes(tableTypes);
            req.setSchemaName(schemaName);
            final TGetTablesResp resp = this.cliService.GetTables(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getTableTypes(final SessionHandle sessionHandle) throws HiveSQLException {
        try {
            final TGetTableTypesReq req = new TGetTableTypesReq(sessionHandle.toTSessionHandle());
            final TGetTableTypesResp resp = this.cliService.GetTableTypes(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getColumns(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String tableName, final String columnName) throws HiveSQLException {
        try {
            final TGetColumnsReq req = new TGetColumnsReq();
            req.setSessionHandle(sessionHandle.toTSessionHandle());
            req.setCatalogName(catalogName);
            req.setSchemaName(schemaName);
            req.setTableName(tableName);
            req.setColumnName(columnName);
            final TGetColumnsResp resp = this.cliService.GetColumns(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationHandle getFunctions(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String functionName) throws HiveSQLException {
        try {
            final TGetFunctionsReq req = new TGetFunctionsReq(sessionHandle.toTSessionHandle(), functionName);
            req.setCatalogName(catalogName);
            req.setSchemaName(schemaName);
            final TGetFunctionsResp resp = this.cliService.GetFunctions(req);
            this.checkStatus(resp.getStatus());
            final TProtocolVersion protocol = sessionHandle.getProtocolVersion();
            return new OperationHandle(resp.getOperationHandle(), protocol);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public OperationStatus getOperationStatus(final OperationHandle opHandle) throws HiveSQLException {
        try {
            final TGetOperationStatusReq req = new TGetOperationStatusReq(opHandle.toTOperationHandle());
            final TGetOperationStatusResp resp = this.cliService.GetOperationStatus(req);
            this.checkStatus(resp.getStatus());
            final OperationState opState = OperationState.getOperationState(resp.getOperationState());
            HiveSQLException opException = null;
            if (opState == OperationState.ERROR) {
                opException = new HiveSQLException(resp.getErrorMessage(), resp.getSqlState(), resp.getErrorCode());
            }
            return new OperationStatus(opState, opException);
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public void cancelOperation(final OperationHandle opHandle) throws HiveSQLException {
        try {
            final TCancelOperationReq req = new TCancelOperationReq(opHandle.toTOperationHandle());
            final TCancelOperationResp resp = this.cliService.CancelOperation(req);
            this.checkStatus(resp.getStatus());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public void closeOperation(final OperationHandle opHandle) throws HiveSQLException {
        try {
            final TCloseOperationReq req = new TCloseOperationReq(opHandle.toTOperationHandle());
            final TCloseOperationResp resp = this.cliService.CloseOperation(req);
            this.checkStatus(resp.getStatus());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public TableSchema getResultSetMetadata(final OperationHandle opHandle) throws HiveSQLException {
        try {
            final TGetResultSetMetadataReq req = new TGetResultSetMetadataReq(opHandle.toTOperationHandle());
            final TGetResultSetMetadataResp resp = this.cliService.GetResultSetMetadata(req);
            this.checkStatus(resp.getStatus());
            return new TableSchema(resp.getSchema());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle, final FetchOrientation orientation, final long maxRows, final FetchType fetchType) throws HiveSQLException {
        try {
            final TFetchResultsReq req = new TFetchResultsReq();
            req.setOperationHandle(opHandle.toTOperationHandle());
            req.setOrientation(orientation.toTFetchOrientation());
            req.setMaxRows(maxRows);
            req.setFetchType(fetchType.toTFetchType());
            final TFetchResultsResp resp = this.cliService.FetchResults(req);
            this.checkStatus(resp.getStatus());
            return RowSetFactory.create(resp.getResults(), opHandle.getProtocolVersion());
        }
        catch (HiveSQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new HiveSQLException(e2);
        }
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle) throws HiveSQLException {
        return this.fetchResults(opHandle, FetchOrientation.FETCH_NEXT, 10000L, FetchType.QUERY_OUTPUT);
    }
    
    @Override
    public String getDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String owner, final String renewer) throws HiveSQLException {
        final TGetDelegationTokenReq req = new TGetDelegationTokenReq(sessionHandle.toTSessionHandle(), owner, renewer);
        try {
            final TGetDelegationTokenResp tokenResp = this.cliService.GetDelegationToken(req);
            this.checkStatus(tokenResp.getStatus());
            return tokenResp.getDelegationToken();
        }
        catch (Exception e) {
            throw new HiveSQLException(e);
        }
    }
    
    @Override
    public void cancelDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        final TCancelDelegationTokenReq cancelReq = new TCancelDelegationTokenReq(sessionHandle.toTSessionHandle(), tokenStr);
        try {
            final TCancelDelegationTokenResp cancelResp = this.cliService.CancelDelegationToken(cancelReq);
            this.checkStatus(cancelResp.getStatus());
        }
        catch (TException e) {
            throw new HiveSQLException(e);
        }
    }
    
    @Override
    public void renewDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        final TRenewDelegationTokenReq cancelReq = new TRenewDelegationTokenReq(sessionHandle.toTSessionHandle(), tokenStr);
        try {
            final TRenewDelegationTokenResp renewResp = this.cliService.RenewDelegationToken(cancelReq);
            this.checkStatus(renewResp.getStatus());
        }
        catch (Exception e) {
            throw new HiveSQLException(e);
        }
    }
}
