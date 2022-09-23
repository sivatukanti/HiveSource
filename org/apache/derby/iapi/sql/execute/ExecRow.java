// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.Row;

public interface ExecRow extends Row
{
    ExecRow getClone();
    
    ExecRow getClone(final FormatableBitSet p0);
    
    ExecRow getNewNullRow();
    
    void resetRowArray();
    
    DataValueDescriptor cloneColumn(final int p0);
    
    DataValueDescriptor[] getRowArrayClone();
    
    DataValueDescriptor[] getRowArray();
    
    void setRowArray(final DataValueDescriptor[] p0);
    
    void getNewObjectArray();
}
