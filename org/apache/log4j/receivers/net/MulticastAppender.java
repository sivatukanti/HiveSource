// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.net.DatagramPacket;
import org.apache.log4j.spi.LoggingEvent;
import java.io.IOException;
import java.util.Map;
import org.apache.log4j.helpers.LogLog;
import java.util.HashMap;
import org.apache.log4j.xml.XMLLayout;
import java.net.UnknownHostException;
import org.apache.log4j.net.ZeroConfSupport;
import java.net.MulticastSocket;
import java.net.InetAddress;
import org.apache.log4j.AppenderSkeleton;

public class MulticastAppender extends AppenderSkeleton implements PortBased
{
    static final int DEFAULT_PORT = 9991;
    public static final String ZONE = "_log4j_xml_mcast_appender.local.";
    String hostname;
    String remoteHost;
    String application;
    int timeToLive;
    InetAddress address;
    int port;
    MulticastSocket outSocket;
    private String encoding;
    private boolean locationInfo;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    
    public MulticastAppender() {
        super(false);
        this.port = 9991;
        this.locationInfo = false;
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
            this.address = this.getAddressByName(this.remoteHost);
            if (this.layout == null) {
                this.layout = new XMLLayout();
            }
            if (this.advertiseViaMulticastDNS) {
                final Map properties = new HashMap();
                properties.put("multicastAddress", this.remoteHost);
                (this.zeroConf = new ZeroConfSupport("_log4j_xml_mcast_appender.local.", this.port, this.getName(), properties)).advertise();
            }
            this.connect();
            super.activateOptions();
            return;
        }
        final String err = "The RemoteHost property is required for MulticastAppender named " + this.name;
        LogLog.error(err);
        throw new IllegalStateException(err);
    }
    
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
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
    
    void connect() {
        if (this.address == null) {
            return;
        }
        try {
            this.cleanUp();
            (this.outSocket = new MulticastSocket()).setTimeToLive(this.timeToLive);
        }
        catch (IOException e) {
            LogLog.error("Error in connect method of MulticastAppender named " + this.name, e);
        }
    }
    
    public void append(final LoggingEvent event) {
        if (event == null) {
            return;
        }
        if (this.locationInfo) {
            event.getLocationInformation();
        }
        if (this.outSocket != null) {
            event.setProperty("hostname", this.hostname);
            if (this.application != null) {
                event.setProperty("application", this.application);
            }
            if (this.locationInfo) {
                event.getLocationInformation();
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
                LogLog.warn("Detected problem with Multicast connection: " + e);
            }
        }
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
    
    public void setRemoteHost(final String host) {
        this.remoteHost = host;
    }
    
    public String getRemoteHost() {
        return this.remoteHost;
    }
    
    public void setLocationInfo(final boolean locationInfo) {
        this.locationInfo = locationInfo;
    }
    
    public boolean getLocationInfo() {
        return this.locationInfo;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setApplication(final String app) {
        this.application = app;
    }
    
    public String getApplication() {
        return this.application;
    }
    
    public void setTimeToLive(final int timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    public int getTimeToLive() {
        return this.timeToLive;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean isActive() {
        return true;
    }
    
    public boolean requiresLayout() {
        return true;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
}
