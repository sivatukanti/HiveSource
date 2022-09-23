// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.DefaultInfo;

public class DefaultInfoImpl implements DefaultInfo, Formatable
{
    private DataValueDescriptor defaultValue;
    private String defaultText;
    private int type;
    private String[] referencedColumnNames;
    private String originalCurrentSchema;
    private static final int BITS_MASK_IS_DEFAULTVALUE_AUTOINC = 1;
    private static final int BITS_MASK_IS_GENERATED_COLUMN = 2;
    
    public DefaultInfoImpl() {
    }
    
    public DefaultInfoImpl(final boolean b, final String defaultText, final DataValueDescriptor defaultValue) {
        this.type = calcType(b);
        this.defaultText = defaultText;
        this.defaultValue = defaultValue;
    }
    
    public DefaultInfoImpl(final String defaultText, String[] referencedColumnNames, final String originalCurrentSchema) {
        if (referencedColumnNames == null) {
            referencedColumnNames = new String[0];
        }
        this.type = 2;
        this.defaultText = defaultText;
        this.referencedColumnNames = referencedColumnNames;
        this.originalCurrentSchema = originalCurrentSchema;
    }
    
    public String getDefaultText() {
        return this.defaultText;
    }
    
    public String[] getReferencedColumnNames() {
        return this.referencedColumnNames;
    }
    
    public String getOriginalCurrentSchema() {
        return this.originalCurrentSchema;
    }
    
    public String toString() {
        if (this.isDefaultValueAutoinc()) {
            return "GENERATED_BY_DEFAULT";
        }
        if (this.isGeneratedColumn()) {
            return "GENERATED ALWAYS AS ( " + this.defaultText + " )";
        }
        return this.defaultText;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.defaultText = (String)objectInput.readObject();
        this.defaultValue = (DataValueDescriptor)objectInput.readObject();
        this.type = objectInput.readInt();
        if (this.isGeneratedColumn()) {
            final int int1 = objectInput.readInt();
            this.referencedColumnNames = new String[int1];
            for (int i = 0; i < int1; ++i) {
                this.referencedColumnNames[i] = (String)objectInput.readObject();
            }
            this.originalCurrentSchema = (String)objectInput.readObject();
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.defaultText);
        objectOutput.writeObject(this.defaultValue);
        objectOutput.writeInt(this.type);
        if (this.isGeneratedColumn()) {
            final int length = this.referencedColumnNames.length;
            objectOutput.writeInt(length);
            for (int i = 0; i < length; ++i) {
                objectOutput.writeObject(this.referencedColumnNames[i]);
            }
            objectOutput.writeObject(this.originalCurrentSchema);
        }
    }
    
    public int getTypeFormatId() {
        return 326;
    }
    
    public DataValueDescriptor getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(final DataValueDescriptor defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public boolean isDefaultValueAutoinc() {
        return (this.type & 0x1) != 0x0;
    }
    
    public boolean isGeneratedColumn() {
        return (this.type & 0x2) != 0x0;
    }
    
    private static int calcType(final boolean b) {
        int n = 0;
        if (b) {
            n |= 0x1;
        }
        return n;
    }
}
