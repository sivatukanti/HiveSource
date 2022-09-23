// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.Logger;
import org.apache.log4j.component.ULogger;
import org.apache.log4j.spi.LoggerRepository;

public class ComponentBase implements Component
{
    private static final int ERROR_COUNT_LIMIT = 3;
    protected LoggerRepository repository;
    private ULogger logger;
    private int errorCount;
    
    protected ComponentBase() {
        this.errorCount = 0;
    }
    
    protected void resetErrorCount() {
        this.errorCount = 0;
    }
    
    public void setLoggerRepository(final LoggerRepository repository) {
        if (this.repository == null) {
            this.repository = repository;
        }
        else if (this.repository != repository) {
            throw new IllegalStateException("Repository has been already set");
        }
    }
    
    protected LoggerRepository getLoggerRepository() {
        return this.repository;
    }
    
    protected ULogger getLogger() {
        if (this.logger == null) {
            if (this.repository != null) {
                final Logger l = this.repository.getLogger(this.getClass().getName());
                if (l instanceof ULogger) {
                    this.logger = (ULogger)l;
                }
                else {
                    this.logger = new Log4JULogger(l);
                }
            }
            else {
                this.logger = SimpleULogger.getLogger(this.getClass().getName());
            }
        }
        return this.logger;
    }
    
    protected ULogger getNonFloodingLogger() {
        if (this.errorCount++ >= 3) {
            return NOPULogger.NOP_LOGGER;
        }
        return this.getLogger();
    }
}
