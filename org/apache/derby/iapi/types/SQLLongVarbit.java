// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public class SQLLongVarbit extends SQLVarbit
{
    public String getTypeName() {
        return "LONG VARCHAR FOR BIT DATA";
    }
    
    int getMaxMemoryUsage() {
        return 32700;
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLLongVarbit();
    }
    
    public int getTypeFormatId() {
        return 234;
    }
    
    public SQLLongVarbit() {
    }
    
    public SQLLongVarbit(final byte[] array) {
        super(array);
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLLongVarbit) {
            final SQLLongVarbit sqlLongVarbit = (SQLLongVarbit)dataValueDescriptor;
            this.stream = sqlLongVarbit.stream;
            this.dataValue = sqlLongVarbit.dataValue;
        }
        else {
            this.setValue(dataValueDescriptor.getBytes());
        }
    }
    
    public int typePrecedence() {
        return 160;
    }
}
