// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.util.Arrays;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import org.apache.htrace.shaded.commons.logging.Log;
import java.io.Closeable;

public class Tracer implements Closeable
{
    private static final Log LOG;
    public static final String SPAN_RECEIVER_CLASSES_KEY = "span.receiver.classes";
    public static final String SAMPLER_CLASSES_KEY = "sampler.classes";
    static final ThreadLocal<TraceScope> threadLocalScope;
    private static final SpanId[] EMPTY_PARENT_ARRAY;
    private final String tracerId;
    private TracerPool tracerPool;
    private final ThreadLocalContext threadContext;
    private final NullScope nullScope;
    private volatile Sampler[] curSamplers;
    
    static void throwClientError(final String str) {
        Tracer.LOG.error(str);
        throw new RuntimeException(str);
    }
    
    public static Tracer curThreadTracer() {
        final TraceScope traceScope = Tracer.threadLocalScope.get();
        if (traceScope == null) {
            return null;
        }
        return traceScope.tracer;
    }
    
    Tracer(final String tracerId, final TracerPool tracerPool, final Sampler[] curSamplers) {
        this.tracerId = tracerId;
        this.tracerPool = tracerPool;
        this.threadContext = new ThreadLocalContext();
        this.nullScope = new NullScope(this);
        this.curSamplers = curSamplers;
    }
    
    public String getTracerId() {
        return this.tracerId;
    }
    
    private TraceScope newScopeImpl(final ThreadContext context, final String description) {
        final Span span = new MilliSpan.Builder().tracerId(this.tracerId).begin(System.currentTimeMillis()).description(description).parents(Tracer.EMPTY_PARENT_ARRAY).spanId(SpanId.fromRandom()).build();
        return context.pushNewScope(this, span, null);
    }
    
    private TraceScope newScopeImpl(final ThreadContext context, final String description, final TraceScope parentScope) {
        final SpanId parentId = parentScope.getSpan().getSpanId();
        final Span span = new MilliSpan.Builder().tracerId(this.tracerId).begin(System.currentTimeMillis()).description(description).parents(new SpanId[] { parentId }).spanId(parentId.newChildId()).build();
        return context.pushNewScope(this, span, parentScope);
    }
    
    private TraceScope newScopeImpl(final ThreadContext context, final String description, final SpanId parentId) {
        final Span span = new MilliSpan.Builder().tracerId(this.tracerId).begin(System.currentTimeMillis()).description(description).parents(new SpanId[] { parentId }).spanId(parentId.newChildId()).build();
        return context.pushNewScope(this, span, null);
    }
    
    private TraceScope newScopeImpl(final ThreadContext context, final String description, final TraceScope parentScope, final SpanId secondParentId) {
        final SpanId parentId = parentScope.getSpan().getSpanId();
        final Span span = new MilliSpan.Builder().tracerId(this.tracerId).begin(System.currentTimeMillis()).description(description).parents(new SpanId[] { parentId, secondParentId }).spanId(parentId.newChildId()).build();
        return context.pushNewScope(this, span, parentScope);
    }
    
    public TraceScope newScope(final String description, final SpanId parentId) {
        final TraceScope parentScope = Tracer.threadLocalScope.get();
        final ThreadContext context = this.threadContext.get();
        if (parentScope != null) {
            if (parentId.isValid() && !parentId.equals(parentScope.getSpan().getSpanId())) {
                return this.newScopeImpl(context, description, parentScope, parentId);
            }
            return this.newScopeImpl(context, description, parentScope);
        }
        else {
            if (parentId.isValid()) {
                return this.newScopeImpl(context, description, parentId);
            }
            if (!context.isTopLevel()) {
                context.pushScope();
                return this.nullScope;
            }
            if (!this.sample()) {
                context.pushScope();
                return this.nullScope;
            }
            return this.newScopeImpl(context, description);
        }
    }
    
    public TraceScope newScope(final String description) {
        final TraceScope parentScope = Tracer.threadLocalScope.get();
        final ThreadContext context = this.threadContext.get();
        if (parentScope != null) {
            return this.newScopeImpl(context, description, parentScope);
        }
        if (!context.isTopLevel()) {
            context.pushScope();
            return this.nullScope;
        }
        if (!this.sample()) {
            context.pushScope();
            return this.nullScope;
        }
        return this.newScopeImpl(context, description);
    }
    
    public TraceScope newNullScope() {
        final ThreadContext context = this.threadContext.get();
        context.pushScope();
        return this.nullScope;
    }
    
    public <V> Callable<V> wrap(final Callable<V> callable, final String description) {
        final TraceScope parentScope = Tracer.threadLocalScope.get();
        if (parentScope == null) {
            return callable;
        }
        return new TraceCallable<V>(this, parentScope, callable, description);
    }
    
    public Runnable wrap(final Runnable runnable, final String description) {
        final TraceScope parentScope = Tracer.threadLocalScope.get();
        if (parentScope == null) {
            return runnable;
        }
        return new TraceRunnable(this, parentScope, runnable, description);
    }
    
    public TraceExecutorService newTraceExecutorService(final ExecutorService impl, final String scopeName) {
        return new TraceExecutorService(this, scopeName, impl);
    }
    
    public TracerPool getTracerPool() {
        if (this.tracerPool == null) {
            throwClientError(this.toString() + " is closed.");
        }
        return this.tracerPool;
    }
    
     <T, V> T createProxy(final T instance) {
        final Tracer tracer = this;
        final InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(final Object obj, final Method method, final Object[] args) throws Throwable {
                final TraceScope scope = tracer.newScope(method.getName());
                try {
                    return method.invoke(instance, args);
                }
                catch (Throwable ex) {
                    ex.printStackTrace();
                    throw ex;
                }
                finally {
                    scope.close();
                }
            }
        };
        return (T)Proxy.newProxyInstance(instance.getClass().getClassLoader(), instance.getClass().getInterfaces(), handler);
    }
    
    private boolean sample() {
        final Sampler[] arr$;
        final Sampler[] samplers = arr$ = this.curSamplers;
        for (final Sampler sampler : arr$) {
            if (sampler.next()) {
                return true;
            }
        }
        return false;
    }
    
    public Sampler[] getSamplers() {
        return this.curSamplers;
    }
    
    public synchronized boolean addSampler(final Sampler sampler) {
        if (this.tracerPool == null) {
            throwClientError(this.toString() + " is closed.");
        }
        final Sampler[] samplers = this.curSamplers;
        for (int i = 0; i < samplers.length; ++i) {
            if (samplers[i] == sampler) {
                return false;
            }
        }
        final Sampler[] newSamplers = Arrays.copyOf(samplers, samplers.length + 1);
        newSamplers[samplers.length] = sampler;
        this.curSamplers = newSamplers;
        return true;
    }
    
    public synchronized boolean removeSampler(final Sampler sampler) {
        if (this.tracerPool == null) {
            throwClientError(this.toString() + " is closed.");
        }
        final Sampler[] samplers = this.curSamplers;
        for (int i = 0; i < samplers.length; ++i) {
            if (samplers[i] == sampler) {
                final Sampler[] newSamplers = new Sampler[samplers.length - 1];
                System.arraycopy(samplers, 0, newSamplers, 0, i);
                System.arraycopy(samplers, i + 1, newSamplers, i, samplers.length - i - 1);
                this.curSamplers = newSamplers;
                return true;
            }
        }
        return false;
    }
    
    void detachScope(final TraceScope scope) {
        final TraceScope curScope = Tracer.threadLocalScope.get();
        if (curScope != scope) {
            throwClientError("Can't detach TraceScope for " + scope.getSpan().toJson() + " because it is not the current " + "TraceScope in thread " + Thread.currentThread().getName());
        }
        final ThreadContext context = this.threadContext.get();
        context.popScope();
        Tracer.threadLocalScope.set(scope.getParent());
    }
    
    void reattachScope(final TraceScope scope) {
        final TraceScope parent = Tracer.threadLocalScope.get();
        Tracer.threadLocalScope.set(scope);
        final ThreadContext context = this.threadContext.get();
        context.pushScope();
        scope.setParent(parent);
    }
    
    void closeScope(final TraceScope scope) {
        final TraceScope curScope = Tracer.threadLocalScope.get();
        if (curScope != scope) {
            throwClientError("Can't close TraceScope for " + scope.getSpan().toJson() + " because it is not the current " + "TraceScope in thread " + Thread.currentThread().getName());
        }
        if (this.tracerPool == null) {
            throwClientError(this.toString() + " is closed.");
        }
        final SpanReceiver[] receivers = this.tracerPool.getReceivers();
        if (receivers == null) {
            throwClientError(this.toString() + " is closed.");
        }
        final ThreadContext context = this.threadContext.get();
        context.popScope();
        Tracer.threadLocalScope.set(scope.getParent());
        scope.setParent(null);
        final Span span = scope.getSpan();
        span.stop();
        for (final SpanReceiver receiver : receivers) {
            receiver.receiveSpan(span);
        }
    }
    
    void popNullScope() {
        final TraceScope curScope = Tracer.threadLocalScope.get();
        if (curScope != null) {
            throwClientError("Attempted to close an empty scope, but it was not the current thread scope in thread " + Thread.currentThread().getName());
        }
        final ThreadContext context = this.threadContext.get();
        context.popScope();
    }
    
    public static Span getCurrentSpan() {
        final TraceScope curScope = Tracer.threadLocalScope.get();
        if (curScope == null) {
            return null;
        }
        return curScope.getSpan();
    }
    
    public static SpanId getCurrentSpanId() {
        final TraceScope curScope = Tracer.threadLocalScope.get();
        if (curScope == null) {
            return SpanId.INVALID;
        }
        return curScope.getSpan().getSpanId();
    }
    
    @Override
    public synchronized void close() {
        if (this.tracerPool == null) {
            return;
        }
        this.curSamplers = new Sampler[0];
        this.tracerPool.removeTracer(this);
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other;
    }
    
    @Override
    public String toString() {
        return "Tracer(" + this.tracerId + ")";
    }
    
    static {
        LOG = LogFactory.getLog(Tracer.class);
        threadLocalScope = new ThreadLocal<TraceScope>();
        EMPTY_PARENT_ARRAY = new SpanId[0];
    }
    
    public static class Builder
    {
        private String name;
        private HTraceConfiguration conf;
        private ClassLoader classLoader;
        private TracerPool tracerPool;
        
        @Deprecated
        public Builder() {
            this.conf = HTraceConfiguration.EMPTY;
            this.classLoader = Builder.class.getClassLoader();
            this.tracerPool = TracerPool.GLOBAL;
        }
        
        public Builder(final String name) {
            this.conf = HTraceConfiguration.EMPTY;
            this.classLoader = Builder.class.getClassLoader();
            this.tracerPool = TracerPool.GLOBAL;
            this.name(name);
        }
        
        @Deprecated
        public Builder name(final String name) {
            this.name = name;
            return this;
        }
        
        public Builder conf(final HTraceConfiguration conf) {
            this.conf = conf;
            return this;
        }
        
        public Builder tracerPool(final TracerPool tracerPool) {
            this.tracerPool = tracerPool;
            return this;
        }
        
        private void loadSamplers(final List<Sampler> samplers) {
            final String classNamesStr = this.conf.get("sampler.classes", "");
            final List<String> classNames = this.getClassNamesFromConf(classNamesStr);
            final StringBuilder bld = new StringBuilder();
            String prefix = "";
            for (final String className : classNames) {
                try {
                    final Sampler sampler = new Sampler.Builder(this.conf).className(className).classLoader(this.classLoader).build();
                    samplers.add(sampler);
                    bld.append(prefix).append(className);
                    prefix = ", ";
                }
                catch (Throwable e) {
                    Tracer.LOG.error("Failed to create SpanReceiver of type " + className, e);
                }
            }
            String resultString = bld.toString();
            if (resultString.isEmpty()) {
                resultString = "no samplers";
            }
            Tracer.LOG.debug("sampler.classes = " + classNamesStr + "; loaded " + resultString);
        }
        
        private void loadSpanReceivers() {
            final String classNamesStr = this.conf.get("span.receiver.classes", "");
            final List<String> classNames = this.getClassNamesFromConf(classNamesStr);
            final StringBuilder bld = new StringBuilder();
            String prefix = "";
            for (final String className : classNames) {
                try {
                    this.tracerPool.loadReceiverType(className, this.conf, this.classLoader);
                    bld.append(prefix).append(className);
                    prefix = ", ";
                }
                catch (Throwable e) {
                    Tracer.LOG.error("Failed to create SpanReceiver of type " + className, e);
                }
            }
            String resultString = bld.toString();
            if (resultString.isEmpty()) {
                resultString = "no span receivers";
            }
            Tracer.LOG.debug("span.receiver.classes = " + classNamesStr + "; loaded " + resultString);
        }
        
        private List<String> getClassNamesFromConf(final String classNamesStr) {
            final String[] classNames = classNamesStr.split(";");
            final LinkedList<String> cleanedClassNames = new LinkedList<String>();
            for (final String className : classNames) {
                final String cleanedClassName = className.trim();
                if (!cleanedClassName.isEmpty()) {
                    cleanedClassNames.add(cleanedClassName);
                }
            }
            return cleanedClassNames;
        }
        
        public Tracer build() {
            if (this.name == null) {
                throw new RuntimeException("You must specify a name for this Tracer.");
            }
            final LinkedList<Sampler> samplers = new LinkedList<Sampler>();
            this.loadSamplers(samplers);
            final String tracerId = new TracerId(this.conf, this.name).get();
            final Tracer tracer = new Tracer(tracerId, this.tracerPool, samplers.toArray(new Sampler[samplers.size()]));
            this.tracerPool.addTracer(tracer);
            this.loadSpanReceivers();
            if (Tracer.LOG.isTraceEnabled()) {
                Tracer.LOG.trace("Created " + tracer + " for " + this.name);
            }
            return tracer;
        }
    }
    
    private static class ThreadContext
    {
        private long depth;
        
        ThreadContext() {
            this.depth = 0L;
        }
        
        boolean isTopLevel() {
            return this.depth == 0L;
        }
        
        void pushScope() {
            ++this.depth;
        }
        
        TraceScope pushNewScope(final Tracer tracer, final Span span, final TraceScope parentScope) {
            final TraceScope scope = new TraceScope(tracer, span, parentScope);
            Tracer.threadLocalScope.set(scope);
            ++this.depth;
            return scope;
        }
        
        void popScope() {
            if (this.depth <= 0L) {
                Tracer.throwClientError("There were more trace scopes closed than were opened.");
            }
            --this.depth;
        }
    }
    
    private static class ThreadLocalContext extends ThreadLocal<ThreadContext>
    {
        @Override
        protected ThreadContext initialValue() {
            return new ThreadContext();
        }
    }
}
