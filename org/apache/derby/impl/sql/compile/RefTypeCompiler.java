// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class RefTypeCompiler extends BaseTypeCompiler
{
    public String getCorrespondingPrimitiveTypeName() {
        return null;
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return 0;
    }
    
    public boolean convertible(final TypeId typeId, final boolean b) {
        return false;
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return typeId.isRefTypeId();
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.RefDataValue";
    }
    
    String nullMethodName() {
        return "getNullRef";
    }
}
