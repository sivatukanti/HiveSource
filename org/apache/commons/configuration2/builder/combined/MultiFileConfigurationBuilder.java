// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.ConfigurationUtils;
import java.util.HashMap;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.ConfigurationBuilderResultCreatedEvent;
import org.apache.commons.configuration2.interpol.InterpolatorSpecification;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import java.util.Iterator;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.event.Event;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventListenerList;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.FileBasedConfiguration;

public class MultiFileConfigurationBuilder<T extends FileBasedConfiguration> extends BasicConfigurationBuilder<T>
{
    private static final String KEY_INTERPOLATOR = "interpolator";
    private final ConcurrentMap<String, FileBasedConfigurationBuilder<T>> managedBuilders;
    private final AtomicReference<ConfigurationInterpolator> interpolator;
    private final ThreadLocal<Boolean> inInterpolation;
    private final EventListenerList configurationListeners;
    private final EventListener<ConfigurationBuilderEvent> managedBuilderDelegationListener;
    
    public MultiFileConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params, final boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
        this.managedBuilders = new ConcurrentHashMap<String, FileBasedConfigurationBuilder<T>>();
        this.interpolator = new AtomicReference<ConfigurationInterpolator>();
        this.inInterpolation = new ThreadLocal<Boolean>();
        this.configurationListeners = new EventListenerList();
        this.managedBuilderDelegationListener = new EventListener<ConfigurationBuilderEvent>() {
            @Override
            public void onEvent(final ConfigurationBuilderEvent event) {
                MultiFileConfigurationBuilder.this.handleManagedBuilderEvent(event);
            }
        };
    }
    
    public MultiFileConfigurationBuilder(final Class<? extends T> resCls, final Map<String, Object> params) {
        super(resCls, params);
        this.managedBuilders = new ConcurrentHashMap<String, FileBasedConfigurationBuilder<T>>();
        this.interpolator = new AtomicReference<ConfigurationInterpolator>();
        this.inInterpolation = new ThreadLocal<Boolean>();
        this.configurationListeners = new EventListenerList();
        this.managedBuilderDelegationListener = new EventListener<ConfigurationBuilderEvent>() {
            @Override
            public void onEvent(final ConfigurationBuilderEvent event) {
                MultiFileConfigurationBuilder.this.handleManagedBuilderEvent(event);
            }
        };
    }
    
    public MultiFileConfigurationBuilder(final Class<? extends T> resCls) {
        super(resCls);
        this.managedBuilders = new ConcurrentHashMap<String, FileBasedConfigurationBuilder<T>>();
        this.interpolator = new AtomicReference<ConfigurationInterpolator>();
        this.inInterpolation = new ThreadLocal<Boolean>();
        this.configurationListeners = new EventListenerList();
        this.managedBuilderDelegationListener = new EventListener<ConfigurationBuilderEvent>() {
            @Override
            public void onEvent(final ConfigurationBuilderEvent event) {
                MultiFileConfigurationBuilder.this.handleManagedBuilderEvent(event);
            }
        };
    }
    
    @Override
    public MultiFileConfigurationBuilder<T> configure(final BuilderParameters... params) {
        super.configure(params);
        return this;
    }
    
    @Override
    public T getConfiguration() throws ConfigurationException {
        return this.getManagedBuilder().getConfiguration();
    }
    
    public FileBasedConfigurationBuilder<T> getManagedBuilder() throws ConfigurationException {
        final Map<String, Object> params = this.getParameters();
        final MultiFileBuilderParametersImpl multiParams = MultiFileBuilderParametersImpl.fromParameters(params, true);
        if (multiParams.getFilePattern() == null) {
            throw new ConfigurationException("No file name pattern is set!");
        }
        final String fileName = this.fetchFileName(multiParams);
        FileBasedConfigurationBuilder<T> builder = this.getManagedBuilders().get(fileName);
        if (builder == null) {
            builder = this.createInitializedManagedBuilder(fileName, createManagedBuilderParameters(params, multiParams));
            final FileBasedConfigurationBuilder<T> newBuilder = ConcurrentUtils.putIfAbsent(this.getManagedBuilders(), fileName, builder);
            if (newBuilder == builder) {
                this.initListeners(newBuilder);
            }
            else {
                builder = newBuilder;
            }
        }
        return builder;
    }
    
    @Override
    public synchronized <E extends Event> void addEventListener(final EventType<E> eventType, final EventListener<? super E> l) {
        super.addEventListener(eventType, l);
        if (isEventTypeForManagedBuilders(eventType)) {
            for (final FileBasedConfigurationBuilder<T> b : this.getManagedBuilders().values()) {
                b.addEventListener(eventType, l);
            }
            this.configurationListeners.addEventListener(eventType, l);
        }
    }
    
    @Override
    public synchronized <E extends Event> boolean removeEventListener(final EventType<E> eventType, final EventListener<? super E> l) {
        final boolean result = super.removeEventListener(eventType, l);
        if (isEventTypeForManagedBuilders(eventType)) {
            for (final FileBasedConfigurationBuilder<T> b : this.getManagedBuilders().values()) {
                b.removeEventListener(eventType, l);
            }
            this.configurationListeners.removeEventListener(eventType, l);
        }
        return result;
    }
    
    @Override
    public synchronized void resetParameters() {
        for (final FileBasedConfigurationBuilder<T> b : this.getManagedBuilders().values()) {
            b.removeEventListener(ConfigurationBuilderEvent.ANY, this.managedBuilderDelegationListener);
        }
        this.getManagedBuilders().clear();
        this.interpolator.set(null);
        super.resetParameters();
    }
    
    protected ConfigurationInterpolator getInterpolator() {
        boolean done;
        ConfigurationInterpolator result;
        do {
            result = this.interpolator.get();
            if (result != null) {
                done = true;
            }
            else {
                result = this.createInterpolator();
                done = this.interpolator.compareAndSet(null, result);
            }
        } while (!done);
        return result;
    }
    
    protected ConfigurationInterpolator createInterpolator() {
        final InterpolatorSpecification spec = BasicBuilderParameters.fetchInterpolatorSpecification(this.getParameters());
        return ConfigurationInterpolator.fromSpecification(spec);
    }
    
    protected String constructFileName(final MultiFileBuilderParametersImpl multiParams) {
        final ConfigurationInterpolator ci = this.getInterpolator();
        return String.valueOf(ci.interpolate(multiParams.getFilePattern()));
    }
    
    protected FileBasedConfigurationBuilder<T> createManagedBuilder(final String fileName, final Map<String, Object> params) throws ConfigurationException {
        return new FileBasedConfigurationBuilder<T>(this.getResultClass(), params, this.isAllowFailOnInit());
    }
    
    protected FileBasedConfigurationBuilder<T> createInitializedManagedBuilder(final String fileName, final Map<String, Object> params) throws ConfigurationException {
        final FileBasedConfigurationBuilder<T> managedBuilder = this.createManagedBuilder(fileName, params);
        managedBuilder.getFileHandler().setFileName(fileName);
        return managedBuilder;
    }
    
    protected ConcurrentMap<String, FileBasedConfigurationBuilder<T>> getManagedBuilders() {
        return this.managedBuilders;
    }
    
    private void initListeners(final FileBasedConfigurationBuilder<T> newBuilder) {
        this.copyEventListeners(newBuilder, this.configurationListeners);
        newBuilder.addEventListener(ConfigurationBuilderEvent.ANY, this.managedBuilderDelegationListener);
    }
    
    private String fetchFileName(final MultiFileBuilderParametersImpl multiParams) {
        final Boolean reentrant = this.inInterpolation.get();
        String fileName;
        if (reentrant != null && reentrant) {
            fileName = multiParams.getFilePattern();
        }
        else {
            this.inInterpolation.set(Boolean.TRUE);
            try {
                fileName = this.constructFileName(multiParams);
            }
            finally {
                this.inInterpolation.set(Boolean.FALSE);
            }
        }
        return fileName;
    }
    
    private void handleManagedBuilderEvent(final ConfigurationBuilderEvent event) {
        if (ConfigurationBuilderEvent.RESET.equals(event.getEventType())) {
            this.resetResult();
        }
        else {
            this.fireBuilderEvent(this.createEventWithChangedSource(event));
        }
    }
    
    private ConfigurationBuilderEvent createEventWithChangedSource(final ConfigurationBuilderEvent event) {
        if (ConfigurationBuilderResultCreatedEvent.RESULT_CREATED.equals(event.getEventType())) {
            return new ConfigurationBuilderResultCreatedEvent(this, ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, ((ConfigurationBuilderResultCreatedEvent)event).getConfiguration());
        }
        final EventType<? extends ConfigurationBuilderEvent> type = (EventType<? extends ConfigurationBuilderEvent>)event.getEventType();
        return new ConfigurationBuilderEvent(this, type);
    }
    
    private static Map<String, Object> createManagedBuilderParameters(final Map<String, Object> params, final MultiFileBuilderParametersImpl multiParams) {
        final Map<String, Object> newParams = new HashMap<String, Object>(params);
        newParams.remove("interpolator");
        final BuilderParameters managedBuilderParameters = multiParams.getManagedBuilderParameters();
        if (managedBuilderParameters != null) {
            final BuilderParameters copy = (BuilderParameters)ConfigurationUtils.cloneIfPossible(managedBuilderParameters);
            newParams.putAll(copy.getParameters());
        }
        return newParams;
    }
    
    private static boolean isEventTypeForManagedBuilders(final EventType<?> eventType) {
        return !EventType.isInstanceOf(eventType, ConfigurationBuilderEvent.ANY);
    }
}
