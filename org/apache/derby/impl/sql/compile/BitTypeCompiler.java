// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class BitTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return !typeId.getBaseTypeId().isAnsiUDT() && (typeId.isBitTypeId() || typeId.isBlobTypeId() || typeId.userType());
    }
    
    public boolean compatible(final TypeId typeId) {
        return !typeId.isBlobTypeId() && typeId.isBitTypeId();
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return !typeId.isBlobTypeId() && (typeId.isBitTypeId() || this.userTypeStorable(this.getTypeId(), typeId, classFactory));
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.BitDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        return "byte[]";
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return dataTypeDescriptor.getMaximumWidth();
    }
    
    String nullMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 27: {
                return "getNullBit";
            }
            case 232: {
                return "getNullLongVarbit";
            }
            case 29: {
                return "getNullVarbit";
            }
            default: {
                return null;
            }
        }
    }
    
    String dataValueMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 27: {
                return "getBitDataValue";
            }
            case 232: {
                return "getLongVarbitDataValue";
            }
            case 29: {
                return "getVarbitDataValue";
            }
            default: {
                return null;
            }
        }
    }
}
