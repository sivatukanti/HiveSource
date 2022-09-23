// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public final class CharTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        if (typeId.getBaseTypeId().isAnsiUDT()) {
            return false;
        }
        if (this.getTypeId().isLongVarcharTypeId()) {
            return typeId.isStringTypeId() || typeId.isBooleanTypeId();
        }
        if (b && typeId.isDoubleTypeId()) {
            return this.getTypeId().isStringTypeId();
        }
        return !typeId.isFloatingPointTypeId() && !typeId.isBitTypeId() && !typeId.isBlobTypeId() && !typeId.isXMLTypeId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return typeId.isStringTypeId() || (typeId.isDateTimeTimeStampTypeId() && !this.getTypeId().isLongVarcharTypeId());
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return (this.convertible(typeId, false) && !typeId.isBlobTypeId() && !typeId.isNumericTypeId()) || this.userTypeStorable(this.getTypeId(), typeId, classFactory);
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.StringDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return "java.lang.String";
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return dataTypeDescriptor.getMaximumWidth();
    }
    
    String nullMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 5: {
                return "getNullChar";
            }
            case 230: {
                return "getNullLongvarchar";
            }
            case 13: {
                return "getNullVarchar";
            }
            default: {
                return null;
            }
        }
    }
    
    boolean pushCollationForDataValue(final int n) {
        return n != 0;
    }
    
    String dataValueMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 5: {
                return "getCharDataValue";
            }
            case 230: {
                return "getLongvarcharDataValue";
            }
            case 13: {
                return "getVarcharDataValue";
            }
            default: {
                return null;
            }
        }
    }
}
