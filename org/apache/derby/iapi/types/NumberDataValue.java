// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

public interface NumberDataValue extends DataValueDescriptor
{
    public static final int MIN_DECIMAL_DIVIDE_SCALE = 4;
    public static final int MAX_DECIMAL_PRECISION_SCALE = 31;
    
    NumberDataValue plus(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    NumberDataValue minus(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    NumberDataValue times(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    NumberDataValue divide(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    NumberDataValue divide(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2, final int p3) throws StandardException;
    
    NumberDataValue mod(final NumberDataValue p0, final NumberDataValue p1, final NumberDataValue p2) throws StandardException;
    
    NumberDataValue minus(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue absolute(final NumberDataValue p0) throws StandardException;
    
    NumberDataValue sqrt(final NumberDataValue p0) throws StandardException;
    
    void setValue(final Number p0) throws StandardException;
    
    int getDecimalValuePrecision();
    
    int getDecimalValueScale();
}
