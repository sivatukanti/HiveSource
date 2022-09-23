// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;

public class PropertyBuilder
{
    protected final SerializationConfig _config;
    protected final BeanDescription _beanDesc;
    protected final JsonInclude.Include _outputProps;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected Object _defaultBean;
    
    public PropertyBuilder(final SerializationConfig config, final BeanDescription beanDesc) {
        this._config = config;
        this._beanDesc = beanDesc;
        this._outputProps = beanDesc.findSerializationInclusion(config.getSerializationInclusion());
        this._annotationIntrospector = this._config.getAnnotationIntrospector();
    }
    
    public Annotations getClassAnnotations() {
        return this._beanDesc.getClassAnnotations();
    }
    
    @Deprecated
    protected final BeanPropertyWriter buildWriter(final BeanPropertyDefinition propDef, final JavaType declaredType, final JsonSerializer<?> ser, final TypeSerializer typeSer, final TypeSerializer contentTypeSer, final AnnotatedMember am, final boolean defaultUseStaticTyping) {
        throw new IllegalStateException();
    }
    
    protected BeanPropertyWriter buildWriter(final SerializerProvider prov, final BeanPropertyDefinition propDef, final JavaType declaredType, final JsonSerializer<?> ser, final TypeSerializer typeSer, final TypeSerializer contentTypeSer, final AnnotatedMember am, final boolean defaultUseStaticTyping) throws JsonMappingException {
        JavaType serializationType = this.findSerializationType(am, defaultUseStaticTyping, declaredType);
        if (contentTypeSer != null) {
            if (serializationType == null) {
                serializationType = declaredType;
            }
            JavaType ct = serializationType.getContentType();
            if (ct == null) {
                throw new IllegalStateException("Problem trying to create BeanPropertyWriter for property '" + propDef.getName() + "' (of type " + this._beanDesc.getType() + "); serialization type " + serializationType + " has no content");
            }
            serializationType = serializationType.withContentTypeHandler(contentTypeSer);
            ct = serializationType.getContentType();
        }
        Object valueToSuppress = null;
        boolean suppressNulls = false;
        final JsonInclude.Include methodProps = this._annotationIntrospector.findSerializationInclusion(am, this._outputProps);
        if (methodProps != null) {
            switch (methodProps) {
                case NON_DEFAULT: {
                    valueToSuppress = this.getDefaultValue(propDef.getName(), am);
                    if (valueToSuppress == null) {
                        suppressNulls = true;
                        break;
                    }
                    if (valueToSuppress.getClass().isArray()) {
                        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                        break;
                    }
                    break;
                }
                case NON_EMPTY: {
                    suppressNulls = true;
                    valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
                    break;
                }
                case NON_NULL: {
                    suppressNulls = true;
                }
                case ALWAYS: {
                    if (declaredType.isContainerType() && !this._config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) {
                        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
                        break;
                    }
                    break;
                }
            }
        }
        BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, this._beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress);
        final Object serDef = this._annotationIntrospector.findNullSerializer(am);
        if (serDef != null) {
            bpw.assignNullSerializer(prov.serializerInstance(am, serDef));
        }
        final NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
        if (unwrapper != null) {
            bpw = bpw.unwrappingWriter(unwrapper);
        }
        return bpw;
    }
    
    protected JavaType findSerializationType(final Annotated a, boolean useStaticTyping, JavaType declaredType) {
        final Class<?> serClass = this._annotationIntrospector.findSerializationType(a);
        if (serClass != null) {
            final Class<?> rawDeclared = declaredType.getRawClass();
            if (serClass.isAssignableFrom(rawDeclared)) {
                declaredType = declaredType.widenBy(serClass);
            }
            else {
                if (!rawDeclared.isAssignableFrom(serClass)) {
                    throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
                }
                declaredType = this._config.constructSpecializedType(declaredType, serClass);
            }
            useStaticTyping = true;
        }
        final JavaType secondary = BasicSerializerFactory.modifySecondaryTypesByAnnotation(this._config, a, declaredType);
        if (secondary != declaredType) {
            useStaticTyping = true;
            declaredType = secondary;
        }
        final JsonSerialize.Typing typing = this._annotationIntrospector.findSerializationTyping(a);
        if (typing != null && typing != JsonSerialize.Typing.DEFAULT_TYPING) {
            useStaticTyping = (typing == JsonSerialize.Typing.STATIC);
        }
        return useStaticTyping ? declaredType : null;
    }
    
    protected Object getDefaultBean() {
        if (this._defaultBean == null) {
            this._defaultBean = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
            if (this._defaultBean == null) {
                final Class<?> cls = this._beanDesc.getClassInfo().getAnnotated();
                throw new IllegalArgumentException("Class " + cls.getName() + " has no default constructor; can not instantiate default bean value to support 'properties=JsonSerialize.Inclusion.NON_DEFAULT' annotation");
            }
        }
        return this._defaultBean;
    }
    
    protected Object getDefaultValue(final String name, final AnnotatedMember member) {
        final Object defaultBean = this.getDefaultBean();
        try {
            return member.getValue(defaultBean);
        }
        catch (Exception e) {
            return this._throwWrapped(e, name, defaultBean);
        }
    }
    
    protected Object _throwWrapped(final Exception e, final String propName, final Object defaultBean) {
        Throwable t;
        for (t = e; t.getCause() != null; t = t.getCause()) {}
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
    }
}
