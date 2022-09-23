// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.net;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.Externalizable;

public class ReplicationMessage implements Externalizable
{
    public static final long serialVersionUID = 1L;
    private Object message;
    private int type;
    public static final int TYPE_INITIATE_VERSION = 0;
    public static final int TYPE_INITIATE_INSTANT = 1;
    public static final int TYPE_LOG = 10;
    public static final int TYPE_ACK = 11;
    public static final int TYPE_ERROR = 12;
    public static final int TYPE_PING = 13;
    public static final int TYPE_PONG = 14;
    public static final int TYPE_STOP = 20;
    public static final int TYPE_FAILOVER = 21;
    
    public ReplicationMessage() {
    }
    
    public ReplicationMessage(final int type, final Object message) {
        this.type = type;
        this.message = message;
    }
    
    public Object getMessage() {
        return this.message;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        switch ((int)objectInput.readLong()) {
            case 1: {
                this.type = objectInput.readInt();
                this.message = objectInput.readObject();
                break;
            }
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(1L);
        objectOutput.writeInt(this.type);
        objectOutput.writeObject(this.message);
    }
}
