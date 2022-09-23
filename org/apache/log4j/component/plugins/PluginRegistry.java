// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.plugins;

import java.util.Iterator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.component.spi.LoggerRepositoryEventListener;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.component.spi.LoggerRepositoryEx;
import java.util.Map;

public final class PluginRegistry
{
    private final Map pluginMap;
    private final LoggerRepositoryEx loggerRepository;
    private final RepositoryListener listener;
    private final List listenerList;
    
    public PluginRegistry(final LoggerRepositoryEx repository) {
        this.listener = new RepositoryListener();
        this.listenerList = Collections.synchronizedList(new ArrayList<Object>());
        this.pluginMap = new HashMap();
        (this.loggerRepository = repository).addLoggerRepositoryEventListener(this.listener);
    }
    
    public LoggerRepositoryEx getLoggerRepository() {
        return this.loggerRepository;
    }
    
    public boolean pluginNameExists(final String name) {
        synchronized (this.pluginMap) {
            return this.pluginMap.containsKey(name);
        }
    }
    
    public void addPlugin(final Plugin plugin) {
        synchronized (this.pluginMap) {
            final String name = plugin.getName();
            plugin.setLoggerRepository(this.getLoggerRepository());
            final Plugin existingPlugin = this.pluginMap.get(name);
            if (existingPlugin != null) {
                existingPlugin.shutdown();
            }
            this.pluginMap.put(name, plugin);
            this.firePluginStarted(plugin);
        }
    }
    
    private void firePluginStarted(final Plugin plugin) {
        PluginEvent e = null;
        synchronized (this.listenerList) {
            for (final PluginListener l : this.listenerList) {
                if (e == null) {
                    e = new PluginEvent(plugin);
                }
                l.pluginStarted(e);
            }
        }
    }
    
    private void firePluginStopped(final Plugin plugin) {
        PluginEvent e = null;
        synchronized (this.listenerList) {
            for (final PluginListener l : this.listenerList) {
                if (e == null) {
                    e = new PluginEvent(plugin);
                }
                l.pluginStopped(e);
            }
        }
    }
    
    public List getPlugins() {
        synchronized (this.pluginMap) {
            final List pluginList = new ArrayList(this.pluginMap.size());
            final Iterator iter = this.pluginMap.values().iterator();
            while (iter.hasNext()) {
                pluginList.add(iter.next());
            }
            return pluginList;
        }
    }
    
    public List getPlugins(final Class pluginClass) {
        synchronized (this.pluginMap) {
            final List pluginList = new ArrayList(this.pluginMap.size());
            for (final Object plugin : this.pluginMap.values()) {
                if (pluginClass.isInstance(plugin)) {
                    pluginList.add(plugin);
                }
            }
            return pluginList;
        }
    }
    
    public Plugin stopPlugin(final String pluginName) {
        synchronized (this.pluginMap) {
            final Plugin plugin = this.pluginMap.get(pluginName);
            if (plugin == null) {
                return null;
            }
            plugin.shutdown();
            this.pluginMap.remove(pluginName);
            this.firePluginStopped(plugin);
            return plugin;
        }
    }
    
    public void stopAllPlugins() {
        synchronized (this.pluginMap) {
            this.loggerRepository.removeLoggerRepositoryEventListener(this.listener);
            for (final Plugin plugin : this.pluginMap.values()) {
                plugin.shutdown();
                this.firePluginStopped(plugin);
            }
        }
    }
    
    public void addPluginListener(final PluginListener l) {
        this.listenerList.add(l);
    }
    
    public void removePluginListener(final PluginListener l) {
        this.listenerList.remove(l);
    }
    
    private class RepositoryListener implements LoggerRepositoryEventListener
    {
        public void configurationResetEvent(final LoggerRepository repository) {
            PluginRegistry.this.stopAllPlugins();
        }
        
        public void configurationChangedEvent(final LoggerRepository repository) {
        }
        
        public void shutdownEvent(final LoggerRepository repository) {
            PluginRegistry.this.stopAllPlugins();
        }
    }
}
