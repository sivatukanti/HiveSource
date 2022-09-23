// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import java.util.LinkedHashMap;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import java.util.Iterator;
import java.util.Collections;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.util.Set;
import java.util.Map;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;

public class BasicBeanDescription extends BeanDescription
{
    protected final MapperConfig<?> _config;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final AnnotatedClass _classInfo;
    protected TypeBindings _bindings;
    protected final List<BeanPropertyDefinition> _properties;
    protected ObjectIdInfo _objectIdInfo;
    protected AnnotatedMethod _anySetterMethod;
    protected Map<Object, AnnotatedMember> _injectables;
    protected Set<String> _ignoredPropertyNames;
    protected AnnotatedMethod _jsonValueMethod;
    protected AnnotatedMember _anyGetter;
    
    protected BasicBeanDescription(final MapperConfig<?> config, final JavaType type, final AnnotatedClass classDef, final List<BeanPropertyDefinition> props) {
        super(type);
        this._config = config;
        this._annotationIntrospector = ((config == null) ? null : config.getAnnotationIntrospector());
        this._classInfo = classDef;
        this._properties = props;
    }
    
    protected BasicBeanDescription(final POJOPropertiesCollector coll) {
        this(coll.getConfig(), coll.getType(), coll.getClassDef(), coll.getProperties());
        this._objectIdInfo = coll.getObjectIdInfo();
    }
    
    public static BasicBeanDescription forDeserialization(final POJOPropertiesCollector coll) {
        final BasicBeanDescription desc = new BasicBeanDescription(coll);
        desc._anySetterMethod = coll.getAnySetterMethod();
        desc._ignoredPropertyNames = coll.getIgnoredPropertyNames();
        desc._injectables = coll.getInjectables();
        desc._jsonValueMethod = coll.getJsonValueMethod();
        return desc;
    }
    
    public static BasicBeanDescription forSerialization(final POJOPropertiesCollector coll) {
        final BasicBeanDescription desc = new BasicBeanDescription(coll);
        desc._jsonValueMethod = coll.getJsonValueMethod();
        desc._anyGetter = coll.getAnyGetter();
        return desc;
    }
    
    public static BasicBeanDescription forOtherUse(final MapperConfig<?> config, final JavaType type, final AnnotatedClass ac) {
        return new BasicBeanDescription(config, type, ac, Collections.emptyList());
    }
    
    public boolean removeProperty(final String propName) {
        final Iterator<BeanPropertyDefinition> it = this._properties.iterator();
        while (it.hasNext()) {
            final BeanPropertyDefinition prop = it.next();
            if (prop.getName().equals(propName)) {
                it.remove();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public AnnotatedClass getClassInfo() {
        return this._classInfo;
    }
    
    @Override
    public ObjectIdInfo getObjectIdInfo() {
        return this._objectIdInfo;
    }
    
    @Override
    public List<BeanPropertyDefinition> findProperties() {
        return this._properties;
    }
    
    @Override
    public AnnotatedMethod findJsonValueMethod() {
        return this._jsonValueMethod;
    }
    
    @Override
    public Set<String> getIgnoredPropertyNames() {
        if (this._ignoredPropertyNames == null) {
            return Collections.emptySet();
        }
        return this._ignoredPropertyNames;
    }
    
    @Override
    public boolean hasKnownClassAnnotations() {
        return this._classInfo.hasAnnotations();
    }
    
    @Override
    public Annotations getClassAnnotations() {
        return this._classInfo.getAnnotations();
    }
    
    @Override
    public TypeBindings bindingsForBeanType() {
        if (this._bindings == null) {
            this._bindings = new TypeBindings(this._config.getTypeFactory(), this._type);
        }
        return this._bindings;
    }
    
    @Override
    public JavaType resolveType(final Type jdkType) {
        if (jdkType == null) {
            return null;
        }
        return this.bindingsForBeanType().resolveType(jdkType);
    }
    
    @Override
    public AnnotatedConstructor findDefaultConstructor() {
        return this._classInfo.getDefaultConstructor();
    }
    
    @Override
    public AnnotatedMethod findAnySetter() throws IllegalArgumentException {
        if (this._anySetterMethod != null) {
            final Class<?> type = this._anySetterMethod.getRawParameterType(0);
            if (type != String.class && type != Object.class) {
                throw new IllegalArgumentException("Invalid 'any-setter' annotation on method " + this._anySetterMethod.getName() + "(): first argument not of type String or Object, but " + type.getName());
            }
        }
        return this._anySetterMethod;
    }
    
    @Override
    public Map<Object, AnnotatedMember> findInjectables() {
        return this._injectables;
    }
    
    @Override
    public List<AnnotatedConstructor> getConstructors() {
        return this._classInfo.getConstructors();
    }
    
    @Override
    public Object instantiateBean(final boolean fixAccess) {
        final AnnotatedConstructor ac = this._classInfo.getDefaultConstructor();
        if (ac == null) {
            return null;
        }
        if (fixAccess) {
            ac.fixAccess();
        }
        try {
            return ac.getAnnotated().newInstance(new Object[0]);
        }
        catch (Exception e) {
            Throwable t;
            for (t = e; t.getCause() != null; t = t.getCause()) {}
            if (t instanceof Error) {
                throw (Error)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new IllegalArgumentException("Failed to instantiate bean of type " + this._classInfo.getAnnotated().getName() + ": (" + t.getClass().getName() + ") " + t.getMessage(), t);
        }
    }
    
    @Override
    public AnnotatedMethod findMethod(final String name, final Class<?>[] paramTypes) {
        return this._classInfo.findMethod(name, paramTypes);
    }
    
    @Override
    public JsonFormat.Value findExpectedFormat(final JsonFormat.Value defValue) {
        if (this._annotationIntrospector != null) {
            final JsonFormat.Value v = this._annotationIntrospector.findFormat(this._classInfo);
            if (v != null) {
                return v;
            }
        }
        return defValue;
    }
    
    @Override
    public Converter<Object, Object> findSerializationConverter() {
        if (this._annotationIntrospector == null) {
            return null;
        }
        return this._createConverter(this._annotationIntrospector.findSerializationConverter(this._classInfo));
    }
    
    @Override
    public JsonInclude.Include findSerializationInclusion(final JsonInclude.Include defValue) {
        if (this._annotationIntrospector == null) {
            return defValue;
        }
        return this._annotationIntrospector.findSerializationInclusion(this._classInfo, defValue);
    }
    
    @Override
    public AnnotatedMember findAnyGetter() throws IllegalArgumentException {
        if (this._anyGetter != null) {
            final Class<?> type = this._anyGetter.getRawType();
            if (!Map.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException("Invalid 'any-getter' annotation on method " + this._anyGetter.getName() + "(): return type is not instance of java.util.Map");
            }
        }
        return this._anyGetter;
    }
    
    @Override
    public Map<String, AnnotatedMember> findBackReferenceProperties() {
        HashMap<String, AnnotatedMember> result = null;
        for (final BeanPropertyDefinition property : this._properties) {
            final AnnotatedMember am = property.getMutator();
            if (am == null) {
                continue;
            }
            final AnnotationIntrospector.ReferenceProperty refDef = this._annotationIntrospector.findReferenceType(am);
            if (refDef == null || !refDef.isBackReference()) {
                continue;
            }
            if (result == null) {
                result = new HashMap<String, AnnotatedMember>();
            }
            final String refName = refDef.getName();
            if (result.put(refName, am) != null) {
                throw new IllegalArgumentException("Multiple back-reference properties with name '" + refName + "'");
            }
        }
        return result;
    }
    
    @Override
    public List<AnnotatedMethod> getFactoryMethods() {
        final List<AnnotatedMethod> candidates = this._classInfo.getStaticMethods();
        if (candidates.isEmpty()) {
            return candidates;
        }
        final ArrayList<AnnotatedMethod> result = new ArrayList<AnnotatedMethod>();
        for (final AnnotatedMethod am : candidates) {
            if (this.isFactoryMethod(am)) {
                result.add(am);
            }
        }
        return result;
    }
    
    @Override
    public Constructor<?> findSingleArgConstructor(final Class<?>... argTypes) {
        for (final AnnotatedConstructor ac : this._classInfo.getConstructors()) {
            if (ac.getParameterCount() == 1) {
                final Class<?> actArg = ac.getRawParameterType(0);
                for (final Class<?> expArg : argTypes) {
                    if (expArg == actArg) {
                        return ac.getAnnotated();
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Method findFactoryMethod(final Class<?>... expArgTypes) {
        for (final AnnotatedMethod am : this._classInfo.getStaticMethods()) {
            if (this.isFactoryMethod(am)) {
                final Class<?> actualArgType = am.getRawParameterType(0);
                for (final Class<?> expArgType : expArgTypes) {
                    if (actualArgType.isAssignableFrom(expArgType)) {
                        return am.getAnnotated();
                    }
                }
            }
        }
        return null;
    }
    
    protected boolean isFactoryMethod(final AnnotatedMethod am) {
        final Class<?> rt = am.getRawReturnType();
        if (!this.getBeanClass().isAssignableFrom(rt)) {
            return false;
        }
        if (this._annotationIntrospector.hasCreatorAnnotation(am)) {
            return true;
        }
        final String name = am.getName();
        if ("valueOf".equals(name)) {
            return true;
        }
        if ("fromString".equals(name) && 1 == am.getParameterCount()) {
            final Class<?> cls = am.getRawParameterType(0);
            if (cls == String.class || CharSequence.class.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public List<String> findCreatorPropertyNames() {
        final List<PropertyName> params = this.findCreatorParameterNames();
        if (params.isEmpty()) {
            return Collections.emptyList();
        }
        final List<String> result = new ArrayList<String>(params.size());
        for (final PropertyName name : params) {
            result.add(name.getSimpleName());
        }
        return result;
    }
    
    public List<PropertyName> findCreatorParameterNames() {
        for (int i = 0; i < 2; ++i) {
            final List<? extends AnnotatedWithParams> l = (List<? extends AnnotatedWithParams>)((i == 0) ? this.getConstructors() : this.getFactoryMethods());
            for (final AnnotatedWithParams creator : l) {
                final int argCount = creator.getParameterCount();
                if (argCount < 1) {
                    continue;
                }
                PropertyName name = this._findCreatorPropertyName(creator.getParameter(0));
                if (name == null) {
                    continue;
                }
                if (name.isEmpty()) {
                    continue;
                }
                final List<PropertyName> names = new ArrayList<PropertyName>();
                names.add(name);
                for (int p = 1; p < argCount; ++p) {
                    name = this._findCreatorPropertyName(creator.getParameter(p));
                    names.add(name);
                }
                return names;
            }
        }
        return Collections.emptyList();
    }
    
    protected PropertyName _findCreatorPropertyName(final AnnotatedParameter param) {
        PropertyName name = this._annotationIntrospector.findNameForDeserialization(param);
        if (name == null || name.isEmpty()) {
            final String str = this._annotationIntrospector.findImplicitPropertyName(param);
            if (str != null && !str.isEmpty()) {
                name = new PropertyName(str);
            }
        }
        return name;
    }
    
    @Override
    public Class<?> findPOJOBuilder() {
        return (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findPOJOBuilder(this._classInfo);
    }
    
    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig() {
        return (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findPOJOBuilderConfig(this._classInfo);
    }
    
    @Override
    public Converter<Object, Object> findDeserializationConverter() {
        if (this._annotationIntrospector == null) {
            return null;
        }
        return this._createConverter(this._annotationIntrospector.findDeserializationConverter(this._classInfo));
    }
    
    public LinkedHashMap<String, AnnotatedField> _findPropertyFields(final Collection<String> ignoredProperties, final boolean forSerialization) {
        final LinkedHashMap<String, AnnotatedField> results = new LinkedHashMap<String, AnnotatedField>();
        for (final BeanPropertyDefinition property : this._properties) {
            final AnnotatedField f = property.getField();
            if (f != null) {
                final String name = property.getName();
                if (ignoredProperties != null && ignoredProperties.contains(name)) {
                    continue;
                }
                results.put(name, f);
            }
        }
        return results;
    }
    
    public Converter<Object, Object> _createConverter(final Object converterDef) {
        if (converterDef == null) {
            return null;
        }
        if (converterDef instanceof Converter) {
            return (Converter<Object, Object>)converterDef;
        }
        if (!(converterDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
        }
        final Class<?> converterClass = (Class<?>)converterDef;
        if (converterClass == Converter.None.class || ClassUtil.isBogusClass(converterClass)) {
            return null;
        }
        if (!Converter.class.isAssignableFrom(converterClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
        }
        final HandlerInstantiator hi = this._config.getHandlerInstantiator();
        Converter<?, ?> conv = (hi == null) ? null : hi.converterInstance(this._config, this._classInfo, converterClass);
        if (conv == null) {
            conv = ClassUtil.createInstance(converterClass, this._config.canOverrideAccessModifiers());
        }
        return (Converter<Object, Object>)conv;
    }
}
