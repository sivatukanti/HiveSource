// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Map;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.Level;
import java.util.StringTokenizer;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import org.apache.log4j.component.ULogger;
import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import java.sql.SQLException;
import org.apache.log4j.component.scheduler.Scheduler;
import org.apache.log4j.component.scheduler.Job;
import org.apache.log4j.component.spi.LoggerRepositoryEx;
import java.sql.Connection;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.plugins.Receiver;

public class CustomSQLDBReceiver extends Receiver implements Pauseable, UnrecognizedElementHandler
{
    protected volatile Connection connection;
    protected String sqlStatement;
    static int DEFAULT_REFRESH_MILLIS;
    int refreshMillis;
    protected String idField;
    int lastID;
    private static final String WHERE_CLAUSE = " WHERE ";
    private static final String AND_CLAUSE = " AND ";
    private boolean whereExists;
    private boolean paused;
    private ConnectionSource connectionSource;
    public static final String LOG4J_ID_KEY = "log4jid";
    private CustomReceiverJob customReceiverJob;
    
    public CustomSQLDBReceiver() {
        this.connection = null;
        this.sqlStatement = "";
        this.refreshMillis = CustomSQLDBReceiver.DEFAULT_REFRESH_MILLIS;
        this.idField = null;
        this.lastID = -1;
        this.whereExists = false;
        this.paused = false;
    }
    
    public void activateOptions() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("CustomSQLDBReceiver cannot function without a connection source");
        }
        this.whereExists = (this.sqlStatement.toUpperCase().indexOf(" WHERE ") > -1);
        this.customReceiverJob = new CustomReceiverJob();
        if (this.repository == null) {
            throw new IllegalStateException("CustomSQLDBReceiver cannot function without a reference to its owning repository");
        }
        if (this.repository instanceof LoggerRepositoryEx) {
            final Scheduler scheduler = ((LoggerRepositoryEx)this.repository).getScheduler();
            scheduler.schedule(this.customReceiverJob, System.currentTimeMillis() + 500L, this.refreshMillis);
        }
    }
    
    void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
            }
            catch (SQLException ex) {}
        }
    }
    
    public void setRefreshMillis(final int refreshMillis) {
        this.refreshMillis = refreshMillis;
    }
    
    public int getRefreshMillis() {
        return this.refreshMillis;
    }
    
    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }
    
    public void setConnectionSource(final ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }
    
    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            this.connection = null;
        }
    }
    
    public void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
    
    public void shutdown() {
        this.getLogger().info("removing receiverJob from the Scheduler.");
        if (this.repository instanceof LoggerRepositoryEx) {
            final Scheduler scheduler = ((LoggerRepositoryEx)this.repository).getScheduler();
            scheduler.delete(this.customReceiverJob);
        }
        this.lastID = -1;
    }
    
    public void setSql(final String s) {
        this.sqlStatement = s;
    }
    
    public String getSql() {
        return this.sqlStatement;
    }
    
    public void setIDField(final String id) {
        this.idField = id;
    }
    
    public String getIDField() {
        return this.idField;
    }
    
    public synchronized void setPaused(final boolean p) {
        this.paused = p;
    }
    
    public synchronized boolean isPaused() {
        return this.paused;
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
        CustomSQLDBReceiver.DEFAULT_REFRESH_MILLIS = 1000;
    }
    
    class CustomReceiverJob implements Job
    {
        public void execute() {
            int oldLastID = CustomSQLDBReceiver.this.lastID;
            try {
                CustomSQLDBReceiver.this.connection = CustomSQLDBReceiver.this.connectionSource.getConnection();
                Statement statement = CustomSQLDBReceiver.this.connection.createStatement();
                Logger eventLogger = null;
                long timeStamp = 0L;
                String level = null;
                String threadName = null;
                Object message = null;
                String ndc = null;
                Hashtable mdc = null;
                String[] throwable = null;
                String className = null;
                String methodName = null;
                String fileName = null;
                String lineNumber = null;
                Hashtable properties = null;
                String currentSQLStatement = CustomSQLDBReceiver.this.sqlStatement;
                if (CustomSQLDBReceiver.this.whereExists) {
                    currentSQLStatement = CustomSQLDBReceiver.this.sqlStatement + " AND " + CustomSQLDBReceiver.this.idField + " > " + CustomSQLDBReceiver.this.lastID;
                }
                else {
                    currentSQLStatement = CustomSQLDBReceiver.this.sqlStatement + " WHERE " + CustomSQLDBReceiver.this.idField + " > " + CustomSQLDBReceiver.this.lastID;
                }
                final ResultSet rs = statement.executeQuery(currentSQLStatement);
                int i = 0;
                while (rs.next()) {
                    if (++i == 1000) {
                        synchronized (this) {
                            try {
                                this.wait(300L);
                            }
                            catch (InterruptedException ex) {}
                            i = 0;
                        }
                    }
                    eventLogger = Logger.getLogger(rs.getString("LOGGER"));
                    timeStamp = rs.getTimestamp("TIMESTAMP").getTime();
                    level = rs.getString("LEVEL");
                    threadName = rs.getString("THREAD");
                    message = rs.getString("MESSAGE");
                    ndc = rs.getString("NDC");
                    String mdcString = rs.getString("MDC");
                    mdc = new Hashtable();
                    if (mdcString != null) {
                        if (mdcString.indexOf("{{") > -1 && mdcString.indexOf("}}") > -1) {
                            mdcString = mdcString.substring(mdcString.indexOf("{{") + 2, mdcString.indexOf("}}"));
                        }
                        final StringTokenizer tok = new StringTokenizer(mdcString, ",");
                        while (tok.countTokens() > 1) {
                            mdc.put(tok.nextToken(), tok.nextToken());
                        }
                    }
                    throwable = new String[] { rs.getString("THROWABLE") };
                    className = rs.getString("CLASS");
                    methodName = rs.getString("METHOD");
                    fileName = rs.getString("FILE");
                    lineNumber = rs.getString("LINE");
                    String propertiesString = rs.getString("PROPERTIES");
                    properties = new Hashtable();
                    if (propertiesString != null) {
                        if (propertiesString.indexOf("{{") > -1 && propertiesString.indexOf("}}") > -1) {
                            propertiesString = propertiesString.substring(propertiesString.indexOf("{{") + 2, propertiesString.indexOf("}}"));
                        }
                        final StringTokenizer tok2 = new StringTokenizer(propertiesString, ",");
                        while (tok2.countTokens() > 1) {
                            final String tokenName = tok2.nextToken();
                            String value = tok2.nextToken();
                            if (tokenName.equals("log4jid")) {
                                try {
                                    final int thisInt = Integer.parseInt(value);
                                    value = String.valueOf(thisInt);
                                    if (thisInt > CustomSQLDBReceiver.this.lastID) {
                                        CustomSQLDBReceiver.this.lastID = thisInt;
                                    }
                                }
                                catch (Exception ex2) {}
                            }
                            properties.put(tokenName, value);
                        }
                    }
                    final Level levelImpl = Level.toLevel(level);
                    final LocationInfo locationInfo = new LocationInfo(fileName, className, methodName, lineNumber);
                    final ThrowableInformation throwableInfo = new ThrowableInformation(throwable);
                    properties.putAll(mdc);
                    final LoggingEvent event = new LoggingEvent(eventLogger.getName(), eventLogger, timeStamp, levelImpl, message, threadName, throwableInfo, ndc, locationInfo, properties);
                    CustomSQLDBReceiver.this.doPost(event);
                }
                if (CustomSQLDBReceiver.this.lastID != oldLastID) {
                    ComponentBase.this.getLogger().debug("lastID: " + CustomSQLDBReceiver.this.lastID);
                    oldLastID = CustomSQLDBReceiver.this.lastID;
                }
                statement.close();
                statement = null;
            }
            catch (SQLException sqle) {
                ComponentBase.this.getLogger().error("*************Problem receiving events", sqle);
            }
            finally {
                CustomSQLDBReceiver.this.closeConnection();
            }
            synchronized (this) {
                while (CustomSQLDBReceiver.this.isPaused()) {
                    try {
                        this.wait(1000L);
                    }
                    catch (InterruptedException ie) {}
                }
            }
        }
    }
}
