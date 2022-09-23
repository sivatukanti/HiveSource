// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;
import java.lang.reflect.Member;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.AnnotatedMember;

public class AnnotatedMemberImpl<T> extends AnnotatedImpl implements AnnotatedMember<T>
{
    private AnnotatedType<T> declaringType;
    private Member javaMember;
    private boolean isStatic;
    
    public AnnotatedMemberImpl(final Type baseType, final Set<Type> typeClosure, final Set<Annotation> annotations, final AnnotatedType<T> declaringType, final Member javaMember, final boolean isStatic) {
        super(baseType, typeClosure, annotations);
        this.declaringType = declaringType;
        this.javaMember = javaMember;
        this.isStatic = isStatic;
    }
    
    public AnnotatedType<T> getDeclaringType() {
        return this.declaringType;
    }
    
    public Member getJavaMember() {
        return this.javaMember;
    }
    
    public boolean isStatic() {
        return this.isStatic;
    }
}
