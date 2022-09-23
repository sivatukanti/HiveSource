// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectInput;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.execute.ConstantAction;

public class DeleteConstantAction extends WriteCursorConstantAction
{
    int numColumns;
    ConstantAction[] dependentCActions;
    ResultDescription resultDescription;
    
    public DeleteConstantAction() {
    }
    
    public DeleteConstantAction(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final boolean b, final UUID uuid, final int n2, final FKInfo[] array4, final TriggerInfo triggerInfo, final FormatableBitSet set, final int[] array5, final int[] array6, final int numColumns, final boolean b2, final ResultDescription resultDescription, final ConstantAction[] dependentCActions) {
        super(n, staticCompiledOpenConglomInfo, array, array2, array3, null, b, null, uuid, n2, array4, triggerInfo, set, array5, array6, b2);
        this.numColumns = numColumns;
        this.resultDescription = resultDescription;
        this.dependentCActions = dependentCActions;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.numColumns = objectInput.readInt();
        ArrayUtil.readArrayItems(objectInput, this.dependentCActions = new ConstantAction[ArrayUtil.readArrayLength(objectInput)]);
        this.resultDescription = (ResultDescription)objectInput.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeInt(this.numColumns);
        ArrayUtil.writeArray(objectOutput, this.dependentCActions);
        objectOutput.writeObject(this.resultDescription);
    }
    
    public int getTypeFormatId() {
        return 37;
    }
}
