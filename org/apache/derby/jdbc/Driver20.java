// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.apache.derby.impl.jdbc.Util;
import java.util.concurrent.ExecutionException;
import org.apache.derby.iapi.util.InterruptStatus;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.sql.Connection;
import org.apache.derby.iapi.security.SecurityUtil;
import java.security.Permission;
import org.apache.derby.iapi.services.io.FormatableProperties;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.DriverPropertyInfo;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
import java.sql.SQLException;
import org.apache.derby.impl.jdbc.EmbedResultSet20;
import org.apache.derby.impl.jdbc.EmbedResultSet;
import org.apache.derby.impl.jdbc.EmbedStatement;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.impl.jdbc.EmbedConnection;
import org.apache.derby.iapi.error.StandardException;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;
import java.sql.Driver;

public abstract class Driver20 extends InternalDriver implements Driver
{
    private static ThreadPoolExecutor _executorPool;
    private static final String[] BOOLEAN_CHOICES;
    private Class antiGCDriverManager;
    private static final String driver20 = "driver20";
    
    @Override
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
        AutoloadedDriver.registerDriverModule(this);
        this.antiGCDriverManager = DriverManager.class;
    }
    
    @Override
    public void stop() {
        super.stop();
        AutoloadedDriver.unregisterDriverModule();
    }
    
    @Override
    public EmbedResultSet newEmbedResultSet(final EmbedConnection embedConnection, final ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        return new EmbedResultSet20(embedConnection, set, b, embedStatement, b2);
    }
    
    public abstract BrokeredConnection newBrokeredConnection(final BrokeredConnectionControl p0) throws SQLException;
    
    public DriverPropertyInfo[] getPropertyInfo(final String s, final Properties properties) throws SQLException {
        if (properties != null && Boolean.valueOf(properties.getProperty("shutdown"))) {
            return new DriverPropertyInfo[0];
        }
        final String databaseName = InternalDriver.getDatabaseName(s, properties);
        final FormatableProperties attributes = this.getAttributes(s, properties);
        final boolean booleanValue = Boolean.valueOf(attributes.getProperty("dataEncryption"));
        final String property = attributes.getProperty("bootPassword");
        if (databaseName.length() == 0 || (booleanValue && property == null)) {
            final String[][] array = { { "databaseName", "J004" }, { "encryptionProvider", "J016" }, { "encryptionAlgorithm", "J017" }, { "encryptionKeyLength", "J018" }, { "encryptionKey", "J019" }, { "territory", "J021" }, { "collation", "J031" }, { "user", "J022" }, { "logDevice", "J025" }, { "rollForwardRecoveryFrom", "J028" }, { "createFrom", "J029" }, { "restoreFrom", "J030" } };
            final String[][] array2 = { { "shutdown", "J005" }, { "deregister", "J006" }, { "create", "J007" }, { "dataEncryption", "J010" }, { "upgrade", "J013" } };
            final String[][] array3 = { { "bootPassword", "J020" }, { "password", "J023" } };
            final DriverPropertyInfo[] array4 = new DriverPropertyInfo[array.length + array2.length + array3.length];
            int n = 0;
            for (int i = 0; i < array.length; ++i, ++n) {
                array4[n] = new DriverPropertyInfo(array[i][0], attributes.getProperty(array[i][0]));
                array4[n].description = MessageService.getTextMessage(array[i][1]);
            }
            array4[0].choices = Monitor.getMonitor().getServiceList("org.apache.derby.database.Database");
            array4[0].value = databaseName;
            for (int j = 0; j < array3.length; ++j, ++n) {
                array4[n] = new DriverPropertyInfo(array3[j][0], (attributes.getProperty(array3[j][0]) == null) ? "" : "****");
                array4[n].description = MessageService.getTextMessage(array3[j][1]);
            }
            for (int k = 0; k < array2.length; ++k, ++n) {
                array4[n] = new DriverPropertyInfo(array2[k][0], Boolean.valueOf((attributes == null) ? "" : attributes.getProperty(array2[k][0])).toString());
                array4[n].description = MessageService.getTextMessage(array2[k][1]);
                array4[n].choices = Driver20.BOOLEAN_CHOICES;
            }
            return array4;
        }
        return new DriverPropertyInfo[0];
    }
    
    @Override
    public void checkSystemPrivileges(final String s, final Permission permission) throws Exception {
        SecurityUtil.checkUserHasPermission(s, permission);
    }
    
    public Connection connect(final String s, final Properties properties) throws SQLException {
        return this.connect(s, properties, DriverManager.getLoginTimeout());
    }
    
    @Override
    protected EmbedConnection timeLogin(final String s, final Properties properties, final int n) throws SQLException {
        try {
            final Future<EmbedConnection> submit = Driver20._executorPool.submit((Callable<EmbedConnection>)new LoginCallable(this, s, properties));
            long n2 = System.currentTimeMillis();
            final long n3 = n2 + n * 1000L;
            while (n2 < n3) {
                try {
                    return submit.get(n3 - n2, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                    n2 = System.currentTimeMillis();
                    continue;
                }
                catch (ExecutionException ex) {
                    throw this.processException(ex);
                }
                catch (TimeoutException ex3) {
                    throw Util.generateCsSQLException("XBDA0.C.1");
                }
                break;
            }
            throw Util.generateCsSQLException("XBDA0.C.1");
        }
        finally {
            InterruptStatus.restoreIntrFlagIfSeen();
        }
    }
    
    private SQLException processException(final Throwable t) {
        final Throwable cause = t.getCause();
        if (!(cause instanceof SQLException)) {
            return Util.javaException(t);
        }
        return (SQLException)cause;
    }
    
    static {
        (Driver20._executorPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>())).setThreadFactory(new DaemonThreadFactory());
        BOOLEAN_CHOICES = new String[] { "false", "true" };
    }
    
    public static final class LoginCallable implements Callable<EmbedConnection>
    {
        private Driver20 _driver;
        private String _url;
        private Properties _info;
        
        public LoginCallable(final Driver20 driver, final String url, final Properties info) {
            this._driver = driver;
            this._url = url;
            this._info = info;
        }
        
        public EmbedConnection call() throws SQLException {
            final String url = this._url;
            final Properties info = this._info;
            final Driver20 driver = this._driver;
            this._url = null;
            this._info = null;
            this._driver = null;
            return driver.getNewEmbedConnection(url, info);
        }
    }
    
    private static final class DaemonThreadFactory implements ThreadFactory
    {
        public Thread newThread(final Runnable target) {
            final Thread thread = new Thread(target);
            thread.setDaemon(true);
            return thread;
        }
    }
}
