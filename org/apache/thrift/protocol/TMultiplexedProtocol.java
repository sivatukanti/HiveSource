// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

import org.apache.thrift.TException;

public class TMultiplexedProtocol extends TProtocolDecorator
{
    public static final String SEPARATOR = ":";
    private final String SERVICE_NAME;
    
    public TMultiplexedProtocol(final TProtocol protocol, final String serviceName) {
        super(protocol);
        this.SERVICE_NAME = serviceName;
    }
    
    @Override
    public void writeMessageBegin(final TMessage tMessage) throws TException {
        if (tMessage.type == 1 || tMessage.type == 4) {
            super.writeMessageBegin(new TMessage(this.SERVICE_NAME + ":" + tMessage.name, tMessage.type, tMessage.seqid));
        }
        else {
            super.writeMessageBegin(tMessage);
        }
    }
}
