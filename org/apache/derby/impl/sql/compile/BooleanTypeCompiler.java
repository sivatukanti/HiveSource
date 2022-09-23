// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class BooleanTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return typeId.isStringTypeId() || typeId.isBooleanTypeId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return typeId.isBooleanTypeId() || typeId.isStringTypeId() || this.userTypeStorable(this.getTypeId(), typeId, classFactory);
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.BooleanDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return "boolean";
    }
    
    public String getPrimitiveMethodName() {
        return "getBoolean";
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return 5;
    }
    
    String nullMethodName() {
        return "getNullBoolean";
    }
}
