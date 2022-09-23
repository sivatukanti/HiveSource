// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectOutput;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.execute.ExecCursorTableReference;
import org.apache.derby.iapi.services.io.Formatable;

public class CursorInfo implements Formatable
{
    ExecCursorTableReference targetTable;
    ResultColumnDescriptor[] targetColumns;
    String[] updateColumns;
    int updateMode;
    
    public CursorInfo() {
    }
    
    public CursorInfo(final int updateMode, final ExecCursorTableReference targetTable, final ResultColumnDescriptor[] targetColumns, final String[] updateColumns) {
        this.updateMode = updateMode;
        this.targetTable = targetTable;
        this.targetColumns = targetColumns;
        this.updateColumns = updateColumns;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.updateMode);
        objectOutput.writeObject(this.targetTable);
        ArrayUtil.writeArray(objectOutput, this.targetColumns);
        ArrayUtil.writeArray(objectOutput, this.updateColumns);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.updateMode = objectInput.readInt();
        this.targetTable = (ExecCursorTableReference)objectInput.readObject();
        final int arrayLength = ArrayUtil.readArrayLength(objectInput);
        if (arrayLength != 0) {
            ArrayUtil.readArrayItems(objectInput, this.targetColumns = new ResultColumnDescriptor[arrayLength]);
        }
        final int arrayLength2 = ArrayUtil.readArrayLength(objectInput);
        if (arrayLength2 != 0) {
            ArrayUtil.readArrayItems(objectInput, this.updateColumns = new String[arrayLength2]);
        }
    }
    
    public int getTypeFormatId() {
        return 297;
    }
    
    public String toString() {
        return "";
    }
}
