// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.TFieldIdEnum;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.apache.thrift.ProcessFunction;
import java.util.HashMap;
import org.slf4j.Logger;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TServiceClient;

public class TCLIService
{
    public static class Client extends TServiceClient implements Iface
    {
        public Client(final TProtocol prot) {
            super(prot, prot);
        }
        
        public Client(final TProtocol iprot, final TProtocol oprot) {
            super(iprot, oprot);
        }
        
        @Override
        public TOpenSessionResp OpenSession(final TOpenSessionReq req) throws TException {
            this.send_OpenSession(req);
            return this.recv_OpenSession();
        }
        
        public void send_OpenSession(final TOpenSessionReq req) throws TException {
            final OpenSession_args args = new OpenSession_args();
            args.setReq(req);
            this.sendBase("OpenSession", args);
        }
        
        public TOpenSessionResp recv_OpenSession() throws TException {
            final OpenSession_result result = new OpenSession_result();
            this.receiveBase(result, "OpenSession");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "OpenSession failed: unknown result");
        }
        
        @Override
        public TCloseSessionResp CloseSession(final TCloseSessionReq req) throws TException {
            this.send_CloseSession(req);
            return this.recv_CloseSession();
        }
        
        public void send_CloseSession(final TCloseSessionReq req) throws TException {
            final CloseSession_args args = new CloseSession_args();
            args.setReq(req);
            this.sendBase("CloseSession", args);
        }
        
        public TCloseSessionResp recv_CloseSession() throws TException {
            final CloseSession_result result = new CloseSession_result();
            this.receiveBase(result, "CloseSession");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "CloseSession failed: unknown result");
        }
        
        @Override
        public TGetInfoResp GetInfo(final TGetInfoReq req) throws TException {
            this.send_GetInfo(req);
            return this.recv_GetInfo();
        }
        
        public void send_GetInfo(final TGetInfoReq req) throws TException {
            final GetInfo_args args = new GetInfo_args();
            args.setReq(req);
            this.sendBase("GetInfo", args);
        }
        
        public TGetInfoResp recv_GetInfo() throws TException {
            final GetInfo_result result = new GetInfo_result();
            this.receiveBase(result, "GetInfo");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetInfo failed: unknown result");
        }
        
        @Override
        public TExecuteStatementResp ExecuteStatement(final TExecuteStatementReq req) throws TException {
            this.send_ExecuteStatement(req);
            return this.recv_ExecuteStatement();
        }
        
        public void send_ExecuteStatement(final TExecuteStatementReq req) throws TException {
            final ExecuteStatement_args args = new ExecuteStatement_args();
            args.setReq(req);
            this.sendBase("ExecuteStatement", args);
        }
        
        public TExecuteStatementResp recv_ExecuteStatement() throws TException {
            final ExecuteStatement_result result = new ExecuteStatement_result();
            this.receiveBase(result, "ExecuteStatement");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "ExecuteStatement failed: unknown result");
        }
        
        @Override
        public TGetTypeInfoResp GetTypeInfo(final TGetTypeInfoReq req) throws TException {
            this.send_GetTypeInfo(req);
            return this.recv_GetTypeInfo();
        }
        
        public void send_GetTypeInfo(final TGetTypeInfoReq req) throws TException {
            final GetTypeInfo_args args = new GetTypeInfo_args();
            args.setReq(req);
            this.sendBase("GetTypeInfo", args);
        }
        
        public TGetTypeInfoResp recv_GetTypeInfo() throws TException {
            final GetTypeInfo_result result = new GetTypeInfo_result();
            this.receiveBase(result, "GetTypeInfo");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetTypeInfo failed: unknown result");
        }
        
        @Override
        public TGetCatalogsResp GetCatalogs(final TGetCatalogsReq req) throws TException {
            this.send_GetCatalogs(req);
            return this.recv_GetCatalogs();
        }
        
        public void send_GetCatalogs(final TGetCatalogsReq req) throws TException {
            final GetCatalogs_args args = new GetCatalogs_args();
            args.setReq(req);
            this.sendBase("GetCatalogs", args);
        }
        
        public TGetCatalogsResp recv_GetCatalogs() throws TException {
            final GetCatalogs_result result = new GetCatalogs_result();
            this.receiveBase(result, "GetCatalogs");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetCatalogs failed: unknown result");
        }
        
        @Override
        public TGetSchemasResp GetSchemas(final TGetSchemasReq req) throws TException {
            this.send_GetSchemas(req);
            return this.recv_GetSchemas();
        }
        
        public void send_GetSchemas(final TGetSchemasReq req) throws TException {
            final GetSchemas_args args = new GetSchemas_args();
            args.setReq(req);
            this.sendBase("GetSchemas", args);
        }
        
        public TGetSchemasResp recv_GetSchemas() throws TException {
            final GetSchemas_result result = new GetSchemas_result();
            this.receiveBase(result, "GetSchemas");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetSchemas failed: unknown result");
        }
        
        @Override
        public TGetTablesResp GetTables(final TGetTablesReq req) throws TException {
            this.send_GetTables(req);
            return this.recv_GetTables();
        }
        
        public void send_GetTables(final TGetTablesReq req) throws TException {
            final GetTables_args args = new GetTables_args();
            args.setReq(req);
            this.sendBase("GetTables", args);
        }
        
        public TGetTablesResp recv_GetTables() throws TException {
            final GetTables_result result = new GetTables_result();
            this.receiveBase(result, "GetTables");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetTables failed: unknown result");
        }
        
        @Override
        public TGetTableTypesResp GetTableTypes(final TGetTableTypesReq req) throws TException {
            this.send_GetTableTypes(req);
            return this.recv_GetTableTypes();
        }
        
        public void send_GetTableTypes(final TGetTableTypesReq req) throws TException {
            final GetTableTypes_args args = new GetTableTypes_args();
            args.setReq(req);
            this.sendBase("GetTableTypes", args);
        }
        
        public TGetTableTypesResp recv_GetTableTypes() throws TException {
            final GetTableTypes_result result = new GetTableTypes_result();
            this.receiveBase(result, "GetTableTypes");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetTableTypes failed: unknown result");
        }
        
        @Override
        public TGetColumnsResp GetColumns(final TGetColumnsReq req) throws TException {
            this.send_GetColumns(req);
            return this.recv_GetColumns();
        }
        
        public void send_GetColumns(final TGetColumnsReq req) throws TException {
            final GetColumns_args args = new GetColumns_args();
            args.setReq(req);
            this.sendBase("GetColumns", args);
        }
        
        public TGetColumnsResp recv_GetColumns() throws TException {
            final GetColumns_result result = new GetColumns_result();
            this.receiveBase(result, "GetColumns");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetColumns failed: unknown result");
        }
        
        @Override
        public TGetFunctionsResp GetFunctions(final TGetFunctionsReq req) throws TException {
            this.send_GetFunctions(req);
            return this.recv_GetFunctions();
        }
        
        public void send_GetFunctions(final TGetFunctionsReq req) throws TException {
            final GetFunctions_args args = new GetFunctions_args();
            args.setReq(req);
            this.sendBase("GetFunctions", args);
        }
        
        public TGetFunctionsResp recv_GetFunctions() throws TException {
            final GetFunctions_result result = new GetFunctions_result();
            this.receiveBase(result, "GetFunctions");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetFunctions failed: unknown result");
        }
        
        @Override
        public TGetOperationStatusResp GetOperationStatus(final TGetOperationStatusReq req) throws TException {
            this.send_GetOperationStatus(req);
            return this.recv_GetOperationStatus();
        }
        
        public void send_GetOperationStatus(final TGetOperationStatusReq req) throws TException {
            final GetOperationStatus_args args = new GetOperationStatus_args();
            args.setReq(req);
            this.sendBase("GetOperationStatus", args);
        }
        
        public TGetOperationStatusResp recv_GetOperationStatus() throws TException {
            final GetOperationStatus_result result = new GetOperationStatus_result();
            this.receiveBase(result, "GetOperationStatus");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetOperationStatus failed: unknown result");
        }
        
        @Override
        public TCancelOperationResp CancelOperation(final TCancelOperationReq req) throws TException {
            this.send_CancelOperation(req);
            return this.recv_CancelOperation();
        }
        
        public void send_CancelOperation(final TCancelOperationReq req) throws TException {
            final CancelOperation_args args = new CancelOperation_args();
            args.setReq(req);
            this.sendBase("CancelOperation", args);
        }
        
        public TCancelOperationResp recv_CancelOperation() throws TException {
            final CancelOperation_result result = new CancelOperation_result();
            this.receiveBase(result, "CancelOperation");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "CancelOperation failed: unknown result");
        }
        
        @Override
        public TCloseOperationResp CloseOperation(final TCloseOperationReq req) throws TException {
            this.send_CloseOperation(req);
            return this.recv_CloseOperation();
        }
        
        public void send_CloseOperation(final TCloseOperationReq req) throws TException {
            final CloseOperation_args args = new CloseOperation_args();
            args.setReq(req);
            this.sendBase("CloseOperation", args);
        }
        
        public TCloseOperationResp recv_CloseOperation() throws TException {
            final CloseOperation_result result = new CloseOperation_result();
            this.receiveBase(result, "CloseOperation");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "CloseOperation failed: unknown result");
        }
        
        @Override
        public TGetResultSetMetadataResp GetResultSetMetadata(final TGetResultSetMetadataReq req) throws TException {
            this.send_GetResultSetMetadata(req);
            return this.recv_GetResultSetMetadata();
        }
        
        public void send_GetResultSetMetadata(final TGetResultSetMetadataReq req) throws TException {
            final GetResultSetMetadata_args args = new GetResultSetMetadata_args();
            args.setReq(req);
            this.sendBase("GetResultSetMetadata", args);
        }
        
        public TGetResultSetMetadataResp recv_GetResultSetMetadata() throws TException {
            final GetResultSetMetadata_result result = new GetResultSetMetadata_result();
            this.receiveBase(result, "GetResultSetMetadata");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetResultSetMetadata failed: unknown result");
        }
        
        @Override
        public TFetchResultsResp FetchResults(final TFetchResultsReq req) throws TException {
            this.send_FetchResults(req);
            return this.recv_FetchResults();
        }
        
        public void send_FetchResults(final TFetchResultsReq req) throws TException {
            final FetchResults_args args = new FetchResults_args();
            args.setReq(req);
            this.sendBase("FetchResults", args);
        }
        
        public TFetchResultsResp recv_FetchResults() throws TException {
            final FetchResults_result result = new FetchResults_result();
            this.receiveBase(result, "FetchResults");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "FetchResults failed: unknown result");
        }
        
        @Override
        public TGetDelegationTokenResp GetDelegationToken(final TGetDelegationTokenReq req) throws TException {
            this.send_GetDelegationToken(req);
            return this.recv_GetDelegationToken();
        }
        
        public void send_GetDelegationToken(final TGetDelegationTokenReq req) throws TException {
            final GetDelegationToken_args args = new GetDelegationToken_args();
            args.setReq(req);
            this.sendBase("GetDelegationToken", args);
        }
        
        public TGetDelegationTokenResp recv_GetDelegationToken() throws TException {
            final GetDelegationToken_result result = new GetDelegationToken_result();
            this.receiveBase(result, "GetDelegationToken");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "GetDelegationToken failed: unknown result");
        }
        
        @Override
        public TCancelDelegationTokenResp CancelDelegationToken(final TCancelDelegationTokenReq req) throws TException {
            this.send_CancelDelegationToken(req);
            return this.recv_CancelDelegationToken();
        }
        
        public void send_CancelDelegationToken(final TCancelDelegationTokenReq req) throws TException {
            final CancelDelegationToken_args args = new CancelDelegationToken_args();
            args.setReq(req);
            this.sendBase("CancelDelegationToken", args);
        }
        
        public TCancelDelegationTokenResp recv_CancelDelegationToken() throws TException {
            final CancelDelegationToken_result result = new CancelDelegationToken_result();
            this.receiveBase(result, "CancelDelegationToken");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "CancelDelegationToken failed: unknown result");
        }
        
        @Override
        public TRenewDelegationTokenResp RenewDelegationToken(final TRenewDelegationTokenReq req) throws TException {
            this.send_RenewDelegationToken(req);
            return this.recv_RenewDelegationToken();
        }
        
        public void send_RenewDelegationToken(final TRenewDelegationTokenReq req) throws TException {
            final RenewDelegationToken_args args = new RenewDelegationToken_args();
            args.setReq(req);
            this.sendBase("RenewDelegationToken", args);
        }
        
        public TRenewDelegationTokenResp recv_RenewDelegationToken() throws TException {
            final RenewDelegationToken_result result = new RenewDelegationToken_result();
            this.receiveBase(result, "RenewDelegationToken");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "RenewDelegationToken failed: unknown result");
        }
        
        public static class Factory implements TServiceClientFactory<Client>
        {
            @Override
            public Client getClient(final TProtocol prot) {
                return new Client(prot);
            }
            
            @Override
            public Client getClient(final TProtocol iprot, final TProtocol oprot) {
                return new Client(iprot, oprot);
            }
        }
    }
    
    public static class AsyncClient extends TAsyncClient implements AsyncIface
    {
        public AsyncClient(final TProtocolFactory protocolFactory, final TAsyncClientManager clientManager, final TNonblockingTransport transport) {
            super(protocolFactory, clientManager, transport);
        }
        
        @Override
        public void OpenSession(final TOpenSessionReq req, final AsyncMethodCallback<OpenSession_call> resultHandler) throws TException {
            this.checkReady();
            final OpenSession_call method_call = new OpenSession_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void CloseSession(final TCloseSessionReq req, final AsyncMethodCallback<CloseSession_call> resultHandler) throws TException {
            this.checkReady();
            final CloseSession_call method_call = new CloseSession_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetInfo(final TGetInfoReq req, final AsyncMethodCallback<GetInfo_call> resultHandler) throws TException {
            this.checkReady();
            final GetInfo_call method_call = new GetInfo_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void ExecuteStatement(final TExecuteStatementReq req, final AsyncMethodCallback<ExecuteStatement_call> resultHandler) throws TException {
            this.checkReady();
            final ExecuteStatement_call method_call = new ExecuteStatement_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetTypeInfo(final TGetTypeInfoReq req, final AsyncMethodCallback<GetTypeInfo_call> resultHandler) throws TException {
            this.checkReady();
            final GetTypeInfo_call method_call = new GetTypeInfo_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetCatalogs(final TGetCatalogsReq req, final AsyncMethodCallback<GetCatalogs_call> resultHandler) throws TException {
            this.checkReady();
            final GetCatalogs_call method_call = new GetCatalogs_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetSchemas(final TGetSchemasReq req, final AsyncMethodCallback<GetSchemas_call> resultHandler) throws TException {
            this.checkReady();
            final GetSchemas_call method_call = new GetSchemas_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetTables(final TGetTablesReq req, final AsyncMethodCallback<GetTables_call> resultHandler) throws TException {
            this.checkReady();
            final GetTables_call method_call = new GetTables_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetTableTypes(final TGetTableTypesReq req, final AsyncMethodCallback<GetTableTypes_call> resultHandler) throws TException {
            this.checkReady();
            final GetTableTypes_call method_call = new GetTableTypes_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetColumns(final TGetColumnsReq req, final AsyncMethodCallback<GetColumns_call> resultHandler) throws TException {
            this.checkReady();
            final GetColumns_call method_call = new GetColumns_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetFunctions(final TGetFunctionsReq req, final AsyncMethodCallback<GetFunctions_call> resultHandler) throws TException {
            this.checkReady();
            final GetFunctions_call method_call = new GetFunctions_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetOperationStatus(final TGetOperationStatusReq req, final AsyncMethodCallback<GetOperationStatus_call> resultHandler) throws TException {
            this.checkReady();
            final GetOperationStatus_call method_call = new GetOperationStatus_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void CancelOperation(final TCancelOperationReq req, final AsyncMethodCallback<CancelOperation_call> resultHandler) throws TException {
            this.checkReady();
            final CancelOperation_call method_call = new CancelOperation_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void CloseOperation(final TCloseOperationReq req, final AsyncMethodCallback<CloseOperation_call> resultHandler) throws TException {
            this.checkReady();
            final CloseOperation_call method_call = new CloseOperation_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetResultSetMetadata(final TGetResultSetMetadataReq req, final AsyncMethodCallback<GetResultSetMetadata_call> resultHandler) throws TException {
            this.checkReady();
            final GetResultSetMetadata_call method_call = new GetResultSetMetadata_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void FetchResults(final TFetchResultsReq req, final AsyncMethodCallback<FetchResults_call> resultHandler) throws TException {
            this.checkReady();
            final FetchResults_call method_call = new FetchResults_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void GetDelegationToken(final TGetDelegationTokenReq req, final AsyncMethodCallback<GetDelegationToken_call> resultHandler) throws TException {
            this.checkReady();
            final GetDelegationToken_call method_call = new GetDelegationToken_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void CancelDelegationToken(final TCancelDelegationTokenReq req, final AsyncMethodCallback<CancelDelegationToken_call> resultHandler) throws TException {
            this.checkReady();
            final CancelDelegationToken_call method_call = new CancelDelegationToken_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void RenewDelegationToken(final TRenewDelegationTokenReq req, final AsyncMethodCallback<RenewDelegationToken_call> resultHandler) throws TException {
            this.checkReady();
            final RenewDelegationToken_call method_call = new RenewDelegationToken_call(req, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        public static class Factory implements TAsyncClientFactory<AsyncClient>
        {
            private TAsyncClientManager clientManager;
            private TProtocolFactory protocolFactory;
            
            public Factory(final TAsyncClientManager clientManager, final TProtocolFactory protocolFactory) {
                this.clientManager = clientManager;
                this.protocolFactory = protocolFactory;
            }
            
            @Override
            public AsyncClient getAsyncClient(final TNonblockingTransport transport) {
                return new AsyncClient(this.protocolFactory, this.clientManager, transport);
            }
        }
        
        public static class OpenSession_call extends TAsyncMethodCall
        {
            private TOpenSessionReq req;
            
            public OpenSession_call(final TOpenSessionReq req, final AsyncMethodCallback<OpenSession_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("OpenSession", (byte)1, 0));
                final OpenSession_args args = new OpenSession_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TOpenSessionResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_OpenSession();
            }
        }
        
        public static class CloseSession_call extends TAsyncMethodCall
        {
            private TCloseSessionReq req;
            
            public CloseSession_call(final TCloseSessionReq req, final AsyncMethodCallback<CloseSession_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("CloseSession", (byte)1, 0));
                final CloseSession_args args = new CloseSession_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TCloseSessionResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_CloseSession();
            }
        }
        
        public static class GetInfo_call extends TAsyncMethodCall
        {
            private TGetInfoReq req;
            
            public GetInfo_call(final TGetInfoReq req, final AsyncMethodCallback<GetInfo_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetInfo", (byte)1, 0));
                final GetInfo_args args = new GetInfo_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetInfoResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetInfo();
            }
        }
        
        public static class ExecuteStatement_call extends TAsyncMethodCall
        {
            private TExecuteStatementReq req;
            
            public ExecuteStatement_call(final TExecuteStatementReq req, final AsyncMethodCallback<ExecuteStatement_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("ExecuteStatement", (byte)1, 0));
                final ExecuteStatement_args args = new ExecuteStatement_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TExecuteStatementResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_ExecuteStatement();
            }
        }
        
        public static class GetTypeInfo_call extends TAsyncMethodCall
        {
            private TGetTypeInfoReq req;
            
            public GetTypeInfo_call(final TGetTypeInfoReq req, final AsyncMethodCallback<GetTypeInfo_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetTypeInfo", (byte)1, 0));
                final GetTypeInfo_args args = new GetTypeInfo_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetTypeInfoResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetTypeInfo();
            }
        }
        
        public static class GetCatalogs_call extends TAsyncMethodCall
        {
            private TGetCatalogsReq req;
            
            public GetCatalogs_call(final TGetCatalogsReq req, final AsyncMethodCallback<GetCatalogs_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetCatalogs", (byte)1, 0));
                final GetCatalogs_args args = new GetCatalogs_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetCatalogsResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetCatalogs();
            }
        }
        
        public static class GetSchemas_call extends TAsyncMethodCall
        {
            private TGetSchemasReq req;
            
            public GetSchemas_call(final TGetSchemasReq req, final AsyncMethodCallback<GetSchemas_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetSchemas", (byte)1, 0));
                final GetSchemas_args args = new GetSchemas_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetSchemasResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetSchemas();
            }
        }
        
        public static class GetTables_call extends TAsyncMethodCall
        {
            private TGetTablesReq req;
            
            public GetTables_call(final TGetTablesReq req, final AsyncMethodCallback<GetTables_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetTables", (byte)1, 0));
                final GetTables_args args = new GetTables_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetTablesResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetTables();
            }
        }
        
        public static class GetTableTypes_call extends TAsyncMethodCall
        {
            private TGetTableTypesReq req;
            
            public GetTableTypes_call(final TGetTableTypesReq req, final AsyncMethodCallback<GetTableTypes_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetTableTypes", (byte)1, 0));
                final GetTableTypes_args args = new GetTableTypes_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetTableTypesResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetTableTypes();
            }
        }
        
        public static class GetColumns_call extends TAsyncMethodCall
        {
            private TGetColumnsReq req;
            
            public GetColumns_call(final TGetColumnsReq req, final AsyncMethodCallback<GetColumns_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetColumns", (byte)1, 0));
                final GetColumns_args args = new GetColumns_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetColumnsResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetColumns();
            }
        }
        
        public static class GetFunctions_call extends TAsyncMethodCall
        {
            private TGetFunctionsReq req;
            
            public GetFunctions_call(final TGetFunctionsReq req, final AsyncMethodCallback<GetFunctions_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetFunctions", (byte)1, 0));
                final GetFunctions_args args = new GetFunctions_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetFunctionsResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetFunctions();
            }
        }
        
        public static class GetOperationStatus_call extends TAsyncMethodCall
        {
            private TGetOperationStatusReq req;
            
            public GetOperationStatus_call(final TGetOperationStatusReq req, final AsyncMethodCallback<GetOperationStatus_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetOperationStatus", (byte)1, 0));
                final GetOperationStatus_args args = new GetOperationStatus_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetOperationStatusResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetOperationStatus();
            }
        }
        
        public static class CancelOperation_call extends TAsyncMethodCall
        {
            private TCancelOperationReq req;
            
            public CancelOperation_call(final TCancelOperationReq req, final AsyncMethodCallback<CancelOperation_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("CancelOperation", (byte)1, 0));
                final CancelOperation_args args = new CancelOperation_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TCancelOperationResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_CancelOperation();
            }
        }
        
        public static class CloseOperation_call extends TAsyncMethodCall
        {
            private TCloseOperationReq req;
            
            public CloseOperation_call(final TCloseOperationReq req, final AsyncMethodCallback<CloseOperation_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("CloseOperation", (byte)1, 0));
                final CloseOperation_args args = new CloseOperation_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TCloseOperationResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_CloseOperation();
            }
        }
        
        public static class GetResultSetMetadata_call extends TAsyncMethodCall
        {
            private TGetResultSetMetadataReq req;
            
            public GetResultSetMetadata_call(final TGetResultSetMetadataReq req, final AsyncMethodCallback<GetResultSetMetadata_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetResultSetMetadata", (byte)1, 0));
                final GetResultSetMetadata_args args = new GetResultSetMetadata_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetResultSetMetadataResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetResultSetMetadata();
            }
        }
        
        public static class FetchResults_call extends TAsyncMethodCall
        {
            private TFetchResultsReq req;
            
            public FetchResults_call(final TFetchResultsReq req, final AsyncMethodCallback<FetchResults_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("FetchResults", (byte)1, 0));
                final FetchResults_args args = new FetchResults_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TFetchResultsResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_FetchResults();
            }
        }
        
        public static class GetDelegationToken_call extends TAsyncMethodCall
        {
            private TGetDelegationTokenReq req;
            
            public GetDelegationToken_call(final TGetDelegationTokenReq req, final AsyncMethodCallback<GetDelegationToken_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("GetDelegationToken", (byte)1, 0));
                final GetDelegationToken_args args = new GetDelegationToken_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TGetDelegationTokenResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_GetDelegationToken();
            }
        }
        
        public static class CancelDelegationToken_call extends TAsyncMethodCall
        {
            private TCancelDelegationTokenReq req;
            
            public CancelDelegationToken_call(final TCancelDelegationTokenReq req, final AsyncMethodCallback<CancelDelegationToken_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("CancelDelegationToken", (byte)1, 0));
                final CancelDelegationToken_args args = new CancelDelegationToken_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TCancelDelegationTokenResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_CancelDelegationToken();
            }
        }
        
        public static class RenewDelegationToken_call extends TAsyncMethodCall
        {
            private TRenewDelegationTokenReq req;
            
            public RenewDelegationToken_call(final TRenewDelegationTokenReq req, final AsyncMethodCallback<RenewDelegationToken_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.req = req;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("RenewDelegationToken", (byte)1, 0));
                final RenewDelegationToken_args args = new RenewDelegationToken_args();
                args.setReq(this.req);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public TRenewDelegationTokenResp getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_RenewDelegationToken();
            }
        }
    }
    
    public static class Processor<I extends Iface> extends TBaseProcessor<I> implements TProcessor
    {
        private static final Logger LOGGER;
        
        public Processor(final I iface) {
            super(iface, (Map<String, ProcessFunction<I, ? extends TBase>>)getProcessMap(new HashMap<String, ProcessFunction<I, ? extends TBase>>()));
        }
        
        protected Processor(final I iface, final Map<String, ProcessFunction<I, ? extends TBase>> processMap) {
            super(iface, (Map<String, ProcessFunction<I, ? extends TBase>>)getProcessMap((Map<String, ProcessFunction<I, ? extends TBase>>)processMap));
        }
        
        private static <I extends Iface> Map<String, ProcessFunction<I, ? extends TBase>> getProcessMap(final Map<String, ProcessFunction<I, ? extends TBase>> processMap) {
            processMap.put("OpenSession", (ProcessFunction<I, ? extends TBase>)new OpenSession());
            processMap.put("CloseSession", (ProcessFunction<I, ? extends TBase>)new CloseSession());
            processMap.put("GetInfo", (ProcessFunction<I, ? extends TBase>)new GetInfo());
            processMap.put("ExecuteStatement", (ProcessFunction<I, ? extends TBase>)new ExecuteStatement());
            processMap.put("GetTypeInfo", (ProcessFunction<I, ? extends TBase>)new GetTypeInfo());
            processMap.put("GetCatalogs", (ProcessFunction<I, ? extends TBase>)new GetCatalogs());
            processMap.put("GetSchemas", (ProcessFunction<I, ? extends TBase>)new GetSchemas());
            processMap.put("GetTables", (ProcessFunction<I, ? extends TBase>)new GetTables());
            processMap.put("GetTableTypes", (ProcessFunction<I, ? extends TBase>)new GetTableTypes());
            processMap.put("GetColumns", (ProcessFunction<I, ? extends TBase>)new GetColumns());
            processMap.put("GetFunctions", (ProcessFunction<I, ? extends TBase>)new GetFunctions());
            processMap.put("GetOperationStatus", (ProcessFunction<I, ? extends TBase>)new GetOperationStatus());
            processMap.put("CancelOperation", (ProcessFunction<I, ? extends TBase>)new CancelOperation());
            processMap.put("CloseOperation", (ProcessFunction<I, ? extends TBase>)new CloseOperation());
            processMap.put("GetResultSetMetadata", (ProcessFunction<I, ? extends TBase>)new GetResultSetMetadata());
            processMap.put("FetchResults", (ProcessFunction<I, ? extends TBase>)new FetchResults());
            processMap.put("GetDelegationToken", (ProcessFunction<I, ? extends TBase>)new GetDelegationToken());
            processMap.put("CancelDelegationToken", (ProcessFunction<I, ? extends TBase>)new CancelDelegationToken());
            processMap.put("RenewDelegationToken", (ProcessFunction<I, ? extends TBase>)new RenewDelegationToken());
            return processMap;
        }
        
        static {
            LOGGER = LoggerFactory.getLogger(Processor.class.getName());
        }
        
        public static class OpenSession<I extends Iface> extends ProcessFunction<I, OpenSession_args>
        {
            public OpenSession() {
                super("OpenSession");
            }
            
            @Override
            public OpenSession_args getEmptyArgsInstance() {
                return new OpenSession_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public OpenSession_result getResult(final I iface, final OpenSession_args args) throws TException {
                final OpenSession_result result = new OpenSession_result();
                result.success = iface.OpenSession(args.req);
                return result;
            }
        }
        
        public static class CloseSession<I extends Iface> extends ProcessFunction<I, CloseSession_args>
        {
            public CloseSession() {
                super("CloseSession");
            }
            
            @Override
            public CloseSession_args getEmptyArgsInstance() {
                return new CloseSession_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public CloseSession_result getResult(final I iface, final CloseSession_args args) throws TException {
                final CloseSession_result result = new CloseSession_result();
                result.success = iface.CloseSession(args.req);
                return result;
            }
        }
        
        public static class GetInfo<I extends Iface> extends ProcessFunction<I, GetInfo_args>
        {
            public GetInfo() {
                super("GetInfo");
            }
            
            @Override
            public GetInfo_args getEmptyArgsInstance() {
                return new GetInfo_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetInfo_result getResult(final I iface, final GetInfo_args args) throws TException {
                final GetInfo_result result = new GetInfo_result();
                result.success = iface.GetInfo(args.req);
                return result;
            }
        }
        
        public static class ExecuteStatement<I extends Iface> extends ProcessFunction<I, ExecuteStatement_args>
        {
            public ExecuteStatement() {
                super("ExecuteStatement");
            }
            
            @Override
            public ExecuteStatement_args getEmptyArgsInstance() {
                return new ExecuteStatement_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public ExecuteStatement_result getResult(final I iface, final ExecuteStatement_args args) throws TException {
                final ExecuteStatement_result result = new ExecuteStatement_result();
                result.success = iface.ExecuteStatement(args.req);
                return result;
            }
        }
        
        public static class GetTypeInfo<I extends Iface> extends ProcessFunction<I, GetTypeInfo_args>
        {
            public GetTypeInfo() {
                super("GetTypeInfo");
            }
            
            @Override
            public GetTypeInfo_args getEmptyArgsInstance() {
                return new GetTypeInfo_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetTypeInfo_result getResult(final I iface, final GetTypeInfo_args args) throws TException {
                final GetTypeInfo_result result = new GetTypeInfo_result();
                result.success = iface.GetTypeInfo(args.req);
                return result;
            }
        }
        
        public static class GetCatalogs<I extends Iface> extends ProcessFunction<I, GetCatalogs_args>
        {
            public GetCatalogs() {
                super("GetCatalogs");
            }
            
            @Override
            public GetCatalogs_args getEmptyArgsInstance() {
                return new GetCatalogs_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetCatalogs_result getResult(final I iface, final GetCatalogs_args args) throws TException {
                final GetCatalogs_result result = new GetCatalogs_result();
                result.success = iface.GetCatalogs(args.req);
                return result;
            }
        }
        
        public static class GetSchemas<I extends Iface> extends ProcessFunction<I, GetSchemas_args>
        {
            public GetSchemas() {
                super("GetSchemas");
            }
            
            @Override
            public GetSchemas_args getEmptyArgsInstance() {
                return new GetSchemas_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetSchemas_result getResult(final I iface, final GetSchemas_args args) throws TException {
                final GetSchemas_result result = new GetSchemas_result();
                result.success = iface.GetSchemas(args.req);
                return result;
            }
        }
        
        public static class GetTables<I extends Iface> extends ProcessFunction<I, GetTables_args>
        {
            public GetTables() {
                super("GetTables");
            }
            
            @Override
            public GetTables_args getEmptyArgsInstance() {
                return new GetTables_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetTables_result getResult(final I iface, final GetTables_args args) throws TException {
                final GetTables_result result = new GetTables_result();
                result.success = iface.GetTables(args.req);
                return result;
            }
        }
        
        public static class GetTableTypes<I extends Iface> extends ProcessFunction<I, GetTableTypes_args>
        {
            public GetTableTypes() {
                super("GetTableTypes");
            }
            
            @Override
            public GetTableTypes_args getEmptyArgsInstance() {
                return new GetTableTypes_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetTableTypes_result getResult(final I iface, final GetTableTypes_args args) throws TException {
                final GetTableTypes_result result = new GetTableTypes_result();
                result.success = iface.GetTableTypes(args.req);
                return result;
            }
        }
        
        public static class GetColumns<I extends Iface> extends ProcessFunction<I, GetColumns_args>
        {
            public GetColumns() {
                super("GetColumns");
            }
            
            @Override
            public GetColumns_args getEmptyArgsInstance() {
                return new GetColumns_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetColumns_result getResult(final I iface, final GetColumns_args args) throws TException {
                final GetColumns_result result = new GetColumns_result();
                result.success = iface.GetColumns(args.req);
                return result;
            }
        }
        
        public static class GetFunctions<I extends Iface> extends ProcessFunction<I, GetFunctions_args>
        {
            public GetFunctions() {
                super("GetFunctions");
            }
            
            @Override
            public GetFunctions_args getEmptyArgsInstance() {
                return new GetFunctions_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetFunctions_result getResult(final I iface, final GetFunctions_args args) throws TException {
                final GetFunctions_result result = new GetFunctions_result();
                result.success = iface.GetFunctions(args.req);
                return result;
            }
        }
        
        public static class GetOperationStatus<I extends Iface> extends ProcessFunction<I, GetOperationStatus_args>
        {
            public GetOperationStatus() {
                super("GetOperationStatus");
            }
            
            @Override
            public GetOperationStatus_args getEmptyArgsInstance() {
                return new GetOperationStatus_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetOperationStatus_result getResult(final I iface, final GetOperationStatus_args args) throws TException {
                final GetOperationStatus_result result = new GetOperationStatus_result();
                result.success = iface.GetOperationStatus(args.req);
                return result;
            }
        }
        
        public static class CancelOperation<I extends Iface> extends ProcessFunction<I, CancelOperation_args>
        {
            public CancelOperation() {
                super("CancelOperation");
            }
            
            @Override
            public CancelOperation_args getEmptyArgsInstance() {
                return new CancelOperation_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public CancelOperation_result getResult(final I iface, final CancelOperation_args args) throws TException {
                final CancelOperation_result result = new CancelOperation_result();
                result.success = iface.CancelOperation(args.req);
                return result;
            }
        }
        
        public static class CloseOperation<I extends Iface> extends ProcessFunction<I, CloseOperation_args>
        {
            public CloseOperation() {
                super("CloseOperation");
            }
            
            @Override
            public CloseOperation_args getEmptyArgsInstance() {
                return new CloseOperation_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public CloseOperation_result getResult(final I iface, final CloseOperation_args args) throws TException {
                final CloseOperation_result result = new CloseOperation_result();
                result.success = iface.CloseOperation(args.req);
                return result;
            }
        }
        
        public static class GetResultSetMetadata<I extends Iface> extends ProcessFunction<I, GetResultSetMetadata_args>
        {
            public GetResultSetMetadata() {
                super("GetResultSetMetadata");
            }
            
            @Override
            public GetResultSetMetadata_args getEmptyArgsInstance() {
                return new GetResultSetMetadata_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetResultSetMetadata_result getResult(final I iface, final GetResultSetMetadata_args args) throws TException {
                final GetResultSetMetadata_result result = new GetResultSetMetadata_result();
                result.success = iface.GetResultSetMetadata(args.req);
                return result;
            }
        }
        
        public static class FetchResults<I extends Iface> extends ProcessFunction<I, FetchResults_args>
        {
            public FetchResults() {
                super("FetchResults");
            }
            
            @Override
            public FetchResults_args getEmptyArgsInstance() {
                return new FetchResults_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public FetchResults_result getResult(final I iface, final FetchResults_args args) throws TException {
                final FetchResults_result result = new FetchResults_result();
                result.success = iface.FetchResults(args.req);
                return result;
            }
        }
        
        public static class GetDelegationToken<I extends Iface> extends ProcessFunction<I, GetDelegationToken_args>
        {
            public GetDelegationToken() {
                super("GetDelegationToken");
            }
            
            @Override
            public GetDelegationToken_args getEmptyArgsInstance() {
                return new GetDelegationToken_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public GetDelegationToken_result getResult(final I iface, final GetDelegationToken_args args) throws TException {
                final GetDelegationToken_result result = new GetDelegationToken_result();
                result.success = iface.GetDelegationToken(args.req);
                return result;
            }
        }
        
        public static class CancelDelegationToken<I extends Iface> extends ProcessFunction<I, CancelDelegationToken_args>
        {
            public CancelDelegationToken() {
                super("CancelDelegationToken");
            }
            
            @Override
            public CancelDelegationToken_args getEmptyArgsInstance() {
                return new CancelDelegationToken_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public CancelDelegationToken_result getResult(final I iface, final CancelDelegationToken_args args) throws TException {
                final CancelDelegationToken_result result = new CancelDelegationToken_result();
                result.success = iface.CancelDelegationToken(args.req);
                return result;
            }
        }
        
        public static class RenewDelegationToken<I extends Iface> extends ProcessFunction<I, RenewDelegationToken_args>
        {
            public RenewDelegationToken() {
                super("RenewDelegationToken");
            }
            
            @Override
            public RenewDelegationToken_args getEmptyArgsInstance() {
                return new RenewDelegationToken_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public RenewDelegationToken_result getResult(final I iface, final RenewDelegationToken_args args) throws TException {
                final RenewDelegationToken_result result = new RenewDelegationToken_result();
                result.success = iface.RenewDelegationToken(args.req);
                return result;
            }
        }
    }
    
    public static class OpenSession_args implements TBase<OpenSession_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TOpenSessionReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public OpenSession_args() {
        }
        
        public OpenSession_args(final TOpenSessionReq req) {
            this();
            this.req = req;
        }
        
        public OpenSession_args(final OpenSession_args other) {
            if (other.isSetReq()) {
                this.req = new TOpenSessionReq(other.req);
            }
        }
        
        @Override
        public OpenSession_args deepCopy() {
            return new OpenSession_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TOpenSessionReq getReq() {
            return this.req;
        }
        
        public void setReq(final TOpenSessionReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TOpenSessionReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof OpenSession_args && this.equals((OpenSession_args)that);
        }
        
        public boolean equals(final OpenSession_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final OpenSession_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final OpenSession_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            OpenSession_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            OpenSession_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("OpenSession_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("OpenSession_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new OpenSession_argsStandardSchemeFactory());
            OpenSession_args.schemes.put(TupleScheme.class, new OpenSession_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TOpenSessionReq.class)));
            FieldMetaData.addStructMetaDataMap(OpenSession_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class OpenSession_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public OpenSession_argsStandardScheme getScheme() {
                return new OpenSession_argsStandardScheme();
            }
        }
        
        private static class OpenSession_argsStandardScheme extends StandardScheme<OpenSession_args>
        {
            @Override
            public void read(final TProtocol iprot, final OpenSession_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TOpenSessionReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final OpenSession_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(OpenSession_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(OpenSession_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class OpenSession_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public OpenSession_argsTupleScheme getScheme() {
                return new OpenSession_argsTupleScheme();
            }
        }
        
        private static class OpenSession_argsTupleScheme extends TupleScheme<OpenSession_args>
        {
            @Override
            public void write(final TProtocol prot, final OpenSession_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final OpenSession_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TOpenSessionReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class OpenSession_result implements TBase<OpenSession_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TOpenSessionResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public OpenSession_result() {
        }
        
        public OpenSession_result(final TOpenSessionResp success) {
            this();
            this.success = success;
        }
        
        public OpenSession_result(final OpenSession_result other) {
            if (other.isSetSuccess()) {
                this.success = new TOpenSessionResp(other.success);
            }
        }
        
        @Override
        public OpenSession_result deepCopy() {
            return new OpenSession_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TOpenSessionResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TOpenSessionResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TOpenSessionResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof OpenSession_result && this.equals((OpenSession_result)that);
        }
        
        public boolean equals(final OpenSession_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final OpenSession_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final OpenSession_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            OpenSession_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            OpenSession_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("OpenSession_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("OpenSession_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new OpenSession_resultStandardSchemeFactory());
            OpenSession_result.schemes.put(TupleScheme.class, new OpenSession_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TOpenSessionResp.class)));
            FieldMetaData.addStructMetaDataMap(OpenSession_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class OpenSession_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public OpenSession_resultStandardScheme getScheme() {
                return new OpenSession_resultStandardScheme();
            }
        }
        
        private static class OpenSession_resultStandardScheme extends StandardScheme<OpenSession_result>
        {
            @Override
            public void read(final TProtocol iprot, final OpenSession_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TOpenSessionResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final OpenSession_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(OpenSession_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(OpenSession_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class OpenSession_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public OpenSession_resultTupleScheme getScheme() {
                return new OpenSession_resultTupleScheme();
            }
        }
        
        private static class OpenSession_resultTupleScheme extends TupleScheme<OpenSession_result>
        {
            @Override
            public void write(final TProtocol prot, final OpenSession_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final OpenSession_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TOpenSessionResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class CloseSession_args implements TBase<CloseSession_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCloseSessionReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CloseSession_args() {
        }
        
        public CloseSession_args(final TCloseSessionReq req) {
            this();
            this.req = req;
        }
        
        public CloseSession_args(final CloseSession_args other) {
            if (other.isSetReq()) {
                this.req = new TCloseSessionReq(other.req);
            }
        }
        
        @Override
        public CloseSession_args deepCopy() {
            return new CloseSession_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TCloseSessionReq getReq() {
            return this.req;
        }
        
        public void setReq(final TCloseSessionReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TCloseSessionReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CloseSession_args && this.equals((CloseSession_args)that);
        }
        
        public boolean equals(final CloseSession_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CloseSession_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CloseSession_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CloseSession_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CloseSession_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CloseSession_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CloseSession_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CloseSession_argsStandardSchemeFactory());
            CloseSession_args.schemes.put(TupleScheme.class, new CloseSession_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TCloseSessionReq.class)));
            FieldMetaData.addStructMetaDataMap(CloseSession_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CloseSession_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseSession_argsStandardScheme getScheme() {
                return new CloseSession_argsStandardScheme();
            }
        }
        
        private static class CloseSession_argsStandardScheme extends StandardScheme<CloseSession_args>
        {
            @Override
            public void read(final TProtocol iprot, final CloseSession_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TCloseSessionReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CloseSession_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CloseSession_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(CloseSession_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CloseSession_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseSession_argsTupleScheme getScheme() {
                return new CloseSession_argsTupleScheme();
            }
        }
        
        private static class CloseSession_argsTupleScheme extends TupleScheme<CloseSession_args>
        {
            @Override
            public void write(final TProtocol prot, final CloseSession_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CloseSession_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TCloseSessionReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class CloseSession_result implements TBase<CloseSession_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCloseSessionResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CloseSession_result() {
        }
        
        public CloseSession_result(final TCloseSessionResp success) {
            this();
            this.success = success;
        }
        
        public CloseSession_result(final CloseSession_result other) {
            if (other.isSetSuccess()) {
                this.success = new TCloseSessionResp(other.success);
            }
        }
        
        @Override
        public CloseSession_result deepCopy() {
            return new CloseSession_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TCloseSessionResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TCloseSessionResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TCloseSessionResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CloseSession_result && this.equals((CloseSession_result)that);
        }
        
        public boolean equals(final CloseSession_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CloseSession_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CloseSession_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CloseSession_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CloseSession_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CloseSession_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CloseSession_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CloseSession_resultStandardSchemeFactory());
            CloseSession_result.schemes.put(TupleScheme.class, new CloseSession_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TCloseSessionResp.class)));
            FieldMetaData.addStructMetaDataMap(CloseSession_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CloseSession_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseSession_resultStandardScheme getScheme() {
                return new CloseSession_resultStandardScheme();
            }
        }
        
        private static class CloseSession_resultStandardScheme extends StandardScheme<CloseSession_result>
        {
            @Override
            public void read(final TProtocol iprot, final CloseSession_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TCloseSessionResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CloseSession_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CloseSession_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(CloseSession_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CloseSession_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseSession_resultTupleScheme getScheme() {
                return new CloseSession_resultTupleScheme();
            }
        }
        
        private static class CloseSession_resultTupleScheme extends TupleScheme<CloseSession_result>
        {
            @Override
            public void write(final TProtocol prot, final CloseSession_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CloseSession_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TCloseSessionResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetInfo_args implements TBase<GetInfo_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetInfoReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetInfo_args() {
        }
        
        public GetInfo_args(final TGetInfoReq req) {
            this();
            this.req = req;
        }
        
        public GetInfo_args(final GetInfo_args other) {
            if (other.isSetReq()) {
                this.req = new TGetInfoReq(other.req);
            }
        }
        
        @Override
        public GetInfo_args deepCopy() {
            return new GetInfo_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetInfoReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetInfoReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetInfoReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetInfo_args && this.equals((GetInfo_args)that);
        }
        
        public boolean equals(final GetInfo_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetInfo_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetInfo_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetInfo_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetInfo_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetInfo_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetInfo_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetInfo_argsStandardSchemeFactory());
            GetInfo_args.schemes.put(TupleScheme.class, new GetInfo_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetInfoReq.class)));
            FieldMetaData.addStructMetaDataMap(GetInfo_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetInfo_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetInfo_argsStandardScheme getScheme() {
                return new GetInfo_argsStandardScheme();
            }
        }
        
        private static class GetInfo_argsStandardScheme extends StandardScheme<GetInfo_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetInfo_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetInfoReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetInfo_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetInfo_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetInfo_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetInfo_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetInfo_argsTupleScheme getScheme() {
                return new GetInfo_argsTupleScheme();
            }
        }
        
        private static class GetInfo_argsTupleScheme extends TupleScheme<GetInfo_args>
        {
            @Override
            public void write(final TProtocol prot, final GetInfo_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetInfo_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetInfoReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetInfo_result implements TBase<GetInfo_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetInfoResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetInfo_result() {
        }
        
        public GetInfo_result(final TGetInfoResp success) {
            this();
            this.success = success;
        }
        
        public GetInfo_result(final GetInfo_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetInfoResp(other.success);
            }
        }
        
        @Override
        public GetInfo_result deepCopy() {
            return new GetInfo_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetInfoResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetInfoResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetInfoResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetInfo_result && this.equals((GetInfo_result)that);
        }
        
        public boolean equals(final GetInfo_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetInfo_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetInfo_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetInfo_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetInfo_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetInfo_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetInfo_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetInfo_resultStandardSchemeFactory());
            GetInfo_result.schemes.put(TupleScheme.class, new GetInfo_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetInfoResp.class)));
            FieldMetaData.addStructMetaDataMap(GetInfo_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetInfo_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetInfo_resultStandardScheme getScheme() {
                return new GetInfo_resultStandardScheme();
            }
        }
        
        private static class GetInfo_resultStandardScheme extends StandardScheme<GetInfo_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetInfo_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetInfoResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetInfo_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetInfo_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetInfo_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetInfo_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetInfo_resultTupleScheme getScheme() {
                return new GetInfo_resultTupleScheme();
            }
        }
        
        private static class GetInfo_resultTupleScheme extends TupleScheme<GetInfo_result>
        {
            @Override
            public void write(final TProtocol prot, final GetInfo_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetInfo_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetInfoResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class ExecuteStatement_args implements TBase<ExecuteStatement_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TExecuteStatementReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public ExecuteStatement_args() {
        }
        
        public ExecuteStatement_args(final TExecuteStatementReq req) {
            this();
            this.req = req;
        }
        
        public ExecuteStatement_args(final ExecuteStatement_args other) {
            if (other.isSetReq()) {
                this.req = new TExecuteStatementReq(other.req);
            }
        }
        
        @Override
        public ExecuteStatement_args deepCopy() {
            return new ExecuteStatement_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TExecuteStatementReq getReq() {
            return this.req;
        }
        
        public void setReq(final TExecuteStatementReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TExecuteStatementReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof ExecuteStatement_args && this.equals((ExecuteStatement_args)that);
        }
        
        public boolean equals(final ExecuteStatement_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final ExecuteStatement_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final ExecuteStatement_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            ExecuteStatement_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            ExecuteStatement_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ExecuteStatement_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("ExecuteStatement_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ExecuteStatement_argsStandardSchemeFactory());
            ExecuteStatement_args.schemes.put(TupleScheme.class, new ExecuteStatement_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TExecuteStatementReq.class)));
            FieldMetaData.addStructMetaDataMap(ExecuteStatement_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class ExecuteStatement_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public ExecuteStatement_argsStandardScheme getScheme() {
                return new ExecuteStatement_argsStandardScheme();
            }
        }
        
        private static class ExecuteStatement_argsStandardScheme extends StandardScheme<ExecuteStatement_args>
        {
            @Override
            public void read(final TProtocol iprot, final ExecuteStatement_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TExecuteStatementReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final ExecuteStatement_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(ExecuteStatement_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(ExecuteStatement_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class ExecuteStatement_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public ExecuteStatement_argsTupleScheme getScheme() {
                return new ExecuteStatement_argsTupleScheme();
            }
        }
        
        private static class ExecuteStatement_argsTupleScheme extends TupleScheme<ExecuteStatement_args>
        {
            @Override
            public void write(final TProtocol prot, final ExecuteStatement_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final ExecuteStatement_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TExecuteStatementReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class ExecuteStatement_result implements TBase<ExecuteStatement_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TExecuteStatementResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public ExecuteStatement_result() {
        }
        
        public ExecuteStatement_result(final TExecuteStatementResp success) {
            this();
            this.success = success;
        }
        
        public ExecuteStatement_result(final ExecuteStatement_result other) {
            if (other.isSetSuccess()) {
                this.success = new TExecuteStatementResp(other.success);
            }
        }
        
        @Override
        public ExecuteStatement_result deepCopy() {
            return new ExecuteStatement_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TExecuteStatementResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TExecuteStatementResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TExecuteStatementResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof ExecuteStatement_result && this.equals((ExecuteStatement_result)that);
        }
        
        public boolean equals(final ExecuteStatement_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final ExecuteStatement_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final ExecuteStatement_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            ExecuteStatement_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            ExecuteStatement_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ExecuteStatement_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("ExecuteStatement_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ExecuteStatement_resultStandardSchemeFactory());
            ExecuteStatement_result.schemes.put(TupleScheme.class, new ExecuteStatement_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TExecuteStatementResp.class)));
            FieldMetaData.addStructMetaDataMap(ExecuteStatement_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class ExecuteStatement_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public ExecuteStatement_resultStandardScheme getScheme() {
                return new ExecuteStatement_resultStandardScheme();
            }
        }
        
        private static class ExecuteStatement_resultStandardScheme extends StandardScheme<ExecuteStatement_result>
        {
            @Override
            public void read(final TProtocol iprot, final ExecuteStatement_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TExecuteStatementResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final ExecuteStatement_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(ExecuteStatement_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(ExecuteStatement_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class ExecuteStatement_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public ExecuteStatement_resultTupleScheme getScheme() {
                return new ExecuteStatement_resultTupleScheme();
            }
        }
        
        private static class ExecuteStatement_resultTupleScheme extends TupleScheme<ExecuteStatement_result>
        {
            @Override
            public void write(final TProtocol prot, final ExecuteStatement_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final ExecuteStatement_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TExecuteStatementResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetTypeInfo_args implements TBase<GetTypeInfo_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetTypeInfoReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetTypeInfo_args() {
        }
        
        public GetTypeInfo_args(final TGetTypeInfoReq req) {
            this();
            this.req = req;
        }
        
        public GetTypeInfo_args(final GetTypeInfo_args other) {
            if (other.isSetReq()) {
                this.req = new TGetTypeInfoReq(other.req);
            }
        }
        
        @Override
        public GetTypeInfo_args deepCopy() {
            return new GetTypeInfo_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetTypeInfoReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetTypeInfoReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetTypeInfoReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetTypeInfo_args && this.equals((GetTypeInfo_args)that);
        }
        
        public boolean equals(final GetTypeInfo_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetTypeInfo_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetTypeInfo_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetTypeInfo_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetTypeInfo_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetTypeInfo_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetTypeInfo_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetTypeInfo_argsStandardSchemeFactory());
            GetTypeInfo_args.schemes.put(TupleScheme.class, new GetTypeInfo_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetTypeInfoReq.class)));
            FieldMetaData.addStructMetaDataMap(GetTypeInfo_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetTypeInfo_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTypeInfo_argsStandardScheme getScheme() {
                return new GetTypeInfo_argsStandardScheme();
            }
        }
        
        private static class GetTypeInfo_argsStandardScheme extends StandardScheme<GetTypeInfo_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetTypeInfo_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetTypeInfoReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetTypeInfo_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetTypeInfo_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetTypeInfo_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetTypeInfo_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTypeInfo_argsTupleScheme getScheme() {
                return new GetTypeInfo_argsTupleScheme();
            }
        }
        
        private static class GetTypeInfo_argsTupleScheme extends TupleScheme<GetTypeInfo_args>
        {
            @Override
            public void write(final TProtocol prot, final GetTypeInfo_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetTypeInfo_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetTypeInfoReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetTypeInfo_result implements TBase<GetTypeInfo_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetTypeInfoResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetTypeInfo_result() {
        }
        
        public GetTypeInfo_result(final TGetTypeInfoResp success) {
            this();
            this.success = success;
        }
        
        public GetTypeInfo_result(final GetTypeInfo_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetTypeInfoResp(other.success);
            }
        }
        
        @Override
        public GetTypeInfo_result deepCopy() {
            return new GetTypeInfo_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetTypeInfoResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetTypeInfoResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetTypeInfoResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetTypeInfo_result && this.equals((GetTypeInfo_result)that);
        }
        
        public boolean equals(final GetTypeInfo_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetTypeInfo_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetTypeInfo_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetTypeInfo_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetTypeInfo_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetTypeInfo_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetTypeInfo_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetTypeInfo_resultStandardSchemeFactory());
            GetTypeInfo_result.schemes.put(TupleScheme.class, new GetTypeInfo_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetTypeInfoResp.class)));
            FieldMetaData.addStructMetaDataMap(GetTypeInfo_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetTypeInfo_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTypeInfo_resultStandardScheme getScheme() {
                return new GetTypeInfo_resultStandardScheme();
            }
        }
        
        private static class GetTypeInfo_resultStandardScheme extends StandardScheme<GetTypeInfo_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetTypeInfo_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetTypeInfoResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetTypeInfo_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetTypeInfo_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetTypeInfo_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetTypeInfo_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTypeInfo_resultTupleScheme getScheme() {
                return new GetTypeInfo_resultTupleScheme();
            }
        }
        
        private static class GetTypeInfo_resultTupleScheme extends TupleScheme<GetTypeInfo_result>
        {
            @Override
            public void write(final TProtocol prot, final GetTypeInfo_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetTypeInfo_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetTypeInfoResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetCatalogs_args implements TBase<GetCatalogs_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetCatalogsReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetCatalogs_args() {
        }
        
        public GetCatalogs_args(final TGetCatalogsReq req) {
            this();
            this.req = req;
        }
        
        public GetCatalogs_args(final GetCatalogs_args other) {
            if (other.isSetReq()) {
                this.req = new TGetCatalogsReq(other.req);
            }
        }
        
        @Override
        public GetCatalogs_args deepCopy() {
            return new GetCatalogs_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetCatalogsReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetCatalogsReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetCatalogsReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetCatalogs_args && this.equals((GetCatalogs_args)that);
        }
        
        public boolean equals(final GetCatalogs_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetCatalogs_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetCatalogs_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetCatalogs_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetCatalogs_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetCatalogs_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetCatalogs_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetCatalogs_argsStandardSchemeFactory());
            GetCatalogs_args.schemes.put(TupleScheme.class, new GetCatalogs_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetCatalogsReq.class)));
            FieldMetaData.addStructMetaDataMap(GetCatalogs_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetCatalogs_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetCatalogs_argsStandardScheme getScheme() {
                return new GetCatalogs_argsStandardScheme();
            }
        }
        
        private static class GetCatalogs_argsStandardScheme extends StandardScheme<GetCatalogs_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetCatalogs_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetCatalogsReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetCatalogs_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetCatalogs_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetCatalogs_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetCatalogs_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetCatalogs_argsTupleScheme getScheme() {
                return new GetCatalogs_argsTupleScheme();
            }
        }
        
        private static class GetCatalogs_argsTupleScheme extends TupleScheme<GetCatalogs_args>
        {
            @Override
            public void write(final TProtocol prot, final GetCatalogs_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetCatalogs_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetCatalogsReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetCatalogs_result implements TBase<GetCatalogs_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetCatalogsResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetCatalogs_result() {
        }
        
        public GetCatalogs_result(final TGetCatalogsResp success) {
            this();
            this.success = success;
        }
        
        public GetCatalogs_result(final GetCatalogs_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetCatalogsResp(other.success);
            }
        }
        
        @Override
        public GetCatalogs_result deepCopy() {
            return new GetCatalogs_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetCatalogsResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetCatalogsResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetCatalogsResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetCatalogs_result && this.equals((GetCatalogs_result)that);
        }
        
        public boolean equals(final GetCatalogs_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetCatalogs_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetCatalogs_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetCatalogs_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetCatalogs_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetCatalogs_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetCatalogs_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetCatalogs_resultStandardSchemeFactory());
            GetCatalogs_result.schemes.put(TupleScheme.class, new GetCatalogs_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetCatalogsResp.class)));
            FieldMetaData.addStructMetaDataMap(GetCatalogs_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetCatalogs_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetCatalogs_resultStandardScheme getScheme() {
                return new GetCatalogs_resultStandardScheme();
            }
        }
        
        private static class GetCatalogs_resultStandardScheme extends StandardScheme<GetCatalogs_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetCatalogs_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetCatalogsResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetCatalogs_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetCatalogs_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetCatalogs_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetCatalogs_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetCatalogs_resultTupleScheme getScheme() {
                return new GetCatalogs_resultTupleScheme();
            }
        }
        
        private static class GetCatalogs_resultTupleScheme extends TupleScheme<GetCatalogs_result>
        {
            @Override
            public void write(final TProtocol prot, final GetCatalogs_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetCatalogs_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetCatalogsResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetSchemas_args implements TBase<GetSchemas_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetSchemasReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetSchemas_args() {
        }
        
        public GetSchemas_args(final TGetSchemasReq req) {
            this();
            this.req = req;
        }
        
        public GetSchemas_args(final GetSchemas_args other) {
            if (other.isSetReq()) {
                this.req = new TGetSchemasReq(other.req);
            }
        }
        
        @Override
        public GetSchemas_args deepCopy() {
            return new GetSchemas_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetSchemasReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetSchemasReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetSchemasReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetSchemas_args && this.equals((GetSchemas_args)that);
        }
        
        public boolean equals(final GetSchemas_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetSchemas_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetSchemas_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetSchemas_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetSchemas_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetSchemas_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetSchemas_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetSchemas_argsStandardSchemeFactory());
            GetSchemas_args.schemes.put(TupleScheme.class, new GetSchemas_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetSchemasReq.class)));
            FieldMetaData.addStructMetaDataMap(GetSchemas_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetSchemas_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetSchemas_argsStandardScheme getScheme() {
                return new GetSchemas_argsStandardScheme();
            }
        }
        
        private static class GetSchemas_argsStandardScheme extends StandardScheme<GetSchemas_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetSchemas_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetSchemasReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetSchemas_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetSchemas_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetSchemas_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetSchemas_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetSchemas_argsTupleScheme getScheme() {
                return new GetSchemas_argsTupleScheme();
            }
        }
        
        private static class GetSchemas_argsTupleScheme extends TupleScheme<GetSchemas_args>
        {
            @Override
            public void write(final TProtocol prot, final GetSchemas_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetSchemas_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetSchemasReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetSchemas_result implements TBase<GetSchemas_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetSchemasResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetSchemas_result() {
        }
        
        public GetSchemas_result(final TGetSchemasResp success) {
            this();
            this.success = success;
        }
        
        public GetSchemas_result(final GetSchemas_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetSchemasResp(other.success);
            }
        }
        
        @Override
        public GetSchemas_result deepCopy() {
            return new GetSchemas_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetSchemasResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetSchemasResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetSchemasResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetSchemas_result && this.equals((GetSchemas_result)that);
        }
        
        public boolean equals(final GetSchemas_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetSchemas_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetSchemas_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetSchemas_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetSchemas_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetSchemas_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetSchemas_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetSchemas_resultStandardSchemeFactory());
            GetSchemas_result.schemes.put(TupleScheme.class, new GetSchemas_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetSchemasResp.class)));
            FieldMetaData.addStructMetaDataMap(GetSchemas_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetSchemas_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetSchemas_resultStandardScheme getScheme() {
                return new GetSchemas_resultStandardScheme();
            }
        }
        
        private static class GetSchemas_resultStandardScheme extends StandardScheme<GetSchemas_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetSchemas_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetSchemasResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetSchemas_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetSchemas_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetSchemas_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetSchemas_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetSchemas_resultTupleScheme getScheme() {
                return new GetSchemas_resultTupleScheme();
            }
        }
        
        private static class GetSchemas_resultTupleScheme extends TupleScheme<GetSchemas_result>
        {
            @Override
            public void write(final TProtocol prot, final GetSchemas_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetSchemas_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetSchemasResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetTables_args implements TBase<GetTables_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetTablesReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetTables_args() {
        }
        
        public GetTables_args(final TGetTablesReq req) {
            this();
            this.req = req;
        }
        
        public GetTables_args(final GetTables_args other) {
            if (other.isSetReq()) {
                this.req = new TGetTablesReq(other.req);
            }
        }
        
        @Override
        public GetTables_args deepCopy() {
            return new GetTables_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetTablesReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetTablesReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetTablesReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetTables_args && this.equals((GetTables_args)that);
        }
        
        public boolean equals(final GetTables_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetTables_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetTables_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetTables_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetTables_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetTables_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetTables_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetTables_argsStandardSchemeFactory());
            GetTables_args.schemes.put(TupleScheme.class, new GetTables_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetTablesReq.class)));
            FieldMetaData.addStructMetaDataMap(GetTables_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetTables_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTables_argsStandardScheme getScheme() {
                return new GetTables_argsStandardScheme();
            }
        }
        
        private static class GetTables_argsStandardScheme extends StandardScheme<GetTables_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetTables_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetTablesReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetTables_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetTables_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetTables_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetTables_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTables_argsTupleScheme getScheme() {
                return new GetTables_argsTupleScheme();
            }
        }
        
        private static class GetTables_argsTupleScheme extends TupleScheme<GetTables_args>
        {
            @Override
            public void write(final TProtocol prot, final GetTables_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetTables_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetTablesReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetTables_result implements TBase<GetTables_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetTablesResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetTables_result() {
        }
        
        public GetTables_result(final TGetTablesResp success) {
            this();
            this.success = success;
        }
        
        public GetTables_result(final GetTables_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetTablesResp(other.success);
            }
        }
        
        @Override
        public GetTables_result deepCopy() {
            return new GetTables_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetTablesResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetTablesResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetTablesResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetTables_result && this.equals((GetTables_result)that);
        }
        
        public boolean equals(final GetTables_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetTables_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetTables_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetTables_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetTables_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetTables_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetTables_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetTables_resultStandardSchemeFactory());
            GetTables_result.schemes.put(TupleScheme.class, new GetTables_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetTablesResp.class)));
            FieldMetaData.addStructMetaDataMap(GetTables_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetTables_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTables_resultStandardScheme getScheme() {
                return new GetTables_resultStandardScheme();
            }
        }
        
        private static class GetTables_resultStandardScheme extends StandardScheme<GetTables_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetTables_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetTablesResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetTables_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetTables_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetTables_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetTables_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTables_resultTupleScheme getScheme() {
                return new GetTables_resultTupleScheme();
            }
        }
        
        private static class GetTables_resultTupleScheme extends TupleScheme<GetTables_result>
        {
            @Override
            public void write(final TProtocol prot, final GetTables_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetTables_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetTablesResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetTableTypes_args implements TBase<GetTableTypes_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetTableTypesReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetTableTypes_args() {
        }
        
        public GetTableTypes_args(final TGetTableTypesReq req) {
            this();
            this.req = req;
        }
        
        public GetTableTypes_args(final GetTableTypes_args other) {
            if (other.isSetReq()) {
                this.req = new TGetTableTypesReq(other.req);
            }
        }
        
        @Override
        public GetTableTypes_args deepCopy() {
            return new GetTableTypes_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetTableTypesReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetTableTypesReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetTableTypesReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetTableTypes_args && this.equals((GetTableTypes_args)that);
        }
        
        public boolean equals(final GetTableTypes_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetTableTypes_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetTableTypes_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetTableTypes_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetTableTypes_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetTableTypes_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetTableTypes_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetTableTypes_argsStandardSchemeFactory());
            GetTableTypes_args.schemes.put(TupleScheme.class, new GetTableTypes_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetTableTypesReq.class)));
            FieldMetaData.addStructMetaDataMap(GetTableTypes_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetTableTypes_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTableTypes_argsStandardScheme getScheme() {
                return new GetTableTypes_argsStandardScheme();
            }
        }
        
        private static class GetTableTypes_argsStandardScheme extends StandardScheme<GetTableTypes_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetTableTypes_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetTableTypesReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetTableTypes_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetTableTypes_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetTableTypes_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetTableTypes_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTableTypes_argsTupleScheme getScheme() {
                return new GetTableTypes_argsTupleScheme();
            }
        }
        
        private static class GetTableTypes_argsTupleScheme extends TupleScheme<GetTableTypes_args>
        {
            @Override
            public void write(final TProtocol prot, final GetTableTypes_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetTableTypes_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetTableTypesReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetTableTypes_result implements TBase<GetTableTypes_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetTableTypesResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetTableTypes_result() {
        }
        
        public GetTableTypes_result(final TGetTableTypesResp success) {
            this();
            this.success = success;
        }
        
        public GetTableTypes_result(final GetTableTypes_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetTableTypesResp(other.success);
            }
        }
        
        @Override
        public GetTableTypes_result deepCopy() {
            return new GetTableTypes_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetTableTypesResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetTableTypesResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetTableTypesResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetTableTypes_result && this.equals((GetTableTypes_result)that);
        }
        
        public boolean equals(final GetTableTypes_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetTableTypes_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetTableTypes_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetTableTypes_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetTableTypes_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetTableTypes_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetTableTypes_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetTableTypes_resultStandardSchemeFactory());
            GetTableTypes_result.schemes.put(TupleScheme.class, new GetTableTypes_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetTableTypesResp.class)));
            FieldMetaData.addStructMetaDataMap(GetTableTypes_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetTableTypes_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTableTypes_resultStandardScheme getScheme() {
                return new GetTableTypes_resultStandardScheme();
            }
        }
        
        private static class GetTableTypes_resultStandardScheme extends StandardScheme<GetTableTypes_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetTableTypes_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetTableTypesResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetTableTypes_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetTableTypes_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetTableTypes_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetTableTypes_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetTableTypes_resultTupleScheme getScheme() {
                return new GetTableTypes_resultTupleScheme();
            }
        }
        
        private static class GetTableTypes_resultTupleScheme extends TupleScheme<GetTableTypes_result>
        {
            @Override
            public void write(final TProtocol prot, final GetTableTypes_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetTableTypes_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetTableTypesResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetColumns_args implements TBase<GetColumns_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetColumnsReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetColumns_args() {
        }
        
        public GetColumns_args(final TGetColumnsReq req) {
            this();
            this.req = req;
        }
        
        public GetColumns_args(final GetColumns_args other) {
            if (other.isSetReq()) {
                this.req = new TGetColumnsReq(other.req);
            }
        }
        
        @Override
        public GetColumns_args deepCopy() {
            return new GetColumns_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetColumnsReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetColumnsReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetColumnsReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetColumns_args && this.equals((GetColumns_args)that);
        }
        
        public boolean equals(final GetColumns_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetColumns_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetColumns_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetColumns_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetColumns_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetColumns_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetColumns_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetColumns_argsStandardSchemeFactory());
            GetColumns_args.schemes.put(TupleScheme.class, new GetColumns_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetColumnsReq.class)));
            FieldMetaData.addStructMetaDataMap(GetColumns_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetColumns_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetColumns_argsStandardScheme getScheme() {
                return new GetColumns_argsStandardScheme();
            }
        }
        
        private static class GetColumns_argsStandardScheme extends StandardScheme<GetColumns_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetColumns_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetColumnsReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetColumns_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetColumns_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetColumns_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetColumns_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetColumns_argsTupleScheme getScheme() {
                return new GetColumns_argsTupleScheme();
            }
        }
        
        private static class GetColumns_argsTupleScheme extends TupleScheme<GetColumns_args>
        {
            @Override
            public void write(final TProtocol prot, final GetColumns_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetColumns_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetColumnsReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetColumns_result implements TBase<GetColumns_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetColumnsResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetColumns_result() {
        }
        
        public GetColumns_result(final TGetColumnsResp success) {
            this();
            this.success = success;
        }
        
        public GetColumns_result(final GetColumns_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetColumnsResp(other.success);
            }
        }
        
        @Override
        public GetColumns_result deepCopy() {
            return new GetColumns_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetColumnsResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetColumnsResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetColumnsResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetColumns_result && this.equals((GetColumns_result)that);
        }
        
        public boolean equals(final GetColumns_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetColumns_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetColumns_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetColumns_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetColumns_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetColumns_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetColumns_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetColumns_resultStandardSchemeFactory());
            GetColumns_result.schemes.put(TupleScheme.class, new GetColumns_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetColumnsResp.class)));
            FieldMetaData.addStructMetaDataMap(GetColumns_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetColumns_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetColumns_resultStandardScheme getScheme() {
                return new GetColumns_resultStandardScheme();
            }
        }
        
        private static class GetColumns_resultStandardScheme extends StandardScheme<GetColumns_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetColumns_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetColumnsResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetColumns_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetColumns_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetColumns_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetColumns_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetColumns_resultTupleScheme getScheme() {
                return new GetColumns_resultTupleScheme();
            }
        }
        
        private static class GetColumns_resultTupleScheme extends TupleScheme<GetColumns_result>
        {
            @Override
            public void write(final TProtocol prot, final GetColumns_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetColumns_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetColumnsResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetFunctions_args implements TBase<GetFunctions_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetFunctionsReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetFunctions_args() {
        }
        
        public GetFunctions_args(final TGetFunctionsReq req) {
            this();
            this.req = req;
        }
        
        public GetFunctions_args(final GetFunctions_args other) {
            if (other.isSetReq()) {
                this.req = new TGetFunctionsReq(other.req);
            }
        }
        
        @Override
        public GetFunctions_args deepCopy() {
            return new GetFunctions_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetFunctionsReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetFunctionsReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetFunctionsReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetFunctions_args && this.equals((GetFunctions_args)that);
        }
        
        public boolean equals(final GetFunctions_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetFunctions_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetFunctions_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetFunctions_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetFunctions_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetFunctions_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetFunctions_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetFunctions_argsStandardSchemeFactory());
            GetFunctions_args.schemes.put(TupleScheme.class, new GetFunctions_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetFunctionsReq.class)));
            FieldMetaData.addStructMetaDataMap(GetFunctions_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetFunctions_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetFunctions_argsStandardScheme getScheme() {
                return new GetFunctions_argsStandardScheme();
            }
        }
        
        private static class GetFunctions_argsStandardScheme extends StandardScheme<GetFunctions_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetFunctions_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetFunctionsReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetFunctions_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetFunctions_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetFunctions_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetFunctions_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetFunctions_argsTupleScheme getScheme() {
                return new GetFunctions_argsTupleScheme();
            }
        }
        
        private static class GetFunctions_argsTupleScheme extends TupleScheme<GetFunctions_args>
        {
            @Override
            public void write(final TProtocol prot, final GetFunctions_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetFunctions_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetFunctionsReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetFunctions_result implements TBase<GetFunctions_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetFunctionsResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetFunctions_result() {
        }
        
        public GetFunctions_result(final TGetFunctionsResp success) {
            this();
            this.success = success;
        }
        
        public GetFunctions_result(final GetFunctions_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetFunctionsResp(other.success);
            }
        }
        
        @Override
        public GetFunctions_result deepCopy() {
            return new GetFunctions_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetFunctionsResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetFunctionsResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetFunctionsResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetFunctions_result && this.equals((GetFunctions_result)that);
        }
        
        public boolean equals(final GetFunctions_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetFunctions_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetFunctions_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetFunctions_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetFunctions_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetFunctions_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetFunctions_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetFunctions_resultStandardSchemeFactory());
            GetFunctions_result.schemes.put(TupleScheme.class, new GetFunctions_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetFunctionsResp.class)));
            FieldMetaData.addStructMetaDataMap(GetFunctions_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetFunctions_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetFunctions_resultStandardScheme getScheme() {
                return new GetFunctions_resultStandardScheme();
            }
        }
        
        private static class GetFunctions_resultStandardScheme extends StandardScheme<GetFunctions_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetFunctions_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetFunctionsResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetFunctions_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetFunctions_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetFunctions_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetFunctions_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetFunctions_resultTupleScheme getScheme() {
                return new GetFunctions_resultTupleScheme();
            }
        }
        
        private static class GetFunctions_resultTupleScheme extends TupleScheme<GetFunctions_result>
        {
            @Override
            public void write(final TProtocol prot, final GetFunctions_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetFunctions_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetFunctionsResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetOperationStatus_args implements TBase<GetOperationStatus_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetOperationStatusReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetOperationStatus_args() {
        }
        
        public GetOperationStatus_args(final TGetOperationStatusReq req) {
            this();
            this.req = req;
        }
        
        public GetOperationStatus_args(final GetOperationStatus_args other) {
            if (other.isSetReq()) {
                this.req = new TGetOperationStatusReq(other.req);
            }
        }
        
        @Override
        public GetOperationStatus_args deepCopy() {
            return new GetOperationStatus_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetOperationStatusReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetOperationStatusReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetOperationStatusReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetOperationStatus_args && this.equals((GetOperationStatus_args)that);
        }
        
        public boolean equals(final GetOperationStatus_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetOperationStatus_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetOperationStatus_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetOperationStatus_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetOperationStatus_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetOperationStatus_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetOperationStatus_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetOperationStatus_argsStandardSchemeFactory());
            GetOperationStatus_args.schemes.put(TupleScheme.class, new GetOperationStatus_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetOperationStatusReq.class)));
            FieldMetaData.addStructMetaDataMap(GetOperationStatus_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetOperationStatus_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetOperationStatus_argsStandardScheme getScheme() {
                return new GetOperationStatus_argsStandardScheme();
            }
        }
        
        private static class GetOperationStatus_argsStandardScheme extends StandardScheme<GetOperationStatus_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetOperationStatus_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetOperationStatusReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetOperationStatus_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetOperationStatus_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetOperationStatus_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetOperationStatus_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetOperationStatus_argsTupleScheme getScheme() {
                return new GetOperationStatus_argsTupleScheme();
            }
        }
        
        private static class GetOperationStatus_argsTupleScheme extends TupleScheme<GetOperationStatus_args>
        {
            @Override
            public void write(final TProtocol prot, final GetOperationStatus_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetOperationStatus_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetOperationStatusReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetOperationStatus_result implements TBase<GetOperationStatus_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetOperationStatusResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetOperationStatus_result() {
        }
        
        public GetOperationStatus_result(final TGetOperationStatusResp success) {
            this();
            this.success = success;
        }
        
        public GetOperationStatus_result(final GetOperationStatus_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetOperationStatusResp(other.success);
            }
        }
        
        @Override
        public GetOperationStatus_result deepCopy() {
            return new GetOperationStatus_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetOperationStatusResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetOperationStatusResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetOperationStatusResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetOperationStatus_result && this.equals((GetOperationStatus_result)that);
        }
        
        public boolean equals(final GetOperationStatus_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetOperationStatus_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetOperationStatus_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetOperationStatus_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetOperationStatus_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetOperationStatus_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetOperationStatus_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetOperationStatus_resultStandardSchemeFactory());
            GetOperationStatus_result.schemes.put(TupleScheme.class, new GetOperationStatus_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetOperationStatusResp.class)));
            FieldMetaData.addStructMetaDataMap(GetOperationStatus_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetOperationStatus_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetOperationStatus_resultStandardScheme getScheme() {
                return new GetOperationStatus_resultStandardScheme();
            }
        }
        
        private static class GetOperationStatus_resultStandardScheme extends StandardScheme<GetOperationStatus_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetOperationStatus_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetOperationStatusResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetOperationStatus_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetOperationStatus_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetOperationStatus_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetOperationStatus_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetOperationStatus_resultTupleScheme getScheme() {
                return new GetOperationStatus_resultTupleScheme();
            }
        }
        
        private static class GetOperationStatus_resultTupleScheme extends TupleScheme<GetOperationStatus_result>
        {
            @Override
            public void write(final TProtocol prot, final GetOperationStatus_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetOperationStatus_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetOperationStatusResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class CancelOperation_args implements TBase<CancelOperation_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCancelOperationReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CancelOperation_args() {
        }
        
        public CancelOperation_args(final TCancelOperationReq req) {
            this();
            this.req = req;
        }
        
        public CancelOperation_args(final CancelOperation_args other) {
            if (other.isSetReq()) {
                this.req = new TCancelOperationReq(other.req);
            }
        }
        
        @Override
        public CancelOperation_args deepCopy() {
            return new CancelOperation_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TCancelOperationReq getReq() {
            return this.req;
        }
        
        public void setReq(final TCancelOperationReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TCancelOperationReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CancelOperation_args && this.equals((CancelOperation_args)that);
        }
        
        public boolean equals(final CancelOperation_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CancelOperation_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CancelOperation_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CancelOperation_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CancelOperation_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CancelOperation_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CancelOperation_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CancelOperation_argsStandardSchemeFactory());
            CancelOperation_args.schemes.put(TupleScheme.class, new CancelOperation_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TCancelOperationReq.class)));
            FieldMetaData.addStructMetaDataMap(CancelOperation_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CancelOperation_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelOperation_argsStandardScheme getScheme() {
                return new CancelOperation_argsStandardScheme();
            }
        }
        
        private static class CancelOperation_argsStandardScheme extends StandardScheme<CancelOperation_args>
        {
            @Override
            public void read(final TProtocol iprot, final CancelOperation_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TCancelOperationReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CancelOperation_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CancelOperation_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(CancelOperation_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CancelOperation_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelOperation_argsTupleScheme getScheme() {
                return new CancelOperation_argsTupleScheme();
            }
        }
        
        private static class CancelOperation_argsTupleScheme extends TupleScheme<CancelOperation_args>
        {
            @Override
            public void write(final TProtocol prot, final CancelOperation_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CancelOperation_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TCancelOperationReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class CancelOperation_result implements TBase<CancelOperation_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCancelOperationResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CancelOperation_result() {
        }
        
        public CancelOperation_result(final TCancelOperationResp success) {
            this();
            this.success = success;
        }
        
        public CancelOperation_result(final CancelOperation_result other) {
            if (other.isSetSuccess()) {
                this.success = new TCancelOperationResp(other.success);
            }
        }
        
        @Override
        public CancelOperation_result deepCopy() {
            return new CancelOperation_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TCancelOperationResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TCancelOperationResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TCancelOperationResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CancelOperation_result && this.equals((CancelOperation_result)that);
        }
        
        public boolean equals(final CancelOperation_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CancelOperation_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CancelOperation_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CancelOperation_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CancelOperation_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CancelOperation_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CancelOperation_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CancelOperation_resultStandardSchemeFactory());
            CancelOperation_result.schemes.put(TupleScheme.class, new CancelOperation_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TCancelOperationResp.class)));
            FieldMetaData.addStructMetaDataMap(CancelOperation_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CancelOperation_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelOperation_resultStandardScheme getScheme() {
                return new CancelOperation_resultStandardScheme();
            }
        }
        
        private static class CancelOperation_resultStandardScheme extends StandardScheme<CancelOperation_result>
        {
            @Override
            public void read(final TProtocol iprot, final CancelOperation_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TCancelOperationResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CancelOperation_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CancelOperation_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(CancelOperation_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CancelOperation_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelOperation_resultTupleScheme getScheme() {
                return new CancelOperation_resultTupleScheme();
            }
        }
        
        private static class CancelOperation_resultTupleScheme extends TupleScheme<CancelOperation_result>
        {
            @Override
            public void write(final TProtocol prot, final CancelOperation_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CancelOperation_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TCancelOperationResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class CloseOperation_args implements TBase<CloseOperation_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCloseOperationReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CloseOperation_args() {
        }
        
        public CloseOperation_args(final TCloseOperationReq req) {
            this();
            this.req = req;
        }
        
        public CloseOperation_args(final CloseOperation_args other) {
            if (other.isSetReq()) {
                this.req = new TCloseOperationReq(other.req);
            }
        }
        
        @Override
        public CloseOperation_args deepCopy() {
            return new CloseOperation_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TCloseOperationReq getReq() {
            return this.req;
        }
        
        public void setReq(final TCloseOperationReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TCloseOperationReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CloseOperation_args && this.equals((CloseOperation_args)that);
        }
        
        public boolean equals(final CloseOperation_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CloseOperation_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CloseOperation_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CloseOperation_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CloseOperation_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CloseOperation_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CloseOperation_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CloseOperation_argsStandardSchemeFactory());
            CloseOperation_args.schemes.put(TupleScheme.class, new CloseOperation_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TCloseOperationReq.class)));
            FieldMetaData.addStructMetaDataMap(CloseOperation_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CloseOperation_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseOperation_argsStandardScheme getScheme() {
                return new CloseOperation_argsStandardScheme();
            }
        }
        
        private static class CloseOperation_argsStandardScheme extends StandardScheme<CloseOperation_args>
        {
            @Override
            public void read(final TProtocol iprot, final CloseOperation_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TCloseOperationReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CloseOperation_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CloseOperation_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(CloseOperation_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CloseOperation_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseOperation_argsTupleScheme getScheme() {
                return new CloseOperation_argsTupleScheme();
            }
        }
        
        private static class CloseOperation_argsTupleScheme extends TupleScheme<CloseOperation_args>
        {
            @Override
            public void write(final TProtocol prot, final CloseOperation_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CloseOperation_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TCloseOperationReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class CloseOperation_result implements TBase<CloseOperation_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCloseOperationResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CloseOperation_result() {
        }
        
        public CloseOperation_result(final TCloseOperationResp success) {
            this();
            this.success = success;
        }
        
        public CloseOperation_result(final CloseOperation_result other) {
            if (other.isSetSuccess()) {
                this.success = new TCloseOperationResp(other.success);
            }
        }
        
        @Override
        public CloseOperation_result deepCopy() {
            return new CloseOperation_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TCloseOperationResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TCloseOperationResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TCloseOperationResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CloseOperation_result && this.equals((CloseOperation_result)that);
        }
        
        public boolean equals(final CloseOperation_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CloseOperation_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CloseOperation_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CloseOperation_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CloseOperation_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CloseOperation_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CloseOperation_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CloseOperation_resultStandardSchemeFactory());
            CloseOperation_result.schemes.put(TupleScheme.class, new CloseOperation_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TCloseOperationResp.class)));
            FieldMetaData.addStructMetaDataMap(CloseOperation_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CloseOperation_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseOperation_resultStandardScheme getScheme() {
                return new CloseOperation_resultStandardScheme();
            }
        }
        
        private static class CloseOperation_resultStandardScheme extends StandardScheme<CloseOperation_result>
        {
            @Override
            public void read(final TProtocol iprot, final CloseOperation_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TCloseOperationResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CloseOperation_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CloseOperation_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(CloseOperation_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CloseOperation_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CloseOperation_resultTupleScheme getScheme() {
                return new CloseOperation_resultTupleScheme();
            }
        }
        
        private static class CloseOperation_resultTupleScheme extends TupleScheme<CloseOperation_result>
        {
            @Override
            public void write(final TProtocol prot, final CloseOperation_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CloseOperation_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TCloseOperationResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetResultSetMetadata_args implements TBase<GetResultSetMetadata_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetResultSetMetadataReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetResultSetMetadata_args() {
        }
        
        public GetResultSetMetadata_args(final TGetResultSetMetadataReq req) {
            this();
            this.req = req;
        }
        
        public GetResultSetMetadata_args(final GetResultSetMetadata_args other) {
            if (other.isSetReq()) {
                this.req = new TGetResultSetMetadataReq(other.req);
            }
        }
        
        @Override
        public GetResultSetMetadata_args deepCopy() {
            return new GetResultSetMetadata_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetResultSetMetadataReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetResultSetMetadataReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetResultSetMetadataReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetResultSetMetadata_args && this.equals((GetResultSetMetadata_args)that);
        }
        
        public boolean equals(final GetResultSetMetadata_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetResultSetMetadata_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetResultSetMetadata_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetResultSetMetadata_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetResultSetMetadata_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetResultSetMetadata_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetResultSetMetadata_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetResultSetMetadata_argsStandardSchemeFactory());
            GetResultSetMetadata_args.schemes.put(TupleScheme.class, new GetResultSetMetadata_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetResultSetMetadataReq.class)));
            FieldMetaData.addStructMetaDataMap(GetResultSetMetadata_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetResultSetMetadata_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetResultSetMetadata_argsStandardScheme getScheme() {
                return new GetResultSetMetadata_argsStandardScheme();
            }
        }
        
        private static class GetResultSetMetadata_argsStandardScheme extends StandardScheme<GetResultSetMetadata_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetResultSetMetadata_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetResultSetMetadataReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetResultSetMetadata_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetResultSetMetadata_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetResultSetMetadata_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetResultSetMetadata_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetResultSetMetadata_argsTupleScheme getScheme() {
                return new GetResultSetMetadata_argsTupleScheme();
            }
        }
        
        private static class GetResultSetMetadata_argsTupleScheme extends TupleScheme<GetResultSetMetadata_args>
        {
            @Override
            public void write(final TProtocol prot, final GetResultSetMetadata_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetResultSetMetadata_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetResultSetMetadataReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetResultSetMetadata_result implements TBase<GetResultSetMetadata_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetResultSetMetadataResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetResultSetMetadata_result() {
        }
        
        public GetResultSetMetadata_result(final TGetResultSetMetadataResp success) {
            this();
            this.success = success;
        }
        
        public GetResultSetMetadata_result(final GetResultSetMetadata_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetResultSetMetadataResp(other.success);
            }
        }
        
        @Override
        public GetResultSetMetadata_result deepCopy() {
            return new GetResultSetMetadata_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetResultSetMetadataResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetResultSetMetadataResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetResultSetMetadataResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetResultSetMetadata_result && this.equals((GetResultSetMetadata_result)that);
        }
        
        public boolean equals(final GetResultSetMetadata_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetResultSetMetadata_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetResultSetMetadata_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetResultSetMetadata_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetResultSetMetadata_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetResultSetMetadata_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetResultSetMetadata_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetResultSetMetadata_resultStandardSchemeFactory());
            GetResultSetMetadata_result.schemes.put(TupleScheme.class, new GetResultSetMetadata_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetResultSetMetadataResp.class)));
            FieldMetaData.addStructMetaDataMap(GetResultSetMetadata_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetResultSetMetadata_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetResultSetMetadata_resultStandardScheme getScheme() {
                return new GetResultSetMetadata_resultStandardScheme();
            }
        }
        
        private static class GetResultSetMetadata_resultStandardScheme extends StandardScheme<GetResultSetMetadata_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetResultSetMetadata_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetResultSetMetadataResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetResultSetMetadata_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetResultSetMetadata_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetResultSetMetadata_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetResultSetMetadata_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetResultSetMetadata_resultTupleScheme getScheme() {
                return new GetResultSetMetadata_resultTupleScheme();
            }
        }
        
        private static class GetResultSetMetadata_resultTupleScheme extends TupleScheme<GetResultSetMetadata_result>
        {
            @Override
            public void write(final TProtocol prot, final GetResultSetMetadata_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetResultSetMetadata_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetResultSetMetadataResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class FetchResults_args implements TBase<FetchResults_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TFetchResultsReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public FetchResults_args() {
        }
        
        public FetchResults_args(final TFetchResultsReq req) {
            this();
            this.req = req;
        }
        
        public FetchResults_args(final FetchResults_args other) {
            if (other.isSetReq()) {
                this.req = new TFetchResultsReq(other.req);
            }
        }
        
        @Override
        public FetchResults_args deepCopy() {
            return new FetchResults_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TFetchResultsReq getReq() {
            return this.req;
        }
        
        public void setReq(final TFetchResultsReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TFetchResultsReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof FetchResults_args && this.equals((FetchResults_args)that);
        }
        
        public boolean equals(final FetchResults_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final FetchResults_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final FetchResults_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            FetchResults_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            FetchResults_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FetchResults_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("FetchResults_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new FetchResults_argsStandardSchemeFactory());
            FetchResults_args.schemes.put(TupleScheme.class, new FetchResults_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TFetchResultsReq.class)));
            FieldMetaData.addStructMetaDataMap(FetchResults_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class FetchResults_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public FetchResults_argsStandardScheme getScheme() {
                return new FetchResults_argsStandardScheme();
            }
        }
        
        private static class FetchResults_argsStandardScheme extends StandardScheme<FetchResults_args>
        {
            @Override
            public void read(final TProtocol iprot, final FetchResults_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TFetchResultsReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final FetchResults_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(FetchResults_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(FetchResults_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class FetchResults_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public FetchResults_argsTupleScheme getScheme() {
                return new FetchResults_argsTupleScheme();
            }
        }
        
        private static class FetchResults_argsTupleScheme extends TupleScheme<FetchResults_args>
        {
            @Override
            public void write(final TProtocol prot, final FetchResults_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final FetchResults_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TFetchResultsReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class FetchResults_result implements TBase<FetchResults_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TFetchResultsResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public FetchResults_result() {
        }
        
        public FetchResults_result(final TFetchResultsResp success) {
            this();
            this.success = success;
        }
        
        public FetchResults_result(final FetchResults_result other) {
            if (other.isSetSuccess()) {
                this.success = new TFetchResultsResp(other.success);
            }
        }
        
        @Override
        public FetchResults_result deepCopy() {
            return new FetchResults_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TFetchResultsResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TFetchResultsResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TFetchResultsResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof FetchResults_result && this.equals((FetchResults_result)that);
        }
        
        public boolean equals(final FetchResults_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final FetchResults_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final FetchResults_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            FetchResults_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            FetchResults_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FetchResults_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("FetchResults_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new FetchResults_resultStandardSchemeFactory());
            FetchResults_result.schemes.put(TupleScheme.class, new FetchResults_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TFetchResultsResp.class)));
            FieldMetaData.addStructMetaDataMap(FetchResults_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class FetchResults_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public FetchResults_resultStandardScheme getScheme() {
                return new FetchResults_resultStandardScheme();
            }
        }
        
        private static class FetchResults_resultStandardScheme extends StandardScheme<FetchResults_result>
        {
            @Override
            public void read(final TProtocol iprot, final FetchResults_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TFetchResultsResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final FetchResults_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(FetchResults_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(FetchResults_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class FetchResults_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public FetchResults_resultTupleScheme getScheme() {
                return new FetchResults_resultTupleScheme();
            }
        }
        
        private static class FetchResults_resultTupleScheme extends TupleScheme<FetchResults_result>
        {
            @Override
            public void write(final TProtocol prot, final FetchResults_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final FetchResults_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TFetchResultsResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class GetDelegationToken_args implements TBase<GetDelegationToken_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetDelegationTokenReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetDelegationToken_args() {
        }
        
        public GetDelegationToken_args(final TGetDelegationTokenReq req) {
            this();
            this.req = req;
        }
        
        public GetDelegationToken_args(final GetDelegationToken_args other) {
            if (other.isSetReq()) {
                this.req = new TGetDelegationTokenReq(other.req);
            }
        }
        
        @Override
        public GetDelegationToken_args deepCopy() {
            return new GetDelegationToken_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TGetDelegationTokenReq getReq() {
            return this.req;
        }
        
        public void setReq(final TGetDelegationTokenReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TGetDelegationTokenReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetDelegationToken_args && this.equals((GetDelegationToken_args)that);
        }
        
        public boolean equals(final GetDelegationToken_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetDelegationToken_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetDelegationToken_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetDelegationToken_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetDelegationToken_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetDelegationToken_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetDelegationToken_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetDelegationToken_argsStandardSchemeFactory());
            GetDelegationToken_args.schemes.put(TupleScheme.class, new GetDelegationToken_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TGetDelegationTokenReq.class)));
            FieldMetaData.addStructMetaDataMap(GetDelegationToken_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetDelegationToken_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetDelegationToken_argsStandardScheme getScheme() {
                return new GetDelegationToken_argsStandardScheme();
            }
        }
        
        private static class GetDelegationToken_argsStandardScheme extends StandardScheme<GetDelegationToken_args>
        {
            @Override
            public void read(final TProtocol iprot, final GetDelegationToken_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TGetDelegationTokenReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetDelegationToken_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetDelegationToken_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(GetDelegationToken_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetDelegationToken_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetDelegationToken_argsTupleScheme getScheme() {
                return new GetDelegationToken_argsTupleScheme();
            }
        }
        
        private static class GetDelegationToken_argsTupleScheme extends TupleScheme<GetDelegationToken_args>
        {
            @Override
            public void write(final TProtocol prot, final GetDelegationToken_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetDelegationToken_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TGetDelegationTokenReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class GetDelegationToken_result implements TBase<GetDelegationToken_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TGetDelegationTokenResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public GetDelegationToken_result() {
        }
        
        public GetDelegationToken_result(final TGetDelegationTokenResp success) {
            this();
            this.success = success;
        }
        
        public GetDelegationToken_result(final GetDelegationToken_result other) {
            if (other.isSetSuccess()) {
                this.success = new TGetDelegationTokenResp(other.success);
            }
        }
        
        @Override
        public GetDelegationToken_result deepCopy() {
            return new GetDelegationToken_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TGetDelegationTokenResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TGetDelegationTokenResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TGetDelegationTokenResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof GetDelegationToken_result && this.equals((GetDelegationToken_result)that);
        }
        
        public boolean equals(final GetDelegationToken_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final GetDelegationToken_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final GetDelegationToken_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            GetDelegationToken_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            GetDelegationToken_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GetDelegationToken_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("GetDelegationToken_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetDelegationToken_resultStandardSchemeFactory());
            GetDelegationToken_result.schemes.put(TupleScheme.class, new GetDelegationToken_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TGetDelegationTokenResp.class)));
            FieldMetaData.addStructMetaDataMap(GetDelegationToken_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class GetDelegationToken_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public GetDelegationToken_resultStandardScheme getScheme() {
                return new GetDelegationToken_resultStandardScheme();
            }
        }
        
        private static class GetDelegationToken_resultStandardScheme extends StandardScheme<GetDelegationToken_result>
        {
            @Override
            public void read(final TProtocol iprot, final GetDelegationToken_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TGetDelegationTokenResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final GetDelegationToken_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(GetDelegationToken_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(GetDelegationToken_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class GetDelegationToken_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public GetDelegationToken_resultTupleScheme getScheme() {
                return new GetDelegationToken_resultTupleScheme();
            }
        }
        
        private static class GetDelegationToken_resultTupleScheme extends TupleScheme<GetDelegationToken_result>
        {
            @Override
            public void write(final TProtocol prot, final GetDelegationToken_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final GetDelegationToken_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TGetDelegationTokenResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class CancelDelegationToken_args implements TBase<CancelDelegationToken_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCancelDelegationTokenReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CancelDelegationToken_args() {
        }
        
        public CancelDelegationToken_args(final TCancelDelegationTokenReq req) {
            this();
            this.req = req;
        }
        
        public CancelDelegationToken_args(final CancelDelegationToken_args other) {
            if (other.isSetReq()) {
                this.req = new TCancelDelegationTokenReq(other.req);
            }
        }
        
        @Override
        public CancelDelegationToken_args deepCopy() {
            return new CancelDelegationToken_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TCancelDelegationTokenReq getReq() {
            return this.req;
        }
        
        public void setReq(final TCancelDelegationTokenReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TCancelDelegationTokenReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CancelDelegationToken_args && this.equals((CancelDelegationToken_args)that);
        }
        
        public boolean equals(final CancelDelegationToken_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CancelDelegationToken_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CancelDelegationToken_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CancelDelegationToken_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CancelDelegationToken_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CancelDelegationToken_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CancelDelegationToken_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CancelDelegationToken_argsStandardSchemeFactory());
            CancelDelegationToken_args.schemes.put(TupleScheme.class, new CancelDelegationToken_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TCancelDelegationTokenReq.class)));
            FieldMetaData.addStructMetaDataMap(CancelDelegationToken_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CancelDelegationToken_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelDelegationToken_argsStandardScheme getScheme() {
                return new CancelDelegationToken_argsStandardScheme();
            }
        }
        
        private static class CancelDelegationToken_argsStandardScheme extends StandardScheme<CancelDelegationToken_args>
        {
            @Override
            public void read(final TProtocol iprot, final CancelDelegationToken_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TCancelDelegationTokenReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CancelDelegationToken_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CancelDelegationToken_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(CancelDelegationToken_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CancelDelegationToken_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelDelegationToken_argsTupleScheme getScheme() {
                return new CancelDelegationToken_argsTupleScheme();
            }
        }
        
        private static class CancelDelegationToken_argsTupleScheme extends TupleScheme<CancelDelegationToken_args>
        {
            @Override
            public void write(final TProtocol prot, final CancelDelegationToken_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CancelDelegationToken_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TCancelDelegationTokenReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class CancelDelegationToken_result implements TBase<CancelDelegationToken_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TCancelDelegationTokenResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public CancelDelegationToken_result() {
        }
        
        public CancelDelegationToken_result(final TCancelDelegationTokenResp success) {
            this();
            this.success = success;
        }
        
        public CancelDelegationToken_result(final CancelDelegationToken_result other) {
            if (other.isSetSuccess()) {
                this.success = new TCancelDelegationTokenResp(other.success);
            }
        }
        
        @Override
        public CancelDelegationToken_result deepCopy() {
            return new CancelDelegationToken_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TCancelDelegationTokenResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TCancelDelegationTokenResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TCancelDelegationTokenResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof CancelDelegationToken_result && this.equals((CancelDelegationToken_result)that);
        }
        
        public boolean equals(final CancelDelegationToken_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final CancelDelegationToken_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final CancelDelegationToken_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            CancelDelegationToken_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            CancelDelegationToken_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CancelDelegationToken_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("CancelDelegationToken_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CancelDelegationToken_resultStandardSchemeFactory());
            CancelDelegationToken_result.schemes.put(TupleScheme.class, new CancelDelegationToken_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TCancelDelegationTokenResp.class)));
            FieldMetaData.addStructMetaDataMap(CancelDelegationToken_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class CancelDelegationToken_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelDelegationToken_resultStandardScheme getScheme() {
                return new CancelDelegationToken_resultStandardScheme();
            }
        }
        
        private static class CancelDelegationToken_resultStandardScheme extends StandardScheme<CancelDelegationToken_result>
        {
            @Override
            public void read(final TProtocol iprot, final CancelDelegationToken_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TCancelDelegationTokenResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final CancelDelegationToken_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(CancelDelegationToken_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(CancelDelegationToken_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class CancelDelegationToken_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public CancelDelegationToken_resultTupleScheme getScheme() {
                return new CancelDelegationToken_resultTupleScheme();
            }
        }
        
        private static class CancelDelegationToken_resultTupleScheme extends TupleScheme<CancelDelegationToken_result>
        {
            @Override
            public void write(final TProtocol prot, final CancelDelegationToken_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final CancelDelegationToken_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TCancelDelegationTokenResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class RenewDelegationToken_args implements TBase<RenewDelegationToken_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField REQ_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TRenewDelegationTokenReq req;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public RenewDelegationToken_args() {
        }
        
        public RenewDelegationToken_args(final TRenewDelegationTokenReq req) {
            this();
            this.req = req;
        }
        
        public RenewDelegationToken_args(final RenewDelegationToken_args other) {
            if (other.isSetReq()) {
                this.req = new TRenewDelegationTokenReq(other.req);
            }
        }
        
        @Override
        public RenewDelegationToken_args deepCopy() {
            return new RenewDelegationToken_args(this);
        }
        
        @Override
        public void clear() {
            this.req = null;
        }
        
        public TRenewDelegationTokenReq getReq() {
            return this.req;
        }
        
        public void setReq(final TRenewDelegationTokenReq req) {
            this.req = req;
        }
        
        public void unsetReq() {
            this.req = null;
        }
        
        public boolean isSetReq() {
            return this.req != null;
        }
        
        public void setReqIsSet(final boolean value) {
            if (!value) {
                this.req = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case REQ: {
                    if (value == null) {
                        this.unsetReq();
                        break;
                    }
                    this.setReq((TRenewDelegationTokenReq)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case REQ: {
                    return this.getReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case REQ: {
                    return this.isSetReq();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof RenewDelegationToken_args && this.equals((RenewDelegationToken_args)that);
        }
        
        public boolean equals(final RenewDelegationToken_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_req = this.isSetReq();
            final boolean that_present_req = that.isSetReq();
            if (this_present_req || that_present_req) {
                if (!this_present_req || !that_present_req) {
                    return false;
                }
                if (!this.req.equals(that.req)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_req = this.isSetReq();
            builder.append(present_req);
            if (present_req) {
                builder.append(this.req);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final RenewDelegationToken_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final RenewDelegationToken_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetReq()).compareTo(Boolean.valueOf(typedOther.isSetReq()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetReq()) {
                lastComparison = TBaseHelper.compareTo(this.req, typedOther.req);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            RenewDelegationToken_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            RenewDelegationToken_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RenewDelegationToken_args(");
            boolean first = true;
            sb.append("req:");
            if (this.req == null) {
                sb.append("null");
            }
            else {
                sb.append(this.req);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.req != null) {
                this.req.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("RenewDelegationToken_args");
            REQ_FIELD_DESC = new TField("req", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new RenewDelegationToken_argsStandardSchemeFactory());
            RenewDelegationToken_args.schemes.put(TupleScheme.class, new RenewDelegationToken_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.REQ, new FieldMetaData("req", (byte)3, new StructMetaData((byte)12, TRenewDelegationTokenReq.class)));
            FieldMetaData.addStructMetaDataMap(RenewDelegationToken_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            REQ((short)1, "req");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.REQ;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class RenewDelegationToken_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public RenewDelegationToken_argsStandardScheme getScheme() {
                return new RenewDelegationToken_argsStandardScheme();
            }
        }
        
        private static class RenewDelegationToken_argsStandardScheme extends StandardScheme<RenewDelegationToken_args>
        {
            @Override
            public void read(final TProtocol iprot, final RenewDelegationToken_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.req = new TRenewDelegationTokenReq();
                                struct.req.read(iprot);
                                struct.setReqIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final RenewDelegationToken_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(RenewDelegationToken_args.STRUCT_DESC);
                if (struct.req != null) {
                    oprot.writeFieldBegin(RenewDelegationToken_args.REQ_FIELD_DESC);
                    struct.req.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class RenewDelegationToken_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public RenewDelegationToken_argsTupleScheme getScheme() {
                return new RenewDelegationToken_argsTupleScheme();
            }
        }
        
        private static class RenewDelegationToken_argsTupleScheme extends TupleScheme<RenewDelegationToken_args>
        {
            @Override
            public void write(final TProtocol prot, final RenewDelegationToken_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetReq()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetReq()) {
                    struct.req.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final RenewDelegationToken_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.req = new TRenewDelegationTokenReq();
                    struct.req.read(iprot);
                    struct.setReqIsSet(true);
                }
            }
        }
    }
    
    public static class RenewDelegationToken_result implements TBase<RenewDelegationToken_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private TRenewDelegationTokenResp success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public RenewDelegationToken_result() {
        }
        
        public RenewDelegationToken_result(final TRenewDelegationTokenResp success) {
            this();
            this.success = success;
        }
        
        public RenewDelegationToken_result(final RenewDelegationToken_result other) {
            if (other.isSetSuccess()) {
                this.success = new TRenewDelegationTokenResp(other.success);
            }
        }
        
        @Override
        public RenewDelegationToken_result deepCopy() {
            return new RenewDelegationToken_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public TRenewDelegationTokenResp getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final TRenewDelegationTokenResp success) {
            this.success = success;
        }
        
        public void unsetSuccess() {
            this.success = null;
        }
        
        public boolean isSetSuccess() {
            return this.success != null;
        }
        
        public void setSuccessIsSet(final boolean value) {
            if (!value) {
                this.success = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((TRenewDelegationTokenResp)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case SUCCESS: {
                    return this.getSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            switch (field) {
                case SUCCESS: {
                    return this.isSetSuccess();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof RenewDelegationToken_result && this.equals((RenewDelegationToken_result)that);
        }
        
        public boolean equals(final RenewDelegationToken_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = this.isSetSuccess();
            final boolean that_present_success = that.isSetSuccess();
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (!this.success.equals(that.success)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_success = this.isSetSuccess();
            builder.append(present_success);
            if (present_success) {
                builder.append(this.success);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final RenewDelegationToken_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final RenewDelegationToken_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            RenewDelegationToken_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            RenewDelegationToken_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("RenewDelegationToken_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
            if (this.success != null) {
                this.success.validate();
            }
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            try {
                this.write(new TCompactProtocol(new TIOStreamTransport(out)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            try {
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("RenewDelegationToken_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new RenewDelegationToken_resultStandardSchemeFactory());
            RenewDelegationToken_result.schemes.put(TupleScheme.class, new RenewDelegationToken_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, TRenewDelegationTokenResp.class)));
            FieldMetaData.addStructMetaDataMap(RenewDelegationToken_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static _Fields findByThriftIdOrThrow(final int fieldId) {
                final _Fields fields = findByThriftId(fieldId);
                if (fields == null) {
                    throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
                }
                return fields;
            }
            
            public static _Fields findByName(final String name) {
                return _Fields.byName.get(name);
            }
            
            private _Fields(final short thriftId, final String fieldName) {
                this._thriftId = thriftId;
                this._fieldName = fieldName;
            }
            
            @Override
            public short getThriftFieldId() {
                return this._thriftId;
            }
            
            @Override
            public String getFieldName() {
                return this._fieldName;
            }
            
            static {
                byName = new HashMap<String, _Fields>();
                for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                    _Fields.byName.put(field.getFieldName(), field);
                }
            }
        }
        
        private static class RenewDelegationToken_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public RenewDelegationToken_resultStandardScheme getScheme() {
                return new RenewDelegationToken_resultStandardScheme();
            }
        }
        
        private static class RenewDelegationToken_resultStandardScheme extends StandardScheme<RenewDelegationToken_result>
        {
            @Override
            public void read(final TProtocol iprot, final RenewDelegationToken_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new TRenewDelegationTokenResp();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        default: {
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                    }
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final RenewDelegationToken_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(RenewDelegationToken_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(RenewDelegationToken_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class RenewDelegationToken_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public RenewDelegationToken_resultTupleScheme getScheme() {
                return new RenewDelegationToken_resultTupleScheme();
            }
        }
        
        private static class RenewDelegationToken_resultTupleScheme extends TupleScheme<RenewDelegationToken_result>
        {
            @Override
            public void write(final TProtocol prot, final RenewDelegationToken_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final RenewDelegationToken_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = new TRenewDelegationTokenResp();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public interface Iface
    {
        TOpenSessionResp OpenSession(final TOpenSessionReq p0) throws TException;
        
        TCloseSessionResp CloseSession(final TCloseSessionReq p0) throws TException;
        
        TGetInfoResp GetInfo(final TGetInfoReq p0) throws TException;
        
        TExecuteStatementResp ExecuteStatement(final TExecuteStatementReq p0) throws TException;
        
        TGetTypeInfoResp GetTypeInfo(final TGetTypeInfoReq p0) throws TException;
        
        TGetCatalogsResp GetCatalogs(final TGetCatalogsReq p0) throws TException;
        
        TGetSchemasResp GetSchemas(final TGetSchemasReq p0) throws TException;
        
        TGetTablesResp GetTables(final TGetTablesReq p0) throws TException;
        
        TGetTableTypesResp GetTableTypes(final TGetTableTypesReq p0) throws TException;
        
        TGetColumnsResp GetColumns(final TGetColumnsReq p0) throws TException;
        
        TGetFunctionsResp GetFunctions(final TGetFunctionsReq p0) throws TException;
        
        TGetOperationStatusResp GetOperationStatus(final TGetOperationStatusReq p0) throws TException;
        
        TCancelOperationResp CancelOperation(final TCancelOperationReq p0) throws TException;
        
        TCloseOperationResp CloseOperation(final TCloseOperationReq p0) throws TException;
        
        TGetResultSetMetadataResp GetResultSetMetadata(final TGetResultSetMetadataReq p0) throws TException;
        
        TFetchResultsResp FetchResults(final TFetchResultsReq p0) throws TException;
        
        TGetDelegationTokenResp GetDelegationToken(final TGetDelegationTokenReq p0) throws TException;
        
        TCancelDelegationTokenResp CancelDelegationToken(final TCancelDelegationTokenReq p0) throws TException;
        
        TRenewDelegationTokenResp RenewDelegationToken(final TRenewDelegationTokenReq p0) throws TException;
    }
    
    public interface AsyncIface
    {
        void OpenSession(final TOpenSessionReq p0, final AsyncMethodCallback<AsyncClient.OpenSession_call> p1) throws TException;
        
        void CloseSession(final TCloseSessionReq p0, final AsyncMethodCallback<AsyncClient.CloseSession_call> p1) throws TException;
        
        void GetInfo(final TGetInfoReq p0, final AsyncMethodCallback<AsyncClient.GetInfo_call> p1) throws TException;
        
        void ExecuteStatement(final TExecuteStatementReq p0, final AsyncMethodCallback<AsyncClient.ExecuteStatement_call> p1) throws TException;
        
        void GetTypeInfo(final TGetTypeInfoReq p0, final AsyncMethodCallback<AsyncClient.GetTypeInfo_call> p1) throws TException;
        
        void GetCatalogs(final TGetCatalogsReq p0, final AsyncMethodCallback<AsyncClient.GetCatalogs_call> p1) throws TException;
        
        void GetSchemas(final TGetSchemasReq p0, final AsyncMethodCallback<AsyncClient.GetSchemas_call> p1) throws TException;
        
        void GetTables(final TGetTablesReq p0, final AsyncMethodCallback<AsyncClient.GetTables_call> p1) throws TException;
        
        void GetTableTypes(final TGetTableTypesReq p0, final AsyncMethodCallback<AsyncClient.GetTableTypes_call> p1) throws TException;
        
        void GetColumns(final TGetColumnsReq p0, final AsyncMethodCallback<AsyncClient.GetColumns_call> p1) throws TException;
        
        void GetFunctions(final TGetFunctionsReq p0, final AsyncMethodCallback<AsyncClient.GetFunctions_call> p1) throws TException;
        
        void GetOperationStatus(final TGetOperationStatusReq p0, final AsyncMethodCallback<AsyncClient.GetOperationStatus_call> p1) throws TException;
        
        void CancelOperation(final TCancelOperationReq p0, final AsyncMethodCallback<AsyncClient.CancelOperation_call> p1) throws TException;
        
        void CloseOperation(final TCloseOperationReq p0, final AsyncMethodCallback<AsyncClient.CloseOperation_call> p1) throws TException;
        
        void GetResultSetMetadata(final TGetResultSetMetadataReq p0, final AsyncMethodCallback<AsyncClient.GetResultSetMetadata_call> p1) throws TException;
        
        void FetchResults(final TFetchResultsReq p0, final AsyncMethodCallback<AsyncClient.FetchResults_call> p1) throws TException;
        
        void GetDelegationToken(final TGetDelegationTokenReq p0, final AsyncMethodCallback<AsyncClient.GetDelegationToken_call> p1) throws TException;
        
        void CancelDelegationToken(final TCancelDelegationTokenReq p0, final AsyncMethodCallback<AsyncClient.CancelDelegationToken_call> p1) throws TException;
        
        void RenewDelegationToken(final TRenewDelegationTokenReq p0, final AsyncMethodCallback<AsyncClient.RenewDelegationToken_call> p1) throws TException;
    }
}
