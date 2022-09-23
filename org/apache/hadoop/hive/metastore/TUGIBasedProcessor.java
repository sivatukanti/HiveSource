// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import java.net.Socket;
import java.util.List;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.FileSystem;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore.Processor;
import org.apache.hadoop.hive.thrift.TUGIContainingTransport;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TProtocol;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.thrift.TBase;
import org.apache.thrift.ProcessFunction;
import java.util.Map;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore;

public class TUGIBasedProcessor<I extends ThriftHiveMetastore.Iface> extends TSetIpAddressProcessor<ThriftHiveMetastore.Iface>
{
    private final I iface;
    private final Map<String, ProcessFunction<ThriftHiveMetastore.Iface, ? extends TBase>> functions;
    static final Log LOG;
    
    public TUGIBasedProcessor(final I iface) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(iface);
        this.iface = iface;
        this.functions = this.getProcessMapView();
    }
    
    @Override
    public boolean process(final TProtocol in, final TProtocol out) throws TException {
        this.setIpAddress(in);
        final TMessage msg = in.readMessageBegin();
        final ProcessFunction<ThriftHiveMetastore.Iface, ? extends TBase> fn = this.functions.get(msg.name);
        if (fn == null) {
            TProtocolUtil.skip(in, (byte)12);
            in.readMessageEnd();
            final TApplicationException x = new TApplicationException(1, "Invalid method name: '" + msg.name + "'");
            out.writeMessageBegin(new TMessage(msg.name, (byte)3, msg.seqid));
            x.write(out);
            out.writeMessageEnd();
            out.getTransport().flush();
            return true;
        }
        final TUGIContainingTransport ugiTrans = (TUGIContainingTransport)in.getTransport();
        if (msg.name.equalsIgnoreCase("set_ugi")) {
            try {
                this.handleSetUGI(ugiTrans, (set_ugi)fn, msg, in, out);
            }
            catch (TException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new TException(e2.getCause());
            }
            return true;
        }
        final UserGroupInformation clientUgi = ugiTrans.getClientUGI();
        if (null == clientUgi) {
            fn.process(msg.seqid, in, out, this.iface);
            return true;
        }
        final PrivilegedExceptionAction<Void> pvea = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() {
                try {
                    fn.process(msg.seqid, in, out, TUGIBasedProcessor.this.iface);
                    return null;
                }
                catch (TException te) {
                    throw new RuntimeException(te);
                }
            }
        };
        try {
            clientUgi.doAs(pvea);
            return true;
        }
        catch (RuntimeException rte) {
            if (rte.getCause() instanceof TException) {
                throw (TException)rte.getCause();
            }
            throw rte;
        }
        catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        finally {
            try {
                FileSystem.closeAllForUGI(clientUgi);
            }
            catch (IOException e3) {
                TUGIBasedProcessor.LOG.error("Could not clean up file-system handles for UGI: " + clientUgi, e3);
            }
        }
    }
    
    private void handleSetUGI(final TUGIContainingTransport ugiTrans, final set_ugi<ThriftHiveMetastore.Iface> fn, final TMessage msg, final TProtocol iprot, final TProtocol oprot) throws TException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final UserGroupInformation clientUgi = ugiTrans.getClientUGI();
        if (null != clientUgi) {
            throw new TException(new IllegalStateException("UGI is already set. Resetting is not allowed. Current ugi is: " + clientUgi.getUserName()));
        }
        final ThriftHiveMetastore.set_ugi_args args = fn.getEmptyArgsInstance();
        try {
            args.read(iprot);
        }
        catch (TProtocolException e) {
            iprot.readMessageEnd();
            final TApplicationException x = new TApplicationException(7, e.getMessage());
            oprot.writeMessageBegin(new TMessage(msg.name, (byte)3, msg.seqid));
            x.write(oprot);
            oprot.writeMessageEnd();
            oprot.getTransport().flush();
            return;
        }
        iprot.readMessageEnd();
        final ThriftHiveMetastore.set_ugi_result result = fn.getResult(this.iface, args);
        final List<String> principals = result.getSuccess();
        ugiTrans.setClientUGI(UserGroupInformation.createRemoteUser(principals.remove(principals.size() - 1)));
        oprot.writeMessageBegin(new TMessage(msg.name, (byte)2, msg.seqid));
        result.write(oprot);
        oprot.writeMessageEnd();
        oprot.getTransport().flush();
    }
    
    @Override
    protected void setIpAddress(final TProtocol in) {
        final TUGIContainingTransport ugiTrans = (TUGIContainingTransport)in.getTransport();
        final Socket socket = ugiTrans.getSocket();
        if (socket != null) {
            this.setIpAddress(socket);
        }
    }
    
    static {
        LOG = LogFactory.getLog(TUGIBasedProcessor.class);
    }
}
