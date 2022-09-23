// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.net;

import java.net.SocketTimeoutException;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.error.StandardException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import javax.net.SocketFactory;
import java.security.PrivilegedExceptionAction;
import java.net.Socket;

public class ReplicationMessageTransmit
{
    private final int DEFAULT_MESSAGE_RESPONSE_TIMEOUT = 30000;
    private final Object receiveSemaphore;
    private ReplicationMessage receivedMsg;
    private volatile boolean stopMessageReceiver;
    private final SlaveAddress slaveAddress;
    private SocketConnection socketConn;
    private String dbname;
    
    public ReplicationMessageTransmit(final SlaveAddress slaveAddress) {
        this.receiveSemaphore = new Object();
        this.receivedMsg = null;
        this.stopMessageReceiver = false;
        this.slaveAddress = slaveAddress;
    }
    
    public void initConnection(final int n, final long n2) throws IOException, StandardException, ClassNotFoundException {
        Socket socket;
        try {
            socket = AccessController.doPrivileged((PrivilegedExceptionAction<Socket>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    final SocketFactory default1 = SocketFactory.getDefault();
                    final InetSocketAddress endpoint = new InetSocketAddress(ReplicationMessageTransmit.this.slaveAddress.getHostAddress(), ReplicationMessageTransmit.this.slaveAddress.getPortNumber());
                    final Socket socket = default1.createSocket();
                    socket.connect(endpoint, n);
                    return socket;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        socket.setKeepAlive(true);
        this.socketConn = new SocketConnection(socket);
        this.startMessageReceiverThread(this.dbname);
        this.brokerConnection(n2);
    }
    
    public void tearDown() throws IOException {
        this.stopMessageReceiver = true;
        if (this.socketConn != null) {
            this.socketConn.tearDown();
            this.socketConn = null;
        }
    }
    
    public void sendMessage(final ReplicationMessage replicationMessage) throws IOException {
        this.checkSocketConnection();
        this.socketConn.writeMessage(replicationMessage);
    }
    
    public synchronized ReplicationMessage sendMessageWaitForReply(final ReplicationMessage replicationMessage) throws IOException, StandardException {
        this.receivedMsg = null;
        this.checkSocketConnection();
        this.socketConn.writeMessage(replicationMessage);
        final long currentTimeMillis = System.currentTimeMillis();
        long n = 0L;
        while (this.receivedMsg == null && n < 30000L) {
            synchronized (this.receiveSemaphore) {
                try {
                    this.receiveSemaphore.wait(30000L - n);
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                    n = System.currentTimeMillis() - currentTimeMillis;
                    continue;
                }
            }
            break;
        }
        if (this.receivedMsg == null) {
            throw StandardException.newException("XRE04.C.2", this.dbname);
        }
        return this.receivedMsg;
    }
    
    private void brokerConnection(final long value) throws IOException, StandardException, ClassNotFoundException {
        this.verifyMessageType(this.sendMessageWaitForReply(new ReplicationMessage(0, new Long(1L))), 11);
        this.verifyMessageType(this.sendMessageWaitForReply(new ReplicationMessage(1, new Long(value))), 11);
    }
    
    private boolean verifyMessageType(final ReplicationMessage replicationMessage, final int n) throws StandardException {
        if (replicationMessage.getType() == n) {
            return true;
        }
        if (replicationMessage.getType() == 12) {
            final String[] array = (String[])replicationMessage.getMessage();
            throw StandardException.newException(array[array.length - 1], array);
        }
        throw StandardException.newException("XRE03");
    }
    
    private void checkSocketConnection() throws IOException {
        if (this.socketConn == null) {
            throw new IOException("R012");
        }
    }
    
    private void startMessageReceiverThread(final String s) {
        final MasterReceiverThread masterReceiverThread = new MasterReceiverThread(s);
        masterReceiverThread.setDaemon(true);
        masterReceiverThread.start();
    }
    
    private class MasterReceiverThread extends Thread
    {
        private final ReplicationMessage pongMsg;
        
        MasterReceiverThread(final String str) {
            super("derby.master.receiver-" + str);
            this.pongMsg = new ReplicationMessage(14, null);
        }
        
        public void run() {
            while (!ReplicationMessageTransmit.this.stopMessageReceiver) {
                try {
                    final ReplicationMessage message = this.readMessage();
                    switch (message.getType()) {
                        case 13: {
                            ReplicationMessageTransmit.this.sendMessage(this.pongMsg);
                            continue;
                        }
                        case 11:
                        case 12: {
                            synchronized (ReplicationMessageTransmit.this.receiveSemaphore) {
                                ReplicationMessageTransmit.this.receivedMsg = message;
                                ReplicationMessageTransmit.this.receiveSemaphore.notify();
                            }
                            continue;
                        }
                    }
                }
                catch (SocketTimeoutException ex) {}
                catch (ClassNotFoundException ex2) {}
                catch (IOException ex3) {
                    ReplicationMessageTransmit.this.stopMessageReceiver = true;
                }
            }
        }
        
        private ReplicationMessage readMessage() throws ClassNotFoundException, IOException {
            ReplicationMessageTransmit.this.checkSocketConnection();
            return (ReplicationMessage)ReplicationMessageTransmit.this.socketConn.readMessage();
        }
    }
}
