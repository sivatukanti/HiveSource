// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import org.slf4j.LoggerFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TProcessor;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;

public class TSimpleServer extends TServer
{
    private static final Logger LOGGER;
    private volatile boolean stopped_;
    
    public TSimpleServer(final AbstractServerArgs args) {
        super(args);
        this.stopped_ = false;
    }
    
    @Override
    public void serve() {
        try {
            this.serverTransport_.listen();
        }
        catch (TTransportException ttx) {
            TSimpleServer.LOGGER.error("Error occurred during listening.", ttx);
            return;
        }
        if (this.eventHandler_ != null) {
            this.eventHandler_.preServe();
        }
        this.setServing(true);
        while (!this.stopped_) {
            TTransport client = null;
            TProcessor processor = null;
            TTransport inputTransport = null;
            TTransport outputTransport = null;
            TProtocol inputProtocol = null;
            TProtocol outputProtocol = null;
            ServerContext connectionContext = null;
            try {
                client = this.serverTransport_.accept();
                if (client != null) {
                    processor = this.processorFactory_.getProcessor(client);
                    inputTransport = this.inputTransportFactory_.getTransport(client);
                    outputTransport = this.outputTransportFactory_.getTransport(client);
                    inputProtocol = this.inputProtocolFactory_.getProtocol(inputTransport);
                    outputProtocol = this.outputProtocolFactory_.getProtocol(outputTransport);
                    if (this.eventHandler_ != null) {
                        connectionContext = this.eventHandler_.createContext(inputProtocol, outputProtocol);
                    }
                    do {
                        if (this.eventHandler_ != null) {
                            this.eventHandler_.processContext(connectionContext, inputTransport, outputTransport);
                        }
                    } while (processor.process(inputProtocol, outputProtocol));
                }
            }
            catch (TTransportException ttx2) {}
            catch (TException tx) {
                if (!this.stopped_) {
                    TSimpleServer.LOGGER.error("Thrift error occurred during processing of message.", tx);
                }
            }
            catch (Exception x) {
                if (!this.stopped_) {
                    TSimpleServer.LOGGER.error("Error occurred during processing of message.", x);
                }
            }
            if (this.eventHandler_ != null) {
                this.eventHandler_.deleteContext(connectionContext, inputProtocol, outputProtocol);
            }
            if (inputTransport != null) {
                inputTransport.close();
            }
            if (outputTransport != null) {
                outputTransport.close();
            }
        }
        this.setServing(false);
    }
    
    @Override
    public void stop() {
        this.stopped_ = true;
        this.serverTransport_.interrupt();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TSimpleServer.class.getName());
    }
}
