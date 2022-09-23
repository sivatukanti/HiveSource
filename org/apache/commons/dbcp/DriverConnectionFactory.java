// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;
import java.sql.Driver;

public class DriverConnectionFactory implements ConnectionFactory
{
    protected Driver _driver;
    protected String _connectUri;
    protected Properties _props;
    
    public DriverConnectionFactory(final Driver driver, final String connectUri, final Properties props) {
        this._driver = null;
        this._connectUri = null;
        this._props = null;
        this._driver = driver;
        this._connectUri = connectUri;
        this._props = props;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        return this._driver.connect(this._connectUri, this._props);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + String.valueOf(this._driver) + ";" + String.valueOf(this._connectUri) + ";" + String.valueOf(this._props) + "]";
    }
}
