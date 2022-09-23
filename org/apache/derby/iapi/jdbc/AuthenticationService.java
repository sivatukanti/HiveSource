// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public interface AuthenticationService
{
    public static final String MODULE = "org.apache.derby.iapi.jdbc.AuthenticationService";
    
    boolean authenticate(final String p0, final Properties p1) throws SQLException;
    
    String getSystemCredentialsDatabaseName();
}
