// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.store.raw.Undoable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.RePreparable;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.Formatable;

public class LogRecord implements Formatable
{
    private TransactionId xactId;
    private Loggable op;
    private int group;
    transient ObjectInput input;
    private static final int formatLength;
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        CompressedNumber.writeInt(objectOutput, this.group);
        objectOutput.writeObject(this.xactId);
        objectOutput.writeObject(this.op);
    }
    
    public void readExternal(final ObjectInput input) throws IOException, ClassNotFoundException {
        this.group = CompressedNumber.readInt(input);
        this.input = input;
        this.xactId = null;
        this.op = null;
    }
    
    public int getTypeFormatId() {
        return 129;
    }
    
    public void setValue(final TransactionId xactId, final Loggable op) {
        this.xactId = xactId;
        this.op = op;
        this.group = op.group();
    }
    
    public static int formatOverhead() {
        return LogRecord.formatLength;
    }
    
    public static int maxGroupStoredSize() {
        return 4;
    }
    
    public static int maxTransactionIdStoredSize(final TransactionId transactionId) {
        return transactionId.getMaxStoredSize();
    }
    
    public static int getStoredSize(final int n, final TransactionId transactionId) {
        return LogRecord.formatLength + CompressedNumber.sizeInt(n) + FormatIdUtil.getFormatIdByteLength(0);
    }
    
    public TransactionId getTransactionId() throws IOException, ClassNotFoundException {
        if (this.xactId != null) {
            return this.xactId;
        }
        return this.xactId = (TransactionId)this.input.readObject();
    }
    
    public Loggable getLoggable() throws IOException, ClassNotFoundException {
        if (this.op != null) {
            return this.op;
        }
        if (this.xactId == null) {
            this.xactId = (TransactionId)this.input.readObject();
        }
        this.op = (Loggable)this.input.readObject();
        this.input = null;
        return this.op;
    }
    
    public RePreparable getRePreparable() throws IOException, ClassNotFoundException {
        return (RePreparable)this.getLoggable();
    }
    
    public void skipLoggable() throws StandardException {
        if (this.op != null) {
            return;
        }
        try {
            if (this.xactId == null) {
                this.xactId = (TransactionId)this.input.readObject();
            }
            if (this.op == null) {
                this.op = (Loggable)this.input.readObject();
            }
        }
        catch (ClassNotFoundException ex) {
            throw StandardException.newException("XSLA3.D", ex);
        }
        catch (IOException ex2) {
            throw StandardException.newException("XSLA3.D", ex2);
        }
    }
    
    public Undoable getUndoable() throws IOException, ClassNotFoundException {
        if (this.op == null) {
            this.getLoggable();
        }
        if (this.op instanceof Undoable) {
            return (Undoable)this.op;
        }
        return null;
    }
    
    public boolean isCLR() {
        return (this.group & 0x4) != 0x0;
    }
    
    public boolean isFirst() {
        return (this.group & 0x1) != 0x0;
    }
    
    public boolean isComplete() {
        return (this.group & 0x2) != 0x0;
    }
    
    public boolean isPrepare() {
        return (this.group & 0x40) != 0x0;
    }
    
    public boolean requiresPrepareLocks() {
        return (this.group & 0x80) != 0x0;
    }
    
    public boolean isCommit() {
        return (this.group & 0x10) != 0x0;
    }
    
    public boolean isAbort() {
        return (this.group & 0x20) != 0x0;
    }
    
    public int group() {
        return this.group;
    }
    
    public boolean isChecksum() {
        return (this.group & 0x800) != 0x0;
    }
    
    static {
        formatLength = FormatIdUtil.getFormatIdByteLength(129);
    }
}
