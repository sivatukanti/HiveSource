// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.http.HttpSession;
import com.google.inject.Provider;
import com.google.inject.internal.util.$Maps;
import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Key;
import java.util.concurrent.Callable;
import java.util.Map;
import com.google.inject.Scope;

public class ServletScopes
{
    public static final Scope REQUEST;
    public static final Scope SESSION;
    private static final ThreadLocal<Map<String, Object>> requestScopeContext;
    
    private ServletScopes() {
    }
    
    public static <T> Callable<T> continueRequest(final Callable<T> callable, final Map<Key<?>, Object> seedMap) {
        $Preconditions.checkArgument(null != seedMap, (Object)"Seed map cannot be null, try passing in Collections.emptyMap() instead.");
        final ContinuingHttpServletRequest continuingRequest = new ContinuingHttpServletRequest(GuiceFilter.getRequest());
        for (final Map.Entry<Key<?>, Object> entry : seedMap.entrySet()) {
            final Object value = validateAndCanonicalizeValue(entry.getKey(), entry.getValue());
            continuingRequest.setAttribute(entry.getKey().toString(), value);
        }
        return new Callable<T>() {
            private HttpServletRequest request = continuingRequest;
            
            public T call() throws Exception {
                final GuiceFilter.Context context = GuiceFilter.localContext.get();
                $Preconditions.checkState(null == context, (Object)"Cannot continue request in the same thread as a HTTP request!");
                GuiceFilter.localContext.set(new GuiceFilter.Context(this.request, null));
                try {
                    return callable.call();
                }
                finally {
                    if (null == context) {
                        GuiceFilter.localContext.remove();
                    }
                }
            }
        };
    }
    
    public static <T> Callable<T> scopeRequest(final Callable<T> callable, final Map<Key<?>, Object> seedMap) {
        $Preconditions.checkArgument(null != seedMap, (Object)"Seed map cannot be null, try passing in Collections.emptyMap() instead.");
        final Map<String, Object> scopeMap = (Map<String, Object>)$Maps.newHashMap();
        for (final Map.Entry<Key<?>, Object> entry : seedMap.entrySet()) {
            final Object value = validateAndCanonicalizeValue(entry.getKey(), entry.getValue());
            scopeMap.put(entry.getKey().toString(), value);
        }
        return new Callable<T>() {
            public T call() throws Exception {
                $Preconditions.checkState(null == GuiceFilter.localContext.get(), (Object)"An HTTP request is already in progress, cannot scope a new request in this thread.");
                $Preconditions.checkState(null == ServletScopes.requestScopeContext.get(), (Object)"A request scope is already in progress, cannot scope a new request in this thread.");
                ServletScopes.requestScopeContext.set(scopeMap);
                try {
                    return callable.call();
                }
                finally {
                    ServletScopes.requestScopeContext.remove();
                }
            }
        };
    }
    
    private static Object validateAndCanonicalizeValue(final Key<?> key, final Object object) {
        if (object == null || object == NullObject.INSTANCE) {
            return NullObject.INSTANCE;
        }
        if (!key.getTypeLiteral().getRawType().isInstance(object)) {
            throw new IllegalArgumentException("Value[" + object + "] of type[" + object.getClass().getName() + "] is not compatible with key[" + key + "]");
        }
        return object;
    }
    
    static {
        REQUEST = new Scope() {
            public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
                final String name = key.toString();
                return new Provider<T>() {
                    public T get() {
                        if (null == GuiceFilter.localContext.get()) {
                            final Map<String, Object> scopeMap = ServletScopes.requestScopeContext.get();
                            if (null != scopeMap) {
                                T t = (T)scopeMap.get(name);
                                if (NullObject.INSTANCE == t) {
                                    return null;
                                }
                                if (t == null) {
                                    t = creator.get();
                                    scopeMap.put(name, (t != null) ? t : NullObject.INSTANCE);
                                }
                                return t;
                            }
                        }
                        final HttpServletRequest request = GuiceFilter.getRequest();
                        synchronized (request) {
                            final Object obj = request.getAttribute(name);
                            if (NullObject.INSTANCE == obj) {
                                return null;
                            }
                            T t2 = (T)obj;
                            if (t2 == null) {
                                t2 = creator.get();
                                request.setAttribute(name, (t2 != null) ? t2 : NullObject.INSTANCE);
                            }
                            return t2;
                        }
                    }
                    
                    @Override
                    public String toString() {
                        return String.format("%s[%s]", creator, ServletScopes.REQUEST);
                    }
                };
            }
            
            @Override
            public String toString() {
                return "ServletScopes.REQUEST";
            }
        };
        SESSION = new Scope() {
            public <T> Provider<T> scope(final Key<T> key, final Provider<T> creator) {
                final String name = key.toString();
                return new Provider<T>() {
                    public T get() {
                        final HttpSession session = GuiceFilter.getRequest().getSession();
                        synchronized (session) {
                            final Object obj = session.getAttribute(name);
                            if (NullObject.INSTANCE == obj) {
                                return null;
                            }
                            T t = (T)obj;
                            if (t == null) {
                                t = creator.get();
                                session.setAttribute(name, (t != null) ? t : NullObject.INSTANCE);
                            }
                            return t;
                        }
                    }
                    
                    @Override
                    public String toString() {
                        return String.format("%s[%s]", creator, ServletScopes.SESSION);
                    }
                };
            }
            
            @Override
            public String toString() {
                return "ServletScopes.SESSION";
            }
        };
        requestScopeContext = new ThreadLocal<Map<String, Object>>();
    }
    
    enum NullObject
    {
        INSTANCE;
    }
}
