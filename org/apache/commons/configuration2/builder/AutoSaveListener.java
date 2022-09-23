// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.io.FileHandlerListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.io.FileHandlerListenerAdapter;

class AutoSaveListener extends FileHandlerListenerAdapter implements EventListener<ConfigurationEvent>
{
    private final Log log;
    private final FileBasedConfigurationBuilder<?> builder;
    private FileHandler handler;
    private int loading;
    
    public AutoSaveListener(final FileBasedConfigurationBuilder<?> bldr) {
        this.log = LogFactory.getLog(this.getClass());
        this.builder = bldr;
    }
    
    @Override
    public void onEvent(final ConfigurationEvent event) {
        if (this.autoSaveRequired(event)) {
            try {
                this.builder.save();
            }
            catch (ConfigurationException ce) {
                this.log.warn("Auto save failed!", ce);
            }
        }
    }
    
    @Override
    public synchronized void loading(final FileHandler handler) {
        ++this.loading;
    }
    
    @Override
    public synchronized void loaded(final FileHandler handler) {
        --this.loading;
    }
    
    public synchronized void updateFileHandler(final FileHandler fh) {
        if (this.handler != null) {
            this.handler.removeFileHandlerListener(this);
        }
        if (fh != null) {
            fh.addFileHandlerListener(this);
        }
        this.handler = fh;
    }
    
    private synchronized boolean inLoadOperation() {
        return this.loading > 0;
    }
    
    private boolean autoSaveRequired(final ConfigurationEvent event) {
        return !event.isBeforeUpdate() && !this.inLoadOperation();
    }
}
