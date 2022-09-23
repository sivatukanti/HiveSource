// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Named;

public abstract class BeanPropertyDefinition implements Named
{
    @Deprecated
    public BeanPropertyDefinition withName(final String newName) {
        return this.withSimpleName(newName);
    }
    
    public abstract BeanPropertyDefinition withName(final PropertyName p0);
    
    public abstract BeanPropertyDefinition withSimpleName(final String p0);
    
    @Override
    public abstract String getName();
    
    public abstract PropertyName getFullName();
    
    public abstract String getInternalName();
    
    public abstract PropertyName getWrapperName();
    
    public abstract PropertyMetadata getMetadata();
    
    public abstract boolean isExplicitlyIncluded();
    
    public boolean isExplicitlyNamed() {
        return this.isExplicitlyIncluded();
    }
    
    public boolean couldDeserialize() {
        return this.getMutator() != null;
    }
    
    public boolean couldSerialize() {
        return this.getAccessor() != null;
    }
    
    public abstract boolean hasGetter();
    
    public abstract boolean hasSetter();
    
    public abstract boolean hasField();
    
    public abstract boolean hasConstructorParameter();
    
    public abstract AnnotatedMethod getGetter();
    
    public abstract AnnotatedMethod getSetter();
    
    public abstract AnnotatedField getField();
    
    public abstract AnnotatedParameter getConstructorParameter();
    
    public abstract AnnotatedMember getAccessor();
    
    public abstract AnnotatedMember getMutator();
    
    public abstract AnnotatedMember getNonConstructorMutator();
    
    public AnnotatedMember getPrimaryMember() {
        return null;
    }
    
    public Class<?>[] findViews() {
        return null;
    }
    
    public AnnotationIntrospector.ReferenceProperty findReferenceType() {
        return null;
    }
    
    public boolean isTypeId() {
        return false;
    }
    
    public ObjectIdInfo findObjectIdInfo() {
        return null;
    }
    
    public final boolean isRequired() {
        final PropertyMetadata md = this.getMetadata();
        return md != null && md.isRequired();
    }
}
