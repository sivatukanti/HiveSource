// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.TProcessorFactory;

public abstract class TServer
{
    protected TProcessorFactory processorFactory_;
    protected TServerTransport serverTransport_;
    protected TTransportFactory inputTransportFactory_;
    protected TTransportFactory outputTransportFactory_;
    protected TProtocolFactory inputProtocolFactory_;
    protected TProtocolFactory outputProtocolFactory_;
    private boolean isServing;
    protected TServerEventHandler eventHandler_;
    
    protected TServer(final AbstractServerArgs args) {
        this.processorFactory_ = args.processorFactory;
        this.serverTransport_ = args.serverTransport;
        this.inputTransportFactory_ = args.inputTransportFactory;
        this.outputTransportFactory_ = args.outputTransportFactory;
        this.inputProtocolFactory_ = args.inputProtocolFactory;
        this.outputProtocolFactory_ = args.outputProtocolFactory;
    }
    
    public abstract void serve();
    
    public void stop() {
    }
    
    public boolean isServing() {
        return this.isServing;
    }
    
    protected void setServing(final boolean serving) {
        this.isServing = serving;
    }
    
    public void setServerEventHandler(final TServerEventHandler eventHandler) {
        this.eventHandler_ = eventHandler;
    }
    
    public TServerEventHandler getEventHandler() {
        return this.eventHandler_;
    }
    
    public static class Args extends AbstractServerArgs<Args>
    {
        public Args(final TServerTransport transport) {
            super(transport);
        }
    }
    
    public abstract static class AbstractServerArgs<T extends AbstractServerArgs<T>>
    {
        final TServerTransport serverTransport;
        TProcessorFactory processorFactory;
        TTransportFactory inputTransportFactory;
        TTransportFactory outputTransportFactory;
        TProtocolFactory inputProtocolFactory;
        TProtocolFactory outputProtocolFactory;
        
        public AbstractServerArgs(final TServerTransport transport) {
            this.inputTransportFactory = new TTransportFactory();
            this.outputTransportFactory = new TTransportFactory();
            this.inputProtocolFactory = new TBinaryProtocol.Factory();
            this.outputProtocolFactory = new TBinaryProtocol.Factory();
            this.serverTransport = transport;
        }
        
        public T processorFactory(final TProcessorFactory factory) {
            this.processorFactory = factory;
            return (T)this;
        }
        
        public T processor(final TProcessor processor) {
            this.processorFactory = new TProcessorFactory(processor);
            return (T)this;
        }
        
        public T transportFactory(final TTransportFactory factory) {
            this.inputTransportFactory = factory;
            this.outputTransportFactory = factory;
            return (T)this;
        }
        
        public T inputTransportFactory(final TTransportFactory factory) {
            this.inputTransportFactory = factory;
            return (T)this;
        }
        
        public T outputTransportFactory(final TTransportFactory factory) {
            this.outputTransportFactory = factory;
            return (T)this;
        }
        
        public T protocolFactory(final TProtocolFactory factory) {
            this.inputProtocolFactory = factory;
            this.outputProtocolFactory = factory;
            return (T)this;
        }
        
        public T inputProtocolFactory(final TProtocolFactory factory) {
            this.inputProtocolFactory = factory;
            return (T)this;
        }
        
        public T outputProtocolFactory(final TProtocolFactory factory) {
            this.outputProtocolFactory = factory;
            return (T)this;
        }
    }
}
