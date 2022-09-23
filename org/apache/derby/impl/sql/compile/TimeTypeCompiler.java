// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class TimeTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return (typeId.isStringTypeId() && !typeId.isLOBTypeId() && !typeId.isLongVarcharTypeId()) || typeId.isTimestampId() || this.getStoredFormatIdFromTypeId() == typeId.getTypeFormatId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        final int jdbcTypeId = typeId.getJDBCTypeId();
        return jdbcTypeId == 92 || jdbcTypeId == 1 || jdbcTypeId == 12 || classFactory.getClassInspector().assignableTo(typeId.getCorrespondingJavaTypeName(), "java.sql.Time");
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.DateTimeDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return "java.sql.Time";
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return 8;
    }
    
    public double estimatedMemoryUsage(final DataTypeDescriptor dataTypeDescriptor) {
        return 12.0;
    }
    
    String nullMethodName() {
        return "getNullTime";
    }
}
