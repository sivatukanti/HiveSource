// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.sql.execute.ExecCursorTableReference;

public class CursorTableReference implements ExecCursorTableReference, Formatable
{
    private String exposedName;
    private String baseName;
    private String schemaName;
    
    public CursorTableReference() {
    }
    
    public CursorTableReference(final String exposedName, final String baseName, final String schemaName) {
        this.exposedName = exposedName;
        this.baseName = baseName;
        this.schemaName = schemaName;
    }
    
    public String getBaseName() {
        return this.baseName;
    }
    
    public String getExposedName() {
        return this.exposedName;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.baseName);
        objectOutput.writeObject(this.exposedName);
        objectOutput.writeObject(this.schemaName);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.baseName = (String)objectInput.readObject();
        this.exposedName = (String)objectInput.readObject();
        this.schemaName = (String)objectInput.readObject();
    }
    
    public int getTypeFormatId() {
        return 296;
    }
    
    public String toString() {
        return "";
    }
}
