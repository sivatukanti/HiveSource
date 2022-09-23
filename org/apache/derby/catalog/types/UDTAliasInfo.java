// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.AliasInfo;

public class UDTAliasInfo implements AliasInfo, Formatable
{
    private static final int FIRST_VERSION = 0;
    
    public boolean isTableFunction() {
        return false;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        objectInput.readInt();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(0);
    }
    
    public int getTypeFormatId() {
        return 474;
    }
    
    public String toString() {
        return "LANGUAGE JAVA";
    }
    
    public String getMethodName() {
        return null;
    }
}
