// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

public class SimpleBeanPropertyDefinition extends BeanPropertyDefinition
{
    protected final AnnotationIntrospector _introspector;
    protected final AnnotatedMember _member;
    protected final String _name;
    
    @Deprecated
    public SimpleBeanPropertyDefinition(final AnnotatedMember member) {
        this(member, member.getName(), null);
    }
    
    @Deprecated
    public SimpleBeanPropertyDefinition(final AnnotatedMember member, final String name) {
        this(member, name, null);
    }
    
    private SimpleBeanPropertyDefinition(final AnnotatedMember member, final String name, final AnnotationIntrospector intr) {
        this._introspector = intr;
        this._member = member;
        this._name = name;
    }
    
    public static SimpleBeanPropertyDefinition construct(final MapperConfig<?> config, final AnnotatedMember member) {
        return new SimpleBeanPropertyDefinition(member, member.getName(), (config == null) ? null : config.getAnnotationIntrospector());
    }
    
    public static SimpleBeanPropertyDefinition construct(final MapperConfig<?> config, final AnnotatedMember member, final String name) {
        return new SimpleBeanPropertyDefinition(member, name, (config == null) ? null : config.getAnnotationIntrospector());
    }
    
    @Deprecated
    @Override
    public SimpleBeanPropertyDefinition withName(final String newName) {
        return this.withSimpleName(newName);
    }
    
    @Override
    public SimpleBeanPropertyDefinition withSimpleName(final String newName) {
        if (this._name.equals(newName)) {
            return this;
        }
        return new SimpleBeanPropertyDefinition(this._member, newName, this._introspector);
    }
    
    @Override
    public SimpleBeanPropertyDefinition withName(final PropertyName newName) {
        return this.withSimpleName(newName.getSimpleName());
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    @Override
    public PropertyName getFullName() {
        return new PropertyName(this._name);
    }
    
    @Override
    public String getInternalName() {
        return this.getName();
    }
    
    @Override
    public PropertyName getWrapperName() {
        return (this._introspector == null) ? null : this._introspector.findWrapperName(this._member);
    }
    
    @Override
    public boolean isExplicitlyIncluded() {
        return false;
    }
    
    @Override
    public boolean isExplicitlyNamed() {
        return false;
    }
    
    @Override
    public PropertyMetadata getMetadata() {
        return PropertyMetadata.STD_OPTIONAL;
    }
    
    @Override
    public boolean hasGetter() {
        return this.getGetter() != null;
    }
    
    @Override
    public boolean hasSetter() {
        return this.getSetter() != null;
    }
    
    @Override
    public boolean hasField() {
        return this._member instanceof AnnotatedField;
    }
    
    @Override
    public boolean hasConstructorParameter() {
        return this._member instanceof AnnotatedParameter;
    }
    
    @Override
    public AnnotatedMethod getGetter() {
        if (this._member instanceof AnnotatedMethod && ((AnnotatedMethod)this._member).getParameterCount() == 0) {
            return (AnnotatedMethod)this._member;
        }
        return null;
    }
    
    @Override
    public AnnotatedMethod getSetter() {
        if (this._member instanceof AnnotatedMethod && ((AnnotatedMethod)this._member).getParameterCount() == 1) {
            return (AnnotatedMethod)this._member;
        }
        return null;
    }
    
    @Override
    public AnnotatedField getField() {
        return (this._member instanceof AnnotatedField) ? ((AnnotatedField)this._member) : null;
    }
    
    @Override
    public AnnotatedParameter getConstructorParameter() {
        return (this._member instanceof AnnotatedParameter) ? ((AnnotatedParameter)this._member) : null;
    }
    
    @Override
    public AnnotatedMember getAccessor() {
        AnnotatedMember acc = this.getGetter();
        if (acc == null) {
            acc = this.getField();
        }
        return acc;
    }
    
    @Override
    public AnnotatedMember getMutator() {
        AnnotatedMember acc = this.getConstructorParameter();
        if (acc == null) {
            acc = this.getSetter();
            if (acc == null) {
                acc = this.getField();
            }
        }
        return acc;
    }
    
    @Override
    public AnnotatedMember getNonConstructorMutator() {
        AnnotatedMember acc = this.getSetter();
        if (acc == null) {
            acc = this.getField();
        }
        return acc;
    }
    
    @Override
    public AnnotatedMember getPrimaryMember() {
        return this._member;
    }
}
