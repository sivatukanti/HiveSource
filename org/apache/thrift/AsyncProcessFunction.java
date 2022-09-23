// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.server.AbstractNonblockingServer;
import org.apache.thrift.async.AsyncMethodCallback;

public abstract class AsyncProcessFunction<I, T, R>
{
    final String methodName;
    
    public AsyncProcessFunction(final String methodName) {
        this.methodName = methodName;
    }
    
    protected abstract boolean isOneway();
    
    public abstract void start(final I p0, final T p1, final AsyncMethodCallback<R> p2) throws TException;
    
    public abstract T getEmptyArgsInstance();
    
    public abstract AsyncMethodCallback getResultHandler(final AbstractNonblockingServer.AsyncFrameBuffer p0, final int p1);
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public void sendResponse(final AbstractNonblockingServer.AsyncFrameBuffer fb, final TBase result, final byte type, final int seqid) throws TException {
        final TProtocol oprot = fb.getOutputProtocol();
        oprot.writeMessageBegin(new TMessage(this.getMethodName(), type, seqid));
        result.write(oprot);
        oprot.writeMessageEnd();
        oprot.getTransport().flush();
        fb.responseReady();
    }
}
