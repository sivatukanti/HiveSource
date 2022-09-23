// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

public final class CONSTANT_Utf8_info extends ConstantPoolEntry
{
    private final String value;
    private int asString;
    private int asCode;
    
    CONSTANT_Utf8_info(final String value) {
        super(1);
        this.value = value;
    }
    
    Object getKey() {
        return this.value;
    }
    
    int classFileSize() {
        return 3 + this.value.length();
    }
    
    public String toString() {
        return this.value;
    }
    
    int setAsCode() {
        if (ClassHolder.isExternalClassName(this.value)) {
            if (this.asString == 0) {
                this.asCode = this.getIndex();
            }
            return this.asCode;
        }
        return this.getIndex();
    }
    
    int setAsString() {
        if (ClassHolder.isExternalClassName(this.value)) {
            if (this.asCode == 0) {
                this.asString = this.getIndex();
            }
            return this.asString;
        }
        return this.getIndex();
    }
    
    void setAlternative(final int n) {
        if (this.asCode == 0) {
            this.asCode = n;
        }
        else {
            this.asString = n;
        }
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        super.put(classFormatOutput);
        if (this.getIndex() == this.asCode) {
            classFormatOutput.writeUTF(ClassHolder.convertToInternalClassName(this.value));
        }
        else {
            classFormatOutput.writeUTF(this.value);
        }
    }
}
