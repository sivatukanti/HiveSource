// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.Constants;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.Local;
import java.util.Map;
import java.util.HashMap;
import net.sf.cglib.core.ObjectSwitchCallback;
import org.objectweb.asm.Label;
import net.sf.cglib.core.ProcessSwitchCallback;
import net.sf.cglib.core.CodeEmitter;
import java.util.Iterator;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.MethodInfo;
import java.lang.reflect.InvocationTargetException;
import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.core.MethodInfoTransformer;
import net.sf.cglib.core.ClassEmitter;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import net.sf.cglib.core.Transformer;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import net.sf.cglib.core.TypeUtils;
import org.objectweb.asm.ClassVisitor;
import net.sf.cglib.core.DuplicatesPredicate;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.RejectModifierPredicate;
import java.util.Collection;
import net.sf.cglib.core.MethodWrapper;
import java.util.Set;
import java.util.List;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import org.objectweb.asm.Type;
import net.sf.cglib.core.AbstractClassGenerator;

public class Enhancer extends AbstractClassGenerator
{
    private static final CallbackFilter ALL_ZERO;
    private static final Source SOURCE;
    private static final EnhancerKey KEY_FACTORY;
    private static final String BOUND_FIELD = "CGLIB$BOUND";
    private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
    private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
    private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
    private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
    private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
    private static final Type FACTORY;
    private static final Type ILLEGAL_STATE_EXCEPTION;
    private static final Type ILLEGAL_ARGUMENT_EXCEPTION;
    private static final Type THREAD_LOCAL;
    private static final Type CALLBACK;
    private static final Type CALLBACK_ARRAY;
    private static final Signature CSTRUCT_NULL;
    private static final Signature SET_THREAD_CALLBACKS;
    private static final Signature SET_STATIC_CALLBACKS;
    private static final Signature NEW_INSTANCE;
    private static final Signature MULTIARG_NEW_INSTANCE;
    private static final Signature SINGLE_NEW_INSTANCE;
    private static final Signature SET_CALLBACK;
    private static final Signature GET_CALLBACK;
    private static final Signature SET_CALLBACKS;
    private static final Signature GET_CALLBACKS;
    private static final Signature THREAD_LOCAL_GET;
    private static final Signature THREAD_LOCAL_SET;
    private static final Signature BIND_CALLBACKS;
    private Class[] interfaces;
    private CallbackFilter filter;
    private Callback[] callbacks;
    private Type[] callbackTypes;
    private boolean classOnly;
    private Class superclass;
    private Class[] argumentTypes;
    private Object[] arguments;
    private boolean useFactory;
    private Long serialVersionUID;
    private boolean interceptDuringConstruction;
    
    public Enhancer() {
        super(Enhancer.SOURCE);
        this.useFactory = true;
        this.interceptDuringConstruction = true;
    }
    
    public void setSuperclass(final Class superclass) {
        if (superclass != null && superclass.isInterface()) {
            this.setInterfaces(new Class[] { superclass });
        }
        else if (superclass != null && superclass.equals(Object.class)) {
            this.superclass = null;
        }
        else {
            this.superclass = superclass;
        }
    }
    
    public void setInterfaces(final Class[] interfaces) {
        this.interfaces = interfaces;
    }
    
    public void setCallbackFilter(final CallbackFilter filter) {
        this.filter = filter;
    }
    
    public void setCallback(final Callback callback) {
        this.setCallbacks(new Callback[] { callback });
    }
    
    public void setCallbacks(final Callback[] callbacks) {
        if (callbacks != null && callbacks.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbacks = callbacks;
    }
    
    public void setUseFactory(final boolean useFactory) {
        this.useFactory = useFactory;
    }
    
    public void setInterceptDuringConstruction(final boolean interceptDuringConstruction) {
        this.interceptDuringConstruction = interceptDuringConstruction;
    }
    
    public void setCallbackType(final Class callbackType) {
        this.setCallbackTypes(new Class[] { callbackType });
    }
    
    public void setCallbackTypes(final Class[] callbackTypes) {
        if (callbackTypes != null && callbackTypes.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbackTypes = CallbackInfo.determineTypes(callbackTypes);
    }
    
    public Object create() {
        this.classOnly = false;
        this.argumentTypes = null;
        return this.createHelper();
    }
    
    public Object create(final Class[] argumentTypes, final Object[] arguments) {
        this.classOnly = false;
        if (argumentTypes == null || arguments == null || argumentTypes.length != arguments.length) {
            throw new IllegalArgumentException("Arguments must be non-null and of equal length");
        }
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
        return this.createHelper();
    }
    
    public Class createClass() {
        this.classOnly = true;
        return (Class)this.createHelper();
    }
    
    public void setSerialVersionUID(final Long sUID) {
        this.serialVersionUID = sUID;
    }
    
    private void validate() {
        if (this.classOnly ^ this.callbacks == null) {
            if (this.classOnly) {
                throw new IllegalStateException("createClass does not accept callbacks");
            }
            throw new IllegalStateException("Callbacks are required");
        }
        else {
            if (this.classOnly && this.callbackTypes == null) {
                throw new IllegalStateException("Callback types are required");
            }
            if (this.callbacks != null && this.callbackTypes != null) {
                if (this.callbacks.length != this.callbackTypes.length) {
                    throw new IllegalStateException("Lengths of callback and callback types array must be the same");
                }
                final Type[] check = CallbackInfo.determineTypes(this.callbacks);
                for (int i = 0; i < check.length; ++i) {
                    if (!check[i].equals(this.callbackTypes[i])) {
                        throw new IllegalStateException("Callback " + check[i] + " is not assignable to " + this.callbackTypes[i]);
                    }
                }
            }
            else if (this.callbacks != null) {
                this.callbackTypes = CallbackInfo.determineTypes(this.callbacks);
            }
            if (this.filter == null) {
                if (this.callbackTypes.length > 1) {
                    throw new IllegalStateException("Multiple callback types possible but no filter specified");
                }
                this.filter = Enhancer.ALL_ZERO;
            }
            if (this.interfaces != null) {
                for (int j = 0; j < this.interfaces.length; ++j) {
                    if (this.interfaces[j] == null) {
                        throw new IllegalStateException("Interfaces cannot be null");
                    }
                    if (!this.interfaces[j].isInterface()) {
                        throw new IllegalStateException(this.interfaces[j] + " is not an interface");
                    }
                }
            }
        }
    }
    
    private Object createHelper() {
        this.validate();
        if (this.superclass != null) {
            this.setNamePrefix(this.superclass.getName());
        }
        else if (this.interfaces != null) {
            this.setNamePrefix(this.interfaces[ReflectUtils.findPackageProtected(this.interfaces)].getName());
        }
        return super.create(Enhancer.KEY_FACTORY.newInstance((this.superclass != null) ? this.superclass.getName() : null, ReflectUtils.getNames(this.interfaces), this.filter, this.callbackTypes, this.useFactory, this.interceptDuringConstruction, this.serialVersionUID));
    }
    
    protected ClassLoader getDefaultClassLoader() {
        if (this.superclass != null) {
            return this.superclass.getClassLoader();
        }
        if (this.interfaces != null) {
            return this.interfaces[0].getClassLoader();
        }
        return null;
    }
    
    private Signature rename(final Signature sig, final int index) {
        return new Signature("CGLIB$" + sig.getName() + "$" + index, sig.getDescriptor());
    }
    
    public static void getMethods(final Class superclass, final Class[] interfaces, final List methods) {
        getMethods(superclass, interfaces, methods, null, null);
    }
    
    private static void getMethods(final Class superclass, final Class[] interfaces, final List methods, final List interfaceMethods, final Set forcePublic) {
        ReflectUtils.addAllMethods(superclass, methods);
        final List target = (interfaceMethods != null) ? interfaceMethods : methods;
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                if (interfaces[i] != Factory.class) {
                    ReflectUtils.addAllMethods(interfaces[i], target);
                }
            }
        }
        if (interfaceMethods != null) {
            if (forcePublic != null) {
                forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
            }
            methods.addAll(interfaceMethods);
        }
        CollectionUtils.filter(methods, new RejectModifierPredicate(8));
        CollectionUtils.filter(methods, new VisibilityPredicate(superclass, true));
        CollectionUtils.filter(methods, new DuplicatesPredicate());
        CollectionUtils.filter(methods, new RejectModifierPredicate(16));
    }
    
    public void generateClass(final ClassVisitor v) throws Exception {
        final Class sc = (this.superclass == null) ? Object.class : this.superclass;
        if (TypeUtils.isFinal(sc.getModifiers())) {
            throw new IllegalArgumentException("Cannot subclass final class " + sc);
        }
        final List constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
        this.filterConstructors(sc, constructors);
        final List actualMethods = new ArrayList();
        final List interfaceMethods = new ArrayList();
        final Set forcePublic = new HashSet();
        getMethods(sc, this.interfaces, actualMethods, interfaceMethods, forcePublic);
        final List methods = CollectionUtils.transform(actualMethods, new Transformer() {
            public Object transform(final Object value) {
                final Method method = (Method)value;
                int modifiers = 0x10 | (method.getModifiers() & 0xFFFFFBFF & 0xFFFFFEFF & 0xFFFFFFDF);
                if (forcePublic.contains(MethodWrapper.create(method))) {
                    modifiers = ((modifiers & 0xFFFFFFFB) | 0x1);
                }
                return ReflectUtils.getMethodInfo(method, modifiers);
            }
        });
        final ClassEmitter e = new ClassEmitter(v);
        e.begin_class(46, 1, this.getClassName(), Type.getType(sc), this.useFactory ? TypeUtils.add(TypeUtils.getTypes(this.interfaces), Enhancer.FACTORY) : TypeUtils.getTypes(this.interfaces), "<generated>");
        final List constructorInfo = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
        e.declare_field(2, "CGLIB$BOUND", Type.BOOLEAN_TYPE, null);
        if (!this.interceptDuringConstruction) {
            e.declare_field(2, "CGLIB$CONSTRUCTED", Type.BOOLEAN_TYPE, null);
        }
        e.declare_field(26, "CGLIB$THREAD_CALLBACKS", Enhancer.THREAD_LOCAL, null);
        e.declare_field(26, "CGLIB$STATIC_CALLBACKS", Enhancer.CALLBACK_ARRAY, null);
        if (this.serialVersionUID != null) {
            e.declare_field(26, "serialVersionUID", Type.LONG_TYPE, this.serialVersionUID);
        }
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            e.declare_field(2, getCallbackField(i), this.callbackTypes[i], null);
        }
        this.emitMethods(e, methods, actualMethods);
        this.emitConstructors(e, constructorInfo);
        this.emitSetThreadCallbacks(e);
        this.emitSetStaticCallbacks(e);
        this.emitBindCallbacks(e);
        if (this.useFactory) {
            final int[] keys = this.getCallbackKeys();
            this.emitNewInstanceCallbacks(e);
            this.emitNewInstanceCallback(e);
            this.emitNewInstanceMultiarg(e, constructorInfo);
            this.emitGetCallback(e, keys);
            this.emitSetCallback(e, keys);
            this.emitGetCallbacks(e);
            this.emitSetCallbacks(e);
        }
        e.end_class();
    }
    
    protected void filterConstructors(final Class sc, final List constructors) {
        CollectionUtils.filter(constructors, new VisibilityPredicate(sc, true));
        if (constructors.size() == 0) {
            throw new IllegalArgumentException("No visible constructors in " + sc);
        }
    }
    
    protected Object firstInstance(final Class type) throws Exception {
        if (this.classOnly) {
            return type;
        }
        return this.createUsingReflection(type);
    }
    
    protected Object nextInstance(final Object instance) {
        final Class protoclass = (instance instanceof Class) ? ((Class)instance) : instance.getClass();
        if (this.classOnly) {
            return protoclass;
        }
        if (!(instance instanceof Factory)) {
            return this.createUsingReflection(protoclass);
        }
        if (this.argumentTypes != null) {
            return ((Factory)instance).newInstance(this.argumentTypes, this.arguments, this.callbacks);
        }
        return ((Factory)instance).newInstance(this.callbacks);
    }
    
    public static void registerCallbacks(final Class generatedClass, final Callback[] callbacks) {
        setThreadCallbacks(generatedClass, callbacks);
    }
    
    public static void registerStaticCallbacks(final Class generatedClass, final Callback[] callbacks) {
        setCallbacksHelper(generatedClass, callbacks, "CGLIB$SET_STATIC_CALLBACKS");
    }
    
    public static boolean isEnhanced(final Class type) {
        try {
            getCallbacksSetter(type, "CGLIB$SET_THREAD_CALLBACKS");
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    private static void setThreadCallbacks(final Class type, final Callback[] callbacks) {
        setCallbacksHelper(type, callbacks, "CGLIB$SET_THREAD_CALLBACKS");
    }
    
    private static void setCallbacksHelper(final Class type, final Callback[] callbacks, final String methodName) {
        try {
            final Method setter = getCallbacksSetter(type, methodName);
            setter.invoke(null, callbacks);
        }
        catch (NoSuchMethodException e3) {
            throw new IllegalArgumentException(type + " is not an enhanced class");
        }
        catch (IllegalAccessException e) {
            throw new CodeGenerationException(e);
        }
        catch (InvocationTargetException e2) {
            throw new CodeGenerationException(e2);
        }
    }
    
    private static Method getCallbacksSetter(final Class type, final String methodName) throws NoSuchMethodException {
        return type.getDeclaredMethod(methodName, Callback[].class);
    }
    
    private Object createUsingReflection(final Class type) {
        setThreadCallbacks(type, this.callbacks);
        try {
            if (this.argumentTypes != null) {
                return ReflectUtils.newInstance(type, this.argumentTypes, this.arguments);
            }
            return ReflectUtils.newInstance(type);
        }
        finally {
            setThreadCallbacks(type, null);
        }
    }
    
    public static Object create(final Class type, final Callback callback) {
        final Enhancer e = new Enhancer();
        e.setSuperclass(type);
        e.setCallback(callback);
        return e.create();
    }
    
    public static Object create(final Class superclass, final Class[] interfaces, final Callback callback) {
        final Enhancer e = new Enhancer();
        e.setSuperclass(superclass);
        e.setInterfaces(interfaces);
        e.setCallback(callback);
        return e.create();
    }
    
    public static Object create(final Class superclass, final Class[] interfaces, final CallbackFilter filter, final Callback[] callbacks) {
        final Enhancer e = new Enhancer();
        e.setSuperclass(superclass);
        e.setInterfaces(interfaces);
        e.setCallbackFilter(filter);
        e.setCallbacks(callbacks);
        return e.create();
    }
    
    private void emitConstructors(final ClassEmitter ce, final List constructors) {
        boolean seenNull = false;
        for (final MethodInfo constructor : constructors) {
            final CodeEmitter e = EmitUtils.begin_method(ce, constructor, 1);
            e.load_this();
            e.dup();
            e.load_args();
            final Signature sig = constructor.getSignature();
            seenNull = (seenNull || sig.getDescriptor().equals("()V"));
            e.super_invoke_constructor(sig);
            e.invoke_static_this(Enhancer.BIND_CALLBACKS);
            if (!this.interceptDuringConstruction) {
                e.load_this();
                e.push(1);
                e.putfield("CGLIB$CONSTRUCTED");
            }
            e.return_value();
            e.end_method();
        }
        if (!this.classOnly && !seenNull && this.arguments == null) {
            throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
        }
    }
    
    private int[] getCallbackKeys() {
        final int[] keys = new int[this.callbackTypes.length];
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            keys[i] = i;
        }
        return keys;
    }
    
    private void emitGetCallback(final ClassEmitter ce, final int[] keys) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.GET_CALLBACK, null);
        e.load_this();
        e.invoke_static_this(Enhancer.BIND_CALLBACKS);
        e.load_this();
        e.load_arg(0);
        e.process_switch(keys, new ProcessSwitchCallback() {
            public void processCase(final int key, final Label end) {
                e.getfield(getCallbackField(key));
                e.goTo(end);
            }
            
            public void processDefault() {
                e.pop();
                e.aconst_null();
            }
        });
        e.return_value();
        e.end_method();
    }
    
    private void emitSetCallback(final ClassEmitter ce, final int[] keys) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.SET_CALLBACK, null);
        e.load_arg(0);
        e.process_switch(keys, new ProcessSwitchCallback() {
            public void processCase(final int key, final Label end) {
                e.load_this();
                e.load_arg(1);
                e.checkcast(Enhancer.this.callbackTypes[key]);
                e.putfield(getCallbackField(key));
                e.goTo(end);
            }
            
            public void processDefault() {
            }
        });
        e.return_value();
        e.end_method();
    }
    
    private void emitSetCallbacks(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.SET_CALLBACKS, null);
        e.load_this();
        e.load_arg(0);
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            e.dup2();
            e.aaload(i);
            e.checkcast(this.callbackTypes[i]);
            e.putfield(getCallbackField(i));
        }
        e.return_value();
        e.end_method();
    }
    
    private void emitGetCallbacks(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.GET_CALLBACKS, null);
        e.load_this();
        e.invoke_static_this(Enhancer.BIND_CALLBACKS);
        e.load_this();
        e.push(this.callbackTypes.length);
        e.newarray(Enhancer.CALLBACK);
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            e.dup();
            e.push(i);
            e.load_this();
            e.getfield(getCallbackField(i));
            e.aastore();
        }
        e.return_value();
        e.end_method();
    }
    
    private void emitNewInstanceCallbacks(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.NEW_INSTANCE, null);
        e.load_arg(0);
        e.invoke_static_this(Enhancer.SET_THREAD_CALLBACKS);
        this.emitCommonNewInstance(e);
    }
    
    private void emitCommonNewInstance(final CodeEmitter e) {
        e.new_instance_this();
        e.dup();
        e.invoke_constructor_this();
        e.aconst_null();
        e.invoke_static_this(Enhancer.SET_THREAD_CALLBACKS);
        e.return_value();
        e.end_method();
    }
    
    private void emitNewInstanceCallback(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.SINGLE_NEW_INSTANCE, null);
        switch (this.callbackTypes.length) {
            case 0: {
                break;
            }
            case 1: {
                e.push(1);
                e.newarray(Enhancer.CALLBACK);
                e.dup();
                e.push(0);
                e.load_arg(0);
                e.aastore();
                e.invoke_static_this(Enhancer.SET_THREAD_CALLBACKS);
                break;
            }
            default: {
                e.throw_exception(Enhancer.ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
                break;
            }
        }
        this.emitCommonNewInstance(e);
    }
    
    private void emitNewInstanceMultiarg(final ClassEmitter ce, final List constructors) {
        final CodeEmitter e = ce.begin_method(1, Enhancer.MULTIARG_NEW_INSTANCE, null);
        e.load_arg(2);
        e.invoke_static_this(Enhancer.SET_THREAD_CALLBACKS);
        e.new_instance_this();
        e.dup();
        e.load_arg(0);
        EmitUtils.constructor_switch(e, constructors, new ObjectSwitchCallback() {
            public void processCase(final Object key, final Label end) {
                final MethodInfo constructor = (MethodInfo)key;
                final Type[] types = constructor.getSignature().getArgumentTypes();
                for (int i = 0; i < types.length; ++i) {
                    e.load_arg(1);
                    e.push(i);
                    e.aaload();
                    e.unbox(types[i]);
                }
                e.invoke_constructor_this(constructor.getSignature());
                e.goTo(end);
            }
            
            public void processDefault() {
                e.throw_exception(Enhancer.ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
            }
        });
        e.aconst_null();
        e.invoke_static_this(Enhancer.SET_THREAD_CALLBACKS);
        e.return_value();
        e.end_method();
    }
    
    private void emitMethods(final ClassEmitter ce, final List methods, final List actualMethods) {
        final CallbackGenerator[] generators = CallbackInfo.getGenerators(this.callbackTypes);
        final Map groups = new HashMap();
        final Map indexes = new HashMap();
        final Map originalModifiers = new HashMap();
        final Map positions = CollectionUtils.getIndexMap(methods);
        final Iterator it1 = methods.iterator();
        final Iterator it2 = (actualMethods != null) ? actualMethods.iterator() : null;
        while (it1.hasNext()) {
            final MethodInfo method = it1.next();
            final Method actualMethod = (it2 != null) ? it2.next() : null;
            final int index = this.filter.accept(actualMethod);
            if (index >= this.callbackTypes.length) {
                throw new IllegalArgumentException("Callback filter returned an index that is too large: " + index);
            }
            originalModifiers.put(method, new Integer((actualMethod != null) ? actualMethod.getModifiers() : method.getModifiers()));
            indexes.put(method, new Integer(index));
            List group = groups.get(generators[index]);
            if (group == null) {
                groups.put(generators[index], group = new ArrayList(methods.size()));
            }
            group.add(method);
        }
        final Set seenGen = new HashSet();
        final CodeEmitter se = ce.getStaticHook();
        se.new_instance(Enhancer.THREAD_LOCAL);
        se.dup();
        se.invoke_constructor(Enhancer.THREAD_LOCAL, Enhancer.CSTRUCT_NULL);
        se.putfield("CGLIB$THREAD_CALLBACKS");
        final Object[] state = { null };
        final CallbackGenerator.Context context = new CallbackGenerator.Context() {
            public ClassLoader getClassLoader() {
                return Enhancer.this.getClassLoader();
            }
            
            public int getOriginalModifiers(final MethodInfo method) {
                return originalModifiers.get(method);
            }
            
            public int getIndex(final MethodInfo method) {
                return indexes.get(method);
            }
            
            public void emitCallback(final CodeEmitter e, final int index) {
                Enhancer.this.emitCurrentCallback(e, index);
            }
            
            public Signature getImplSignature(final MethodInfo method) {
                return Enhancer.this.rename(method.getSignature(), positions.get(method));
            }
            
            public CodeEmitter beginMethod(final ClassEmitter ce, final MethodInfo method) {
                final CodeEmitter e = EmitUtils.begin_method(ce, method);
                if (!Enhancer.this.interceptDuringConstruction && !TypeUtils.isAbstract(method.getModifiers())) {
                    final Label constructed = e.make_label();
                    e.load_this();
                    e.getfield("CGLIB$CONSTRUCTED");
                    e.if_jump(154, constructed);
                    e.load_this();
                    e.load_args();
                    e.super_invoke();
                    e.return_value();
                    e.mark(constructed);
                }
                return e;
            }
        };
        for (int i = 0; i < this.callbackTypes.length; ++i) {
            final CallbackGenerator gen = generators[i];
            if (!seenGen.contains(gen)) {
                seenGen.add(gen);
                final List fmethods = groups.get(gen);
                if (fmethods != null) {
                    try {
                        gen.generate(ce, context, fmethods);
                        gen.generateStatic(se, context, fmethods);
                    }
                    catch (RuntimeException x) {
                        throw x;
                    }
                    catch (Exception x2) {
                        throw new CodeGenerationException(x2);
                    }
                }
            }
        }
        se.return_value();
        se.end_method();
    }
    
    private void emitSetThreadCallbacks(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(9, Enhancer.SET_THREAD_CALLBACKS, null);
        e.getfield("CGLIB$THREAD_CALLBACKS");
        e.load_arg(0);
        e.invoke_virtual(Enhancer.THREAD_LOCAL, Enhancer.THREAD_LOCAL_SET);
        e.return_value();
        e.end_method();
    }
    
    private void emitSetStaticCallbacks(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(9, Enhancer.SET_STATIC_CALLBACKS, null);
        e.load_arg(0);
        e.putfield("CGLIB$STATIC_CALLBACKS");
        e.return_value();
        e.end_method();
    }
    
    private void emitCurrentCallback(final CodeEmitter e, final int index) {
        e.load_this();
        e.getfield(getCallbackField(index));
        e.dup();
        final Label end = e.make_label();
        e.ifnonnull(end);
        e.pop();
        e.load_this();
        e.invoke_static_this(Enhancer.BIND_CALLBACKS);
        e.load_this();
        e.getfield(getCallbackField(index));
        e.mark(end);
    }
    
    private void emitBindCallbacks(final ClassEmitter ce) {
        final CodeEmitter e = ce.begin_method(26, Enhancer.BIND_CALLBACKS, null);
        final Local me = e.make_local();
        e.load_arg(0);
        e.checkcast_this();
        e.store_local(me);
        final Label end = e.make_label();
        e.load_local(me);
        e.getfield("CGLIB$BOUND");
        e.if_jump(154, end);
        e.load_local(me);
        e.push(1);
        e.putfield("CGLIB$BOUND");
        e.getfield("CGLIB$THREAD_CALLBACKS");
        e.invoke_virtual(Enhancer.THREAD_LOCAL, Enhancer.THREAD_LOCAL_GET);
        e.dup();
        final Label found_callback = e.make_label();
        e.ifnonnull(found_callback);
        e.pop();
        e.getfield("CGLIB$STATIC_CALLBACKS");
        e.dup();
        e.ifnonnull(found_callback);
        e.pop();
        e.goTo(end);
        e.mark(found_callback);
        e.checkcast(Enhancer.CALLBACK_ARRAY);
        e.load_local(me);
        e.swap();
        for (int i = this.callbackTypes.length - 1; i >= 0; --i) {
            if (i != 0) {
                e.dup2();
            }
            e.aaload(i);
            e.checkcast(this.callbackTypes[i]);
            e.putfield(getCallbackField(i));
        }
        e.mark(end);
        e.return_value();
        e.end_method();
    }
    
    private static String getCallbackField(final int index) {
        return "CGLIB$CALLBACK_" + index;
    }
    
    static {
        ALL_ZERO = new CallbackFilter() {
            public int accept(final Method method) {
                return 0;
            }
        };
        SOURCE = new Source(Enhancer.class.getName());
        KEY_FACTORY = (EnhancerKey)KeyFactory.create(EnhancerKey.class);
        FACTORY = TypeUtils.parseType("net.sf.cglib.proxy.Factory");
        ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
        ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
        THREAD_LOCAL = TypeUtils.parseType("ThreadLocal");
        CALLBACK = TypeUtils.parseType("net.sf.cglib.proxy.Callback");
        CALLBACK_ARRAY = Type.getType(Callback[].class);
        CSTRUCT_NULL = TypeUtils.parseConstructor("");
        SET_THREAD_CALLBACKS = new Signature("CGLIB$SET_THREAD_CALLBACKS", Type.VOID_TYPE, new Type[] { Enhancer.CALLBACK_ARRAY });
        SET_STATIC_CALLBACKS = new Signature("CGLIB$SET_STATIC_CALLBACKS", Type.VOID_TYPE, new Type[] { Enhancer.CALLBACK_ARRAY });
        NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { Enhancer.CALLBACK_ARRAY });
        MULTIARG_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { Constants.TYPE_CLASS_ARRAY, Constants.TYPE_OBJECT_ARRAY, Enhancer.CALLBACK_ARRAY });
        SINGLE_NEW_INSTANCE = new Signature("newInstance", Constants.TYPE_OBJECT, new Type[] { Enhancer.CALLBACK });
        SET_CALLBACK = new Signature("setCallback", Type.VOID_TYPE, new Type[] { Type.INT_TYPE, Enhancer.CALLBACK });
        GET_CALLBACK = new Signature("getCallback", Enhancer.CALLBACK, new Type[] { Type.INT_TYPE });
        SET_CALLBACKS = new Signature("setCallbacks", Type.VOID_TYPE, new Type[] { Enhancer.CALLBACK_ARRAY });
        GET_CALLBACKS = new Signature("getCallbacks", Enhancer.CALLBACK_ARRAY, new Type[0]);
        THREAD_LOCAL_GET = TypeUtils.parseSignature("Object get()");
        THREAD_LOCAL_SET = TypeUtils.parseSignature("void set(Object)");
        BIND_CALLBACKS = TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");
    }
    
    public interface EnhancerKey
    {
        Object newInstance(final String p0, final String[] p1, final CallbackFilter p2, final Type[] p3, final boolean p4, final boolean p5, final Long p6);
    }
}
