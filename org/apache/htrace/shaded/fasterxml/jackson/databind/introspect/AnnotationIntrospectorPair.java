// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;

public class AnnotationIntrospectorPair extends AnnotationIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final AnnotationIntrospector _primary;
    protected final AnnotationIntrospector _secondary;
    
    public AnnotationIntrospectorPair(final AnnotationIntrospector p, final AnnotationIntrospector s) {
        this._primary = p;
        this._secondary = s;
    }
    
    @Override
    public Version version() {
        return this._primary.version();
    }
    
    public static AnnotationIntrospector create(final AnnotationIntrospector primary, final AnnotationIntrospector secondary) {
        if (primary == null) {
            return secondary;
        }
        if (secondary == null) {
            return primary;
        }
        return new AnnotationIntrospectorPair(primary, secondary);
    }
    
    @Override
    public Collection<AnnotationIntrospector> allIntrospectors() {
        return this.allIntrospectors(new ArrayList<AnnotationIntrospector>());
    }
    
    @Override
    public Collection<AnnotationIntrospector> allIntrospectors(final Collection<AnnotationIntrospector> result) {
        this._primary.allIntrospectors(result);
        this._secondary.allIntrospectors(result);
        return result;
    }
    
    @Override
    public boolean isAnnotationBundle(final Annotation ann) {
        return this._primary.isAnnotationBundle(ann) || this._secondary.isAnnotationBundle(ann);
    }
    
    @Override
    public PropertyName findRootName(final AnnotatedClass ac) {
        final PropertyName name1 = this._primary.findRootName(ac);
        if (name1 == null) {
            return this._secondary.findRootName(ac);
        }
        if (name1.hasSimpleName()) {
            return name1;
        }
        final PropertyName name2 = this._secondary.findRootName(ac);
        return (name2 == null) ? name1 : name2;
    }
    
    @Override
    public String[] findPropertiesToIgnore(final Annotated ac) {
        String[] result = this._primary.findPropertiesToIgnore(ac);
        if (result == null) {
            result = this._secondary.findPropertiesToIgnore(ac);
        }
        return result;
    }
    
    @Override
    public Boolean findIgnoreUnknownProperties(final AnnotatedClass ac) {
        Boolean result = this._primary.findIgnoreUnknownProperties(ac);
        if (result == null) {
            result = this._secondary.findIgnoreUnknownProperties(ac);
        }
        return result;
    }
    
    @Override
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        Boolean result = this._primary.isIgnorableType(ac);
        if (result == null) {
            result = this._secondary.isIgnorableType(ac);
        }
        return result;
    }
    
    @Deprecated
    @Override
    public Object findFilterId(final AnnotatedClass ac) {
        Object id = this._primary.findFilterId(ac);
        if (id == null) {
            id = this._secondary.findFilterId(ac);
        }
        return id;
    }
    
    @Override
    public Object findFilterId(final Annotated ann) {
        Object id = this._primary.findFilterId(ann);
        if (id == null) {
            id = this._secondary.findFilterId(ann);
        }
        return id;
    }
    
    @Override
    public Object findNamingStrategy(final AnnotatedClass ac) {
        Object str = this._primary.findNamingStrategy(ac);
        if (str == null) {
            str = this._secondary.findNamingStrategy(ac);
        }
        return str;
    }
    
    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, VisibilityChecker<?> checker) {
        checker = this._secondary.findAutoDetectVisibility(ac, checker);
        return this._primary.findAutoDetectVisibility(ac, checker);
    }
    
    @Override
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findTypeResolver(config, ac, baseType);
        if (b == null) {
            b = this._secondary.findTypeResolver(config, ac, baseType);
        }
        return b;
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findPropertyTypeResolver(config, am, baseType);
        if (b == null) {
            b = this._secondary.findPropertyTypeResolver(config, am, baseType);
        }
        return b;
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findPropertyContentTypeResolver(config, am, baseType);
        if (b == null) {
            b = this._secondary.findPropertyContentTypeResolver(config, am, baseType);
        }
        return b;
    }
    
    @Override
    public List<NamedType> findSubtypes(final Annotated a) {
        final List<NamedType> types1 = this._primary.findSubtypes(a);
        final List<NamedType> types2 = this._secondary.findSubtypes(a);
        if (types1 == null || types1.isEmpty()) {
            return types2;
        }
        if (types2 == null || types2.isEmpty()) {
            return types1;
        }
        final ArrayList<NamedType> result = new ArrayList<NamedType>(types1.size() + types2.size());
        result.addAll(types1);
        result.addAll(types2);
        return result;
    }
    
    @Override
    public String findTypeName(final AnnotatedClass ac) {
        String name = this._primary.findTypeName(ac);
        if (name == null || name.length() == 0) {
            name = this._secondary.findTypeName(ac);
        }
        return name;
    }
    
    @Override
    public ReferenceProperty findReferenceType(final AnnotatedMember member) {
        final ReferenceProperty r = this._primary.findReferenceType(member);
        return (r == null) ? this._secondary.findReferenceType(member) : r;
    }
    
    @Override
    public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
        final NameTransformer r = this._primary.findUnwrappingNameTransformer(member);
        return (r == null) ? this._secondary.findUnwrappingNameTransformer(member) : r;
    }
    
    @Override
    public Object findInjectableValueId(final AnnotatedMember m) {
        final Object r = this._primary.findInjectableValueId(m);
        return (r == null) ? this._secondary.findInjectableValueId(m) : r;
    }
    
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return this._primary.hasIgnoreMarker(m) || this._secondary.hasIgnoreMarker(m);
    }
    
    @Override
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        final Boolean r = this._primary.hasRequiredMarker(m);
        return (r == null) ? this._secondary.hasRequiredMarker(m) : r;
    }
    
    @Override
    public Object findSerializer(final Annotated am) {
        final Object r = this._primary.findSerializer(am);
        return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._secondary.findSerializer(am);
    }
    
    @Override
    public Object findKeySerializer(final Annotated a) {
        final Object r = this._primary.findKeySerializer(a);
        return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._secondary.findKeySerializer(a);
    }
    
    @Override
    public Object findContentSerializer(final Annotated a) {
        final Object r = this._primary.findContentSerializer(a);
        return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._secondary.findContentSerializer(a);
    }
    
    @Override
    public Object findNullSerializer(final Annotated a) {
        final Object r = this._primary.findNullSerializer(a);
        return this._isExplicitClassOrOb(r, JsonSerializer.None.class) ? r : this._secondary.findNullSerializer(a);
    }
    
    @Override
    public JsonInclude.Include findSerializationInclusion(final Annotated a, JsonInclude.Include defValue) {
        defValue = this._secondary.findSerializationInclusion(a, defValue);
        defValue = this._primary.findSerializationInclusion(a, defValue);
        return defValue;
    }
    
    @Override
    public Class<?> findSerializationType(final Annotated a) {
        final Class<?> r = this._primary.findSerializationType(a);
        return (r == null) ? this._secondary.findSerializationType(a) : r;
    }
    
    @Override
    public Class<?> findSerializationKeyType(final Annotated am, final JavaType baseType) {
        final Class<?> r = this._primary.findSerializationKeyType(am, baseType);
        return (r == null) ? this._secondary.findSerializationKeyType(am, baseType) : r;
    }
    
    @Override
    public Class<?> findSerializationContentType(final Annotated am, final JavaType baseType) {
        final Class<?> r = this._primary.findSerializationContentType(am, baseType);
        return (r == null) ? this._secondary.findSerializationContentType(am, baseType) : r;
    }
    
    @Override
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        final JsonSerialize.Typing r = this._primary.findSerializationTyping(a);
        return (r == null) ? this._secondary.findSerializationTyping(a) : r;
    }
    
    @Override
    public Object findSerializationConverter(final Annotated a) {
        final Object r = this._primary.findSerializationConverter(a);
        return (r == null) ? this._secondary.findSerializationConverter(a) : r;
    }
    
    @Override
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        final Object r = this._primary.findSerializationContentConverter(a);
        return (r == null) ? this._secondary.findSerializationContentConverter(a) : r;
    }
    
    @Override
    public Class<?>[] findViews(final Annotated a) {
        Class<?>[] result = this._primary.findViews(a);
        if (result == null) {
            result = this._secondary.findViews(a);
        }
        return result;
    }
    
    @Override
    public Boolean isTypeId(final AnnotatedMember member) {
        final Boolean b = this._primary.isTypeId(member);
        return (b == null) ? this._secondary.isTypeId(member) : b;
    }
    
    @Override
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        final ObjectIdInfo r = this._primary.findObjectIdInfo(ann);
        return (r == null) ? this._secondary.findObjectIdInfo(ann) : r;
    }
    
    @Override
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, ObjectIdInfo objectIdInfo) {
        objectIdInfo = this._secondary.findObjectReferenceInfo(ann, objectIdInfo);
        objectIdInfo = this._primary.findObjectReferenceInfo(ann, objectIdInfo);
        return objectIdInfo;
    }
    
    @Override
    public JsonFormat.Value findFormat(final Annotated ann) {
        final JsonFormat.Value r = this._primary.findFormat(ann);
        return (r == null) ? this._secondary.findFormat(ann) : r;
    }
    
    @Override
    public PropertyName findWrapperName(final Annotated ann) {
        PropertyName name = this._primary.findWrapperName(ann);
        if (name == null) {
            name = this._secondary.findWrapperName(ann);
        }
        else if (name == PropertyName.USE_DEFAULT) {
            final PropertyName name2 = this._secondary.findWrapperName(ann);
            if (name2 != null) {
                name = name2;
            }
        }
        return name;
    }
    
    @Override
    public String findPropertyDescription(final Annotated ann) {
        final String r = this._primary.findPropertyDescription(ann);
        return (r == null) ? this._secondary.findPropertyDescription(ann) : r;
    }
    
    @Override
    public Integer findPropertyIndex(final Annotated ann) {
        final Integer r = this._primary.findPropertyIndex(ann);
        return (r == null) ? this._secondary.findPropertyIndex(ann) : r;
    }
    
    @Override
    public String findImplicitPropertyName(final AnnotatedMember param) {
        final String r = this._primary.findImplicitPropertyName(param);
        return (r == null) ? this._secondary.findImplicitPropertyName(param) : r;
    }
    
    @Override
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        final String[] r = this._primary.findSerializationPropertyOrder(ac);
        return (r == null) ? this._secondary.findSerializationPropertyOrder(ac) : r;
    }
    
    @Deprecated
    @Override
    public Boolean findSerializationSortAlphabetically(final AnnotatedClass ac) {
        final Boolean r = this._primary.findSerializationSortAlphabetically(ac);
        return (r == null) ? this._secondary.findSerializationSortAlphabetically(ac) : r;
    }
    
    @Override
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        final Boolean r = this._primary.findSerializationSortAlphabetically(ann);
        return (r == null) ? this._secondary.findSerializationSortAlphabetically(ann) : r;
    }
    
    @Override
    public PropertyName findNameForSerialization(final Annotated a) {
        PropertyName n = this._primary.findNameForSerialization(a);
        if (n == null) {
            n = this._secondary.findNameForSerialization(a);
        }
        else if (n == PropertyName.USE_DEFAULT) {
            final PropertyName n2 = this._secondary.findNameForSerialization(a);
            if (n2 != null) {
                n = n2;
            }
        }
        return n;
    }
    
    @Override
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        return this._primary.hasAsValueAnnotation(am) || this._secondary.hasAsValueAnnotation(am);
    }
    
    @Override
    public String findEnumValue(final Enum<?> value) {
        final String r = this._primary.findEnumValue(value);
        return (r == null) ? this._secondary.findEnumValue(value) : r;
    }
    
    @Override
    public Object findDeserializer(final Annotated am) {
        final Object r = this._primary.findDeserializer(am);
        return this._isExplicitClassOrOb(r, JsonDeserializer.None.class) ? r : this._secondary.findDeserializer(am);
    }
    
    @Override
    public Object findKeyDeserializer(final Annotated am) {
        final Object r = this._primary.findKeyDeserializer(am);
        return this._isExplicitClassOrOb(r, KeyDeserializer.None.class) ? r : this._secondary.findKeyDeserializer(am);
    }
    
    @Override
    public Object findContentDeserializer(final Annotated am) {
        final Object r = this._primary.findContentDeserializer(am);
        return this._isExplicitClassOrOb(r, JsonDeserializer.None.class) ? r : this._secondary.findContentDeserializer(am);
    }
    
    @Override
    public Class<?> findDeserializationType(final Annotated am, final JavaType baseType) {
        final Class<?> r = this._primary.findDeserializationType(am, baseType);
        return (r != null) ? r : this._secondary.findDeserializationType(am, baseType);
    }
    
    @Override
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType) {
        final Class<?> result = this._primary.findDeserializationKeyType(am, baseKeyType);
        return (result == null) ? this._secondary.findDeserializationKeyType(am, baseKeyType) : result;
    }
    
    @Override
    public Class<?> findDeserializationContentType(final Annotated am, final JavaType baseContentType) {
        final Class<?> result = this._primary.findDeserializationContentType(am, baseContentType);
        return (result == null) ? this._secondary.findDeserializationContentType(am, baseContentType) : result;
    }
    
    @Override
    public Object findDeserializationConverter(final Annotated a) {
        final Object ob = this._primary.findDeserializationConverter(a);
        return (ob == null) ? this._secondary.findDeserializationConverter(a) : ob;
    }
    
    @Override
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        final Object ob = this._primary.findDeserializationContentConverter(a);
        return (ob == null) ? this._secondary.findDeserializationContentConverter(a) : ob;
    }
    
    @Override
    public Object findValueInstantiator(final AnnotatedClass ac) {
        final Object result = this._primary.findValueInstantiator(ac);
        return (result == null) ? this._secondary.findValueInstantiator(ac) : result;
    }
    
    @Override
    public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
        final Class<?> result = this._primary.findPOJOBuilder(ac);
        return (result == null) ? this._secondary.findPOJOBuilder(ac) : result;
    }
    
    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
        final JsonPOJOBuilder.Value result = this._primary.findPOJOBuilderConfig(ac);
        return (result == null) ? this._secondary.findPOJOBuilderConfig(ac) : result;
    }
    
    @Override
    public PropertyName findNameForDeserialization(final Annotated a) {
        PropertyName n = this._primary.findNameForDeserialization(a);
        if (n == null) {
            n = this._secondary.findNameForDeserialization(a);
        }
        else if (n == PropertyName.USE_DEFAULT) {
            final PropertyName n2 = this._secondary.findNameForDeserialization(a);
            if (n2 != null) {
                n = n2;
            }
        }
        return n;
    }
    
    @Override
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return this._primary.hasAnySetterAnnotation(am) || this._secondary.hasAnySetterAnnotation(am);
    }
    
    @Override
    public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
        return this._primary.hasAnyGetterAnnotation(am) || this._secondary.hasAnyGetterAnnotation(am);
    }
    
    @Override
    public boolean hasCreatorAnnotation(final Annotated a) {
        return this._primary.hasCreatorAnnotation(a) || this._secondary.hasCreatorAnnotation(a);
    }
    
    protected boolean _isExplicitClassOrOb(final Object maybeCls, final Class<?> implicit) {
        if (maybeCls == null) {
            return false;
        }
        if (!(maybeCls instanceof Class)) {
            return true;
        }
        final Class<?> cls = (Class<?>)maybeCls;
        return cls != implicit && !ClassUtil.isBogusClass(cls);
    }
}
