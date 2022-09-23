// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j;

import org.apache.log4j.component.plugins.Plugin;
import org.apache.log4j.xml.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.component.spi.ErrorItem;
import java.util.Enumeration;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.component.spi.LoggerEventListener;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.component.spi.LoggerRepositoryEventListener;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.component.scheduler.Scheduler;
import org.apache.log4j.component.plugins.PluginRegistry;
import java.util.Map;
import java.util.ArrayList;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.component.spi.LoggerRepositoryEx;

public final class LoggerRepositoryExImpl implements LoggerRepositoryEx, RendererSupport, UnrecognizedElementHandler
{
    private final LoggerRepository repo;
    private LoggerFactory loggerFactory;
    private final RendererSupport rendererSupport;
    private final ArrayList repositoryEventListeners;
    private final Map loggerEventListeners;
    private String name;
    private PluginRegistry pluginRegistry;
    private final Map properties;
    private Scheduler scheduler;
    private Map objectMap;
    private List errorList;
    private boolean pristine;
    
    public LoggerRepositoryExImpl(final LoggerRepository repository) {
        this.repositoryEventListeners = new ArrayList();
        this.loggerEventListeners = new HashMap();
        this.properties = new Hashtable();
        this.objectMap = new HashMap();
        this.errorList = new Vector();
        this.pristine = true;
        if (repository == null) {
            throw new NullPointerException("repository");
        }
        this.repo = repository;
        if (repository instanceof RendererSupport) {
            this.rendererSupport = (RendererSupport)repository;
        }
        else {
            this.rendererSupport = new RendererSupportImpl();
        }
    }
    
    public void addLoggerRepositoryEventListener(final LoggerRepositoryEventListener listener) {
        synchronized (this.repositoryEventListeners) {
            if (this.repositoryEventListeners.contains(listener)) {
                LogLog.warn("Ignoring attempt to add a previously registered LoggerRepositoryEventListener.");
            }
            else {
                this.repositoryEventListeners.add(listener);
            }
        }
    }
    
    public void removeLoggerRepositoryEventListener(final LoggerRepositoryEventListener listener) {
        synchronized (this.repositoryEventListeners) {
            if (!this.repositoryEventListeners.contains(listener)) {
                LogLog.warn("Ignoring attempt to remove a non-registered LoggerRepositoryEventListener.");
            }
            else {
                this.repositoryEventListeners.remove(listener);
            }
        }
    }
    
    public void addLoggerEventListener(final LoggerEventListener listener) {
        synchronized (this.loggerEventListeners) {
            if (this.loggerEventListeners.get(listener) != null) {
                LogLog.warn("Ignoring attempt to add a previously registerd LoggerEventListener.");
            }
            else {
                final HierarchyEventListenerProxy proxy = new HierarchyEventListenerProxy(listener);
                this.loggerEventListeners.put(listener, proxy);
                this.repo.addHierarchyEventListener(proxy);
            }
        }
    }
    
    public void addHierarchyEventListener(final HierarchyEventListener listener) {
        this.repo.addHierarchyEventListener(listener);
    }
    
    public void removeLoggerEventListener(final LoggerEventListener listener) {
        synchronized (this.loggerEventListeners) {
            final HierarchyEventListenerProxy proxy = this.loggerEventListeners.get(listener);
            if (proxy == null) {
                LogLog.warn("Ignoring attempt to remove a non-registered LoggerEventListener.");
            }
            else {
                this.loggerEventListeners.remove(listener);
                proxy.disable();
            }
        }
    }
    
    public void emitNoAppenderWarning(final Category cat) {
        this.repo.emitNoAppenderWarning(cat);
    }
    
    public Logger exists(final String loggerName) {
        return this.repo.exists(loggerName);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String repoName) {
        if (this.name == null) {
            this.name = repoName;
        }
        else if (!this.name.equals(repoName)) {
            throw new IllegalStateException("Repository [" + this.name + "] cannot be renamed as [" + repoName + "].");
        }
    }
    
    public Map getProperties() {
        return this.properties;
    }
    
    public String getProperty(final String key) {
        return this.properties.get(key);
    }
    
    public void setProperty(final String key, final String value) {
        this.properties.put(key, value);
    }
    
    public void setThreshold(final String levelStr) {
        this.repo.setThreshold(levelStr);
    }
    
    public void setThreshold(final Level l) {
        this.repo.setThreshold(l);
    }
    
    public PluginRegistry getPluginRegistry() {
        if (this.pluginRegistry == null) {
            this.pluginRegistry = new PluginRegistry(this);
        }
        return this.pluginRegistry;
    }
    
    public void fireAddAppenderEvent(final Category logger, final Appender appender) {
        this.repo.fireAddAppenderEvent(logger, appender);
    }
    
    public void fireRemoveAppenderEvent(final Category logger, final Appender appender) {
        if (this.repo instanceof Hierarchy) {
            ((Hierarchy)this.repo).fireRemoveAppenderEvent(logger, appender);
        }
    }
    
    public void fireLevelChangedEvent(final Logger logger) {
    }
    
    public void fireConfigurationChangedEvent() {
    }
    
    public Level getThreshold() {
        return this.repo.getThreshold();
    }
    
    public Logger getLogger(final String loggerName) {
        return this.repo.getLogger(loggerName);
    }
    
    public Logger getLogger(final String loggerName, final LoggerFactory factory) {
        return this.repo.getLogger(loggerName, factory);
    }
    
    public Enumeration getCurrentLoggers() {
        return this.repo.getCurrentLoggers();
    }
    
    public List getErrorList() {
        return this.errorList;
    }
    
    public void addErrorItem(final ErrorItem errorItem) {
        this.getErrorList().add(errorItem);
    }
    
    public Enumeration getCurrentCategories() {
        return this.repo.getCurrentCategories();
    }
    
    public RendererMap getRendererMap() {
        return this.rendererSupport.getRendererMap();
    }
    
    public Logger getRootLogger() {
        return this.repo.getRootLogger();
    }
    
    public boolean isDisabled(final int level) {
        return this.repo.isDisabled(level);
    }
    
    public void resetConfiguration() {
        this.repo.resetConfiguration();
    }
    
    public void setRenderer(final Class renderedClass, final ObjectRenderer renderer) {
        this.rendererSupport.setRenderer(renderedClass, renderer);
    }
    
    public boolean isPristine() {
        return this.pristine;
    }
    
    public void setPristine(final boolean state) {
        this.pristine = state;
    }
    
    public void shutdown() {
        this.repo.shutdown();
    }
    
    public Scheduler getScheduler() {
        if (this.scheduler == null) {
            (this.scheduler = new Scheduler()).setDaemon(true);
            this.scheduler.start();
        }
        return this.scheduler;
    }
    
    public void putObject(final String key, final Object value) {
        this.objectMap.put(key, value);
    }
    
    public Object getObject(final String key) {
        return this.objectMap.get(key);
    }
    
    public void setLoggerFactory(final LoggerFactory factory) {
        if (factory == null) {
            throw new NullPointerException();
        }
        this.loggerFactory = factory;
    }
    
    public LoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        if ("plugin".equals(element.getNodeName())) {
            final Object instance = DOMConfigurator.parseElement(element, props, Plugin.class);
            if (instance instanceof Plugin) {
                final Plugin plugin = (Plugin)instance;
                final String pluginName = DOMConfigurator.subst(element.getAttribute("name"), props);
                if (pluginName.length() > 0) {
                    plugin.setName(pluginName);
                }
                this.getPluginRegistry().addPlugin(plugin);
                plugin.setLoggerRepository(this);
                LogLog.debug("Pushing plugin on to the object stack.");
                plugin.activateOptions();
                return true;
            }
        }
        return false;
    }
    
    private static final class RendererSupportImpl implements RendererSupport
    {
        private final RendererMap renderers;
        
        public RendererSupportImpl() {
            this.renderers = new RendererMap();
        }
        
        public RendererMap getRendererMap() {
            return this.renderers;
        }
        
        public void setRenderer(final Class renderedClass, final ObjectRenderer renderer) {
            this.renderers.put(renderedClass, renderer);
        }
    }
    
    private static final class HierarchyEventListenerProxy implements HierarchyEventListener
    {
        private LoggerEventListener listener;
        
        public HierarchyEventListenerProxy(final LoggerEventListener l) {
            if (l == null) {
                throw new NullPointerException("l");
            }
            this.listener = l;
        }
        
        public void addAppenderEvent(final Category cat, final Appender appender) {
            if (this.isEnabled() && cat instanceof Logger) {
                this.listener.appenderAddedEvent((Logger)cat, appender);
            }
        }
        
        public void removeAppenderEvent(final Category cat, final Appender appender) {
            if (this.isEnabled() && cat instanceof Logger) {
                this.listener.appenderRemovedEvent((Logger)cat, appender);
            }
        }
        
        public synchronized void disable() {
            this.listener = null;
        }
        
        private synchronized boolean isEnabled() {
            return this.listener != null;
        }
    }
}
