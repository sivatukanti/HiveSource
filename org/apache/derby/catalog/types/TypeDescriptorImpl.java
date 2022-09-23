// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.TypeDescriptor;

public class TypeDescriptorImpl implements TypeDescriptor, Formatable
{
    private BaseTypeIdImpl typeId;
    private int precision;
    private int scale;
    private boolean isNullable;
    private int maximumWidth;
    private int collationType;
    
    public TypeDescriptorImpl() {
        this.collationType = 0;
    }
    
    public TypeDescriptorImpl(final BaseTypeIdImpl typeId, final int precision, final int scale, final boolean isNullable, final int maximumWidth) {
        this.collationType = 0;
        this.typeId = typeId;
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
        this.maximumWidth = maximumWidth;
    }
    
    public TypeDescriptorImpl(final BaseTypeIdImpl typeId, final int precision, final int scale, final boolean isNullable, final int maximumWidth, final int collationType) {
        this.collationType = 0;
        this.typeId = typeId;
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
        this.maximumWidth = maximumWidth;
        this.collationType = collationType;
    }
    
    public TypeDescriptorImpl(final BaseTypeIdImpl typeId, final boolean isNullable, final int maximumWidth) {
        this.collationType = 0;
        this.typeId = typeId;
        this.isNullable = isNullable;
        this.maximumWidth = maximumWidth;
        this.scale = 0;
        this.precision = 0;
    }
    
    public TypeDescriptorImpl(final TypeDescriptorImpl typeDescriptorImpl, final int precision, final int scale, final boolean isNullable, final int maximumWidth) {
        this.collationType = 0;
        this.typeId = typeDescriptorImpl.typeId;
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
        this.maximumWidth = maximumWidth;
    }
    
    public TypeDescriptorImpl(final TypeDescriptorImpl typeDescriptorImpl, final int precision, final int scale, final boolean isNullable, final int maximumWidth, final int collationType) {
        this.collationType = 0;
        this.typeId = typeDescriptorImpl.typeId;
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
        this.maximumWidth = maximumWidth;
        this.collationType = collationType;
    }
    
    public TypeDescriptorImpl(final TypeDescriptorImpl typeDescriptorImpl, final boolean isNullable, final int maximumWidth) {
        this.collationType = 0;
        this.typeId = typeDescriptorImpl.typeId;
        this.precision = typeDescriptorImpl.precision;
        this.scale = typeDescriptorImpl.scale;
        this.isNullable = isNullable;
        this.maximumWidth = maximumWidth;
    }
    
    public int getMaximumWidth() {
        return this.maximumWidth;
    }
    
    public int getMaximumWidthInBytes() {
        switch (this.typeId.getJDBCTypeId()) {
            case -7:
            case -6:
            case -4:
            case -3:
            case -2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 2004: {
                return this.maximumWidth;
            }
            case -5: {
                return 40;
            }
            case 91:
            case 92: {
                return 6;
            }
            case 93: {
                return 16;
            }
            case 2:
            case 3: {
                return 2 * (this.precision + 2);
            }
            case -1:
            case 1:
            case 12:
            case 2005: {
                if (this.maximumWidth > 0 && 2 * this.maximumWidth < 0) {
                    return Integer.MAX_VALUE;
                }
                return 2 * this.maximumWidth;
            }
            case 16: {
                return 1;
            }
            default: {
                return -1;
            }
        }
    }
    
    public boolean isStringType() {
        switch (this.typeId.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public int getJDBCTypeId() {
        return this.typeId.getJDBCTypeId();
    }
    
    public String getTypeName() {
        return this.typeId.getSQLTypeName();
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    public boolean isNullable() {
        return this.isNullable;
    }
    
    public boolean isRowMultiSet() {
        return this.typeId instanceof RowMultiSetImpl;
    }
    
    public boolean isUserDefinedType() {
        return this.typeId.userType();
    }
    
    public int getCollationType() {
        return this.collationType;
    }
    
    public void setCollationType(final int collationType) {
        this.collationType = collationType;
    }
    
    public String getSQLstring() {
        return this.typeId.toParsableString(this);
    }
    
    public String toString() {
        final String sqLstring = this.getSQLstring();
        if (!this.isNullable()) {
            return sqLstring + " NOT NULL";
        }
        return sqLstring;
    }
    
    public BaseTypeIdImpl getTypeId() {
        return this.typeId;
    }
    
    public boolean equals(final Object o) {
        final TypeDescriptor typeDescriptor = (TypeDescriptor)o;
        if (!this.getTypeName().equals(typeDescriptor.getTypeName()) || this.precision != typeDescriptor.getPrecision() || this.scale != typeDescriptor.getScale() || this.isNullable != typeDescriptor.isNullable() || this.maximumWidth != typeDescriptor.getMaximumWidth()) {
            return false;
        }
        switch (this.typeId.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                return this.collationType == typeDescriptor.getCollationType();
            }
            default: {
                return true;
            }
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.typeId = (BaseTypeIdImpl)objectInput.readObject();
        this.precision = objectInput.readInt();
        switch (this.typeId.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                this.scale = 0;
                this.collationType = objectInput.readInt();
                break;
            }
            default: {
                this.scale = objectInput.readInt();
                this.collationType = 0;
                break;
            }
        }
        this.isNullable = objectInput.readBoolean();
        this.maximumWidth = objectInput.readInt();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.typeId);
        objectOutput.writeInt(this.precision);
        switch (this.typeId.getJDBCTypeId()) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                objectOutput.writeInt(this.collationType);
                break;
            }
            default: {
                objectOutput.writeInt(this.scale);
                break;
            }
        }
        objectOutput.writeBoolean(this.isNullable);
        objectOutput.writeInt(this.maximumWidth);
    }
    
    public int getTypeFormatId() {
        return 14;
    }
    
    public String[] getRowColumnNames() {
        if (!this.isRowMultiSet()) {
            return null;
        }
        return ((RowMultiSetImpl)this.typeId).getColumnNames();
    }
    
    public TypeDescriptor[] getRowTypes() {
        if (!this.isRowMultiSet()) {
            return null;
        }
        return ((RowMultiSetImpl)this.typeId).getTypes();
    }
}
