// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.receivers.db.dialect.Util;
import org.apache.log4j.component.spi.ComponentBase;

public abstract class ConnectionSourceSkeleton extends ComponentBase implements ConnectionSource
{
    private Boolean overriddenSupportsGetGeneratedKeys;
    private String user;
    private String password;
    private int dialectCode;
    private boolean supportsGetGeneratedKeys;
    private boolean supportsBatchUpdates;
    
    public ConnectionSourceSkeleton() {
        this.overriddenSupportsGetGeneratedKeys = null;
        this.user = null;
        this.password = null;
        this.dialectCode = 0;
        this.supportsGetGeneratedKeys = false;
        this.supportsBatchUpdates = false;
    }
    
    public void discoverConnnectionProperties() {
        Connection connection = null;
        try {
            connection = this.getConnection();
            if (connection == null) {
                this.getLogger().warn("Could not get a conneciton");
                return;
            }
            final DatabaseMetaData meta = connection.getMetaData();
            final Util util = new Util();
            util.setLoggerRepository(this.repository);
            if (this.overriddenSupportsGetGeneratedKeys != null) {
                this.supportsGetGeneratedKeys = this.overriddenSupportsGetGeneratedKeys;
            }
            else {
                this.supportsGetGeneratedKeys = util.supportsGetGeneratedKeys(meta);
            }
            this.supportsBatchUpdates = util.supportsBatchUpdates(meta);
            this.dialectCode = Util.discoverSQLDialect(meta);
        }
        catch (SQLException se) {
            this.getLogger().warn("Could not discover the dialect to use.", se);
        }
        finally {
            DBHelper.closeConnection(connection);
        }
    }
    
    public final boolean supportsGetGeneratedKeys() {
        return this.supportsGetGeneratedKeys;
    }
    
    public final int getSQLDialectCode() {
        return this.dialectCode;
    }
    
    public final String getPassword() {
        return this.password;
    }
    
    public final void setPassword(final String password) {
        this.password = password;
    }
    
    public final String getUser() {
        return this.user;
    }
    
    public final void setUser(final String username) {
        this.user = username;
    }
    
    public String getOverriddenSupportsGetGeneratedKeys() {
        return (this.overriddenSupportsGetGeneratedKeys != null) ? this.overriddenSupportsGetGeneratedKeys.toString() : null;
    }
    
    public void setOverriddenSupportsGetGeneratedKeys(final String overriddenSupportsGetGeneratedKeys) {
        this.overriddenSupportsGetGeneratedKeys = Boolean.valueOf(overriddenSupportsGetGeneratedKeys);
    }
    
    public final boolean supportsBatchUpdates() {
        return this.supportsBatchUpdates;
    }
}
