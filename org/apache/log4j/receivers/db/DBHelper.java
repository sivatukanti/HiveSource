// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Set;
import org.apache.log4j.spi.LoggingEvent;

public class DBHelper
{
    public static final short PROPERTIES_EXIST = 1;
    public static final short EXCEPTION_EXISTS = 2;
    
    public static short computeReferenceMask(final LoggingEvent event) {
        short mask = 0;
        final Set propertiesKeys = event.getPropertyKeySet();
        if (propertiesKeys.size() > 0) {
            mask = 1;
        }
        final String[] strRep = event.getThrowableStrRep();
        if (strRep != null) {
            mask |= 0x2;
        }
        return mask;
    }
    
    public static void closeConnection(final Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    public static void closeStatement(final Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException ex) {}
        }
    }
}
