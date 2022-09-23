// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.conn;

import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

public interface SQLSessionContext
{
    void setRole(final String p0);
    
    String getRole();
    
    void setUser(final String p0);
    
    String getCurrentUser();
    
    void setDefaultSchema(final SchemaDescriptor p0);
    
    SchemaDescriptor getDefaultSchema();
}
