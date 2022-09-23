// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.store.access.Qualifier;

public class GenericQualifier implements Qualifier
{
    private int columnId;
    private int operator;
    private GeneratedMethod orderableGetter;
    private Activation activation;
    private boolean orderedNulls;
    private boolean unknownRV;
    private boolean negateCompareResult;
    protected int variantType;
    private DataValueDescriptor orderableCache;
    
    public GenericQualifier(final int columnId, final int operator, final GeneratedMethod orderableGetter, final Activation activation, final boolean orderedNulls, final boolean unknownRV, final boolean negateCompareResult, final int variantType) {
        this.orderableCache = null;
        this.columnId = columnId;
        this.operator = operator;
        this.orderableGetter = orderableGetter;
        this.activation = activation;
        this.orderedNulls = orderedNulls;
        this.unknownRV = unknownRV;
        this.negateCompareResult = negateCompareResult;
        this.variantType = variantType;
    }
    
    public int getColumnId() {
        return this.columnId;
    }
    
    public DataValueDescriptor getOrderable() throws StandardException {
        if (this.variantType != 0) {
            if (this.orderableCache == null) {
                this.orderableCache = (DataValueDescriptor)this.orderableGetter.invoke(this.activation);
            }
            return this.orderableCache;
        }
        return (DataValueDescriptor)this.orderableGetter.invoke(this.activation);
    }
    
    public int getOperator() {
        return this.operator;
    }
    
    public boolean negateCompareResult() {
        return this.negateCompareResult;
    }
    
    public boolean getOrderedNulls() {
        return this.orderedNulls;
    }
    
    public boolean getUnknownRV() {
        return this.unknownRV;
    }
    
    public void clearOrderableCache() {
        if (this.variantType == 1 || this.variantType == 0) {
            this.orderableCache = null;
        }
    }
    
    public void reinitialize() {
        if (this.variantType != 3) {
            this.orderableCache = null;
        }
    }
    
    public String toString() {
        return "";
    }
}
