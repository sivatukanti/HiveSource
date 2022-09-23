// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class TimestampTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        if (typeId.isStringTypeId() && !typeId.isLongConcatableTypeId()) {
            return true;
        }
        final int jdbcTypeId = typeId.getJDBCTypeId();
        return jdbcTypeId == 93 || jdbcTypeId == 91 || jdbcTypeId == 92;
    }
    
    public boolean compatible(final TypeId typeId) {
        return (typeId.isStringTypeId() && !typeId.isLongConcatableTypeId()) || this.getStoredFormatIdFromTypeId() == typeId.getTypeFormatId();
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        final int jdbcTypeId = typeId.getJDBCTypeId();
        return jdbcTypeId == 93 || jdbcTypeId == 1 || jdbcTypeId == 12 || classFactory.getClassInspector().assignableTo(typeId.getCorrespondingJavaTypeName(), "java.sql.Timestamp");
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.DateTimeDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return "java.sql.Timestamp";
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return 26;
    }
    
    public double estimatedMemoryUsage(final DataTypeDescriptor dataTypeDescriptor) {
        return 12.0;
    }
    
    String nullMethodName() {
        return "getNullTimestamp";
    }
}
