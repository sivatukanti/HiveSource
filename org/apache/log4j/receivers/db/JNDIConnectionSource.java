// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class JNDIConnectionSource extends ConnectionSourceSkeleton
{
    private String jndiLocation;
    private DataSource dataSource;
    
    public JNDIConnectionSource() {
        this.jndiLocation = null;
        this.dataSource = null;
    }
    
    public void activateOptions() {
        if (this.jndiLocation == null) {
            this.getLogger().error("No JNDI location specified for JNDIConnectionSource.");
        }
        this.discoverConnnectionProperties();
    }
    
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            if (this.dataSource == null) {
                this.dataSource = this.lookupDataSource();
            }
            if (this.getUser() == null) {
                conn = this.dataSource.getConnection();
            }
            else {
                conn = this.dataSource.getConnection(this.getUser(), this.getPassword());
            }
        }
        catch (NamingException ne) {
            this.getLogger().error("Error while getting data source", ne);
            throw new SQLException("NamingException while looking up DataSource: " + ne.getMessage());
        }
        catch (ClassCastException cce) {
            this.getLogger().error("ClassCastException while looking up DataSource.", cce);
            throw new SQLException("ClassCastException while looking up DataSource: " + cce.getMessage());
        }
        return conn;
    }
    
    public String getJndiLocation() {
        return this.jndiLocation;
    }
    
    public void setJndiLocation(final String jndiLocation) {
        this.jndiLocation = jndiLocation;
    }
    
    private DataSource lookupDataSource() throws NamingException, SQLException {
        final Context ctx = new InitialContext();
        final Object obj = ctx.lookup(this.jndiLocation);
        final DataSource ds = (DataSource)obj;
        if (ds == null) {
            throw new SQLException("Failed to obtain data source from JNDI location " + this.jndiLocation);
        }
        return ds;
    }
}
