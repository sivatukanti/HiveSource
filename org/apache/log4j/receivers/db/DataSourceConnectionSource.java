// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log4j.xml.UnrecognizedElementHandler;

public class DataSourceConnectionSource extends ConnectionSourceSkeleton implements UnrecognizedElementHandler
{
    private DataSource dataSource;
    
    public void activateOptions() {
        if (this.dataSource == null) {
            this.getLogger().warn("WARNING: No data source specified");
        }
        else {
            Connection connection = null;
            try {
                connection = this.getConnection();
            }
            catch (SQLException se) {
                this.getLogger().warn("Could not get a connection to discover the dialect to use.", se);
            }
            if (connection != null) {
                this.discoverConnnectionProperties();
            }
            if (!this.supportsGetGeneratedKeys() && this.getSQLDialectCode() == 0) {
                this.getLogger().warn("Connection does not support GetGeneratedKey method and could not discover the dialect.");
            }
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            this.getLogger().error("WARNING: No data source specified");
            return null;
        }
        if (this.getUser() == null) {
            return this.dataSource.getConnection();
        }
        return this.dataSource.getConnection(this.getUser(), this.getPassword());
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        if ("dataSource".equals(element.getNodeName())) {
            final Object instance = DOMConfigurator.parseElement(element, props, DataSource.class);
            if (instance instanceof DataSource) {
                this.setDataSource((DataSource)instance);
            }
            return true;
        }
        return false;
    }
}
