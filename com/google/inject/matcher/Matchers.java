// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.matcher;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.AnnotatedElement;
import com.google.inject.internal.util.$Preconditions;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

public class Matchers
{
    private static final Matcher<Object> ANY;
    
    private Matchers() {
    }
    
    public static Matcher<Object> any() {
        return Matchers.ANY;
    }
    
    public static <T> Matcher<T> not(final Matcher<? super T> p) {
        return new Not<T>((Matcher)p);
    }
    
    private static void checkForRuntimeRetention(final Class<? extends Annotation> annotationType) {
        final Retention retention = annotationType.getAnnotation(Retention.class);
        $Preconditions.checkArgument(retention != null && retention.value() == RetentionPolicy.RUNTIME, (Object)("Annotation " + annotationType.getSimpleName() + " is missing RUNTIME retention"));
    }
    
    public static Matcher<AnnotatedElement> annotatedWith(final Class<? extends Annotation> annotationType) {
        return new AnnotatedWithType(annotationType);
    }
    
    public static Matcher<AnnotatedElement> annotatedWith(final Annotation annotation) {
        return new AnnotatedWith(annotation);
    }
    
    public static Matcher<Class> subclassesOf(final Class<?> superclass) {
        return new SubclassesOf(superclass);
    }
    
    public static Matcher<Object> only(final Object value) {
        return new Only(value);
    }
    
    public static Matcher<Object> identicalTo(final Object value) {
        return new IdenticalTo(value);
    }
    
    public static Matcher<Class> inPackage(final Package targetPackage) {
        return new InPackage(targetPackage);
    }
    
    public static Matcher<Class> inSubpackage(final String targetPackageName) {
        return new InSubpackage(targetPackageName);
    }
    
    public static Matcher<Method> returns(final Matcher<? super Class<?>> returnType) {
        return new Returns(returnType);
    }
    
    static {
        ANY = new Any();
    }
    
    private static class Any extends AbstractMatcher<Object> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        
        public boolean matches(final Object o) {
            return true;
        }
        
        @Override
        public String toString() {
            return "any()";
        }
        
        public Object readResolve() {
            return Matchers.any();
        }
    }
    
    private static class Not<T> extends AbstractMatcher<T> implements Serializable
    {
        final Matcher<? super T> delegate;
        private static final long serialVersionUID = 0L;
        
        private Not(final Matcher<? super T> delegate) {
            this.delegate = $Preconditions.checkNotNull(delegate, (Object)"delegate");
        }
        
        public boolean matches(final T t) {
            return !this.delegate.matches((Object)t);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof Not && ((Not)other).delegate.equals(this.delegate);
        }
        
        @Override
        public int hashCode() {
            return -this.delegate.hashCode();
        }
        
        @Override
        public String toString() {
            return "not(" + this.delegate + ")";
        }
    }
    
    private static class AnnotatedWithType extends AbstractMatcher<AnnotatedElement> implements Serializable
    {
        private final Class<? extends Annotation> annotationType;
        private static final long serialVersionUID = 0L;
        
        public AnnotatedWithType(final Class<? extends Annotation> annotationType) {
            this.annotationType = $Preconditions.checkNotNull(annotationType, (Object)"annotation type");
            checkForRuntimeRetention(annotationType);
        }
        
        public boolean matches(final AnnotatedElement element) {
            return element.getAnnotation(this.annotationType) != null;
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof AnnotatedWithType && ((AnnotatedWithType)other).annotationType.equals(this.annotationType);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.annotationType.hashCode();
        }
        
        @Override
        public String toString() {
            return "annotatedWith(" + this.annotationType.getSimpleName() + ".class)";
        }
    }
    
    private static class AnnotatedWith extends AbstractMatcher<AnnotatedElement> implements Serializable
    {
        private final Annotation annotation;
        private static final long serialVersionUID = 0L;
        
        public AnnotatedWith(final Annotation annotation) {
            this.annotation = $Preconditions.checkNotNull(annotation, (Object)"annotation");
            checkForRuntimeRetention(annotation.annotationType());
        }
        
        public boolean matches(final AnnotatedElement element) {
            final Annotation fromElement = element.getAnnotation(this.annotation.annotationType());
            return fromElement != null && this.annotation.equals(fromElement);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof AnnotatedWith && ((AnnotatedWith)other).annotation.equals(this.annotation);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.annotation.hashCode();
        }
        
        @Override
        public String toString() {
            return "annotatedWith(" + this.annotation + ")";
        }
    }
    
    private static class SubclassesOf extends AbstractMatcher<Class> implements Serializable
    {
        private final Class<?> superclass;
        private static final long serialVersionUID = 0L;
        
        public SubclassesOf(final Class<?> superclass) {
            this.superclass = $Preconditions.checkNotNull(superclass, (Object)"superclass");
        }
        
        public boolean matches(final Class subclass) {
            return this.superclass.isAssignableFrom(subclass);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof SubclassesOf && ((SubclassesOf)other).superclass.equals(this.superclass);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.superclass.hashCode();
        }
        
        @Override
        public String toString() {
            return "subclassesOf(" + this.superclass.getSimpleName() + ".class)";
        }
    }
    
    private static class Only extends AbstractMatcher<Object> implements Serializable
    {
        private final Object value;
        private static final long serialVersionUID = 0L;
        
        public Only(final Object value) {
            this.value = $Preconditions.checkNotNull(value, (Object)"value");
        }
        
        public boolean matches(final Object other) {
            return this.value.equals(other);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof Only && ((Only)other).value.equals(this.value);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.value.hashCode();
        }
        
        @Override
        public String toString() {
            return "only(" + this.value + ")";
        }
    }
    
    private static class IdenticalTo extends AbstractMatcher<Object> implements Serializable
    {
        private final Object value;
        private static final long serialVersionUID = 0L;
        
        public IdenticalTo(final Object value) {
            this.value = $Preconditions.checkNotNull(value, (Object)"value");
        }
        
        public boolean matches(final Object other) {
            return this.value == other;
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof IdenticalTo && ((IdenticalTo)other).value == this.value;
        }
        
        @Override
        public int hashCode() {
            return 37 * System.identityHashCode(this.value);
        }
        
        @Override
        public String toString() {
            return "identicalTo(" + this.value + ")";
        }
    }
    
    private static class InPackage extends AbstractMatcher<Class> implements Serializable
    {
        private final transient Package targetPackage;
        private final String packageName;
        private static final long serialVersionUID = 0L;
        
        public InPackage(final Package targetPackage) {
            this.targetPackage = $Preconditions.checkNotNull(targetPackage, (Object)"package");
            this.packageName = targetPackage.getName();
        }
        
        public boolean matches(final Class c) {
            return c.getPackage().equals(this.targetPackage);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof InPackage && ((InPackage)other).targetPackage.equals(this.targetPackage);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.targetPackage.hashCode();
        }
        
        @Override
        public String toString() {
            return "inPackage(" + this.targetPackage.getName() + ")";
        }
        
        public Object readResolve() {
            return Matchers.inPackage(Package.getPackage(this.packageName));
        }
    }
    
    private static class InSubpackage extends AbstractMatcher<Class> implements Serializable
    {
        private final String targetPackageName;
        private static final long serialVersionUID = 0L;
        
        public InSubpackage(final String targetPackageName) {
            this.targetPackageName = targetPackageName;
        }
        
        public boolean matches(final Class c) {
            final String classPackageName = c.getPackage().getName();
            return classPackageName.equals(this.targetPackageName) || classPackageName.startsWith(this.targetPackageName + ".");
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof InSubpackage && ((InSubpackage)other).targetPackageName.equals(this.targetPackageName);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.targetPackageName.hashCode();
        }
        
        @Override
        public String toString() {
            return "inSubpackage(" + this.targetPackageName + ")";
        }
    }
    
    private static class Returns extends AbstractMatcher<Method> implements Serializable
    {
        private final Matcher<? super Class<?>> returnType;
        private static final long serialVersionUID = 0L;
        
        public Returns(final Matcher<? super Class<?>> returnType) {
            this.returnType = $Preconditions.checkNotNull(returnType, (Object)"return type matcher");
        }
        
        public boolean matches(final Method m) {
            return this.returnType.matches(m.getReturnType());
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof Returns && ((Returns)other).returnType.equals(this.returnType);
        }
        
        @Override
        public int hashCode() {
            return 37 * this.returnType.hashCode();
        }
        
        @Override
        public String toString() {
            return "returns(" + this.returnType + ")";
        }
    }
}
