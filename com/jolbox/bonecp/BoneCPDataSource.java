// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.util.Enumeration;
import javax.naming.RefAddr;
import java.util.Properties;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.ExecutionException;
import java.sql.SQLException;
import java.sql.Connection;
import java.lang.reflect.Field;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import java.io.PrintWriter;
import java.io.Closeable;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

public class BoneCPDataSource extends BoneCPConfig implements DataSource, ObjectFactory, Closeable
{
    private static final long serialVersionUID = -1561804548443209469L;
    private transient PrintWriter logWriter;
    private transient FinalWrapper<BoneCP> pool;
    private String driverClass;
    private static final Logger logger;
    private LoadingCache<UsernamePassword, BoneCPDataSource> multiDataSource;
    
    public BoneCPDataSource() {
        this.logWriter = null;
        this.pool = null;
        this.multiDataSource = CacheBuilder.newBuilder().build((CacheLoader<? super UsernamePassword, BoneCPDataSource>)new CacheLoader<UsernamePassword, BoneCPDataSource>() {
            @Override
            public BoneCPDataSource load(final UsernamePassword key) throws Exception {
                BoneCPDataSource ds = null;
                ds = new BoneCPDataSource(BoneCPDataSource.this.getConfig());
                ds.setUsername(key.getUsername());
                ds.setPassword(key.getPassword());
                return ds;
            }
        });
    }
    
    public BoneCPDataSource(final BoneCPConfig config) {
        this.logWriter = null;
        this.pool = null;
        this.multiDataSource = CacheBuilder.newBuilder().build((CacheLoader<? super UsernamePassword, BoneCPDataSource>)new CacheLoader<UsernamePassword, BoneCPDataSource>() {
            @Override
            public BoneCPDataSource load(final UsernamePassword key) throws Exception {
                BoneCPDataSource ds = null;
                ds = new BoneCPDataSource(BoneCPDataSource.this.getConfig());
                ds.setUsername(key.getUsername());
                ds.setPassword(key.getPassword());
                return ds;
            }
        });
        final Field[] arr$;
        final Field[] fields = arr$ = BoneCPConfig.class.getDeclaredFields();
        for (final Field field : arr$) {
            try {
                field.setAccessible(true);
                field.set(this, field.get(config));
            }
            catch (Exception ex) {}
        }
    }
    
    public Connection getConnection() throws SQLException {
        FinalWrapper<BoneCP> wrapper = this.pool;
        if (wrapper == null) {
            synchronized (this) {
                if (this.pool == null) {
                    try {
                        if (this.getDriverClass() != null) {
                            this.loadClass(this.getDriverClass());
                        }
                        BoneCPDataSource.logger.debug(this.toString());
                        this.pool = new FinalWrapper<BoneCP>(new BoneCP(this));
                    }
                    catch (ClassNotFoundException e) {
                        throw new SQLException(PoolUtil.stringifyException(e));
                    }
                }
                wrapper = this.pool;
            }
        }
        return ((BoneCP)((FinalWrapper<Object>)wrapper).value).getConnection();
    }
    
    public void close() {
        if (this.getPool() != null) {
            this.getPool().shutdown();
            BoneCPDataSource.logger.debug("Connection pool has been shut down");
        }
    }
    
    public Connection getConnection(final String username, final String password) throws SQLException {
        try {
            return this.multiDataSource.get(new UsernamePassword(username, password)).getConnection();
        }
        catch (ExecutionException e) {
            throw PoolUtil.generateSQLException("Unable to obtain connection", e);
        }
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        return this.logWriter;
    }
    
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("getLoginTimeout is unsupported.");
    }
    
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("getParentLogger is unsupported");
    }
    
    public void setLogWriter(final PrintWriter out) throws SQLException {
        this.logWriter = out;
    }
    
    public void setLoginTimeout(final int seconds) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout is unsupported.");
    }
    
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        return false;
    }
    
    public Object unwrap(final Class arg0) throws SQLException {
        return null;
    }
    
    public String getDriverClass() {
        return this.driverClass;
    }
    
    public void setDriverClass(final String driverClass) {
        this.driverClass = driverClass;
    }
    
    public int getTotalLeased() {
        return (this.getPool() == null) ? 0 : this.getPool().getTotalLeased();
    }
    
    public BoneCPConfig getConfig() {
        return this;
    }
    
    public Object getObjectInstance(final Object object, final Name name, final Context context, final Hashtable<?, ?> table) throws Exception {
        final Reference ref = (Reference)object;
        final Enumeration<RefAddr> addrs = ref.getAll();
        final Properties props = new Properties();
        while (addrs.hasMoreElements()) {
            final RefAddr addr = addrs.nextElement();
            if (addr.getType().equals("driverClassName")) {
                Class.forName((String)addr.getContent());
            }
            else {
                props.put(addr.getType(), addr.getContent());
            }
        }
        final BoneCPConfig config = new BoneCPConfig(props);
        return new BoneCPDataSource(config);
    }
    
    public BoneCP getPool() {
        return (this.pool == null) ? null : ((BoneCP)((FinalWrapper<Object>)this.pool).value);
    }
    
    static {
        logger = LoggerFactory.getLogger(BoneCPDataSource.class);
    }
    
    class FinalWrapper<T>
    {
        private final T value;
        
        public FinalWrapper(final T value) {
            this.value = value;
        }
    }
}
