// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface ConcatableDataValue extends DataValueDescriptor, VariableSizeDataValue
{
    NumberDataValue charLength(final NumberDataValue p0) throws StandardException;
    
    ConcatableDataValue substring(final NumberDataValue p0, final NumberDataValue p1, final ConcatableDataValue p2, final int p3) throws StandardException;
}
