// 
// Decompiled by Procyon v0.5.36
// 

package com.facebook.fb303;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
import java.util.BitSet;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
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
import java.util.List;
import java.util.ArrayList;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.server.AbstractNonblockingServer;
import org.apache.thrift.AsyncProcessFunction;
import org.apache.thrift.TBaseAsyncProcessor;
import org.slf4j.LoggerFactory;
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
import java.util.Map;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TServiceClient;

public class FacebookService
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
        public String getName() throws TException {
            this.send_getName();
            return this.recv_getName();
        }
        
        public void send_getName() throws TException {
            final getName_args args = new getName_args();
            this.sendBase("getName", args);
        }
        
        public String recv_getName() throws TException {
            final getName_result result = new getName_result();
            this.receiveBase(result, "getName");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getName failed: unknown result");
        }
        
        @Override
        public String getVersion() throws TException {
            this.send_getVersion();
            return this.recv_getVersion();
        }
        
        public void send_getVersion() throws TException {
            final getVersion_args args = new getVersion_args();
            this.sendBase("getVersion", args);
        }
        
        public String recv_getVersion() throws TException {
            final getVersion_result result = new getVersion_result();
            this.receiveBase(result, "getVersion");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getVersion failed: unknown result");
        }
        
        @Override
        public fb_status getStatus() throws TException {
            this.send_getStatus();
            return this.recv_getStatus();
        }
        
        public void send_getStatus() throws TException {
            final getStatus_args args = new getStatus_args();
            this.sendBase("getStatus", args);
        }
        
        public fb_status recv_getStatus() throws TException {
            final getStatus_result result = new getStatus_result();
            this.receiveBase(result, "getStatus");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getStatus failed: unknown result");
        }
        
        @Override
        public String getStatusDetails() throws TException {
            this.send_getStatusDetails();
            return this.recv_getStatusDetails();
        }
        
        public void send_getStatusDetails() throws TException {
            final getStatusDetails_args args = new getStatusDetails_args();
            this.sendBase("getStatusDetails", args);
        }
        
        public String recv_getStatusDetails() throws TException {
            final getStatusDetails_result result = new getStatusDetails_result();
            this.receiveBase(result, "getStatusDetails");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getStatusDetails failed: unknown result");
        }
        
        @Override
        public Map<String, Long> getCounters() throws TException {
            this.send_getCounters();
            return this.recv_getCounters();
        }
        
        public void send_getCounters() throws TException {
            final getCounters_args args = new getCounters_args();
            this.sendBase("getCounters", args);
        }
        
        public Map<String, Long> recv_getCounters() throws TException {
            final getCounters_result result = new getCounters_result();
            this.receiveBase(result, "getCounters");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getCounters failed: unknown result");
        }
        
        @Override
        public long getCounter(final String key) throws TException {
            this.send_getCounter(key);
            return this.recv_getCounter();
        }
        
        public void send_getCounter(final String key) throws TException {
            final getCounter_args args = new getCounter_args();
            args.setKey(key);
            this.sendBase("getCounter", args);
        }
        
        public long recv_getCounter() throws TException {
            final getCounter_result result = new getCounter_result();
            this.receiveBase(result, "getCounter");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getCounter failed: unknown result");
        }
        
        @Override
        public void setOption(final String key, final String value) throws TException {
            this.send_setOption(key, value);
            this.recv_setOption();
        }
        
        public void send_setOption(final String key, final String value) throws TException {
            final setOption_args args = new setOption_args();
            args.setKey(key);
            args.setValue(value);
            this.sendBase("setOption", args);
        }
        
        public void recv_setOption() throws TException {
            final setOption_result result = new setOption_result();
            this.receiveBase(result, "setOption");
        }
        
        @Override
        public String getOption(final String key) throws TException {
            this.send_getOption(key);
            return this.recv_getOption();
        }
        
        public void send_getOption(final String key) throws TException {
            final getOption_args args = new getOption_args();
            args.setKey(key);
            this.sendBase("getOption", args);
        }
        
        public String recv_getOption() throws TException {
            final getOption_result result = new getOption_result();
            this.receiveBase(result, "getOption");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getOption failed: unknown result");
        }
        
        @Override
        public Map<String, String> getOptions() throws TException {
            this.send_getOptions();
            return this.recv_getOptions();
        }
        
        public void send_getOptions() throws TException {
            final getOptions_args args = new getOptions_args();
            this.sendBase("getOptions", args);
        }
        
        public Map<String, String> recv_getOptions() throws TException {
            final getOptions_result result = new getOptions_result();
            this.receiveBase(result, "getOptions");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getOptions failed: unknown result");
        }
        
        @Override
        public String getCpuProfile(final int profileDurationInSec) throws TException {
            this.send_getCpuProfile(profileDurationInSec);
            return this.recv_getCpuProfile();
        }
        
        public void send_getCpuProfile(final int profileDurationInSec) throws TException {
            final getCpuProfile_args args = new getCpuProfile_args();
            args.setProfileDurationInSec(profileDurationInSec);
            this.sendBase("getCpuProfile", args);
        }
        
        public String recv_getCpuProfile() throws TException {
            final getCpuProfile_result result = new getCpuProfile_result();
            this.receiveBase(result, "getCpuProfile");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "getCpuProfile failed: unknown result");
        }
        
        @Override
        public long aliveSince() throws TException {
            this.send_aliveSince();
            return this.recv_aliveSince();
        }
        
        public void send_aliveSince() throws TException {
            final aliveSince_args args = new aliveSince_args();
            this.sendBase("aliveSince", args);
        }
        
        public long recv_aliveSince() throws TException {
            final aliveSince_result result = new aliveSince_result();
            this.receiveBase(result, "aliveSince");
            if (result.isSetSuccess()) {
                return result.success;
            }
            throw new TApplicationException(5, "aliveSince failed: unknown result");
        }
        
        @Override
        public void reinitialize() throws TException {
            this.send_reinitialize();
        }
        
        public void send_reinitialize() throws TException {
            final reinitialize_args args = new reinitialize_args();
            this.sendBase("reinitialize", args);
        }
        
        @Override
        public void shutdown() throws TException {
            this.send_shutdown();
        }
        
        public void send_shutdown() throws TException {
            final shutdown_args args = new shutdown_args();
            this.sendBase("shutdown", args);
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
        public void getName(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getName_call method_call = new getName_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getVersion(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getVersion_call method_call = new getVersion_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getStatus(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getStatus_call method_call = new getStatus_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getStatusDetails(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getStatusDetails_call method_call = new getStatusDetails_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getCounters(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getCounters_call method_call = new getCounters_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getCounter(final String key, final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getCounter_call method_call = new getCounter_call(key, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void setOption(final String key, final String value, final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final setOption_call method_call = new setOption_call(key, value, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getOption(final String key, final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getOption_call method_call = new getOption_call(key, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getOptions(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getOptions_call method_call = new getOptions_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void getCpuProfile(final int profileDurationInSec, final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final getCpuProfile_call method_call = new getCpuProfile_call(profileDurationInSec, resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void aliveSince(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final aliveSince_call method_call = new aliveSince_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void reinitialize(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final reinitialize_call method_call = new reinitialize_call(resultHandler, this, this.___protocolFactory, this.___transport);
            this.___currentMethod = method_call;
            this.___manager.call(method_call);
        }
        
        @Override
        public void shutdown(final AsyncMethodCallback resultHandler) throws TException {
            this.checkReady();
            final shutdown_call method_call = new shutdown_call(resultHandler, this, this.___protocolFactory, this.___transport);
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
        
        public static class getName_call extends TAsyncMethodCall
        {
            public getName_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getName", (byte)1, 0));
                final getName_args args = new getName_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public String getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getName();
            }
        }
        
        public static class getVersion_call extends TAsyncMethodCall
        {
            public getVersion_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getVersion", (byte)1, 0));
                final getVersion_args args = new getVersion_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public String getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getVersion();
            }
        }
        
        public static class getStatus_call extends TAsyncMethodCall
        {
            public getStatus_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getStatus", (byte)1, 0));
                final getStatus_args args = new getStatus_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public fb_status getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getStatus();
            }
        }
        
        public static class getStatusDetails_call extends TAsyncMethodCall
        {
            public getStatusDetails_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getStatusDetails", (byte)1, 0));
                final getStatusDetails_args args = new getStatusDetails_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public String getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getStatusDetails();
            }
        }
        
        public static class getCounters_call extends TAsyncMethodCall
        {
            public getCounters_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getCounters", (byte)1, 0));
                final getCounters_args args = new getCounters_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public Map<String, Long> getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getCounters();
            }
        }
        
        public static class getCounter_call extends TAsyncMethodCall
        {
            private String key;
            
            public getCounter_call(final String key, final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.key = key;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getCounter", (byte)1, 0));
                final getCounter_args args = new getCounter_args();
                args.setKey(this.key);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public long getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getCounter();
            }
        }
        
        public static class setOption_call extends TAsyncMethodCall
        {
            private String key;
            private String value;
            
            public setOption_call(final String key, final String value, final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.key = key;
                this.value = value;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("setOption", (byte)1, 0));
                final setOption_args args = new setOption_args();
                args.setKey(this.key);
                args.setValue(this.value);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public void getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                new Client(prot).recv_setOption();
            }
        }
        
        public static class getOption_call extends TAsyncMethodCall
        {
            private String key;
            
            public getOption_call(final String key, final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.key = key;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getOption", (byte)1, 0));
                final getOption_args args = new getOption_args();
                args.setKey(this.key);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public String getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getOption();
            }
        }
        
        public static class getOptions_call extends TAsyncMethodCall
        {
            public getOptions_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getOptions", (byte)1, 0));
                final getOptions_args args = new getOptions_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public Map<String, String> getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getOptions();
            }
        }
        
        public static class getCpuProfile_call extends TAsyncMethodCall
        {
            private int profileDurationInSec;
            
            public getCpuProfile_call(final int profileDurationInSec, final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
                this.profileDurationInSec = profileDurationInSec;
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("getCpuProfile", (byte)1, 0));
                final getCpuProfile_args args = new getCpuProfile_args();
                args.setProfileDurationInSec(this.profileDurationInSec);
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public String getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_getCpuProfile();
            }
        }
        
        public static class aliveSince_call extends TAsyncMethodCall
        {
            public aliveSince_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, false);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("aliveSince", (byte)1, 0));
                final aliveSince_args args = new aliveSince_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public long getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
                return new Client(prot).recv_aliveSince();
            }
        }
        
        public static class reinitialize_call extends TAsyncMethodCall
        {
            public reinitialize_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, true);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("reinitialize", (byte)4, 0));
                final reinitialize_args args = new reinitialize_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public void getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
            }
        }
        
        public static class shutdown_call extends TAsyncMethodCall
        {
            public shutdown_call(final AsyncMethodCallback resultHandler, final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport) throws TException {
                super(client, protocolFactory, transport, resultHandler, true);
            }
            
            public void write_args(final TProtocol prot) throws TException {
                prot.writeMessageBegin(new TMessage("shutdown", (byte)4, 0));
                final shutdown_args args = new shutdown_args();
                args.write(prot);
                prot.writeMessageEnd();
            }
            
            public void getResult() throws TException {
                if (this.getState() != State.RESPONSE_READ) {
                    throw new IllegalStateException("Method call not finished!");
                }
                final TMemoryInputTransport memoryTransport = new TMemoryInputTransport(this.getFrameBuffer().array());
                final TProtocol prot = this.client.getProtocolFactory().getProtocol(memoryTransport);
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
            processMap.put("getName", (ProcessFunction<I, ? extends TBase>)new getName());
            processMap.put("getVersion", (ProcessFunction<I, ? extends TBase>)new getVersion());
            processMap.put("getStatus", (ProcessFunction<I, ? extends TBase>)new getStatus());
            processMap.put("getStatusDetails", (ProcessFunction<I, ? extends TBase>)new getStatusDetails());
            processMap.put("getCounters", (ProcessFunction<I, ? extends TBase>)new getCounters());
            processMap.put("getCounter", (ProcessFunction<I, ? extends TBase>)new getCounter());
            processMap.put("setOption", (ProcessFunction<I, ? extends TBase>)new setOption());
            processMap.put("getOption", (ProcessFunction<I, ? extends TBase>)new getOption());
            processMap.put("getOptions", (ProcessFunction<I, ? extends TBase>)new getOptions());
            processMap.put("getCpuProfile", (ProcessFunction<I, ? extends TBase>)new getCpuProfile());
            processMap.put("aliveSince", (ProcessFunction<I, ? extends TBase>)new aliveSince());
            processMap.put("reinitialize", (ProcessFunction<I, ? extends TBase>)new reinitialize());
            processMap.put("shutdown", (ProcessFunction<I, ? extends TBase>)new shutdown());
            return processMap;
        }
        
        static {
            LOGGER = LoggerFactory.getLogger(Processor.class.getName());
        }
        
        public static class getName<I extends Iface> extends ProcessFunction<I, getName_args>
        {
            public getName() {
                super("getName");
            }
            
            @Override
            public getName_args getEmptyArgsInstance() {
                return new getName_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getName_result getResult(final I iface, final getName_args args) throws TException {
                final getName_result result = new getName_result();
                result.success = iface.getName();
                return result;
            }
        }
        
        public static class getVersion<I extends Iface> extends ProcessFunction<I, getVersion_args>
        {
            public getVersion() {
                super("getVersion");
            }
            
            @Override
            public getVersion_args getEmptyArgsInstance() {
                return new getVersion_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getVersion_result getResult(final I iface, final getVersion_args args) throws TException {
                final getVersion_result result = new getVersion_result();
                result.success = iface.getVersion();
                return result;
            }
        }
        
        public static class getStatus<I extends Iface> extends ProcessFunction<I, getStatus_args>
        {
            public getStatus() {
                super("getStatus");
            }
            
            @Override
            public getStatus_args getEmptyArgsInstance() {
                return new getStatus_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getStatus_result getResult(final I iface, final getStatus_args args) throws TException {
                final getStatus_result result = new getStatus_result();
                result.success = iface.getStatus();
                return result;
            }
        }
        
        public static class getStatusDetails<I extends Iface> extends ProcessFunction<I, getStatusDetails_args>
        {
            public getStatusDetails() {
                super("getStatusDetails");
            }
            
            @Override
            public getStatusDetails_args getEmptyArgsInstance() {
                return new getStatusDetails_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getStatusDetails_result getResult(final I iface, final getStatusDetails_args args) throws TException {
                final getStatusDetails_result result = new getStatusDetails_result();
                result.success = iface.getStatusDetails();
                return result;
            }
        }
        
        public static class getCounters<I extends Iface> extends ProcessFunction<I, getCounters_args>
        {
            public getCounters() {
                super("getCounters");
            }
            
            @Override
            public getCounters_args getEmptyArgsInstance() {
                return new getCounters_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getCounters_result getResult(final I iface, final getCounters_args args) throws TException {
                final getCounters_result result = new getCounters_result();
                result.success = iface.getCounters();
                return result;
            }
        }
        
        public static class getCounter<I extends Iface> extends ProcessFunction<I, getCounter_args>
        {
            public getCounter() {
                super("getCounter");
            }
            
            @Override
            public getCounter_args getEmptyArgsInstance() {
                return new getCounter_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getCounter_result getResult(final I iface, final getCounter_args args) throws TException {
                final getCounter_result result = new getCounter_result();
                result.success = iface.getCounter(args.key);
                result.setSuccessIsSet(true);
                return result;
            }
        }
        
        public static class setOption<I extends Iface> extends ProcessFunction<I, setOption_args>
        {
            public setOption() {
                super("setOption");
            }
            
            @Override
            public setOption_args getEmptyArgsInstance() {
                return new setOption_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public setOption_result getResult(final I iface, final setOption_args args) throws TException {
                final setOption_result result = new setOption_result();
                iface.setOption(args.key, args.value);
                return result;
            }
        }
        
        public static class getOption<I extends Iface> extends ProcessFunction<I, getOption_args>
        {
            public getOption() {
                super("getOption");
            }
            
            @Override
            public getOption_args getEmptyArgsInstance() {
                return new getOption_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getOption_result getResult(final I iface, final getOption_args args) throws TException {
                final getOption_result result = new getOption_result();
                result.success = iface.getOption(args.key);
                return result;
            }
        }
        
        public static class getOptions<I extends Iface> extends ProcessFunction<I, getOptions_args>
        {
            public getOptions() {
                super("getOptions");
            }
            
            @Override
            public getOptions_args getEmptyArgsInstance() {
                return new getOptions_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getOptions_result getResult(final I iface, final getOptions_args args) throws TException {
                final getOptions_result result = new getOptions_result();
                result.success = iface.getOptions();
                return result;
            }
        }
        
        public static class getCpuProfile<I extends Iface> extends ProcessFunction<I, getCpuProfile_args>
        {
            public getCpuProfile() {
                super("getCpuProfile");
            }
            
            @Override
            public getCpuProfile_args getEmptyArgsInstance() {
                return new getCpuProfile_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public getCpuProfile_result getResult(final I iface, final getCpuProfile_args args) throws TException {
                final getCpuProfile_result result = new getCpuProfile_result();
                result.success = iface.getCpuProfile(args.profileDurationInSec);
                return result;
            }
        }
        
        public static class aliveSince<I extends Iface> extends ProcessFunction<I, aliveSince_args>
        {
            public aliveSince() {
                super("aliveSince");
            }
            
            @Override
            public aliveSince_args getEmptyArgsInstance() {
                return new aliveSince_args();
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public aliveSince_result getResult(final I iface, final aliveSince_args args) throws TException {
                final aliveSince_result result = new aliveSince_result();
                result.success = iface.aliveSince();
                result.setSuccessIsSet(true);
                return result;
            }
        }
        
        public static class reinitialize<I extends Iface> extends ProcessFunction<I, reinitialize_args>
        {
            public reinitialize() {
                super("reinitialize");
            }
            
            @Override
            public reinitialize_args getEmptyArgsInstance() {
                return new reinitialize_args();
            }
            
            @Override
            protected boolean isOneway() {
                return true;
            }
            
            @Override
            public TBase getResult(final I iface, final reinitialize_args args) throws TException {
                iface.reinitialize();
                return null;
            }
        }
        
        public static class shutdown<I extends Iface> extends ProcessFunction<I, shutdown_args>
        {
            public shutdown() {
                super("shutdown");
            }
            
            @Override
            public shutdown_args getEmptyArgsInstance() {
                return new shutdown_args();
            }
            
            @Override
            protected boolean isOneway() {
                return true;
            }
            
            @Override
            public TBase getResult(final I iface, final shutdown_args args) throws TException {
                iface.shutdown();
                return null;
            }
        }
    }
    
    public static class AsyncProcessor<I extends AsyncIface> extends TBaseAsyncProcessor<I>
    {
        private static final Logger LOGGER;
        
        public AsyncProcessor(final I iface) {
            super(iface, (Map<String, AsyncProcessFunction<I, ? extends TBase, ?>>)getProcessMap(new HashMap<String, AsyncProcessFunction<I, ? extends TBase, ?>>()));
        }
        
        protected AsyncProcessor(final I iface, final Map<String, AsyncProcessFunction<I, ? extends TBase, ?>> processMap) {
            super(iface, (Map<String, AsyncProcessFunction<I, ? extends TBase, ?>>)getProcessMap((Map<String, AsyncProcessFunction<I, ? extends TBase, ?>>)processMap));
        }
        
        private static <I extends AsyncIface> Map<String, AsyncProcessFunction<I, ? extends TBase, ?>> getProcessMap(final Map<String, AsyncProcessFunction<I, ? extends TBase, ?>> processMap) {
            processMap.put("getName", (AsyncProcessFunction<I, ? extends TBase, ?>)new getName());
            processMap.put("getVersion", (AsyncProcessFunction<I, ? extends TBase, ?>)new getVersion());
            processMap.put("getStatus", (AsyncProcessFunction<I, ? extends TBase, ?>)new getStatus());
            processMap.put("getStatusDetails", (AsyncProcessFunction<I, ? extends TBase, ?>)new getStatusDetails());
            processMap.put("getCounters", (AsyncProcessFunction<I, ? extends TBase, ?>)new getCounters());
            processMap.put("getCounter", (AsyncProcessFunction<I, ? extends TBase, ?>)new getCounter());
            processMap.put("setOption", (AsyncProcessFunction<I, ? extends TBase, ?>)new setOption());
            processMap.put("getOption", (AsyncProcessFunction<I, ? extends TBase, ?>)new getOption());
            processMap.put("getOptions", (AsyncProcessFunction<I, ? extends TBase, ?>)new getOptions());
            processMap.put("getCpuProfile", (AsyncProcessFunction<I, ? extends TBase, ?>)new getCpuProfile());
            processMap.put("aliveSince", (AsyncProcessFunction<I, ? extends TBase, ?>)new aliveSince());
            processMap.put("reinitialize", (AsyncProcessFunction<I, ? extends TBase, ?>)new reinitialize());
            processMap.put("shutdown", (AsyncProcessFunction<I, ? extends TBase, ?>)new shutdown());
            return processMap;
        }
        
        static {
            LOGGER = LoggerFactory.getLogger(AsyncProcessor.class.getName());
        }
        
        public static class getName<I extends AsyncIface> extends AsyncProcessFunction<I, getName_args, String>
        {
            public getName() {
                super("getName");
            }
            
            @Override
            public getName_args getEmptyArgsInstance() {
                return new getName_args();
            }
            
            @Override
            public AsyncMethodCallback<String> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<String>() {
                    @Override
                    public void onComplete(final String o) {
                        final getName_result result = new getName_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getName_result result = new getName_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getName_args args, final AsyncMethodCallback<String> resultHandler) throws TException {
                iface.getName(resultHandler);
            }
        }
        
        public static class getVersion<I extends AsyncIface> extends AsyncProcessFunction<I, getVersion_args, String>
        {
            public getVersion() {
                super("getVersion");
            }
            
            @Override
            public getVersion_args getEmptyArgsInstance() {
                return new getVersion_args();
            }
            
            @Override
            public AsyncMethodCallback<String> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<String>() {
                    @Override
                    public void onComplete(final String o) {
                        final getVersion_result result = new getVersion_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getVersion_result result = new getVersion_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getVersion_args args, final AsyncMethodCallback<String> resultHandler) throws TException {
                iface.getVersion(resultHandler);
            }
        }
        
        public static class getStatus<I extends AsyncIface> extends AsyncProcessFunction<I, getStatus_args, fb_status>
        {
            public getStatus() {
                super("getStatus");
            }
            
            @Override
            public getStatus_args getEmptyArgsInstance() {
                return new getStatus_args();
            }
            
            @Override
            public AsyncMethodCallback<fb_status> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<fb_status>() {
                    @Override
                    public void onComplete(final fb_status o) {
                        final getStatus_result result = new getStatus_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getStatus_result result = new getStatus_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getStatus_args args, final AsyncMethodCallback<fb_status> resultHandler) throws TException {
                iface.getStatus(resultHandler);
            }
        }
        
        public static class getStatusDetails<I extends AsyncIface> extends AsyncProcessFunction<I, getStatusDetails_args, String>
        {
            public getStatusDetails() {
                super("getStatusDetails");
            }
            
            @Override
            public getStatusDetails_args getEmptyArgsInstance() {
                return new getStatusDetails_args();
            }
            
            @Override
            public AsyncMethodCallback<String> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<String>() {
                    @Override
                    public void onComplete(final String o) {
                        final getStatusDetails_result result = new getStatusDetails_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getStatusDetails_result result = new getStatusDetails_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getStatusDetails_args args, final AsyncMethodCallback<String> resultHandler) throws TException {
                iface.getStatusDetails(resultHandler);
            }
        }
        
        public static class getCounters<I extends AsyncIface> extends AsyncProcessFunction<I, getCounters_args, Map<String, Long>>
        {
            public getCounters() {
                super("getCounters");
            }
            
            @Override
            public getCounters_args getEmptyArgsInstance() {
                return new getCounters_args();
            }
            
            @Override
            public AsyncMethodCallback<Map<String, Long>> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Map<String, Long>>() {
                    @Override
                    public void onComplete(final Map<String, Long> o) {
                        final getCounters_result result = new getCounters_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getCounters_result result = new getCounters_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getCounters_args args, final AsyncMethodCallback<Map<String, Long>> resultHandler) throws TException {
                iface.getCounters(resultHandler);
            }
        }
        
        public static class getCounter<I extends AsyncIface> extends AsyncProcessFunction<I, getCounter_args, Long>
        {
            public getCounter() {
                super("getCounter");
            }
            
            @Override
            public getCounter_args getEmptyArgsInstance() {
                return new getCounter_args();
            }
            
            @Override
            public AsyncMethodCallback<Long> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Long>() {
                    @Override
                    public void onComplete(final Long o) {
                        final getCounter_result result = new getCounter_result();
                        result.success = o;
                        result.setSuccessIsSet(true);
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getCounter_result result = new getCounter_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getCounter_args args, final AsyncMethodCallback<Long> resultHandler) throws TException {
                iface.getCounter(args.key, resultHandler);
            }
        }
        
        public static class setOption<I extends AsyncIface> extends AsyncProcessFunction<I, setOption_args, Void>
        {
            public setOption() {
                super("setOption");
            }
            
            @Override
            public setOption_args getEmptyArgsInstance() {
                return new setOption_args();
            }
            
            @Override
            public AsyncMethodCallback<Void> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Void>() {
                    @Override
                    public void onComplete(final Void o) {
                        final setOption_result result = new setOption_result();
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final setOption_result result = new setOption_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final setOption_args args, final AsyncMethodCallback<Void> resultHandler) throws TException {
                iface.setOption(args.key, args.value, resultHandler);
            }
        }
        
        public static class getOption<I extends AsyncIface> extends AsyncProcessFunction<I, getOption_args, String>
        {
            public getOption() {
                super("getOption");
            }
            
            @Override
            public getOption_args getEmptyArgsInstance() {
                return new getOption_args();
            }
            
            @Override
            public AsyncMethodCallback<String> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<String>() {
                    @Override
                    public void onComplete(final String o) {
                        final getOption_result result = new getOption_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getOption_result result = new getOption_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getOption_args args, final AsyncMethodCallback<String> resultHandler) throws TException {
                iface.getOption(args.key, resultHandler);
            }
        }
        
        public static class getOptions<I extends AsyncIface> extends AsyncProcessFunction<I, getOptions_args, Map<String, String>>
        {
            public getOptions() {
                super("getOptions");
            }
            
            @Override
            public getOptions_args getEmptyArgsInstance() {
                return new getOptions_args();
            }
            
            @Override
            public AsyncMethodCallback<Map<String, String>> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Map<String, String>>() {
                    @Override
                    public void onComplete(final Map<String, String> o) {
                        final getOptions_result result = new getOptions_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getOptions_result result = new getOptions_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getOptions_args args, final AsyncMethodCallback<Map<String, String>> resultHandler) throws TException {
                iface.getOptions(resultHandler);
            }
        }
        
        public static class getCpuProfile<I extends AsyncIface> extends AsyncProcessFunction<I, getCpuProfile_args, String>
        {
            public getCpuProfile() {
                super("getCpuProfile");
            }
            
            @Override
            public getCpuProfile_args getEmptyArgsInstance() {
                return new getCpuProfile_args();
            }
            
            @Override
            public AsyncMethodCallback<String> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<String>() {
                    @Override
                    public void onComplete(final String o) {
                        final getCpuProfile_result result = new getCpuProfile_result();
                        result.success = o;
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final getCpuProfile_result result = new getCpuProfile_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final getCpuProfile_args args, final AsyncMethodCallback<String> resultHandler) throws TException {
                iface.getCpuProfile(args.profileDurationInSec, resultHandler);
            }
        }
        
        public static class aliveSince<I extends AsyncIface> extends AsyncProcessFunction<I, aliveSince_args, Long>
        {
            public aliveSince() {
                super("aliveSince");
            }
            
            @Override
            public aliveSince_args getEmptyArgsInstance() {
                return new aliveSince_args();
            }
            
            @Override
            public AsyncMethodCallback<Long> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Long>() {
                    @Override
                    public void onComplete(final Long o) {
                        final aliveSince_result result = new aliveSince_result();
                        result.success = o;
                        result.setSuccessIsSet(true);
                        try {
                            fcall.sendResponse(fb, result, (byte)2, seqid);
                        }
                        catch (Exception e) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", e);
                            fb.close();
                        }
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                        byte msgType = 2;
                        final aliveSince_result result = new aliveSince_result();
                        msgType = 3;
                        final TBase msg = (TBase)new TApplicationException(6, e.getMessage());
                        try {
                            fcall.sendResponse(fb, msg, msgType, seqid);
                        }
                        catch (Exception ex) {
                            AsyncProcessor.LOGGER.error("Exception writing to internal frame buffer", ex);
                            fb.close();
                        }
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return false;
            }
            
            @Override
            public void start(final I iface, final aliveSince_args args, final AsyncMethodCallback<Long> resultHandler) throws TException {
                iface.aliveSince(resultHandler);
            }
        }
        
        public static class reinitialize<I extends AsyncIface> extends AsyncProcessFunction<I, reinitialize_args, Void>
        {
            public reinitialize() {
                super("reinitialize");
            }
            
            @Override
            public reinitialize_args getEmptyArgsInstance() {
                return new reinitialize_args();
            }
            
            @Override
            public AsyncMethodCallback<Void> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Void>() {
                    @Override
                    public void onComplete(final Void o) {
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return true;
            }
            
            @Override
            public void start(final I iface, final reinitialize_args args, final AsyncMethodCallback<Void> resultHandler) throws TException {
                iface.reinitialize(resultHandler);
            }
        }
        
        public static class shutdown<I extends AsyncIface> extends AsyncProcessFunction<I, shutdown_args, Void>
        {
            public shutdown() {
                super("shutdown");
            }
            
            @Override
            public shutdown_args getEmptyArgsInstance() {
                return new shutdown_args();
            }
            
            @Override
            public AsyncMethodCallback<Void> getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer fb, final int seqid) {
                final AsyncProcessFunction fcall = this;
                return new AsyncMethodCallback<Void>() {
                    @Override
                    public void onComplete(final Void o) {
                    }
                    
                    @Override
                    public void onError(final Exception e) {
                    }
                };
            }
            
            @Override
            protected boolean isOneway() {
                return true;
            }
            
            @Override
            public void start(final I iface, final shutdown_args args, final AsyncMethodCallback<Void> resultHandler) throws TException {
                iface.shutdown(resultHandler);
            }
        }
    }
    
    public static class getName_args implements TBase<getName_args, _Fields>, Serializable, Cloneable, Comparable<getName_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getName_args() {
        }
        
        public getName_args(final getName_args other) {
        }
        
        @Override
        public getName_args deepCopy() {
            return new getName_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getName_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getName_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getName_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getName_args && this.equals((getName_args)that);
        }
        
        public boolean equals(final getName_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getName_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getName_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getName_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getName_args(");
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
            STRUCT_DESC = new TStruct("getName_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getName_argsStandardSchemeFactory());
            getName_args.schemes.put(TupleScheme.class, new getName_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getName_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getName_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getName_argsStandardScheme getScheme() {
                return new getName_argsStandardScheme();
            }
        }
        
        private static class getName_argsStandardScheme extends StandardScheme<getName_args>
        {
            @Override
            public void read(final TProtocol iprot, final getName_args struct) throws TException {
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
            public void write(final TProtocol oprot, final getName_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getName_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getName_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getName_argsTupleScheme getScheme() {
                return new getName_argsTupleScheme();
            }
        }
        
        private static class getName_argsTupleScheme extends TupleScheme<getName_args>
        {
            @Override
            public void write(final TProtocol prot, final getName_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getName_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getName_result implements TBase<getName_result, _Fields>, Serializable, Cloneable, Comparable<getName_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getName_result() {
        }
        
        public getName_result(final String success) {
            this();
            this.success = success;
        }
        
        public getName_result(final getName_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
        }
        
        @Override
        public getName_result deepCopy() {
            return new getName_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public String getSuccess() {
            return this.success;
        }
        
        public getName_result setSuccess(final String success) {
            this.success = success;
            return this;
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
                    this.setSuccess((String)value);
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
            return that != null && that instanceof getName_result && this.equals((getName_result)that);
        }
        
        public boolean equals(final getName_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getName_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getName_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getName_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getName_result(");
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
            STRUCT_DESC = new TStruct("getName_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)11, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getName_resultStandardSchemeFactory());
            getName_result.schemes.put(TupleScheme.class, new getName_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getName_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getName_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getName_resultStandardScheme getScheme() {
                return new getName_resultStandardScheme();
            }
        }
        
        private static class getName_resultStandardScheme extends StandardScheme<getName_result>
        {
            @Override
            public void read(final TProtocol iprot, final getName_result struct) throws TException {
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
            public void write(final TProtocol oprot, final getName_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getName_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getName_result.SUCCESS_FIELD_DESC);
                    oprot.writeString(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getName_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getName_resultTupleScheme getScheme() {
                return new getName_resultTupleScheme();
            }
        }
        
        private static class getName_resultTupleScheme extends TupleScheme<getName_result>
        {
            @Override
            public void write(final TProtocol prot, final getName_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeString(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getName_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readString();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getVersion_args implements TBase<getVersion_args, _Fields>, Serializable, Cloneable, Comparable<getVersion_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getVersion_args() {
        }
        
        public getVersion_args(final getVersion_args other) {
        }
        
        @Override
        public getVersion_args deepCopy() {
            return new getVersion_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getVersion_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getVersion_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getVersion_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getVersion_args && this.equals((getVersion_args)that);
        }
        
        public boolean equals(final getVersion_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getVersion_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getVersion_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getVersion_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getVersion_args(");
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
            STRUCT_DESC = new TStruct("getVersion_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getVersion_argsStandardSchemeFactory());
            getVersion_args.schemes.put(TupleScheme.class, new getVersion_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getVersion_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getVersion_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getVersion_argsStandardScheme getScheme() {
                return new getVersion_argsStandardScheme();
            }
        }
        
        private static class getVersion_argsStandardScheme extends StandardScheme<getVersion_args>
        {
            @Override
            public void read(final TProtocol iprot, final getVersion_args struct) throws TException {
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
            public void write(final TProtocol oprot, final getVersion_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getVersion_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getVersion_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getVersion_argsTupleScheme getScheme() {
                return new getVersion_argsTupleScheme();
            }
        }
        
        private static class getVersion_argsTupleScheme extends TupleScheme<getVersion_args>
        {
            @Override
            public void write(final TProtocol prot, final getVersion_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getVersion_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getVersion_result implements TBase<getVersion_result, _Fields>, Serializable, Cloneable, Comparable<getVersion_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getVersion_result() {
        }
        
        public getVersion_result(final String success) {
            this();
            this.success = success;
        }
        
        public getVersion_result(final getVersion_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
        }
        
        @Override
        public getVersion_result deepCopy() {
            return new getVersion_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public String getSuccess() {
            return this.success;
        }
        
        public getVersion_result setSuccess(final String success) {
            this.success = success;
            return this;
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
                    this.setSuccess((String)value);
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
            return that != null && that instanceof getVersion_result && this.equals((getVersion_result)that);
        }
        
        public boolean equals(final getVersion_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getVersion_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getVersion_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getVersion_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getVersion_result(");
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
            STRUCT_DESC = new TStruct("getVersion_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)11, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getVersion_resultStandardSchemeFactory());
            getVersion_result.schemes.put(TupleScheme.class, new getVersion_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getVersion_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getVersion_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getVersion_resultStandardScheme getScheme() {
                return new getVersion_resultStandardScheme();
            }
        }
        
        private static class getVersion_resultStandardScheme extends StandardScheme<getVersion_result>
        {
            @Override
            public void read(final TProtocol iprot, final getVersion_result struct) throws TException {
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
            public void write(final TProtocol oprot, final getVersion_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getVersion_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getVersion_result.SUCCESS_FIELD_DESC);
                    oprot.writeString(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getVersion_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getVersion_resultTupleScheme getScheme() {
                return new getVersion_resultTupleScheme();
            }
        }
        
        private static class getVersion_resultTupleScheme extends TupleScheme<getVersion_result>
        {
            @Override
            public void write(final TProtocol prot, final getVersion_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeString(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getVersion_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readString();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getStatus_args implements TBase<getStatus_args, _Fields>, Serializable, Cloneable, Comparable<getStatus_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getStatus_args() {
        }
        
        public getStatus_args(final getStatus_args other) {
        }
        
        @Override
        public getStatus_args deepCopy() {
            return new getStatus_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getStatus_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getStatus_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getStatus_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getStatus_args && this.equals((getStatus_args)that);
        }
        
        public boolean equals(final getStatus_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getStatus_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getStatus_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getStatus_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getStatus_args(");
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
            STRUCT_DESC = new TStruct("getStatus_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getStatus_argsStandardSchemeFactory());
            getStatus_args.schemes.put(TupleScheme.class, new getStatus_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getStatus_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getStatus_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatus_argsStandardScheme getScheme() {
                return new getStatus_argsStandardScheme();
            }
        }
        
        private static class getStatus_argsStandardScheme extends StandardScheme<getStatus_args>
        {
            @Override
            public void read(final TProtocol iprot, final getStatus_args struct) throws TException {
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
            public void write(final TProtocol oprot, final getStatus_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getStatus_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getStatus_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatus_argsTupleScheme getScheme() {
                return new getStatus_argsTupleScheme();
            }
        }
        
        private static class getStatus_argsTupleScheme extends TupleScheme<getStatus_args>
        {
            @Override
            public void write(final TProtocol prot, final getStatus_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getStatus_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getStatus_result implements TBase<getStatus_result, _Fields>, Serializable, Cloneable, Comparable<getStatus_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public fb_status success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getStatus_result() {
        }
        
        public getStatus_result(final fb_status success) {
            this();
            this.success = success;
        }
        
        public getStatus_result(final getStatus_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
        }
        
        @Override
        public getStatus_result deepCopy() {
            return new getStatus_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public fb_status getSuccess() {
            return this.success;
        }
        
        public getStatus_result setSuccess(final fb_status success) {
            this.success = success;
            return this;
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
                    this.setSuccess((fb_status)value);
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
            return that != null && that instanceof getStatus_result && this.equals((getStatus_result)that);
        }
        
        public boolean equals(final getStatus_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success.getValue());
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getStatus_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getStatus_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getStatus_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getStatus_result(");
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
            STRUCT_DESC = new TStruct("getStatus_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)8, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getStatus_resultStandardSchemeFactory());
            getStatus_result.schemes.put(TupleScheme.class, new getStatus_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new EnumMetaData((byte)16, fb_status.class)));
            FieldMetaData.addStructMetaDataMap(getStatus_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getStatus_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatus_resultStandardScheme getScheme() {
                return new getStatus_resultStandardScheme();
            }
        }
        
        private static class getStatus_resultStandardScheme extends StandardScheme<getStatus_result>
        {
            @Override
            public void read(final TProtocol iprot, final getStatus_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 8) {
                                struct.success = fb_status.findByValue(iprot.readI32());
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
            public void write(final TProtocol oprot, final getStatus_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getStatus_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getStatus_result.SUCCESS_FIELD_DESC);
                    oprot.writeI32(struct.success.getValue());
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getStatus_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatus_resultTupleScheme getScheme() {
                return new getStatus_resultTupleScheme();
            }
        }
        
        private static class getStatus_resultTupleScheme extends TupleScheme<getStatus_result>
        {
            @Override
            public void write(final TProtocol prot, final getStatus_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeI32(struct.success.getValue());
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getStatus_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = fb_status.findByValue(iprot.readI32());
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getStatusDetails_args implements TBase<getStatusDetails_args, _Fields>, Serializable, Cloneable, Comparable<getStatusDetails_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getStatusDetails_args() {
        }
        
        public getStatusDetails_args(final getStatusDetails_args other) {
        }
        
        @Override
        public getStatusDetails_args deepCopy() {
            return new getStatusDetails_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getStatusDetails_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getStatusDetails_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getStatusDetails_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getStatusDetails_args && this.equals((getStatusDetails_args)that);
        }
        
        public boolean equals(final getStatusDetails_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getStatusDetails_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getStatusDetails_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getStatusDetails_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getStatusDetails_args(");
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
            STRUCT_DESC = new TStruct("getStatusDetails_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getStatusDetails_argsStandardSchemeFactory());
            getStatusDetails_args.schemes.put(TupleScheme.class, new getStatusDetails_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getStatusDetails_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getStatusDetails_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatusDetails_argsStandardScheme getScheme() {
                return new getStatusDetails_argsStandardScheme();
            }
        }
        
        private static class getStatusDetails_argsStandardScheme extends StandardScheme<getStatusDetails_args>
        {
            @Override
            public void read(final TProtocol iprot, final getStatusDetails_args struct) throws TException {
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
            public void write(final TProtocol oprot, final getStatusDetails_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getStatusDetails_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getStatusDetails_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatusDetails_argsTupleScheme getScheme() {
                return new getStatusDetails_argsTupleScheme();
            }
        }
        
        private static class getStatusDetails_argsTupleScheme extends TupleScheme<getStatusDetails_args>
        {
            @Override
            public void write(final TProtocol prot, final getStatusDetails_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getStatusDetails_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getStatusDetails_result implements TBase<getStatusDetails_result, _Fields>, Serializable, Cloneable, Comparable<getStatusDetails_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getStatusDetails_result() {
        }
        
        public getStatusDetails_result(final String success) {
            this();
            this.success = success;
        }
        
        public getStatusDetails_result(final getStatusDetails_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
        }
        
        @Override
        public getStatusDetails_result deepCopy() {
            return new getStatusDetails_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public String getSuccess() {
            return this.success;
        }
        
        public getStatusDetails_result setSuccess(final String success) {
            this.success = success;
            return this;
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
                    this.setSuccess((String)value);
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
            return that != null && that instanceof getStatusDetails_result && this.equals((getStatusDetails_result)that);
        }
        
        public boolean equals(final getStatusDetails_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getStatusDetails_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getStatusDetails_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getStatusDetails_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getStatusDetails_result(");
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
            STRUCT_DESC = new TStruct("getStatusDetails_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)11, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getStatusDetails_resultStandardSchemeFactory());
            getStatusDetails_result.schemes.put(TupleScheme.class, new getStatusDetails_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getStatusDetails_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getStatusDetails_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatusDetails_resultStandardScheme getScheme() {
                return new getStatusDetails_resultStandardScheme();
            }
        }
        
        private static class getStatusDetails_resultStandardScheme extends StandardScheme<getStatusDetails_result>
        {
            @Override
            public void read(final TProtocol iprot, final getStatusDetails_result struct) throws TException {
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
            public void write(final TProtocol oprot, final getStatusDetails_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getStatusDetails_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getStatusDetails_result.SUCCESS_FIELD_DESC);
                    oprot.writeString(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getStatusDetails_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getStatusDetails_resultTupleScheme getScheme() {
                return new getStatusDetails_resultTupleScheme();
            }
        }
        
        private static class getStatusDetails_resultTupleScheme extends TupleScheme<getStatusDetails_result>
        {
            @Override
            public void write(final TProtocol prot, final getStatusDetails_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeString(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getStatusDetails_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readString();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getCounters_args implements TBase<getCounters_args, _Fields>, Serializable, Cloneable, Comparable<getCounters_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getCounters_args() {
        }
        
        public getCounters_args(final getCounters_args other) {
        }
        
        @Override
        public getCounters_args deepCopy() {
            return new getCounters_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getCounters_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getCounters_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getCounters_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getCounters_args && this.equals((getCounters_args)that);
        }
        
        public boolean equals(final getCounters_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getCounters_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getCounters_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getCounters_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getCounters_args(");
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
            STRUCT_DESC = new TStruct("getCounters_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getCounters_argsStandardSchemeFactory());
            getCounters_args.schemes.put(TupleScheme.class, new getCounters_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getCounters_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getCounters_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounters_argsStandardScheme getScheme() {
                return new getCounters_argsStandardScheme();
            }
        }
        
        private static class getCounters_argsStandardScheme extends StandardScheme<getCounters_args>
        {
            @Override
            public void read(final TProtocol iprot, final getCounters_args struct) throws TException {
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
            public void write(final TProtocol oprot, final getCounters_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getCounters_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getCounters_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounters_argsTupleScheme getScheme() {
                return new getCounters_argsTupleScheme();
            }
        }
        
        private static class getCounters_argsTupleScheme extends TupleScheme<getCounters_args>
        {
            @Override
            public void write(final TProtocol prot, final getCounters_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getCounters_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getCounters_result implements TBase<getCounters_result, _Fields>, Serializable, Cloneable, Comparable<getCounters_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public Map<String, Long> success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getCounters_result() {
        }
        
        public getCounters_result(final Map<String, Long> success) {
            this();
            this.success = success;
        }
        
        public getCounters_result(final getCounters_result other) {
            if (other.isSetSuccess()) {
                final Map<String, Long> __this__success = new HashMap<String, Long>(other.success);
                this.success = __this__success;
            }
        }
        
        @Override
        public getCounters_result deepCopy() {
            return new getCounters_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public int getSuccessSize() {
            return (this.success == null) ? 0 : this.success.size();
        }
        
        public void putToSuccess(final String key, final long val) {
            if (this.success == null) {
                this.success = new HashMap<String, Long>();
            }
            this.success.put(key, val);
        }
        
        public Map<String, Long> getSuccess() {
            return this.success;
        }
        
        public getCounters_result setSuccess(final Map<String, Long> success) {
            this.success = success;
            return this;
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
                    this.setSuccess((Map<String, Long>)value);
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
            return that != null && that instanceof getCounters_result && this.equals((getCounters_result)that);
        }
        
        public boolean equals(final getCounters_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getCounters_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getCounters_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getCounters_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getCounters_result(");
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
            STRUCT_DESC = new TStruct("getCounters_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)13, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getCounters_resultStandardSchemeFactory());
            getCounters_result.schemes.put(TupleScheme.class, new getCounters_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)10))));
            FieldMetaData.addStructMetaDataMap(getCounters_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getCounters_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounters_resultStandardScheme getScheme() {
                return new getCounters_resultStandardScheme();
            }
        }
        
        private static class getCounters_resultStandardScheme extends StandardScheme<getCounters_result>
        {
            @Override
            public void read(final TProtocol iprot, final getCounters_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 13) {
                                final TMap _map0 = iprot.readMapBegin();
                                struct.success = new HashMap<String, Long>(2 * _map0.size);
                                for (int _i3 = 0; _i3 < _map0.size; ++_i3) {
                                    final String _key1 = iprot.readString();
                                    final long _val2 = iprot.readI64();
                                    struct.success.put(_key1, _val2);
                                }
                                iprot.readMapEnd();
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
            public void write(final TProtocol oprot, final getCounters_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getCounters_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getCounters_result.SUCCESS_FIELD_DESC);
                    oprot.writeMapBegin(new TMap((byte)11, (byte)10, struct.success.size()));
                    for (final Map.Entry<String, Long> _iter4 : struct.success.entrySet()) {
                        oprot.writeString(_iter4.getKey());
                        oprot.writeI64(_iter4.getValue());
                    }
                    oprot.writeMapEnd();
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getCounters_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounters_resultTupleScheme getScheme() {
                return new getCounters_resultTupleScheme();
            }
        }
        
        private static class getCounters_resultTupleScheme extends TupleScheme<getCounters_result>
        {
            @Override
            public void write(final TProtocol prot, final getCounters_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeI32(struct.success.size());
                    for (final Map.Entry<String, Long> _iter5 : struct.success.entrySet()) {
                        oprot.writeString(_iter5.getKey());
                        oprot.writeI64(_iter5.getValue());
                    }
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getCounters_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    final TMap _map6 = new TMap((byte)11, (byte)10, iprot.readI32());
                    struct.success = new HashMap<String, Long>(2 * _map6.size);
                    for (int _i9 = 0; _i9 < _map6.size; ++_i9) {
                        final String _key7 = iprot.readString();
                        final long _val8 = iprot.readI64();
                        struct.success.put(_key7, _val8);
                    }
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getCounter_args implements TBase<getCounter_args, _Fields>, Serializable, Cloneable, Comparable<getCounter_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField KEY_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String key;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getCounter_args() {
        }
        
        public getCounter_args(final String key) {
            this();
            this.key = key;
        }
        
        public getCounter_args(final getCounter_args other) {
            if (other.isSetKey()) {
                this.key = other.key;
            }
        }
        
        @Override
        public getCounter_args deepCopy() {
            return new getCounter_args(this);
        }
        
        @Override
        public void clear() {
            this.key = null;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public getCounter_args setKey(final String key) {
            this.key = key;
            return this;
        }
        
        public void unsetKey() {
            this.key = null;
        }
        
        public boolean isSetKey() {
            return this.key != null;
        }
        
        public void setKeyIsSet(final boolean value) {
            if (!value) {
                this.key = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case KEY: {
                    if (value == null) {
                        this.unsetKey();
                        break;
                    }
                    this.setKey((String)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case KEY: {
                    return this.getKey();
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
                case KEY: {
                    return this.isSetKey();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getCounter_args && this.equals((getCounter_args)that);
        }
        
        public boolean equals(final getCounter_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_key = this.isSetKey();
            final boolean that_present_key = that.isSetKey();
            if (this_present_key || that_present_key) {
                if (!this_present_key || !that_present_key) {
                    return false;
                }
                if (!this.key.equals(that.key)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            final boolean present_key = this.isSetKey();
            list.add(present_key);
            if (present_key) {
                list.add(this.key);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getCounter_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetKey()).compareTo(Boolean.valueOf(other.isSetKey()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetKey()) {
                lastComparison = TBaseHelper.compareTo(this.key, other.key);
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
            getCounter_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getCounter_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getCounter_args(");
            boolean first = true;
            sb.append("key:");
            if (this.key == null) {
                sb.append("null");
            }
            else {
                sb.append(this.key);
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
            STRUCT_DESC = new TStruct("getCounter_args");
            KEY_FIELD_DESC = new TField("key", (byte)11, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getCounter_argsStandardSchemeFactory());
            getCounter_args.schemes.put(TupleScheme.class, new getCounter_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.KEY, new FieldMetaData("key", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getCounter_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            KEY((short)1, "key");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.KEY;
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
        
        private static class getCounter_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounter_argsStandardScheme getScheme() {
                return new getCounter_argsStandardScheme();
            }
        }
        
        private static class getCounter_argsStandardScheme extends StandardScheme<getCounter_args>
        {
            @Override
            public void read(final TProtocol iprot, final getCounter_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 11) {
                                struct.key = iprot.readString();
                                struct.setKeyIsSet(true);
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
            public void write(final TProtocol oprot, final getCounter_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getCounter_args.STRUCT_DESC);
                if (struct.key != null) {
                    oprot.writeFieldBegin(getCounter_args.KEY_FIELD_DESC);
                    oprot.writeString(struct.key);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getCounter_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounter_argsTupleScheme getScheme() {
                return new getCounter_argsTupleScheme();
            }
        }
        
        private static class getCounter_argsTupleScheme extends TupleScheme<getCounter_args>
        {
            @Override
            public void write(final TProtocol prot, final getCounter_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetKey()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetKey()) {
                    oprot.writeString(struct.key);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getCounter_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.key = iprot.readString();
                    struct.setKeyIsSet(true);
                }
            }
        }
    }
    
    public static class getCounter_result implements TBase<getCounter_result, _Fields>, Serializable, Cloneable, Comparable<getCounter_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public long success;
        private static final int __SUCCESS_ISSET_ID = 0;
        private byte __isset_bitfield;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getCounter_result() {
            this.__isset_bitfield = 0;
        }
        
        public getCounter_result(final long success) {
            this();
            this.success = success;
            this.setSuccessIsSet(true);
        }
        
        public getCounter_result(final getCounter_result other) {
            this.__isset_bitfield = 0;
            this.__isset_bitfield = other.__isset_bitfield;
            this.success = other.success;
        }
        
        @Override
        public getCounter_result deepCopy() {
            return new getCounter_result(this);
        }
        
        @Override
        public void clear() {
            this.setSuccessIsSet(false);
            this.success = 0L;
        }
        
        public long getSuccess() {
            return this.success;
        }
        
        public getCounter_result setSuccess(final long success) {
            this.success = success;
            this.setSuccessIsSet(true);
            return this;
        }
        
        public void unsetSuccess() {
            this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
        }
        
        public boolean isSetSuccess() {
            return EncodingUtils.testBit(this.__isset_bitfield, 0);
        }
        
        public void setSuccessIsSet(final boolean value) {
            this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((long)value);
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
            return that != null && that instanceof getCounter_result && this.equals((getCounter_result)that);
        }
        
        public boolean equals(final getCounter_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = true;
            final boolean that_present_success = true;
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (this.success != that.success) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = true;
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getCounter_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getCounter_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getCounter_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getCounter_result(");
            boolean first = true;
            sb.append("success:");
            sb.append(this.success);
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
            STRUCT_DESC = new TStruct("getCounter_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)10, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getCounter_resultStandardSchemeFactory());
            getCounter_result.schemes.put(TupleScheme.class, new getCounter_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)10)));
            FieldMetaData.addStructMetaDataMap(getCounter_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getCounter_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounter_resultStandardScheme getScheme() {
                return new getCounter_resultStandardScheme();
            }
        }
        
        private static class getCounter_resultStandardScheme extends StandardScheme<getCounter_result>
        {
            @Override
            public void read(final TProtocol iprot, final getCounter_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 10) {
                                struct.success = iprot.readI64();
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
            public void write(final TProtocol oprot, final getCounter_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getCounter_result.STRUCT_DESC);
                if (struct.isSetSuccess()) {
                    oprot.writeFieldBegin(getCounter_result.SUCCESS_FIELD_DESC);
                    oprot.writeI64(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getCounter_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getCounter_resultTupleScheme getScheme() {
                return new getCounter_resultTupleScheme();
            }
        }
        
        private static class getCounter_resultTupleScheme extends TupleScheme<getCounter_result>
        {
            @Override
            public void write(final TProtocol prot, final getCounter_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeI64(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getCounter_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readI64();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class setOption_args implements TBase<setOption_args, _Fields>, Serializable, Cloneable, Comparable<setOption_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField KEY_FIELD_DESC;
        private static final TField VALUE_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String key;
        public String value;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public setOption_args() {
        }
        
        public setOption_args(final String key, final String value) {
            this();
            this.key = key;
            this.value = value;
        }
        
        public setOption_args(final setOption_args other) {
            if (other.isSetKey()) {
                this.key = other.key;
            }
            if (other.isSetValue()) {
                this.value = other.value;
            }
        }
        
        @Override
        public setOption_args deepCopy() {
            return new setOption_args(this);
        }
        
        @Override
        public void clear() {
            this.key = null;
            this.value = null;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public setOption_args setKey(final String key) {
            this.key = key;
            return this;
        }
        
        public void unsetKey() {
            this.key = null;
        }
        
        public boolean isSetKey() {
            return this.key != null;
        }
        
        public void setKeyIsSet(final boolean value) {
            if (!value) {
                this.key = null;
            }
        }
        
        public String getValue() {
            return this.value;
        }
        
        public setOption_args setValue(final String value) {
            this.value = value;
            return this;
        }
        
        public void unsetValue() {
            this.value = null;
        }
        
        public boolean isSetValue() {
            return this.value != null;
        }
        
        public void setValueIsSet(final boolean value) {
            if (!value) {
                this.value = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case KEY: {
                    if (value == null) {
                        this.unsetKey();
                        break;
                    }
                    this.setKey((String)value);
                    break;
                }
                case VALUE: {
                    if (value == null) {
                        this.unsetValue();
                        break;
                    }
                    this.setValue((String)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case KEY: {
                    return this.getKey();
                }
                case VALUE: {
                    return this.getValue();
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
                case KEY: {
                    return this.isSetKey();
                }
                case VALUE: {
                    return this.isSetValue();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof setOption_args && this.equals((setOption_args)that);
        }
        
        public boolean equals(final setOption_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_key = this.isSetKey();
            final boolean that_present_key = that.isSetKey();
            if (this_present_key || that_present_key) {
                if (!this_present_key || !that_present_key) {
                    return false;
                }
                if (!this.key.equals(that.key)) {
                    return false;
                }
            }
            final boolean this_present_value = this.isSetValue();
            final boolean that_present_value = that.isSetValue();
            if (this_present_value || that_present_value) {
                if (!this_present_value || !that_present_value) {
                    return false;
                }
                if (!this.value.equals(that.value)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            final boolean present_key = this.isSetKey();
            list.add(present_key);
            if (present_key) {
                list.add(this.key);
            }
            final boolean present_value = this.isSetValue();
            list.add(present_value);
            if (present_value) {
                list.add(this.value);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final setOption_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetKey()).compareTo(Boolean.valueOf(other.isSetKey()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetKey()) {
                lastComparison = TBaseHelper.compareTo(this.key, other.key);
                if (lastComparison != 0) {
                    return lastComparison;
                }
            }
            lastComparison = Boolean.valueOf(this.isSetValue()).compareTo(Boolean.valueOf(other.isSetValue()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetValue()) {
                lastComparison = TBaseHelper.compareTo(this.value, other.value);
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
            setOption_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            setOption_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("setOption_args(");
            boolean first = true;
            sb.append("key:");
            if (this.key == null) {
                sb.append("null");
            }
            else {
                sb.append(this.key);
            }
            first = false;
            if (!first) {
                sb.append(", ");
            }
            sb.append("value:");
            if (this.value == null) {
                sb.append("null");
            }
            else {
                sb.append(this.value);
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
            STRUCT_DESC = new TStruct("setOption_args");
            KEY_FIELD_DESC = new TField("key", (byte)11, (short)1);
            VALUE_FIELD_DESC = new TField("value", (byte)11, (short)2);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new setOption_argsStandardSchemeFactory());
            setOption_args.schemes.put(TupleScheme.class, new setOption_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.KEY, new FieldMetaData("key", (byte)3, new FieldValueMetaData((byte)11)));
            tmpMap.put(_Fields.VALUE, new FieldMetaData("value", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(setOption_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            KEY((short)1, "key"), 
            VALUE((short)2, "value");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.KEY;
                    }
                    case 2: {
                        return _Fields.VALUE;
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
        
        private static class setOption_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public setOption_argsStandardScheme getScheme() {
                return new setOption_argsStandardScheme();
            }
        }
        
        private static class setOption_argsStandardScheme extends StandardScheme<setOption_args>
        {
            @Override
            public void read(final TProtocol iprot, final setOption_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 11) {
                                struct.key = iprot.readString();
                                struct.setKeyIsSet(true);
                                break;
                            }
                            TProtocolUtil.skip(iprot, schemeField.type);
                            break;
                        }
                        case 2: {
                            if (schemeField.type == 11) {
                                struct.value = iprot.readString();
                                struct.setValueIsSet(true);
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
            public void write(final TProtocol oprot, final setOption_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(setOption_args.STRUCT_DESC);
                if (struct.key != null) {
                    oprot.writeFieldBegin(setOption_args.KEY_FIELD_DESC);
                    oprot.writeString(struct.key);
                    oprot.writeFieldEnd();
                }
                if (struct.value != null) {
                    oprot.writeFieldBegin(setOption_args.VALUE_FIELD_DESC);
                    oprot.writeString(struct.value);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class setOption_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public setOption_argsTupleScheme getScheme() {
                return new setOption_argsTupleScheme();
            }
        }
        
        private static class setOption_argsTupleScheme extends TupleScheme<setOption_args>
        {
            @Override
            public void write(final TProtocol prot, final setOption_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetKey()) {
                    optionals.set(0);
                }
                if (struct.isSetValue()) {
                    optionals.set(1);
                }
                oprot.writeBitSet(optionals, 2);
                if (struct.isSetKey()) {
                    oprot.writeString(struct.key);
                }
                if (struct.isSetValue()) {
                    oprot.writeString(struct.value);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final setOption_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(2);
                if (incoming.get(0)) {
                    struct.key = iprot.readString();
                    struct.setKeyIsSet(true);
                }
                if (incoming.get(1)) {
                    struct.value = iprot.readString();
                    struct.setValueIsSet(true);
                }
            }
        }
    }
    
    public static class setOption_result implements TBase<setOption_result, _Fields>, Serializable, Cloneable, Comparable<setOption_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public setOption_result() {
        }
        
        public setOption_result(final setOption_result other) {
        }
        
        @Override
        public setOption_result deepCopy() {
            return new setOption_result(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$setOption_result$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$setOption_result$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$setOption_result$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof setOption_result && this.equals((setOption_result)that);
        }
        
        public boolean equals(final setOption_result that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final setOption_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            setOption_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            setOption_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("setOption_result(");
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
            STRUCT_DESC = new TStruct("setOption_result");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new setOption_resultStandardSchemeFactory());
            setOption_result.schemes.put(TupleScheme.class, new setOption_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(setOption_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class setOption_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public setOption_resultStandardScheme getScheme() {
                return new setOption_resultStandardScheme();
            }
        }
        
        private static class setOption_resultStandardScheme extends StandardScheme<setOption_result>
        {
            @Override
            public void read(final TProtocol iprot, final setOption_result struct) throws TException {
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
            public void write(final TProtocol oprot, final setOption_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(setOption_result.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class setOption_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public setOption_resultTupleScheme getScheme() {
                return new setOption_resultTupleScheme();
            }
        }
        
        private static class setOption_resultTupleScheme extends TupleScheme<setOption_result>
        {
            @Override
            public void write(final TProtocol prot, final setOption_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final setOption_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getOption_args implements TBase<getOption_args, _Fields>, Serializable, Cloneable, Comparable<getOption_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField KEY_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String key;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getOption_args() {
        }
        
        public getOption_args(final String key) {
            this();
            this.key = key;
        }
        
        public getOption_args(final getOption_args other) {
            if (other.isSetKey()) {
                this.key = other.key;
            }
        }
        
        @Override
        public getOption_args deepCopy() {
            return new getOption_args(this);
        }
        
        @Override
        public void clear() {
            this.key = null;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public getOption_args setKey(final String key) {
            this.key = key;
            return this;
        }
        
        public void unsetKey() {
            this.key = null;
        }
        
        public boolean isSetKey() {
            return this.key != null;
        }
        
        public void setKeyIsSet(final boolean value) {
            if (!value) {
                this.key = null;
            }
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case KEY: {
                    if (value == null) {
                        this.unsetKey();
                        break;
                    }
                    this.setKey((String)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case KEY: {
                    return this.getKey();
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
                case KEY: {
                    return this.isSetKey();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getOption_args && this.equals((getOption_args)that);
        }
        
        public boolean equals(final getOption_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_key = this.isSetKey();
            final boolean that_present_key = that.isSetKey();
            if (this_present_key || that_present_key) {
                if (!this_present_key || !that_present_key) {
                    return false;
                }
                if (!this.key.equals(that.key)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            final boolean present_key = this.isSetKey();
            list.add(present_key);
            if (present_key) {
                list.add(this.key);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getOption_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetKey()).compareTo(Boolean.valueOf(other.isSetKey()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetKey()) {
                lastComparison = TBaseHelper.compareTo(this.key, other.key);
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
            getOption_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getOption_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getOption_args(");
            boolean first = true;
            sb.append("key:");
            if (this.key == null) {
                sb.append("null");
            }
            else {
                sb.append(this.key);
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
            STRUCT_DESC = new TStruct("getOption_args");
            KEY_FIELD_DESC = new TField("key", (byte)11, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getOption_argsStandardSchemeFactory());
            getOption_args.schemes.put(TupleScheme.class, new getOption_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.KEY, new FieldMetaData("key", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getOption_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            KEY((short)1, "key");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.KEY;
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
        
        private static class getOption_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getOption_argsStandardScheme getScheme() {
                return new getOption_argsStandardScheme();
            }
        }
        
        private static class getOption_argsStandardScheme extends StandardScheme<getOption_args>
        {
            @Override
            public void read(final TProtocol iprot, final getOption_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 11) {
                                struct.key = iprot.readString();
                                struct.setKeyIsSet(true);
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
            public void write(final TProtocol oprot, final getOption_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getOption_args.STRUCT_DESC);
                if (struct.key != null) {
                    oprot.writeFieldBegin(getOption_args.KEY_FIELD_DESC);
                    oprot.writeString(struct.key);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getOption_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getOption_argsTupleScheme getScheme() {
                return new getOption_argsTupleScheme();
            }
        }
        
        private static class getOption_argsTupleScheme extends TupleScheme<getOption_args>
        {
            @Override
            public void write(final TProtocol prot, final getOption_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetKey()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetKey()) {
                    oprot.writeString(struct.key);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getOption_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.key = iprot.readString();
                    struct.setKeyIsSet(true);
                }
            }
        }
    }
    
    public static class getOption_result implements TBase<getOption_result, _Fields>, Serializable, Cloneable, Comparable<getOption_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getOption_result() {
        }
        
        public getOption_result(final String success) {
            this();
            this.success = success;
        }
        
        public getOption_result(final getOption_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
        }
        
        @Override
        public getOption_result deepCopy() {
            return new getOption_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public String getSuccess() {
            return this.success;
        }
        
        public getOption_result setSuccess(final String success) {
            this.success = success;
            return this;
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
                    this.setSuccess((String)value);
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
            return that != null && that instanceof getOption_result && this.equals((getOption_result)that);
        }
        
        public boolean equals(final getOption_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getOption_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getOption_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getOption_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getOption_result(");
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
            STRUCT_DESC = new TStruct("getOption_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)11, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getOption_resultStandardSchemeFactory());
            getOption_result.schemes.put(TupleScheme.class, new getOption_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getOption_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getOption_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getOption_resultStandardScheme getScheme() {
                return new getOption_resultStandardScheme();
            }
        }
        
        private static class getOption_resultStandardScheme extends StandardScheme<getOption_result>
        {
            @Override
            public void read(final TProtocol iprot, final getOption_result struct) throws TException {
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
            public void write(final TProtocol oprot, final getOption_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getOption_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getOption_result.SUCCESS_FIELD_DESC);
                    oprot.writeString(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getOption_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getOption_resultTupleScheme getScheme() {
                return new getOption_resultTupleScheme();
            }
        }
        
        private static class getOption_resultTupleScheme extends TupleScheme<getOption_result>
        {
            @Override
            public void write(final TProtocol prot, final getOption_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeString(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getOption_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readString();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getOptions_args implements TBase<getOptions_args, _Fields>, Serializable, Cloneable, Comparable<getOptions_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getOptions_args() {
        }
        
        public getOptions_args(final getOptions_args other) {
        }
        
        @Override
        public getOptions_args deepCopy() {
            return new getOptions_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getOptions_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getOptions_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$getOptions_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getOptions_args && this.equals((getOptions_args)that);
        }
        
        public boolean equals(final getOptions_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getOptions_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            getOptions_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getOptions_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getOptions_args(");
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
            STRUCT_DESC = new TStruct("getOptions_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getOptions_argsStandardSchemeFactory());
            getOptions_args.schemes.put(TupleScheme.class, new getOptions_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(getOptions_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getOptions_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getOptions_argsStandardScheme getScheme() {
                return new getOptions_argsStandardScheme();
            }
        }
        
        private static class getOptions_argsStandardScheme extends StandardScheme<getOptions_args>
        {
            @Override
            public void read(final TProtocol iprot, final getOptions_args struct) throws TException {
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
            public void write(final TProtocol oprot, final getOptions_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getOptions_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getOptions_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getOptions_argsTupleScheme getScheme() {
                return new getOptions_argsTupleScheme();
            }
        }
        
        private static class getOptions_argsTupleScheme extends TupleScheme<getOptions_args>
        {
            @Override
            public void write(final TProtocol prot, final getOptions_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final getOptions_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class getOptions_result implements TBase<getOptions_result, _Fields>, Serializable, Cloneable, Comparable<getOptions_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public Map<String, String> success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getOptions_result() {
        }
        
        public getOptions_result(final Map<String, String> success) {
            this();
            this.success = success;
        }
        
        public getOptions_result(final getOptions_result other) {
            if (other.isSetSuccess()) {
                final Map<String, String> __this__success = new HashMap<String, String>(other.success);
                this.success = __this__success;
            }
        }
        
        @Override
        public getOptions_result deepCopy() {
            return new getOptions_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public int getSuccessSize() {
            return (this.success == null) ? 0 : this.success.size();
        }
        
        public void putToSuccess(final String key, final String val) {
            if (this.success == null) {
                this.success = new HashMap<String, String>();
            }
            this.success.put(key, val);
        }
        
        public Map<String, String> getSuccess() {
            return this.success;
        }
        
        public getOptions_result setSuccess(final Map<String, String> success) {
            this.success = success;
            return this;
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
                    this.setSuccess((Map<String, String>)value);
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
            return that != null && that instanceof getOptions_result && this.equals((getOptions_result)that);
        }
        
        public boolean equals(final getOptions_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getOptions_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getOptions_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getOptions_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getOptions_result(");
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
            STRUCT_DESC = new TStruct("getOptions_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)13, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getOptions_resultStandardSchemeFactory());
            getOptions_result.schemes.put(TupleScheme.class, new getOptions_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
            FieldMetaData.addStructMetaDataMap(getOptions_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getOptions_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getOptions_resultStandardScheme getScheme() {
                return new getOptions_resultStandardScheme();
            }
        }
        
        private static class getOptions_resultStandardScheme extends StandardScheme<getOptions_result>
        {
            @Override
            public void read(final TProtocol iprot, final getOptions_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 13) {
                                final TMap _map10 = iprot.readMapBegin();
                                struct.success = new HashMap<String, String>(2 * _map10.size);
                                for (int _i13 = 0; _i13 < _map10.size; ++_i13) {
                                    final String _key11 = iprot.readString();
                                    final String _val12 = iprot.readString();
                                    struct.success.put(_key11, _val12);
                                }
                                iprot.readMapEnd();
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
            public void write(final TProtocol oprot, final getOptions_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getOptions_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getOptions_result.SUCCESS_FIELD_DESC);
                    oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.success.size()));
                    for (final Map.Entry<String, String> _iter14 : struct.success.entrySet()) {
                        oprot.writeString(_iter14.getKey());
                        oprot.writeString(_iter14.getValue());
                    }
                    oprot.writeMapEnd();
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getOptions_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getOptions_resultTupleScheme getScheme() {
                return new getOptions_resultTupleScheme();
            }
        }
        
        private static class getOptions_resultTupleScheme extends TupleScheme<getOptions_result>
        {
            @Override
            public void write(final TProtocol prot, final getOptions_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeI32(struct.success.size());
                    for (final Map.Entry<String, String> _iter15 : struct.success.entrySet()) {
                        oprot.writeString(_iter15.getKey());
                        oprot.writeString(_iter15.getValue());
                    }
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getOptions_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    final TMap _map16 = new TMap((byte)11, (byte)11, iprot.readI32());
                    struct.success = new HashMap<String, String>(2 * _map16.size);
                    for (int _i19 = 0; _i19 < _map16.size; ++_i19) {
                        final String _key17 = iprot.readString();
                        final String _val18 = iprot.readString();
                        struct.success.put(_key17, _val18);
                    }
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class getCpuProfile_args implements TBase<getCpuProfile_args, _Fields>, Serializable, Cloneable, Comparable<getCpuProfile_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField PROFILE_DURATION_IN_SEC_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public int profileDurationInSec;
        private static final int __PROFILEDURATIONINSEC_ISSET_ID = 0;
        private byte __isset_bitfield;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getCpuProfile_args() {
            this.__isset_bitfield = 0;
        }
        
        public getCpuProfile_args(final int profileDurationInSec) {
            this();
            this.profileDurationInSec = profileDurationInSec;
            this.setProfileDurationInSecIsSet(true);
        }
        
        public getCpuProfile_args(final getCpuProfile_args other) {
            this.__isset_bitfield = 0;
            this.__isset_bitfield = other.__isset_bitfield;
            this.profileDurationInSec = other.profileDurationInSec;
        }
        
        @Override
        public getCpuProfile_args deepCopy() {
            return new getCpuProfile_args(this);
        }
        
        @Override
        public void clear() {
            this.setProfileDurationInSecIsSet(false);
            this.profileDurationInSec = 0;
        }
        
        public int getProfileDurationInSec() {
            return this.profileDurationInSec;
        }
        
        public getCpuProfile_args setProfileDurationInSec(final int profileDurationInSec) {
            this.profileDurationInSec = profileDurationInSec;
            this.setProfileDurationInSecIsSet(true);
            return this;
        }
        
        public void unsetProfileDurationInSec() {
            this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
        }
        
        public boolean isSetProfileDurationInSec() {
            return EncodingUtils.testBit(this.__isset_bitfield, 0);
        }
        
        public void setProfileDurationInSecIsSet(final boolean value) {
            this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case PROFILE_DURATION_IN_SEC: {
                    if (value == null) {
                        this.unsetProfileDurationInSec();
                        break;
                    }
                    this.setProfileDurationInSec((int)value);
                    break;
                }
            }
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            switch (field) {
                case PROFILE_DURATION_IN_SEC: {
                    return this.getProfileDurationInSec();
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
                case PROFILE_DURATION_IN_SEC: {
                    return this.isSetProfileDurationInSec();
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof getCpuProfile_args && this.equals((getCpuProfile_args)that);
        }
        
        public boolean equals(final getCpuProfile_args that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_profileDurationInSec = true;
            final boolean that_present_profileDurationInSec = true;
            if (this_present_profileDurationInSec || that_present_profileDurationInSec) {
                if (!this_present_profileDurationInSec || !that_present_profileDurationInSec) {
                    return false;
                }
                if (this.profileDurationInSec != that.profileDurationInSec) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            final boolean present_profileDurationInSec = true;
            list.add(present_profileDurationInSec);
            if (present_profileDurationInSec) {
                list.add(this.profileDurationInSec);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getCpuProfile_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetProfileDurationInSec()).compareTo(Boolean.valueOf(other.isSetProfileDurationInSec()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetProfileDurationInSec()) {
                lastComparison = TBaseHelper.compareTo(this.profileDurationInSec, other.profileDurationInSec);
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
            getCpuProfile_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getCpuProfile_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getCpuProfile_args(");
            boolean first = true;
            sb.append("profileDurationInSec:");
            sb.append(this.profileDurationInSec);
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
            STRUCT_DESC = new TStruct("getCpuProfile_args");
            PROFILE_DURATION_IN_SEC_FIELD_DESC = new TField("profileDurationInSec", (byte)8, (short)1);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getCpuProfile_argsStandardSchemeFactory());
            getCpuProfile_args.schemes.put(TupleScheme.class, new getCpuProfile_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.PROFILE_DURATION_IN_SEC, new FieldMetaData("profileDurationInSec", (byte)3, new FieldValueMetaData((byte)8)));
            FieldMetaData.addStructMetaDataMap(getCpuProfile_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
        }
        
        public enum _Fields implements TFieldIdEnum
        {
            PROFILE_DURATION_IN_SEC((short)1, "profileDurationInSec");
            
            private static final Map<String, _Fields> byName;
            private final short _thriftId;
            private final String _fieldName;
            
            public static _Fields findByThriftId(final int fieldId) {
                switch (fieldId) {
                    case 1: {
                        return _Fields.PROFILE_DURATION_IN_SEC;
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
        
        private static class getCpuProfile_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getCpuProfile_argsStandardScheme getScheme() {
                return new getCpuProfile_argsStandardScheme();
            }
        }
        
        private static class getCpuProfile_argsStandardScheme extends StandardScheme<getCpuProfile_args>
        {
            @Override
            public void read(final TProtocol iprot, final getCpuProfile_args struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 1: {
                            if (schemeField.type == 8) {
                                struct.profileDurationInSec = iprot.readI32();
                                struct.setProfileDurationInSecIsSet(true);
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
            public void write(final TProtocol oprot, final getCpuProfile_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getCpuProfile_args.STRUCT_DESC);
                oprot.writeFieldBegin(getCpuProfile_args.PROFILE_DURATION_IN_SEC_FIELD_DESC);
                oprot.writeI32(struct.profileDurationInSec);
                oprot.writeFieldEnd();
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getCpuProfile_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getCpuProfile_argsTupleScheme getScheme() {
                return new getCpuProfile_argsTupleScheme();
            }
        }
        
        private static class getCpuProfile_argsTupleScheme extends TupleScheme<getCpuProfile_args>
        {
            @Override
            public void write(final TProtocol prot, final getCpuProfile_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetProfileDurationInSec()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetProfileDurationInSec()) {
                    oprot.writeI32(struct.profileDurationInSec);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getCpuProfile_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.profileDurationInSec = iprot.readI32();
                    struct.setProfileDurationInSecIsSet(true);
                }
            }
        }
    }
    
    public static class getCpuProfile_result implements TBase<getCpuProfile_result, _Fields>, Serializable, Cloneable, Comparable<getCpuProfile_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public String success;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public getCpuProfile_result() {
        }
        
        public getCpuProfile_result(final String success) {
            this();
            this.success = success;
        }
        
        public getCpuProfile_result(final getCpuProfile_result other) {
            if (other.isSetSuccess()) {
                this.success = other.success;
            }
        }
        
        @Override
        public getCpuProfile_result deepCopy() {
            return new getCpuProfile_result(this);
        }
        
        @Override
        public void clear() {
            this.success = null;
        }
        
        public String getSuccess() {
            return this.success;
        }
        
        public getCpuProfile_result setSuccess(final String success) {
            this.success = success;
            return this;
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
                    this.setSuccess((String)value);
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
            return that != null && that instanceof getCpuProfile_result && this.equals((getCpuProfile_result)that);
        }
        
        public boolean equals(final getCpuProfile_result that) {
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
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = this.isSetSuccess();
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final getCpuProfile_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            getCpuProfile_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            getCpuProfile_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("getCpuProfile_result(");
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
            STRUCT_DESC = new TStruct("getCpuProfile_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)11, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new getCpuProfile_resultStandardSchemeFactory());
            getCpuProfile_result.schemes.put(TupleScheme.class, new getCpuProfile_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)11)));
            FieldMetaData.addStructMetaDataMap(getCpuProfile_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class getCpuProfile_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public getCpuProfile_resultStandardScheme getScheme() {
                return new getCpuProfile_resultStandardScheme();
            }
        }
        
        private static class getCpuProfile_resultStandardScheme extends StandardScheme<getCpuProfile_result>
        {
            @Override
            public void read(final TProtocol iprot, final getCpuProfile_result struct) throws TException {
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
            public void write(final TProtocol oprot, final getCpuProfile_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(getCpuProfile_result.STRUCT_DESC);
                if (struct.success != null) {
                    oprot.writeFieldBegin(getCpuProfile_result.SUCCESS_FIELD_DESC);
                    oprot.writeString(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class getCpuProfile_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public getCpuProfile_resultTupleScheme getScheme() {
                return new getCpuProfile_resultTupleScheme();
            }
        }
        
        private static class getCpuProfile_resultTupleScheme extends TupleScheme<getCpuProfile_result>
        {
            @Override
            public void write(final TProtocol prot, final getCpuProfile_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeString(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final getCpuProfile_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readString();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class aliveSince_args implements TBase<aliveSince_args, _Fields>, Serializable, Cloneable, Comparable<aliveSince_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public aliveSince_args() {
        }
        
        public aliveSince_args(final aliveSince_args other) {
        }
        
        @Override
        public aliveSince_args deepCopy() {
            return new aliveSince_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$aliveSince_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$aliveSince_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$aliveSince_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof aliveSince_args && this.equals((aliveSince_args)that);
        }
        
        public boolean equals(final aliveSince_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final aliveSince_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            aliveSince_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            aliveSince_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("aliveSince_args(");
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
            STRUCT_DESC = new TStruct("aliveSince_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new aliveSince_argsStandardSchemeFactory());
            aliveSince_args.schemes.put(TupleScheme.class, new aliveSince_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(aliveSince_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class aliveSince_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public aliveSince_argsStandardScheme getScheme() {
                return new aliveSince_argsStandardScheme();
            }
        }
        
        private static class aliveSince_argsStandardScheme extends StandardScheme<aliveSince_args>
        {
            @Override
            public void read(final TProtocol iprot, final aliveSince_args struct) throws TException {
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
            public void write(final TProtocol oprot, final aliveSince_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(aliveSince_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class aliveSince_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public aliveSince_argsTupleScheme getScheme() {
                return new aliveSince_argsTupleScheme();
            }
        }
        
        private static class aliveSince_argsTupleScheme extends TupleScheme<aliveSince_args>
        {
            @Override
            public void write(final TProtocol prot, final aliveSince_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final aliveSince_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class aliveSince_result implements TBase<aliveSince_result, _Fields>, Serializable, Cloneable, Comparable<aliveSince_result>
    {
        private static final TStruct STRUCT_DESC;
        private static final TField SUCCESS_FIELD_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public long success;
        private static final int __SUCCESS_ISSET_ID = 0;
        private byte __isset_bitfield;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public aliveSince_result() {
            this.__isset_bitfield = 0;
        }
        
        public aliveSince_result(final long success) {
            this();
            this.success = success;
            this.setSuccessIsSet(true);
        }
        
        public aliveSince_result(final aliveSince_result other) {
            this.__isset_bitfield = 0;
            this.__isset_bitfield = other.__isset_bitfield;
            this.success = other.success;
        }
        
        @Override
        public aliveSince_result deepCopy() {
            return new aliveSince_result(this);
        }
        
        @Override
        public void clear() {
            this.setSuccessIsSet(false);
            this.success = 0L;
        }
        
        public long getSuccess() {
            return this.success;
        }
        
        public aliveSince_result setSuccess(final long success) {
            this.success = success;
            this.setSuccessIsSet(true);
            return this;
        }
        
        public void unsetSuccess() {
            this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
        }
        
        public boolean isSetSuccess() {
            return EncodingUtils.testBit(this.__isset_bitfield, 0);
        }
        
        public void setSuccessIsSet(final boolean value) {
            this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            switch (field) {
                case SUCCESS: {
                    if (value == null) {
                        this.unsetSuccess();
                        break;
                    }
                    this.setSuccess((long)value);
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
            return that != null && that instanceof aliveSince_result && this.equals((aliveSince_result)that);
        }
        
        public boolean equals(final aliveSince_result that) {
            if (that == null) {
                return false;
            }
            final boolean this_present_success = true;
            final boolean that_present_success = true;
            if (this_present_success || that_present_success) {
                if (!this_present_success || !that_present_success) {
                    return false;
                }
                if (this.success != that.success) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            final boolean present_success = true;
            list.add(present_success);
            if (present_success) {
                list.add(this.success);
            }
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final aliveSince_result other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            int lastComparison = 0;
            lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(other.isSetSuccess()));
            if (lastComparison != 0) {
                return lastComparison;
            }
            if (this.isSetSuccess()) {
                lastComparison = TBaseHelper.compareTo(this.success, other.success);
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
            aliveSince_result.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            aliveSince_result.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("aliveSince_result(");
            boolean first = true;
            sb.append("success:");
            sb.append(this.success);
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
            STRUCT_DESC = new TStruct("aliveSince_result");
            SUCCESS_FIELD_DESC = new TField("success", (byte)10, (short)0);
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new aliveSince_resultStandardSchemeFactory());
            aliveSince_result.schemes.put(TupleScheme.class, new aliveSince_resultTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)3, new FieldValueMetaData((byte)10)));
            FieldMetaData.addStructMetaDataMap(aliveSince_result.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class aliveSince_resultStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public aliveSince_resultStandardScheme getScheme() {
                return new aliveSince_resultStandardScheme();
            }
        }
        
        private static class aliveSince_resultStandardScheme extends StandardScheme<aliveSince_result>
        {
            @Override
            public void read(final TProtocol iprot, final aliveSince_result struct) throws TException {
                iprot.readStructBegin();
                while (true) {
                    final TField schemeField = iprot.readFieldBegin();
                    if (schemeField.type == 0) {
                        break;
                    }
                    switch (schemeField.id) {
                        case 0: {
                            if (schemeField.type == 10) {
                                struct.success = iprot.readI64();
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
            public void write(final TProtocol oprot, final aliveSince_result struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(aliveSince_result.STRUCT_DESC);
                if (struct.isSetSuccess()) {
                    oprot.writeFieldBegin(aliveSince_result.SUCCESS_FIELD_DESC);
                    oprot.writeI64(struct.success);
                    oprot.writeFieldEnd();
                }
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class aliveSince_resultTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public aliveSince_resultTupleScheme getScheme() {
                return new aliveSince_resultTupleScheme();
            }
        }
        
        private static class aliveSince_resultTupleScheme extends TupleScheme<aliveSince_result>
        {
            @Override
            public void write(final TProtocol prot, final aliveSince_result struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
                final BitSet optionals = new BitSet();
                if (struct.isSetSuccess()) {
                    optionals.set(0);
                }
                oprot.writeBitSet(optionals, 1);
                if (struct.isSetSuccess()) {
                    oprot.writeI64(struct.success);
                }
            }
            
            @Override
            public void read(final TProtocol prot, final aliveSince_result struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
                final BitSet incoming = iprot.readBitSet(1);
                if (incoming.get(0)) {
                    struct.success = iprot.readI64();
                    struct.setSuccessIsSet(true);
                }
            }
        }
    }
    
    public static class reinitialize_args implements TBase<reinitialize_args, _Fields>, Serializable, Cloneable, Comparable<reinitialize_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public reinitialize_args() {
        }
        
        public reinitialize_args(final reinitialize_args other) {
        }
        
        @Override
        public reinitialize_args deepCopy() {
            return new reinitialize_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$reinitialize_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$reinitialize_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$reinitialize_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof reinitialize_args && this.equals((reinitialize_args)that);
        }
        
        public boolean equals(final reinitialize_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final reinitialize_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            reinitialize_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            reinitialize_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("reinitialize_args(");
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
            STRUCT_DESC = new TStruct("reinitialize_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new reinitialize_argsStandardSchemeFactory());
            reinitialize_args.schemes.put(TupleScheme.class, new reinitialize_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(reinitialize_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class reinitialize_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public reinitialize_argsStandardScheme getScheme() {
                return new reinitialize_argsStandardScheme();
            }
        }
        
        private static class reinitialize_argsStandardScheme extends StandardScheme<reinitialize_args>
        {
            @Override
            public void read(final TProtocol iprot, final reinitialize_args struct) throws TException {
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
            public void write(final TProtocol oprot, final reinitialize_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(reinitialize_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class reinitialize_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public reinitialize_argsTupleScheme getScheme() {
                return new reinitialize_argsTupleScheme();
            }
        }
        
        private static class reinitialize_argsTupleScheme extends TupleScheme<reinitialize_args>
        {
            @Override
            public void write(final TProtocol prot, final reinitialize_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final reinitialize_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public static class shutdown_args implements TBase<shutdown_args, _Fields>, Serializable, Cloneable, Comparable<shutdown_args>
    {
        private static final TStruct STRUCT_DESC;
        private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
        public static final Map<_Fields, FieldMetaData> metaDataMap;
        
        public shutdown_args() {
        }
        
        public shutdown_args(final shutdown_args other) {
        }
        
        @Override
        public shutdown_args deepCopy() {
            return new shutdown_args(this);
        }
        
        @Override
        public void clear() {
        }
        
        @Override
        public void setFieldValue(final _Fields field, final Object value) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$shutdown_args$_Fields[field.ordinal()];
        }
        
        @Override
        public Object getFieldValue(final _Fields field) {
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$shutdown_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean isSet(final _Fields field) {
            if (field == null) {
                throw new IllegalArgumentException();
            }
            final int n = FacebookService$1.$SwitchMap$com$facebook$fb303$FacebookService$shutdown_args$_Fields[field.ordinal()];
            throw new IllegalStateException();
        }
        
        @Override
        public boolean equals(final Object that) {
            return that != null && that instanceof shutdown_args && this.equals((shutdown_args)that);
        }
        
        public boolean equals(final shutdown_args that) {
            return that != null;
        }
        
        @Override
        public int hashCode() {
            final List<Object> list = new ArrayList<Object>();
            return list.hashCode();
        }
        
        @Override
        public int compareTo(final shutdown_args other) {
            if (!this.getClass().equals(other.getClass())) {
                return this.getClass().getName().compareTo(other.getClass().getName());
            }
            final int lastComparison = 0;
            return 0;
        }
        
        @Override
        public _Fields fieldForId(final int fieldId) {
            return _Fields.findByThriftId(fieldId);
        }
        
        @Override
        public void read(final TProtocol iprot) throws TException {
            shutdown_args.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
        }
        
        @Override
        public void write(final TProtocol oprot) throws TException {
            shutdown_args.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("shutdown_args(");
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
            STRUCT_DESC = new TStruct("shutdown_args");
            (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new shutdown_argsStandardSchemeFactory());
            shutdown_args.schemes.put(TupleScheme.class, new shutdown_argsTupleSchemeFactory());
            final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
            FieldMetaData.addStructMetaDataMap(shutdown_args.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
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
        
        private static class shutdown_argsStandardSchemeFactory implements SchemeFactory
        {
            @Override
            public shutdown_argsStandardScheme getScheme() {
                return new shutdown_argsStandardScheme();
            }
        }
        
        private static class shutdown_argsStandardScheme extends StandardScheme<shutdown_args>
        {
            @Override
            public void read(final TProtocol iprot, final shutdown_args struct) throws TException {
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
            public void write(final TProtocol oprot, final shutdown_args struct) throws TException {
                struct.validate();
                oprot.writeStructBegin(shutdown_args.STRUCT_DESC);
                oprot.writeFieldStop();
                oprot.writeStructEnd();
            }
        }
        
        private static class shutdown_argsTupleSchemeFactory implements SchemeFactory
        {
            @Override
            public shutdown_argsTupleScheme getScheme() {
                return new shutdown_argsTupleScheme();
            }
        }
        
        private static class shutdown_argsTupleScheme extends TupleScheme<shutdown_args>
        {
            @Override
            public void write(final TProtocol prot, final shutdown_args struct) throws TException {
                final TTupleProtocol oprot = (TTupleProtocol)prot;
            }
            
            @Override
            public void read(final TProtocol prot, final shutdown_args struct) throws TException {
                final TTupleProtocol iprot = (TTupleProtocol)prot;
            }
        }
    }
    
    public interface AsyncIface
    {
        void getName(final AsyncMethodCallback p0) throws TException;
        
        void getVersion(final AsyncMethodCallback p0) throws TException;
        
        void getStatus(final AsyncMethodCallback p0) throws TException;
        
        void getStatusDetails(final AsyncMethodCallback p0) throws TException;
        
        void getCounters(final AsyncMethodCallback p0) throws TException;
        
        void getCounter(final String p0, final AsyncMethodCallback p1) throws TException;
        
        void setOption(final String p0, final String p1, final AsyncMethodCallback p2) throws TException;
        
        void getOption(final String p0, final AsyncMethodCallback p1) throws TException;
        
        void getOptions(final AsyncMethodCallback p0) throws TException;
        
        void getCpuProfile(final int p0, final AsyncMethodCallback p1) throws TException;
        
        void aliveSince(final AsyncMethodCallback p0) throws TException;
        
        void reinitialize(final AsyncMethodCallback p0) throws TException;
        
        void shutdown(final AsyncMethodCallback p0) throws TException;
    }
    
    public interface Iface
    {
        String getName() throws TException;
        
        String getVersion() throws TException;
        
        fb_status getStatus() throws TException;
        
        String getStatusDetails() throws TException;
        
        Map<String, Long> getCounters() throws TException;
        
        long getCounter(final String p0) throws TException;
        
        void setOption(final String p0, final String p1) throws TException;
        
        String getOption(final String p0) throws TException;
        
        Map<String, String> getOptions() throws TException;
        
        String getCpuProfile(final int p0) throws TException;
        
        long aliveSince() throws TException;
        
        void reinitialize() throws TException;
        
        void shutdown() throws TException;
    }
}
