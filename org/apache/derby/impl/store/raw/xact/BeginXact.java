// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.raw.Loggable;

public class BeginXact implements Loggable
{
    protected int transactionStatus;
    protected GlobalTransactionId xactId;
    
    public BeginXact(final GlobalTransactionId xactId, final int transactionStatus) {
        this.xactId = xactId;
        this.transactionStatus = transactionStatus;
    }
    
    public BeginXact() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.transactionStatus);
        objectOutput.writeObject(this.xactId);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.transactionStatus = objectInput.readInt();
        this.xactId = (GlobalTransactionId)objectInput.readObject();
    }
    
    public int getTypeFormatId() {
        return 169;
    }
    
    public void doMe(final Transaction transaction, final LogInstant firstLogInstant, final LimitObjectInput limitObjectInput) {
        final RawTransaction rawTransaction = (RawTransaction)transaction;
        if (firstLogInstant != null) {
            rawTransaction.setFirstLogInstant(firstLogInstant);
            rawTransaction.addUpdateTransaction(this.transactionStatus);
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
        return 257;
    }
    
    public String toString() {
        return null;
    }
    
    public GlobalTransactionId getGlobalId() {
        return this.xactId;
    }
}
