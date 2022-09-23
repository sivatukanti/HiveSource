// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;

public final class MaxMinAggregator extends OrderableAggregator
{
    private boolean isMax;
    
    public void setup(final ClassFactory classFactory, final String s, final DataTypeDescriptor dataTypeDescriptor) {
        super.setup(classFactory, s, dataTypeDescriptor);
        this.isMax = s.equals("MAX");
    }
    
    protected void accumulate(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.value == null || (this.isMax && this.value.compare(dataValueDescriptor) < 0) || (!this.isMax && this.value.compare(dataValueDescriptor) > 0)) {
            this.value = dataValueDescriptor.cloneValue(false);
        }
    }
    
    public ExecAggregator newAggregator() {
        final MaxMinAggregator maxMinAggregator = new MaxMinAggregator();
        maxMinAggregator.isMax = this.isMax;
        return maxMinAggregator;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeBoolean(this.isMax);
        super.writeExternal(objectOutput);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.isMax = objectInput.readBoolean();
        super.readExternal(objectInput);
    }
    
    public int getTypeFormatId() {
        return 152;
    }
}
