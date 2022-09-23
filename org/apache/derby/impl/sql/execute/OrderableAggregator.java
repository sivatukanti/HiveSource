// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.DataValueDescriptor;

abstract class OrderableAggregator extends SystemAggregator
{
    protected DataValueDescriptor value;
    
    public void setup(final ClassFactory classFactory, final String s, final DataTypeDescriptor dataTypeDescriptor) {
    }
    
    public void merge(final ExecAggregator execAggregator) throws StandardException {
        final DataValueDescriptor value = ((OrderableAggregator)execAggregator).value;
        if (value != null) {
            this.accumulate(value);
        }
    }
    
    public DataValueDescriptor getResult() throws StandardException {
        return this.value;
    }
    
    public String toString() {
        try {
            return "OrderableAggregator: " + this.value.getString();
        }
        catch (StandardException ex) {
            return super.toString() + ":" + ex.getMessage();
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.value);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.value = (DataValueDescriptor)objectInput.readObject();
    }
}
