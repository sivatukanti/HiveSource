// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface RowSource
{
    DataValueDescriptor[] getNextRowFromRowSource() throws StandardException;
    
    boolean needsToClone();
    
    FormatableBitSet getValidColumns();
    
    void closeRowSource();
}
