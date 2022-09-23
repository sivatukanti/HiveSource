// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
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
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.store.raw.Loggable;

public class CheckpointOperation implements Loggable
{
    protected long redoLWM;
    protected long undoLWM;
    protected Formatable transactionTable;
    
    public CheckpointOperation(final long redoLWM, final long undoLWM, final Formatable transactionTable) {
        this.redoLWM = redoLWM;
        this.undoLWM = undoLWM;
        this.transactionTable = transactionTable;
    }
    
    public CheckpointOperation() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        CompressedNumber.writeLong(objectOutput, this.redoLWM);
        CompressedNumber.writeLong(objectOutput, this.undoLWM);
        CompressedNumber.writeInt(objectOutput, 0);
        if (this.transactionTable == null) {
            CompressedNumber.writeInt(objectOutput, 0);
        }
        else {
            CompressedNumber.writeInt(objectOutput, 1);
            objectOutput.writeObject(this.transactionTable);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.redoLWM = CompressedNumber.readLong(objectInput);
        this.undoLWM = CompressedNumber.readLong(objectInput);
        CompressedNumber.readInt(objectInput);
        if (CompressedNumber.readInt(objectInput) == 1) {
            this.transactionTable = (Formatable)objectInput.readObject();
        }
        else {
            this.transactionTable = null;
        }
    }
    
    public int getTypeFormatId() {
        return 263;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        if (((RawTransaction)transaction).inRollForwardRecovery()) {
            ((RawTransaction)transaction).checkpointInRollForwardRecovery(logInstant, this.redoLWM, this.undoLWM);
        }
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public boolean needsRedo(final Transaction transaction) {
        return ((RawTransaction)transaction).inRollForwardRecovery();
    }
    
    public void releaseResource(final Transaction transaction) {
    }
    
    public int group() {
        return 256;
    }
    
    public long redoLWM() {
        return this.redoLWM;
    }
    
    public long undoLWM() {
        return this.undoLWM;
    }
    
    public Formatable getTransactionTable() {
        return this.transactionTable;
    }
    
    public String toString() {
        return null;
    }
}
