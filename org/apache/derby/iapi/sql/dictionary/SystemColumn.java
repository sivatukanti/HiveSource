// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.types.DataTypeDescriptor;

public interface SystemColumn
{
    String getName();
    
    DataTypeDescriptor getType();
}
