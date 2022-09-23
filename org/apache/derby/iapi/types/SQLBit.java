// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.util.StringUtil;
import java.util.Arrays;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;

public class SQLBit extends SQLBinary
{
    public Object getObject() throws StandardException {
        return this.getBytes();
    }
    
    public String getTypeName() {
        return "CHAR () FOR BIT DATA";
    }
    
    int getMaxMemoryUsage() {
        return 254;
    }
    
    public int getTypeFormatId() {
        return 87;
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLBit();
    }
    
    public final void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        this.setValue(set.getBytes(n));
    }
    
    public int typePrecedence() {
        return 140;
    }
    
    final void setObject(final Object o) throws StandardException {
        this.setValue((byte[])o);
    }
    
    public SQLBit() {
    }
    
    public SQLBit(final byte[] dataValue) {
        this.dataValue = dataValue;
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final int maximumWidth = dataTypeDescriptor.getMaximumWidth();
        this.setValue(dataValueDescriptor.getBytes());
        this.setWidth(maximumWidth, 0, true);
    }
    
    public void setWidth(final int i, final int n, final boolean b) throws StandardException {
        if (this.getValue() == null) {
            return;
        }
        final int length = this.dataValue.length;
        if (length < i) {
            final byte[] array = new byte[i];
            System.arraycopy(this.dataValue, 0, array, 0, this.dataValue.length);
            Arrays.fill(array, this.dataValue.length, array.length, (byte)32);
            this.dataValue = array;
        }
        else if (length > i) {
            if (b) {
                for (int j = i; j < this.dataValue.length; ++j) {
                    if (this.dataValue[j] != 32) {
                        throw StandardException.newException("22001", this.getTypeName(), StringUtil.formatForPrint(this.toString()), String.valueOf(i));
                    }
                }
            }
            this.truncate(length, i, !b);
        }
    }
}
