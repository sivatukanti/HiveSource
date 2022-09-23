// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.catalog.TypeDescriptor;

public class RowMultiSetImpl extends BaseTypeIdImpl
{
    private String[] _columnNames;
    private TypeDescriptor[] _types;
    
    public RowMultiSetImpl() {
    }
    
    public RowMultiSetImpl(final String[] array, final TypeDescriptor[] array2) {
        this._columnNames = array;
        this._types = array2;
        if (array == null || array2 == null || array.length != array2.length) {
            throw new IllegalArgumentException("Bad args: columnNames = " + array + ". types = " + array2);
        }
    }
    
    public String[] getColumnNames() {
        return this._columnNames;
    }
    
    public TypeDescriptor[] getTypes() {
        return this._types;
    }
    
    public String getSQLTypeName() {
        final StringBuffer sb = new StringBuffer();
        final int length = this._columnNames.length;
        sb.append("TABLE ( ");
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('\"');
            sb.append(this._columnNames[i]);
            sb.append('\"');
            sb.append(' ');
            sb.append(this._types[i].getSQLstring());
        }
        sb.append(" )");
        return sb.toString();
    }
    
    public int getJDBCTypeId() {
        return 1111;
    }
    
    public int getTypeFormatId() {
        return 469;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final int int1 = objectInput.readInt();
        this._columnNames = new String[int1];
        this._types = new TypeDescriptor[int1];
        for (int i = 0; i < int1; ++i) {
            this._columnNames[i] = objectInput.readUTF();
        }
        for (int j = 0; j < int1; ++j) {
            this._types[j] = (TypeDescriptor)objectInput.readObject();
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final int length = this._columnNames.length;
        objectOutput.writeInt(length);
        for (int i = 0; i < length; ++i) {
            objectOutput.writeUTF(this._columnNames[i]);
        }
        for (int j = 0; j < length; ++j) {
            objectOutput.writeObject(this._types[j]);
        }
    }
}
