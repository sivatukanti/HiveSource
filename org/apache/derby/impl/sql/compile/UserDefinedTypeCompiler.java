// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.catalog.types.UserDefinedTypeIdImpl;
import org.apache.derby.iapi.types.TypeId;

public class UserDefinedTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return !this.getTypeId().getBaseTypeId().isAnsiUDT() || (typeId.getBaseTypeId().isAnsiUDT() && ((UserDefinedTypeIdImpl)this.getTypeId().getBaseTypeId()).getSQLTypeName().equals(((UserDefinedTypeIdImpl)typeId.getBaseTypeId()).getSQLTypeName()));
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        if (!typeId.isUserDefinedTypeId()) {
            return false;
        }
        final UserDefinedTypeIdImpl userDefinedTypeIdImpl = (UserDefinedTypeIdImpl)this.getTypeId().getBaseTypeId();
        final UserDefinedTypeIdImpl userDefinedTypeIdImpl2 = (UserDefinedTypeIdImpl)typeId.getBaseTypeId();
        if (userDefinedTypeIdImpl.isAnsiUDT() != userDefinedTypeIdImpl2.isAnsiUDT()) {
            return false;
        }
        if (userDefinedTypeIdImpl.isAnsiUDT()) {
            return userDefinedTypeIdImpl.getSQLTypeName().equals(userDefinedTypeIdImpl2.getSQLTypeName());
        }
        return classFactory.getClassInspector().assignableTo(typeId.getCorrespondingJavaTypeName(), this.getTypeId().getCorrespondingJavaTypeName());
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.UserDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return this.getTypeId().getCorrespondingJavaTypeName();
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return -1;
    }
    
    String nullMethodName() {
        return "getNullObject";
    }
    
    public void generateDataValue(final MethodBuilder methodBuilder, final int n, final LocalField localField) {
        methodBuilder.upCast("java.lang.Object");
        super.generateDataValue(methodBuilder, n, localField);
    }
}
