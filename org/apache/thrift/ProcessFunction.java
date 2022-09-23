// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.slf4j.LoggerFactory;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;

public abstract class ProcessFunction<I, T extends TBase>
{
    private final String methodName;
    private static final Logger LOGGER;
    
    public ProcessFunction(final String methodName) {
        this.methodName = methodName;
    }
    
    public final void process(final int seqid, final TProtocol iprot, final TProtocol oprot, final I iface) throws TException {
        final T args = this.getEmptyArgsInstance();
        try {
            args.read(iprot);
        }
        catch (TProtocolException e) {
            iprot.readMessageEnd();
            final TApplicationException x = new TApplicationException(7, e.getMessage());
            oprot.writeMessageBegin(new TMessage(this.getMethodName(), (byte)3, seqid));
            x.write(oprot);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
            return;
        }
        iprot.readMessageEnd();
        TBase result = null;
        try {
            result = this.getResult(iface, args);
        }
        catch (TException tex) {
            ProcessFunction.LOGGER.error("Internal error processing " + this.getMethodName(), tex);
            final TApplicationException x2 = new TApplicationException(6, "Internal error processing " + this.getMethodName());
            oprot.writeMessageBegin(new TMessage(this.getMethodName(), (byte)3, seqid));
            x2.write(oprot);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
            return;
        }
        if (!this.isOneway()) {
            oprot.writeMessageBegin(new TMessage(this.getMethodName(), (byte)2, seqid));
            result.write(oprot);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
        }
    }
    
    protected abstract boolean isOneway();
    
    public abstract TBase getResult(final I p0, final T p1) throws TException;
    
    public abstract T getEmptyArgsInstance();
    
    public String getMethodName() {
        return this.methodName;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(ProcessFunction.class.getName());
    }
}
