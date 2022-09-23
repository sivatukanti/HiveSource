// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface VariableSizeDataValue
{
    public static final int IGNORE_PRECISION = -1;
    
    void setWidth(final int p0, final int p1, final boolean p2) throws StandardException;
}
