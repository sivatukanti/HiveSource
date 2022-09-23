// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$MapMaker;
import java.util.Map;
import com.google.inject.internal.util.$Function;
import javax.inject.Qualifier;
import com.google.inject.BindingAnnotation;
import java.util.Collection;
import java.util.Arrays;
import javax.inject.Scope;
import com.google.inject.ScopeAnnotation;
import com.google.inject.name.Names;
import javax.inject.Named;
import com.google.inject.Key;
import java.lang.reflect.Member;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.$Classes;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

public class Annotations
{
    private static final AnnotationChecker scopeChecker;
    private static final AnnotationChecker bindingAnnotationChecker;
    
    public static boolean isMarker(final Class<? extends Annotation> annotationType) {
        return annotationType.getDeclaredMethods().length == 0;
    }
    
    public static boolean isRetainedAtRuntime(final Class<? extends Annotation> annotationType) {
        final Retention retention = annotationType.getAnnotation(Retention.class);
        return retention != null && retention.value() == RetentionPolicy.RUNTIME;
    }
    
    public static Class<? extends Annotation> findScopeAnnotation(final Errors errors, final Class<?> implementation) {
        return findScopeAnnotation(errors, implementation.getAnnotations());
    }
    
    public static Class<? extends Annotation> findScopeAnnotation(final Errors errors, final Annotation[] annotations) {
        Class<? extends Annotation> found = null;
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isScopeAnnotation(annotationType)) {
                if (found != null) {
                    errors.duplicateScopeAnnotations(found, annotationType);
                }
                else {
                    found = annotationType;
                }
            }
        }
        return found;
    }
    
    public static boolean isScopeAnnotation(final Class<? extends Annotation> annotationType) {
        return Annotations.scopeChecker.hasAnnotations(annotationType);
    }
    
    public static void checkForMisplacedScopeAnnotations(final Class<?> type, final Object source, final Errors errors) {
        if ($Classes.isConcrete(type)) {
            return;
        }
        final Class<? extends Annotation> scopeAnnotation = findScopeAnnotation(errors, type);
        if (scopeAnnotation != null) {
            errors.withSource(type).scopeAnnotationOnAbstractType(scopeAnnotation, type, source);
        }
    }
    
    public static Key<?> getKey(final TypeLiteral<?> type, final Member member, final Annotation[] annotations, final Errors errors) throws ErrorsException {
        final int numErrorsBefore = errors.size();
        final Annotation found = findBindingAnnotation(errors, member, annotations);
        errors.throwIfNewErrors(numErrorsBefore);
        return (found == null) ? Key.get(type) : Key.get(type, found);
    }
    
    public static Annotation findBindingAnnotation(final Errors errors, final Member member, final Annotation[] annotations) {
        Annotation found = null;
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isBindingAnnotation(annotationType)) {
                if (found != null) {
                    errors.duplicateBindingAnnotations(member, found.annotationType(), annotationType);
                }
                else {
                    found = annotation;
                }
            }
        }
        return found;
    }
    
    public static boolean isBindingAnnotation(final Class<? extends Annotation> annotationType) {
        return Annotations.bindingAnnotationChecker.hasAnnotations(annotationType);
    }
    
    public static Annotation canonicalizeIfNamed(final Annotation annotation) {
        if (annotation instanceof Named) {
            return Names.named(((Named)annotation).value());
        }
        return annotation;
    }
    
    public static Class<? extends Annotation> canonicalizeIfNamed(final Class<? extends Annotation> annotationType) {
        if (annotationType == Named.class) {
            return com.google.inject.name.Named.class;
        }
        return annotationType;
    }
    
    static {
        scopeChecker = new AnnotationChecker((Collection<Class<? extends Annotation>>)Arrays.asList(ScopeAnnotation.class, Scope.class));
        bindingAnnotationChecker = new AnnotationChecker((Collection<Class<? extends Annotation>>)Arrays.asList(BindingAnnotation.class, Qualifier.class));
    }
    
    static class AnnotationChecker
    {
        private final Collection<Class<? extends Annotation>> annotationTypes;
        private $Function<Class<? extends Annotation>, Boolean> hasAnnotations;
        final Map<Class<? extends Annotation>, Boolean> cache;
        
        AnnotationChecker(final Collection<Class<? extends Annotation>> annotationTypes) {
            this.hasAnnotations = new $Function<Class<? extends Annotation>, Boolean>() {
                public Boolean apply(final Class<? extends Annotation> annotationType) {
                    for (final Annotation annotation : annotationType.getAnnotations()) {
                        if (AnnotationChecker.this.annotationTypes.contains(annotation.annotationType())) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            this.cache = (Map<Class<? extends Annotation>, Boolean>)new $MapMaker().weakKeys().makeComputingMap(($Function<? super Object, ?>)this.hasAnnotations);
            this.annotationTypes = annotationTypes;
        }
        
        boolean hasAnnotations(final Class<? extends Annotation> annotated) {
            return this.cache.get(annotated);
        }
    }
}
