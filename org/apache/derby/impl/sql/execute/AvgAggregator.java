// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.sql.execute.ExecAggregator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.SQLDouble;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.types.DataValueDescriptor;

public final class AvgAggregator extends SumAggregator
{
    private long count;
    private int scale;
    
    protected void accumulate(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.count == 0L) {
            final String typeName = dataValueDescriptor.getTypeName();
            if (typeName.equals("TINYINT") || typeName.equals("SMALLINT") || typeName.equals("INTEGER") || typeName.equals("BIGINT")) {
                this.scale = 0;
            }
            else if (typeName.equals("REAL") || typeName.equals("DOUBLE")) {
                this.scale = 31;
            }
            else {
                this.scale = ((NumberDataValue)dataValueDescriptor).getDecimalValueScale();
                if (this.scale < 4) {
                    this.scale = 4;
                }
            }
        }
        try {
            super.accumulate(dataValueDescriptor);
            ++this.count;
        }
        catch (StandardException ex) {
            if (!ex.getMessageId().equals("22003")) {
                throw ex;
            }
            final String typeName2 = this.value.getTypeName();
            DataValueDescriptor null;
            if (typeName2.equals("INTEGER")) {
                null = new SQLLongint();
            }
            else if (typeName2.equals("TINYINT") || typeName2.equals("SMALLINT")) {
                null = new SQLInteger();
            }
            else if (typeName2.equals("REAL")) {
                null = new SQLDouble();
            }
            else {
                null = TypeId.getBuiltInTypeId(3).getNull();
            }
            null.setValue(this.value);
            this.value = null;
            this.accumulate(dataValueDescriptor);
        }
    }
    
    public void merge(final ExecAggregator execAggregator) throws StandardException {
        final AvgAggregator avgAggregator = (AvgAggregator)execAggregator;
        if (this.count == 0L) {
            this.count = avgAggregator.count;
            this.value = avgAggregator.value;
            this.scale = avgAggregator.scale;
            return;
        }
        if (avgAggregator.value != null) {
            this.count += avgAggregator.count - 1L;
            this.accumulate(avgAggregator.value);
        }
    }
    
    public DataValueDescriptor getResult() throws StandardException {
        if (this.count == 0L) {
            return null;
        }
        final NumberDataValue numberDataValue = (NumberDataValue)this.value;
        final NumberDataValue numberDataValue2 = (NumberDataValue)this.value.getNewNull();
        if (this.count > 2147483647L) {
            final String typeName = numberDataValue.getTypeName();
            if (typeName.equals("INTEGER") || typeName.equals("TINYINT") || typeName.equals("SMALLINT")) {
                numberDataValue2.setValue(0);
                return numberDataValue2;
            }
        }
        numberDataValue.divide(numberDataValue, new SQLLongint(this.count), numberDataValue2, this.scale);
        return numberDataValue2;
    }
    
    public ExecAggregator newAggregator() {
        return new AvgAggregator();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeLong(this.count);
        objectOutput.writeInt(this.scale);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.count = objectInput.readLong();
        this.scale = objectInput.readInt();
    }
    
    public int getTypeFormatId() {
        return 149;
    }
}
