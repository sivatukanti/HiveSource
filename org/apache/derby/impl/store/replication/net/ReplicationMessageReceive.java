// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.net;

import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.impl.store.raw.log.LogCounter;
import javax.net.ServerSocketFactory;
import org.apache.derby.iapi.error.StandardException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.net.Socket;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.net.ServerSocket;

public class ReplicationMessageReceive
{
    private final SlaveAddress slaveAddress;
    private ServerSocket serverSocket;
    private SocketConnection socketConn;
    private static final int DEFAULT_PING_TIMEOUT = 5000;
    private Thread pingThread;
    private boolean killPingThread;
    private boolean connectionConfirmed;
    private final Object sendPingSemaphore;
    private boolean doSendPing;
    private final Object receivePongSemaphore;
    
    public ReplicationMessageReceive(final SlaveAddress slaveAddress, final String s) {
        this.pingThread = null;
        this.killPingThread = false;
        this.connectionConfirmed = false;
        this.sendPingSemaphore = new Object();
        this.doSendPing = false;
        this.receivePongSemaphore = new Object();
        this.slaveAddress = slaveAddress;
        Monitor.logTextMessage("R011", s, slaveAddress.getHostAddress().getHostName(), String.valueOf(slaveAddress.getPortNumber()));
    }
    
    public void initConnection(final int soTimeout, final long n, final String s) throws IOException, StandardException, ClassNotFoundException {
        if (this.serverSocket == null) {
            this.serverSocket = this.createServerSocket();
        }
        this.serverSocket.setSoTimeout(soTimeout);
        Socket socket;
        try {
            socket = AccessController.doPrivileged((PrivilegedExceptionAction<Socket>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return ReplicationMessageReceive.this.serverSocket.accept();
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        this.socketConn = new SocketConnection(socket);
        this.parseAndAckVersion(this.readMessage(), s);
        this.parseAndAckInstant(this.readMessage(), n, s);
        this.killPingThread = false;
        (this.pingThread = new SlavePingThread(s)).setDaemon(true);
        this.pingThread.start();
    }
    
    private ServerSocket createServerSocket() throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<ServerSocket>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return ServerSocketFactory.getDefault().createServerSocket(ReplicationMessageReceive.this.slaveAddress.getPortNumber(), 0, ReplicationMessageReceive.this.slaveAddress.getHostAddress());
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public void tearDown() throws IOException {
        synchronized (this.sendPingSemaphore) {
            this.killPingThread = true;
            this.sendPingSemaphore.notify();
        }
        try {
            if (this.socketConn != null) {
                this.socketConn.tearDown();
            }
        }
        finally {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        }
    }
    
    private void parseAndAckVersion(final ReplicationMessage replicationMessage, final String s) throws IOException, StandardException {
        if (replicationMessage.getType() != 0) {
            this.handleUnexpectedMessage(s, String.valueOf(0), String.valueOf(replicationMessage.getType()));
        }
        if ((long)replicationMessage.getMessage() == 1L) {
            this.sendMessage(new ReplicationMessage(11, "UID OK"));
            return;
        }
        this.sendMessage(new ReplicationMessage(12, new String[] { "XRE02" }));
        throw StandardException.newException("XRE02");
    }
    
    private void parseAndAckInstant(final ReplicationMessage replicationMessage, final long n, final String s) throws IOException, StandardException {
        if (replicationMessage.getType() != 1) {
            this.handleUnexpectedMessage(s, String.valueOf(1), String.valueOf(replicationMessage.getType()));
        }
        final long longValue = (long)replicationMessage.getMessage();
        if (longValue == n) {
            this.sendMessage(new ReplicationMessage(11, "Instant OK"));
            return;
        }
        final String[] array = { s, String.valueOf(LogCounter.getLogFileNumber(longValue)), String.valueOf(LogCounter.getLogFilePosition(longValue)), String.valueOf(LogCounter.getLogFileNumber(n)), String.valueOf(LogCounter.getLogFilePosition(n)), "XRE05.C" };
        this.sendMessage(new ReplicationMessage(12, array));
        throw StandardException.newException("XRE05.C", array);
    }
    
    private void handleUnexpectedMessage(final String s, final String s2, final String s3) throws StandardException, IOException {
        final String[] array = { s, s2, s3, "XRE12" };
        this.sendMessage(new ReplicationMessage(12, array));
        throw StandardException.newException("XRE12", array);
    }
    
    public void sendMessage(final ReplicationMessage replicationMessage) throws IOException {
        this.checkSocketConnection();
        this.socketConn.writeMessage(replicationMessage);
    }
    
    public ReplicationMessage readMessage() throws ClassNotFoundException, IOException {
        this.checkSocketConnection();
        final ReplicationMessage replicationMessage = (ReplicationMessage)this.socketConn.readMessage();
        if (replicationMessage.getType() == 14) {
            synchronized (this.receivePongSemaphore) {
                this.connectionConfirmed = true;
                this.receivePongSemaphore.notify();
            }
            return this.readMessage();
        }
        return replicationMessage;
    }
    
    private void checkSocketConnection() throws IOException {
        if (this.socketConn == null) {
            throw new IOException("R012");
        }
    }
    
    public synchronized boolean isConnectedToMaster() {
        synchronized (this.receivePongSemaphore) {
            this.connectionConfirmed = false;
            long n = 5000L;
            final long n2;
            synchronized (this.sendPingSemaphore) {
                this.doSendPing = true;
                this.sendPingSemaphore.notify();
                n2 = System.currentTimeMillis() + 5000L;
            }
            do {
                try {
                    this.receivePongSemaphore.wait(n);
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
                n = n2 - System.currentTimeMillis();
            } while (!this.connectionConfirmed && n > 0L);
        }
        return this.connectionConfirmed;
    }
    
    private class SlavePingThread extends Thread
    {
        private final ReplicationMessage pingMsg;
        
        SlavePingThread(final String str) {
            super("derby.slave.ping-" + str);
            this.pingMsg = new ReplicationMessage(13, null);
        }
        
        public void run() {
            try {
                while (!ReplicationMessageReceive.this.killPingThread) {
                    synchronized (ReplicationMessageReceive.this.sendPingSemaphore) {
                        while (!ReplicationMessageReceive.this.doSendPing) {
                            try {
                                ReplicationMessageReceive.this.sendPingSemaphore.wait();
                            }
                            catch (InterruptedException ex) {
                                InterruptStatus.setInterrupted();
                            }
                        }
                        ReplicationMessageReceive.this.doSendPing = false;
                    }
                    if (ReplicationMessageReceive.this.killPingThread) {
                        break;
                    }
                    ReplicationMessageReceive.this.sendMessage(this.pingMsg);
                }
            }
            catch (IOException ex2) {}
        }
    }
}
