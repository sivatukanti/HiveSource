// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;

public class SetReservedSpaceOperation extends PageBasicOperation
{
    protected int doMeSlot;
    protected int recordId;
    protected int newValue;
    protected int oldValue;
    
    public SetReservedSpaceOperation(final BasePage basePage, final int doMeSlot, final int recordId, final int newValue, final int oldValue) {
        super(basePage);
        this.doMeSlot = doMeSlot;
        this.recordId = recordId;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }
    
    public SetReservedSpaceOperation() {
    }
    
    public int getTypeFormatId() {
        return 287;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.doMeSlot);
        CompressedNumber.writeInt(objectOutput, this.recordId);
        CompressedNumber.writeInt(objectOutput, this.newValue);
        CompressedNumber.writeInt(objectOutput, this.oldValue);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.doMeSlot = CompressedNumber.readInt(objectInput);
        this.recordId = CompressedNumber.readInt(objectInput);
        this.newValue = CompressedNumber.readInt(objectInput);
        this.oldValue = CompressedNumber.readInt(objectInput);
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.setReservedSpace(logInstant, this.doMeSlot, this.newValue);
    }
    
    public void restoreMe(final Transaction transaction, final BasePage basePage, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException, IOException {
        this.page.setReservedSpace(logInstant, basePage.findRecordById(this.recordId, 0), this.oldValue);
    }
    
    public String toString() {
        return null;
    }
}
