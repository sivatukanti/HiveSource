// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.net.Socket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore;

public class TSetIpAddressProcessor<I extends ThriftHiveMetastore.Iface> extends ThriftHiveMetastore.Processor<ThriftHiveMetastore.Iface>
{
    public TSetIpAddressProcessor(final I iface) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(iface);
    }
    
    @Override
    public boolean process(final TProtocol in, final TProtocol out) throws TException {
        this.setIpAddress(in);
        return super.process(in, out);
    }
    
    protected void setIpAddress(final TProtocol in) {
        final TTransport transport = in.getTransport();
        if (!(transport instanceof TSocket)) {
            return;
        }
        this.setIpAddress(((TSocket)transport).getSocket());
    }
    
    protected void setIpAddress(final Socket inSocket) {
        HiveMetaStore.HMSHandler.setIpAddress(inSocket.getInetAddress().getHostAddress());
    }
}
