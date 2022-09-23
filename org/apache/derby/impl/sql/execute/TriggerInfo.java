// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectOutput;
import java.util.Iterator;
import org.apache.derby.iapi.sql.dictionary.GenericDescriptorList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.TriggerDescriptor;
import org.apache.derby.iapi.services.io.Formatable;

public final class TriggerInfo implements Formatable
{
    TriggerDescriptor[] triggerArray;
    String[] columnNames;
    int[] columnIds;
    
    public TriggerInfo() {
    }
    
    public TriggerInfo(final TableDescriptor tableDescriptor, final int[] columnIds, final GenericDescriptorList list) {
        this.columnIds = columnIds;
        if (this.columnIds != null) {
            this.columnNames = new String[this.columnIds.length];
            for (int i = 0; i < this.columnIds.length; ++i) {
                this.columnNames[i] = tableDescriptor.getColumnDescriptor(this.columnIds[i]).getColumnName();
            }
        }
        final Iterator<TriggerDescriptor> iterator = list.iterator();
        final int size = list.size();
        this.triggerArray = new TriggerDescriptor[size];
        for (int j = 0; j < size; ++j) {
            this.triggerArray[j] = iterator.next();
        }
    }
    
    private TriggerInfo(final TriggerDescriptor[] triggerArray, final int[] columnIds, final String[] columnNames) {
        this.columnIds = columnIds;
        this.columnNames = columnNames;
        this.triggerArray = triggerArray;
    }
    
    boolean hasTrigger(final boolean value, final boolean value2) {
        return this.triggerArray != null && this.hasTrigger(new Boolean(value), new Boolean(value2));
    }
    
    private boolean hasTrigger(final Boolean b, final Boolean b2) {
        if (this.triggerArray == null) {
            return false;
        }
        for (int i = 0; i < this.triggerArray.length; ++i) {
            if ((b == null || this.triggerArray[i].isBeforeTrigger() == b) && (b2 == null || this.triggerArray[i].isRowTrigger() == b2)) {
                return true;
            }
        }
        return false;
    }
    
    TriggerDescriptor[] getTriggerArray() {
        return this.triggerArray;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        ArrayUtil.writeArray(objectOutput, this.triggerArray);
        ArrayUtil.writeIntArray(objectOutput, this.columnIds);
        ArrayUtil.writeArray(objectOutput, this.columnNames);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        ArrayUtil.readArrayItems(objectInput, this.triggerArray = new TriggerDescriptor[ArrayUtil.readArrayLength(objectInput)]);
        this.columnIds = ArrayUtil.readIntArray(objectInput);
        final int arrayLength = ArrayUtil.readArrayLength(objectInput);
        if (arrayLength > 0) {
            ArrayUtil.readArrayItems(objectInput, this.columnNames = new String[arrayLength]);
        }
    }
    
    public int getTypeFormatId() {
        return 317;
    }
    
    public String toString() {
        return "";
    }
}
