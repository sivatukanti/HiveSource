// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import java.util.Map;
import java.util.Collection;
import org.codehaus.jackson.map.util.ClassUtil;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;
import java.lang.reflect.Field;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.KeyDeserializer;
import javax.xml.bind.annotation.XmlEnumValue;
import org.codehaus.jackson.map.util.BeanUtil;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElementRef;
import java.beans.Introspector;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import org.codehaus.jackson.map.jsontype.NamedType;
import java.util.List;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.map.MapperConfig;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.annotate.JsonCachable;
import java.lang.annotation.Annotation;
import org.codehaus.jackson.util.VersionUtil;
import org.codehaus.jackson.Version;
import javax.xml.bind.annotation.XmlElement;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.Versioned;
import org.codehaus.jackson.map.AnnotationIntrospector;

public class JaxbAnnotationIntrospector extends AnnotationIntrospector implements Versioned
{
    protected static final String MARKER_FOR_DEFAULT = "##default";
    protected final String _jaxbPackageName;
    protected final JsonSerializer<?> _dataHandlerSerializer;
    protected final JsonDeserializer<?> _dataHandlerDeserializer;
    
    public JaxbAnnotationIntrospector() {
        this._jaxbPackageName = XmlElement.class.getPackage().getName();
        JsonSerializer<?> dataHandlerSerializer = null;
        JsonDeserializer<?> dataHandlerDeserializer = null;
        try {
            dataHandlerSerializer = (JsonSerializer<?>)Class.forName("org.codehaus.jackson.xc.DataHandlerJsonSerializer").newInstance();
            dataHandlerDeserializer = (JsonDeserializer<?>)Class.forName("org.codehaus.jackson.xc.DataHandlerJsonDeserializer").newInstance();
        }
        catch (Throwable t) {}
        this._dataHandlerSerializer = dataHandlerSerializer;
        this._dataHandlerDeserializer = dataHandlerDeserializer;
    }
    
    public Version version() {
        return VersionUtil.versionFor(this.getClass());
    }
    
    @Override
    public boolean isHandled(final Annotation ann) {
        final Class<?> cls = ann.annotationType();
        final Package pkg = cls.getPackage();
        final String pkgName = (pkg != null) ? pkg.getName() : cls.getName();
        return pkgName.startsWith(this._jaxbPackageName) || cls == JsonCachable.class;
    }
    
    @Override
    public Boolean findCachability(final AnnotatedClass ac) {
        final JsonCachable ann = ac.getAnnotation(JsonCachable.class);
        if (ann != null) {
            return ann.value() ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }
    
    @Override
    public String findRootName(final AnnotatedClass ac) {
        final XmlRootElement elem = this.findRootElementAnnotation(ac);
        if (elem != null) {
            final String name = elem.name();
            return "##default".equals(name) ? "" : name;
        }
        return null;
    }
    
    @Override
    public String[] findPropertiesToIgnore(final AnnotatedClass ac) {
        return null;
    }
    
    @Override
    public Boolean findIgnoreUnknownProperties(final AnnotatedClass ac) {
        return null;
    }
    
    @Override
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        return null;
    }
    
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return m.getAnnotation(XmlTransient.class) != null;
    }
    
    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, final VisibilityChecker<?> checker) {
        final XmlAccessType at = this.findAccessType(ac);
        if (at == null) {
            return checker;
        }
        switch (at) {
            case FIELD: {
                return ((VisibilityChecker<VisibilityChecker<VisibilityChecker<VisibilityChecker<?>>>>)checker.withFieldVisibility(JsonAutoDetect.Visibility.ANY)).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            case NONE: {
                return ((VisibilityChecker<VisibilityChecker<VisibilityChecker<VisibilityChecker<?>>>>)checker.withFieldVisibility(JsonAutoDetect.Visibility.NONE)).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            case PROPERTY: {
                return ((VisibilityChecker<VisibilityChecker<VisibilityChecker<VisibilityChecker<?>>>>)checker.withFieldVisibility(JsonAutoDetect.Visibility.NONE)).withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
            }
            case PUBLIC_MEMBER: {
                return ((VisibilityChecker<VisibilityChecker<VisibilityChecker<VisibilityChecker<?>>>>)checker.withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)).withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
            }
            default: {
                return checker;
            }
        }
    }
    
    protected XmlAccessType findAccessType(final Annotated ac) {
        final XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, ac, true, true, true);
        return (at == null) ? null : at.value();
    }
    
    @Override
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        return null;
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        if (baseType.isContainerType()) {
            return null;
        }
        return this._typeResolverFromXmlElements(am);
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType containerType) {
        if (!containerType.isContainerType()) {
            throw new IllegalArgumentException("Must call method with a container type (got " + containerType + ")");
        }
        return this._typeResolverFromXmlElements(am);
    }
    
    protected TypeResolverBuilder<?> _typeResolverFromXmlElements(final AnnotatedMember am) {
        final XmlElements elems = this.findAnnotation(XmlElements.class, am, false, false, false);
        final XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, am, false, false, false);
        if (elems == null && elemRefs == null) {
            return null;
        }
        TypeResolverBuilder<?> b = new StdTypeResolverBuilder();
        b = (TypeResolverBuilder<?>)b.init(JsonTypeInfo.Id.NAME, null);
        b = (TypeResolverBuilder<?>)b.inclusion(JsonTypeInfo.As.WRAPPER_OBJECT);
        return b;
    }
    
    @Override
    public List<NamedType> findSubtypes(final Annotated a) {
        final XmlElements elems = this.findAnnotation(XmlElements.class, a, false, false, false);
        if (elems != null) {
            final ArrayList<NamedType> result = new ArrayList<NamedType>();
            for (final XmlElement elem : elems.value()) {
                String name = elem.name();
                if ("##default".equals(name)) {
                    name = null;
                }
                result.add(new NamedType(elem.type(), name));
            }
            return result;
        }
        final XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, a, false, false, false);
        if (elemRefs != null) {
            final ArrayList<NamedType> result2 = new ArrayList<NamedType>();
            for (final XmlElementRef elemRef : elemRefs.value()) {
                final Class<?> refType = (Class<?>)elemRef.type();
                if (!JAXBElement.class.isAssignableFrom(refType)) {
                    String name2 = elemRef.name();
                    if (name2 == null || "##default".equals(name2)) {
                        final XmlRootElement rootElement = refType.getAnnotation(XmlRootElement.class);
                        if (rootElement != null) {
                            name2 = rootElement.name();
                        }
                    }
                    if (name2 == null || "##default".equals(name2)) {
                        name2 = Introspector.decapitalize(refType.getSimpleName());
                    }
                    result2.add(new NamedType(refType, name2));
                }
            }
            return result2;
        }
        return null;
    }
    
    @Override
    public String findTypeName(final AnnotatedClass ac) {
        final XmlType type = this.findAnnotation(XmlType.class, ac, false, false, false);
        if (type != null) {
            final String name = type.name();
            if (!"##default".equals(name)) {
                return name;
            }
        }
        return null;
    }
    
    @Override
    public boolean isIgnorableMethod(final AnnotatedMethod m) {
        return m.getAnnotation(XmlTransient.class) != null;
    }
    
    @Override
    public boolean isIgnorableConstructor(final AnnotatedConstructor c) {
        return false;
    }
    
    @Override
    public boolean isIgnorableField(final AnnotatedField f) {
        return f.getAnnotation(XmlTransient.class) != null;
    }
    
    @Override
    public JsonSerializer<?> findSerializer(final Annotated am) {
        final XmlAdapter<Object, Object> adapter = this.findAdapter(am, true);
        if (adapter != null) {
            return new XmlAdapterJsonSerializer(adapter);
        }
        final Class<?> type = am.getRawType();
        if (type != null && this._dataHandlerSerializer != null && this.isDataHandler(type)) {
            return this._dataHandlerSerializer;
        }
        return null;
    }
    
    private boolean isDataHandler(final Class<?> type) {
        return type != null && Object.class != type && ("javax.activation.DataHandler".equals(type.getName()) || this.isDataHandler(type.getSuperclass()));
    }
    
    @Override
    public Class<?> findSerializationType(final Annotated a) {
        final XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
        if (annotation == null || annotation.type() == XmlElement.DEFAULT.class) {
            return null;
        }
        final Class<?> rawPropType = a.getRawType();
        if (this.isIndexedType(rawPropType)) {
            return null;
        }
        final Class<?> allegedType = (Class<?>)annotation.type();
        if (a.getAnnotation(XmlJavaTypeAdapter.class) != null) {
            return null;
        }
        return allegedType;
    }
    
    @Override
    public JsonSerialize.Inclusion findSerializationInclusion(final Annotated a, final JsonSerialize.Inclusion defValue) {
        final XmlElementWrapper w = a.getAnnotation(XmlElementWrapper.class);
        if (w != null) {
            return w.nillable() ? JsonSerialize.Inclusion.ALWAYS : JsonSerialize.Inclusion.NON_NULL;
        }
        final XmlElement e = a.getAnnotation(XmlElement.class);
        if (e != null) {
            return e.nillable() ? JsonSerialize.Inclusion.ALWAYS : JsonSerialize.Inclusion.NON_NULL;
        }
        return defValue;
    }
    
    @Override
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        return null;
    }
    
    @Override
    public Class<?>[] findSerializationViews(final Annotated a) {
        return null;
    }
    
    @Override
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        final XmlType type = this.findAnnotation(XmlType.class, ac, true, true, true);
        if (type == null) {
            return null;
        }
        final String[] order = type.propOrder();
        if (order == null || order.length == 0) {
            return null;
        }
        return order;
    }
    
    @Override
    public Boolean findSerializationSortAlphabetically(final AnnotatedClass ac) {
        final XmlAccessorOrder order = this.findAnnotation(XmlAccessorOrder.class, ac, true, true, true);
        return (order == null) ? null : Boolean.valueOf(order.value() == XmlAccessOrder.ALPHABETICAL);
    }
    
    @Override
    public String findGettablePropertyName(final AnnotatedMethod am) {
        if (!this.isVisible(am)) {
            return null;
        }
        final String name = findJaxbPropertyName(am, am.getRawType(), BeanUtil.okNameForGetter(am));
        if (name == null) {}
        return name;
    }
    
    @Override
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    @Override
    public String findEnumValue(final Enum<?> e) {
        final Class<?> enumClass = e.getDeclaringClass();
        final String enumValue = e.name();
        try {
            final XmlEnumValue xmlEnumValue = enumClass.getDeclaredField(enumValue).getAnnotation(XmlEnumValue.class);
            return (xmlEnumValue != null) ? xmlEnumValue.value() : enumValue;
        }
        catch (NoSuchFieldException e2) {
            throw new IllegalStateException("Could not locate Enum entry '" + enumValue + "' (Enum class " + enumClass.getName() + ")", e2);
        }
    }
    
    @Override
    public String findSerializablePropertyName(final AnnotatedField af) {
        if (!this.isVisible(af)) {
            return null;
        }
        final String name = findJaxbPropertyName(af, af.getRawType(), null);
        return (name == null) ? af.getName() : name;
    }
    
    @Override
    public JsonDeserializer<?> findDeserializer(final Annotated am) {
        final XmlAdapter<Object, Object> adapter = this.findAdapter(am, false);
        if (adapter != null) {
            return new XmlAdapterJsonDeserializer(adapter);
        }
        final Class<?> type = am.getRawType();
        if (type != null && this._dataHandlerDeserializer != null && this.isDataHandler(type)) {
            return this._dataHandlerDeserializer;
        }
        return null;
    }
    
    @Override
    public Class<KeyDeserializer> findKeyDeserializer(final Annotated am) {
        return null;
    }
    
    @Override
    public Class<JsonDeserializer<?>> findContentDeserializer(final Annotated am) {
        return null;
    }
    
    @Override
    public Class<?> findDeserializationType(final Annotated a, final JavaType baseType, final String propName) {
        if (!baseType.isContainerType()) {
            return this._doFindDeserializationType(a, baseType, propName);
        }
        return null;
    }
    
    @Override
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType, final String propName) {
        return null;
    }
    
    @Override
    public Class<?> findDeserializationContentType(final Annotated a, final JavaType baseContentType, final String propName) {
        return this._doFindDeserializationType(a, baseContentType, propName);
    }
    
    protected Class<?> _doFindDeserializationType(final Annotated a, final JavaType baseType, final String propName) {
        if (a.hasAnnotation(XmlJavaTypeAdapter.class)) {
            return null;
        }
        XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
        if (annotation != null) {
            final Class<?> type = (Class<?>)annotation.type();
            if (type != XmlElement.DEFAULT.class) {
                return type;
            }
        }
        if (a instanceof AnnotatedMethod && propName != null) {
            final AnnotatedMethod am = (AnnotatedMethod)a;
            annotation = this.findFieldAnnotation(XmlElement.class, am.getDeclaringClass(), propName);
            if (annotation != null && annotation.type() != XmlElement.DEFAULT.class) {
                return (Class<?>)annotation.type();
            }
        }
        return null;
    }
    
    @Override
    public String findSettablePropertyName(final AnnotatedMethod am) {
        if (!this.isVisible(am)) {
            return null;
        }
        final Class<?> rawType = am.getParameterClass(0);
        final String name = findJaxbPropertyName(am, rawType, BeanUtil.okNameForSetter(am));
        return name;
    }
    
    @Override
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    @Override
    public boolean hasCreatorAnnotation(final Annotated am) {
        return false;
    }
    
    @Override
    public String findDeserializablePropertyName(final AnnotatedField af) {
        if (!this.isVisible(af)) {
            return null;
        }
        final String name = findJaxbPropertyName(af, af.getRawType(), null);
        return (name == null) ? af.getName() : name;
    }
    
    @Override
    public String findPropertyNameForParam(final AnnotatedParameter param) {
        return null;
    }
    
    private boolean isVisible(final AnnotatedField f) {
        for (final Annotation annotation : f.getAnnotated().getDeclaredAnnotations()) {
            if (this.isHandled(annotation)) {
                return true;
            }
        }
        XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
        final XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, f, true, true, true);
        if (at != null) {
            accessType = at.value();
        }
        return accessType == XmlAccessType.FIELD || (accessType == XmlAccessType.PUBLIC_MEMBER && Modifier.isPublic(f.getAnnotated().getModifiers()));
    }
    
    private boolean isVisible(final AnnotatedMethod m) {
        for (final Annotation annotation : m.getAnnotated().getDeclaredAnnotations()) {
            if (this.isHandled(annotation)) {
                return true;
            }
        }
        XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
        final XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, m, true, true, true);
        if (at != null) {
            accessType = at.value();
        }
        return (accessType == XmlAccessType.PROPERTY || accessType == XmlAccessType.PUBLIC_MEMBER) && Modifier.isPublic(m.getModifiers());
    }
    
    protected <A extends Annotation> A findAnnotation(final Class<A> annotationClass, final Annotated annotated, final boolean includePackage, final boolean includeClass, final boolean includeSuperclasses) {
        A annotation = annotated.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Class<?> memberClass = null;
        if (annotated instanceof AnnotatedParameter) {
            memberClass = ((AnnotatedParameter)annotated).getDeclaringClass();
        }
        else {
            final AnnotatedElement annType = annotated.getAnnotated();
            if (annType instanceof Member) {
                memberClass = ((Member)annType).getDeclaringClass();
                if (includeClass) {
                    annotation = memberClass.getAnnotation(annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
            else {
                if (!(annType instanceof Class)) {
                    throw new IllegalStateException("Unsupported annotated member: " + annotated.getClass().getName());
                }
                memberClass = (Class<?>)annType;
            }
        }
        if (memberClass != null) {
            if (includeSuperclasses) {
                for (Class<?> superclass = memberClass.getSuperclass(); superclass != null && superclass != Object.class; superclass = superclass.getSuperclass()) {
                    annotation = superclass.getAnnotation(annotationClass);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
            if (includePackage) {
                final Package pkg = memberClass.getPackage();
                if (pkg != null) {
                    return memberClass.getPackage().getAnnotation(annotationClass);
                }
            }
        }
        return null;
    }
    
    private <A extends Annotation> A findFieldAnnotation(final Class<A> annotationType, Class<?> cls, final String fieldName) {
        do {
            for (final Field f : cls.getDeclaredFields()) {
                if (fieldName.equals(f.getName())) {
                    return f.getAnnotation(annotationType);
                }
            }
            if (cls.isInterface()) {
                break;
            }
            if (cls == Object.class) {
                break;
            }
            cls = cls.getSuperclass();
        } while (cls != null);
        return null;
    }
    
    private static String findJaxbPropertyName(final Annotated ae, final Class<?> aeType, final String defaultName) {
        final XmlElementWrapper elementWrapper = ae.getAnnotation(XmlElementWrapper.class);
        if (elementWrapper != null) {
            final String name = elementWrapper.name();
            if (!"##default".equals(name)) {
                return name;
            }
            return defaultName;
        }
        else {
            final XmlAttribute attribute = ae.getAnnotation(XmlAttribute.class);
            if (attribute != null) {
                final String name2 = attribute.name();
                if (!"##default".equals(name2)) {
                    return name2;
                }
                return defaultName;
            }
            else {
                final XmlElement element = ae.getAnnotation(XmlElement.class);
                if (element != null) {
                    final String name3 = element.name();
                    if (!"##default".equals(name3)) {
                        return name3;
                    }
                    return defaultName;
                }
                else {
                    final XmlElementRef elementRef = ae.getAnnotation(XmlElementRef.class);
                    if (elementRef != null) {
                        String name4 = elementRef.name();
                        if (!"##default".equals(name4)) {
                            return name4;
                        }
                        if (aeType != null) {
                            final XmlRootElement rootElement = aeType.getAnnotation(XmlRootElement.class);
                            if (rootElement != null) {
                                name4 = rootElement.name();
                                if (!"##default".equals(name4)) {
                                    return name4;
                                }
                                return Introspector.decapitalize(aeType.getSimpleName());
                            }
                        }
                    }
                    final XmlValue valueInfo = ae.getAnnotation(XmlValue.class);
                    if (valueInfo != null) {
                        return "value";
                    }
                    return null;
                }
            }
        }
    }
    
    private XmlRootElement findRootElementAnnotation(final AnnotatedClass ac) {
        return this.findAnnotation(XmlRootElement.class, ac, true, false, true);
    }
    
    private XmlAdapter<Object, Object> findAdapter(final Annotated am, final boolean forSerialization) {
        if (am instanceof AnnotatedClass) {
            return this.findAdapterForClass((AnnotatedClass)am, forSerialization);
        }
        Class<?> memberType = am.getRawType();
        if (memberType == Void.TYPE && am instanceof AnnotatedMethod) {
            memberType = ((AnnotatedMethod)am).getParameterClass(0);
        }
        final Member member = (Member)am.getAnnotated();
        if (member != null) {
            final Class<?> potentialAdaptee = member.getDeclaringClass();
            if (potentialAdaptee != null) {
                final XmlJavaTypeAdapter adapterInfo = potentialAdaptee.getAnnotation(XmlJavaTypeAdapter.class);
                if (adapterInfo != null) {
                    final XmlAdapter<Object, Object> adapter = this.checkAdapter(adapterInfo, memberType);
                    if (adapter != null) {
                        return adapter;
                    }
                }
            }
        }
        final XmlJavaTypeAdapter adapterInfo2 = this.findAnnotation(XmlJavaTypeAdapter.class, am, true, false, false);
        if (adapterInfo2 != null) {
            final XmlAdapter<Object, Object> adapter2 = this.checkAdapter(adapterInfo2, memberType);
            if (adapter2 != null) {
                return adapter2;
            }
        }
        final XmlJavaTypeAdapters adapters = this.findAnnotation(XmlJavaTypeAdapters.class, am, true, false, false);
        if (adapters != null) {
            for (final XmlJavaTypeAdapter info : adapters.value()) {
                final XmlAdapter<Object, Object> adapter3 = this.checkAdapter(info, memberType);
                if (adapter3 != null) {
                    return adapter3;
                }
            }
        }
        return null;
    }
    
    private final XmlAdapter<Object, Object> checkAdapter(final XmlJavaTypeAdapter adapterInfo, final Class<?> typeNeeded) {
        final Class<?> adaptedType = (Class<?>)adapterInfo.type();
        if (adaptedType == XmlJavaTypeAdapter.DEFAULT.class || adaptedType.isAssignableFrom(typeNeeded)) {
            final Class<? extends XmlAdapter> cls = adapterInfo.value();
            return ClassUtil.createInstance(cls, false);
        }
        return null;
    }
    
    private XmlAdapter<Object, Object> findAdapterForClass(final AnnotatedClass ac, final boolean forSerialization) {
        final XmlJavaTypeAdapter adapterInfo = ac.getAnnotated().getAnnotation(XmlJavaTypeAdapter.class);
        if (adapterInfo != null) {
            final Class<? extends XmlAdapter> cls = adapterInfo.value();
            return ClassUtil.createInstance(cls, false);
        }
        return null;
    }
    
    private boolean isIndexedType(final Class<?> raw) {
        return raw.isArray() || Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw);
    }
}
