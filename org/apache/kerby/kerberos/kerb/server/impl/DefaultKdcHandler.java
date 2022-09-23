// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.impl;

import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import org.apache.kerby.kerberos.kerb.transport.KrbTcpTransport;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.server.KdcHandler;

public class DefaultKdcHandler extends KdcHandler implements Runnable
{
    private static Logger logger;
    private final KrbTransport transport;
    
    public DefaultKdcHandler(final KdcContext kdcContext, final KrbTransport transport) {
        super(kdcContext);
        this.transport = transport;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                final ByteBuffer message = this.transport.receiveMessage();
                if (message == null) {
                    break;
                }
                this.handleMessage(message);
            }
            DefaultKdcHandler.logger.debug("No valid request recved. Disconnect actively");
            this.transport.release();
        }
        catch (IOException e) {
            this.transport.release();
            DefaultKdcHandler.logger.debug("Transport or decoding error occurred, disconnecting abnormally", e);
        }
    }
    
    protected void handleMessage(final ByteBuffer message) {
        final InetAddress clientAddress = this.transport.getRemoteAddress();
        final boolean isTcp = this.transport instanceof KrbTcpTransport;
        try {
            final ByteBuffer krbResponse = this.handleMessage(message, isTcp, clientAddress);
            this.transport.sendMessage(krbResponse);
        }
        catch (Exception e) {
            this.transport.release();
            DefaultKdcHandler.logger.error("Error occured while processing request:", e);
        }
    }
    
    static {
        DefaultKdcHandler.logger = LoggerFactory.getLogger(DefaultKdcHandler.class);
    }
}
