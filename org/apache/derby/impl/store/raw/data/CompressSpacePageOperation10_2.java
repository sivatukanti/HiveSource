// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;

public final class CompressSpacePageOperation10_2 extends CompressSpacePageOperation
{
    public CompressSpacePageOperation10_2(final AllocPage allocPage, final int n, final int n2) throws StandardException {
        super(allocPage, n, n2);
    }
    
    public CompressSpacePageOperation10_2() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        CompressedNumber.writeInt(objectOutput, this.newHighestPage);
        CompressedNumber.writeInt(objectOutput, this.num_pages_truncated);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.newHighestPage = CompressedNumber.readInt(objectInput);
        this.num_pages_truncated = CompressedNumber.readInt(objectInput);
    }
    
    public int getTypeFormatId() {
        return 454;
    }
}
