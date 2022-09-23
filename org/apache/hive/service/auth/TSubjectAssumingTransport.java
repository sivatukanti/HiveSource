// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import java.security.AccessControlContext;
import java.security.PrivilegedActionException;
import org.apache.thrift.transport.TTransportException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import java.security.AccessController;
import org.apache.thrift.transport.TTransport;
import org.apache.hadoop.hive.thrift.TFilterTransport;

public class TSubjectAssumingTransport extends TFilterTransport
{
    public TSubjectAssumingTransport(final TTransport wrapped) {
        super(wrapped);
    }
    
    @Override
    public void open() throws TTransportException {
        try {
            final AccessControlContext context = AccessController.getContext();
            final Subject subject = Subject.getSubject(context);
            Subject.doAs(subject, (PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() {
                    try {
                        TSubjectAssumingTransport.this.wrapped.open();
                    }
                    catch (TTransportException tte) {
                        throw new RuntimeException(tte);
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ioe) {
            throw new RuntimeException("Received an ioe we never threw!", ioe);
        }
        catch (RuntimeException rte) {
            if (rte.getCause() instanceof TTransportException) {
                throw (TTransportException)rte.getCause();
            }
            throw rte;
        }
    }
}
