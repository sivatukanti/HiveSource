// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import java.util.HashMap;
import java.util.Map;

public class TMultiplexedProcessor implements TProcessor
{
    private final Map<String, TProcessor> SERVICE_PROCESSOR_MAP;
    
    public TMultiplexedProcessor() {
        this.SERVICE_PROCESSOR_MAP = new HashMap<String, TProcessor>();
    }
    
    public void registerProcessor(final String serviceName, final TProcessor processor) {
        this.SERVICE_PROCESSOR_MAP.put(serviceName, processor);
    }
    
    public boolean process(final TProtocol iprot, final TProtocol oprot) throws TException {
        final TMessage message = iprot.readMessageBegin();
        if (message.type != 1 && message.type != 4) {
            throw new TException("This should not have happened!?");
        }
        final int index = message.name.indexOf(":");
        if (index < 0) {
            throw new TException("Service name not found in message name: " + message.name + ".  Did you " + "forget to use a TMultiplexProtocol in your client?");
        }
        final String serviceName = message.name.substring(0, index);
        final TProcessor actualProcessor = this.SERVICE_PROCESSOR_MAP.get(serviceName);
        if (actualProcessor == null) {
            throw new TException("Service name not found: " + serviceName + ".  Did you forget " + "to call registerProcessor()?");
        }
        final TMessage standardMessage = new TMessage(message.name.substring(serviceName.length() + ":".length()), message.type, message.seqid);
        return actualProcessor.process(new StoredMessageProtocol(iprot, standardMessage), oprot);
    }
    
    private static class StoredMessageProtocol extends TProtocolDecorator
    {
        TMessage messageBegin;
        
        public StoredMessageProtocol(final TProtocol protocol, final TMessage messageBegin) {
            super(protocol);
            this.messageBegin = messageBegin;
        }
        
        @Override
        public TMessage readMessageBegin() throws TException {
            return this.messageBegin;
        }
    }
}
