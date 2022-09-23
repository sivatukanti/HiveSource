// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class DecimalTypeIdImpl extends BaseTypeIdImpl
{
    public DecimalTypeIdImpl() {
    }
    
    public DecimalTypeIdImpl(final boolean b) {
        super(198);
        if (b) {
            this.setNumericType();
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final boolean boolean1 = objectInput.readBoolean();
        super.readExternal(objectInput);
        if (boolean1) {
            this.setNumericType();
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeBoolean(this.getJDBCTypeId() == 2);
        super.writeExternal(objectOutput);
    }
    
    private void setNumericType() {
        this.unqualifiedName = "NUMERIC";
        this.JDBCTypeId = 2;
    }
}
