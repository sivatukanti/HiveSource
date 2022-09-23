// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import org.apache.derby.iapi.services.monitor.PersistentService;

class ModuleInstance
{
    protected Object instance;
    protected String identifier;
    protected Object topLevelService;
    protected Object service;
    private boolean booted;
    
    protected ModuleInstance(final Object instance, final String identifier, final Object service, final Object topLevelService) {
        this.instance = instance;
        this.identifier = identifier;
        this.topLevelService = topLevelService;
        this.service = service;
    }
    
    protected ModuleInstance(final Object o) {
        this(o, null, null, null);
    }
    
    protected boolean isTypeAndName(final PersistentService persistentService, final Class clazz, final String s) {
        if (!clazz.isInstance(this.instance)) {
            return false;
        }
        if (persistentService != null && s != null) {
            return persistentService.isSameService(this.identifier, s);
        }
        if (s != null) {
            if (this.identifier == null) {
                return false;
            }
            if (!s.equals(this.identifier)) {
                return false;
            }
        }
        else if (this.identifier != null) {
            return false;
        }
        return true;
    }
    
    protected String getIdentifier() {
        return this.identifier;
    }
    
    protected Object getTopLevelService() {
        return this.topLevelService;
    }
    
    protected Object getInstance() {
        return this.instance;
    }
    
    synchronized void setBooted() {
        this.booted = true;
    }
    
    synchronized boolean isBooted() {
        return this.booted;
    }
}
