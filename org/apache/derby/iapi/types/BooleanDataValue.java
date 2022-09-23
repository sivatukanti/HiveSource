// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface BooleanDataValue extends DataValueDescriptor
{
    boolean getBoolean();
    
    BooleanDataValue and(final BooleanDataValue p0);
    
    BooleanDataValue or(final BooleanDataValue p0);
    
    BooleanDataValue is(final BooleanDataValue p0);
    
    BooleanDataValue isNot(final BooleanDataValue p0);
    
    BooleanDataValue throwExceptionIfFalse(final String p0, final String p1, final String p2) throws StandardException;
    
    void setValue(final Boolean p0);
    
    boolean equals(final boolean p0);
    
    BooleanDataValue getImmutable();
}
