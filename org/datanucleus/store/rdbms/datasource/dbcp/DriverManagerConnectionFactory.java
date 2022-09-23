// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Properties;

public class DriverManagerConnectionFactory implements ConnectionFactory
{
    protected String _connectUri;
    protected String _uname;
    protected String _passwd;
    protected Properties _props;
    
    public DriverManagerConnectionFactory(final String connectUri, final Properties props) {
        this._connectUri = null;
        this._uname = null;
        this._passwd = null;
        this._props = null;
        this._connectUri = connectUri;
        this._props = props;
    }
    
    public DriverManagerConnectionFactory(final String connectUri, final String uname, final String passwd) {
        this._connectUri = null;
        this._uname = null;
        this._passwd = null;
        this._props = null;
        this._connectUri = connectUri;
        this._uname = uname;
        this._passwd = passwd;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        if (null != this._props) {
            return DriverManager.getConnection(this._connectUri, this._props);
        }
        if (this._uname == null && this._passwd == null) {
            return DriverManager.getConnection(this._connectUri);
        }
        return DriverManager.getConnection(this._connectUri, this._uname, this._passwd);
    }
    
    static {
        DriverManager.getDrivers();
    }
}
