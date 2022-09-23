// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.Initializable;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.Iterator;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.event.EventListenerRegistrationData;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.beanutils.ConstructorArg;
import java.util.Collection;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import java.util.Collections;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.event.EventType;
import java.util.HashMap;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import java.util.Map;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.ImmutableConfiguration;

public class BasicConfigurationBuilder<T extends ImmutableConfiguration> implements ConfigurationBuilder<T>
{
    private final Class<? extends T> resultClass;
    private final EventListenerList eventListeners;
    private final boolean allowFailOnInit;
    private Map<String, Object> parameters;
    private BeanDeclaration resultDeclaration;
    private volatile T result;
    
    public BasicConfigurationBuilder(final Class<? extends T> resCls) {
        this(resCls, null);
    }
    
    public BasicConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params) {
        this(resCls, params, false);
    }
    
    public BasicConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params, final boolean allowFailOnInit) {
        if (resCls == null) {
            throw new IllegalArgumentException("Result class must not be null!");
        }
        this.resultClass = resCls;
        this.allowFailOnInit = allowFailOnInit;
        this.eventListeners = new EventListenerList();
        this.updateParameters(params);
    }
    
    public Class<? extends T> getResultClass() {
        return this.resultClass;
    }
    
    public boolean isAllowFailOnInit() {
        return this.allowFailOnInit;
    }
    
    public synchronized BasicConfigurationBuilder<T> setParameters(final Map<String, Object> params) {
        this.updateParameters(params);
        return this;
    }
    
    public synchronized BasicConfigurationBuilder<T> addParameters(final Map<String, Object> params) {
        final Map<String, Object> newParams = new HashMap<String, Object>(this.getParameters());
        if (params != null) {
            newParams.putAll(params);
        }
        this.updateParameters(newParams);
        return this;
    }
    
    public BasicConfigurationBuilder<T> configure(final BuilderParameters... params) {
        final Map<String, Object> newParams = new HashMap<String, Object>();
        for (final BuilderParameters p : params) {
            newParams.putAll(p.getParameters());
            this.handleEventListenerProviders(p);
        }
        return this.setParameters(newParams);
    }
    
    @Override
    public T getConfiguration() throws ConfigurationException {
        this.fireBuilderEvent(new ConfigurationBuilderEvent(this, ConfigurationBuilderEvent.CONFIGURATION_REQUEST));
        T resObj = this.result;
        boolean created = false;
        if (resObj == null) {
            synchronized (this) {
                resObj = this.result;
                if (resObj == null) {
                    resObj = (this.result = this.createResult());
                    created = true;
                }
            }
        }
        if (created) {
            this.fireBuilderEvent(new ConfigurationBuilderResultCreatedEvent(this, ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, resObj));
        }
        return resObj;
    }
    
    @Override
    public <E extends Event> void addEventListener(final EventType<E> eventType, final EventListener<? super E> listener) {
        this.installEventListener((EventType<Event>)eventType, (EventListener<? super Event>)listener);
    }
    
    @Override
    public <E extends Event> boolean removeEventListener(final EventType<E> eventType, final EventListener<? super E> listener) {
        this.fetchEventSource().removeEventListener(eventType, listener);
        return this.eventListeners.removeEventListener(eventType, listener);
    }
    
    public void resetResult() {
        final T oldResult;
        synchronized (this) {
            oldResult = this.result;
            this.result = null;
            this.resultDeclaration = null;
        }
        if (oldResult != null) {
            this.removeEventListeners(oldResult);
        }
        this.fireBuilderEvent(new ConfigurationBuilderEvent(this, ConfigurationBuilderEvent.RESET));
    }
    
    public void resetParameters() {
        this.setParameters(null);
    }
    
    public synchronized void reset() {
        this.resetParameters();
        this.resetResult();
    }
    
    public final void connectToReloadingController(final ReloadingController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("ReloadingController must not be null!");
        }
        ReloadingBuilderSupportListener.connect(this, controller);
    }
    
    protected T createResult() throws ConfigurationException {
        final T resObj = this.createResultInstance();
        try {
            this.initResultInstance(resObj);
        }
        catch (ConfigurationException cex) {
            if (!this.isAllowFailOnInit()) {
                throw cex;
            }
        }
        return resObj;
    }
    
    protected T createResultInstance() throws ConfigurationException {
        final Object bean = this.fetchBeanHelper().createBean(this.getResultDeclaration());
        this.checkResultInstance(bean);
        return (T)this.getResultClass().cast(bean);
    }
    
    protected void initResultInstance(final T obj) throws ConfigurationException {
        this.fetchBeanHelper().initBean(obj, this.getResultDeclaration());
        this.registerEventListeners(obj);
        this.handleInitializable(obj);
    }
    
    protected final synchronized BeanDeclaration getResultDeclaration() throws ConfigurationException {
        if (this.resultDeclaration == null) {
            this.resultDeclaration = this.createResultDeclaration(this.getFilteredParameters());
        }
        return this.resultDeclaration;
    }
    
    protected final synchronized Map<String, Object> getParameters() {
        if (this.parameters != null) {
            return this.parameters;
        }
        return Collections.emptyMap();
    }
    
    protected final BeanHelper fetchBeanHelper() {
        final BeanHelper helper = BasicBuilderParameters.fetchBeanHelper(this.getParameters());
        return (helper != null) ? helper : BeanHelper.INSTANCE;
    }
    
    protected BeanDeclaration createResultDeclaration(final Map<String, Object> params) throws ConfigurationException {
        return new BeanDeclaration() {
            @Override
            public Map<String, Object> getNestedBeanDeclarations() {
                return Collections.emptyMap();
            }
            
            @Override
            public Collection<ConstructorArg> getConstructorArgs() {
                return (Collection<ConstructorArg>)Collections.emptySet();
            }
            
            @Override
            public Map<String, Object> getBeanProperties() {
                return params;
            }
            
            @Override
            public Object getBeanFactoryParameter() {
                return null;
            }
            
            @Override
            public String getBeanFactoryName() {
                return null;
            }
            
            @Override
            public String getBeanClassName() {
                return BasicConfigurationBuilder.this.getResultClass().getName();
            }
        };
    }
    
    protected synchronized void copyEventListeners(final BasicConfigurationBuilder<?> target) {
        this.copyEventListeners(target, this.eventListeners);
    }
    
    protected void copyEventListeners(final BasicConfigurationBuilder<?> target, final EventListenerList listeners) {
        target.eventListeners.addAll(listeners);
    }
    
    protected final <E extends Event> void installEventListener(final EventType<E> eventType, final EventListener<? super E> listener) {
        this.fetchEventSource().addEventListener(eventType, listener);
        this.eventListeners.addEventListener(eventType, listener);
    }
    
    protected void fireBuilderEvent(final ConfigurationBuilderEvent event) {
        this.eventListeners.fire(event);
    }
    
    private void updateParameters(final Map<String, Object> newParams) {
        final Map<String, Object> map = new HashMap<String, Object>();
        if (newParams != null) {
            map.putAll(newParams);
        }
        this.parameters = Collections.unmodifiableMap((Map<? extends String, ?>)map);
    }
    
    private void registerEventListeners(final T obj) {
        final EventSource evSrc = ConfigurationUtils.asEventSource(obj, true);
        for (final EventListenerRegistrationData<?> regData : this.eventListeners.getRegistrations()) {
            registerListener(evSrc, regData);
        }
    }
    
    private void removeEventListeners(final T obj) {
        final EventSource evSrc = ConfigurationUtils.asEventSource(obj, true);
        for (final EventListenerRegistrationData<?> regData : this.eventListeners.getRegistrations()) {
            removeListener(evSrc, regData);
        }
    }
    
    private EventSource fetchEventSource() {
        return ConfigurationUtils.asEventSource(this.result, true);
    }
    
    private void handleEventListenerProviders(final BuilderParameters params) {
        if (params instanceof EventListenerProvider) {
            this.eventListeners.addAll(((EventListenerProvider)params).getListeners());
        }
    }
    
    private void checkResultInstance(final Object inst) {
        if (!this.getResultClass().isInstance(inst)) {
            throw new ConfigurationRuntimeException("Incompatible result object: " + inst);
        }
    }
    
    private Map<String, Object> getFilteredParameters() {
        final Map<String, Object> filteredMap = new HashMap<String, Object>(this.getParameters());
        final Iterator<String> it = filteredMap.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            if (key.startsWith("config-")) {
                it.remove();
            }
        }
        return filteredMap;
    }
    
    private void handleInitializable(final T obj) {
        if (obj instanceof Initializable) {
            ((Initializable)obj).initialize();
        }
    }
    
    private static <E extends Event> void registerListener(final EventSource evSrc, final EventListenerRegistrationData<E> regData) {
        evSrc.addEventListener(regData.getEventType(), regData.getListener());
    }
    
    private static <E extends Event> void removeListener(final EventSource evSrc, final EventListenerRegistrationData<E> regData) {
        evSrc.removeEventListener(regData.getEventType(), regData.getListener());
    }
}
