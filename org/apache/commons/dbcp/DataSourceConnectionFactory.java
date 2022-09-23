// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class DataSourceConnectionFactory implements ConnectionFactory
{
    protected String _uname;
    protected String _passwd;
    protected DataSource _source;
    
    public DataSourceConnectionFactory(final DataSource source) {
        this(source, null, null);
    }
    
    public DataSourceConnectionFactory(final DataSource source, final String uname, final String passwd) {
        this._uname = null;
        this._passwd = null;
        this._source = null;
        this._source = source;
        this._uname = uname;
        this._passwd = passwd;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        if (null == this._uname && null == this._passwd) {
            return this._source.getConnection();
        }
        return this._source.getConnection(this._uname, this._passwd);
    }
}
