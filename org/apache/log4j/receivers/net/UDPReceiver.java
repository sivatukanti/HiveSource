// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.net.SocketException;
import java.net.DatagramPacket;
import java.util.Iterator;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.component.ULogger;
import java.io.IOException;
import org.apache.log4j.net.ZeroConfSupport;
import java.net.DatagramSocket;
import org.apache.log4j.receivers.spi.Decoder;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.plugins.Receiver;

public class UDPReceiver extends Receiver implements PortBased, Pauseable
{
    private static final int PACKET_LENGTH = 16384;
    private UDPReceiverThread receiverThread;
    private String encoding;
    private String decoder;
    private Decoder decoderImpl;
    protected boolean paused;
    private transient boolean closed;
    private int port;
    private DatagramSocket socket;
    UDPHandlerThread handlerThread;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    public static final String ZONE = "_log4j_xml_udp_receiver.local.";
    
    public UDPReceiver() {
        this.decoder = "org.apache.log4j.xml.XMLDecoder";
        this.closed = false;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public String getDecoder() {
        return this.decoder;
    }
    
    public void setDecoder(final String decoder) {
        this.decoder = decoder;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void setPaused(final boolean b) {
        this.paused = b;
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
    
    public synchronized void shutdown() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.active = false;
        if (this.socket != null) {
            this.socket.close();
        }
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
        try {
            if (this.handlerThread != null) {
                this.handlerThread.close();
                this.handlerThread.join();
            }
            if (this.receiverThread != null) {
                this.receiverThread.join();
            }
        }
        catch (InterruptedException ex) {}
    }
    
    public void activateOptions() {
        try {
            final Class c = Class.forName(this.decoder);
            final Object o = c.newInstance();
            if (o instanceof Decoder) {
                this.decoderImpl = (Decoder)o;
            }
        }
        catch (ClassNotFoundException cnfe) {
            this.getLogger().warn("Unable to find decoder", cnfe);
        }
        catch (IllegalAccessException iae) {
            this.getLogger().warn("Could not construct decoder", iae);
        }
        catch (InstantiationException ie) {
            this.getLogger().warn("Could not construct decoder", ie);
        }
        try {
            this.socket = new DatagramSocket(this.port);
            (this.receiverThread = new UDPReceiverThread()).start();
            (this.handlerThread = new UDPHandlerThread()).start();
            if (this.advertiseViaMulticastDNS) {
                (this.zeroConf = new ZeroConfSupport("_log4j_xml_udp_receiver.local.", this.port, this.getName())).advertise();
            }
            this.active = true;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    class UDPHandlerThread extends Thread
    {
        private List list;
        
        public UDPHandlerThread() {
            this.list = new ArrayList();
            this.setDaemon(true);
        }
        
        public void append(final String data) {
            synchronized (this.list) {
                this.list.add(data);
                this.list.notify();
            }
        }
        
        void close() {
            synchronized (this.list) {
                this.list.notify();
            }
        }
        
        public void run() {
            final ArrayList list2 = new ArrayList();
            while (!UDPReceiver.this.closed) {
                synchronized (this.list) {
                    try {
                        while (!UDPReceiver.this.closed && this.list.size() == 0) {
                            this.list.wait(300L);
                        }
                        if (this.list.size() > 0) {
                            list2.addAll(this.list);
                            this.list.clear();
                        }
                    }
                    catch (InterruptedException ex) {}
                }
                if (list2.size() > 0) {
                    for (final String data : list2) {
                        final List v = UDPReceiver.this.decoderImpl.decodeEvents(data);
                        if (v != null) {
                            final Iterator eventIter = v.iterator();
                            while (eventIter.hasNext()) {
                                if (!UDPReceiver.this.isPaused()) {
                                    UDPReceiver.this.doPost(eventIter.next());
                                }
                            }
                        }
                    }
                    list2.clear();
                }
                else {
                    try {
                        synchronized (this) {
                            this.wait(1000L);
                        }
                    }
                    catch (InterruptedException ie) {}
                }
            }
            ComponentBase.this.getLogger().debug(UDPReceiver.this.getName() + "'s handler thread is exiting");
        }
    }
    
    class UDPReceiverThread extends Thread
    {
        public UDPReceiverThread() {
            this.setDaemon(true);
        }
        
        public void run() {
            final byte[] b = new byte[16384];
            final DatagramPacket p = new DatagramPacket(b, b.length);
            while (!UDPReceiver.this.closed) {
                try {
                    UDPReceiver.this.socket.receive(p);
                    if (UDPReceiver.this.encoding == null) {
                        UDPReceiver.this.handlerThread.append(new String(p.getData(), 0, p.getLength()));
                    }
                    else {
                        UDPReceiver.this.handlerThread.append(new String(p.getData(), 0, p.getLength(), UDPReceiver.this.encoding));
                    }
                }
                catch (SocketException se) {}
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}
