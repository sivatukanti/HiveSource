// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.AliasInfo;

public class MethodAliasInfo implements AliasInfo, Formatable
{
    private String methodName;
    
    public MethodAliasInfo() {
    }
    
    public MethodAliasInfo(final String methodName) {
        this.methodName = methodName;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.methodName = (String)objectInput.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.methodName);
    }
    
    public int getTypeFormatId() {
        return 312;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public boolean isTableFunction() {
        return false;
    }
    
    public String toString() {
        return this.methodName;
    }
}
