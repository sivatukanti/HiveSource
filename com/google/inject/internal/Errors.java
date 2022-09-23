// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Classes;
import com.google.inject.internal.util.$StackTraceElements;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.Dependency;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Lists;
import java.lang.reflect.Type;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.CreationException;
import com.google.inject.ConfigurationException;
import com.google.inject.ProvisionException;
import com.google.inject.spi.InjectionListener;
import com.google.inject.MembersInjector;
import com.google.inject.spi.TypeListenerBinding;
import java.util.Iterator;
import java.util.Formatter;
import java.util.Set;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import com.google.inject.Scope;
import java.lang.reflect.Member;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import com.google.inject.Provider;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.TypeLiteral;
import com.google.inject.Key;
import com.google.inject.internal.util.$SourceProvider;
import java.util.Collection;
import com.google.inject.spi.Message;
import java.util.List;
import java.io.Serializable;

public final class Errors implements Serializable
{
    private final Errors root;
    private final Errors parent;
    private final Object source;
    private List<Message> errors;
    private static final String CONSTRUCTOR_RULES = "Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.";
    private static final Collection<Converter<?>> converters;
    
    public Errors() {
        this.root = this;
        this.parent = null;
        this.source = $SourceProvider.UNKNOWN_SOURCE;
    }
    
    public Errors(final Object source) {
        this.root = this;
        this.parent = null;
        this.source = source;
    }
    
    private Errors(final Errors parent, final Object source) {
        this.root = parent.root;
        this.parent = parent;
        this.source = source;
    }
    
    public Errors withSource(final Object source) {
        return (source == $SourceProvider.UNKNOWN_SOURCE) ? this : new Errors(this, source);
    }
    
    public Errors missingImplementation(final Key key) {
        return this.addMessage("No implementation for %s was bound.", key);
    }
    
    public Errors jitDisabled(final Key key) {
        return this.addMessage("Explicit bindings are required and %s is not explicitly bound.", key);
    }
    
    public Errors converterReturnedNull(final String stringValue, final Object source, final TypeLiteral<?> type, final TypeConverterBinding typeConverterBinding) {
        return this.addMessage("Received null converting '%s' (bound at %s) to %s%n using %s.", stringValue, convert(source), type, typeConverterBinding);
    }
    
    public Errors conversionTypeError(final String stringValue, final Object source, final TypeLiteral<?> type, final TypeConverterBinding typeConverterBinding, final Object converted) {
        return this.addMessage("Type mismatch converting '%s' (bound at %s) to %s%n using %s.%n Converter returned %s.", stringValue, convert(source), type, typeConverterBinding, converted);
    }
    
    public Errors conversionError(final String stringValue, final Object source, final TypeLiteral<?> type, final TypeConverterBinding typeConverterBinding, final RuntimeException cause) {
        return this.errorInUserCode(cause, "Error converting '%s' (bound at %s) to %s%n using %s.%n Reason: %s", stringValue, convert(source), type, typeConverterBinding, cause);
    }
    
    public Errors ambiguousTypeConversion(final String stringValue, final Object source, final TypeLiteral<?> type, final TypeConverterBinding a, final TypeConverterBinding b) {
        return this.addMessage("Multiple converters can convert '%s' (bound at %s) to %s:%n %s and%n %s.%n Please adjust your type converter configuration to avoid overlapping matches.", stringValue, convert(source), type, a, b);
    }
    
    public Errors bindingToProvider() {
        return this.addMessage("Binding to Provider is not allowed.", new Object[0]);
    }
    
    public Errors subtypeNotProvided(final Class<? extends Provider<?>> providerType, final Class<?> type) {
        return this.addMessage("%s doesn't provide instances of %s.", providerType, type);
    }
    
    public Errors notASubtype(final Class<?> implementationType, final Class<?> type) {
        return this.addMessage("%s doesn't extend %s.", implementationType, type);
    }
    
    public Errors recursiveImplementationType() {
        return this.addMessage("@ImplementedBy points to the same class it annotates.", new Object[0]);
    }
    
    public Errors recursiveProviderType() {
        return this.addMessage("@ProvidedBy points to the same class it annotates.", new Object[0]);
    }
    
    public Errors missingRuntimeRetention(final Object source) {
        return this.addMessage("Please annotate with @Retention(RUNTIME).%n Bound at %s.", convert(source));
    }
    
    public Errors missingScopeAnnotation() {
        return this.addMessage("Please annotate with @ScopeAnnotation.", new Object[0]);
    }
    
    public Errors optionalConstructor(final Constructor constructor) {
        return this.addMessage("%s is annotated @Inject(optional=true), but constructors cannot be optional.", constructor);
    }
    
    public Errors cannotBindToGuiceType(final String simpleName) {
        return this.addMessage("Binding to core guice framework type is not allowed: %s.", simpleName);
    }
    
    public Errors scopeNotFound(final Class<? extends Annotation> scopeAnnotation) {
        return this.addMessage("No scope is bound to %s.", scopeAnnotation);
    }
    
    public Errors scopeAnnotationOnAbstractType(final Class<? extends Annotation> scopeAnnotation, final Class<?> type, final Object source) {
        return this.addMessage("%s is annotated with %s, but scope annotations are not supported for abstract types.%n Bound at %s.", type, scopeAnnotation, convert(source));
    }
    
    public Errors misplacedBindingAnnotation(final Member member, final Annotation bindingAnnotation) {
        return this.addMessage("%s is annotated with %s, but binding annotations should be applied to its parameters instead.", member, bindingAnnotation);
    }
    
    public Errors missingConstructor(final Class<?> implementation) {
        return this.addMessage("Could not find a suitable constructor in %s. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.", implementation);
    }
    
    public Errors tooManyConstructors(final Class<?> implementation) {
        return this.addMessage("%s has more than one constructor annotated with @Inject. Classes must have either one (and only one) constructor annotated with @Inject or a zero-argument constructor that is not private.", implementation);
    }
    
    public Errors constructorNotDefinedByType(final Constructor<?> constructor, final TypeLiteral<?> type) {
        return this.addMessage("%s does not define %s", type, constructor);
    }
    
    public Errors duplicateScopes(final Scope existing, final Class<? extends Annotation> annotationType, final Scope scope) {
        return this.addMessage("Scope %s is already bound to %s. Cannot bind %s.", existing, annotationType, scope);
    }
    
    public Errors voidProviderMethod() {
        return this.addMessage("Provider methods must return a value. Do not return void.", new Object[0]);
    }
    
    public Errors missingConstantValues() {
        return this.addMessage("Missing constant value. Please call to(...).", new Object[0]);
    }
    
    public Errors cannotInjectInnerClass(final Class<?> type) {
        return this.addMessage("Injecting into inner classes is not supported.  Please use a 'static' class (top-level or nested) instead of %s.", type);
    }
    
    public Errors duplicateBindingAnnotations(final Member member, final Class<? extends Annotation> a, final Class<? extends Annotation> b) {
        return this.addMessage("%s has more than one annotation annotated with @BindingAnnotation: %s and %s", member, a, b);
    }
    
    public Errors cannotInjectFinalField(final Field field) {
        return this.addMessage("Injected field %s cannot be final.", field);
    }
    
    public Errors cannotInjectAbstractMethod(final Method method) {
        return this.addMessage("Injected method %s cannot be abstract.", method);
    }
    
    public Errors cannotInjectNonVoidMethod(final Method method) {
        return this.addMessage("Injected method %s must return void.", method);
    }
    
    public Errors cannotInjectMethodWithTypeParameters(final Method method) {
        return this.addMessage("Injected method %s cannot declare type parameters of its own.", method);
    }
    
    public Errors duplicateScopeAnnotations(final Class<? extends Annotation> a, final Class<? extends Annotation> b) {
        return this.addMessage("More than one scope annotation was found: %s and %s.", a, b);
    }
    
    public Errors recursiveBinding() {
        return this.addMessage("Binding points to itself.", new Object[0]);
    }
    
    public Errors bindingAlreadySet(final Key<?> key, final Object source) {
        return this.addMessage("A binding to %s was already configured at %s.", key, convert(source));
    }
    
    public Errors jitBindingAlreadySet(final Key<?> key) {
        return this.addMessage("A just-in-time binding to %s was already configured on a parent injector.", key);
    }
    
    public Errors childBindingAlreadySet(final Key<?> key, final Set<Object> sources) {
        final Formatter allSources = new Formatter();
        for (final Object source : sources) {
            if (source == null) {
                allSources.format("%n    (bound by a just-in-time binding)", new Object[0]);
            }
            else {
                allSources.format("%n    bound at %s", source);
            }
        }
        final Errors errors = this.addMessage("Unable to create binding for %s. It was already configured on one or more child injectors or private modules%s%n  If it was in a PrivateModule, did you forget to expose the binding?", key, allSources.out());
        return errors;
    }
    
    public Errors errorCheckingDuplicateBinding(final Key<?> key, final Object source, final Throwable t) {
        return this.addMessage("A binding to %s was already configured at %s and an error was thrown while checking duplicate bindings.  Error: %s", key, convert(source), t);
    }
    
    public Errors errorInjectingMethod(final Throwable cause) {
        return this.errorInUserCode(cause, "Error injecting method, %s", cause);
    }
    
    public Errors errorNotifyingTypeListener(final TypeListenerBinding listener, final TypeLiteral<?> type, final Throwable cause) {
        return this.errorInUserCode(cause, "Error notifying TypeListener %s (bound at %s) of %s.%n Reason: %s", listener.getListener(), convert(listener.getSource()), type, cause);
    }
    
    public Errors errorInjectingConstructor(final Throwable cause) {
        return this.errorInUserCode(cause, "Error injecting constructor, %s", cause);
    }
    
    public Errors errorInProvider(final RuntimeException runtimeException) {
        final Throwable unwrapped = this.unwrap(runtimeException);
        return this.errorInUserCode(unwrapped, "Error in custom provider, %s", unwrapped);
    }
    
    public Errors errorInUserInjector(final MembersInjector<?> listener, final TypeLiteral<?> type, final RuntimeException cause) {
        return this.errorInUserCode(cause, "Error injecting %s using %s.%n Reason: %s", type, listener, cause);
    }
    
    public Errors errorNotifyingInjectionListener(final InjectionListener<?> listener, final TypeLiteral<?> type, final RuntimeException cause) {
        return this.errorInUserCode(cause, "Error notifying InjectionListener %s of %s.%n Reason: %s", listener, type, cause);
    }
    
    public Errors exposedButNotBound(final Key<?> key) {
        return this.addMessage("Could not expose() %s, it must be explicitly bound.", key);
    }
    
    public Errors keyNotFullySpecified(final TypeLiteral<?> typeLiteral) {
        return this.addMessage("%s cannot be used as a key; It is not fully specified.", typeLiteral);
    }
    
    public Errors errorEnhancingClass(final Class<?> clazz, final Throwable cause) {
        return this.errorInUserCode(cause, "Unable to method intercept: %s", clazz);
    }
    
    public static Collection<Message> getMessagesFromThrowable(final Throwable throwable) {
        if (throwable instanceof ProvisionException) {
            return ((ProvisionException)throwable).getErrorMessages();
        }
        if (throwable instanceof ConfigurationException) {
            return ((ConfigurationException)throwable).getErrorMessages();
        }
        if (throwable instanceof CreationException) {
            return ((CreationException)throwable).getErrorMessages();
        }
        return (Collection<Message>)$ImmutableSet.of();
    }
    
    public Errors errorInUserCode(final Throwable cause, final String messageFormat, final Object... arguments) {
        final Collection<Message> messages = getMessagesFromThrowable(cause);
        if (!messages.isEmpty()) {
            return this.merge(messages);
        }
        return this.addMessage(cause, messageFormat, arguments);
    }
    
    private Throwable unwrap(final RuntimeException runtimeException) {
        if (runtimeException instanceof Exceptions.UnhandledCheckedUserException) {
            return runtimeException.getCause();
        }
        return runtimeException;
    }
    
    public Errors cannotInjectRawProvider() {
        return this.addMessage("Cannot inject a Provider that has no type parameter", new Object[0]);
    }
    
    public Errors cannotInjectRawMembersInjector() {
        return this.addMessage("Cannot inject a MembersInjector that has no type parameter", new Object[0]);
    }
    
    public Errors cannotInjectTypeLiteralOf(final Type unsupportedType) {
        return this.addMessage("Cannot inject a TypeLiteral of %s", unsupportedType);
    }
    
    public Errors cannotInjectRawTypeLiteral() {
        return this.addMessage("Cannot inject a TypeLiteral that has no type parameter", new Object[0]);
    }
    
    public Errors cannotSatisfyCircularDependency(final Class<?> expectedType) {
        return this.addMessage("Tried proxying %s to support a circular dependency, but it is not an interface.", expectedType);
    }
    
    public Errors circularProxiesDisabled(final Class<?> expectedType) {
        return this.addMessage("Tried proxying %s to support a circular dependency, but circular proxies are disabled.", expectedType);
    }
    
    public void throwCreationExceptionIfErrorsExist() {
        if (!this.hasErrors()) {
            return;
        }
        throw new CreationException(this.getMessages());
    }
    
    public void throwConfigurationExceptionIfErrorsExist() {
        if (!this.hasErrors()) {
            return;
        }
        throw new ConfigurationException(this.getMessages());
    }
    
    public void throwProvisionExceptionIfErrorsExist() {
        if (!this.hasErrors()) {
            return;
        }
        throw new ProvisionException(this.getMessages());
    }
    
    private Message merge(final Message message) {
        final List<Object> sources = $Lists.newArrayList();
        sources.addAll(this.getSources());
        sources.addAll(message.getSources());
        return new Message(sources, message.getMessage(), message.getCause());
    }
    
    public Errors merge(final Collection<Message> messages) {
        for (final Message message : messages) {
            this.addMessage(this.merge(message));
        }
        return this;
    }
    
    public Errors merge(final Errors moreErrors) {
        if (moreErrors.root == this.root || moreErrors.root.errors == null) {
            return this;
        }
        this.merge(moreErrors.root.errors);
        return this;
    }
    
    public List<Object> getSources() {
        final List<Object> sources = $Lists.newArrayList();
        for (Errors e = this; e != null; e = e.parent) {
            if (e.source != $SourceProvider.UNKNOWN_SOURCE) {
                sources.add(0, e.source);
            }
        }
        return sources;
    }
    
    public void throwIfNewErrors(final int expectedSize) throws ErrorsException {
        if (this.size() == expectedSize) {
            return;
        }
        throw this.toException();
    }
    
    public ErrorsException toException() {
        return new ErrorsException(this);
    }
    
    public boolean hasErrors() {
        return this.root.errors != null;
    }
    
    public Errors addMessage(final String messageFormat, final Object... arguments) {
        return this.addMessage(null, messageFormat, arguments);
    }
    
    private Errors addMessage(final Throwable cause, final String messageFormat, final Object... arguments) {
        final String message = format(messageFormat, arguments);
        this.addMessage(new Message(this.getSources(), message, cause));
        return this;
    }
    
    public Errors addMessage(final Message message) {
        if (this.root.errors == null) {
            this.root.errors = (List<Message>)$Lists.newArrayList();
        }
        this.root.errors.add(message);
        return this;
    }
    
    public static String format(final String messageFormat, final Object... arguments) {
        for (int i = 0; i < arguments.length; ++i) {
            arguments[i] = convert(arguments[i]);
        }
        return String.format(messageFormat, arguments);
    }
    
    public List<Message> getMessages() {
        if (this.root.errors == null) {
            return (List<Message>)$ImmutableList.of();
        }
        final List<Message> result = (List<Message>)$Lists.newArrayList((Iterable<?>)this.root.errors);
        Collections.sort(result, new Comparator<Message>() {
            public int compare(final Message a, final Message b) {
                return a.getSource().compareTo(b.getSource());
            }
        });
        return result;
    }
    
    public static String format(final String heading, final Collection<Message> errorMessages) {
        final Formatter fmt = new Formatter().format(heading, new Object[0]).format(":%n%n", new Object[0]);
        int index = 1;
        final boolean displayCauses = getOnlyCause(errorMessages) == null;
        for (final Message errorMessage : errorMessages) {
            fmt.format("%s) %s%n", index++, errorMessage.getMessage());
            final List<Object> dependencies = errorMessage.getSources();
            for (int i = dependencies.size() - 1; i >= 0; --i) {
                final Object source = dependencies.get(i);
                formatSource(fmt, source);
            }
            final Throwable cause = errorMessage.getCause();
            if (displayCauses && cause != null) {
                final StringWriter writer = new StringWriter();
                cause.printStackTrace(new PrintWriter(writer));
                fmt.format("Caused by: %s", writer.getBuffer());
            }
            fmt.format("%n", new Object[0]);
        }
        if (errorMessages.size() == 1) {
            fmt.format("1 error", new Object[0]);
        }
        else {
            fmt.format("%s errors", errorMessages.size());
        }
        return fmt.toString();
    }
    
    public <T> T checkForNull(final T value, final Object source, final Dependency<?> dependency) throws ErrorsException {
        if (value != null || dependency.isNullable()) {
            return value;
        }
        final int parameterIndex = dependency.getParameterIndex();
        final String parameterName = (parameterIndex != -1) ? ("parameter " + parameterIndex + " of ") : "";
        this.addMessage("null returned by binding at %s%n but %s%s is not @Nullable", source, parameterName, dependency.getInjectionPoint().getMember());
        throw this.toException();
    }
    
    public static Throwable getOnlyCause(final Collection<Message> messages) {
        Throwable onlyCause = null;
        for (final Message message : messages) {
            final Throwable messageCause = message.getCause();
            if (messageCause == null) {
                continue;
            }
            if (onlyCause != null) {
                return null;
            }
            onlyCause = messageCause;
        }
        return onlyCause;
    }
    
    public int size() {
        return (this.root.errors == null) ? 0 : this.root.errors.size();
    }
    
    public static Object convert(final Object o) {
        for (final Converter<?> converter : Errors.converters) {
            if (converter.appliesTo(o)) {
                return converter.convert(o);
            }
        }
        return o;
    }
    
    public static void formatSource(final Formatter formatter, final Object source) {
        if (source instanceof Dependency) {
            final Dependency<?> dependency = (Dependency<?>)source;
            final InjectionPoint injectionPoint = dependency.getInjectionPoint();
            if (injectionPoint != null) {
                formatInjectionPoint(formatter, dependency, injectionPoint);
            }
            else {
                formatSource(formatter, dependency.getKey());
            }
        }
        else if (source instanceof InjectionPoint) {
            formatInjectionPoint(formatter, null, (InjectionPoint)source);
        }
        else if (source instanceof Class) {
            formatter.format("  at %s%n", $StackTraceElements.forType((Class<?>)source));
        }
        else if (source instanceof Member) {
            formatter.format("  at %s%n", $StackTraceElements.forMember((Member)source));
        }
        else if (source instanceof TypeLiteral) {
            formatter.format("  while locating %s%n", source);
        }
        else if (source instanceof Key) {
            final Key<?> key = (Key<?>)source;
            formatter.format("  while locating %s%n", convert(key));
        }
        else {
            formatter.format("  at %s%n", source);
        }
    }
    
    public static void formatInjectionPoint(final Formatter formatter, Dependency<?> dependency, final InjectionPoint injectionPoint) {
        final Member member = injectionPoint.getMember();
        final Class<? extends Member> memberType = $Classes.memberType(member);
        if (memberType == Field.class) {
            dependency = injectionPoint.getDependencies().get(0);
            formatter.format("  while locating %s%n", convert(dependency.getKey()));
            formatter.format("    for field at %s%n", $StackTraceElements.forMember(member));
        }
        else if (dependency != null) {
            formatter.format("  while locating %s%n", convert(dependency.getKey()));
            formatter.format("    for parameter %s at %s%n", dependency.getParameterIndex(), $StackTraceElements.forMember(member));
        }
        else {
            formatSource(formatter, injectionPoint.getMember());
        }
    }
    
    static {
        converters = $ImmutableList.of(new Converter<Class>((Class)Class.class) {
            public String toString(final Class c) {
                return c.getName();
            }
        }, new Converter<Member>((Class)Member.class) {
            public String toString(final Member member) {
                return $Classes.toString(member);
            }
        }, new Converter<Key>((Class)Key.class) {
            public String toString(final Key key) {
                if (key.getAnnotationType() != null) {
                    return key.getTypeLiteral() + " annotated with " + ((key.getAnnotation() != null) ? key.getAnnotation() : key.getAnnotationType());
                }
                return key.getTypeLiteral().toString();
            }
        });
    }
    
    private abstract static class Converter<T>
    {
        final Class<T> type;
        
        Converter(final Class<T> type) {
            this.type = type;
        }
        
        boolean appliesTo(final Object o) {
            return o != null && this.type.isAssignableFrom(o.getClass());
        }
        
        String convert(final Object o) {
            return this.toString(this.type.cast(o));
        }
        
        abstract String toString(final T p0);
    }
}
