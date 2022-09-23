// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.IdUtil;

public class UserDefinedTypeIdImpl extends BaseTypeIdImpl
{
    protected String className;
    
    public UserDefinedTypeIdImpl() {
    }
    
    public UserDefinedTypeIdImpl(final String s) throws StandardException {
        if (s.charAt(0) == '\"') {
            final String[] multiPartSQLIdentifier = IdUtil.parseMultiPartSQLIdentifier(s);
            this.schemaName = multiPartSQLIdentifier[0];
            this.unqualifiedName = multiPartSQLIdentifier[1];
        }
        else {
            this.schemaName = null;
            this.unqualifiedName = s;
            this.className = s;
        }
        this.JDBCTypeId = 2000;
    }
    
    public UserDefinedTypeIdImpl(final String s, final String s2, final String className) {
        super(s, s2);
        this.className = className;
        this.JDBCTypeId = 2000;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public boolean userType() {
        return true;
    }
    
    public boolean isBound() {
        return this.className != null;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.className = objectInput.readUTF();
        this.JDBCTypeId = 2000;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        if (this.className == null) {
            throw new IOException("Internal error: class name for user defined type has not been determined yet.");
        }
        objectOutput.writeUTF(this.className);
    }
    
    public int getTypeFormatId() {
        return 264;
    }
}
