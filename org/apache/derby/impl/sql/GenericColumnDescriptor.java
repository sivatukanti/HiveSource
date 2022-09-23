// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.catalog.types.RoutineAliasInfo;
import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.io.ObjectOutput;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;

public final class GenericColumnDescriptor implements ResultColumnDescriptor, Formatable
{
    private String name;
    private String schemaName;
    private String tableName;
    private int columnPos;
    private DataTypeDescriptor type;
    private boolean isAutoincrement;
    private boolean updatableByCursor;
    private boolean hasGenerationClause;
    
    public GenericColumnDescriptor() {
    }
    
    public GenericColumnDescriptor(final String name, final DataTypeDescriptor type) {
        this.name = name;
        this.type = type;
    }
    
    public GenericColumnDescriptor(final ResultColumnDescriptor resultColumnDescriptor) {
        this.name = resultColumnDescriptor.getName();
        this.tableName = resultColumnDescriptor.getSourceTableName();
        this.schemaName = resultColumnDescriptor.getSourceSchemaName();
        this.columnPos = resultColumnDescriptor.getColumnPosition();
        this.type = resultColumnDescriptor.getType();
        this.isAutoincrement = resultColumnDescriptor.isAutoincrement();
        this.updatableByCursor = resultColumnDescriptor.updatableByCursor();
        this.hasGenerationClause = resultColumnDescriptor.hasGenerationClause();
    }
    
    public DataTypeDescriptor getType() {
        return this.type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getSourceSchemaName() {
        return this.schemaName;
    }
    
    public String getSourceTableName() {
        return this.tableName;
    }
    
    public int getColumnPosition() {
        return this.columnPos;
    }
    
    public boolean isAutoincrement() {
        return this.isAutoincrement;
    }
    
    public boolean updatableByCursor() {
        return this.updatableByCursor;
    }
    
    public boolean hasGenerationClause() {
        return this.hasGenerationClause;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final FormatableHashtable formatableHashtable = new FormatableHashtable();
        formatableHashtable.put("name", this.name);
        formatableHashtable.put("tableName", this.tableName);
        formatableHashtable.put("schemaName", this.schemaName);
        formatableHashtable.putInt("columnPos", this.columnPos);
        formatableHashtable.put("type", this.type);
        formatableHashtable.putBoolean("isAutoincrement", this.isAutoincrement);
        formatableHashtable.putBoolean("updatableByCursor", this.updatableByCursor);
        objectOutput.writeObject(formatableHashtable);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final FormatableHashtable formatableHashtable = (FormatableHashtable)objectInput.readObject();
        this.name = formatableHashtable.get("name");
        this.tableName = formatableHashtable.get("tableName");
        this.schemaName = formatableHashtable.get("schemaName");
        this.columnPos = formatableHashtable.getInt("columnPos");
        this.type = this.getStoredDataTypeDescriptor(formatableHashtable.get("type"));
        this.isAutoincrement = formatableHashtable.getBoolean("isAutoincrement");
        this.updatableByCursor = formatableHashtable.getBoolean("updatableByCursor");
    }
    
    public int getTypeFormatId() {
        return 383;
    }
    
    public String toString() {
        return "";
    }
    
    private DataTypeDescriptor getStoredDataTypeDescriptor(final Object o) {
        if (o instanceof DataTypeDescriptor) {
            return (DataTypeDescriptor)o;
        }
        return DataTypeDescriptor.getType(RoutineAliasInfo.getStoredType(o));
    }
}
