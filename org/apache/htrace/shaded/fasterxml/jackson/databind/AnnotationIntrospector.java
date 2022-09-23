// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import java.util.Collections;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.Versioned;

public abstract class AnnotationIntrospector implements Versioned, Serializable
{
    public static AnnotationIntrospector nopInstance() {
        return NopAnnotationIntrospector.instance;
    }
    
    public static AnnotationIntrospector pair(final AnnotationIntrospector a1, final AnnotationIntrospector a2) {
        return new AnnotationIntrospectorPair(a1, a2);
    }
    
    public Collection<AnnotationIntrospector> allIntrospectors() {
        return Collections.singletonList(this);
    }
    
    public Collection<AnnotationIntrospector> allIntrospectors(final Collection<AnnotationIntrospector> result) {
        result.add(this);
        return result;
    }
    
    @Override
    public abstract Version version();
    
    public boolean isAnnotationBundle(final Annotation ann) {
        return false;
    }
    
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        return null;
    }
    
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, final ObjectIdInfo objectIdInfo) {
        return objectIdInfo;
    }
    
    public PropertyName findRootName(final AnnotatedClass ac) {
        return null;
    }
    
    public String[] findPropertiesToIgnore(final Annotated ac) {
        return null;
    }
    
    public Boolean findIgnoreUnknownProperties(final AnnotatedClass ac) {
        return null;
    }
    
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        return null;
    }
    
    @Deprecated
    public Object findFilterId(final AnnotatedClass ac) {
        return this.findFilterId((Annotated)ac);
    }
    
    public Object findFilterId(final Annotated ann) {
        return null;
    }
    
    public Object findNamingStrategy(final AnnotatedClass ac) {
        return null;
    }
    
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, final VisibilityChecker<?> checker) {
        return checker;
    }
    
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        return null;
    }
    
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        return null;
    }
    
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType containerType) {
        return null;
    }
    
    public List<NamedType> findSubtypes(final Annotated a) {
        return null;
    }
    
    public String findTypeName(final AnnotatedClass ac) {
        return null;
    }
    
    public ReferenceProperty findReferenceType(final AnnotatedMember member) {
        return null;
    }
    
    public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
        return null;
    }
    
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return false;
    }
    
    public Object findInjectableValueId(final AnnotatedMember m) {
        return null;
    }
    
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        return null;
    }
    
    public Class<?>[] findViews(final Annotated a) {
        return null;
    }
    
    public JsonFormat.Value findFormat(final Annotated memberOrClass) {
        return null;
    }
    
    public Boolean isTypeId(final AnnotatedMember member) {
        return null;
    }
    
    public PropertyName findWrapperName(final Annotated ann) {
        return null;
    }
    
    public String findPropertyDescription(final Annotated ann) {
        return null;
    }
    
    public Integer findPropertyIndex(final Annotated ann) {
        return null;
    }
    
    public String findImplicitPropertyName(final AnnotatedMember member) {
        return null;
    }
    
    public Object findSerializer(final Annotated am) {
        return null;
    }
    
    public Object findKeySerializer(final Annotated am) {
        return null;
    }
    
    public Object findContentSerializer(final Annotated am) {
        return null;
    }
    
    public Object findNullSerializer(final Annotated am) {
        return null;
    }
    
    public JsonInclude.Include findSerializationInclusion(final Annotated a, final JsonInclude.Include defValue) {
        return defValue;
    }
    
    public Class<?> findSerializationType(final Annotated a) {
        return null;
    }
    
    public Class<?> findSerializationKeyType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    public Class<?> findSerializationContentType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        return null;
    }
    
    public Object findSerializationConverter(final Annotated a) {
        return null;
    }
    
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        return null;
    }
    
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        return null;
    }
    
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        return null;
    }
    
    @Deprecated
    public Boolean findSerializationSortAlphabetically(final AnnotatedClass ac) {
        return null;
    }
    
    public PropertyName findNameForSerialization(final Annotated a) {
        return null;
    }
    
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    public String findEnumValue(final Enum<?> value) {
        return value.name();
    }
    
    public Object findDeserializer(final Annotated am) {
        return null;
    }
    
    public Object findKeyDeserializer(final Annotated am) {
        return null;
    }
    
    public Object findContentDeserializer(final Annotated am) {
        return null;
    }
    
    public Class<?> findDeserializationType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType) {
        return null;
    }
    
    public Class<?> findDeserializationContentType(final Annotated am, final JavaType baseContentType) {
        return null;
    }
    
    public Object findDeserializationConverter(final Annotated a) {
        return null;
    }
    
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        return null;
    }
    
    public Object findValueInstantiator(final AnnotatedClass ac) {
        return null;
    }
    
    public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
        return null;
    }
    
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
        return null;
    }
    
    public PropertyName findNameForDeserialization(final Annotated a) {
        return null;
    }
    
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    public boolean hasCreatorAnnotation(final Annotated a) {
        return false;
    }
    
    public static class ReferenceProperty
    {
        private final Type _type;
        private final String _name;
        
        public ReferenceProperty(final Type t, final String n) {
            this._type = t;
            this._name = n;
        }
        
        public static ReferenceProperty managed(final String name) {
            return new ReferenceProperty(Type.MANAGED_REFERENCE, name);
        }
        
        public static ReferenceProperty back(final String name) {
            return new ReferenceProperty(Type.BACK_REFERENCE, name);
        }
        
        public Type getType() {
            return this._type;
        }
        
        public String getName() {
            return this._name;
        }
        
        public boolean isManagedReference() {
            return this._type == Type.MANAGED_REFERENCE;
        }
        
        public boolean isBackReference() {
            return this._type == Type.BACK_REFERENCE;
        }
        
        public enum Type
        {
            MANAGED_REFERENCE, 
            BACK_REFERENCE;
        }
    }
}
