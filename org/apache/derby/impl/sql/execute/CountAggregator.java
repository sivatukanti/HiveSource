// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;

public final class CountAggregator extends SystemAggregator
{
    private long value;
    private boolean isCountStar;
    
    public void setup(final ClassFactory classFactory, final String s, final DataTypeDescriptor dataTypeDescriptor) {
        this.isCountStar = s.equals("COUNT(*)");
    }
    
    public void merge(final ExecAggregator execAggregator) throws StandardException {
        this.value += ((CountAggregator)execAggregator).value;
    }
    
    public DataValueDescriptor getResult() {
        return new SQLLongint(this.value);
    }
    
    public void accumulate(final DataValueDescriptor dataValueDescriptor, final Object o) throws StandardException {
        if (this.isCountStar) {
            ++this.value;
        }
        else {
            super.accumulate(dataValueDescriptor, o);
        }
    }
    
    protected final void accumulate(final DataValueDescriptor dataValueDescriptor) {
        ++this.value;
    }
    
    public ExecAggregator newAggregator() {
        final CountAggregator countAggregator = new CountAggregator();
        countAggregator.isCountStar = this.isCountStar;
        return countAggregator;
    }
    
    public boolean isCountStar() {
        return this.isCountStar;
    }
    
    public final void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeBoolean(this.isCountStar);
        objectOutput.writeLong(this.value);
    }
    
    public final void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.isCountStar = objectInput.readBoolean();
        this.value = objectInput.readLong();
    }
    
    public int getTypeFormatId() {
        return 151;
    }
}
