// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import org.apache.log4j.Logger;
import java.util.Iterator;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import java.net.SocketException;
import java.io.EOFException;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.receivers.spi.Decoder;
import org.apache.log4j.component.plugins.Receiver;
import java.net.Socket;
import org.apache.log4j.component.spi.ComponentBase;

public class XMLSocketNode extends ComponentBase implements Runnable
{
    Socket socket;
    Receiver receiver;
    Decoder decoder;
    SocketNodeEventListener listener;
    
    public XMLSocketNode(final String decoder, final Socket socket, final LoggerRepository hierarchy) {
        this.repository = hierarchy;
        try {
            final Class c = Class.forName(decoder);
            final Object o = c.newInstance();
            if (o instanceof Decoder) {
                this.decoder = (Decoder)o;
            }
        }
        catch (ClassNotFoundException cnfe) {
            this.getLogger().warn("Unable to find decoder", cnfe);
        }
        catch (IllegalAccessException iae) {
            this.getLogger().warn("Unable to construct decoder", iae);
        }
        catch (InstantiationException ie) {
            this.getLogger().warn("Unable to construct decoder", ie);
        }
        this.socket = socket;
    }
    
    public XMLSocketNode(final String decoder, final Socket socket, final Receiver receiver) {
        try {
            final Class c = Class.forName(decoder);
            final Object o = c.newInstance();
            if (o instanceof Decoder) {
                this.decoder = (Decoder)o;
            }
        }
        catch (ClassNotFoundException cnfe) {
            this.getLogger().warn("Unable to find decoder", cnfe);
        }
        catch (IllegalAccessException iae) {
            this.getLogger().warn("Unable to construct decoder", iae);
        }
        catch (InstantiationException ie) {
            this.getLogger().warn("Unable to construct decoder", ie);
        }
        this.socket = socket;
        this.receiver = receiver;
    }
    
    public void setListener(final SocketNodeEventListener _listener) {
        this.listener = _listener;
    }
    
    public void run() {
        Exception listenerException = null;
        InputStream is = null;
        if (this.receiver == null || this.decoder == null) {
            is = null;
            listenerException = new Exception("No receiver or decoder provided.  Cannot process xml socket events");
            this.getLogger().error("Exception constructing XML Socket Receiver", listenerException);
        }
        try {
            is = this.socket.getInputStream();
        }
        catch (Exception e) {
            is = null;
            listenerException = e;
            this.getLogger().error("Exception opening ObjectInputStream to " + this.socket, e);
        }
        if (is != null) {
            final String hostName = this.socket.getInetAddress().getHostName();
            final String remoteInfo = hostName + ":" + this.socket.getPort();
            try {
                while (true) {
                    final byte[] b = new byte[1024];
                    final int length = is.read(b);
                    if (length == -1) {
                        break;
                    }
                    final List v = this.decoder.decodeEvents(new String(b, 0, length));
                    if (v == null) {
                        continue;
                    }
                    for (final LoggingEvent e2 : v) {
                        e2.setProperty("hostname", hostName);
                        e2.setProperty("log4j.remoteSourceInfo", remoteInfo);
                        if (this.receiver != null) {
                            this.receiver.doPost(e2);
                        }
                        else {
                            final Logger remoteLogger = this.repository.getLogger(e2.getLoggerName());
                            if (!e2.getLevel().isGreaterOrEqual(remoteLogger.getEffectiveLevel())) {
                                continue;
                            }
                            remoteLogger.callAppenders(e2);
                        }
                    }
                }
                this.getLogger().info("no bytes read from stream - closing connection.");
            }
            catch (EOFException e3) {
                this.getLogger().info("Caught java.io.EOFException closing connection.");
                listenerException = e3;
            }
            catch (SocketException e4) {
                this.getLogger().info("Caught java.net.SocketException closing connection.");
                listenerException = e4;
            }
            catch (IOException e5) {
                this.getLogger().info("Caught java.io.IOException: " + e5);
                this.getLogger().info("Closing connection.");
                listenerException = e5;
            }
            catch (Exception e6) {
                this.getLogger().error("Unexpected exception. Closing connection.", e6);
                listenerException = e6;
            }
        }
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (Exception ex) {}
        if (this.listener != null) {
            this.listener.socketClosedEvent(listenerException);
        }
    }
}
