// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class ConnectionProviderPriorityList implements ConnectionProvider
{
    private boolean failOnError;
    
    @Override
    public void setFailOnError(final boolean flag) {
        this.failOnError = flag;
    }
    
    @Override
    public Connection getConnection(final DataSource[] ds) throws SQLException {
        if (ds == null) {
            return null;
        }
        int i = 0;
        while (i < ds.length) {
            try {
                return ds[i].getConnection();
            }
            catch (SQLException e) {
                if (this.failOnError || i == ds.length - 1) {
                    throw e;
                }
                ++i;
                continue;
            }
            break;
        }
        return null;
    }
}
