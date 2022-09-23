// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.OutputStream;
import java.io.IOException;

class AttributeEntry
{
    private int attribute_name_index;
    private ClassFormatOutput infoOut;
    byte[] infoIn;
    
    AttributeEntry(final int attribute_name_index, final ClassFormatOutput infoOut) {
        this.attribute_name_index = attribute_name_index;
        this.infoOut = infoOut;
    }
    
    AttributeEntry(final ClassInput classInput) throws IOException {
        this.attribute_name_index = classInput.getU2();
        this.infoIn = classInput.getU1Array(classInput.getU4());
    }
    
    int getNameIndex() {
        return this.attribute_name_index;
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        classFormatOutput.putU2(this.attribute_name_index);
        if (this.infoOut != null) {
            classFormatOutput.putU4(this.infoOut.size());
            this.infoOut.writeTo(classFormatOutput);
        }
        else {
            classFormatOutput.putU4(this.infoIn.length);
            classFormatOutput.write(this.infoIn);
        }
    }
    
    int classFileSize() {
        return 6 + ((this.infoOut != null) ? this.infoOut.size() : this.infoIn.length);
    }
}
