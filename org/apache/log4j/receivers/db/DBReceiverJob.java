// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import java.util.Vector;
import org.apache.log4j.spi.ThrowableInformation;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.Hashtable;
import org.apache.log4j.component.scheduler.Job;
import org.apache.log4j.component.spi.ComponentBase;

class DBReceiverJob extends ComponentBase implements Job
{
    String sqlException;
    String sqlProperties;
    String sqlSelect;
    long lastId;
    DBReceiver parentDBReceiver;
    
    DBReceiverJob(final DBReceiver parent) {
        this.sqlException = "SELECT trace_line FROM logging_event_exception where event_id=? ORDER by i ASC";
        this.sqlProperties = "SELECT mapped_key, mapped_value FROM logging_event_property WHERE event_id=?";
        this.sqlSelect = "SELECT sequence_number, timestamp, rendered_message, logger_name, level_string, ndc, thread_name, reference_flag, caller_filename, caller_class, caller_method, caller_line, event_id FROM logging_event WHERE event_id > ?  ORDER BY event_id ASC";
        this.lastId = -32768L;
        this.parentDBReceiver = parent;
    }
    
    public void execute() {
        Connection connection = null;
        try {
            connection = this.parentDBReceiver.connectionSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(this.sqlSelect);
            statement.setLong(1, this.lastId);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Logger logger = null;
                long timeStamp = 0L;
                String level = null;
                String threadName = null;
                Object message = null;
                String ndc = null;
                String className = null;
                String methodName = null;
                String fileName = null;
                String lineNumber = null;
                final Hashtable properties = new Hashtable();
                timeStamp = rs.getLong(2);
                message = rs.getString(3);
                logger = Logger.getLogger(rs.getString(4));
                level = rs.getString(5);
                final Level levelImpl = Level.toLevel(level.trim());
                ndc = rs.getString(6);
                threadName = rs.getString(7);
                final short mask = rs.getShort(8);
                fileName = rs.getString(9);
                className = rs.getString(10);
                methodName = rs.getString(11);
                lineNumber = rs.getString(12).trim();
                LocationInfo locationInfo = null;
                if (fileName.equals("?")) {
                    locationInfo = LocationInfo.NA_LOCATION_INFO;
                }
                else {
                    locationInfo = new LocationInfo(fileName, className, methodName, lineNumber);
                }
                final long id = rs.getLong(13);
                this.lastId = id;
                ThrowableInformation throwableInfo = null;
                if ((mask & 0x2) != 0x0) {
                    throwableInfo = this.getException(connection, id);
                }
                final LoggingEvent event = new LoggingEvent(logger.getName(), logger, timeStamp, levelImpl, message, threadName, throwableInfo, ndc, locationInfo, properties);
                event.setProperty("log4jid", Long.toString(id));
                if ((mask & 0x1) != 0x0) {
                    this.getProperties(connection, id, event);
                }
                if (!this.parentDBReceiver.isPaused()) {
                    this.parentDBReceiver.doPost(event);
                }
            }
            statement.close();
            statement = null;
        }
        catch (SQLException sqle) {
            this.getLogger().error("Problem receiving events", sqle);
        }
        finally {
            this.closeConnection(connection);
        }
    }
    
    void closeConnection(final Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    void getProperties(final Connection connection, final long id, final LoggingEvent event) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(this.sqlProperties);
        try {
            statement.setLong(1, id);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                final String key = rs.getString(1);
                final String value = rs.getString(2);
                event.setProperty(key, value);
            }
        }
        finally {
            statement.close();
        }
    }
    
    ThrowableInformation getException(final Connection connection, final long id) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(this.sqlException);
            statement.setLong(1, id);
            final ResultSet rs = statement.executeQuery();
            final Vector v = new Vector();
            while (rs.next()) {
                v.add(rs.getString(1));
            }
            final int len = v.size();
            final String[] strRep = new String[len];
            for (int i = 0; i < len; ++i) {
                strRep[i] = v.get(i);
            }
            return new ThrowableInformation(strRep);
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
