// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.error.StandardException;

public class SQLVarbit extends SQLBit
{
    public String getTypeName() {
        return "VARCHAR () FOR BIT DATA";
    }
    
    int getMaxMemoryUsage() {
        return 32672;
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLVarbit();
    }
    
    public int getTypeFormatId() {
        return 88;
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final int maximumWidth = dataTypeDescriptor.getMaximumWidth();
        final byte[] bytes = dataValueDescriptor.getBytes();
        this.setValue(bytes);
        if (bytes.length > maximumWidth) {
            this.setWidth(maximumWidth, 0, true);
        }
    }
    
    public void setWidth(final int i, final int n, final boolean b) throws StandardException {
        if (this.getValue() == null) {
            return;
        }
        final int length = this.dataValue.length;
        if (length > i) {
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
    
    public SQLVarbit() {
    }
    
    public SQLVarbit(final byte[] array) {
        super(array);
    }
    
    public int typePrecedence() {
        return 150;
    }
}
