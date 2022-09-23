// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.PrivateBinder;
import com.google.inject.Binder;
import com.google.inject.spi.ElementVisitor;
import java.util.Iterator;
import java.util.Map;
import com.google.inject.internal.util.$Maps;
import java.util.Set;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$Lists;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.spi.Element;
import java.util.List;
import com.google.inject.spi.PrivateElements;

public final class PrivateElementsImpl implements PrivateElements
{
    private final Object source;
    private List<Element> elementsMutable;
    private List<ExposureBuilder<?>> exposureBuilders;
    private $ImmutableList<Element> elements;
    private $ImmutableMap<Key<?>, Object> exposedKeysToSources;
    private Injector injector;
    
    public PrivateElementsImpl(final Object source) {
        this.elementsMutable = (List<Element>)$Lists.newArrayList();
        this.exposureBuilders = (List<ExposureBuilder<?>>)$Lists.newArrayList();
        this.source = $Preconditions.checkNotNull(source, (Object)"source");
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public List<Element> getElements() {
        if (this.elements == null) {
            this.elements = $ImmutableList.copyOf((Iterable<? extends Element>)this.elementsMutable);
            this.elementsMutable = null;
        }
        return this.elements;
    }
    
    public Injector getInjector() {
        return this.injector;
    }
    
    public void initInjector(final Injector injector) {
        $Preconditions.checkState(this.injector == null, (Object)"injector already initialized");
        this.injector = $Preconditions.checkNotNull(injector, (Object)"injector");
    }
    
    public Set<Key<?>> getExposedKeys() {
        if (this.exposedKeysToSources == null) {
            final Map<Key<?>, Object> exposedKeysToSourcesMutable = (Map<Key<?>, Object>)$Maps.newLinkedHashMap();
            for (final ExposureBuilder<?> exposureBuilder : this.exposureBuilders) {
                exposedKeysToSourcesMutable.put(exposureBuilder.getKey(), exposureBuilder.getSource());
            }
            this.exposedKeysToSources = $ImmutableMap.copyOf((Map<? extends Key<?>, ?>)exposedKeysToSourcesMutable);
            this.exposureBuilders = null;
        }
        return this.exposedKeysToSources.keySet();
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public List<Element> getElementsMutable() {
        return this.elementsMutable;
    }
    
    public void addExposureBuilder(final ExposureBuilder<?> exposureBuilder) {
        this.exposureBuilders.add(exposureBuilder);
    }
    
    public void applyTo(final Binder binder) {
        final PrivateBinder privateBinder = binder.withSource(this.source).newPrivateBinder();
        for (final Element element : this.getElements()) {
            element.applyTo(privateBinder);
        }
        this.getExposedKeys();
        for (final Map.Entry<Key<?>, Object> entry : this.exposedKeysToSources.entrySet()) {
            privateBinder.withSource(entry.getValue()).expose(entry.getKey());
        }
    }
    
    public Object getExposedSource(final Key<?> key) {
        this.getExposedKeys();
        final Object source = this.exposedKeysToSources.get(key);
        $Preconditions.checkArgument(source != null, "%s not exposed by %s.", key, this);
        return source;
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(PrivateElements.class).add("exposedKeys", this.getExposedKeys()).add("source", this.getSource()).toString();
    }
}
