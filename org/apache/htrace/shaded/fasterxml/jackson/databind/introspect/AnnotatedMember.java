// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collections;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.io.Serializable;

public abstract class AnnotatedMember extends Annotated implements Serializable
{
    private static final long serialVersionUID = 7364428299211355871L;
    protected final transient AnnotationMap _annotations;
    
    protected AnnotatedMember(final AnnotationMap annotations) {
        this._annotations = annotations;
    }
    
    public abstract Class<?> getDeclaringClass();
    
    public abstract Member getMember();
    
    @Override
    public Iterable<Annotation> annotations() {
        if (this._annotations == null) {
            return (Iterable<Annotation>)Collections.emptyList();
        }
        return this._annotations.annotations();
    }
    
    @Override
    protected AnnotationMap getAllAnnotations() {
        return this._annotations;
    }
    
    public final void addOrOverride(final Annotation a) {
        this._annotations.add(a);
    }
    
    public final void addIfNotPresent(final Annotation a) {
        this._annotations.addIfNotPresent(a);
    }
    
    public final void fixAccess() {
        ClassUtil.checkAndFixAccess(this.getMember());
    }
    
    public abstract void setValue(final Object p0, final Object p1) throws UnsupportedOperationException, IllegalArgumentException;
    
    public abstract Object getValue(final Object p0) throws UnsupportedOperationException, IllegalArgumentException;
}
