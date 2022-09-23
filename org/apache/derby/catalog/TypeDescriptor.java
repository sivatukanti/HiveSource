// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import org.apache.derby.iapi.types.DataTypeDescriptor;

public interface TypeDescriptor
{
    public static final int MAXIMUM_WIDTH_UNKNOWN = -1;
    public static final TypeDescriptor INTEGER = DataTypeDescriptor.INTEGER.getCatalogType();
    public static final TypeDescriptor INTEGER_NOT_NULL = DataTypeDescriptor.INTEGER_NOT_NULL.getCatalogType();
    public static final TypeDescriptor SMALLINT = DataTypeDescriptor.SMALLINT.getCatalogType();
    public static final TypeDescriptor SMALLINT_NOT_NULL = DataTypeDescriptor.SMALLINT_NOT_NULL.getCatalogType();
    
    int getJDBCTypeId();
    
    int getMaximumWidth();
    
    int getMaximumWidthInBytes();
    
    int getPrecision();
    
    int getScale();
    
    boolean isNullable();
    
    String getTypeName();
    
    String getSQLstring();
    
    int getCollationType();
    
    boolean isRowMultiSet();
    
    boolean isUserDefinedType();
    
    TypeDescriptor[] getRowTypes();
    
    String[] getRowColumnNames();
}
