// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.compile.TypeCompiler;

abstract class BaseTypeCompiler implements TypeCompiler
{
    private TypeId correspondingTypeId;
    
    public String getPrimitiveMethodName() {
        return null;
    }
    
    public DataTypeDescriptor resolveArithmeticOperation(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2, final String s) throws StandardException {
        throw StandardException.newException("42Y95", s, dataTypeDescriptor.getTypeId().getSQLTypeName(), dataTypeDescriptor2.getTypeId().getSQLTypeName());
    }
    
    public void generateNull(final MethodBuilder methodBuilder, final int n) {
        int n2;
        if (this.pushCollationForDataValue(n)) {
            methodBuilder.push(n);
            n2 = 2;
        }
        else {
            n2 = 1;
        }
        methodBuilder.callMethod((short)185, null, this.nullMethodName(), this.interfaceName(), n2);
    }
    
    public void generateDataValue(final MethodBuilder methodBuilder, final int n, final LocalField localField) {
        final String interfaceName = this.interfaceName();
        if (localField == null) {
            methodBuilder.pushNull(interfaceName);
        }
        else {
            methodBuilder.getField(localField);
        }
        int n2;
        if (this.pushCollationForDataValue(n)) {
            methodBuilder.push(n);
            n2 = 3;
        }
        else {
            n2 = 2;
        }
        methodBuilder.callMethod((short)185, null, this.dataValueMethodName(), interfaceName, n2);
        if (localField != null) {
            methodBuilder.putField(localField);
        }
    }
    
    abstract String nullMethodName();
    
    String dataValueMethodName() {
        return "getDataValue";
    }
    
    boolean pushCollationForDataValue(final int n) {
        return false;
    }
    
    protected boolean userTypeStorable(final TypeId typeId, final TypeId typeId2, final ClassFactory classFactory) {
        return typeId2.userType() && classFactory.getClassInspector().assignableTo(typeId.getCorrespondingJavaTypeName(), typeId2.getCorrespondingJavaTypeName());
    }
    
    public boolean numberConvertible(final TypeId typeId, final boolean b) {
        if (typeId.getBaseTypeId().isAnsiUDT()) {
            return false;
        }
        if (typeId.isLongConcatableTypeId()) {
            return false;
        }
        boolean b2 = typeId.isNumericTypeId() || typeId.userType();
        if (b) {
            b2 = (b2 || (typeId.isFixedStringTypeId() && this.getTypeId().isFloatingPointTypeId()));
        }
        return b2 || (typeId.isFixedStringTypeId() && !this.getTypeId().isFloatingPointTypeId());
    }
    
    public boolean numberStorable(final TypeId typeId, final TypeId typeId2, final ClassFactory classFactory) {
        return !typeId2.getBaseTypeId().isAnsiUDT() && (typeId2.isNumericTypeId() || this.userTypeStorable(typeId, typeId2, classFactory));
    }
    
    protected TypeId getTypeId() {
        return this.correspondingTypeId;
    }
    
    protected TypeCompiler getTypeCompiler(final TypeId typeId) {
        return TypeCompilerFactoryImpl.staticGetTypeCompiler(typeId);
    }
    
    void setTypeId(final TypeId correspondingTypeId) {
        this.correspondingTypeId = correspondingTypeId;
    }
    
    protected int getStoredFormatIdFromTypeId() {
        return this.getTypeId().getTypeFormatId();
    }
}
