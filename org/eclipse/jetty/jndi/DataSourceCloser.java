// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import org.eclipse.jetty.util.log.Log;
import java.lang.reflect.Method;
import java.sql.Statement;
import javax.sql.DataSource;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Destroyable;

public class DataSourceCloser implements Destroyable
{
    private static final Logger LOG;
    final DataSource _datasource;
    final String _shutdown;
    
    public DataSourceCloser(final DataSource datasource) {
        if (datasource == null) {
            throw new IllegalArgumentException();
        }
        this._datasource = datasource;
        this._shutdown = null;
    }
    
    public DataSourceCloser(final DataSource datasource, final String shutdownSQL) {
        if (datasource == null) {
            throw new IllegalArgumentException();
        }
        this._datasource = datasource;
        this._shutdown = shutdownSQL;
    }
    
    public void destroy() {
        try {
            if (this._shutdown != null) {
                DataSourceCloser.LOG.info("Shutdown datasource {}", this._datasource);
                final Statement stmt = this._datasource.getConnection().createStatement();
                stmt.executeUpdate(this._shutdown);
                stmt.close();
            }
        }
        catch (Exception e) {
            DataSourceCloser.LOG.warn(e);
        }
        try {
            final Method close = this._datasource.getClass().getMethod("close", (Class<?>[])new Class[0]);
            DataSourceCloser.LOG.info("Close datasource {}", this._datasource);
            close.invoke(this._datasource, new Object[0]);
        }
        catch (Exception e) {
            DataSourceCloser.LOG.warn(e);
        }
    }
    
    static {
        LOG = Log.getLogger(DataSourceCloser.class);
    }
}
