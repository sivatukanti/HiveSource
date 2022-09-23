// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import com.google.inject.internal.util.$ImmutableSet;
import java.util.Collections;
import java.util.logging.Level;
import java.util.Set;
import java.lang.reflect.Modifier;
import com.google.inject.Inject;
import com.google.inject.internal.MoreTypes;
import com.google.inject.internal.util.$Classes;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Iterator;
import com.google.inject.internal.util.$Lists;
import java.util.Arrays;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import com.google.inject.internal.Nullability;
import com.google.inject.internal.ErrorsException;
import com.google.inject.ConfigurationException;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.TypeLiteral;
import java.lang.reflect.Member;
import java.util.logging.Logger;

public final class InjectionPoint
{
    private static final Logger logger;
    private final boolean optional;
    private final Member member;
    private final TypeLiteral<?> declaringType;
    private final $ImmutableList<Dependency<?>> dependencies;
    
    InjectionPoint(final TypeLiteral<?> declaringType, final Method method, final boolean optional) {
        this.member = method;
        this.declaringType = declaringType;
        this.optional = optional;
        this.dependencies = this.forMember(method, declaringType, method.getParameterAnnotations());
    }
    
    InjectionPoint(final TypeLiteral<?> declaringType, final Constructor<?> constructor) {
        this.member = constructor;
        this.declaringType = declaringType;
        this.optional = false;
        this.dependencies = this.forMember(constructor, declaringType, constructor.getParameterAnnotations());
    }
    
    InjectionPoint(final TypeLiteral<?> declaringType, final Field field, final boolean optional) {
        this.member = field;
        this.declaringType = declaringType;
        this.optional = optional;
        final Annotation[] annotations = field.getAnnotations();
        final Errors errors = new Errors(field);
        Key<?> key = null;
        try {
            key = Annotations.getKey(declaringType.getFieldType(field), field, annotations, errors);
        }
        catch (ConfigurationException e) {
            errors.merge(e.getErrorMessages());
        }
        catch (ErrorsException e2) {
            errors.merge(e2.getErrors());
        }
        errors.throwConfigurationExceptionIfErrorsExist();
        this.dependencies = $ImmutableList.of(this.newDependency(key, Nullability.allowsNull(annotations), -1));
    }
    
    private $ImmutableList<Dependency<?>> forMember(final Member member, final TypeLiteral<?> type, final Annotation[][] paramterAnnotations) {
        final Errors errors = new Errors(member);
        final Iterator<Annotation[]> annotationsIterator = Arrays.asList(paramterAnnotations).iterator();
        final List<Dependency<?>> dependencies = (List<Dependency<?>>)$Lists.newArrayList();
        int index = 0;
        for (final TypeLiteral<?> parameterType : type.getParameterTypes(member)) {
            try {
                final Annotation[] parameterAnnotations = annotationsIterator.next();
                final Key<?> key = Annotations.getKey(parameterType, member, parameterAnnotations, errors);
                dependencies.add(this.newDependency(key, Nullability.allowsNull(parameterAnnotations), index));
                ++index;
            }
            catch (ConfigurationException e) {
                errors.merge(e.getErrorMessages());
            }
            catch (ErrorsException e2) {
                errors.merge(e2.getErrors());
            }
        }
        errors.throwConfigurationExceptionIfErrorsExist();
        return $ImmutableList.copyOf((Iterable<? extends Dependency<?>>)dependencies);
    }
    
    private <T> Dependency<T> newDependency(final Key<T> key, final boolean allowsNull, final int parameterIndex) {
        return new Dependency<T>(this, key, allowsNull, parameterIndex);
    }
    
    public Member getMember() {
        return this.member;
    }
    
    public List<Dependency<?>> getDependencies() {
        return this.dependencies;
    }
    
    public boolean isOptional() {
        return this.optional;
    }
    
    public boolean isToolable() {
        return ((AnnotatedElement)this.member).isAnnotationPresent(Toolable.class);
    }
    
    public TypeLiteral<?> getDeclaringType() {
        return this.declaringType;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof InjectionPoint && this.member.equals(((InjectionPoint)o).member) && this.declaringType.equals(((InjectionPoint)o).declaringType);
    }
    
    @Override
    public int hashCode() {
        return this.member.hashCode() ^ this.declaringType.hashCode();
    }
    
    @Override
    public String toString() {
        return $Classes.toString(this.member);
    }
    
    public static <T> InjectionPoint forConstructor(final Constructor<T> constructor) {
        return new InjectionPoint(TypeLiteral.get(constructor.getDeclaringClass()), constructor);
    }
    
    public static <T> InjectionPoint forConstructor(final Constructor<T> constructor, final TypeLiteral<? extends T> type) {
        if (type.getRawType() != constructor.getDeclaringClass()) {
            new Errors(type).constructorNotDefinedByType(constructor, type).throwConfigurationExceptionIfErrorsExist();
        }
        return new InjectionPoint(type, constructor);
    }
    
    public static InjectionPoint forConstructorOf(final TypeLiteral<?> type) {
        final Class<?> rawType = MoreTypes.getRawType(type.getType());
        final Errors errors = new Errors(rawType);
        Constructor<?> injectableConstructor = null;
        for (final Constructor<?> constructor : rawType.getDeclaredConstructors()) {
            final Inject guiceInject = constructor.getAnnotation(Inject.class);
            Label_0132: {
                boolean optional;
                if (guiceInject == null) {
                    final javax.inject.Inject javaxInject = constructor.getAnnotation(javax.inject.Inject.class);
                    if (javaxInject == null) {
                        break Label_0132;
                    }
                    optional = false;
                }
                else {
                    optional = guiceInject.optional();
                }
                if (optional) {
                    errors.optionalConstructor(constructor);
                }
                if (injectableConstructor != null) {
                    errors.tooManyConstructors(rawType);
                }
                injectableConstructor = constructor;
                checkForMisplacedBindingAnnotations(injectableConstructor, errors);
            }
        }
        errors.throwConfigurationExceptionIfErrorsExist();
        if (injectableConstructor != null) {
            return new InjectionPoint(type, injectableConstructor);
        }
        try {
            final Constructor<?> noArgConstructor = rawType.getDeclaredConstructor((Class<?>[])new Class[0]);
            if (Modifier.isPrivate(noArgConstructor.getModifiers()) && !Modifier.isPrivate(rawType.getModifiers())) {
                errors.missingConstructor(rawType);
                throw new ConfigurationException(errors.getMessages());
            }
            checkForMisplacedBindingAnnotations(noArgConstructor, errors);
            return new InjectionPoint(type, noArgConstructor);
        }
        catch (NoSuchMethodException e) {
            errors.missingConstructor(rawType);
            throw new ConfigurationException(errors.getMessages());
        }
    }
    
    public static InjectionPoint forConstructorOf(final Class<?> type) {
        return forConstructorOf(TypeLiteral.get(type));
    }
    
    public static Set<InjectionPoint> forStaticMethodsAndFields(final TypeLiteral<?> type) {
        final Errors errors = new Errors();
        final Set<InjectionPoint> result = getInjectionPoints(type, true, errors);
        if (errors.hasErrors()) {
            throw new ConfigurationException(errors.getMessages()).withPartialValue(result);
        }
        return result;
    }
    
    public static Set<InjectionPoint> forStaticMethodsAndFields(final Class<?> type) {
        return forStaticMethodsAndFields(TypeLiteral.get(type));
    }
    
    public static Set<InjectionPoint> forInstanceMethodsAndFields(final TypeLiteral<?> type) {
        final Errors errors = new Errors();
        final Set<InjectionPoint> result = getInjectionPoints(type, false, errors);
        if (errors.hasErrors()) {
            throw new ConfigurationException(errors.getMessages()).withPartialValue(result);
        }
        return result;
    }
    
    public static Set<InjectionPoint> forInstanceMethodsAndFields(final Class<?> type) {
        return forInstanceMethodsAndFields(TypeLiteral.get(type));
    }
    
    private static boolean checkForMisplacedBindingAnnotations(final Member member, final Errors errors) {
        final Annotation misplacedBindingAnnotation = Annotations.findBindingAnnotation(errors, member, ((AnnotatedElement)member).getAnnotations());
        if (misplacedBindingAnnotation == null) {
            return false;
        }
        if (member instanceof Method) {
            try {
                if (member.getDeclaringClass().getDeclaredField(member.getName()) != null) {
                    return false;
                }
            }
            catch (NoSuchFieldException ex) {}
        }
        errors.misplacedBindingAnnotation(member, misplacedBindingAnnotation);
        return true;
    }
    
    static Annotation getAtInject(final AnnotatedElement member) {
        final Annotation a = member.getAnnotation(javax.inject.Inject.class);
        return (a == null) ? member.getAnnotation(Inject.class) : a;
    }
    
    private static Set<InjectionPoint> getInjectionPoints(final TypeLiteral<?> type, final boolean statics, final Errors errors) {
        final InjectableMembers injectableMembers = new InjectableMembers();
        OverrideIndex overrideIndex = null;
        final List<TypeLiteral<?>> hierarchy = hierarchyFor(type);
        int i;
        for (int topIndex = i = hierarchy.size() - 1; i >= 0; --i) {
            if (overrideIndex != null && i < topIndex) {
                if (i == 0) {
                    overrideIndex.position = Position.BOTTOM;
                }
                else {
                    overrideIndex.position = Position.MIDDLE;
                }
            }
            final TypeLiteral<?> current = hierarchy.get(i);
            for (final Field field : current.getRawType().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) == statics) {
                    final Annotation atInject = getAtInject(field);
                    if (atInject != null) {
                        final InjectableField injectableField = new InjectableField(current, field, atInject);
                        if (injectableField.jsr330 && Modifier.isFinal(field.getModifiers())) {
                            errors.cannotInjectFinalField(field);
                        }
                        injectableMembers.add(injectableField);
                    }
                }
            }
            for (final Method method : current.getRawType().getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) == statics) {
                    final Annotation atInject = getAtInject(method);
                    if (atInject != null) {
                        final InjectableMethod injectableMethod = new InjectableMethod(current, method, atInject);
                        if (checkForMisplacedBindingAnnotations(method, errors) | !isValidMethod(injectableMethod, errors)) {
                            if (overrideIndex != null) {
                                final boolean removed = overrideIndex.removeIfOverriddenBy(method, false, injectableMethod);
                                if (removed) {
                                    InjectionPoint.logger.log(Level.WARNING, "Method: {0} is not a valid injectable method (because it either has misplaced binding annotations or specifies type parameters) but is overriding a method that is valid. Because it is not valid, the method will not be injected. To fix this, make the method a valid injectable method.", method);
                                }
                            }
                        }
                        else if (statics) {
                            injectableMembers.add(injectableMethod);
                        }
                        else {
                            if (overrideIndex == null) {
                                overrideIndex = new OverrideIndex(injectableMembers);
                            }
                            else {
                                overrideIndex.removeIfOverriddenBy(method, true, injectableMethod);
                            }
                            overrideIndex.add(injectableMethod);
                        }
                    }
                    else if (overrideIndex != null) {
                        final boolean removed2 = overrideIndex.removeIfOverriddenBy(method, false, null);
                        if (removed2) {
                            InjectionPoint.logger.log(Level.WARNING, "Method: {0} is not annotated with @Inject but is overriding a method that is annotated with @javax.inject.Inject.  Because it is not annotated with @Inject, the method will not be injected. To fix this, annotate the method with @Inject.", method);
                        }
                    }
                }
            }
        }
        if (injectableMembers.isEmpty()) {
            return Collections.emptySet();
        }
        final $ImmutableSet.Builder<InjectionPoint> builder = $ImmutableSet.builder();
        for (InjectableMember im = injectableMembers.head; im != null; im = im.next) {
            try {
                builder.add(im.toInjectionPoint());
            }
            catch (ConfigurationException ignorable) {
                if (!im.optional) {
                    errors.merge(ignorable.getErrorMessages());
                }
            }
        }
        return builder.build();
    }
    
    private static boolean isValidMethod(final InjectableMethod injectableMethod, final Errors errors) {
        boolean result = true;
        if (injectableMethod.jsr330) {
            final Method method = injectableMethod.method;
            if (Modifier.isAbstract(method.getModifiers())) {
                errors.cannotInjectAbstractMethod(method);
                result = false;
            }
            if (method.getTypeParameters().length > 0) {
                errors.cannotInjectMethodWithTypeParameters(method);
                result = false;
            }
        }
        return result;
    }
    
    private static List<TypeLiteral<?>> hierarchyFor(final TypeLiteral<?> type) {
        final List<TypeLiteral<?>> hierarchy = new ArrayList<TypeLiteral<?>>();
        for (TypeLiteral<?> current = type; current.getRawType() != Object.class; current = current.getSupertype(current.getRawType().getSuperclass())) {
            hierarchy.add(current);
        }
        return hierarchy;
    }
    
    private static boolean overrides(final Method a, final Method b) {
        final int modifiers = b.getModifiers();
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers) || (!Modifier.isPrivate(modifiers) && a.getDeclaringClass().getPackage().equals(b.getDeclaringClass().getPackage()));
    }
    
    static {
        logger = Logger.getLogger(InjectionPoint.class.getName());
    }
    
    abstract static class InjectableMember
    {
        final TypeLiteral<?> declaringType;
        final boolean optional;
        final boolean jsr330;
        InjectableMember previous;
        InjectableMember next;
        
        InjectableMember(final TypeLiteral<?> declaringType, final Annotation atInject) {
            this.declaringType = declaringType;
            if (atInject.annotationType() == javax.inject.Inject.class) {
                this.optional = false;
                this.jsr330 = true;
                return;
            }
            this.jsr330 = false;
            this.optional = ((Inject)atInject).optional();
        }
        
        abstract InjectionPoint toInjectionPoint();
    }
    
    static class InjectableField extends InjectableMember
    {
        final Field field;
        
        InjectableField(final TypeLiteral<?> declaringType, final Field field, final Annotation atInject) {
            super(declaringType, atInject);
            this.field = field;
        }
        
        @Override
        InjectionPoint toInjectionPoint() {
            return new InjectionPoint(this.declaringType, this.field, this.optional);
        }
    }
    
    static class InjectableMethod extends InjectableMember
    {
        final Method method;
        boolean overrodeGuiceInject;
        
        InjectableMethod(final TypeLiteral<?> declaringType, final Method method, final Annotation atInject) {
            super(declaringType, atInject);
            this.method = method;
        }
        
        @Override
        InjectionPoint toInjectionPoint() {
            return new InjectionPoint(this.declaringType, this.method, this.optional);
        }
        
        public boolean isFinal() {
            return Modifier.isFinal(this.method.getModifiers());
        }
    }
    
    static class InjectableMembers
    {
        InjectableMember head;
        InjectableMember tail;
        
        void add(final InjectableMember member) {
            if (this.head == null) {
                this.tail = member;
                this.head = member;
            }
            else {
                member.previous = this.tail;
                this.tail.next = member;
                this.tail = member;
            }
        }
        
        void remove(final InjectableMember member) {
            if (member.previous != null) {
                member.previous.next = member.next;
            }
            if (member.next != null) {
                member.next.previous = member.previous;
            }
            if (this.head == member) {
                this.head = member.next;
            }
            if (this.tail == member) {
                this.tail = member.previous;
            }
        }
        
        boolean isEmpty() {
            return this.head == null;
        }
    }
    
    enum Position
    {
        TOP, 
        MIDDLE, 
        BOTTOM;
    }
    
    static class OverrideIndex
    {
        final InjectableMembers injectableMembers;
        Map<Signature, List<InjectableMethod>> bySignature;
        Position position;
        Method lastMethod;
        Signature lastSignature;
        
        OverrideIndex(final InjectableMembers injectableMembers) {
            this.position = Position.TOP;
            this.injectableMembers = injectableMembers;
        }
        
        boolean removeIfOverriddenBy(final Method method, final boolean alwaysRemove, final InjectableMethod injectableMethod) {
            if (this.position == Position.TOP) {
                return false;
            }
            if (this.bySignature == null) {
                this.bySignature = new HashMap<Signature, List<InjectableMethod>>();
                for (InjectableMember member = this.injectableMembers.head; member != null; member = member.next) {
                    if (member instanceof InjectableMethod) {
                        final InjectableMethod im = (InjectableMethod)member;
                        if (!im.isFinal()) {
                            final List<InjectableMethod> methods = new ArrayList<InjectableMethod>();
                            methods.add(im);
                            this.bySignature.put(new Signature(im.method), methods);
                        }
                    }
                }
            }
            this.lastMethod = method;
            final Signature lastSignature = new Signature(method);
            this.lastSignature = lastSignature;
            final Signature signature = lastSignature;
            final List<InjectableMethod> methods2 = this.bySignature.get(signature);
            boolean removed = false;
            if (methods2 != null) {
                final Iterator<InjectableMethod> iterator = methods2.iterator();
                while (iterator.hasNext()) {
                    final InjectableMethod possiblyOverridden = iterator.next();
                    if (overrides(method, possiblyOverridden.method)) {
                        final boolean wasGuiceInject = !possiblyOverridden.jsr330 || possiblyOverridden.overrodeGuiceInject;
                        if (injectableMethod != null) {
                            injectableMethod.overrodeGuiceInject = wasGuiceInject;
                        }
                        if (!alwaysRemove && wasGuiceInject) {
                            continue;
                        }
                        removed = true;
                        iterator.remove();
                        this.injectableMembers.remove(possiblyOverridden);
                    }
                }
            }
            return removed;
        }
        
        void add(final InjectableMethod injectableMethod) {
            this.injectableMembers.add(injectableMethod);
            if (this.position == Position.BOTTOM || injectableMethod.isFinal()) {
                return;
            }
            if (this.bySignature != null) {
                final Signature signature = (injectableMethod.method == this.lastMethod) ? this.lastSignature : new Signature(injectableMethod.method);
                List<InjectableMethod> methods = this.bySignature.get(signature);
                if (methods == null) {
                    methods = new ArrayList<InjectableMethod>();
                    this.bySignature.put(signature, methods);
                }
                methods.add(injectableMethod);
            }
        }
    }
    
    static class Signature
    {
        final String name;
        final Class[] parameterTypes;
        final int hash;
        
        Signature(final Method method) {
            this.name = method.getName();
            this.parameterTypes = method.getParameterTypes();
            int h = this.name.hashCode();
            h = h * 31 + this.parameterTypes.length;
            for (final Class parameterType : this.parameterTypes) {
                h = h * 31 + parameterType.hashCode();
            }
            this.hash = h;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Signature)) {
                return false;
            }
            final Signature other = (Signature)o;
            if (!this.name.equals(other.name)) {
                return false;
            }
            if (this.parameterTypes.length != other.parameterTypes.length) {
                return false;
            }
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                if (this.parameterTypes[i] != other.parameterTypes[i]) {
                    return false;
                }
            }
            return true;
        }
    }
}
