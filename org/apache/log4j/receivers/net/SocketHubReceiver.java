// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.io.IOException;
import java.net.ConnectException;
import org.apache.log4j.component.ULogger;
import java.util.Iterator;
import java.net.Socket;
import org.apache.log4j.component.plugins.Plugin;
import org.apache.log4j.spi.LoggerRepository;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.log4j.net.ZeroConfSupport;
import java.util.List;
import org.apache.log4j.component.plugins.Receiver;

public class SocketHubReceiver extends Receiver implements SocketNodeEventListener, PortBased
{
    static final int DEFAULT_RECONNECTION_DELAY = 30000;
    protected String host;
    protected int port;
    protected int reconnectionDelay;
    public static final String ZONE = "_log4j_obj_tcpconnect_receiver.local.";
    protected boolean active;
    protected Connector connector;
    protected SocketNode13 socketNode;
    private List listenerList;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    
    public SocketHubReceiver() {
        this.reconnectionDelay = 30000;
        this.active = false;
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
    }
    
    public SocketHubReceiver(final String h, final int p) {
        this.reconnectionDelay = 30000;
        this.active = false;
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.host = h;
        this.port = p;
    }
    
    public SocketHubReceiver(final String h, final int p, final LoggerRepository repo) {
        this.reconnectionDelay = 30000;
        this.active = false;
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.host = h;
        this.port = p;
        this.repository = repo;
    }
    
    public void addSocketNodeEventListener(final SocketNodeEventListener l) {
        this.listenerList.add(l);
    }
    
    public void removeSocketNodeEventListener(final SocketNodeEventListener l) {
        this.listenerList.remove(l);
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String remoteHost) {
        this.host = remoteHost;
    }
    
    public void setPort(final String remoteHost) {
        this.host = remoteHost;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int p) {
        this.port = p;
    }
    
    public void setReconnectionDelay(final int delay) {
        final int oldValue = this.reconnectionDelay;
        this.firePropertyChange("reconnectionDelay", oldValue, this.reconnectionDelay = delay);
    }
    
    public int getReconnectionDelay() {
        return this.reconnectionDelay;
    }
    
    public boolean isEquivalent(final Plugin testPlugin) {
        if (testPlugin != null && testPlugin instanceof SocketHubReceiver) {
            final SocketHubReceiver sReceiver = (SocketHubReceiver)testPlugin;
            return this.port == sReceiver.getPort() && this.host.equals(sReceiver.getHost()) && this.reconnectionDelay == sReceiver.getReconnectionDelay() && super.isEquivalent(testPlugin);
        }
        return false;
    }
    
    protected synchronized void setActive(final boolean b) {
        this.active = b;
    }
    
    public void activateOptions() {
        if (!this.isActive()) {
            this.setActive(true);
            if (this.advertiseViaMulticastDNS) {
                (this.zeroConf = new ZeroConfSupport("_log4j_obj_tcpconnect_receiver.local.", this.port, this.getName())).advertise();
            }
            this.fireConnector(false);
        }
    }
    
    public synchronized void shutdown() {
        this.active = false;
        try {
            if (this.socketNode != null) {
                this.socketNode.close();
                this.socketNode = null;
            }
        }
        catch (Exception e) {
            this.getLogger().info("Excpetion closing socket", e);
        }
        if (this.connector != null) {
            this.connector.interrupted = true;
            this.connector = null;
        }
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
    }
    
    public void socketClosedEvent(final Exception e) {
        if (e != null) {
            this.connector = null;
            this.fireConnector(true);
        }
    }
    
    private synchronized void fireConnector(final boolean isReconnect) {
        if (this.active && this.connector == null) {
            this.getLogger().debug("Starting a new connector thread.");
            (this.connector = new Connector(isReconnect)).setDaemon(true);
            this.connector.setPriority(1);
            this.connector.start();
        }
    }
    
    private synchronized void setSocket(final Socket newSocket) {
        this.connector = null;
        (this.socketNode = new SocketNode13(newSocket, this)).addSocketNodeEventListener(this);
        synchronized (this.listenerList) {
            for (final SocketNodeEventListener listener : this.listenerList) {
                this.socketNode.addSocketNodeEventListener(listener);
            }
        }
        new Thread(this.socketNode).start();
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
    
    public void socketOpened(final String remoteInfo) {
    }
    
    private final class Connector extends Thread
    {
        boolean interrupted;
        boolean doDelay;
        
        public Connector(final boolean isReconnect) {
            this.interrupted = false;
            this.doDelay = isReconnect;
        }
        
        public void run() {
            while (!this.interrupted) {
                try {
                    if (this.doDelay) {
                        ComponentBase.this.getLogger().debug("waiting for " + SocketHubReceiver.this.reconnectionDelay + " milliseconds before reconnecting.");
                        Thread.sleep(SocketHubReceiver.this.reconnectionDelay);
                    }
                    this.doDelay = true;
                    ComponentBase.this.getLogger().debug("Attempting connection to " + SocketHubReceiver.this.host);
                    final Socket s = new Socket(SocketHubReceiver.this.host, SocketHubReceiver.this.port);
                    SocketHubReceiver.this.setSocket(s);
                    ComponentBase.this.getLogger().debug("Connection established. Exiting connector thread.");
                }
                catch (InterruptedException e2) {
                    ComponentBase.this.getLogger().debug("Connector interrupted. Leaving loop.");
                    return;
                }
                catch (ConnectException e3) {
                    ComponentBase.this.getLogger().debug("Remote host {} refused connection.", SocketHubReceiver.this.host);
                    continue;
                }
                catch (IOException e) {
                    ComponentBase.this.getLogger().debug("Could not connect to {}. Exception is {}.", SocketHubReceiver.this.host, e);
                    continue;
                }
                break;
            }
        }
    }
}
