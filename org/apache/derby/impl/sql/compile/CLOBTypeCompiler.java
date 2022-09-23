// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class CLOBTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return typeId.isStringTypeId() || typeId.isBooleanTypeId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return typeId.isStringTypeId() || typeId.isBooleanTypeId();
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.StringDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 444: {
                return "java.sql.Clob";
            }
            default: {
                return null;
            }
        }
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return dataTypeDescriptor.getMaximumWidth();
    }
    
    String nullMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 444: {
                return "getNullClob";
            }
            default: {
                return null;
            }
        }
    }
    
    String dataValueMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 444: {
                return "getClobDataValue";
            }
            default: {
                return null;
            }
        }
    }
    
    boolean pushCollationForDataValue(final int n) {
        return n != 0;
    }
}
