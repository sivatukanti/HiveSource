// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.conn.SQLSessionContext;

public class SQLSessionContextImpl implements SQLSessionContext
{
    private String currentUser;
    private String currentRole;
    private SchemaDescriptor currentDefaultSchema;
    
    public SQLSessionContextImpl(final SchemaDescriptor currentDefaultSchema, final String currentUser) {
        this.currentRole = null;
        this.currentDefaultSchema = currentDefaultSchema;
        this.currentUser = currentUser;
    }
    
    public void setRole(final String currentRole) {
        this.currentRole = currentRole;
    }
    
    public String getRole() {
        return this.currentRole;
    }
    
    public void setUser(final String currentUser) {
        this.currentUser = currentUser;
    }
    
    public String getCurrentUser() {
        return this.currentUser;
    }
    
    public void setDefaultSchema(final SchemaDescriptor currentDefaultSchema) {
        this.currentDefaultSchema = currentDefaultSchema;
    }
    
    public SchemaDescriptor getDefaultSchema() {
        return this.currentDefaultSchema;
    }
}
