// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.internal.Annotations;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.MoreTypes;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;

public class Key<T>
{
    private final AnnotationStrategy annotationStrategy;
    private final TypeLiteral<T> typeLiteral;
    private final int hashCode;
    
    protected Key(final Class<? extends Annotation> annotationType) {
        this.annotationStrategy = strategyFor(annotationType);
        this.typeLiteral = (TypeLiteral<T>)TypeLiteral.fromSuperclassTypeParameter(this.getClass());
        this.hashCode = this.computeHashCode();
    }
    
    protected Key(final Annotation annotation) {
        this.annotationStrategy = strategyFor(annotation);
        this.typeLiteral = (TypeLiteral<T>)TypeLiteral.fromSuperclassTypeParameter(this.getClass());
        this.hashCode = this.computeHashCode();
    }
    
    protected Key() {
        this.annotationStrategy = NullAnnotationStrategy.INSTANCE;
        this.typeLiteral = (TypeLiteral<T>)TypeLiteral.fromSuperclassTypeParameter(this.getClass());
        this.hashCode = this.computeHashCode();
    }
    
    private Key(final Type type, final AnnotationStrategy annotationStrategy) {
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = MoreTypes.canonicalizeForKey(TypeLiteral.get(type));
        this.hashCode = this.computeHashCode();
    }
    
    private Key(final TypeLiteral<T> typeLiteral, final AnnotationStrategy annotationStrategy) {
        this.annotationStrategy = annotationStrategy;
        this.typeLiteral = MoreTypes.canonicalizeForKey(typeLiteral);
        this.hashCode = this.computeHashCode();
    }
    
    private int computeHashCode() {
        return this.typeLiteral.hashCode() * 31 + this.annotationStrategy.hashCode();
    }
    
    public final TypeLiteral<T> getTypeLiteral() {
        return this.typeLiteral;
    }
    
    public final Class<? extends Annotation> getAnnotationType() {
        return this.annotationStrategy.getAnnotationType();
    }
    
    public final Annotation getAnnotation() {
        return this.annotationStrategy.getAnnotation();
    }
    
    boolean hasAnnotationType() {
        return this.annotationStrategy.getAnnotationType() != null;
    }
    
    String getAnnotationName() {
        final Annotation annotation = this.annotationStrategy.getAnnotation();
        if (annotation != null) {
            return annotation.toString();
        }
        return this.annotationStrategy.getAnnotationType().toString();
    }
    
    Class<? super T> getRawType() {
        return this.typeLiteral.getRawType();
    }
    
    Key<Provider<T>> providerKey() {
        return this.ofType(this.typeLiteral.providerType());
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Key)) {
            return false;
        }
        final Key<?> other = (Key<?>)o;
        return this.annotationStrategy.equals(other.annotationStrategy) && this.typeLiteral.equals(other.typeLiteral);
    }
    
    @Override
    public final int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public final String toString() {
        return "Key[type=" + this.typeLiteral + ", annotation=" + this.annotationStrategy + "]";
    }
    
    static <T> Key<T> get(final Class<T> type, final AnnotationStrategy annotationStrategy) {
        return new Key<T>(type, annotationStrategy);
    }
    
    public static <T> Key<T> get(final Class<T> type) {
        return new Key<T>(type, NullAnnotationStrategy.INSTANCE);
    }
    
    public static <T> Key<T> get(final Class<T> type, final Class<? extends Annotation> annotationType) {
        return new Key<T>(type, strategyFor(annotationType));
    }
    
    public static <T> Key<T> get(final Class<T> type, final Annotation annotation) {
        return new Key<T>(type, strategyFor(annotation));
    }
    
    public static Key<?> get(final Type type) {
        return new Key<Object>(type, NullAnnotationStrategy.INSTANCE);
    }
    
    public static Key<?> get(final Type type, final Class<? extends Annotation> annotationType) {
        return new Key<Object>(type, strategyFor(annotationType));
    }
    
    public static Key<?> get(final Type type, final Annotation annotation) {
        return new Key<Object>(type, strategyFor(annotation));
    }
    
    public static <T> Key<T> get(final TypeLiteral<T> typeLiteral) {
        return new Key<T>(typeLiteral, NullAnnotationStrategy.INSTANCE);
    }
    
    public static <T> Key<T> get(final TypeLiteral<T> typeLiteral, final Class<? extends Annotation> annotationType) {
        return new Key<T>(typeLiteral, strategyFor(annotationType));
    }
    
    public static <T> Key<T> get(final TypeLiteral<T> typeLiteral, final Annotation annotation) {
        return new Key<T>(typeLiteral, strategyFor(annotation));
    }
    
    public <T> Key<T> ofType(final Class<T> type) {
        return new Key<T>(type, this.annotationStrategy);
    }
    
    public Key<?> ofType(final Type type) {
        return new Key<Object>(type, this.annotationStrategy);
    }
    
    public <T> Key<T> ofType(final TypeLiteral<T> type) {
        return new Key<T>(type, this.annotationStrategy);
    }
    
    public boolean hasAttributes() {
        return this.annotationStrategy.hasAttributes();
    }
    
    public Key<T> withoutAttributes() {
        return new Key<T>(this.typeLiteral, this.annotationStrategy.withoutAttributes());
    }
    
    static AnnotationStrategy strategyFor(final Annotation annotation) {
        $Preconditions.checkNotNull(annotation, (Object)"annotation");
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        ensureRetainedAtRuntime(annotationType);
        ensureIsBindingAnnotation(annotationType);
        if (Annotations.isMarker(annotationType)) {
            return new AnnotationTypeStrategy(annotationType, annotation);
        }
        return new AnnotationInstanceStrategy(Annotations.canonicalizeIfNamed(annotation));
    }
    
    static AnnotationStrategy strategyFor(final Class<? extends Annotation> annotationType) {
        $Preconditions.checkNotNull(annotationType, (Object)"annotation type");
        ensureRetainedAtRuntime(annotationType);
        ensureIsBindingAnnotation(annotationType);
        return new AnnotationTypeStrategy(Annotations.canonicalizeIfNamed(annotationType), null);
    }
    
    private static void ensureRetainedAtRuntime(final Class<? extends Annotation> annotationType) {
        $Preconditions.checkArgument(Annotations.isRetainedAtRuntime(annotationType), "%s is not retained at runtime. Please annotate it with @Retention(RUNTIME).", annotationType.getName());
    }
    
    private static void ensureIsBindingAnnotation(final Class<? extends Annotation> annotationType) {
        $Preconditions.checkArgument(Annotations.isBindingAnnotation(annotationType), "%s is not a binding annotation. Please annotate it with @BindingAnnotation.", annotationType.getName());
    }
    
    enum NullAnnotationStrategy implements AnnotationStrategy
    {
        INSTANCE;
        
        public boolean hasAttributes() {
            return false;
        }
        
        public AnnotationStrategy withoutAttributes() {
            throw new UnsupportedOperationException("Key already has no attributes.");
        }
        
        public Annotation getAnnotation() {
            return null;
        }
        
        public Class<? extends Annotation> getAnnotationType() {
            return null;
        }
        
        @Override
        public String toString() {
            return "[none]";
        }
    }
    
    static class AnnotationInstanceStrategy implements AnnotationStrategy
    {
        final Annotation annotation;
        
        AnnotationInstanceStrategy(final Annotation annotation) {
            this.annotation = $Preconditions.checkNotNull(annotation, (Object)"annotation");
        }
        
        public boolean hasAttributes() {
            return true;
        }
        
        public AnnotationStrategy withoutAttributes() {
            return new AnnotationTypeStrategy(this.getAnnotationType(), this.annotation);
        }
        
        public Annotation getAnnotation() {
            return this.annotation;
        }
        
        public Class<? extends Annotation> getAnnotationType() {
            return this.annotation.annotationType();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof AnnotationInstanceStrategy)) {
                return false;
            }
            final AnnotationInstanceStrategy other = (AnnotationInstanceStrategy)o;
            return this.annotation.equals(other.annotation);
        }
        
        @Override
        public int hashCode() {
            return this.annotation.hashCode();
        }
        
        @Override
        public String toString() {
            return this.annotation.toString();
        }
    }
    
    static class AnnotationTypeStrategy implements AnnotationStrategy
    {
        final Class<? extends Annotation> annotationType;
        final Annotation annotation;
        
        AnnotationTypeStrategy(final Class<? extends Annotation> annotationType, final Annotation annotation) {
            this.annotationType = $Preconditions.checkNotNull(annotationType, (Object)"annotation type");
            this.annotation = annotation;
        }
        
        public boolean hasAttributes() {
            return false;
        }
        
        public AnnotationStrategy withoutAttributes() {
            throw new UnsupportedOperationException("Key already has no attributes.");
        }
        
        public Annotation getAnnotation() {
            return this.annotation;
        }
        
        public Class<? extends Annotation> getAnnotationType() {
            return this.annotationType;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof AnnotationTypeStrategy)) {
                return false;
            }
            final AnnotationTypeStrategy other = (AnnotationTypeStrategy)o;
            return this.annotationType.equals(other.annotationType);
        }
        
        @Override
        public int hashCode() {
            return this.annotationType.hashCode();
        }
        
        @Override
        public String toString() {
            return "@" + this.annotationType.getName();
        }
    }
    
    interface AnnotationStrategy
    {
        Annotation getAnnotation();
        
        Class<? extends Annotation> getAnnotationType();
        
        boolean hasAttributes();
        
        AnnotationStrategy withoutAttributes();
    }
}
