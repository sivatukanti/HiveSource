// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.lang.reflect.Array;

public class FormatableArrayHolder implements Formatable
{
    private Object[] array;
    
    public FormatableArrayHolder() {
    }
    
    public FormatableArrayHolder(final Object[] array) {
        this.array = array;
    }
    
    public void setArray(final Object[] array) {
        this.array = array;
    }
    
    public Object[] getArray(final Class componentType) {
        final Object[] array = (Object[])Array.newInstance(componentType, this.array.length);
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.array[i];
        }
        return array;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        ArrayUtil.writeArrayLength(objectOutput, this.array);
        ArrayUtil.writeArrayItems(objectOutput, this.array);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        ArrayUtil.readArrayItems(objectInput, this.array = new Object[ArrayUtil.readArrayLength(objectInput)]);
    }
    
    public int getTypeFormatId() {
        return 270;
    }
}
