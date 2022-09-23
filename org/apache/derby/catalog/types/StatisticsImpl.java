// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.Statistics;

public class StatisticsImpl implements Statistics, Formatable
{
    private long numRows;
    private long numUnique;
    
    public StatisticsImpl(final long numRows, final long numUnique) {
        this.numRows = numRows;
        this.numUnique = numUnique;
    }
    
    public StatisticsImpl() {
    }
    
    public long getRowEstimate() {
        return this.numRows;
    }
    
    public double selectivity(final Object[] array) {
        if (this.numRows == 0.0) {
            return 0.1;
        }
        return 1.0 / this.numUnique;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final FormatableHashtable formatableHashtable = (FormatableHashtable)objectInput.readObject();
        this.numRows = formatableHashtable.getLong("numRows");
        this.numUnique = formatableHashtable.getLong("numUnique");
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final FormatableHashtable formatableHashtable = new FormatableHashtable();
        formatableHashtable.putLong("numRows", this.numRows);
        formatableHashtable.putLong("numUnique", this.numUnique);
        objectOutput.writeObject(formatableHashtable);
    }
    
    public int getTypeFormatId() {
        return 397;
    }
    
    public String toString() {
        return "numunique= " + this.numUnique + " numrows= " + this.numRows;
    }
}
