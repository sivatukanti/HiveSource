// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn;

import java.util.Iterator;
import java.io.File;
import java.util.LinkedList;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Queue;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Flushable;
import org.apache.log4j.FileAppender;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ContainerLogAppender extends FileAppender implements Flushable
{
    private String containerLogDir;
    private int maxEvents;
    private Queue<LoggingEvent> tail;
    private static final int EVENT_SIZE = 100;
    
    public ContainerLogAppender() {
        this.tail = null;
    }
    
    @Override
    public void activateOptions() {
        synchronized (this) {
            if (this.maxEvents > 0) {
                this.tail = new LinkedList<LoggingEvent>();
            }
            this.setFile(new File(this.containerLogDir, "syslog").toString());
            this.setAppend(true);
            super.activateOptions();
        }
    }
    
    @Override
    public void append(final LoggingEvent event) {
        synchronized (this) {
            if (this.tail == null) {
                super.append(event);
            }
            else {
                if (this.tail.size() >= this.maxEvents) {
                    this.tail.remove();
                }
                this.tail.add(event);
            }
        }
    }
    
    @Override
    public void flush() {
        if (this.qw != null) {
            this.qw.flush();
        }
    }
    
    @Override
    public synchronized void close() {
        if (this.tail != null) {
            for (final LoggingEvent event : this.tail) {
                super.append(event);
            }
        }
        super.close();
    }
    
    public String getContainerLogDir() {
        return this.containerLogDir;
    }
    
    public void setContainerLogDir(final String containerLogDir) {
        this.containerLogDir = containerLogDir;
    }
    
    public long getTotalLogFileSize() {
        return this.maxEvents * 100;
    }
    
    public void setTotalLogFileSize(final long logSize) {
        this.maxEvents = (int)logSize / 100;
    }
}
