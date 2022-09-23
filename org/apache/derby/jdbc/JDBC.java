// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.sql.SQLException;
import org.apache.derby.iapi.services.info.JVMInfo;
import org.apache.derby.mbeans.JDBCMBean;

final class JDBC implements JDBCMBean
{
    private final InternalDriver driver;
    
    JDBC(final InternalDriver driver) {
        this.driver = driver;
    }
    
    public String getDriverLevel() {
        return JVMInfo.derbyVMLevel();
    }
    
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }
    
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }
    
    public boolean isCompliantDriver() {
        return this.driver.jdbcCompliant();
    }
    
    public boolean acceptsURL(final String s) throws SQLException {
        return this.driver.acceptsURL(s);
    }
}
