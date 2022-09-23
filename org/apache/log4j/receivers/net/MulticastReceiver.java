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
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.log4j.net.ZeroConfSupport;
import org.apache.log4j.receivers.spi.Decoder;
import java.net.MulticastSocket;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.plugins.Receiver;

public class MulticastReceiver extends Receiver implements PortBased, AddressBased, Pauseable
{
    private static final int PACKET_LENGTH = 16384;
    private int port;
    private String address;
    private String encoding;
    private MulticastSocket socket;
    private String decoder;
    private Decoder decoderImpl;
    private MulticastHandlerThread handlerThread;
    private MulticastReceiverThread receiverThread;
    private boolean paused;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    public static final String ZONE = "_log4j_xml_mcast_receiver.local.";
    
    public MulticastReceiver() {
        this.socket = null;
        this.decoder = "org.apache.log4j.xml.XMLDecoder";
    }
    
    public String getDecoder() {
        return this.decoder;
    }
    
    public void setDecoder(final String decoder) {
        this.decoder = decoder;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public synchronized void shutdown() {
        this.active = false;
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
        if (this.handlerThread != null) {
            this.handlerThread.interrupt();
        }
        if (this.receiverThread != null) {
            this.receiverThread.interrupt();
        }
        if (this.socket != null) {
            this.socket.close();
        }
    }
    
    public void setAddress(final String address) {
        this.address = address;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void setPaused(final boolean b) {
        this.paused = b;
    }
    
    public void activateOptions() {
        InetAddress addr = null;
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
            addr = InetAddress.getByName(this.address);
        }
        catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        try {
            this.active = true;
            (this.socket = new MulticastSocket(this.port)).joinGroup(addr);
            (this.receiverThread = new MulticastReceiverThread()).start();
            (this.handlerThread = new MulticastHandlerThread()).start();
            if (this.advertiseViaMulticastDNS) {
                (this.zeroConf = new ZeroConfSupport("_log4j_xml_mcast_receiver.local.", this.port, this.getName())).advertise();
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
    
    class MulticastHandlerThread extends Thread
    {
        private List list;
        
        public MulticastHandlerThread() {
            this.list = new ArrayList();
            this.setDaemon(true);
        }
        
        public void append(final String data) {
            synchronized (this.list) {
                this.list.add(data);
                this.list.notify();
            }
        }
        
        public void run() {
            final ArrayList list2 = new ArrayList();
            while (this.isAlive()) {
                synchronized (this.list) {
                    try {
                        while (this.list.size() == 0) {
                            this.list.wait();
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
                        final List v = MulticastReceiver.this.decoderImpl.decodeEvents(data.trim());
                        if (v != null) {
                            final Iterator eventIter = v.iterator();
                            while (eventIter.hasNext()) {
                                if (!MulticastReceiver.this.isPaused()) {
                                    MulticastReceiver.this.doPost(eventIter.next());
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
        }
    }
    
    class MulticastReceiverThread extends Thread
    {
        public MulticastReceiverThread() {
            this.setDaemon(true);
        }
        
        public void run() {
            MulticastReceiver.this.active = true;
            final byte[] b = new byte[16384];
            final DatagramPacket p = new DatagramPacket(b, b.length);
            while (MulticastReceiver.this.active) {
                try {
                    MulticastReceiver.this.socket.receive(p);
                    if (MulticastReceiver.this.encoding == null) {
                        MulticastReceiver.this.handlerThread.append(new String(p.getData(), 0, p.getLength()));
                    }
                    else {
                        MulticastReceiver.this.handlerThread.append(new String(p.getData(), 0, p.getLength(), MulticastReceiver.this.encoding));
                    }
                }
                catch (SocketException se) {}
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            ComponentBase.this.getLogger().debug("{}'s thread is ending.", MulticastReceiver.this.getName());
        }
    }
}
