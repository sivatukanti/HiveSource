// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import org.apache.derby.iapi.util.IdUtil;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.AliasInfo;

public class SynonymAliasInfo implements AliasInfo, Formatable
{
    private String schemaName;
    private String tableName;
    
    public SynonymAliasInfo() {
        this.schemaName = null;
        this.tableName = null;
    }
    
    public SynonymAliasInfo(final String schemaName, final String tableName) {
        this.schemaName = null;
        this.tableName = null;
        this.schemaName = schemaName;
        this.tableName = tableName;
    }
    
    public String getSynonymTable() {
        return this.tableName;
    }
    
    public String getSynonymSchema() {
        return this.schemaName;
    }
    
    public boolean isTableFunction() {
        return false;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.schemaName = (String)objectInput.readObject();
        this.tableName = (String)objectInput.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.schemaName);
        objectOutput.writeObject(this.tableName);
    }
    
    public int getTypeFormatId() {
        return 455;
    }
    
    public String toString() {
        return IdUtil.mkQualifiedName(this.schemaName, this.tableName);
    }
    
    public String getMethodName() {
        return null;
    }
}
