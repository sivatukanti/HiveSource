// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.store.access.ColumnOrdering;

public class IndexColumnOrder implements ColumnOrdering, Formatable
{
    int colNum;
    boolean ascending;
    boolean nullsOrderedLow;
    
    public IndexColumnOrder() {
    }
    
    public IndexColumnOrder(final int colNum) {
        this.colNum = colNum;
        this.ascending = true;
        this.nullsOrderedLow = false;
    }
    
    public IndexColumnOrder(final int colNum, final boolean ascending) {
        this.colNum = colNum;
        this.ascending = ascending;
        this.nullsOrderedLow = false;
    }
    
    public IndexColumnOrder(final int colNum, final boolean ascending, final boolean nullsOrderedLow) {
        this.colNum = colNum;
        this.ascending = ascending;
        this.nullsOrderedLow = nullsOrderedLow;
    }
    
    public int getColumnId() {
        return this.colNum;
    }
    
    public boolean getIsAscending() {
        return this.ascending;
    }
    
    public boolean getIsNullsOrderedLow() {
        return this.nullsOrderedLow;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.colNum);
        objectOutput.writeBoolean(this.ascending);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.colNum = objectInput.readInt();
        this.ascending = objectInput.readBoolean();
    }
    
    public int getTypeFormatId() {
        return 218;
    }
    
    public String toString() {
        return super.toString();
    }
}
