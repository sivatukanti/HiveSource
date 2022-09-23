// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.ReferencedColumns;

public class ReferencedColumnsDescriptorImpl implements ReferencedColumns, Formatable
{
    private int[] referencedColumns;
    private int[] referencedColumnsInTriggerAction;
    
    public ReferencedColumnsDescriptorImpl(final int[] referencedColumns) {
        this.referencedColumns = referencedColumns;
    }
    
    public ReferencedColumnsDescriptorImpl(final int[] referencedColumns, final int[] referencedColumnsInTriggerAction) {
        this.referencedColumns = referencedColumns;
        this.referencedColumnsInTriggerAction = referencedColumnsInTriggerAction;
    }
    
    public ReferencedColumnsDescriptorImpl() {
    }
    
    public int[] getReferencedColumnPositions() {
        return this.referencedColumns;
    }
    
    public int[] getTriggerActionReferencedColumnPositions() {
        return this.referencedColumnsInTriggerAction;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        final int int1 = objectInput.readInt();
        int int2;
        if (int1 < 0) {
            int2 = objectInput.readInt();
            if (int2 < 0) {
                int2 = 0;
            }
            else {
                this.referencedColumns = new int[int2];
            }
        }
        else {
            int2 = int1;
            this.referencedColumns = new int[int2];
        }
        for (int i = 0; i < int2; ++i) {
            this.referencedColumns[i] = objectInput.readInt();
        }
        if (int1 < 0) {
            final int int3 = objectInput.readInt();
            this.referencedColumnsInTriggerAction = new int[int3];
            for (int j = 0; j < int3; ++j) {
                this.referencedColumnsInTriggerAction[j] = objectInput.readInt();
            }
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final int n = (this.referencedColumnsInTriggerAction == null) ? this.referencedColumns.length : -1;
        if (n < 0) {
            objectOutput.writeInt(n);
            if (this.referencedColumns != null) {
                this.writeReferencedColumns(objectOutput);
            }
            else {
                objectOutput.writeInt(n);
            }
            objectOutput.writeInt(this.referencedColumnsInTriggerAction.length);
            for (int i = 0; i < this.referencedColumnsInTriggerAction.length; ++i) {
                objectOutput.writeInt(this.referencedColumnsInTriggerAction[i]);
            }
        }
        else {
            this.writeReferencedColumns(objectOutput);
        }
    }
    
    private void writeReferencedColumns(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.referencedColumns.length);
        for (int i = 0; i < this.referencedColumns.length; ++i) {
            objectOutput.writeInt(this.referencedColumns[i]);
        }
    }
    
    public int getTypeFormatId() {
        return 205;
    }
    
    public String toString() {
        if (this.referencedColumns == null) {
            return "NULL";
        }
        final StringBuffer sb = new StringBuffer(60);
        sb.append('(');
        for (int i = 0; i < this.referencedColumns.length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(String.valueOf(this.referencedColumns[i]));
        }
        sb.append(')');
        return sb.toString();
    }
}
