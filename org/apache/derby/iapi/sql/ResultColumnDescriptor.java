// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import org.apache.derby.iapi.types.DataTypeDescriptor;

public interface ResultColumnDescriptor
{
    DataTypeDescriptor getType();
    
    String getName();
    
    String getSourceSchemaName();
    
    String getSourceTableName();
    
    boolean updatableByCursor();
    
    int getColumnPosition();
    
    boolean isAutoincrement();
    
    boolean hasGenerationClause();
}
