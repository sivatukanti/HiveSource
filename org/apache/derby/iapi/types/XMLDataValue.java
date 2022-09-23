// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface XMLDataValue extends DataValueDescriptor
{
    XMLDataValue XMLParse(final StringDataValue p0, final boolean p1, final SqlXmlUtil p2) throws StandardException;
    
    StringDataValue XMLSerialize(final StringDataValue p0, final int p1, final int p2, final int p3) throws StandardException;
    
    BooleanDataValue XMLExists(final SqlXmlUtil p0) throws StandardException;
    
    XMLDataValue XMLQuery(final SqlXmlUtil p0, final XMLDataValue p1) throws StandardException;
    
    void setXType(final int p0);
    
    int getXType();
    
    void markAsHavingTopLevelAttr();
    
    boolean hasTopLevelAttr();
}
