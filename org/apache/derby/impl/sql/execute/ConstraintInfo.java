// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayUtil;
import java.io.ObjectOutput;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConsInfo;

public class ConstraintInfo implements ConsInfo
{
    private String tableName;
    private SchemaDescriptor tableSd;
    private UUID tableSchemaId;
    private String[] columnNames;
    private int raDeleteRule;
    private int raUpdateRule;
    
    public ConstraintInfo() {
    }
    
    public ConstraintInfo(final String tableName, final SchemaDescriptor tableSd, final String[] columnNames, final int raDeleteRule, final int raUpdateRule) {
        this.tableName = tableName;
        this.tableSd = tableSd;
        this.columnNames = columnNames;
        this.raDeleteRule = raDeleteRule;
        this.raUpdateRule = raUpdateRule;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.tableName);
        if (this.tableSd == null) {
            objectOutput.writeBoolean(false);
        }
        else {
            objectOutput.writeBoolean(true);
            objectOutput.writeObject(this.tableSd.getUUID());
        }
        if (this.columnNames == null) {
            objectOutput.writeBoolean(false);
        }
        else {
            objectOutput.writeBoolean(true);
            ArrayUtil.writeArrayLength(objectOutput, this.columnNames);
            ArrayUtil.writeArrayItems(objectOutput, this.columnNames);
        }
        objectOutput.writeInt(this.raDeleteRule);
        objectOutput.writeInt(this.raUpdateRule);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.tableName = (String)objectInput.readObject();
        if (objectInput.readBoolean()) {
            this.tableSchemaId = (UUID)objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            ArrayUtil.readArrayItems(objectInput, this.columnNames = new String[ArrayUtil.readArrayLength(objectInput)]);
        }
        this.raDeleteRule = objectInput.readInt();
        this.raUpdateRule = objectInput.readInt();
    }
    
    public int getTypeFormatId() {
        return 278;
    }
    
    public String toString() {
        return "";
    }
    
    public SchemaDescriptor getReferencedTableSchemaDescriptor(final DataDictionary dataDictionary) throws StandardException {
        if (this.tableSd != null) {
            return this.tableSd;
        }
        return dataDictionary.getSchemaDescriptor(this.tableSchemaId, null);
    }
    
    public TableDescriptor getReferencedTableDescriptor(final DataDictionary dataDictionary) throws StandardException {
        if (this.tableName == null) {
            return null;
        }
        return dataDictionary.getTableDescriptor(this.tableName, this.getReferencedTableSchemaDescriptor(dataDictionary), null);
    }
    
    public String[] getReferencedColumnNames() {
        return this.columnNames;
    }
    
    public String getReferencedTableName() {
        return this.tableName;
    }
    
    public int getReferentialActionUpdateRule() {
        return this.raUpdateRule;
    }
    
    public int getReferentialActionDeleteRule() {
        return this.raDeleteRule;
    }
}
