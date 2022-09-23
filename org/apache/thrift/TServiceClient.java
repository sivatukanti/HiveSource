// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;

public abstract class TServiceClient
{
    protected TProtocol iprot_;
    protected TProtocol oprot_;
    protected int seqid_;
    
    public TServiceClient(final TProtocol prot) {
        this(prot, prot);
    }
    
    public TServiceClient(final TProtocol iprot, final TProtocol oprot) {
        this.iprot_ = iprot;
        this.oprot_ = oprot;
    }
    
    public TProtocol getInputProtocol() {
        return this.iprot_;
    }
    
    public TProtocol getOutputProtocol() {
        return this.oprot_;
    }
    
    protected void sendBase(final String methodName, final TBase args) throws TException {
        this.oprot_.writeMessageBegin(new TMessage(methodName, (byte)1, ++this.seqid_));
        args.write(this.oprot_);
        this.oprot_.writeMessageEnd();
        this.oprot_.getTransport().flush();
    }
    
    protected void receiveBase(final TBase result, final String methodName) throws TException {
        final TMessage msg = this.iprot_.readMessageBegin();
        if (msg.type == 3) {
            final TApplicationException x = TApplicationException.read(this.iprot_);
            this.iprot_.readMessageEnd();
            throw x;
        }
        if (msg.seqid != this.seqid_) {
            throw new TApplicationException(4, methodName + " failed: out of sequence response");
        }
        result.read(this.iprot_);
        this.iprot_.readMessageEnd();
    }
}
