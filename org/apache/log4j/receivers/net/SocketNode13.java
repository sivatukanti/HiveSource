// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.util.Iterator;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.SocketException;
import java.io.EOFException;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.log4j.spi.LoggerRepository;
import java.util.List;
import org.apache.log4j.component.plugins.Receiver;
import java.net.Socket;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.spi.ComponentBase;

public class SocketNode13 extends ComponentBase implements Runnable, Pauseable
{
    private boolean paused;
    private boolean closed;
    private Socket socket;
    private Receiver receiver;
    private List listenerList;
    
    public SocketNode13(final Socket s, final LoggerRepository hierarchy) {
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.socket = s;
        this.repository = hierarchy;
    }
    
    public SocketNode13(final Socket s, final Receiver r) {
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.socket = s;
        this.receiver = r;
    }
    
    public void setListener(final SocketNodeEventListener l) {
        this.removeSocketNodeEventListener(l);
        this.addSocketNodeEventListener(l);
    }
    
    public void addSocketNodeEventListener(final SocketNodeEventListener listener) {
        this.listenerList.add(listener);
    }
    
    public void removeSocketNodeEventListener(final SocketNodeEventListener listener) {
        this.listenerList.remove(listener);
    }
    
    public void run() {
        Exception listenerException = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()));
        }
        catch (Exception e) {
            ois = null;
            listenerException = e;
            this.getLogger().error("Exception opening ObjectInputStream to " + this.socket, e);
        }
        if (ois != null) {
            final String hostName = this.socket.getInetAddress().getHostName();
            final String remoteInfo = hostName + ":" + this.socket.getPort();
            this.fireSocketOpened(remoteInfo);
            try {
                while (!this.isClosed()) {
                    final LoggingEvent event = (LoggingEvent)ois.readObject();
                    event.setProperty("hostname", hostName);
                    event.setProperty("log4j.remoteSourceInfo", remoteInfo);
                    if (!this.isPaused() && !this.isClosed()) {
                        if (this.receiver != null) {
                            this.receiver.doPost(event);
                        }
                        else {
                            final Logger remoteLogger = this.repository.getLogger(event.getLoggerName());
                            if (!event.getLevel().isGreaterOrEqual(remoteLogger.getEffectiveLevel())) {
                                continue;
                            }
                            remoteLogger.callAppenders(event);
                        }
                    }
                }
            }
            catch (EOFException e2) {
                this.getLogger().info("Caught java.io.EOFException closing connection.");
                listenerException = e2;
            }
            catch (SocketException e3) {
                this.getLogger().info("Caught java.net.SocketException closing connection.");
                listenerException = e3;
            }
            catch (IOException e4) {
                this.getLogger().info("Caught java.io.IOException: " + e4);
                this.getLogger().info("Closing connection.");
                listenerException = e4;
            }
            catch (Exception e5) {
                this.getLogger().error("Unexpected exception. Closing connection.", e5);
                listenerException = e5;
            }
        }
        try {
            if (ois != null) {
                ois.close();
            }
        }
        catch (Exception ex) {}
        if (this.listenerList.size() > 0 && !this.isClosed()) {
            this.fireSocketClosedEvent(listenerException);
        }
    }
    
    private void fireSocketClosedEvent(final Exception listenerException) {
        synchronized (this.listenerList) {
            for (final SocketNodeEventListener snel : this.listenerList) {
                if (snel != null) {
                    snel.socketClosedEvent(listenerException);
                }
            }
        }
    }
    
    private void fireSocketOpened(final String remoteInfo) {
        synchronized (this.listenerList) {
            for (final SocketNodeEventListener snel : this.listenerList) {
                if (snel != null) {
                    snel.socketOpened(remoteInfo);
                }
            }
        }
    }
    
    public void setPaused(final boolean b) {
        this.paused = b;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void close() throws IOException {
        this.getLogger().debug("closing socket");
        this.closed = true;
        this.socket.close();
        this.fireSocketClosedEvent(null);
    }
    
    public boolean isClosed() {
        return this.closed;
    }
}
