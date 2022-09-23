// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketConnection
{
    private final Socket socket;
    private final ObjectOutputStream objOutputStream;
    private final ObjectInputStream objInputStream;
    
    public SocketConnection(final Socket socket) throws IOException {
        this.socket = socket;
        this.objOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objInputStream = new ObjectInputStream(socket.getInputStream());
    }
    
    public Object readMessage() throws ClassNotFoundException, IOException {
        return this.objInputStream.readObject();
    }
    
    public void writeMessage(final Object obj) throws IOException {
        this.objOutputStream.reset();
        this.objOutputStream.writeObject(obj);
        this.objOutputStream.flush();
    }
    
    public void tearDown() throws IOException {
        try {
            this.objInputStream.close();
            this.objOutputStream.close();
        }
        finally {
            this.socket.close();
        }
    }
}
