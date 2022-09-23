// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class AbstractRule implements Rule, Serializable
{
    static final long serialVersionUID = -2844288145563025172L;
    private PropertyChangeSupport propertySupport;
    
    public AbstractRule() {
        this.propertySupport = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.propertySupport.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.propertySupport.removePropertyChangeListener(l);
    }
    
    protected void firePropertyChange(final String propertyName, final Object oldVal, final Object newVal) {
        this.propertySupport.firePropertyChange(propertyName, oldVal, newVal);
    }
    
    public void firePropertyChange(final PropertyChangeEvent evt) {
        this.propertySupport.firePropertyChange(evt);
    }
}
