// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAnyGetter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAnySetter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonSetter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonValue;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonGetter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonIdentityReference;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonIdentityInfo;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeId;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonView;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.RawSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonRawValue;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JacksonInject;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonProperty;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonUnwrapped;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonManagedReference;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonNaming;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFilter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonIgnoreType;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonRootName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.PackageVersion;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;

public class JacksonAnnotationIntrospector extends AnnotationIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    @Override
    public boolean isAnnotationBundle(final Annotation ann) {
        return ann.annotationType().getAnnotation(JacksonAnnotationsInside.class) != null;
    }
    
    @Override
    public PropertyName findRootName(final AnnotatedClass ac) {
        final JsonRootName ann = ac.getAnnotation(JsonRootName.class);
        if (ann == null) {
            return null;
        }
        String ns = ann.namespace();
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        return PropertyName.construct(ann.value(), ns);
    }
    
    @Override
    public String[] findPropertiesToIgnore(final Annotated ac) {
        final JsonIgnoreProperties ignore = ac.getAnnotation(JsonIgnoreProperties.class);
        return (String[])((ignore == null) ? null : ignore.value());
    }
    
    @Override
    public Boolean findIgnoreUnknownProperties(final AnnotatedClass ac) {
        final JsonIgnoreProperties ignore = ac.getAnnotation(JsonIgnoreProperties.class);
        return (ignore == null) ? null : Boolean.valueOf(ignore.ignoreUnknown());
    }
    
    @Override
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        final JsonIgnoreType ignore = ac.getAnnotation(JsonIgnoreType.class);
        return (ignore == null) ? null : Boolean.valueOf(ignore.value());
    }
    
    @Deprecated
    @Override
    public Object findFilterId(final AnnotatedClass ac) {
        return this._findFilterId(ac);
    }
    
    @Override
    public Object findFilterId(final Annotated a) {
        return this._findFilterId(a);
    }
    
    protected final Object _findFilterId(final Annotated a) {
        final JsonFilter ann = a.getAnnotation(JsonFilter.class);
        if (ann != null) {
            final String id = ann.value();
            if (id.length() > 0) {
                return id;
            }
        }
        return null;
    }
    
    @Override
    public Object findNamingStrategy(final AnnotatedClass ac) {
        final JsonNaming ann = ac.getAnnotation(JsonNaming.class);
        return (ann == null) ? null : ann.value();
    }
    
    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, final VisibilityChecker<?> checker) {
        final JsonAutoDetect ann = ac.getAnnotation(JsonAutoDetect.class);
        return (VisibilityChecker<?>)((ann == null) ? checker : checker.with(ann));
    }
    
    @Override
    public ReferenceProperty findReferenceType(final AnnotatedMember member) {
        final JsonManagedReference ref1 = member.getAnnotation(JsonManagedReference.class);
        if (ref1 != null) {
            return ReferenceProperty.managed(ref1.value());
        }
        final JsonBackReference ref2 = member.getAnnotation(JsonBackReference.class);
        if (ref2 != null) {
            return ReferenceProperty.back(ref2.value());
        }
        return null;
    }
    
    @Override
    public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
        final JsonUnwrapped ann = member.getAnnotation(JsonUnwrapped.class);
        if (ann == null || !ann.enabled()) {
            return null;
        }
        final String prefix = ann.prefix();
        final String suffix = ann.suffix();
        return NameTransformer.simpleTransformer(prefix, suffix);
    }
    
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return this._isIgnorable(m);
    }
    
    @Override
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        final JsonProperty ann = m.getAnnotation(JsonProperty.class);
        if (ann != null) {
            return ann.required();
        }
        return null;
    }
    
    @Override
    public Object findInjectableValueId(final AnnotatedMember m) {
        final JacksonInject ann = m.getAnnotation(JacksonInject.class);
        if (ann == null) {
            return null;
        }
        final String id = ann.value();
        if (id.length() != 0) {
            return id;
        }
        if (!(m instanceof AnnotatedMethod)) {
            return m.getRawType().getName();
        }
        final AnnotatedMethod am = (AnnotatedMethod)m;
        if (am.getParameterCount() == 0) {
            return m.getRawType().getName();
        }
        return am.getRawParameterType(0).getName();
    }
    
    @Override
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        return this._findTypeResolver(config, ac, baseType);
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        if (baseType.isContainerType()) {
            return null;
        }
        return this._findTypeResolver(config, am, baseType);
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType containerType) {
        if (!containerType.isContainerType()) {
            throw new IllegalArgumentException("Must call method with a container type (got " + containerType + ")");
        }
        return this._findTypeResolver(config, am, containerType);
    }
    
    @Override
    public List<NamedType> findSubtypes(final Annotated a) {
        final JsonSubTypes t = a.getAnnotation(JsonSubTypes.class);
        if (t == null) {
            return null;
        }
        final JsonSubTypes.Type[] types = t.value();
        final ArrayList<NamedType> result = new ArrayList<NamedType>(types.length);
        for (final JsonSubTypes.Type type : types) {
            result.add(new NamedType(type.value(), type.name()));
        }
        return result;
    }
    
    @Override
    public String findTypeName(final AnnotatedClass ac) {
        final JsonTypeName tn = ac.getAnnotation(JsonTypeName.class);
        return (tn == null) ? null : tn.value();
    }
    
    @Override
    public Object findSerializer(final Annotated a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer<?>> serClass = ann.using();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        final JsonRawValue annRaw = a.getAnnotation(JsonRawValue.class);
        if (annRaw != null && annRaw.value()) {
            final Class<?> cls = a.getRawType();
            return new RawSerializer(cls);
        }
        return null;
    }
    
    @Override
    public Class<? extends JsonSerializer<?>> findKeySerializer(final Annotated a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer<?>> serClass = ann.keyUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }
    
    @Override
    public Class<? extends JsonSerializer<?>> findContentSerializer(final Annotated a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer<?>> serClass = ann.contentUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }
    
    @Override
    public Object findNullSerializer(final Annotated a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer<?>> serClass = ann.nullsUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }
    
    @Override
    public JsonInclude.Include findSerializationInclusion(final Annotated a, final JsonInclude.Include defValue) {
        final JsonInclude inc = a.getAnnotation(JsonInclude.class);
        if (inc != null) {
            return inc.value();
        }
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        if (ann != null) {
            final JsonSerialize.Inclusion i2 = ann.include();
            switch (i2) {
                case ALWAYS: {
                    return JsonInclude.Include.ALWAYS;
                }
                case NON_NULL: {
                    return JsonInclude.Include.NON_NULL;
                }
                case NON_DEFAULT: {
                    return JsonInclude.Include.NON_DEFAULT;
                }
                case NON_EMPTY: {
                    return JsonInclude.Include.NON_EMPTY;
                }
            }
        }
        return defValue;
    }
    
    @Override
    public Class<?> findSerializationType(final Annotated am) {
        final JsonSerialize ann = am.getAnnotation(JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.as());
    }
    
    @Override
    public Class<?> findSerializationKeyType(final Annotated am, final JavaType baseType) {
        final JsonSerialize ann = am.getAnnotation(JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.keyAs());
    }
    
    @Override
    public Class<?> findSerializationContentType(final Annotated am, final JavaType baseType) {
        final JsonSerialize ann = am.getAnnotation(JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.contentAs());
    }
    
    @Override
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        return (ann == null) ? null : ann.typing();
    }
    
    @Override
    public Object findSerializationConverter(final Annotated a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.converter(), Converter.None.class);
    }
    
    @Override
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        final JsonSerialize ann = a.getAnnotation(JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.contentConverter(), Converter.None.class);
    }
    
    @Override
    public Class<?>[] findViews(final Annotated a) {
        final JsonView ann = a.getAnnotation(JsonView.class);
        return (Class<?>[])((ann == null) ? null : ann.value());
    }
    
    @Override
    public Boolean isTypeId(final AnnotatedMember member) {
        return member.hasAnnotation(JsonTypeId.class);
    }
    
    @Override
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        final JsonIdentityInfo info = ann.getAnnotation(JsonIdentityInfo.class);
        if (info == null || info.generator() == ObjectIdGenerators.None.class) {
            return null;
        }
        final PropertyName name = new PropertyName(info.property());
        return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
    }
    
    @Override
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, ObjectIdInfo objectIdInfo) {
        final JsonIdentityReference ref = ann.getAnnotation(JsonIdentityReference.class);
        if (ref != null) {
            objectIdInfo = objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
        }
        return objectIdInfo;
    }
    
    @Override
    public JsonFormat.Value findFormat(final Annotated annotated) {
        final JsonFormat ann = annotated.getAnnotation(JsonFormat.class);
        return (ann == null) ? null : new JsonFormat.Value(ann);
    }
    
    @Override
    public String findPropertyDescription(final Annotated annotated) {
        final JsonPropertyDescription desc = annotated.getAnnotation(JsonPropertyDescription.class);
        return (desc == null) ? null : desc.value();
    }
    
    @Override
    public Integer findPropertyIndex(final Annotated annotated) {
        final JsonProperty ann = annotated.getAnnotation(JsonProperty.class);
        if (ann != null) {
            final int ix = ann.index();
            if (ix != -1) {
                return ix;
            }
        }
        return null;
    }
    
    @Override
    public String findImplicitPropertyName(final AnnotatedMember param) {
        return null;
    }
    
    @Override
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        final JsonPropertyOrder order = ac.getAnnotation(JsonPropertyOrder.class);
        return (String[])((order == null) ? null : order.value());
    }
    
    @Override
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        return this._findSortAlpha(ann);
    }
    
    @Deprecated
    @Override
    public Boolean findSerializationSortAlphabetically(final AnnotatedClass ac) {
        return this._findSortAlpha(ac);
    }
    
    private final Boolean _findSortAlpha(final Annotated ann) {
        final JsonPropertyOrder order = ann.getAnnotation(JsonPropertyOrder.class);
        return (order == null) ? null : Boolean.valueOf(order.alphabetic());
    }
    
    @Override
    public PropertyName findNameForSerialization(final Annotated a) {
        String name = null;
        final JsonGetter jg = a.getAnnotation(JsonGetter.class);
        if (jg != null) {
            name = jg.value();
        }
        else {
            final JsonProperty pann = a.getAnnotation(JsonProperty.class);
            if (pann != null) {
                name = pann.value();
            }
            else {
                if (!a.hasAnnotation(JsonSerialize.class) && !a.hasAnnotation(JsonView.class)) {
                    return null;
                }
                name = "";
            }
        }
        if (name.length() == 0) {
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(name);
    }
    
    @Override
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        final JsonValue ann = am.getAnnotation(JsonValue.class);
        return ann != null && ann.value();
    }
    
    @Override
    public Class<? extends JsonDeserializer<?>> findDeserializer(final Annotated a) {
        final JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        if (ann != null) {
            final Class<? extends JsonDeserializer<?>> deserClass = ann.using();
            if (deserClass != JsonDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }
    
    @Override
    public Class<? extends KeyDeserializer> findKeyDeserializer(final Annotated a) {
        final JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        if (ann != null) {
            final Class<? extends KeyDeserializer> deserClass = ann.keyUsing();
            if (deserClass != KeyDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }
    
    @Override
    public Class<? extends JsonDeserializer<?>> findContentDeserializer(final Annotated a) {
        final JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        if (ann != null) {
            final Class<? extends JsonDeserializer<?>> deserClass = ann.contentUsing();
            if (deserClass != JsonDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }
    
    @Override
    public Class<?> findDeserializationType(final Annotated am, final JavaType baseType) {
        final JsonDeserialize ann = am.getAnnotation(JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.as());
    }
    
    @Override
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType) {
        final JsonDeserialize ann = am.getAnnotation(JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.keyAs());
    }
    
    @Override
    public Class<?> findDeserializationContentType(final Annotated am, final JavaType baseContentType) {
        final JsonDeserialize ann = am.getAnnotation(JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.contentAs());
    }
    
    @Override
    public Object findDeserializationConverter(final Annotated a) {
        final JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.converter(), Converter.None.class);
    }
    
    @Override
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        final JsonDeserialize ann = a.getAnnotation(JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.contentConverter(), Converter.None.class);
    }
    
    @Override
    public Object findValueInstantiator(final AnnotatedClass ac) {
        final JsonValueInstantiator ann = ac.getAnnotation(JsonValueInstantiator.class);
        return (ann == null) ? null : ann.value();
    }
    
    @Override
    public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
        final JsonDeserialize ann = ac.getAnnotation(JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.builder());
    }
    
    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
        final JsonPOJOBuilder ann = ac.getAnnotation(JsonPOJOBuilder.class);
        return (ann == null) ? null : new JsonPOJOBuilder.Value(ann);
    }
    
    @Override
    public PropertyName findNameForDeserialization(final Annotated a) {
        final JsonSetter js = a.getAnnotation(JsonSetter.class);
        String name;
        if (js != null) {
            name = js.value();
        }
        else {
            final JsonProperty pann = a.getAnnotation(JsonProperty.class);
            if (pann != null) {
                name = pann.value();
            }
            else {
                if (!a.hasAnnotation(JsonDeserialize.class) && !a.hasAnnotation(JsonView.class) && !a.hasAnnotation(JsonUnwrapped.class) && !a.hasAnnotation(JsonBackReference.class) && !a.hasAnnotation(JsonManagedReference.class)) {
                    return null;
                }
                name = "";
            }
        }
        if (name.length() == 0) {
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(name);
    }
    
    @Override
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return am.hasAnnotation(JsonAnySetter.class);
    }
    
    @Override
    public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
        return am.hasAnnotation(JsonAnyGetter.class);
    }
    
    @Override
    public boolean hasCreatorAnnotation(final Annotated a) {
        return a.hasAnnotation(JsonCreator.class);
    }
    
    protected boolean _isIgnorable(final Annotated a) {
        final JsonIgnore ann = a.getAnnotation(JsonIgnore.class);
        return ann != null && ann.value();
    }
    
    protected Class<?> _classIfExplicit(final Class<?> cls) {
        if (cls == null || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }
    
    protected Class<?> _classIfExplicit(Class<?> cls, final Class<?> implicit) {
        cls = this._classIfExplicit(cls);
        return (cls == null || cls == implicit) ? null : cls;
    }
    
    protected TypeResolverBuilder<?> _findTypeResolver(final MapperConfig<?> config, final Annotated ann, final JavaType baseType) {
        final JsonTypeInfo info = ann.getAnnotation(JsonTypeInfo.class);
        final JsonTypeResolver resAnn = ann.getAnnotation(JsonTypeResolver.class);
        TypeResolverBuilder<?> b;
        if (resAnn != null) {
            if (info == null) {
                return null;
            }
            b = config.typeResolverBuilderInstance(ann, resAnn.value());
        }
        else {
            if (info == null) {
                return null;
            }
            if (info.use() == JsonTypeInfo.Id.NONE) {
                return this._constructNoTypeResolverBuilder();
            }
            b = this._constructStdTypeResolverBuilder();
        }
        final JsonTypeIdResolver idResInfo = ann.getAnnotation(JsonTypeIdResolver.class);
        final TypeIdResolver idRes = (idResInfo == null) ? null : config.typeIdResolverInstance(ann, idResInfo.value());
        if (idRes != null) {
            idRes.init(baseType);
        }
        b = (TypeResolverBuilder<?>)b.init(info.use(), idRes);
        JsonTypeInfo.As inclusion = info.include();
        if (inclusion == JsonTypeInfo.As.EXTERNAL_PROPERTY && ann instanceof AnnotatedClass) {
            inclusion = JsonTypeInfo.As.PROPERTY;
        }
        b = (TypeResolverBuilder<?>)b.inclusion(inclusion);
        b = (TypeResolverBuilder<?>)b.typeProperty(info.property());
        final Class<?> defaultImpl = info.defaultImpl();
        if (defaultImpl != JsonTypeInfo.None.class) {
            b = (TypeResolverBuilder<?>)b.defaultImpl(defaultImpl);
        }
        b = (TypeResolverBuilder<?>)b.typeIdVisibility(info.visible());
        return b;
    }
    
    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new StdTypeResolverBuilder();
    }
    
    protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
        return StdTypeResolverBuilder.noTypeInfoBuilder();
    }
}
