// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.service;

import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.meta_data.ListMetaData;
import java.util.ArrayList;
import org.apache.thrift.EncodingUtils;
import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
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
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.hadoop.hive.ql.plan.api.QueryPlan;
import org.apache.hadoop.hive.metastore.api.Schema;
import java.util.List;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore;

public class ThriftHive
{
    public static class Client extends ThriftHiveMetastore.Client implements ThriftHive.Iface
    {
        public Client(final TProtocol prot) {
            super(prot, prot);
        }
        
        public Client(final TProtocol iprot, final TProtocol oprot) {
            super(iprot, oprot);
        }
        
        @Override
        public void execute(final String query) throws HiveServerException, TException {
            this.send_execute(query);
            this.recv_execute();
        }
        
        public void send_execute(final String query) throws TException {
            final execute_args args = new execute_args();
            args.setQuery(query);
            this.sendBase("execute", args);
        }
        
        public void recv_execute() throws HiveServerException, TException {
            final execute_result result = new execute_result();
            this.receiveBase(result, "execute");
            if (result.ex != null) {
                throw result.ex;
            }
        }
        
        @Override
        public String fetchOne() throws HiveServerException, TException {
            this.send_fetchOne();
            return this.recv_fetchOne();
        }
        
        public void send_fetchOne() throws TException {
            final fetchOne_args args = new fetchOne_args();
            this.sendBase("fetchOne", args);
        }
        
        public String recv_fetchOne() throws HiveServerException, TException {
            final fetchOne_result result = new fetchOne_result();
            this.receiveBase(result, "fetchOne");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "fetchOne failed: unknown result");
        }
        
        @Override
        public List<String> fetchN(final int numRows) throws HiveServerException, TException {
            this.send_fetchN(numRows);
            return this.recv_fetchN();
        }
        
        public void send_fetchN(final int numRows) throws TException {
            final fetchN_args args = new fetchN_args();
            args.setNumRows(numRows);
            this.sendBase("fetchN", args);
        }
        
        public List<String> recv_fetchN() throws HiveServerException, TException {
            final fetchN_result result = new fetchN_result();
            this.receiveBase(result, "fetchN");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "fetchN failed: unknown result");
        }
        
        @Override
        public List<String> fetchAll() throws HiveServerException, TException {
            this.send_fetchAll();
            return this.recv_fetchAll();
        }
        
        public void send_fetchAll() throws TException {
            final fetchAll_args args = new fetchAll_args();
            this.sendBase("fetchAll", args);
        }
        
        public List<String> recv_fetchAll() throws HiveServerException, TException {
            final fetchAll_result result = new fetchAll_result();
            this.receiveBase(result, "fetchAll");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "fetchAll failed: unknown result");
        }
        
        @Override
        public Schema getSchema() throws HiveServerException, TException {
            this.send_getSchema();
            return this.recv_getSchema();
        }
        
        public void send_getSchema() throws TException {
            final getSchema_args args = new getSchema_args();
            this.sendBase("getSchema", args);
        }
        
        public Schema recv_getSchema() throws HiveServerException, TException {
            final getSchema_result result = new getSchema_result();
            this.receiveBase(result, "getSchema");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "getSchema failed: unknown result");
        }
        
        @Override
        public Schema getThriftSchema() throws HiveServerException, TException {
            this.send_getThriftSchema();
            return this.recv_getThriftSchema();
        }
        
        public void send_getThriftSchema() throws TException {
            final getThriftSchema_args args = new getThriftSchema_args();
            this.sendBase("getThriftSchema", args);
        }
        
        public Schema recv_getThriftSchema() throws HiveServerException, TException {
            final getThriftSchema_result result = new getThriftSchema_result();
            this.receiveBase(result, "getThriftSchema");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "getThriftSchema failed: unknown result");
        }
        
        @Override
        public HiveClusterStatus getClusterStatus() throws HiveServerException, TException {
            this.send_getClusterStatus();
            return this.recv_getClusterStatus();
        }
        
        public void send_getClusterStatus() throws TException {
            final getClusterStatus_args args = new getClusterStatus_args();
            this.sendBase("getClusterStatus", args);
        }
        
        public HiveClusterStatus recv_getClusterStatus() throws HiveServerException, TException {
            final getClusterStatus_result result = new getClusterStatus_result();
            this.receiveBase(result, "getClusterStatus");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "getClusterStatus failed: unknown result");
        }
        
        @Override
        public QueryPlan getQueryPlan() throws HiveServerException, TException {
            this.send_getQueryPlan();
            return this.recv_getQueryPlan();
        }
        
        public void send_getQueryPlan() throws TException {
            final getQueryPlan_args args = new getQueryPlan_args();
            this.sendBase("getQueryPlan", args);
        }
        
        public QueryPlan recv_getQueryPlan() throws HiveServerException, TException {
            final getQueryPlan_result result = new getQueryPlan_result();
            this.receiveBase(result, "getQueryPlan");
            if (result.isSetSuccess()) {
                return result.success;
            }
            if (result.ex != null) {
                throw result.ex;
            }
            throw new TApplicationException(5, "getQueryPlan failed: unknown result");
        }
        
        @Override
        public void clean() throws TException {
            this.send_clean();
            this.recv_clean();
        }
        
        public void send_clean() throws TException {
            final clean_args args = new clean_args();
            this.sendBase("clean", args);
        }
        
        public void recv_clean() throws TException {
            final clean_result result = new clean_result();
            this.receiveBase(result, "clean");
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
    
    public static class AsyncClient extends ThriftHiveMetastore.AsyncClient implements ThriftHive.AsyncIface
    {
        public AsyncClient(final TProtocolFactory protocolFactory, final TAsyncClientManager clientManager, final TNonblockingTransport transport) {
            super(protocolFactory, clientManager, transport);
        }
        
        @Override
        public void execute(final String query, final AsyncMethodCallback<execute_call> resultHandler) throws TException {
            this.checkReady();
            final execute_call method_call = new execute_call(query, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void fetchOne(final AsyncMethodCallback<fetchOne_call> resultHandler) throws TException {
            this.checkReady();
            final fetchOne_call method_call = new fetchOne_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void fetchN(final int numRows, final AsyncMethodCallback<fetchN_call> resultHandler) throws TException {
            this.checkReady();
            final fetchN_call method_call = new fetchN_call(numRows, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void fetchAll(final AsyncMethodCallback<fetchAll_call> resultHandler) throws TException {
            this.checkReady();
            final fetchAll_call method_call = new fetchAll_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getSchema(final AsyncMethodCallback<getSchema_call> resultHandler) throws TException {
            this.checkReady();
            final getSchema_call method_call = new getSchema_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getThriftSchema(final AsyncMethodCallback<getThriftSchema_call> resultHandler) throws TException {
            this.checkReady();
            final getThriftSchema_call method_call = new getThriftSchema_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getClusterStatus(final AsyncMethodCallback<getClusterStatus_call> resultHandler) throws TException {
            this.checkReady();
            final getClusterStatus_call method_call = new getClusterStatus_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getQueryPlan(final AsyncMethodCallback<getQueryPlan_call> resultHandler) throws TException {
            this.checkReady();
            final getQueryPlan_call method_call = new getQueryPlan_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void clean(final AsyncMethodCallback<clean_call> resultHandler) throws TException {
            this.checkReady();
            final clean_call method_call = new clean_call(resultHandler, this, this.___protocolFactory, this.___transport);
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
        
        public static class execute_call extends TAsyncMethodCall
        {
            private String query;
            
            public execute_call(final String query, final AsyncMethodCallback<execute_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.query = query;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("execute", (byte)1, 0));
                final execute_args args = new execute_args();
                args.setQuery(this.query);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public void getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                new ThriftHive.Client(prot).recv_execute();
            }
        }
        
        public static class fetchOne_call extends TAsyncMethodCall
        {
            public fetchOne_call(final AsyncMethodCallback<fetchOne_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("fetchOne", (byte)1, 0));
                final fetchOne_args args = new fetchOne_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public String getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_fetchOne();
            }
        }
        
        public static class fetchN_call extends TAsyncMethodCall
        {
            private int numRows;
            
            public fetchN_call(final int numRows, final AsyncMethodCallback<fetchN_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.numRows = numRows;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("fetchN", (byte)1, 0));
                final fetchN_args args = new fetchN_args();
                args.setNumRows(this.numRows);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public List<String> getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_fetchN();
            }
        }
        
        public static class fetchAll_call extends TAsyncMethodCall
        {
            public fetchAll_call(final AsyncMethodCallback<fetchAll_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("fetchAll", (byte)1, 0));
                final fetchAll_args args = new fetchAll_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public List<String> getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_fetchAll();
            }
        }
        
        public static class getSchema_call extends TAsyncMethodCall
        {
            public getSchema_call(final AsyncMethodCallback<getSchema_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getSchema", (byte)1, 0));
                final getSchema_args args = new getSchema_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public Schema getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_getSchema();
            }
        }
        
        public static class getThriftSchema_call extends TAsyncMethodCall
        {
            public getThriftSchema_call(final AsyncMethodCallback<getThriftSchema_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getThriftSchema", (byte)1, 0));
                final getThriftSchema_args args = new getThriftSchema_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public Schema getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_getThriftSchema();
            }
        }
        
        public static class getClusterStatus_call extends TAsyncMethodCall
        {
            public getClusterStatus_call(final AsyncMethodCallback<getClusterStatus_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getClusterStatus", (byte)1, 0));
                final getClusterStatus_args args = new getClusterStatus_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public HiveClusterStatus getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_getClusterStatus();
            }
        }
        
        public static class getQueryPlan_call extends TAsyncMethodCall
        {
            public getQueryPlan_call(final AsyncMethodCallback<getQueryPlan_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getQueryPlan", (byte)1, 0));
                final getQueryPlan_args args = new getQueryPlan_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public QueryPlan getResult() throws HiveServerException, TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new ThriftHive.Client(prot).recv_getQueryPlan();
            }
        }
        
        public static class clean_call extends TAsyncMethodCall
        {
            public clean_call(final AsyncMethodCallback<clean_call> resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("clean", (byte)1, 0));
                final clean_args args = new clean_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public void getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                new ThriftHive.Client(prot).recv_clean();
            }
        }
    }
    
    public static class Processor<I extends ThriftHive.Iface> extends ThriftHiveMetastore.Processor<I> implements TProcessor
    {
        private static final Logger LOGGER;
        
        public Processor(final I iface) {
            super(iface, (Map<String, ProcessFunction<I, ? extends TBase>>)getProcessMap(new HashMap<String, ProcessFunction<I, ? extends TBase>>()));
        }
        
        protected Processor(final I iface, final Map<String, ProcessFunction<I, ? extends TBase>> processMap) {
            super(iface, (Map<String, ProcessFunction<I, ? extends TBase>>)getProcessMap((Map<String, ProcessFunction<I, ? extends TBase>>)processMap));
        }
        
        private static <I extends ThriftHive.Iface> Map<String, ProcessFunction<I, ? extends TBase>> getProcessMap(final Map<String, ProcessFunction<I, ? extends TBase>> processMap) {
            processMap.put("execute", (ProcessFunction<I, ? extends TBase>)new execute());
            processMap.put("fetchOne", (ProcessFunction<I, ? extends TBase>)new fetchOne());
            processMap.put("fetchN", (ProcessFunction<I, ? extends TBase>)new fetchN());
            processMap.put("fetchAll", (ProcessFunction<I, ? extends TBase>)new fetchAll());
            processMap.put("getSchema", (ProcessFunction<I, ? extends TBase>)new getSchema());
            processMap.put("getThriftSchema", (ProcessFunction<I, ? extends TBase>)new getThriftSchema());
            processMap.put("getClusterStatus", (ProcessFunction<I, ? extends TBase>)new getClusterStatus());
            processMap.put("getQueryPlan", (ProcessFunction<I, ? extends TBase>)new getQueryPlan());
            processMap.put("clean", (ProcessFunction<I, ? extends TBase>)new clean());
            return processMap;
        }
        
        static {
            LOGGER = LoggerFactory.getLogger(Processor.class.getName());
        }
        
        public static class execute<I extends ThriftHive.Iface> extends ProcessFunction<I, execute_args>
        {
            public execute() {
                super("execute");
            }
            
            @Override
            public execute_args getEmptyArgsInstance() {
                return new execute_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public execute_result getResult(final I iface, final execute_args args) throws TException {
                final execute_result result = new execute_result();
                try {
                    iface.execute(args.query);
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class fetchOne<I extends ThriftHive.Iface> extends ProcessFunction<I, fetchOne_args>
        {
            public fetchOne() {
                super("fetchOne");
            }
            
            @Override
            public fetchOne_args getEmptyArgsInstance() {
                return new fetchOne_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public fetchOne_result getResult(final I iface, final fetchOne_args args) throws TException {
                final fetchOne_result result = new fetchOne_result();
                try {
                    result.success = iface.fetchOne();
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class fetchN<I extends ThriftHive.Iface> extends ProcessFunction<I, fetchN_args>
        {
            public fetchN() {
                super("fetchN");
            }
            
            @Override
            public fetchN_args getEmptyArgsInstance() {
                return new fetchN_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public fetchN_result getResult(final I iface, final fetchN_args args) throws TException {
                final fetchN_result result = new fetchN_result();
                try {
                    result.success = iface.fetchN(args.numRows);
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class fetchAll<I extends ThriftHive.Iface> extends ProcessFunction<I, fetchAll_args>
        {
            public fetchAll() {
                super("fetchAll");
            }
            
            @Override
            public fetchAll_args getEmptyArgsInstance() {
                return new fetchAll_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public fetchAll_result getResult(final I iface, final fetchAll_args args) throws TException {
                final fetchAll_result result = new fetchAll_result();
                try {
                    result.success = iface.fetchAll();
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class getSchema<I extends ThriftHive.Iface> extends ProcessFunction<I, getSchema_args>
        {
            public getSchema() {
                super("getSchema");
            }
            
            @Override
            public getSchema_args getEmptyArgsInstance() {
                return new getSchema_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getSchema_result getResult(final I iface, final getSchema_args args) throws TException {
                final getSchema_result result = new getSchema_result();
                try {
                    result.success = iface.getSchema();
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class getThriftSchema<I extends ThriftHive.Iface> extends ProcessFunction<I, getThriftSchema_args>
        {
            public getThriftSchema() {
                super("getThriftSchema");
            }
            
            @Override
            public getThriftSchema_args getEmptyArgsInstance() {
                return new getThriftSchema_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getThriftSchema_result getResult(final I iface, final getThriftSchema_args args) throws TException {
                final getThriftSchema_result result = new getThriftSchema_result();
                try {
                    result.success = iface.getThriftSchema();
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class getClusterStatus<I extends ThriftHive.Iface> extends ProcessFunction<I, getClusterStatus_args>
        {
            public getClusterStatus() {
                super("getClusterStatus");
            }
            
            @Override
            public getClusterStatus_args getEmptyArgsInstance() {
                return new getClusterStatus_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getClusterStatus_result getResult(final I iface, final getClusterStatus_args args) throws TException {
                final getClusterStatus_result result = new getClusterStatus_result();
                try {
                    result.success = iface.getClusterStatus();
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class getQueryPlan<I extends ThriftHive.Iface> extends ProcessFunction<I, getQueryPlan_args>
        {
            public getQueryPlan() {
                super("getQueryPlan");
            }
            
            @Override
            public getQueryPlan_args getEmptyArgsInstance() {
                return new getQueryPlan_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getQueryPlan_result getResult(final I iface, final getQueryPlan_args args) throws TException {
                final getQueryPlan_result result = new getQueryPlan_result();
                try {
                    result.success = iface.getQueryPlan();
                }
                catch (HiveServerException ex) {
                    result.ex = ex;
                }
                return result;
            }
        }
        
        public static class clean<I extends ThriftHive.Iface> extends ProcessFunction<I, clean_args>
        {
            public clean() {
                super("clean");
            }
            
            @Override
            public clean_args getEmptyArgsInstance() {
                return new clean_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public clean_result getResult(final I iface, final clean_args args) throws TException {
                final clean_result result = new clean_result();
                iface.clean();
                return result;
            }
        }
    }
    
    public static class execute_args implements TBase<execute_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField QUERY_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private String query;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public execute_args() {
        }
        
        public execute_args(final String query) {
            this();
            this.query = query;
        }
        
        public execute_args(final execute_args other) {
            if (other.isSetQuery()) {
                this.query = other.query;
            }
        }
        
        @Override
        public execute_args deepCopy() {
            return new execute_args(this);
        }
        
        @Override
        public void clear() {
            this.query = null;
        }
        
        public String getQuery() {
            return this.query;
        }
        
        public void setQuery(final String query) {
            this.query = query;
        }
        
        public void unsetQuery() {
            this.query = null;
        }
        
        public boolean isSetQuery() {
            return this.query != null;
        }
        
        public void setQueryIsSet(final boolean value) {
            if (!value) {
                this.query = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case QUERY: {
                    if (value == null) {
                        this.unsetQuery();
                        break;
                    }
                    this.setQuery((String)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case QUERY: {
                    return this.getQuery();
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
                case QUERY: {
                    return this.isSetQuery();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof execute_args && this.equals((execute_args)that);
        }
        
        public boolean equals(final execute_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_query = this.isSetQuery();
            final boolean that_present_query = that.isSetQuery();
            if (this_present_query || that_present_query) {
                if (!this_present_query || !that_present_query) {
                    return false;
                }
                if (!this.query.equals(that.query)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_query = this.isSetQuery();
            builder.append(present_query);
            if (present_query) {
                builder.append(this.query);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final execute_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final execute_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetQuery()).compareTo(Boolean.valueOf(typedOther.isSetQuery()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetQuery()) {
                lastComparison = TBaseHelper.compareTo(this.query, typedOther.query);
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
            execute_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            execute_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("execute_args(");
            boolean first = true;
            sb.append("query:");
            if (this.query == null) {
                sb.append("null");
            }
            else {
                sb.append(this.query);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("execute_args");
            QUERY_FIELD_DESC = new TField("query", (byte)11, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new execute_argsStandardSchemeFactory());
            execute_args.schemes.put(TupleScheme.class, new execute_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.QUERY, new FieldMetaData("query", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(execute_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            QUERY((short)1, "query");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.QUERY;
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
        
        private static class execute_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public execute_argsStandardScheme getScheme() {
                return new execute_argsStandardScheme();
            }
        }
        
        private static class execute_argsStandardScheme extends StandardScheme<execute_args>
        {
            @Override
            public void read(final TProtocol iprot, final execute_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 11) {
                                struct.query = iprot.readString();
                                struct.setQueryIsSet(true);
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
            public void write(final TProtocol oprot, final execute_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(execute_args.STRUCT_DESC);
                if (struct.query != null) {
                    oprot.writeFieldBegin(execute_args.QUERY_FIELD_DESC);
                    oprot.writeString(struct.query);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class execute_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public execute_argsTupleScheme getScheme() {
                return new execute_argsTupleScheme();
            }
        }
        
        private static class execute_argsTupleScheme extends TupleScheme<execute_args>
        {
            @Override
            public void write(final TProtocol prot, final execute_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetQuery()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetQuery()) {
                    oprot.writeString(struct.query);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final execute_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.query = iprot.readString();
                    struct.setQueryIsSet(true);
                }
            }
        }
    }
    
    public static class execute_result implements TBase<execute_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public execute_result() {
        }
        
        public execute_result(final HiveServerException ex) {
            this();
            this.ex = ex;
        }
        
        public execute_result(final execute_result other) {
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public execute_result deepCopy() {
            return new execute_result(this);
        }
        
        @Override
        public void clear() {
            this.ex = null;
        }
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof execute_result && this.equals((execute_result)that);
        }
        
        public boolean equals(final execute_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final execute_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final execute_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            execute_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            execute_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("execute_result(");
            boolean first = true;
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("execute_result");
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new execute_resultStandardSchemeFactory());
            execute_result.schemes.put(TupleScheme.class, new execute_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(execute_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.EX;
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
        
        private static class execute_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public execute_resultStandardScheme getScheme() {
                return new execute_resultStandardScheme();
            }
        }
        
        private static class execute_resultStandardScheme extends StandardScheme<execute_result>
        {
            @Override
            public void read(final TProtocol iprot, final execute_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final execute_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(execute_result.STRUCT_DESC);
                if (struct.ex != null) {
                    oprot.writeFieldBegin(execute_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class execute_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public execute_resultTupleScheme getScheme() {
                return new execute_resultTupleScheme();
            }
        }
        
        private static class execute_resultTupleScheme extends TupleScheme<execute_result>
        {
            @Override
            public void write(final TProtocol prot, final execute_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetEx()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final execute_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class fetchOne_args implements TBase<fetchOne_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public fetchOne_args() {
        }
        
        public fetchOne_args(final fetchOne_args other) {
        }
        
        @Override
        public fetchOne_args deepCopy() {
            return new fetchOne_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$fetchOne_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$fetchOne_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$fetchOne_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof fetchOne_args && this.equals((fetchOne_args)that);
        }
        
        public boolean equals(final fetchOne_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final fetchOne_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final fetchOne_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            fetchOne_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            fetchOne_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("fetchOne_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("fetchOne_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new fetchOne_argsStandardSchemeFactory());
            fetchOne_args.schemes.put(TupleScheme.class, new fetchOne_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(fetchOne_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class fetchOne_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchOne_argsStandardScheme getScheme() {
                return new fetchOne_argsStandardScheme();
            }
        }
        
        private static class fetchOne_argsStandardScheme extends StandardScheme<fetchOne_args>
        {
            @Override
            public void read(final TProtocol iprot, final fetchOne_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final fetchOne_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(fetchOne_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class fetchOne_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchOne_argsTupleScheme getScheme() {
                return new fetchOne_argsTupleScheme();
            }
        }
        
        private static class fetchOne_argsTupleScheme extends TupleScheme<fetchOne_args>
        {
            @Override
            public void write(final TProtocol prot, final fetchOne_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final fetchOne_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class fetchOne_result implements TBase<fetchOne_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private String success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public fetchOne_result() {
        }
        
        public fetchOne_result(final String success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public fetchOne_result(final fetchOne_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public fetchOne_result deepCopy() {
            return new fetchOne_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public String getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final String success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((String)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof fetchOne_result && this.equals((fetchOne_result)that);
        }
        
        public boolean equals(final fetchOne_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final fetchOne_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final fetchOne_result typedOther = other;
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
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            fetchOne_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            fetchOne_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("fetchOne_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("fetchOne_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)11, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new fetchOne_resultStandardSchemeFactory());
            fetchOne_result.schemes.put(TupleScheme.class, new fetchOne_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)11)));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(fetchOne_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class fetchOne_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchOne_resultStandardScheme getScheme() {
                return new fetchOne_resultStandardScheme();
            }
        }
        
        private static class fetchOne_resultStandardScheme extends StandardScheme<fetchOne_result>
        {
            @Override
            public void read(final TProtocol iprot, final fetchOne_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 11) {
                                struct.success = iprot.readString();
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final fetchOne_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(fetchOne_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(fetchOne_result.SUCCESS_FIELD_DESC);
                    oprot.writeString(struct.success);
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(fetchOne_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class fetchOne_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchOne_resultTupleScheme getScheme() {
                return new fetchOne_resultTupleScheme();
            }
        }
        
        private static class fetchOne_resultTupleScheme extends TupleScheme<fetchOne_result>
        {
            @Override
            public void write(final TProtocol prot, final fetchOne_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    oprot.writeString(struct.success);
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final fetchOne_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    struct.success = iprot.readString();
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class fetchN_args implements TBase<fetchN_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField NUM_ROWS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private int numRows;
        private static final int __NUMROWS_ISSET_ID = 0;
        private byte __isset_bitfield;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public fetchN_args() {
            this.__isset_bitfield = 0;
        }
        
        public fetchN_args(final int numRows) {
            this();
            this.numRows = numRows;
            this.setNumRowsIsSet(true);
        }
        
        public fetchN_args(final fetchN_args other) {
            this.__isset_bitfield = 0;
            this.__isset_bitfield = other.__isset_bitfield;
            this.numRows = other.numRows;
        }
        
        @Override
        public fetchN_args deepCopy() {
            return new fetchN_args(this);
        }
        
        @Override
        public void clear() {
            this.setNumRowsIsSet(false);
            this.numRows = 0;
        }
        
        public int getNumRows() {
            return this.numRows;
        }
        
        public void setNumRows(final int numRows) {
            this.numRows = numRows;
            this.setNumRowsIsSet(true);
        }
        
        public void unsetNumRows() {
            this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
        }
        
        public boolean isSetNumRows() {
            return EncodingUtils.testBit(this.__isset_bitfield, 0);
        }
        
        public void setNumRowsIsSet(final boolean value) {
            this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case NUM_ROWS: {
                    if (value == null) {
                        this.unsetNumRows();
                        break;
                    }
                    this.setNumRows((int)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case NUM_ROWS: {
                    return this.getNumRows();
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
                case NUM_ROWS: {
                    return this.isSetNumRows();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof fetchN_args && this.equals((fetchN_args)that);
        }
        
        public boolean equals(final fetchN_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_numRows = true;
            final boolean that_present_numRows = true;
            if (this_present_numRows || that_present_numRows) {
                if (!this_present_numRows || !that_present_numRows) {
                    return false;
                }
                if (this.numRows != that.numRows) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            final boolean present_numRows = true;
            builder.append(present_numRows);
            if (present_numRows) {
                builder.append(this.numRows);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final fetchN_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final fetchN_args typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetNumRows()).compareTo(Boolean.valueOf(typedOther.isSetNumRows()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetNumRows()) {
                lastComparison = TBaseHelper.compareTo(this.numRows, typedOther.numRows);
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
            fetchN_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            fetchN_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("fetchN_args(");
            boolean first = true;
            sb.append("numRows:");
            sb.append(this.numRows);
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
                this.__isset_bitfield = 0;
                this.read(new TCompactProtocol(new TIOStreamTransport(in)));
            }
            catch (TException te) {
                throw new IOException(te);
            }
        }
        
        static {
            STRUCT_DESC = new TStruct("fetchN_args");
            NUM_ROWS_FIELD_DESC = new TField("numRows", (byte)8, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new fetchN_argsStandardSchemeFactory());
            fetchN_args.schemes.put(TupleScheme.class, new fetchN_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.NUM_ROWS, new FieldMetaData("numRows", (byte)3, new FieldValueMetaData((byte)8)));
            FieldMetaData.addStructMetaDataMap(fetchN_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            NUM_ROWS((short)1, "numRows");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.NUM_ROWS;
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
        
        private static class fetchN_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchN_argsStandardScheme getScheme() {
                return new fetchN_argsStandardScheme();
            }
        }
        
        private static class fetchN_argsStandardScheme extends StandardScheme<fetchN_args>
        {
            @Override
            public void read(final TProtocol iprot, final fetchN_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 8) {
                                struct.numRows = iprot.readI32();
                                struct.setNumRowsIsSet(true);
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
            public void write(final TProtocol oprot, final fetchN_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(fetchN_args.STRUCT_DESC);
                oprot.writeFieldBegin(fetchN_args.NUM_ROWS_FIELD_DESC);
                oprot.writeI32(struct.numRows);
                oprot.writeFieldEnd();
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class fetchN_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchN_argsTupleScheme getScheme() {
                return new fetchN_argsTupleScheme();
            }
        }
        
        private static class fetchN_argsTupleScheme extends TupleScheme<fetchN_args>
        {
            @Override
            public void write(final TProtocol prot, final fetchN_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetNumRows()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetNumRows()) {
                    oprot.writeI32(struct.numRows);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final fetchN_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.numRows = iprot.readI32();
                    struct.setNumRowsIsSet(true);
                }
            }
        }
    }
    
    public static class fetchN_result implements TBase<fetchN_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private List<String> success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public fetchN_result() {
        }
        
        public fetchN_result(final List<String> success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public fetchN_result(final fetchN_result other) {
            if (other.isSetSuccess()) {
                final List<String> __this__success = new ArrayList<String>();
                for (final String other_element : other.success) {
                    __this__success.add(other_element);
                }
                this.success = __this__success;
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public fetchN_result deepCopy() {
            return new fetchN_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public int getSuccessSize() {
            return (this.success == null) ? 0 : this.success.size();
        }
        
        public Iterator<String> getSuccessIterator() {
            return (this.success == null) ? null : this.success.iterator();
        }
        
        public void addToSuccess(final String elem) {
            if (this.success == null) {
                this.success = new ArrayList<String>();
            }
            this.success.add(elem);
        }
        
        public List<String> getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final List<String> success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((List<String>)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof fetchN_result && this.equals((fetchN_result)that);
        }
        
        public boolean equals(final fetchN_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final fetchN_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final fetchN_result typedOther = other;
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
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            fetchN_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            fetchN_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("fetchN_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("fetchN_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)15, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new fetchN_resultStandardSchemeFactory());
            fetchN_result.schemes.put(TupleScheme.class, new fetchN_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(fetchN_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class fetchN_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchN_resultStandardScheme getScheme() {
                return new fetchN_resultStandardScheme();
            }
        }
        
        private static class fetchN_resultStandardScheme extends StandardScheme<fetchN_result>
        {
            @Override
            public void read(final TProtocol iprot, final fetchN_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 15) {
                                final TList _list0 = iprot.readListBegin();
                                struct.success = (List<String>)new ArrayList(_list0.size);
                                for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
                                    final String _elem2 = iprot.readString();
                                    struct.success.add(_elem2);
                                }
                                iprot.readListEnd();
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final fetchN_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(fetchN_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(fetchN_result.SUCCESS_FIELD_DESC);
                    oprot.writeListBegin(new TList((byte)11, struct.success.size()));
                    for (final String _iter3 : struct.success) {
                        oprot.writeString(_iter3);
                    }
                    oprot.writeListEnd();
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(fetchN_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class fetchN_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchN_resultTupleScheme getScheme() {
                return new fetchN_resultTupleScheme();
            }
        }
        
        private static class fetchN_resultTupleScheme extends TupleScheme<fetchN_result>
        {
            @Override
            public void write(final TProtocol prot, final fetchN_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    oprot.writeI32(struct.success.size());
                    for (final String _iter4 : struct.success) {
                        oprot.writeString(_iter4);
                    }
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final fetchN_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    final TList _list5 = new TList((byte)11, iprot.readI32());
                    struct.success = (List<String>)new ArrayList(_list5.size);
                    for (int _i6 = 0; _i6 < _list5.size; ++_i6) {
                        final String _elem7 = iprot.readString();
                        struct.success.add(_elem7);
                    }
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class fetchAll_args implements TBase<fetchAll_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public fetchAll_args() {
        }
        
        public fetchAll_args(final fetchAll_args other) {
        }
        
        @Override
        public fetchAll_args deepCopy() {
            return new fetchAll_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$fetchAll_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$fetchAll_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$fetchAll_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof fetchAll_args && this.equals((fetchAll_args)that);
        }
        
        public boolean equals(final fetchAll_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final fetchAll_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final fetchAll_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            fetchAll_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            fetchAll_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("fetchAll_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("fetchAll_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new fetchAll_argsStandardSchemeFactory());
            fetchAll_args.schemes.put(TupleScheme.class, new fetchAll_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(fetchAll_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class fetchAll_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchAll_argsStandardScheme getScheme() {
                return new fetchAll_argsStandardScheme();
            }
        }
        
        private static class fetchAll_argsStandardScheme extends StandardScheme<fetchAll_args>
        {
            @Override
            public void read(final TProtocol iprot, final fetchAll_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final fetchAll_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(fetchAll_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class fetchAll_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchAll_argsTupleScheme getScheme() {
                return new fetchAll_argsTupleScheme();
            }
        }
        
        private static class fetchAll_argsTupleScheme extends TupleScheme<fetchAll_args>
        {
            @Override
            public void write(final TProtocol prot, final fetchAll_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final fetchAll_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class fetchAll_result implements TBase<fetchAll_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private List<String> success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public fetchAll_result() {
        }
        
        public fetchAll_result(final List<String> success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public fetchAll_result(final fetchAll_result other) {
            if (other.isSetSuccess()) {
                final List<String> __this__success = new ArrayList<String>();
                for (final String other_element : other.success) {
                    __this__success.add(other_element);
                }
                this.success = __this__success;
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public fetchAll_result deepCopy() {
            return new fetchAll_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public int getSuccessSize() {
            return (this.success == null) ? 0 : this.success.size();
        }
        
        public Iterator<String> getSuccessIterator() {
            return (this.success == null) ? null : this.success.iterator();
        }
        
        public void addToSuccess(final String elem) {
            if (this.success == null) {
                this.success = new ArrayList<String>();
            }
            this.success.add(elem);
        }
        
        public List<String> getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final List<String> success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((List<String>)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof fetchAll_result && this.equals((fetchAll_result)that);
        }
        
        public boolean equals(final fetchAll_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final fetchAll_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final fetchAll_result typedOther = other;
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
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            fetchAll_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            fetchAll_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("fetchAll_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
            }
            first = false;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("fetchAll_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)15, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new fetchAll_resultStandardSchemeFactory());
            fetchAll_result.schemes.put(TupleScheme.class, new fetchAll_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(fetchAll_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class fetchAll_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchAll_resultStandardScheme getScheme() {
                return new fetchAll_resultStandardScheme();
            }
        }
        
        private static class fetchAll_resultStandardScheme extends StandardScheme<fetchAll_result>
        {
            @Override
            public void read(final TProtocol iprot, final fetchAll_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 15) {
                                final TList _list8 = iprot.readListBegin();
                                struct.success = (List<String>)new ArrayList(_list8.size);
                                for (int _i9 = 0; _i9 < _list8.size; ++_i9) {
                                    final String _elem10 = iprot.readString();
                                    struct.success.add(_elem10);
                                }
                                iprot.readListEnd();
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final fetchAll_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(fetchAll_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(fetchAll_result.SUCCESS_FIELD_DESC);
                    oprot.writeListBegin(new TList((byte)11, struct.success.size()));
                    for (final String _iter11 : struct.success) {
                        oprot.writeString(_iter11);
                    }
                    oprot.writeListEnd();
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(fetchAll_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class fetchAll_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public fetchAll_resultTupleScheme getScheme() {
                return new fetchAll_resultTupleScheme();
            }
        }
        
        private static class fetchAll_resultTupleScheme extends TupleScheme<fetchAll_result>
        {
            @Override
            public void write(final TProtocol prot, final fetchAll_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    oprot.writeI32(struct.success.size());
                    for (final String _iter12 : struct.success) {
                        oprot.writeString(_iter12);
                    }
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final fetchAll_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    final TList _list13 = new TList((byte)11, iprot.readI32());
                    struct.success = (List<String>)new ArrayList(_list13.size);
                    for (int _i14 = 0; _i14 < _list13.size; ++_i14) {
                        final String _elem15 = iprot.readString();
                        struct.success.add(_elem15);
                    }
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class getSchema_args implements TBase<getSchema_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getSchema_args() {
        }
        
        public getSchema_args(final getSchema_args other) {
        }
        
        @Override
        public getSchema_args deepCopy() {
            return new getSchema_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getSchema_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getSchema_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getSchema_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getSchema_args && this.equals((getSchema_args)that);
        }
        
        public boolean equals(final getSchema_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getSchema_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final getSchema_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getSchema_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getSchema_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getSchema_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("getSchema_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getSchema_argsStandardSchemeFactory());
            getSchema_args.schemes.put(TupleScheme.class, new getSchema_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getSchema_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class getSchema_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getSchema_argsStandardScheme getScheme() {
                return new getSchema_argsStandardScheme();
            }
        }
        
        private static class getSchema_argsStandardScheme extends StandardScheme<getSchema_args>
        {
            @Override
            public void read(final TProtocol iprot, final getSchema_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final getSchema_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getSchema_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getSchema_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getSchema_argsTupleScheme getScheme() {
                return new getSchema_argsTupleScheme();
            }
        }
        
        private static class getSchema_argsTupleScheme extends TupleScheme<getSchema_args>
        {
            @Override
            public void write(final TProtocol prot, final getSchema_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getSchema_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getSchema_result implements TBase<getSchema_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private Schema success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getSchema_result() {
        }
        
        public getSchema_result(final Schema success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public getSchema_result(final getSchema_result other) {
            if (other.isSetSuccess()) {
                this.success = new Schema(other.success);
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public getSchema_result deepCopy() {
            return new getSchema_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public Schema getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final Schema success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((Schema)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getSchema_result && this.equals((getSchema_result)that);
        }
        
        public boolean equals(final getSchema_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getSchema_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final getSchema_result typedOther = other;
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
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            getSchema_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getSchema_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getSchema_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
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
            STRUCT_DESC = new TStruct("getSchema_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getSchema_resultStandardSchemeFactory());
            getSchema_result.schemes.put(TupleScheme.class, new getSchema_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, Schema.class)));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(getSchema_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class getSchema_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getSchema_resultStandardScheme getScheme() {
                return new getSchema_resultStandardScheme();
            }
        }
        
        private static class getSchema_resultStandardScheme extends StandardScheme<getSchema_result>
        {
            @Override
            public void read(final TProtocol iprot, final getSchema_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new Schema();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final getSchema_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getSchema_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getSchema_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(getSchema_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getSchema_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getSchema_resultTupleScheme getScheme() {
                return new getSchema_resultTupleScheme();
            }
        }
        
        private static class getSchema_resultTupleScheme extends TupleScheme<getSchema_result>
        {
            @Override
            public void write(final TProtocol prot, final getSchema_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getSchema_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    struct.success = new Schema();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class getThriftSchema_args implements TBase<getThriftSchema_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getThriftSchema_args() {
        }
        
        public getThriftSchema_args(final getThriftSchema_args other) {
        }
        
        @Override
        public getThriftSchema_args deepCopy() {
            return new getThriftSchema_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getThriftSchema_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getThriftSchema_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getThriftSchema_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getThriftSchema_args && this.equals((getThriftSchema_args)that);
        }
        
        public boolean equals(final getThriftSchema_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getThriftSchema_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final getThriftSchema_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getThriftSchema_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getThriftSchema_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getThriftSchema_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("getThriftSchema_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getThriftSchema_argsStandardSchemeFactory());
            getThriftSchema_args.schemes.put(TupleScheme.class, new getThriftSchema_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getThriftSchema_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class getThriftSchema_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getThriftSchema_argsStandardScheme getScheme() {
                return new getThriftSchema_argsStandardScheme();
            }
        }
        
        private static class getThriftSchema_argsStandardScheme extends StandardScheme<getThriftSchema_args>
        {
            @Override
            public void read(final TProtocol iprot, final getThriftSchema_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final getThriftSchema_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getThriftSchema_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getThriftSchema_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getThriftSchema_argsTupleScheme getScheme() {
                return new getThriftSchema_argsTupleScheme();
            }
        }
        
        private static class getThriftSchema_argsTupleScheme extends TupleScheme<getThriftSchema_args>
        {
            @Override
            public void write(final TProtocol prot, final getThriftSchema_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getThriftSchema_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getThriftSchema_result implements TBase<getThriftSchema_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private Schema success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getThriftSchema_result() {
        }
        
        public getThriftSchema_result(final Schema success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public getThriftSchema_result(final getThriftSchema_result other) {
            if (other.isSetSuccess()) {
                this.success = new Schema(other.success);
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public getThriftSchema_result deepCopy() {
            return new getThriftSchema_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public Schema getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final Schema success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((Schema)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getThriftSchema_result && this.equals((getThriftSchema_result)that);
        }
        
        public boolean equals(final getThriftSchema_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getThriftSchema_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final getThriftSchema_result typedOther = other;
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
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            getThriftSchema_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getThriftSchema_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getThriftSchema_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
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
            STRUCT_DESC = new TStruct("getThriftSchema_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getThriftSchema_resultStandardSchemeFactory());
            getThriftSchema_result.schemes.put(TupleScheme.class, new getThriftSchema_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, Schema.class)));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(getThriftSchema_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class getThriftSchema_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getThriftSchema_resultStandardScheme getScheme() {
                return new getThriftSchema_resultStandardScheme();
            }
        }
        
        private static class getThriftSchema_resultStandardScheme extends StandardScheme<getThriftSchema_result>
        {
            @Override
            public void read(final TProtocol iprot, final getThriftSchema_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new Schema();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final getThriftSchema_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getThriftSchema_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getThriftSchema_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(getThriftSchema_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getThriftSchema_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getThriftSchema_resultTupleScheme getScheme() {
                return new getThriftSchema_resultTupleScheme();
            }
        }
        
        private static class getThriftSchema_resultTupleScheme extends TupleScheme<getThriftSchema_result>
        {
            @Override
            public void write(final TProtocol prot, final getThriftSchema_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getThriftSchema_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    struct.success = new Schema();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class getClusterStatus_args implements TBase<getClusterStatus_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getClusterStatus_args() {
        }
        
        public getClusterStatus_args(final getClusterStatus_args other) {
        }
        
        @Override
        public getClusterStatus_args deepCopy() {
            return new getClusterStatus_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getClusterStatus_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getClusterStatus_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getClusterStatus_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getClusterStatus_args && this.equals((getClusterStatus_args)that);
        }
        
        public boolean equals(final getClusterStatus_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getClusterStatus_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final getClusterStatus_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getClusterStatus_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getClusterStatus_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getClusterStatus_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("getClusterStatus_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getClusterStatus_argsStandardSchemeFactory());
            getClusterStatus_args.schemes.put(TupleScheme.class, new getClusterStatus_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getClusterStatus_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class getClusterStatus_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getClusterStatus_argsStandardScheme getScheme() {
                return new getClusterStatus_argsStandardScheme();
            }
        }
        
        private static class getClusterStatus_argsStandardScheme extends StandardScheme<getClusterStatus_args>
        {
            @Override
            public void read(final TProtocol iprot, final getClusterStatus_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final getClusterStatus_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getClusterStatus_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getClusterStatus_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getClusterStatus_argsTupleScheme getScheme() {
                return new getClusterStatus_argsTupleScheme();
            }
        }
        
        private static class getClusterStatus_argsTupleScheme extends TupleScheme<getClusterStatus_args>
        {
            @Override
            public void write(final TProtocol prot, final getClusterStatus_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getClusterStatus_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getClusterStatus_result implements TBase<getClusterStatus_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private HiveClusterStatus success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getClusterStatus_result() {
        }
        
        public getClusterStatus_result(final HiveClusterStatus success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public getClusterStatus_result(final getClusterStatus_result other) {
            if (other.isSetSuccess()) {
                this.success = new HiveClusterStatus(other.success);
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public getClusterStatus_result deepCopy() {
            return new getClusterStatus_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public HiveClusterStatus getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final HiveClusterStatus success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((HiveClusterStatus)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getClusterStatus_result && this.equals((getClusterStatus_result)that);
        }
        
        public boolean equals(final getClusterStatus_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getClusterStatus_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final getClusterStatus_result typedOther = other;
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
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            getClusterStatus_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getClusterStatus_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getClusterStatus_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
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
            STRUCT_DESC = new TStruct("getClusterStatus_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getClusterStatus_resultStandardSchemeFactory());
            getClusterStatus_result.schemes.put(TupleScheme.class, new getClusterStatus_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, HiveClusterStatus.class)));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(getClusterStatus_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class getClusterStatus_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getClusterStatus_resultStandardScheme getScheme() {
                return new getClusterStatus_resultStandardScheme();
            }
        }
        
        private static class getClusterStatus_resultStandardScheme extends StandardScheme<getClusterStatus_result>
        {
            @Override
            public void read(final TProtocol iprot, final getClusterStatus_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new HiveClusterStatus();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final getClusterStatus_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getClusterStatus_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getClusterStatus_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(getClusterStatus_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getClusterStatus_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getClusterStatus_resultTupleScheme getScheme() {
                return new getClusterStatus_resultTupleScheme();
            }
        }
        
        private static class getClusterStatus_resultTupleScheme extends TupleScheme<getClusterStatus_result>
        {
            @Override
            public void write(final TProtocol prot, final getClusterStatus_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    struct.success.write(oprot);
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getClusterStatus_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    struct.success = new HiveClusterStatus();
                    struct.success.read(iprot);
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class getQueryPlan_args implements TBase<getQueryPlan_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getQueryPlan_args() {
        }
        
        public getQueryPlan_args(final getQueryPlan_args other) {
        }
        
        @Override
        public getQueryPlan_args deepCopy() {
            return new getQueryPlan_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getQueryPlan_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getQueryPlan_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$getQueryPlan_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getQueryPlan_args && this.equals((getQueryPlan_args)that);
        }
        
        public boolean equals(final getQueryPlan_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getQueryPlan_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final getQueryPlan_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getQueryPlan_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getQueryPlan_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getQueryPlan_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("getQueryPlan_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getQueryPlan_argsStandardSchemeFactory());
            getQueryPlan_args.schemes.put(TupleScheme.class, new getQueryPlan_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getQueryPlan_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class getQueryPlan_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getQueryPlan_argsStandardScheme getScheme() {
                return new getQueryPlan_argsStandardScheme();
            }
        }
        
        private static class getQueryPlan_argsStandardScheme extends StandardScheme<getQueryPlan_args>
        {
            @Override
            public void read(final TProtocol iprot, final getQueryPlan_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final getQueryPlan_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getQueryPlan_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getQueryPlan_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getQueryPlan_argsTupleScheme getScheme() {
                return new getQueryPlan_argsTupleScheme();
            }
        }
        
        private static class getQueryPlan_argsTupleScheme extends TupleScheme<getQueryPlan_args>
        {
            @Override
            public void write(final TProtocol prot, final getQueryPlan_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getQueryPlan_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getQueryPlan_result implements TBase<getQueryPlan_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final TField EX_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        private QueryPlan success;
        private HiveServerException ex;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getQueryPlan_result() {
        }
        
        public getQueryPlan_result(final QueryPlan success, final HiveServerException ex) {
            this();
            this.success = success;
            this.ex = ex;
        }
        
        public getQueryPlan_result(final getQueryPlan_result other) {
            if (other.isSetSuccess()) {
                this.success = new QueryPlan(other.success);
            }
            if (other.isSetEx()) {
                this.ex = new HiveServerException(other.ex);
            }
        }
        
        @Override
        public getQueryPlan_result deepCopy() {
            return new getQueryPlan_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
            this.ex = null;
        }
        
        public QueryPlan getSuccess() {
            return this.success;
        }
        
        public void setSuccess(final QueryPlan success) {
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
        
        public HiveServerException getEx() {
            return this.ex;
        }
        
        public void setEx(final HiveServerException ex) {
            this.ex = ex;
        }
        
        public void unsetEx() {
            this.ex = null;
        }
        
        public boolean isSetEx() {
            return this.ex != null;
        }
        
        public void setExIsSet(final boolean value) {
            if (!value) {
                this.ex = null;
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
                    this.setSuccess((QueryPlan)value);
                    break;
                }
                case EX: {
                    if (value == null) {
                        this.unsetEx();
                        break;
                    }
                    this.setEx((HiveServerException)value);
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
                case EX: {
                    return this.getEx();
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
                case EX: {
                    return this.isSetEx();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getQueryPlan_result && this.equals((getQueryPlan_result)that);
        }
        
        public boolean equals(final getQueryPlan_result that) {
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
            final boolean this_present_ex = this.isSetEx();
            final boolean that_present_ex = that.isSetEx();
            if (this_present_ex || that_present_ex) {
                if (!this_present_ex || !that_present_ex) {
                    return false;
                }
                if (!this.ex.equals(that.ex)) {
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
            final boolean present_ex = this.isSetEx();
            builder.append(present_ex);
            if (present_ex) {
                builder.append(this.ex);
            }
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final getQueryPlan_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            final getQueryPlan_result typedOther = other;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo((Comparable)this.success, (Comparable)typedOther.success);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            lastComparison = Boolean.valueOf(this.isSetEx()).compareTo(Boolean.valueOf(typedOther.isSetEx()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetEx()) {
                lastComparison = TBaseHelper.compareTo(this.ex, typedOther.ex);
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
            getQueryPlan_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getQueryPlan_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getQueryPlan_result(");
            boolean first = true;
            sb.append("success:");
            if (this.success == null) {
                sb.append("null");
            }
            else {
                sb.append(this.success);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("ex:");
            if (this.ex == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ex);
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
            STRUCT_DESC = new TStruct("getQueryPlan_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)12, (short)0);
            EX_FIELD_DESC = new TField("ex", (byte)12, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getQueryPlan_resultStandardSchemeFactory());
            getQueryPlan_result.schemes.put(TupleScheme.class, new getQueryPlan_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new StructMetaData((byte)12, (Class<? extends TBase>)QueryPlan.class)));
            tmpMap.put(_Fields.EX, new FieldMetaData("ex", (byte)3, new FieldValueMetaData((byte)12)));
            FieldMetaData.addStructMetaDataMap(getQueryPlan_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            SUCCESS((short)0, "success"), 
            EX((short)1, "ex");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 0: {
                        return _Fields.SUCCESS;
                    }
                    case 1: {
                        return _Fields.EX;
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
        
        private static class getQueryPlan_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getQueryPlan_resultStandardScheme getScheme() {
                return new getQueryPlan_resultStandardScheme();
            }
        }
        
        private static class getQueryPlan_resultStandardScheme extends StandardScheme<getQueryPlan_result>
        {
            @Override
            public void read(final TProtocol iprot, final getQueryPlan_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 12) {
                                struct.success = new QueryPlan();
                                struct.success.read(iprot);
                                struct.setSuccessIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 1: {
                            if (schemeField.type == 12) {
                                struct.ex = new HiveServerException();
                                struct.ex.read(iprot);
                                struct.setExIsSet(true);
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
            public void write(final TProtocol oprot, final getQueryPlan_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getQueryPlan_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getQueryPlan_result.SUCCESS_FIELD_DESC);
                    struct.success.write(oprot);
                    oprot.writeFieldEnd();
                }
                if (struct.ex != null) {
                    oprot.writeFieldBegin(getQueryPlan_result.EX_FIELD_DESC);
                    struct.ex.write(oprot);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getQueryPlan_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getQueryPlan_resultTupleScheme getScheme() {
                return new getQueryPlan_resultTupleScheme();
            }
        }
        
        private static class getQueryPlan_resultTupleScheme extends TupleScheme<getQueryPlan_result>
        {
            @Override
            public void write(final TProtocol prot, final getQueryPlan_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                if (struct.isSetEx()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetSuccess()) {
                    struct.success.write((TProtocol)oprot);
                }
                if (struct.isSetEx()) {
                    struct.ex.write(oprot);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getQueryPlan_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    struct.success = new QueryPlan();
                    struct.success.read((TProtocol)iprot);
                    struct.setSuccessIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.ex = new HiveServerException();
                    struct.ex.read(iprot);
                    struct.setExIsSet(true);
                }
            }
        }
    }
    
    public static class clean_args implements TBase<clean_args, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public clean_args() {
        }
        
        public clean_args(final clean_args other) {
        }
        
        @Override
        public clean_args deepCopy() {
            return new clean_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$clean_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$clean_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$clean_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof clean_args && this.equals((clean_args)that);
        }
        
        public boolean equals(final clean_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final clean_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final clean_args typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            clean_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            clean_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("clean_args(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("clean_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new clean_argsStandardSchemeFactory());
            clean_args.schemes.put(TupleScheme.class, new clean_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(clean_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class clean_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public clean_argsStandardScheme getScheme() {
                return new clean_argsStandardScheme();
            }
        }
        
        private static class clean_argsStandardScheme extends StandardScheme<clean_args>
        {
            @Override
            public void read(final TProtocol iprot, final clean_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final clean_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(clean_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class clean_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public clean_argsTupleScheme getScheme() {
                return new clean_argsTupleScheme();
            }
        }
        
        private static class clean_argsTupleScheme extends TupleScheme<clean_args>
        {
            @Override
            public void write(final TProtocol prot, final clean_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final clean_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class clean_result implements TBase<clean_result, _Fields>, Serializable, Cloneable
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public clean_result() {
        }
        
        public clean_result(final clean_result other) {
        }
        
        @Override
        public clean_result deepCopy() {
            return new clean_result(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$clean_result$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$clean_result$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = ThriftHive$1.$SwitchMap$org$apache$hadoop$hive$service$ThriftHive$clean_result$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof clean_result && this.equals((clean_result)that);
        }
        
        public boolean equals(final clean_result that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            return builder.toHashCode();
        }
        
        @Override
        public int compareTo(final clean_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            final clean_result typedOther = other;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            clean_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            clean_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("clean_result(");
            final boolean first = true;
            sb.append(")");
            return sb.toString();
        }
        
        public void validate() throws TException {
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
            STRUCT_DESC = new TStruct("clean_result");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new clean_resultStandardSchemeFactory());
            clean_result.schemes.put(TupleScheme.class, new clean_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(clean_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                return null;
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
        
        private static class clean_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public clean_resultStandardScheme getScheme() {
                return new clean_resultStandardScheme();
            }
        }
        
        private static class clean_resultStandardScheme extends StandardScheme<clean_result>
        {
            @Override
            public void read(final TProtocol iprot, final clean_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    final short id = schemeField.id;
                    TProtocolUtil.skip(iprot, schemeField.type);
                    iprot.readFieldEnd();
                }
                iprot.readStructEnd();
                struct.validate();
            }
            
            @Override
            public void write(final TProtocol oprot, final clean_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(clean_result.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class clean_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public clean_resultTupleScheme getScheme() {
                return new clean_resultTupleScheme();
            }
        }
        
        private static class clean_resultTupleScheme extends TupleScheme<clean_result>
        {
            @Override
            public void write(final TProtocol prot, final clean_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final clean_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public interface Iface extends ThriftHiveMetastore.Iface
    {
        void execute(final String p0) throws HiveServerException, TException;
        
        String fetchOne() throws HiveServerException, TException;
        
        List<String> fetchN(final int p0) throws HiveServerException, TException;
        
        List<String> fetchAll() throws HiveServerException, TException;
        
        Schema getSchema() throws HiveServerException, TException;
        
        Schema getThriftSchema() throws HiveServerException, TException;
        
        HiveClusterStatus getClusterStatus() throws HiveServerException, TException;
        
        QueryPlan getQueryPlan() throws HiveServerException, TException;
        
        void clean() throws TException;
    }
    
    public interface AsyncIface extends ThriftHiveMetastore.AsyncIface
    {
        void execute(final String p0, final AsyncMethodCallback<ThriftHive.AsyncClient.execute_call> p1) throws TException;
        
        void fetchOne(final AsyncMethodCallback<ThriftHive.AsyncClient.fetchOne_call> p0) throws TException;
        
        void fetchN(final int p0, final AsyncMethodCallback<ThriftHive.AsyncClient.fetchN_call> p1) throws TException;
        
        void fetchAll(final AsyncMethodCallback<ThriftHive.AsyncClient.fetchAll_call> p0) throws TException;
        
        void getSchema(final AsyncMethodCallback<ThriftHive.AsyncClient.getSchema_call> p0) throws TException;
        
        void getThriftSchema(final AsyncMethodCallback<ThriftHive.AsyncClient.getThriftSchema_call> p0) throws TException;
        
        void getClusterStatus(final AsyncMethodCallback<ThriftHive.AsyncClient.getClusterStatus_call> p0) throws TException;
        
        void getQueryPlan(final AsyncMethodCallback<ThriftHive.AsyncClient.getQueryPlan_call> p0) throws TException;
        
        void clean(final AsyncMethodCallback<ThriftHive.AsyncClient.clean_call> p0) throws TException;
    }
}
