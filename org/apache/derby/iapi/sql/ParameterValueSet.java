// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public interface ParameterValueSet
{
    void initialize(final DataTypeDescriptor[] p0) throws StandardException;
    
    void setParameterMode(final int p0, final int p1);
    
    void registerOutParameter(final int p0, final int p1, final int p2) throws StandardException;
    
    void clearParameters();
    
    int getParameterCount();
    
    DataValueDescriptor getParameter(final int p0) throws StandardException;
    
    DataValueDescriptor getParameterForSet(final int p0) throws StandardException;
    
    void setParameterAsObject(final int p0, final Object p1) throws StandardException;
    
    DataValueDescriptor getParameterForGet(final int p0) throws StandardException;
    
    boolean allAreSet();
    
    ParameterValueSet getClone();
    
    void validate() throws StandardException;
    
    boolean hasReturnOutputParameter();
    
    boolean checkNoDeclaredOutputParameters();
    
    void transferDataValues(final ParameterValueSet p0) throws StandardException;
    
    short getParameterMode(final int p0);
    
    DataValueDescriptor getReturnValueForSet() throws StandardException;
    
    int getScale(final int p0);
    
    int getPrecision(final int p0);
}
