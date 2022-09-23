// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Set;
import java.util.Comparator;
import java.util.TreeSet;
import java.lang.reflect.ParameterizedType;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.core.util.KeyComparatorHashMap;
import java.util.ArrayList;
import com.sun.jersey.core.header.MediaTypes;
import javax.ws.rs.Produces;
import java.util.List;
import com.sun.jersey.core.spi.component.ProviderServices;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.Map;

public class ContextResolverFactory
{
    private final Map<Type, Map<MediaType, ContextResolver>> resolver;
    private final Map<Type, ConcurrentHashMap<MediaType, ContextResolver>> cache;
    private static final NullContextResolverAdapter NULL_CONTEXT_RESOLVER;
    
    public ContextResolverFactory() {
        this.resolver = new HashMap<Type, Map<MediaType, ContextResolver>>(4);
        this.cache = new HashMap<Type, ConcurrentHashMap<MediaType, ContextResolver>>(4);
    }
    
    public void init(final ProviderServices providersServices, final InjectableProviderFactory ipf) {
        final Map<Type, Map<MediaType, List<ContextResolver>>> rs = new HashMap<Type, Map<MediaType, List<ContextResolver>>>();
        final Set<ContextResolver> providers = (Set<ContextResolver>)providersServices.getProviders(ContextResolver.class);
        for (final ContextResolver provider : providers) {
            final List<MediaType> ms = MediaTypes.createMediaTypes(provider.getClass().getAnnotation(Produces.class));
            final Type type = this.getParameterizedType(provider.getClass());
            Map<MediaType, List<ContextResolver>> mr = rs.get(type);
            if (mr == null) {
                mr = new HashMap<MediaType, List<ContextResolver>>();
                rs.put(type, mr);
            }
            for (final MediaType m : ms) {
                List<ContextResolver> crl = mr.get(m);
                if (crl == null) {
                    crl = new ArrayList<ContextResolver>();
                    mr.put(m, crl);
                }
                crl.add(provider);
            }
        }
        for (final Map.Entry<Type, Map<MediaType, List<ContextResolver>>> e : rs.entrySet()) {
            final Map<MediaType, ContextResolver> mr2 = new KeyComparatorHashMap<MediaType, ContextResolver>(4, MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
            this.resolver.put(e.getKey(), mr2);
            this.cache.put(e.getKey(), new ConcurrentHashMap<MediaType, ContextResolver>(4));
            for (final Map.Entry<MediaType, List<ContextResolver>> f : e.getValue().entrySet()) {
                mr2.put(f.getKey(), this.reduce(f.getValue()));
            }
        }
        ipf.add(new InjectableProvider<Context, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable getInjectable(final ComponentContext ic, final Context ac, final Type c) {
                if (!(c instanceof ParameterizedType)) {
                    return null;
                }
                final ParameterizedType pType = (ParameterizedType)c;
                if (pType.getRawType() != ContextResolver.class) {
                    return null;
                }
                final Type type = pType.getActualTypeArguments()[0];
                final ContextResolver cr = this.getResolver(ic, type);
                if (cr == null) {
                    return new Injectable() {
                        @Override
                        public Object getValue() {
                            return null;
                        }
                    };
                }
                return new Injectable() {
                    @Override
                    public Object getValue() {
                        return cr;
                    }
                };
            }
            
            ContextResolver getResolver(final ComponentContext ic, final Type type) {
                final Map<MediaType, ContextResolver> x = ContextResolverFactory.this.resolver.get(type);
                if (x == null) {
                    return null;
                }
                final List<MediaType> ms = this.getMediaTypes(ic);
                if (ms.size() == 1) {
                    return ContextResolverFactory.this.resolve(type, ms.get(0));
                }
                final Set<MediaType> ml = new TreeSet<MediaType>(MediaTypes.MEDIA_TYPE_COMPARATOR);
                for (final MediaType m : ms) {
                    if (m.isWildcardType()) {
                        ml.add(MediaTypes.GENERAL_MEDIA_TYPE);
                    }
                    else if (m.isWildcardSubtype()) {
                        ml.add(new MediaType(m.getType(), "*"));
                        ml.add(MediaTypes.GENERAL_MEDIA_TYPE);
                    }
                    else {
                        ml.add(new MediaType(m.getType(), m.getSubtype()));
                        ml.add(new MediaType(m.getType(), "*"));
                        ml.add(MediaTypes.GENERAL_MEDIA_TYPE);
                    }
                }
                final List<ContextResolver> crl = new ArrayList<ContextResolver>(ml.size());
                for (final MediaType i : ms) {
                    final ContextResolver cr = x.get(i);
                    if (cr != null) {
                        crl.add(cr);
                    }
                }
                if (crl.isEmpty()) {
                    return null;
                }
                return new ContextResolverAdapter(crl);
            }
            
            List<MediaType> getMediaTypes(final ComponentContext ic) {
                Produces p = null;
                for (final Annotation a : ic.getAnnotations()) {
                    if (a instanceof Produces) {
                        p = (Produces)a;
                        break;
                    }
                }
                return MediaTypes.createMediaTypes(p);
            }
        });
    }
    
    private Type getParameterizedType(final Class c) {
        final ReflectionHelper.DeclaringClassInterfacePair p = ReflectionHelper.getClass(c, ContextResolver.class);
        final Type[] as = ReflectionHelper.getParameterizedTypeArguments(p);
        return (as != null) ? as[0] : Object.class;
    }
    
    private ContextResolver reduce(final List<ContextResolver> r) {
        if (r.size() == 1) {
            return r.iterator().next();
        }
        return new ContextResolverAdapter(r);
    }
    
    public <T> ContextResolver<T> resolve(final Type t, MediaType m) {
        final ConcurrentHashMap<MediaType, ContextResolver> crMapCache = this.cache.get(t);
        if (crMapCache == null) {
            return null;
        }
        if (m == null) {
            m = MediaTypes.GENERAL_MEDIA_TYPE;
        }
        ContextResolver<T> cr = crMapCache.get(m);
        if (cr == null) {
            final Map<MediaType, ContextResolver> crMap = this.resolver.get(t);
            if (m.isWildcardType()) {
                cr = crMap.get(MediaTypes.GENERAL_MEDIA_TYPE);
                if (cr == null) {
                    cr = (ContextResolver<T>)ContextResolverFactory.NULL_CONTEXT_RESOLVER;
                }
            }
            else if (m.isWildcardSubtype()) {
                final ContextResolver<T> subTypeWildCard = crMap.get(m);
                final ContextResolver<T> wildCard = crMap.get(MediaTypes.GENERAL_MEDIA_TYPE);
                cr = (ContextResolver<T>)new ContextResolverAdapter(new ContextResolver[] { subTypeWildCard, wildCard }).reduce();
            }
            else {
                final ContextResolver<T> type = crMap.get(m);
                final ContextResolver<T> subTypeWildCard2 = crMap.get(new MediaType(m.getType(), "*"));
                final ContextResolver<T> wildCard2 = crMap.get(MediaType.WILDCARD_TYPE);
                cr = (ContextResolver<T>)new ContextResolverAdapter(new ContextResolver[] { type, subTypeWildCard2, wildCard2 }).reduce();
            }
            final ContextResolver<T> _cr = crMapCache.putIfAbsent(m, cr);
            if (_cr != null) {
                cr = _cr;
            }
        }
        return (cr != ContextResolverFactory.NULL_CONTEXT_RESOLVER) ? cr : null;
    }
    
    static {
        NULL_CONTEXT_RESOLVER = new NullContextResolverAdapter();
    }
    
    private static final class NullContextResolverAdapter implements ContextResolver
    {
        @Override
        public Object getContext(final Class type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private static final class ContextResolverAdapter implements ContextResolver
    {
        private final ContextResolver[] cra;
        
        ContextResolverAdapter(final ContextResolver... cra) {
            this(removeNull(cra));
        }
        
        ContextResolverAdapter(final List<ContextResolver> crl) {
            this.cra = crl.toArray(new ContextResolver[crl.size()]);
        }
        
        @Override
        public Object getContext(final Class objectType) {
            for (final ContextResolver cr : this.cra) {
                final Object c = cr.getContext(objectType);
                if (c != null) {
                    return c;
                }
            }
            return null;
        }
        
        ContextResolver reduce() {
            if (this.cra.length == 0) {
                return ContextResolverFactory.NULL_CONTEXT_RESOLVER;
            }
            if (this.cra.length == 1) {
                return this.cra[0];
            }
            return this;
        }
        
        private static List<ContextResolver> removeNull(final ContextResolver... cra) {
            final List<ContextResolver> crl = new ArrayList<ContextResolver>(cra.length);
            for (final ContextResolver cr : cra) {
                if (cr != null) {
                    crl.add(cr);
                }
            }
            return crl;
        }
    }
}
