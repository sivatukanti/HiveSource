// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j;

import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.util.Set;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.receivers.db.DBHelper;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.receivers.db.dialect.Util;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.receivers.db.dialect.SQLDialect;
import org.apache.log4j.receivers.db.ConnectionSource;
import java.lang.reflect.Method;
import org.apache.log4j.xml.UnrecognizedElementHandler;

public class DBAppender extends AppenderSkeleton implements UnrecognizedElementHandler
{
    static final String insertPropertiesSQL = "INSERT INTO  logging_event_property (event_id, mapped_key, mapped_value) VALUES (?, ?, ?)";
    static final String insertExceptionSQL = "INSERT INTO  logging_event_exception (event_id, i, trace_line) VALUES (?, ?, ?)";
    static final String insertSQL;
    private static final Method GET_GENERATED_KEYS_METHOD;
    ConnectionSource connectionSource;
    boolean cnxSupportsGetGeneratedKeys;
    boolean cnxSupportsBatchUpdates;
    SQLDialect sqlDialect;
    boolean locationInfo;
    
    public DBAppender() {
        super(false);
        this.cnxSupportsGetGeneratedKeys = false;
        this.cnxSupportsBatchUpdates = false;
        this.locationInfo = false;
    }
    
    public void activateOptions() {
        LogLog.debug("DBAppender.activateOptions called");
        if (this.connectionSource == null) {
            throw new IllegalStateException("DBAppender cannot function without a connection source");
        }
        this.sqlDialect = Util.getDialectFromCode(this.connectionSource.getSQLDialectCode());
        if (DBAppender.GET_GENERATED_KEYS_METHOD != null) {
            this.cnxSupportsGetGeneratedKeys = this.connectionSource.supportsGetGeneratedKeys();
        }
        else {
            this.cnxSupportsGetGeneratedKeys = false;
        }
        this.cnxSupportsBatchUpdates = this.connectionSource.supportsBatchUpdates();
        if (!this.cnxSupportsGetGeneratedKeys && this.sqlDialect == null) {
            throw new IllegalStateException("DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
        }
        super.activateOptions();
    }
    
    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }
    
    public void setConnectionSource(final ConnectionSource connectionSource) {
        LogLog.debug("setConnectionSource called for DBAppender");
        this.connectionSource = connectionSource;
    }
    
    protected void append(final LoggingEvent event) {
        Connection connection = null;
        try {
            connection = this.connectionSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement insertStatement;
            if (this.cnxSupportsGetGeneratedKeys) {
                insertStatement = connection.prepareStatement(DBAppender.insertSQL, 1);
            }
            else {
                insertStatement = connection.prepareStatement(DBAppender.insertSQL);
            }
            insertStatement.setLong(1, 0L);
            insertStatement.setLong(2, event.getTimeStamp());
            insertStatement.setString(3, event.getRenderedMessage());
            insertStatement.setString(4, event.getLoggerName());
            insertStatement.setString(5, event.getLevel().toString());
            insertStatement.setString(6, event.getNDC());
            insertStatement.setString(7, event.getThreadName());
            insertStatement.setShort(8, DBHelper.computeReferenceMask(event));
            LocationInfo li;
            if (event.locationInformationExists() || this.locationInfo) {
                li = event.getLocationInformation();
            }
            else {
                li = LocationInfo.NA_LOCATION_INFO;
            }
            insertStatement.setString(9, li.getFileName());
            insertStatement.setString(10, li.getClassName());
            insertStatement.setString(11, li.getMethodName());
            insertStatement.setString(12, li.getLineNumber());
            final int updateCount = insertStatement.executeUpdate();
            if (updateCount != 1) {
                LogLog.warn("Failed to insert loggingEvent");
            }
            ResultSet rs = null;
            Statement idStatement = null;
            boolean gotGeneratedKeys = false;
            if (this.cnxSupportsGetGeneratedKeys) {
                try {
                    rs = (ResultSet)DBAppender.GET_GENERATED_KEYS_METHOD.invoke(insertStatement, (Object[])null);
                    gotGeneratedKeys = true;
                }
                catch (InvocationTargetException ex) {
                    final Throwable target = ex.getTargetException();
                    if (target instanceof SQLException) {
                        throw (SQLException)target;
                    }
                    throw ex;
                }
                catch (IllegalAccessException ex2) {
                    LogLog.warn("IllegalAccessException invoking PreparedStatement.getGeneratedKeys", ex2);
                }
            }
            if (!gotGeneratedKeys) {
                insertStatement.close();
                insertStatement = null;
                idStatement = connection.createStatement();
                idStatement.setMaxRows(1);
                rs = idStatement.executeQuery(this.sqlDialect.getSelectInsertId());
            }
            rs.next();
            final int eventId = rs.getInt(1);
            rs.close();
            if (insertStatement != null) {
                insertStatement.close();
                insertStatement = null;
            }
            if (idStatement != null) {
                idStatement.close();
                idStatement = null;
            }
            final Set propertiesKeys = event.getPropertyKeySet();
            if (propertiesKeys.size() > 0) {
                PreparedStatement insertPropertiesStatement = connection.prepareStatement("INSERT INTO  logging_event_property (event_id, mapped_key, mapped_value) VALUES (?, ?, ?)");
                for (final String key : propertiesKeys) {
                    final String value = event.getProperty(key);
                    insertPropertiesStatement.setInt(1, eventId);
                    insertPropertiesStatement.setString(2, key);
                    insertPropertiesStatement.setString(3, value);
                    if (this.cnxSupportsBatchUpdates) {
                        insertPropertiesStatement.addBatch();
                    }
                    else {
                        insertPropertiesStatement.execute();
                    }
                }
                if (this.cnxSupportsBatchUpdates) {
                    insertPropertiesStatement.executeBatch();
                }
                insertPropertiesStatement.close();
                insertPropertiesStatement = null;
            }
            final String[] strRep = event.getThrowableStrRep();
            if (strRep != null) {
                LogLog.debug("Logging an exception");
                PreparedStatement insertExceptionStatement = connection.prepareStatement("INSERT INTO  logging_event_exception (event_id, i, trace_line) VALUES (?, ?, ?)");
                for (short j = 0; j < strRep.length; ++j) {
                    insertExceptionStatement.setInt(1, eventId);
                    insertExceptionStatement.setShort(2, j);
                    insertExceptionStatement.setString(3, strRep[j]);
                    if (this.cnxSupportsBatchUpdates) {
                        insertExceptionStatement.addBatch();
                    }
                    else {
                        insertExceptionStatement.execute();
                    }
                }
                if (this.cnxSupportsBatchUpdates) {
                    insertExceptionStatement.executeBatch();
                }
                insertExceptionStatement.close();
                insertExceptionStatement = null;
            }
            connection.commit();
        }
        catch (Throwable sqle) {
            LogLog.error("problem appending event", sqle);
        }
        finally {
            DBHelper.closeConnection(connection);
        }
    }
    
    public void close() {
        this.closed = true;
    }
    
    public boolean getLocationInfo() {
        return this.locationInfo;
    }
    
    public void setLocationInfo(final boolean locationInfo) {
        this.locationInfo = locationInfo;
    }
    
    public boolean requiresLayout() {
        return false;
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        if ("connectionSource".equals(element.getNodeName())) {
            final Object instance = DOMConfigurator.parseElement(element, props, ConnectionSource.class);
            if (instance instanceof ConnectionSource) {
                final ConnectionSource source = (ConnectionSource)instance;
                source.activateOptions();
                this.setConnectionSource(source);
            }
            return true;
        }
        return false;
    }
    
    static {
        final StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO logging_event (");
        sql.append("sequence_number, ");
        sql.append("timestamp, ");
        sql.append("rendered_message, ");
        sql.append("logger_name, ");
        sql.append("level_string, ");
        sql.append("ndc, ");
        sql.append("thread_name, ");
        sql.append("reference_flag, ");
        sql.append("caller_filename, ");
        sql.append("caller_class, ");
        sql.append("caller_method, ");
        sql.append("caller_line) ");
        sql.append(" VALUES (?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?)");
        insertSQL = sql.toString();
        Method getGeneratedKeysMethod;
        try {
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class[])null);
        }
        catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }
}
