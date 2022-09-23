// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Named;

public interface BeanProperty extends Named
{
    String getName();
    
    PropertyName getFullName();
    
    JavaType getType();
    
    PropertyName getWrapperName();
    
    PropertyMetadata getMetadata();
    
    boolean isRequired();
    
     <A extends Annotation> A getAnnotation(final Class<A> p0);
    
     <A extends Annotation> A getContextAnnotation(final Class<A> p0);
    
    AnnotatedMember getMember();
    
    void depositSchemaProperty(final JsonObjectFormatVisitor p0) throws JsonMappingException;
    
    public static class Std implements BeanProperty
    {
        protected final PropertyName _name;
        protected final JavaType _type;
        protected final PropertyName _wrapperName;
        protected final PropertyMetadata _metadata;
        protected final AnnotatedMember _member;
        protected final Annotations _contextAnnotations;
        
        public Std(final PropertyName name, final JavaType type, final PropertyName wrapperName, final Annotations contextAnnotations, final AnnotatedMember member, final PropertyMetadata metadata) {
            this._name = name;
            this._type = type;
            this._wrapperName = wrapperName;
            this._metadata = metadata;
            this._member = member;
            this._contextAnnotations = contextAnnotations;
        }
        
        @Deprecated
        public Std(final String name, final JavaType type, final PropertyName wrapperName, final Annotations contextAnnotations, final AnnotatedMember member, final boolean isRequired) {
            this(new PropertyName(name), type, wrapperName, contextAnnotations, member, isRequired ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL);
        }
        
        public Std withType(final JavaType type) {
            return new Std(this._name, type, this._wrapperName, this._contextAnnotations, this._member, this._metadata);
        }
        
        @Override
        public <A extends Annotation> A getAnnotation(final Class<A> acls) {
            return (A)((this._member == null) ? null : this._member.getAnnotation(acls));
        }
        
        @Override
        public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
            return (A)((this._contextAnnotations == null) ? null : this._contextAnnotations.get(acls));
        }
        
        @Override
        public String getName() {
            return this._name.getSimpleName();
        }
        
        @Override
        public PropertyName getFullName() {
            return this._name;
        }
        
        @Override
        public JavaType getType() {
            return this._type;
        }
        
        @Override
        public PropertyName getWrapperName() {
            return this._wrapperName;
        }
        
        @Override
        public boolean isRequired() {
            return this._metadata.isRequired();
        }
        
        @Override
        public PropertyMetadata getMetadata() {
            return this._metadata;
        }
        
        @Override
        public AnnotatedMember getMember() {
            return this._member;
        }
        
        @Override
        public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor) {
            throw new UnsupportedOperationException("Instances of " + this.getClass().getName() + " should not get visited");
        }
    }
}
