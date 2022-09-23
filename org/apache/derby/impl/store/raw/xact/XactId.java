// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.xact.TransactionId;

public class XactId implements TransactionId
{
    private long id;
    
    public XactId(final long id) {
        this.id = id;
    }
    
    public XactId() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        CompressedNumber.writeLong(objectOutput, this.id);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.id = CompressedNumber.readLong(objectInput);
    }
    
    public int getTypeFormatId() {
        return 147;
    }
    
    public int getMaxStoredSize() {
        return FormatIdUtil.getFormatIdByteLength(147) + 8;
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        try {
            return this.id == ((XactId)o).id;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }
    
    public int hashCode() {
        return (int)this.id;
    }
    
    public static long compare(final TransactionId transactionId, final TransactionId transactionId2) {
        if (transactionId != null && transactionId2 != null) {
            return ((XactId)transactionId).id - ((XactId)transactionId2).id;
        }
        if (transactionId == null) {
            return -1L;
        }
        if (transactionId2 == null) {
            return 1L;
        }
        return 0L;
    }
    
    protected long getId() {
        return this.id;
    }
    
    public String toString() {
        return Long.toString(this.id);
    }
}
