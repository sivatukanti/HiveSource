// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecAggregator;

abstract class SystemAggregator implements ExecAggregator
{
    private boolean eliminatedNulls;
    
    public boolean didEliminateNulls() {
        return this.eliminatedNulls;
    }
    
    public void accumulate(final DataValueDescriptor dataValueDescriptor, final Object o) throws StandardException {
        if (dataValueDescriptor == null || dataValueDescriptor.isNull()) {
            this.eliminatedNulls = true;
            return;
        }
        this.accumulate(dataValueDescriptor);
    }
    
    protected abstract void accumulate(final DataValueDescriptor p0) throws StandardException;
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeBoolean(this.eliminatedNulls);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.eliminatedNulls = objectInput.readBoolean();
    }
    
    public String toString() {
        try {
            return super.toString() + "[" + this.getResult().getString() + "]";
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }
}
