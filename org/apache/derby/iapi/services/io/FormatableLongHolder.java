// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class FormatableLongHolder implements Formatable
{
    private long theLong;
    
    public FormatableLongHolder() {
    }
    
    public FormatableLongHolder(final long theLong) {
        this.theLong = theLong;
    }
    
    public void setLong(final int n) {
        this.theLong = n;
    }
    
    public long getLong() {
        return this.theLong;
    }
    
    public static FormatableLongHolder[] getFormatableLongHolders(final long[] array) {
        if (array == null) {
            return null;
        }
        final FormatableLongHolder[] array2 = new FormatableLongHolder[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new FormatableLongHolder(array[i]);
        }
        return array2;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(this.theLong);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.theLong = objectInput.readLong();
    }
    
    public int getTypeFormatId() {
        return 329;
    }
}
