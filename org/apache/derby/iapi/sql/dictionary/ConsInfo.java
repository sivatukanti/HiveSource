// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.Formatable;

public interface ConsInfo extends Formatable
{
    SchemaDescriptor getReferencedTableSchemaDescriptor(final DataDictionary p0) throws StandardException;
    
    TableDescriptor getReferencedTableDescriptor(final DataDictionary p0) throws StandardException;
    
    String[] getReferencedColumnNames();
    
    String getReferencedTableName();
    
    int getReferentialActionUpdateRule();
    
    int getReferentialActionDeleteRule();
}
