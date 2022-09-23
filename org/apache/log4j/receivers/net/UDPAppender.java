// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.net.DatagramPacket;
import org.apache.log4j.spi.LoggingEvent;
import java.io.IOException;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.xml.XMLLayout;
import java.net.UnknownHostException;
import org.apache.log4j.net.ZeroConfSupport;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.apache.log4j.AppenderSkeleton;

public class UDPAppender extends AppenderSkeleton implements PortBased
{
    public static final int DEFAULT_PORT = 9991;
    String hostname;
    String remoteHost;
    String application;
    String encoding;
    InetAddress address;
    int port;
    DatagramSocket outSocket;
    public static final String ZONE = "_log4j_xml_udp_appender.local.";
    boolean inError;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    
    public UDPAppender() {
        super(false);
        this.port = 9991;
        this.inError = false;
    }
    
    public UDPAppender(final InetAddress address, final int port) {
        super(false);
        this.port = 9991;
        this.inError = false;
        this.address = address;
        this.remoteHost = address.getHostName();
        this.port = port;
        this.activateOptions();
    }
    
    public UDPAppender(final String host, final int port) {
        super(false);
        this.port = 9991;
        this.inError = false;
        this.port = port;
        this.address = this.getAddressByName(host);
        this.remoteHost = host;
        this.activateOptions();
    }
    
    public void activateOptions() {
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException uhe) {
            try {
                this.hostname = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException uhe2) {
                this.hostname = "unknown";
            }
        }
        if (this.application == null) {
            this.application = System.getProperty("application");
        }
        else if (System.getProperty("application") != null) {
            this.application = this.application + "-" + System.getProperty("application");
        }
        if (this.remoteHost != null) {
            this.connect(this.address = this.getAddressByName(this.remoteHost), this.port);
            if (this.layout == null) {
                this.layout = new XMLLayout();
            }
            if (this.advertiseViaMulticastDNS) {
                (this.zeroConf = new ZeroConfSupport("_log4j_xml_udp_appender.local.", this.port, this.getName())).advertise();
            }
            super.activateOptions();
            return;
        }
        final String err = "The RemoteHost property is required for SocketAppender named " + this.name;
        LogLog.error(err);
        throw new IllegalStateException(err);
    }
    
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
        this.closed = true;
        this.cleanUp();
    }
    
    public void cleanUp() {
        if (this.outSocket != null) {
            try {
                this.outSocket.close();
            }
            catch (Exception e) {
                LogLog.error("Could not close outSocket.", e);
            }
            this.outSocket = null;
        }
    }
    
    void connect(final InetAddress address, final int port) {
        if (this.address == null) {
            return;
        }
        try {
            this.cleanUp();
            (this.outSocket = new DatagramSocket()).connect(address, port);
        }
        catch (IOException e) {
            LogLog.error("Could not open UDP Socket for sending.", e);
            this.inError = true;
        }
    }
    
    public void append(final LoggingEvent event) {
        if (this.inError) {
            return;
        }
        if (event == null) {
            return;
        }
        if (this.address == null) {
            return;
        }
        if (this.outSocket != null) {
            event.setProperty("hostname", this.hostname);
            if (this.application != null) {
                event.setProperty("application", this.application);
            }
            try {
                final StringBuffer buf = new StringBuffer(this.layout.format(event));
                byte[] payload;
                if (this.encoding == null) {
                    payload = buf.toString().getBytes();
                }
                else {
                    payload = buf.toString().getBytes(this.encoding);
                }
                final DatagramPacket dp = new DatagramPacket(payload, payload.length, this.address, this.port);
                this.outSocket.send(dp);
            }
            catch (IOException e) {
                this.outSocket = null;
                LogLog.warn("Detected problem with UDP connection: " + e);
            }
        }
    }
    
    public boolean isActive() {
        return !this.inError;
    }
    
    InetAddress getAddressByName(final String host) {
        try {
            return InetAddress.getByName(host);
        }
        catch (Exception e) {
            LogLog.error("Could not find address of [" + host + "].", e);
            return null;
        }
    }
    
    public boolean requiresLayout() {
        return true;
    }
    
    public void setRemoteHost(final String host) {
        this.remoteHost = host;
    }
    
    public String getRemoteHost() {
        return this.remoteHost;
    }
    
    public void setApplication(final String app) {
        this.application = app;
    }
    
    public String getApplication() {
        return this.application;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
}
