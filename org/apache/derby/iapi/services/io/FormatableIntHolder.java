// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class FormatableIntHolder implements Formatable
{
    private int theInt;
    
    public FormatableIntHolder() {
    }
    
    public FormatableIntHolder(final int theInt) {
        this.theInt = theInt;
    }
    
    public void setInt(final int theInt) {
        this.theInt = theInt;
    }
    
    public int getInt() {
        return this.theInt;
    }
    
    public static FormatableIntHolder[] getFormatableIntHolders(final int[] array) {
        if (array == null) {
            return null;
        }
        final FormatableIntHolder[] array2 = new FormatableIntHolder[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new FormatableIntHolder(array[i]);
        }
        return array2;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.theInt);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.theInt = objectInput.readInt();
    }
    
    public int getTypeFormatId() {
        return 303;
    }
}
