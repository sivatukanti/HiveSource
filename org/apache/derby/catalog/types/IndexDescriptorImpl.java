// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.IndexDescriptor;

public class IndexDescriptorImpl implements IndexDescriptor, Formatable
{
    private boolean isUnique;
    private int[] baseColumnPositions;
    private boolean[] isAscending;
    private int numberOfOrderedColumns;
    private String indexType;
    private boolean isUniqueWithDuplicateNulls;
    
    public IndexDescriptorImpl(final String indexType, final boolean isUnique, final boolean isUniqueWithDuplicateNulls, final int[] baseColumnPositions, final boolean[] isAscending, final int numberOfOrderedColumns) {
        this.indexType = indexType;
        this.isUnique = isUnique;
        this.isUniqueWithDuplicateNulls = isUniqueWithDuplicateNulls;
        this.baseColumnPositions = baseColumnPositions;
        this.isAscending = isAscending;
        this.numberOfOrderedColumns = numberOfOrderedColumns;
    }
    
    public IndexDescriptorImpl() {
    }
    
    public boolean isUniqueWithDuplicateNulls() {
        return this.isUniqueWithDuplicateNulls;
    }
    
    public boolean isUnique() {
        return this.isUnique;
    }
    
    public int[] baseColumnPositions() {
        return this.baseColumnPositions;
    }
    
    public int getKeyColumnPosition(final int n) {
        int n2 = 0;
        for (int i = 0; i < this.baseColumnPositions.length; ++i) {
            if (this.baseColumnPositions[i] == n) {
                n2 = i + 1;
                break;
            }
        }
        return n2;
    }
    
    public int numberOfOrderedColumns() {
        return this.numberOfOrderedColumns;
    }
    
    public String indexType() {
        return this.indexType;
    }
    
    public boolean isAscending(final Integer n) {
        final int n2 = n - 1;
        return n2 >= 0 && n2 < this.baseColumnPositions.length && this.isAscending[n2];
    }
    
    public boolean isDescending(final Integer n) {
        final int n2 = n - 1;
        return n2 >= 0 && n2 < this.baseColumnPositions.length && !this.isAscending[n2];
    }
    
    public boolean[] isAscending() {
        return this.isAscending;
    }
    
    public void setBaseColumnPositions(final int[] baseColumnPositions) {
        this.baseColumnPositions = baseColumnPositions;
    }
    
    public void setIsAscending(final boolean[] isAscending) {
        this.isAscending = isAscending;
    }
    
    public void setNumberOfOrderedColumns(final int numberOfOrderedColumns) {
        this.numberOfOrderedColumns = numberOfOrderedColumns;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer(60);
        if (this.isUnique) {
            sb.append("UNIQUE ");
        }
        else if (this.isUniqueWithDuplicateNulls) {
            sb.append("UNIQUE WITH DUPLICATE NULLS");
        }
        sb.append(this.indexType);
        sb.append(" (");
        for (int i = 0; i < this.baseColumnPositions.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.baseColumnPositions[i]);
            if (!this.isAscending[i]) {
                sb.append(" DESC");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final FormatableHashtable formatableHashtable = (FormatableHashtable)objectInput.readObject();
        this.isUnique = formatableHashtable.getBoolean("isUnique");
        final int int1 = formatableHashtable.getInt("keyLength");
        this.baseColumnPositions = new int[int1];
        this.isAscending = new boolean[int1];
        for (int i = 0; i < int1; ++i) {
            this.baseColumnPositions[i] = formatableHashtable.getInt("bcp" + i);
            this.isAscending[i] = formatableHashtable.getBoolean("isAsc" + i);
        }
        this.numberOfOrderedColumns = formatableHashtable.getInt("orderedColumns");
        this.indexType = formatableHashtable.get("indexType");
        if (formatableHashtable.containsKey("isUniqueWithDuplicateNulls")) {
            this.isUniqueWithDuplicateNulls = formatableHashtable.getBoolean("isUniqueWithDuplicateNulls");
        }
        else {
            this.isUniqueWithDuplicateNulls = false;
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final FormatableHashtable formatableHashtable = new FormatableHashtable();
        formatableHashtable.putBoolean("isUnique", this.isUnique);
        formatableHashtable.putInt("keyLength", this.baseColumnPositions.length);
        for (int i = 0; i < this.baseColumnPositions.length; ++i) {
            formatableHashtable.putInt("bcp" + i, this.baseColumnPositions[i]);
            formatableHashtable.putBoolean("isAsc" + i, this.isAscending[i]);
        }
        formatableHashtable.putInt("orderedColumns", this.numberOfOrderedColumns);
        formatableHashtable.put("indexType", this.indexType);
        formatableHashtable.putBoolean("isUniqueWithDuplicateNulls", this.isUniqueWithDuplicateNulls);
        objectOutput.writeObject(formatableHashtable);
    }
    
    public int getTypeFormatId() {
        return 387;
    }
    
    public boolean equals(final Object o) {
        boolean b = false;
        if (o instanceof IndexDescriptorImpl) {
            final IndexDescriptorImpl indexDescriptorImpl = (IndexDescriptorImpl)o;
            if (indexDescriptorImpl.isUnique == this.isUnique && indexDescriptorImpl.isUniqueWithDuplicateNulls == this.isUniqueWithDuplicateNulls && indexDescriptorImpl.baseColumnPositions.length == this.baseColumnPositions.length && indexDescriptorImpl.numberOfOrderedColumns == this.numberOfOrderedColumns && indexDescriptorImpl.indexType.equals(this.indexType)) {
                b = true;
                for (int i = 0; i < this.baseColumnPositions.length; ++i) {
                    if (indexDescriptorImpl.baseColumnPositions[i] != this.baseColumnPositions[i] || indexDescriptorImpl.isAscending[i] != this.isAscending[i]) {
                        b = false;
                        break;
                    }
                }
            }
        }
        return b;
    }
    
    public int hashCode() {
        int n = (this.isUnique ? 1 : 2) * this.numberOfOrderedColumns;
        for (int i = 0; i < this.baseColumnPositions.length; ++i) {
            n *= this.baseColumnPositions[i];
        }
        return n * this.indexType.hashCode();
    }
}
