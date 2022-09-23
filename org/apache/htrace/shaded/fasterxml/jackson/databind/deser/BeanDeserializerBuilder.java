// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import java.util.LinkedHashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import java.util.HashSet;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ValueInjector;
import java.util.List;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;

public class BeanDeserializerBuilder
{
    protected final BeanDescription _beanDesc;
    protected final boolean _defaultViewInclusion;
    protected final Map<String, SettableBeanProperty> _properties;
    protected List<ValueInjector> _injectables;
    protected HashMap<String, SettableBeanProperty> _backRefProperties;
    protected HashSet<String> _ignorableProps;
    protected ValueInstantiator _valueInstantiator;
    protected ObjectIdReader _objectIdReader;
    protected SettableAnyProperty _anySetter;
    protected boolean _ignoreAllUnknown;
    protected AnnotatedMethod _buildMethod;
    protected JsonPOJOBuilder.Value _builderConfig;
    
    public BeanDeserializerBuilder(final BeanDescription beanDesc, final DeserializationConfig config) {
        this._properties = new LinkedHashMap<String, SettableBeanProperty>();
        this._beanDesc = beanDesc;
        this._defaultViewInclusion = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
    }
    
    protected BeanDeserializerBuilder(final BeanDeserializerBuilder src) {
        this._properties = new LinkedHashMap<String, SettableBeanProperty>();
        this._beanDesc = src._beanDesc;
        this._defaultViewInclusion = src._defaultViewInclusion;
        this._properties.putAll(src._properties);
        this._injectables = _copy(src._injectables);
        this._backRefProperties = _copy(src._backRefProperties);
        this._ignorableProps = src._ignorableProps;
        this._valueInstantiator = src._valueInstantiator;
        this._objectIdReader = src._objectIdReader;
        this._anySetter = src._anySetter;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._buildMethod = src._buildMethod;
        this._builderConfig = src._builderConfig;
    }
    
    private static HashMap<String, SettableBeanProperty> _copy(final HashMap<String, SettableBeanProperty> src) {
        return (src == null) ? null : new HashMap<String, SettableBeanProperty>(src);
    }
    
    private static <T> List<T> _copy(final List<T> src) {
        return (src == null) ? null : new ArrayList<T>((Collection<? extends T>)src);
    }
    
    public void addOrReplaceProperty(final SettableBeanProperty prop, final boolean allowOverride) {
        this._properties.put(prop.getName(), prop);
    }
    
    public void addProperty(final SettableBeanProperty prop) {
        final SettableBeanProperty old = this._properties.put(prop.getName(), prop);
        if (old != null && old != prop) {
            throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + this._beanDesc.getType());
        }
    }
    
    public void addBackReferenceProperty(final String referenceName, final SettableBeanProperty prop) {
        if (this._backRefProperties == null) {
            this._backRefProperties = new HashMap<String, SettableBeanProperty>(4);
        }
        this._backRefProperties.put(referenceName, prop);
        if (this._properties != null) {
            this._properties.remove(prop.getName());
        }
    }
    
    @Deprecated
    public void addInjectable(final String propName, final JavaType propType, final Annotations contextAnnotations, final AnnotatedMember member, final Object valueId) {
        this.addInjectable(new PropertyName(propName), propType, contextAnnotations, member, valueId);
    }
    
    public void addInjectable(final PropertyName propName, final JavaType propType, final Annotations contextAnnotations, final AnnotatedMember member, final Object valueId) {
        if (this._injectables == null) {
            this._injectables = new ArrayList<ValueInjector>();
        }
        this._injectables.add(new ValueInjector(propName, propType, contextAnnotations, member, valueId));
    }
    
    public void addIgnorable(final String propName) {
        if (this._ignorableProps == null) {
            this._ignorableProps = new HashSet<String>();
        }
        this._ignorableProps.add(propName);
    }
    
    public void addCreatorProperty(final SettableBeanProperty prop) {
        this.addProperty(prop);
    }
    
    public void setAnySetter(final SettableAnyProperty s) {
        if (this._anySetter != null && s != null) {
            throw new IllegalStateException("_anySetter already set to non-null");
        }
        this._anySetter = s;
    }
    
    public void setIgnoreUnknownProperties(final boolean ignore) {
        this._ignoreAllUnknown = ignore;
    }
    
    public void setValueInstantiator(final ValueInstantiator inst) {
        this._valueInstantiator = inst;
    }
    
    public void setObjectIdReader(final ObjectIdReader r) {
        this._objectIdReader = r;
    }
    
    public void setPOJOBuilder(final AnnotatedMethod buildMethod, final JsonPOJOBuilder.Value config) {
        this._buildMethod = buildMethod;
        this._builderConfig = config;
    }
    
    public Iterator<SettableBeanProperty> getProperties() {
        return this._properties.values().iterator();
    }
    
    public SettableBeanProperty findProperty(final PropertyName propertyName) {
        return this._properties.get(propertyName.getSimpleName());
    }
    
    @Deprecated
    public SettableBeanProperty findProperty(final String propertyName) {
        return this._properties.get(propertyName);
    }
    
    public boolean hasProperty(final PropertyName propertyName) {
        return this.findProperty(propertyName) != null;
    }
    
    @Deprecated
    public boolean hasProperty(final String propertyName) {
        return this.findProperty(propertyName) != null;
    }
    
    public SettableBeanProperty removeProperty(final PropertyName name) {
        return this._properties.remove(name.getSimpleName());
    }
    
    @Deprecated
    public SettableBeanProperty removeProperty(final String name) {
        return this._properties.remove(name);
    }
    
    public SettableAnyProperty getAnySetter() {
        return this._anySetter;
    }
    
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    public List<ValueInjector> getInjectables() {
        return this._injectables;
    }
    
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }
    
    public AnnotatedMethod getBuildMethod() {
        return this._buildMethod;
    }
    
    public JsonPOJOBuilder.Value getBuilderConfig() {
        return this._builderConfig;
    }
    
    public JsonDeserializer<?> build() {
        final Collection<SettableBeanProperty> props = this._properties.values();
        BeanPropertyMap propertyMap = new BeanPropertyMap(props);
        propertyMap.assignIndexes();
        boolean anyViews = !this._defaultViewInclusion;
        if (!anyViews) {
            for (final SettableBeanProperty prop : props) {
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }
        if (this._objectIdReader != null) {
            final ObjectIdValueProperty prop2 = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop2);
        }
        return new BeanDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
    }
    
    public AbstractDeserializer buildAbstract() {
        return new AbstractDeserializer(this, this._beanDesc, this._backRefProperties);
    }
    
    public JsonDeserializer<?> buildBuilderBased(final JavaType valueType, final String expBuildMethodName) {
        if (this._buildMethod == null) {
            throw new IllegalArgumentException("Builder class " + this._beanDesc.getBeanClass().getName() + " does not have build method '" + expBuildMethodName + "()'");
        }
        final Class<?> rawBuildType = this._buildMethod.getRawReturnType();
        if (!valueType.getRawClass().isAssignableFrom(rawBuildType)) {
            throw new IllegalArgumentException("Build method '" + this._buildMethod.getFullName() + " has bad return type (" + rawBuildType.getName() + "), not compatible with POJO type (" + valueType.getRawClass().getName() + ")");
        }
        final Collection<SettableBeanProperty> props = this._properties.values();
        BeanPropertyMap propertyMap = new BeanPropertyMap(props);
        propertyMap.assignIndexes();
        boolean anyViews = !this._defaultViewInclusion;
        if (!anyViews) {
            for (final SettableBeanProperty prop : props) {
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }
        if (this._objectIdReader != null) {
            final ObjectIdValueProperty prop2 = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop2);
        }
        return new BuilderBasedDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, anyViews);
    }
}
