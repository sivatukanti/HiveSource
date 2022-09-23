// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.store.raw.GlobalTransactionId;
import org.apache.derby.iapi.store.access.GlobalXact;

public class GlobalXactId extends GlobalXact implements GlobalTransactionId
{
    public GlobalXactId(final int format_id, final byte[] array, final byte[] array2) {
        this.format_id = format_id;
        System.arraycopy(array, 0, this.global_id = new byte[array.length], 0, array.length);
        System.arraycopy(array2, 0, this.branch_id = new byte[array2.length], 0, array2.length);
    }
    
    public GlobalXactId() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.format_id);
        objectOutput.write(this.global_id.length);
        if (this.global_id.length > 0) {
            objectOutput.write(this.global_id);
        }
        objectOutput.write(this.branch_id.length);
        if (this.branch_id.length > 0) {
            objectOutput.write(this.branch_id);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.format_id = objectInput.readInt();
        final int read = objectInput.read();
        this.global_id = new byte[read];
        if (read > 0) {
            objectInput.read(this.global_id);
        }
        final int read2 = objectInput.read();
        this.branch_id = new byte[read2];
        if (read2 > 0) {
            objectInput.read(this.branch_id);
        }
    }
    
    public int getTypeFormatId() {
        return 328;
    }
    
    public int getFormat_Id() {
        return this.format_id;
    }
    
    public byte[] getGlobalTransactionId() {
        return this.global_id;
    }
    
    public byte[] getBranchQualifier() {
        return this.branch_id;
    }
}
