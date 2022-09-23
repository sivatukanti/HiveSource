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

public class UpdateConstantAction extends WriteCursorConstantAction
{
    int[] changedColumnIds;
    private boolean positionedUpdate;
    int numColumns;
    
    public UpdateConstantAction() {
    }
    
    public UpdateConstantAction(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final String[] array4, final boolean b, final UUID uuid, final int n2, final int[] changedColumnIds, final FKInfo[] array5, final TriggerInfo triggerInfo, final FormatableBitSet set, final int[] array6, final int[] array7, final int numColumns, final boolean positionedUpdate, final boolean b2) {
        super(n, staticCompiledOpenConglomInfo, array, array2, array3, array4, b, null, uuid, n2, array5, triggerInfo, set, array6, array7, b2);
        this.changedColumnIds = changedColumnIds;
        this.positionedUpdate = positionedUpdate;
        this.numColumns = numColumns;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.changedColumnIds = ArrayUtil.readIntArray(objectInput);
        this.positionedUpdate = objectInput.readBoolean();
        this.numColumns = objectInput.readInt();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        ArrayUtil.writeIntArray(objectOutput, this.changedColumnIds);
        objectOutput.writeBoolean(this.positionedUpdate);
        objectOutput.writeInt(this.numColumns);
    }
    
    public int getTypeFormatId() {
        return 39;
    }
}
