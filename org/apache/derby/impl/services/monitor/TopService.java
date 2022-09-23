// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.error.StandardException;
import java.util.Locale;
import org.apache.derby.iapi.services.monitor.PersistentService;
import java.util.Vector;
import java.util.Hashtable;

final class TopService
{
    ProtocolKey key;
    ModuleInstance topModule;
    Hashtable protocolTable;
    Vector moduleInstances;
    BaseMonitor monitor;
    boolean inShutdown;
    PersistentService serviceType;
    Locale serviceLocale;
    
    TopService(final BaseMonitor monitor) {
        this.monitor = monitor;
        this.protocolTable = new Hashtable();
        this.moduleInstances = new Vector(0, 5);
    }
    
    TopService(final BaseMonitor baseMonitor, final ProtocolKey key, final PersistentService serviceType, final Locale serviceLocale) {
        this(baseMonitor);
        this.key = key;
        this.serviceType = serviceType;
        this.serviceLocale = serviceLocale;
    }
    
    void setTopModule(final Object o) {
        synchronized (this) {
            final ModuleInstance moduleInstance = this.findModuleInstance(o);
            if (moduleInstance != null) {
                this.topModule = moduleInstance;
                this.notifyAll();
            }
            if (this.getServiceType() != null) {
                this.addToProtocol(new ProtocolKey(this.key.getFactoryInterface(), this.monitor.getServiceName(o)), this.topModule);
            }
        }
    }
    
    Object getService() {
        return this.topModule.getInstance();
    }
    
    boolean isPotentialService(final ProtocolKey protocolKey) {
        String s;
        if (this.serviceType == null) {
            s = protocolKey.getIdentifier();
        }
        else {
            try {
                s = this.serviceType.getCanonicalServiceName(protocolKey.getIdentifier());
            }
            catch (StandardException ex) {
                return false;
            }
            if (s == null) {
                return false;
            }
        }
        if (this.topModule != null) {
            return this.topModule.isTypeAndName(this.serviceType, this.key.getFactoryInterface(), s);
        }
        return protocolKey.getFactoryInterface().isAssignableFrom(this.key.getFactoryInterface()) && this.serviceType.isSameService(this.key.getIdentifier(), s);
    }
    
    boolean isActiveService() {
        synchronized (this) {
            return this.topModule != null;
        }
    }
    
    boolean isActiveService(final ProtocolKey protocolKey) {
        synchronized (this) {
            if (this.inShutdown) {
                return false;
            }
            if (!this.isPotentialService(protocolKey)) {
                return false;
            }
            if (this.topModule != null) {
                return true;
            }
            while (!this.inShutdown && this.topModule == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
            return !this.inShutdown;
        }
    }
    
    synchronized Object findModule(final ProtocolKey key, final boolean b, final Properties properties) {
        final ModuleInstance moduleInstance = this.protocolTable.get(key);
        if (moduleInstance == null) {
            return null;
        }
        final Object instance = moduleInstance.getInstance();
        if (b || BaseMonitor.canSupport(instance, properties)) {
            return instance;
        }
        return null;
    }
    
    private ModuleInstance findModuleInstance(final Object o) {
        synchronized (this.moduleInstances) {
            for (int i = 0; i < this.moduleInstances.size(); ++i) {
                final ModuleInstance moduleInstance = this.moduleInstances.get(i);
                if (moduleInstance.getInstance() == o) {
                    return moduleInstance;
                }
            }
        }
        return null;
    }
    
    Object bootModule(final boolean b, final Object o, final ProtocolKey protocolKey, final Properties properties) throws StandardException {
        synchronized (this) {
            if (this.inShutdown) {
                throw StandardException.newException("08006.D", this.getKey().getIdentifier());
            }
        }
        final Object module = this.findModule(protocolKey, false, properties);
        if (module != null) {
            return module;
        }
        if (this.monitor.reportOn) {
            this.monitor.report("Booting Module   " + protocolKey.toString() + " create = " + b);
        }
        synchronized (this) {
            int index = 0;
            while (true) {
                final ModuleInstance moduleInstance;
                synchronized (this.moduleInstances) {
                    if (index >= this.moduleInstances.size()) {
                        break;
                    }
                    moduleInstance = this.moduleInstances.get(index);
                }
                if (moduleInstance.isBooted()) {
                    if (moduleInstance.isTypeAndName(null, protocolKey.getFactoryInterface(), protocolKey.getIdentifier())) {
                        final Object instance = moduleInstance.getInstance();
                        if (BaseMonitor.canSupport(instance, properties)) {
                            if (this.addToProtocol(protocolKey, moduleInstance)) {
                                if (this.monitor.reportOn) {
                                    this.monitor.report("Started Module   " + protocolKey.toString());
                                    this.monitor.report("  Implementation " + instance.getClass().getName());
                                }
                                return instance;
                            }
                        }
                    }
                }
                ++index;
                continue;
                break;
            }
        }
        final Object loadInstance = this.monitor.loadInstance(protocolKey.getFactoryInterface(), properties);
        if (loadInstance == null) {
            throw Monitor.missingImplementation(protocolKey.getFactoryInterface().getName());
        }
        final ModuleInstance o2 = new ModuleInstance(loadInstance, protocolKey.getIdentifier(), o, (this.topModule == null) ? null : this.topModule.getInstance());
        this.moduleInstances.add(o2);
        try {
            BaseMonitor.boot(loadInstance, b, properties);
        }
        catch (StandardException ex) {
            this.moduleInstances.remove(o2);
            throw ex;
        }
        o2.setBooted();
        synchronized (this) {
            if (this.addToProtocol(protocolKey, o2)) {
                if (this.monitor.reportOn) {
                    this.monitor.report("Started Module   " + protocolKey.toString());
                    this.monitor.report("  Implementation " + o2.getInstance().getClass().getName());
                }
                return o2.getInstance();
            }
        }
        stop(loadInstance);
        this.moduleInstances.remove(o2);
        return this.findModule(protocolKey, true, properties);
    }
    
    boolean shutdown() {
        Object o = this;
        synchronized (this) {
            if (this.inShutdown) {
                return false;
            }
            this.inShutdown = true;
            this.notifyAll();
        }
        while (true) {
            synchronized (this) {
                if (this.moduleInstances.isEmpty()) {
                    return true;
                }
                o = this.moduleInstances.get(0);
            }
            stop(((ModuleInstance)o).getInstance());
            synchronized (this) {
                this.moduleInstances.remove(0);
            }
        }
    }
    
    private boolean addToProtocol(final ProtocolKey protocolKey, final ModuleInstance value) {
        value.getIdentifier();
        synchronized (this) {
            final ModuleInstance value2 = this.protocolTable.get(protocolKey);
            if (value2 == null) {
                this.protocolTable.put(protocolKey, value);
                return true;
            }
            return value2 == value;
        }
    }
    
    boolean inService(final Object o) {
        return this.findModuleInstance(o) != null;
    }
    
    public ProtocolKey getKey() {
        return this.key;
    }
    
    PersistentService getServiceType() {
        return this.serviceType;
    }
    
    private static void stop(final Object o) {
        if (o instanceof ModuleControl) {
            ((ModuleControl)o).stop();
        }
    }
}
