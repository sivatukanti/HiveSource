// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.DefaultInfo;

public final class ColumnDescriptor extends TupleDescriptor
{
    private DefaultInfo columnDefaultInfo;
    private TableDescriptor table;
    private String columnName;
    private int columnPosition;
    private DataTypeDescriptor columnType;
    private DataValueDescriptor columnDefault;
    private UUID uuid;
    private UUID defaultUUID;
    private long autoincStart;
    private long autoincInc;
    private long autoincValue;
    long autoinc_create_or_modify_Start_Increment;
    
    public ColumnDescriptor(final String s, final int n, final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor, final DefaultInfo defaultInfo, final TableDescriptor tableDescriptor, final UUID uuid, final long n2, final long n3, final long autoinc_create_or_modify_Start_Increment) {
        this(s, n, dataTypeDescriptor, dataValueDescriptor, defaultInfo, tableDescriptor, uuid, n2, n3);
        this.autoinc_create_or_modify_Start_Increment = autoinc_create_or_modify_Start_Increment;
    }
    
    public ColumnDescriptor(final String columnName, final int columnPosition, final DataTypeDescriptor columnType, final DataValueDescriptor columnDefault, final DefaultInfo columnDefaultInfo, final TableDescriptor table, final UUID defaultUUID, final long n, final long autoincInc) {
        this.autoinc_create_or_modify_Start_Increment = -1L;
        this.columnName = columnName;
        this.columnPosition = columnPosition;
        this.columnType = columnType;
        this.columnDefault = columnDefault;
        this.columnDefaultInfo = columnDefaultInfo;
        this.defaultUUID = defaultUUID;
        if (table != null) {
            this.table = table;
            this.uuid = table.getUUID();
        }
        assertAutoinc(autoincInc != 0L, autoincInc, columnDefaultInfo);
        this.autoincStart = n;
        this.autoincValue = n;
        this.autoincInc = autoincInc;
    }
    
    public ColumnDescriptor(final String columnName, final int columnPosition, final DataTypeDescriptor columnType, final DataValueDescriptor columnDefault, final DefaultInfo columnDefaultInfo, final UUID uuid, final UUID defaultUUID, final long autoincStart, final long autoincInc, final long autoincValue) {
        this.autoinc_create_or_modify_Start_Increment = -1L;
        this.columnName = columnName;
        this.columnPosition = columnPosition;
        this.columnType = columnType;
        this.columnDefault = columnDefault;
        this.columnDefaultInfo = columnDefaultInfo;
        this.uuid = uuid;
        this.defaultUUID = defaultUUID;
        assertAutoinc(autoincInc != 0L, autoincInc, columnDefaultInfo);
        this.autoincStart = autoincStart;
        this.autoincValue = autoincValue;
        this.autoincInc = autoincInc;
    }
    
    public UUID getReferencingUUID() {
        return this.uuid;
    }
    
    public TableDescriptor getTableDescriptor() {
        return this.table;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void setTableDescriptor(final TableDescriptor table) {
        this.table = table;
    }
    
    public int getPosition() {
        return this.columnPosition;
    }
    
    public DataTypeDescriptor getType() {
        return this.columnType;
    }
    
    public boolean hasNonNullDefault() {
        return (this.columnDefault != null && !this.columnDefault.isNull()) || this.columnDefaultInfo != null;
    }
    
    public DataValueDescriptor getDefaultValue() {
        return this.columnDefault;
    }
    
    public DefaultInfo getDefaultInfo() {
        return this.columnDefaultInfo;
    }
    
    public UUID getDefaultUUID() {
        return this.defaultUUID;
    }
    
    public DefaultDescriptor getDefaultDescriptor(final DataDictionary dataDictionary) {
        DefaultDescriptor defaultDescriptor = null;
        if (this.defaultUUID != null) {
            defaultDescriptor = new DefaultDescriptor(dataDictionary, this.defaultUUID, this.uuid, this.columnPosition);
        }
        return defaultDescriptor;
    }
    
    public boolean isAutoincrement() {
        return this.autoincInc != 0L;
    }
    
    public boolean updatableByCursor() {
        return false;
    }
    
    public boolean hasGenerationClause() {
        return this.columnDefaultInfo != null && this.columnDefaultInfo.isGeneratedColumn();
    }
    
    public boolean isAutoincAlways() {
        return this.columnDefaultInfo == null && this.isAutoincrement();
    }
    
    public long getAutoincStart() {
        return this.autoincStart;
    }
    
    public long getAutoincInc() {
        return this.autoincInc;
    }
    
    public long getAutoincValue() {
        return this.autoincValue;
    }
    
    public long getAutoinc_create_or_modify_Start_Increment() {
        return this.autoinc_create_or_modify_Start_Increment;
    }
    
    public void setAutoinc_create_or_modify_Start_Increment(final int n) {
        this.autoinc_create_or_modify_Start_Increment = n;
    }
    
    public void setPosition(final int columnPosition) {
        this.columnPosition = columnPosition;
    }
    
    public String toString() {
        return "";
    }
    
    public String getDescriptorName() {
        return this.columnName;
    }
    
    public String getDescriptorType() {
        return "Column";
    }
    
    private static void assertAutoinc(final boolean b, final long n, final DefaultInfo defaultInfo) {
    }
}
