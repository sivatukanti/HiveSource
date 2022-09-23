// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectOutput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.catalog.UUID;
import java.util.Properties;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.types.RowLocation;

public class InsertConstantAction extends WriteCursorConstantAction
{
    boolean[] indexedCols;
    private String schemaName;
    private String tableName;
    private String[] columnNames;
    protected RowLocation[] autoincRowLocation;
    private long[] autoincIncrement;
    
    public InsertConstantAction() {
    }
    
    public InsertConstantAction(final TableDescriptor tableDescriptor, final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final String[] indexNames, final boolean b, final Properties properties, final UUID uuid, final int n2, final FKInfo[] array4, final TriggerInfo triggerInfo, final int[] array5, final boolean[] indexedCols, final boolean b2, final RowLocation[] autoincRowLocation) {
        super(n, staticCompiledOpenConglomInfo, array, array2, array3, indexNames, b, properties, uuid, n2, array4, triggerInfo, null, null, array5, b2);
        this.indexedCols = indexedCols;
        this.autoincRowLocation = autoincRowLocation;
        this.schemaName = tableDescriptor.getSchemaName();
        this.tableName = tableDescriptor.getName();
        this.columnNames = tableDescriptor.getColumnNamesArray();
        this.autoincIncrement = tableDescriptor.getAutoincIncrementArray();
        this.indexNames = indexNames;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.indexedCols = ArrayUtil.readBooleanArray(objectInput);
        final Object[] objectArray = ArrayUtil.readObjectArray(objectInput);
        if (objectArray != null) {
            this.autoincRowLocation = new RowLocation[objectArray.length];
            for (int i = 0; i < objectArray.length; ++i) {
                this.autoincRowLocation[i] = (RowLocation)objectArray[i];
            }
        }
        this.schemaName = (String)objectInput.readObject();
        this.tableName = (String)objectInput.readObject();
        final Object[] objectArray2 = ArrayUtil.readObjectArray(objectInput);
        if (objectArray2 != null) {
            this.columnNames = new String[objectArray2.length];
            for (int j = 0; j < objectArray2.length; ++j) {
                this.columnNames[j] = (String)objectArray2[j];
            }
        }
        this.autoincIncrement = ArrayUtil.readLongArray(objectInput);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        ArrayUtil.writeBooleanArray(objectOutput, this.indexedCols);
        ArrayUtil.writeArray(objectOutput, this.autoincRowLocation);
        objectOutput.writeObject(this.schemaName);
        objectOutput.writeObject(this.tableName);
        ArrayUtil.writeArray(objectOutput, this.columnNames);
        ArrayUtil.writeLongArray(objectOutput, this.autoincIncrement);
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getColumnName(final int n) {
        return this.columnNames[n];
    }
    
    public long getAutoincIncrement(final int n) {
        return this.autoincIncrement[n];
    }
    
    public boolean hasAutoincrement() {
        return this.autoincRowLocation != null;
    }
    
    public RowLocation[] getAutoincRowLocation() {
        return this.autoincRowLocation;
    }
    
    public int getTypeFormatId() {
        return 38;
    }
}
