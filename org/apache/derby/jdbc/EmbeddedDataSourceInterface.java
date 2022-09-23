// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import javax.sql.DataSource;

public interface EmbeddedDataSourceInterface extends DataSource
{
    void setDatabaseName(final String p0);
    
    String getDatabaseName();
    
    void setDataSourceName(final String p0);
    
    String getDataSourceName();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setUser(final String p0);
    
    String getUser();
    
    void setPassword(final String p0);
    
    String getPassword();
    
    void setCreateDatabase(final String p0);
    
    String getCreateDatabase();
    
    void setConnectionAttributes(final String p0);
    
    String getConnectionAttributes();
    
    void setShutdownDatabase(final String p0);
    
    String getShutdownDatabase();
    
    void setAttributesAsPassword(final boolean p0);
    
    boolean getAttributesAsPassword();
}
