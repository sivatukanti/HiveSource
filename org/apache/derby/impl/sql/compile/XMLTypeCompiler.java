// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;

public class XMLTypeCompiler extends BaseTypeCompiler
{
    public boolean convertible(final TypeId typeId, final boolean b) {
        return typeId.isXMLTypeId();
    }
    
    public boolean compatible(final TypeId typeId) {
        return typeId.isXMLTypeId();
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return typeId.isXMLTypeId();
    }
    
    public String interfaceName() {
        return "org.apache.derby.iapi.types.XMLDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        if (this.getStoredFormatIdFromTypeId() == 456) {
            return "org.apache.derby.iapi.types.XML";
        }
        return null;
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        return -1;
    }
    
    String nullMethodName() {
        return "getNullXML";
    }
    
    protected String dataValueMethodName() {
        if (this.getStoredFormatIdFromTypeId() == 456) {
            return "getXMLDataValue";
        }
        return null;
    }
}
