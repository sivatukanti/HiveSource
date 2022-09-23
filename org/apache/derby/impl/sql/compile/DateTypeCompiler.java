// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class DateTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return (typeId.isStringTypeId() && !typeId.isLongConcatableTypeId()) || typeId.isTimestampId() || this.getStoredFormatIdFromTypeId() == typeId.getTypeFormatId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        final int jdbcTypeId = typeId.getJDBCTypeId();
        return jdbcTypeId == 91 || jdbcTypeId == 1 || jdbcTypeId == 12 || classFactory.getClassInspector().assignableTo(typeId.getCorrespondingJavaTypeName(), "java.sql.Date");
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.DateTimeDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return "java.sql.Date";
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return 10;
    }
    
    String nullMethodName() {
        return "getNullDate";
    }
}
