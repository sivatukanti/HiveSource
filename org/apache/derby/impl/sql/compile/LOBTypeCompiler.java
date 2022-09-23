// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class LOBTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return typeId.isBlobTypeId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return this.convertible(typeId, false);
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return typeId.isBlobTypeId();
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.BitDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 440: {
                return "java.sql.Blob";
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
            case 440: {
                return "getNullBlob";
            }
            default: {
                return null;
            }
        }
    }
    
    String dataValueMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 440: {
                return "getBlobDataValue";
            }
            default: {
                return null;
            }
        }
    }
}
