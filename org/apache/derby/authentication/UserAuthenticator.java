// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.authentication;

import java.sql.SQLException;
import java.util.Properties;

public interface UserAuthenticator
{
    boolean authenticateUser(final String p0, final String p1, final String p2, final Properties p3) throws SQLException;
}
