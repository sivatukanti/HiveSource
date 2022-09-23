// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectInput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.UUID;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.sql.execute.ConstantAction;

abstract class WriteCursorConstantAction implements ConstantAction, Formatable
{
    long conglomId;
    StaticCompiledOpenConglomInfo heapSCOCI;
    IndexRowGenerator[] irgs;
    long[] indexCIDS;
    StaticCompiledOpenConglomInfo[] indexSCOCIs;
    String[] indexNames;
    boolean deferred;
    private Properties targetProperties;
    UUID targetUUID;
    int lockMode;
    private FKInfo[] fkInfo;
    private TriggerInfo triggerInfo;
    private FormatableBitSet baseRowReadList;
    private int[] baseRowReadMap;
    private int[] streamStorableHeapColIds;
    boolean singleRowSource;
    
    public WriteCursorConstantAction() {
    }
    
    public WriteCursorConstantAction(final long conglomId, final StaticCompiledOpenConglomInfo heapSCOCI, final IndexRowGenerator[] irgs, final long[] indexCIDS, final StaticCompiledOpenConglomInfo[] array, final String[] indexNames, final boolean deferred, final Properties targetProperties, final UUID targetUUID, final int lockMode, final FKInfo[] fkInfo, final TriggerInfo triggerInfo, final FormatableBitSet baseRowReadList, final int[] baseRowReadMap, final int[] streamStorableHeapColIds, final boolean singleRowSource) {
        this.conglomId = conglomId;
        this.heapSCOCI = heapSCOCI;
        this.irgs = irgs;
        this.indexSCOCIs = array;
        this.indexCIDS = indexCIDS;
        this.indexSCOCIs = array;
        this.deferred = deferred;
        this.targetProperties = targetProperties;
        this.targetUUID = targetUUID;
        this.lockMode = lockMode;
        this.fkInfo = fkInfo;
        this.triggerInfo = triggerInfo;
        this.baseRowReadList = baseRowReadList;
        this.baseRowReadMap = baseRowReadMap;
        this.streamStorableHeapColIds = streamStorableHeapColIds;
        this.singleRowSource = singleRowSource;
        this.indexNames = indexNames;
    }
    
    final FKInfo[] getFKInfo() {
        return this.fkInfo;
    }
    
    TriggerInfo getTriggerInfo() {
        return this.triggerInfo;
    }
    
    public final void executeConstantAction(final Activation activation) throws StandardException {
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.conglomId = objectInput.readLong();
        this.heapSCOCI = (StaticCompiledOpenConglomInfo)objectInput.readObject();
        ArrayUtil.readArrayItems(objectInput, this.irgs = new IndexRowGenerator[ArrayUtil.readArrayLength(objectInput)]);
        this.indexCIDS = ArrayUtil.readLongArray(objectInput);
        ArrayUtil.readArrayItems(objectInput, this.indexSCOCIs = new StaticCompiledOpenConglomInfo[ArrayUtil.readArrayLength(objectInput)]);
        this.deferred = objectInput.readBoolean();
        this.targetProperties = (Properties)objectInput.readObject();
        this.targetUUID = (UUID)objectInput.readObject();
        this.lockMode = objectInput.readInt();
        ArrayUtil.readArrayItems(objectInput, this.fkInfo = new FKInfo[ArrayUtil.readArrayLength(objectInput)]);
        this.triggerInfo = (TriggerInfo)objectInput.readObject();
        this.baseRowReadList = (FormatableBitSet)objectInput.readObject();
        this.baseRowReadMap = ArrayUtil.readIntArray(objectInput);
        this.streamStorableHeapColIds = ArrayUtil.readIntArray(objectInput);
        this.singleRowSource = objectInput.readBoolean();
        this.indexNames = ArrayUtil.readStringArray(objectInput);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(this.conglomId);
        objectOutput.writeObject(this.heapSCOCI);
        ArrayUtil.writeArray(objectOutput, this.irgs);
        ArrayUtil.writeLongArray(objectOutput, this.indexCIDS);
        ArrayUtil.writeArray(objectOutput, this.indexSCOCIs);
        objectOutput.writeBoolean(this.deferred);
        objectOutput.writeObject(this.targetProperties);
        objectOutput.writeObject(this.targetUUID);
        objectOutput.writeInt(this.lockMode);
        ArrayUtil.writeArray(objectOutput, this.fkInfo);
        objectOutput.writeObject(this.triggerInfo);
        objectOutput.writeObject(this.baseRowReadList);
        ArrayUtil.writeIntArray(objectOutput, this.baseRowReadMap);
        ArrayUtil.writeIntArray(objectOutput, this.streamStorableHeapColIds);
        objectOutput.writeBoolean(this.singleRowSource);
        ArrayUtil.writeArray(objectOutput, this.indexNames);
    }
    
    public long getConglomerateId() {
        return this.conglomId;
    }
    
    public Properties getTargetProperties() {
        return this.targetProperties;
    }
    
    public String getProperty(final String key) {
        return (this.targetProperties == null) ? null : this.targetProperties.getProperty(key);
    }
    
    public FormatableBitSet getBaseRowReadList() {
        return this.baseRowReadList;
    }
    
    public int[] getBaseRowReadMap() {
        return this.baseRowReadMap;
    }
    
    public int[] getStreamStorableHeapColIds() {
        return this.streamStorableHeapColIds;
    }
    
    public String getIndexNameFromCID(final long n) {
        final int length = this.indexCIDS.length;
        if (this.indexNames == null) {
            return null;
        }
        for (int i = 0; i < length; ++i) {
            if (this.indexCIDS[i] == n) {
                return this.indexNames[i];
            }
        }
        return null;
    }
    
    public String[] getIndexNames() {
        return this.indexNames;
    }
}
