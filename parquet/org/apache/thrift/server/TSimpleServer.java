// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.server;

import parquet.org.slf4j.LoggerFactory;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TProcessor;
import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.transport.TTransportException;
import parquet.org.slf4j.Logger;

public class TSimpleServer extends TServer
{
    private static final Logger LOGGER;
    private boolean stopped_;
    
    public TSimpleServer(final AbstractServerArgs args) {
        super(args);
        this.stopped_ = false;
    }
    
    @Override
    public void serve() {
        this.stopped_ = false;
        try {
            this.serverTransport_.listen();
        }
        catch (TTransportException ttx) {
            TSimpleServer.LOGGER.error("Error occurred during listening.", ttx);
            return;
        }
        this.setServing(true);
        while (!this.stopped_) {
            TTransport client = null;
            TProcessor processor = null;
            TTransport inputTransport = null;
            TTransport outputTransport = null;
            TProtocol inputProtocol = null;
            TProtocol outputProtocol = null;
            try {
                client = this.serverTransport_.accept();
                if (client != null) {
                    processor = this.processorFactory_.getProcessor(client);
                    inputTransport = this.inputTransportFactory_.getTransport(client);
                    outputTransport = this.outputTransportFactory_.getTransport(client);
                    inputProtocol = this.inputProtocolFactory_.getProtocol(inputTransport);
                    outputProtocol = this.outputProtocolFactory_.getProtocol(outputTransport);
                    while (processor.process(inputProtocol, outputProtocol)) {}
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
