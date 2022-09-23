// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.raw.Loggable;

public class EndXact implements Loggable
{
    private int transactionStatus;
    private GlobalTransactionId xactId;
    
    public EndXact(final GlobalTransactionId xactId, final int transactionStatus) {
        this.xactId = xactId;
        this.transactionStatus = transactionStatus;
    }
    
    public EndXact() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.xactId);
        CompressedNumber.writeInt(objectOutput, this.transactionStatus);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.xactId = (GlobalTransactionId)objectInput.readObject();
        this.transactionStatus = CompressedNumber.readInt(objectInput);
    }
    
    public int getTypeFormatId() {
        return 102;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) {
        if ((this.transactionStatus & 0x2) == 0x0) {
            ((RawTransaction)transaction).removeUpdateTransaction();
        }
        else {
            ((RawTransaction)transaction).prepareTransaction();
        }
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public boolean needsRedo(final Transaction transaction) {
        return true;
    }
    
    public void releaseResource(final Transaction transaction) {
    }
    
    public int group() {
        int n = 256;
        if ((this.transactionStatus & 0x4) != 0x0) {
            n |= 0x12;
        }
        else if ((this.transactionStatus & 0x1) != 0x0) {
            n |= 0x22;
        }
        else if ((this.transactionStatus & 0x2) != 0x0) {
            n |= 0x40;
        }
        return n;
    }
    
    public String toString() {
        return null;
    }
}
