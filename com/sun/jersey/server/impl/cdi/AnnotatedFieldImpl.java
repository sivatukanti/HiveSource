// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.reflect.Member;
import javax.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import javax.enterprise.inject.spi.AnnotatedField;

public class AnnotatedFieldImpl<T> extends AnnotatedMemberImpl<T> implements AnnotatedField<T>
{
    private Field javaMember;
    
    public AnnotatedFieldImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final AnnotatedType<T> declaringType, final Field javaMember, final boolean isStatic) {
        super(baseType, typeClosure, annotations, declaringType, javaMember, isStatic);
        this.javaMember = javaMember;
    }
    
    public AnnotatedFieldImpl(final AnnotatedField<? super T> field, final AnnotatedType<T> declaringType) {
        this(field.getBaseType(), field.getTypeClosure(), field.getAnnotations(), declaringType, field.getJavaMember(), field.isStatic());
    }
    
    public AnnotatedFieldImpl(final AnnotatedField<? super T> field, final Set<Annotation> annotations, final AnnotatedType<T> declaringType) {
        this(field.getBaseType(), field.getTypeClosure(), annotations, declaringType, field.getJavaMember(), field.isStatic());
    }
    
    @Override
    public Field getJavaMember() {
        return this.javaMember;
    }
}
