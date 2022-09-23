// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ScanQualifier;

public class GenericScanQualifier implements ScanQualifier
{
    private int columnId;
    private DataValueDescriptor orderable;
    private int operator;
    private boolean negateCR;
    private boolean orderedNulls;
    private boolean unknownRV;
    private boolean properInit;
    
    public GenericScanQualifier() {
        this.columnId = -1;
        this.orderable = null;
        this.operator = -1;
        this.negateCR = false;
        this.orderedNulls = false;
        this.unknownRV = false;
        this.properInit = false;
    }
    
    public int getColumnId() {
        return this.columnId;
    }
    
    public DataValueDescriptor getOrderable() {
        return this.orderable;
    }
    
    public int getOperator() {
        return this.operator;
    }
    
    public boolean negateCompareResult() {
        return this.negateCR;
    }
    
    public boolean getOrderedNulls() {
        return this.orderedNulls;
    }
    
    public boolean getUnknownRV() {
        return this.unknownRV;
    }
    
    public void clearOrderableCache() {
    }
    
    public void reinitialize() {
    }
    
    public void setQualifier(final int columnId, final DataValueDescriptor orderable, final int operator, final boolean negateCR, final boolean orderedNulls, final boolean unknownRV) {
        this.columnId = columnId;
        this.orderable = orderable;
        this.operator = operator;
        this.negateCR = negateCR;
        this.orderedNulls = orderedNulls;
        this.unknownRV = unknownRV;
        this.properInit = true;
    }
}
