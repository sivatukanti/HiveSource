// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.AliasInfo;

public class AggregateAliasInfo implements AliasInfo, Formatable
{
    private static final int FIRST_VERSION = 0;
    private TypeDescriptor _forType;
    private TypeDescriptor _returnType;
    
    public AggregateAliasInfo() {
    }
    
    public AggregateAliasInfo(final TypeDescriptor forType, final TypeDescriptor returnType) {
        this._forType = forType;
        this._returnType = returnType;
    }
    
    public boolean isTableFunction() {
        return false;
    }
    
    public TypeDescriptor getForType() {
        return this._forType;
    }
    
    public TypeDescriptor getReturnType() {
        return this._returnType;
    }
    
    public void setCollationTypeForAllStringTypes(final int n) {
        this._forType = DataTypeDescriptor.getCatalogType(this._forType, n);
        this._returnType = DataTypeDescriptor.getCatalogType(this._returnType, n);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        objectInput.readInt();
        this._forType = (TypeDescriptor)objectInput.readObject();
        this._returnType = (TypeDescriptor)objectInput.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(0);
        objectOutput.writeObject(this._forType);
        objectOutput.writeObject(this._returnType);
    }
    
    public int getTypeFormatId() {
        return 475;
    }
    
    public String toString() {
        return "FOR " + this._forType.getSQLstring() + " RETURNS " + this._returnType.getSQLstring();
    }
    
    public String getMethodName() {
        return null;
    }
}
