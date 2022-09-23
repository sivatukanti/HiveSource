// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.types.DataValueDescriptor;

public class SumAggregator extends OrderableAggregator
{
    protected void accumulate(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.value == null) {
            this.value = dataValueDescriptor.cloneValue(false);
        }
        else {
            final NumberDataValue numberDataValue = (NumberDataValue)dataValueDescriptor;
            final NumberDataValue numberDataValue2 = (NumberDataValue)this.value;
            this.value = numberDataValue2.plus(numberDataValue, numberDataValue2, numberDataValue2);
        }
    }
    
    public ExecAggregator newAggregator() {
        return new SumAggregator();
    }
    
    public int getTypeFormatId() {
        return 154;
    }
    
    public String toString() {
        try {
            return "SumAggregator: " + this.value.getString();
        }
        catch (StandardException ex) {
            return super.toString() + ":" + ex.getMessage();
        }
    }
}
