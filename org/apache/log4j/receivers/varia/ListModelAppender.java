// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.varia;

import org.apache.log4j.spi.LoggingEvent;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import org.apache.log4j.AppenderSkeleton;

public final class ListModelAppender extends AppenderSkeleton
{
    private final DefaultListModel model;
    
    public ListModelAppender() {
        super(true);
        this.model = new DefaultListModel();
    }
    
    public ListModel getModel() {
        return this.model;
    }
    
    protected void append(final LoggingEvent event) {
        this.model.addElement(event);
    }
    
    public void close() {
        this.clearModel();
    }
    
    public void clearModel() {
        this.model.clear();
    }
    
    public boolean requiresLayout() {
        return false;
    }
}
