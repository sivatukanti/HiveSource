// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.chainsaw;

import java.net.SocketException;
import java.io.EOFException;
import org.apache.log4j.spi.LoggingEvent;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import org.apache.log4j.Logger;

class LoggingReceiver extends Thread
{
    private static final Logger LOG;
    private MyTableModel mModel;
    private ServerSocket mSvrSock;
    
    LoggingReceiver(final MyTableModel aModel, final int aPort) throws IOException {
        this.setDaemon(true);
        this.mModel = aModel;
        this.mSvrSock = new ServerSocket(aPort);
    }
    
    public void run() {
        LoggingReceiver.LOG.info("Thread started");
        try {
            while (true) {
                LoggingReceiver.LOG.debug("Waiting for a connection");
                final Socket client = this.mSvrSock.accept();
                LoggingReceiver.LOG.debug("Got a connection from " + client.getInetAddress().getHostName());
                final Thread t = new Thread(new Slurper(client));
                t.setDaemon(true);
                t.start();
            }
        }
        catch (IOException e) {
            LoggingReceiver.LOG.error("Error in accepting connections, stopping.", e);
        }
    }
    
    static {
        LOG = Logger.getLogger(LoggingReceiver.class);
    }
    
    private class Slurper implements Runnable
    {
        private final Socket mClient;
        
        Slurper(final Socket aClient) {
            this.mClient = aClient;
        }
        
        public void run() {
            LoggingReceiver.LOG.debug("Starting to get data");
            try {
                final ObjectInputStream ois = new ObjectInputStream(this.mClient.getInputStream());
                while (true) {
                    final LoggingEvent event = (LoggingEvent)ois.readObject();
                    LoggingReceiver.this.mModel.addEvent(new EventDetails(event));
                }
            }
            catch (EOFException e3) {
                LoggingReceiver.LOG.info("Reached EOF, closing connection");
            }
            catch (SocketException e4) {
                LoggingReceiver.LOG.info("Caught SocketException, closing connection");
            }
            catch (IOException e) {
                LoggingReceiver.LOG.warn("Got IOException, closing connection", e);
            }
            catch (ClassNotFoundException e2) {
                LoggingReceiver.LOG.warn("Got ClassNotFoundException, closing connection", e2);
            }
            try {
                this.mClient.close();
            }
            catch (IOException e) {
                LoggingReceiver.LOG.warn("Error closing connection", e);
            }
        }
    }
}
