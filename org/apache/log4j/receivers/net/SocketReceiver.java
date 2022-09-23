// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import org.apache.log4j.spi.LoggingEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.component.plugins.Plugin;
import org.apache.log4j.spi.LoggerRepository;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.net.ZeroConfSupport;
import java.util.List;
import java.util.Vector;
import java.net.ServerSocket;
import java.util.Map;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.plugins.Receiver;

public class SocketReceiver extends Receiver implements Runnable, PortBased, Pauseable
{
    private Map socketMap;
    private boolean paused;
    private Thread rThread;
    protected int port;
    private ServerSocket serverSocket;
    private Vector socketList;
    public static final String ZONE = "_log4j_obj_tcpaccept_receiver.local.";
    private SocketNodeEventListener listener;
    private List listenerList;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    
    public SocketReceiver() {
        this.socketMap = new HashMap();
        this.socketList = new Vector();
        this.listener = null;
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
    }
    
    public SocketReceiver(final int p) {
        this.socketMap = new HashMap();
        this.socketList = new Vector();
        this.listener = null;
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.port = p;
    }
    
    public SocketReceiver(final int p, final LoggerRepository repo) {
        this.socketMap = new HashMap();
        this.socketList = new Vector();
        this.listener = null;
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.port = p;
        this.repository = repo;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int p) {
        this.port = p;
    }
    
    public boolean isEquivalent(final Plugin testPlugin) {
        if (testPlugin != null && testPlugin instanceof SocketReceiver) {
            final SocketReceiver sReceiver = (SocketReceiver)testPlugin;
            return this.port == sReceiver.getPort() && super.isEquivalent(testPlugin);
        }
        return false;
    }
    
    public void activateOptions() {
        if (!this.isActive()) {
            (this.rThread = new Thread(this)).setDaemon(true);
            this.rThread.start();
            if (this.advertiseViaMulticastDNS) {
                (this.zeroConf = new ZeroConfSupport("_log4j_obj_tcpaccept_receiver.local.", this.port, this.getName())).advertise();
            }
            this.active = true;
        }
    }
    
    public synchronized void shutdown() {
        this.getLogger().debug(this.getName() + " received shutdown request");
        this.active = false;
        if (this.rThread != null) {
            this.rThread.interrupt();
            this.rThread = null;
        }
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
        this.doShutdown();
    }
    
    private synchronized void doShutdown() {
        this.active = false;
        this.getLogger().debug(this.getName() + " doShutdown called");
        this.closeServerSocket();
        this.closeAllAcceptedSockets();
    }
    
    private void closeServerSocket() {
        this.getLogger().debug("{} closing server socket", this.getName());
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        }
        catch (Exception ex) {}
        this.serverSocket = null;
    }
    
    private synchronized void closeAllAcceptedSockets() {
        for (int x = 0; x < this.socketList.size(); ++x) {
            try {
                this.socketList.get(x).close();
            }
            catch (Exception ex) {}
        }
        this.socketMap.clear();
        this.socketList.clear();
    }
    
    protected synchronized void setActive(final boolean b) {
        this.active = b;
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
    
    public void run() {
        this.closeServerSocket();
        this.closeAllAcceptedSockets();
        try {
            this.serverSocket = new ServerSocket(this.port);
        }
        catch (Exception e) {
            this.getLogger().error("error starting SocketReceiver (" + this.getName() + "), receiver did not start", e);
            this.active = false;
            return;
        }
        Socket socket = null;
        try {
            this.getLogger().debug("in run-about to enter while not interrupted loop");
            this.active = true;
            while (!this.rThread.isInterrupted()) {
                if (socket != null) {
                    this.getLogger().debug("socket not null - creating and starting socketnode");
                    this.socketList.add(socket);
                    final SocketNode13 node = new SocketNode13(socket, this);
                    synchronized (this.listenerList) {
                        for (final SocketNodeEventListener l : this.listenerList) {
                            node.addSocketNodeEventListener(l);
                        }
                    }
                    this.socketMap.put(socket, node);
                    new Thread(node).start();
                    socket = null;
                }
                this.getLogger().debug("waiting to accept socket");
                socket = this.serverSocket.accept();
                this.getLogger().debug("accepted socket");
            }
        }
        catch (Exception e2) {
            this.getLogger().warn("exception while watching socket server in SocketReceiver (" + this.getName() + "), stopping");
        }
        this.getLogger().debug("{} has exited the not interrupted loop", this.getName());
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e3) {
                this.getLogger().warn("socket exception caught - socket closed");
            }
        }
        this.getLogger().debug("{} is exiting main run loop", this.getName());
    }
    
    public Vector getConnectedSocketDetails() {
        final Vector details = new Vector(this.socketList.size());
        final Enumeration enumeration = this.socketList.elements();
        while (enumeration.hasMoreElements()) {
            final Socket socket = enumeration.nextElement();
            details.add(new SocketDetail(socket, (SocketNode13)this.socketMap.get(socket)));
        }
        return details;
    }
    
    public SocketNodeEventListener getListener() {
        return this.listener;
    }
    
    public void addSocketNodeEventListener(final SocketNodeEventListener l) {
        this.listenerList.add(l);
    }
    
    public void removeSocketNodeEventListener(final SocketNodeEventListener l) {
        this.listenerList.remove(l);
    }
    
    public void setListener(final SocketNodeEventListener l) {
        this.removeSocketNodeEventListener(l);
        this.addSocketNodeEventListener(l);
        this.listener = l;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void setPaused(final boolean b) {
        this.paused = b;
    }
    
    public void doPost(final LoggingEvent event) {
        if (!this.isPaused()) {
            super.doPost(event);
        }
    }
    
    private static final class SocketDetail implements AddressBased, PortBased, Pauseable
    {
        private String address;
        private int port;
        private SocketNode13 socketNode;
        
        private SocketDetail(final Socket socket, final SocketNode13 node) {
            this.address = socket.getInetAddress().getHostName();
            this.port = socket.getPort();
            this.socketNode = node;
        }
        
        public String getAddress() {
            return this.address;
        }
        
        public int getPort() {
            return this.port;
        }
        
        public String getName() {
            return "Socket";
        }
        
        public boolean isActive() {
            return true;
        }
        
        public boolean isPaused() {
            return this.socketNode.isPaused();
        }
        
        public void setPaused(final boolean b) {
            this.socketNode.setPaused(b);
        }
    }
}
