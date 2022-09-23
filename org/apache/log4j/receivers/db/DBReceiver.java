// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.db;

import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import org.apache.log4j.component.scheduler.Scheduler;
import org.apache.log4j.component.scheduler.Job;
import org.apache.log4j.component.spi.LoggerRepositoryEx;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.component.plugins.Pauseable;
import org.apache.log4j.component.plugins.Receiver;

public class DBReceiver extends Receiver implements Pauseable, UnrecognizedElementHandler
{
    static int DEFAULT_REFRESH_MILLIS;
    ConnectionSource connectionSource;
    int refreshMillis;
    DBReceiverJob receiverJob;
    boolean paused;
    
    public DBReceiver() {
        this.refreshMillis = DBReceiver.DEFAULT_REFRESH_MILLIS;
        this.paused = false;
    }
    
    public void activateOptions() {
        if (this.connectionSource == null) {
            throw new IllegalStateException("DBAppender cannot function without a connection source");
        }
        (this.receiverJob = new DBReceiverJob(this)).setLoggerRepository(this.repository);
        if (this.repository == null) {
            throw new IllegalStateException("DBAppender cannot function without a reference to its owning repository");
        }
        if (this.repository instanceof LoggerRepositoryEx) {
            final Scheduler scheduler = ((LoggerRepositoryEx)this.repository).getScheduler();
            scheduler.schedule(this.receiverJob, System.currentTimeMillis() + 500L, this.refreshMillis);
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
    
    public void shutdown() {
        this.getLogger().info("removing receiverJob from the Scheduler.");
        if (this.repository instanceof LoggerRepositoryEx) {
            final Scheduler scheduler = ((LoggerRepositoryEx)this.repository).getScheduler();
            scheduler.delete(this.receiverJob);
        }
    }
    
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
    
    public boolean isPaused() {
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
        DBReceiver.DEFAULT_REFRESH_MILLIS = 1000;
    }
}
