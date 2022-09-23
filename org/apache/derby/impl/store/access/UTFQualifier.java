// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;

public class UTFQualifier implements Qualifier
{
    private UTF value;
    private int columnId;
    
    public UTFQualifier(final int columnId, final String s) {
        this.columnId = columnId;
        this.value = new UTF(s);
    }
    
    public int getColumnId() {
        return this.columnId;
    }
    
    public DataValueDescriptor getOrderable() {
        return this.value;
    }
    
    public int getOperator() {
        return 2;
    }
    
    public boolean negateCompareResult() {
        return false;
    }
    
    public boolean getOrderedNulls() {
        return false;
    }
    
    public boolean getUnknownRV() {
        return false;
    }
    
    public void clearOrderableCache() {
    }
    
    public void reinitialize() {
    }
}
