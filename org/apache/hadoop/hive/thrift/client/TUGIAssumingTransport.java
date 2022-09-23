// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift.client;

import java.io.IOException;
import org.apache.thrift.transport.TTransportException;
import java.security.PrivilegedExceptionAction;
import org.apache.thrift.transport.TTransport;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.hive.thrift.TFilterTransport;

public class TUGIAssumingTransport extends TFilterTransport
{
    protected UserGroupInformation ugi;
    
    public TUGIAssumingTransport(final TTransport wrapped, final UserGroupInformation ugi) {
        super(wrapped);
        this.ugi = ugi;
    }
    
    @Override
    public void open() throws TTransportException {
        try {
            this.ugi.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() {
                    try {
                        TUGIAssumingTransport.this.wrapped.open();
                    }
                    catch (TTransportException tte) {
                        throw new RuntimeException(tte);
                    }
                    return null;
                }
            });
        }
        catch (IOException ioe) {
            throw new RuntimeException("Received an ioe we never threw!", ioe);
        }
        catch (InterruptedException ie) {
            throw new RuntimeException("Received an ie we never threw!", ie);
        }
        catch (RuntimeException rte) {
            if (rte.getCause() instanceof TTransportException) {
                throw (TTransportException)rte.getCause();
            }
            throw rte;
        }
    }
}
