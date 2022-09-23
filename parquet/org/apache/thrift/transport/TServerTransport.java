// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

public abstract class TServerTransport
{
    public abstract void listen() throws TTransportException;
    
    public final TTransport accept() throws TTransportException {
        final TTransport transport = this.acceptImpl();
        if (transport == null) {
            throw new TTransportException("accept() may not return NULL");
        }
        return transport;
    }
    
    public abstract void close();
    
    protected abstract TTransport acceptImpl() throws TTransportException;
    
    public void interrupt() {
    }
}
