// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import org.slf4j.LoggerFactory;
import org.apache.thrift.transport.TSaslClientTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSaslServerTransport;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.apache.hive.service.cli.thrift.TCLIService;

public class TSetIpAddressProcessor<I extends TCLIService.Iface> extends TCLIService.Processor<TCLIService.Iface>
{
    private static final Logger LOGGER;
    private static final ThreadLocal<String> THREAD_LOCAL_IP_ADDRESS;
    private static final ThreadLocal<String> THREAD_LOCAL_USER_NAME;
    
    public TSetIpAddressProcessor(final TCLIService.Iface iface) {
        super(iface);
    }
    
    @Override
    public boolean process(final TProtocol in, final TProtocol out) throws TException {
        this.setIpAddress(in);
        this.setUserName(in);
        try {
            return super.process(in, out);
        }
        finally {
            TSetIpAddressProcessor.THREAD_LOCAL_USER_NAME.remove();
            TSetIpAddressProcessor.THREAD_LOCAL_IP_ADDRESS.remove();
        }
    }
    
    private void setUserName(final TProtocol in) {
        final TTransport transport = in.getTransport();
        if (transport instanceof TSaslServerTransport) {
            final String userName = ((TSaslServerTransport)transport).getSaslServer().getAuthorizationID();
            TSetIpAddressProcessor.THREAD_LOCAL_USER_NAME.set(userName);
        }
    }
    
    protected void setIpAddress(final TProtocol in) {
        final TTransport transport = in.getTransport();
        final TSocket tSocket = this.getUnderlyingSocketFromTransport(transport);
        if (tSocket == null) {
            TSetIpAddressProcessor.LOGGER.warn("Unknown Transport, cannot determine ipAddress");
        }
        else {
            TSetIpAddressProcessor.THREAD_LOCAL_IP_ADDRESS.set(tSocket.getSocket().getInetAddress().getHostAddress());
        }
    }
    
    private TSocket getUnderlyingSocketFromTransport(TTransport transport) {
        while (transport != null) {
            if (transport instanceof TSaslServerTransport) {
                transport = ((TSaslServerTransport)transport).getUnderlyingTransport();
            }
            if (transport instanceof TSaslClientTransport) {
                transport = ((TSaslClientTransport)transport).getUnderlyingTransport();
            }
            if (transport instanceof TSocket) {
                return (TSocket)transport;
            }
        }
        return null;
    }
    
    public static String getUserIpAddress() {
        return TSetIpAddressProcessor.THREAD_LOCAL_IP_ADDRESS.get();
    }
    
    public static String getUserName() {
        return TSetIpAddressProcessor.THREAD_LOCAL_USER_NAME.get();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TSetIpAddressProcessor.class.getName());
        THREAD_LOCAL_IP_ADDRESS = new ThreadLocal<String>() {
            @Override
            protected synchronized String initialValue() {
                return null;
            }
        };
        THREAD_LOCAL_USER_NAME = new ThreadLocal<String>() {
            @Override
            protected synchronized String initialValue() {
                return null;
            }
        };
    }
}
