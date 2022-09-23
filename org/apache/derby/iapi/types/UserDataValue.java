// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface UserDataValue extends DataValueDescriptor
{
    void setValue(final Object p0) throws StandardException;
}
