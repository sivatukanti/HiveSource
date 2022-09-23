// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.plugins;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.log4j.spi.LoggerRepository;
import java.beans.PropertyChangeSupport;
import org.apache.log4j.component.spi.ComponentBase;

public abstract class PluginSkeleton extends ComponentBase implements Plugin
{
    protected String name;
    protected boolean active;
    private PropertyChangeSupport propertySupport;
    
    protected PluginSkeleton() {
        this.name = "plugin";
        this.propertySupport = new PropertyChangeSupport(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String newName) {
        final String oldName = this.name;
        this.name = newName;
        this.propertySupport.firePropertyChange("name", oldName, this.name);
    }
    
    public LoggerRepository getLoggerRepository() {
        return this.repository;
    }
    
    public void setLoggerRepository(final LoggerRepository repository) {
        final Object oldValue = this.repository;
        this.firePropertyChange("loggerRepository", oldValue, this.repository = repository);
    }
    
    public synchronized boolean isActive() {
        return this.active;
    }
    
    public boolean isEquivalent(final Plugin testPlugin) {
        return this.repository == testPlugin.getLoggerRepository() && ((this.name == null && testPlugin.getName() == null) || (this.name != null && this.name.equals(testPlugin.getName()))) && this.getClass().equals(testPlugin.getClass());
    }
    
    public final void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertySupport.addPropertyChangeListener(listener);
    }
    
    public final void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        this.propertySupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public final void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertySupport.removePropertyChangeListener(listener);
    }
    
    public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        this.propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    protected final void firePropertyChange(final PropertyChangeEvent evt) {
        this.propertySupport.firePropertyChange(evt);
    }
    
    protected final void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {
        this.propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    protected final void firePropertyChange(final String propertyName, final int oldValue, final int newValue) {
        this.propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    protected final void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        this.propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
