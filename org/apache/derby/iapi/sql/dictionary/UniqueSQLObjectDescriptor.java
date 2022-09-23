// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.error.StandardException;

public interface UniqueSQLObjectDescriptor extends UniqueTupleDescriptor
{
    String getName();
    
    SchemaDescriptor getSchemaDescriptor() throws StandardException;
}
