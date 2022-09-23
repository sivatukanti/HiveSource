// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import org.apache.log4j.spi.LoggingEvent;
import java.net.Socket;
import org.apache.log4j.component.plugins.Plugin;
import org.apache.log4j.spi.LoggerRepository;
import java.util.Vector;
import org.apache.log4j.net.ZeroConfSupport;
import java.util.List;
import java.net.ServerSocket;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.plugins.Receiver;

public class XMLSocketReceiver extends Receiver implements Runnable, PortBased, Pauseable
{
    private boolean paused;
    protected String decoder;
    private ServerSocket serverSocket;
    private List socketList;
    private Thread rThread;
    public static final int DEFAULT_PORT = 4448;
    protected int port;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;
    public static final String ZONE = "_log4j_xml_tcpaccept_receiver.local.";
    
    public XMLSocketReceiver() {
        this.decoder = "org.apache.log4j.xml.XMLDecoder";
        this.socketList = new Vector();
        this.port = 4448;
    }
    
    public XMLSocketReceiver(final int _port) {
        this.decoder = "org.apache.log4j.xml.XMLDecoder";
        this.socketList = new Vector();
        this.port = 4448;
        this.port = _port;
    }
    
    public XMLSocketReceiver(final int _port, final LoggerRepository _repository) {
        this.decoder = "org.apache.log4j.xml.XMLDecoder";
        this.socketList = new Vector();
        this.port = 4448;
        this.port = _port;
        this.repository = _repository;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int _port) {
        this.port = _port;
    }
    
    public String getDecoder() {
        return this.decoder;
    }
    
    public void setDecoder(final String _decoder) {
        this.decoder = _decoder;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void setPaused(final boolean b) {
        this.paused = b;
    }
    
    public boolean isEquivalent(final Plugin testPlugin) {
        if (testPlugin != null && testPlugin instanceof XMLSocketReceiver) {
            final XMLSocketReceiver sReceiver = (XMLSocketReceiver)testPlugin;
            return this.port == sReceiver.getPort() && super.isEquivalent(testPlugin);
        }
        return false;
    }
    
    public int hashCode() {
        int result = 37 * ((this.repository != null) ? this.repository.hashCode() : 0);
        result = result * 37 + this.port;
        return result * 37 + ((this.getName() != null) ? this.getName().hashCode() : 0);
    }
    
    protected synchronized void setActive(final boolean b) {
        this.active = b;
    }
    
    public void activateOptions() {
        if (!this.isActive()) {
            (this.rThread = new Thread(this)).setDaemon(true);
            this.rThread.start();
            if (this.advertiseViaMulticastDNS) {
                (this.zeroConf = new ZeroConfSupport("_log4j_xml_tcpaccept_receiver.local.", this.port, this.getName())).advertise();
            }
            this.active = true;
        }
    }
    
    public void setAdvertiseViaMulticastDNS(final boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }
    
    public boolean isAdvertiseViaMulticastDNS() {
        return this.advertiseViaMulticastDNS;
    }
    
    public synchronized void shutdown() {
        this.active = false;
        if (this.rThread != null) {
            this.rThread.interrupt();
            this.rThread = null;
        }
        this.doShutdown();
    }
    
    private synchronized void doShutdown() {
        this.active = false;
        this.getLogger().debug("{} doShutdown called", this.getName());
        this.closeServerSocket();
        this.closeAllAcceptedSockets();
        if (this.advertiseViaMulticastDNS) {
            this.zeroConf.unadvertise();
        }
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
        this.socketList.clear();
    }
    
    public void run() {
        this.getLogger().debug("performing socket cleanup prior to entering loop for {}", this.name);
        this.closeServerSocket();
        this.closeAllAcceptedSockets();
        this.getLogger().debug("socket cleanup complete for {}", this.name);
        this.active = true;
        try {
            this.serverSocket = new ServerSocket(this.port);
        }
        catch (Exception e) {
            this.getLogger().error("error starting SocketReceiver (" + this.getName() + "), receiver did not start", e);
            this.active = false;
            this.doShutdown();
            return;
        }
        Socket socket = null;
        try {
            this.getLogger().debug("in run-about to enter while isactiveloop");
            this.active = true;
            while (!this.rThread.isInterrupted()) {
                if (socket != null) {
                    this.getLogger().debug("socket not null - creating and starting socketnode");
                    this.socketList.add(socket);
                    final XMLSocketNode node = new XMLSocketNode(this.decoder, socket, this);
                    node.setLoggerRepository(this.repository);
                    new Thread(node).start();
                    socket = null;
                }
                this.getLogger().debug("waiting to accept socket");
                socket = this.serverSocket.accept();
                this.getLogger().debug("accepted socket");
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (Exception e2) {
            this.getLogger().warn("socket server disconnected, stopping");
        }
    }
    
    public void doPost(final LoggingEvent event) {
        if (!this.isPaused()) {
            super.doPost(event);
        }
    }
}
