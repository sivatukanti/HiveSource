// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLException;
import org.apache.derby.iapi.util.IdUtil;
import java.util.Properties;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.jdbc.InternalDriver;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.context.ContextManager;

public final class TransactionResourceImpl
{
    protected ContextManager cm;
    protected ContextService csf;
    protected String username;
    private String dbname;
    private InternalDriver driver;
    private String url;
    private String drdaID;
    protected Database database;
    protected LanguageConnectionContext lcc;
    
    TransactionResourceImpl(final InternalDriver driver, final String url, final Properties properties) throws SQLException {
        this.driver = driver;
        this.csf = driver.getContextServiceFactory();
        this.dbname = InternalDriver.getDatabaseName(url, properties);
        this.url = url;
        this.username = IdUtil.getUserNameFromURLProps(properties);
        this.drdaID = properties.getProperty("drdaID", null);
        this.cm = this.csf.newContextManager();
    }
    
    void setDatabase(final Database database) {
        this.database = database;
    }
    
    void startTransaction() throws StandardException, SQLException {
        this.lcc = this.database.setupConnection(this.cm, this.username, this.drdaID, this.dbname);
    }
    
    InternalDriver getDriver() {
        return this.driver;
    }
    
    ContextService getCsf() {
        return this.csf;
    }
    
    ContextManager getContextManager() {
        return this.cm;
    }
    
    LanguageConnectionContext getLcc() {
        return this.lcc;
    }
    
    String getDBName() {
        return this.dbname;
    }
    
    String getUrl() {
        return this.url;
    }
    
    Database getDatabase() {
        return this.database;
    }
    
    StandardException shutdownDatabaseException() {
        final StandardException exception = StandardException.newException("08006.D", this.getDBName());
        exception.setReport(1);
        return exception;
    }
    
    void commit() throws StandardException {
        this.lcc.userCommit();
    }
    
    void rollback() throws StandardException {
        if (this.lcc != null) {
            this.lcc.userRollback();
        }
    }
    
    void clearContextInError() {
        this.csf.resetCurrentContextManager(this.cm);
        this.cm = null;
    }
    
    void clearLcc() {
        this.lcc = null;
    }
    
    final void setupContextStack() {
        this.csf.setCurrentContextManager(this.cm);
    }
    
    final void restoreContextStack() {
        if (this.csf == null || this.cm == null) {
            return;
        }
        this.csf.resetCurrentContextManager(this.cm);
    }
    
    final SQLException handleException(Throwable shutdownDatabaseException, final boolean b, final boolean b2) throws SQLException {
        try {
            if (shutdownDatabaseException instanceof SQLException) {
                InterruptStatus.restoreIntrFlagIfSeen();
                return (SQLException)shutdownDatabaseException;
            }
            boolean b3 = false;
            if (shutdownDatabaseException instanceof StandardException) {
                final StandardException ex = (StandardException)shutdownDatabaseException;
                if (ex.getSeverity() <= 20000) {
                    if (b && b2) {
                        ex.setSeverity(30000);
                    }
                }
                else if ("08000".equals(ex.getMessageId())) {
                    b3 = true;
                }
            }
            if (this.cm != null) {
                final boolean cleanupOnError = this.cleanupOnError(shutdownDatabaseException, this.database != null && this.database.isActive() && !this.isLoginException(shutdownDatabaseException));
                if (b3 && cleanupOnError) {
                    shutdownDatabaseException = this.shutdownDatabaseException();
                }
            }
            InterruptStatus.restoreIntrFlagIfSeen();
            return wrapInSQLException(shutdownDatabaseException);
        }
        catch (Throwable t) {
            if (this.cm != null) {
                this.cm.cleanupOnError(t, this.database != null && this.isActive());
            }
            InterruptStatus.restoreIntrFlagIfSeen();
            throw wrapInSQLException(t);
        }
    }
    
    private boolean isLoginException(final Throwable t) {
        return t instanceof StandardException && ((StandardException)t).getSQLState().equals("08004");
    }
    
    public static SQLException wrapInSQLException(final Throwable t) {
        if (t == null) {
            return null;
        }
        if (t instanceof SQLException) {
            return (SQLException)t;
        }
        if (!(t instanceof StandardException)) {
            return Util.javaException(t);
        }
        final StandardException ex = (StandardException)t;
        if ("08000".equals(ex.getSQLState())) {
            Thread.currentThread().interrupt();
        }
        if (ex.getCause() == null) {
            return Util.generateCsSQLException(ex);
        }
        return Util.seeNextException(ex.getMessageId(), ex.getArguments(), wrapInSQLException(ex.getCause()));
    }
    
    String getUserName() {
        return this.username;
    }
    
    boolean cleanupOnError(final Throwable t, final boolean b) {
        return this.cm.cleanupOnError(t, b);
    }
    
    boolean isIdle() {
        return this.lcc == null || this.lcc.getTransactionExecute().isIdle();
    }
    
    boolean isActive() {
        return this.driver.isActive() && (this.database == null || this.database.isActive());
    }
}
