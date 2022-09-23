// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.thrift.transport.TTransport;
import java.net.SocketException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TServerSocket;

public class TServerSocketKeepAlive extends TServerSocket
{
    public TServerSocketKeepAlive(final int port) throws TTransportException {
        super(port, 0);
    }
    
    @Override
    protected TSocket acceptImpl() throws TTransportException {
        final TSocket ts = super.acceptImpl();
        try {
            ts.getSocket().setKeepAlive(true);
        }
        catch (SocketException e) {
            throw new TTransportException(e);
        }
        return ts;
    }
}
