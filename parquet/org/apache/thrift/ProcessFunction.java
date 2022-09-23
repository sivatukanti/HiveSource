// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import parquet.org.apache.thrift.protocol.TProtocolException;
import parquet.org.apache.thrift.protocol.TMessage;
import parquet.org.apache.thrift.protocol.TProtocol;

public abstract class ProcessFunction<I, T extends TBase>
{
    private final String methodName;
    
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
        final TBase result = this.getResult(iface, args);
        oprot.writeMessageBegin(new TMessage(this.getMethodName(), (byte)2, seqid));
        result.write(oprot);
        oprot.writeMessageEnd();
        oprot.getTransport().flush();
    }
    
    protected abstract TBase getResult(final I p0, final T p1) throws TException;
    
    protected abstract T getEmptyArgsInstance();
    
    public String getMethodName() {
        return this.methodName;
    }
}
