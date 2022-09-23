// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PassThroughException;
import org.apache.derby.io.StorageFactory;
import org.apache.derby.io.WritableStorageFactory;
import org.apache.derby.iapi.services.monitor.PersistentService;
import java.util.Properties;

public class UpdateServiceProperties extends Properties
{
    private PersistentService serviceType;
    private String serviceName;
    private volatile WritableStorageFactory storageFactory;
    private boolean serviceBooted;
    
    public UpdateServiceProperties(final PersistentService serviceType, final String serviceName, final Properties defaults, final boolean serviceBooted) {
        super(defaults);
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.serviceBooted = serviceBooted;
    }
    
    public void setServiceBooted() {
        this.serviceBooted = true;
    }
    
    public void setStorageFactory(final WritableStorageFactory storageFactory) {
        this.storageFactory = storageFactory;
    }
    
    public WritableStorageFactory getStorageFactory() {
        return this.storageFactory;
    }
    
    public Object put(final Object key, final Object value) {
        final Object put = this.defaults.put(key, value);
        if (!((String)key).startsWith("derby.__rt.")) {
            this.update();
        }
        return put;
    }
    
    public Object remove(final Object key) {
        final Object remove = this.defaults.remove(key);
        if (remove != null && !((String)key).startsWith("derby.__rt.")) {
            this.update();
        }
        return remove;
    }
    
    public void saveServiceProperties() {
        try {
            this.serviceType.saveServiceProperties(this.serviceName, this.storageFactory, BaseMonitor.removeRuntimeProperties(this.defaults), false);
        }
        catch (StandardException ex) {
            throw new PassThroughException(ex);
        }
    }
    
    private void update() {
        try {
            if (this.serviceBooted) {
                this.serviceType.saveServiceProperties(this.serviceName, this.storageFactory, BaseMonitor.removeRuntimeProperties(this.defaults), true);
            }
        }
        catch (StandardException ex) {
            throw new PassThroughException(ex);
        }
    }
}
