// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.transport.TSocket;
import java.net.Socket;
import org.apache.thrift.transport.TTransport;
import org.apache.hadoop.security.UserGroupInformation;

public class TUGIContainingTransport extends TFilterTransport
{
    private UserGroupInformation ugi;
    
    public TUGIContainingTransport(final TTransport wrapped) {
        super(wrapped);
    }
    
    public UserGroupInformation getClientUGI() {
        return this.ugi;
    }
    
    public void setClientUGI(final UserGroupInformation ugi) {
        this.ugi = ugi;
    }
    
    public Socket getSocket() {
        if (this.wrapped instanceof TSocket) {
            return ((TSocket)this.wrapped).getSocket();
        }
        return null;
    }
    
    public static class Factory extends TTransportFactory
    {
        private static final ConcurrentMap<TTransport, TUGIContainingTransport> transMap;
        
        @Override
        public TUGIContainingTransport getTransport(final TTransport trans) {
            TUGIContainingTransport tugiTrans = Factory.transMap.get(trans);
            if (tugiTrans == null) {
                tugiTrans = new TUGIContainingTransport(trans);
                final TUGIContainingTransport prev = Factory.transMap.putIfAbsent(trans, tugiTrans);
                if (prev != null) {
                    return prev;
                }
            }
            return tugiTrans;
        }
        
        static {
            transMap = new MapMaker().weakKeys().weakValues().makeMap();
        }
    }
}
