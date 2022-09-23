// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.BeanUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import java.lang.reflect.Modifier;
import java.util.TreeMap;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;

public class POJOPropertiesCollector
{
    protected final MapperConfig<?> _config;
    protected final boolean _forSerialization;
    protected final JavaType _type;
    protected final AnnotatedClass _classDef;
    protected final VisibilityChecker<?> _visibilityChecker;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final String _mutatorPrefix;
    protected final LinkedHashMap<String, POJOPropertyBuilder> _properties;
    protected LinkedList<POJOPropertyBuilder> _creatorProperties;
    protected LinkedList<AnnotatedMember> _anyGetters;
    protected LinkedList<AnnotatedMethod> _anySetters;
    protected LinkedList<AnnotatedMethod> _jsonValueGetters;
    protected HashSet<String> _ignoredPropertyNames;
    protected LinkedHashMap<Object, AnnotatedMember> _injectables;
    
    protected POJOPropertiesCollector(final MapperConfig<?> config, final boolean forSerialization, final JavaType type, final AnnotatedClass classDef, final String mutatorPrefix) {
        this._properties = new LinkedHashMap<String, POJOPropertyBuilder>();
        this._creatorProperties = null;
        this._anyGetters = null;
        this._anySetters = null;
        this._jsonValueGetters = null;
        this._config = config;
        this._forSerialization = forSerialization;
        this._type = type;
        this._classDef = classDef;
        this._mutatorPrefix = ((mutatorPrefix == null) ? "set" : mutatorPrefix);
        this._annotationIntrospector = (config.isAnnotationProcessingEnabled() ? this._config.getAnnotationIntrospector() : null);
        if (this._annotationIntrospector == null) {
            this._visibilityChecker = this._config.getDefaultVisibilityChecker();
        }
        else {
            this._visibilityChecker = this._annotationIntrospector.findAutoDetectVisibility(classDef, this._config.getDefaultVisibilityChecker());
        }
    }
    
    public MapperConfig<?> getConfig() {
        return this._config;
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    public AnnotatedClass getClassDef() {
        return this._classDef;
    }
    
    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._annotationIntrospector;
    }
    
    public List<BeanPropertyDefinition> getProperties() {
        return new ArrayList<BeanPropertyDefinition>(this._properties.values());
    }
    
    public Map<Object, AnnotatedMember> getInjectables() {
        return this._injectables;
    }
    
    public AnnotatedMethod getJsonValueMethod() {
        if (this._jsonValueGetters != null) {
            if (this._jsonValueGetters.size() > 1) {
                this.reportProblem("Multiple value properties defined (" + this._jsonValueGetters.get(0) + " vs " + this._jsonValueGetters.get(1) + ")");
            }
            return this._jsonValueGetters.get(0);
        }
        return null;
    }
    
    public AnnotatedMember getAnyGetter() {
        if (this._anyGetters != null) {
            if (this._anyGetters.size() > 1) {
                this.reportProblem("Multiple 'any-getters' defined (" + this._anyGetters.get(0) + " vs " + this._anyGetters.get(1) + ")");
            }
            return this._anyGetters.getFirst();
        }
        return null;
    }
    
    public AnnotatedMethod getAnySetterMethod() {
        if (this._anySetters != null) {
            if (this._anySetters.size() > 1) {
                this.reportProblem("Multiple 'any-setters' defined (" + this._anySetters.get(0) + " vs " + this._anySetters.get(1) + ")");
            }
            return this._anySetters.getFirst();
        }
        return null;
    }
    
    public Set<String> getIgnoredPropertyNames() {
        return this._ignoredPropertyNames;
    }
    
    public ObjectIdInfo getObjectIdInfo() {
        if (this._annotationIntrospector == null) {
            return null;
        }
        ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
        if (info != null) {
            info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info);
        }
        return info;
    }
    
    public Class<?> findPOJOBuilderClass() {
        return this._annotationIntrospector.findPOJOBuilder(this._classDef);
    }
    
    protected Map<String, POJOPropertyBuilder> getPropertyMap() {
        return this._properties;
    }
    
    public POJOPropertiesCollector collect() {
        this._properties.clear();
        this._addFields();
        this._addMethods();
        this._addCreators();
        this._addInjectables();
        this._removeUnwantedProperties();
        this._renameProperties();
        final PropertyNamingStrategy naming = this._findNamingStrategy();
        if (naming != null) {
            this._renameUsing(naming);
        }
        for (final POJOPropertyBuilder property : this._properties.values()) {
            property.trimByVisibility();
        }
        for (final POJOPropertyBuilder property : this._properties.values()) {
            property.mergeAnnotations(this._forSerialization);
        }
        if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
            this._renameWithWrappers();
        }
        this._sortProperties();
        return this;
    }
    
    protected void _sortProperties() {
        final AnnotationIntrospector intr = this._annotationIntrospector;
        final Boolean alpha = (intr == null) ? null : intr.findSerializationSortAlphabetically((Annotated)this._classDef);
        boolean sort;
        if (alpha == null) {
            sort = this._config.shouldSortPropertiesAlphabetically();
        }
        else {
            sort = alpha;
        }
        final String[] propertyOrder = (String[])((intr == null) ? null : intr.findSerializationPropertyOrder(this._classDef));
        if (!sort && this._creatorProperties == null && propertyOrder == null) {
            return;
        }
        final int size = this._properties.size();
        Map<String, POJOPropertyBuilder> all;
        if (sort) {
            all = new TreeMap<String, POJOPropertyBuilder>();
        }
        else {
            all = new LinkedHashMap<String, POJOPropertyBuilder>(size + size);
        }
        for (final POJOPropertyBuilder prop : this._properties.values()) {
            all.put(prop.getName(), prop);
        }
        final Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap<String, POJOPropertyBuilder>(size + size);
        if (propertyOrder != null) {
            for (String name : propertyOrder) {
                POJOPropertyBuilder w = all.get(name);
                if (w == null) {
                    for (final POJOPropertyBuilder prop2 : this._properties.values()) {
                        if (name.equals(prop2.getInternalName())) {
                            w = prop2;
                            name = prop2.getName();
                            break;
                        }
                    }
                }
                if (w != null) {
                    ordered.put(name, w);
                }
            }
        }
        if (this._creatorProperties != null) {
            Collection<POJOPropertyBuilder> cr;
            if (sort) {
                final TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap<String, POJOPropertyBuilder>();
                for (final POJOPropertyBuilder prop3 : this._creatorProperties) {
                    sorted.put(prop3.getName(), prop3);
                }
                cr = sorted.values();
            }
            else {
                cr = this._creatorProperties;
            }
            for (final POJOPropertyBuilder prop4 : cr) {
                ordered.put(prop4.getName(), prop4);
            }
        }
        ordered.putAll(all);
        this._properties.clear();
        this._properties.putAll((Map<?, ?>)ordered);
    }
    
    protected void _addFields() {
        final AnnotationIntrospector ai = this._annotationIntrospector;
        final boolean pruneFinalFields = !this._forSerialization && !this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS);
        for (final AnnotatedField f : this._classDef.fields()) {
            String implName = (ai == null) ? null : ai.findImplicitPropertyName(f);
            if (implName == null) {
                implName = f.getName();
            }
            PropertyName pn;
            if (ai == null) {
                pn = null;
            }
            else if (this._forSerialization) {
                pn = ai.findNameForSerialization(f);
            }
            else {
                pn = ai.findNameForDeserialization(f);
            }
            boolean nameExplicit = pn != null;
            if (nameExplicit && pn.isEmpty()) {
                pn = this._propNameFromSimple(implName);
                nameExplicit = false;
            }
            boolean visible = pn != null;
            if (!visible) {
                visible = this._visibilityChecker.isFieldVisible(f);
            }
            final boolean ignored = ai != null && ai.hasIgnoreMarker(f);
            if (pruneFinalFields && pn == null && !ignored && Modifier.isFinal(f.getModifiers())) {
                continue;
            }
            this._property(implName).addField(f, pn, nameExplicit, visible, ignored);
        }
    }
    
    protected void _addCreators() {
        if (this._annotationIntrospector != null) {
            for (final AnnotatedConstructor ctor : this._classDef.getConstructors()) {
                if (this._creatorProperties == null) {
                    this._creatorProperties = new LinkedList<POJOPropertyBuilder>();
                }
                for (int i = 0, len = ctor.getParameterCount(); i < len; ++i) {
                    this._addCreatorParam(ctor.getParameter(i));
                }
            }
            for (final AnnotatedMethod factory : this._classDef.getStaticMethods()) {
                if (this._creatorProperties == null) {
                    this._creatorProperties = new LinkedList<POJOPropertyBuilder>();
                }
                for (int i = 0, len = factory.getParameterCount(); i < len; ++i) {
                    this._addCreatorParam(factory.getParameter(i));
                }
            }
        }
    }
    
    protected void _addCreatorParam(final AnnotatedParameter param) {
        String impl = this._annotationIntrospector.findImplicitPropertyName(param);
        if (impl == null) {
            impl = "";
        }
        PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
        final boolean expl = pn != null && !pn.isEmpty();
        if (!expl) {
            if (impl.isEmpty()) {
                return;
            }
            pn = new PropertyName(impl);
        }
        final POJOPropertyBuilder prop = expl ? this._property(pn) : this._property(impl);
        prop.addCtor(param, pn, expl, true, false);
        this._creatorProperties.add(prop);
    }
    
    protected void _addMethods() {
        final AnnotationIntrospector ai = this._annotationIntrospector;
        for (final AnnotatedMethod m : this._classDef.memberMethods()) {
            final int argCount = m.getParameterCount();
            if (argCount == 0) {
                this._addGetterMethod(m, ai);
            }
            else if (argCount == 1) {
                this._addSetterMethod(m, ai);
            }
            else {
                if (argCount != 2 || ai == null || !ai.hasAnySetterAnnotation(m)) {
                    continue;
                }
                if (this._anySetters == null) {
                    this._anySetters = new LinkedList<AnnotatedMethod>();
                }
                this._anySetters.add(m);
            }
        }
    }
    
    protected void _addGetterMethod(final AnnotatedMethod m, final AnnotationIntrospector ai) {
        if (!m.hasReturnType()) {
            return;
        }
        if (ai != null) {
            if (ai.hasAnyGetterAnnotation(m)) {
                if (this._anyGetters == null) {
                    this._anyGetters = new LinkedList<AnnotatedMember>();
                }
                this._anyGetters.add(m);
                return;
            }
            if (ai.hasAsValueAnnotation(m)) {
                if (this._jsonValueGetters == null) {
                    this._jsonValueGetters = new LinkedList<AnnotatedMethod>();
                }
                this._jsonValueGetters.add(m);
                return;
            }
        }
        PropertyName pn = (ai == null) ? null : ai.findNameForSerialization(m);
        boolean nameExplicit = pn != null;
        String implName;
        boolean visible;
        if (!nameExplicit) {
            implName = ((ai == null) ? null : ai.findImplicitPropertyName(m));
            if (implName == null) {
                implName = BeanUtil.okNameForRegularGetter(m, m.getName());
            }
            if (implName == null) {
                implName = BeanUtil.okNameForIsGetter(m, m.getName());
                if (implName == null) {
                    return;
                }
                visible = this._visibilityChecker.isIsGetterVisible(m);
            }
            else {
                visible = this._visibilityChecker.isGetterVisible(m);
            }
        }
        else {
            implName = ((ai == null) ? null : ai.findImplicitPropertyName(m));
            if (implName == null) {
                implName = BeanUtil.okNameForGetter(m);
            }
            if (implName == null) {
                implName = m.getName();
            }
            if (pn.isEmpty()) {
                pn = this._propNameFromSimple(implName);
                nameExplicit = false;
            }
            visible = true;
        }
        final boolean ignore = ai != null && ai.hasIgnoreMarker(m);
        this._property(implName).addGetter(m, pn, nameExplicit, visible, ignore);
    }
    
    protected void _addSetterMethod(final AnnotatedMethod m, final AnnotationIntrospector ai) {
        PropertyName pn = (ai == null) ? null : ai.findNameForDeserialization(m);
        boolean nameExplicit = pn != null;
        String implName;
        boolean visible;
        if (!nameExplicit) {
            implName = ((ai == null) ? null : ai.findImplicitPropertyName(m));
            if (implName == null) {
                implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix);
            }
            if (implName == null) {
                return;
            }
            visible = this._visibilityChecker.isSetterVisible(m);
        }
        else {
            implName = ((ai == null) ? null : ai.findImplicitPropertyName(m));
            if (implName == null) {
                implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix);
            }
            if (implName == null) {
                implName = m.getName();
            }
            if (pn.isEmpty()) {
                pn = this._propNameFromSimple(implName);
                nameExplicit = false;
            }
            visible = true;
        }
        final boolean ignore = ai != null && ai.hasIgnoreMarker(m);
        this._property(implName).addSetter(m, pn, nameExplicit, visible, ignore);
    }
    
    protected void _addInjectables() {
        final AnnotationIntrospector ai = this._annotationIntrospector;
        if (ai == null) {
            return;
        }
        for (final AnnotatedField f : this._classDef.fields()) {
            this._doAddInjectable(ai.findInjectableValueId(f), f);
        }
        for (final AnnotatedMethod m : this._classDef.memberMethods()) {
            if (m.getParameterCount() != 1) {
                continue;
            }
            this._doAddInjectable(ai.findInjectableValueId(m), m);
        }
    }
    
    protected void _doAddInjectable(final Object id, final AnnotatedMember m) {
        if (id == null) {
            return;
        }
        if (this._injectables == null) {
            this._injectables = new LinkedHashMap<Object, AnnotatedMember>();
        }
        final AnnotatedMember prev = this._injectables.put(id, m);
        if (prev != null) {
            final String type = id.getClass().getName();
            throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
        }
    }
    
    private PropertyName _propNameFromSimple(final String simpleName) {
        return PropertyName.construct(simpleName, null);
    }
    
    protected void _removeUnwantedProperties() {
        final Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
        final boolean forceNonVisibleRemoval = !this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
        while (it.hasNext()) {
            final Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            final POJOPropertyBuilder prop = entry.getValue();
            if (!prop.anyVisible()) {
                it.remove();
            }
            else {
                if (prop.anyIgnorals()) {
                    if (!prop.isExplicitlyIncluded()) {
                        it.remove();
                        this._addIgnored(prop.getName());
                        continue;
                    }
                    prop.removeIgnored();
                    if (!this._forSerialization && !prop.couldDeserialize()) {
                        this._addIgnored(prop.getName());
                    }
                }
                prop.removeNonVisible(forceNonVisibleRemoval);
            }
        }
    }
    
    private void _addIgnored(final String name) {
        if (!this._forSerialization) {
            if (this._ignoredPropertyNames == null) {
                this._ignoredPropertyNames = new HashSet<String>();
            }
            this._ignoredPropertyNames.add(name);
        }
    }
    
    protected void _renameProperties() {
        final Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            final Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            final POJOPropertyBuilder prop = entry.getValue();
            final Collection<PropertyName> l = prop.findExplicitNames();
            if (l.isEmpty()) {
                continue;
            }
            it.remove();
            if (renamed == null) {
                renamed = new LinkedList<POJOPropertyBuilder>();
            }
            if (l.size() == 1) {
                final PropertyName n = l.iterator().next();
                renamed.add(prop.withName(n));
            }
            else {
                renamed.addAll(prop.explode(l));
            }
        }
        if (renamed != null) {
            for (final POJOPropertyBuilder prop : renamed) {
                final String name = prop.getName();
                final POJOPropertyBuilder old = this._properties.get(name);
                if (old == null) {
                    this._properties.put(name, prop);
                }
                else {
                    old.addAll(prop);
                }
                this._updateCreatorProperty(prop, this._creatorProperties);
            }
        }
    }
    
    protected void _renameUsing(final PropertyNamingStrategy naming) {
        final POJOPropertyBuilder[] props = this._properties.values().toArray(new POJOPropertyBuilder[this._properties.size()]);
        this._properties.clear();
        for (POJOPropertyBuilder prop : props) {
            final PropertyName fullName = prop.getFullName();
            String rename = null;
            if (!prop.isExplicitlyNamed()) {
                if (this._forSerialization) {
                    if (prop.hasGetter()) {
                        rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
                    }
                    else if (prop.hasField()) {
                        rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
                    }
                }
                else if (prop.hasSetter()) {
                    rename = naming.nameForSetterMethod(this._config, prop.getSetter(), fullName.getSimpleName());
                }
                else if (prop.hasConstructorParameter()) {
                    rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
                }
                else if (prop.hasField()) {
                    rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
                }
                else if (prop.hasGetter()) {
                    rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
                }
            }
            String simpleName;
            if (rename != null && !fullName.hasSimpleName(rename)) {
                prop = prop.withSimpleName(rename);
                simpleName = rename;
            }
            else {
                simpleName = fullName.getSimpleName();
            }
            final POJOPropertyBuilder old = this._properties.get(simpleName);
            if (old == null) {
                this._properties.put(simpleName, prop);
            }
            else {
                old.addAll(prop);
            }
            this._updateCreatorProperty(prop, this._creatorProperties);
        }
    }
    
    protected void _renameWithWrappers() {
        final Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            final Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            POJOPropertyBuilder prop = entry.getValue();
            final AnnotatedMember member = prop.getPrimaryMember();
            if (member == null) {
                continue;
            }
            final PropertyName wrapperName = this._annotationIntrospector.findWrapperName(member);
            if (wrapperName == null) {
                continue;
            }
            if (!wrapperName.hasSimpleName()) {
                continue;
            }
            if (wrapperName.equals(prop.getFullName())) {
                continue;
            }
            if (renamed == null) {
                renamed = new LinkedList<POJOPropertyBuilder>();
            }
            prop = prop.withName(wrapperName);
            renamed.add(prop);
            it.remove();
        }
        if (renamed != null) {
            for (final POJOPropertyBuilder prop : renamed) {
                final String name = prop.getName();
                final POJOPropertyBuilder old = this._properties.get(name);
                if (old == null) {
                    this._properties.put(name, prop);
                }
                else {
                    old.addAll(prop);
                }
            }
        }
    }
    
    protected void reportProblem(final String msg) {
        throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
    }
    
    protected POJOPropertyBuilder _property(final PropertyName name) {
        return this._property(name.getSimpleName());
    }
    
    protected POJOPropertyBuilder _property(final String implName) {
        POJOPropertyBuilder prop = this._properties.get(implName);
        if (prop == null) {
            prop = new POJOPropertyBuilder(new PropertyName(implName), this._annotationIntrospector, this._forSerialization);
            this._properties.put(implName, prop);
        }
        return prop;
    }
    
    private PropertyNamingStrategy _findNamingStrategy() {
        final Object namingDef = (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findNamingStrategy(this._classDef);
        if (namingDef == null) {
            return this._config.getPropertyNamingStrategy();
        }
        if (namingDef instanceof PropertyNamingStrategy) {
            return (PropertyNamingStrategy)namingDef;
        }
        if (!(namingDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
        }
        final Class<?> namingClass = (Class<?>)namingDef;
        if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>");
        }
        final HandlerInstantiator hi = this._config.getHandlerInstantiator();
        if (hi != null) {
            final PropertyNamingStrategy pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass);
            if (pns != null) {
                return pns;
            }
        }
        return ClassUtil.createInstance(namingClass, this._config.canOverrideAccessModifiers());
    }
    
    protected void _updateCreatorProperty(final POJOPropertyBuilder prop, final List<POJOPropertyBuilder> creatorProperties) {
        if (creatorProperties != null) {
            for (int i = 0, len = creatorProperties.size(); i < len; ++i) {
                if (creatorProperties.get(i).getInternalName().equals(prop.getInternalName())) {
                    creatorProperties.set(i, prop);
                    break;
                }
            }
        }
    }
}
