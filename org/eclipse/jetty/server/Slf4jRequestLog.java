// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("NCSA standard format request log to slf4j bridge")
public class Slf4jRequestLog extends AbstractNCSARequestLog
{
    private Slf4jLog logger;
    private String loggerName;
    
    public Slf4jRequestLog() {
        this.loggerName = "org.eclipse.jetty.server.RequestLog";
    }
    
    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }
    
    public String getLoggerName() {
        return this.loggerName;
    }
    
    @Override
    protected boolean isEnabled() {
        return this.logger != null;
    }
    
    @Override
    public void write(final String requestEntry) throws IOException {
        this.logger.info(requestEntry, new Object[0]);
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        this.logger = new Slf4jLog(this.loggerName);
        super.doStart();
    }
}
