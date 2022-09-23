// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators;

import com.sun.jersey.api.JResponse;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import com.sun.jersey.server.wadl.ApplicationDescription;
import java.util.HashMap;
import com.sun.research.ws.wadl.Response;
import com.sun.research.ws.wadl.Resources;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlSeeAlso;
import com.sun.research.ws.wadl.Resource;
import java.util.Iterator;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Param;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Method;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.ext.Providers;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import com.sun.jersey.server.wadl.WadlGenerator;

public abstract class AbstractWadlGeneratorGrammarGenerator<T> implements WadlGenerator
{
    private static final Logger LOGGER;
    public static final Set<Class> SPECIAL_GENERIC_TYPES;
    private WadlGenerator _delegate;
    protected Set<Class> _seeAlso;
    protected List<Pair> _hasTypeWantsName;
    protected URI _root;
    protected URI _wadl;
    protected Providers _providers;
    protected FeaturesAndProperties _fap;
    protected Class<T> _resolvedType;
    
    protected static HasType parameter(final Parameter param, final MediaType mt) {
        return new HasType() {
            @Override
            public Class getPrimaryClass() {
                return param.getParameterClass();
            }
            
            @Override
            public Type getType() {
                return param.getParameterType();
            }
            
            @Override
            public MediaType getMediaType() {
                return mt;
            }
        };
    }
    
    protected AbstractWadlGeneratorGrammarGenerator(final WadlGenerator delegate, final Class<T> resolvedType) {
        this._delegate = delegate;
        this._resolvedType = resolvedType;
    }
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        this._delegate = delegate;
    }
    
    @Override
    public String getRequiredJaxbContextPath() {
        return this._delegate.getRequiredJaxbContextPath();
    }
    
    @Override
    public void init() throws Exception {
        this._delegate.init();
        this._seeAlso = new HashSet<Class>();
        this._hasTypeWantsName = new ArrayList<Pair>();
    }
    
    @Override
    public void setEnvironment(final Environment env) {
        this._delegate.setEnvironment(env);
        this._providers = env.getProviders();
        this._fap = env.getFeaturesAndProperties();
    }
    
    public abstract boolean acceptMediaType(final MediaType p0);
    
    @Override
    public Application createApplication(final UriInfo requestInfo) {
        if (requestInfo != null) {
            this._root = requestInfo.getBaseUri();
            this._wadl = requestInfo.getRequestUri();
        }
        return this._delegate.createApplication(requestInfo);
    }
    
    @Override
    public Method createMethod(final AbstractResource ar, final AbstractResourceMethod arm) {
        return this._delegate.createMethod(ar, arm);
    }
    
    @Override
    public Request createRequest(final AbstractResource ar, final AbstractResourceMethod arm) {
        return this._delegate.createRequest(ar, arm);
    }
    
    @Override
    public Param createParam(final AbstractResource ar, final AbstractMethod am, final Parameter p) {
        final Param param = this._delegate.createParam(ar, am, p);
        if (p.getSource() == Parameter.Source.ENTITY) {
            this._hasTypeWantsName.add(new Pair(parameter(p, MediaType.APPLICATION_XML_TYPE), this.createParmWantsName(param)));
        }
        return param;
    }
    
    @Override
    public Representation createRequestRepresentation(final AbstractResource ar, final AbstractResourceMethod arm, final MediaType mt) {
        final Representation rt = this._delegate.createRequestRepresentation(ar, arm, mt);
        for (final Parameter p : arm.getParameters()) {
            if (p.getSource() == Parameter.Source.ENTITY && this.acceptMediaType(mt)) {
                this._hasTypeWantsName.add(new Pair(parameter(p, mt), this.createRepresentationWantsName(rt)));
            }
        }
        return rt;
    }
    
    @Override
    public Resource createResource(final AbstractResource ar, final String path) {
        final Class cls = ar.getResourceClass();
        final XmlSeeAlso seeAlso = cls.getAnnotation(XmlSeeAlso.class);
        if (seeAlso != null) {
            Collections.addAll(this._seeAlso, (Class[])seeAlso.value());
        }
        return this._delegate.createResource(ar, path);
    }
    
    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }
    
    @Override
    public List<Response> createResponses(final AbstractResource ar, final AbstractResourceMethod arm) {
        final List<Response> responses = this._delegate.createResponses(ar, arm);
        if (responses != null) {
            for (final Response response : responses) {
                for (final Representation representation : response.getRepresentation()) {
                    if (representation.getMediaType() != null && this.acceptMediaType(MediaType.valueOf(representation.getMediaType()))) {
                        final HasType hasType = new HasType() {
                            @Override
                            public Class getPrimaryClass() {
                                return arm.getReturnType();
                            }
                            
                            @Override
                            public Type getType() {
                                return arm.getGenericReturnType();
                            }
                            
                            @Override
                            public MediaType getMediaType() {
                                return MediaType.valueOf(representation.getMediaType());
                            }
                        };
                        this._hasTypeWantsName.add(new Pair(hasType, this.createRepresentationWantsName(representation)));
                    }
                }
            }
        }
        return responses;
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        final ExternalGrammarDefinition previous = this._delegate.createExternalGrammar();
        final Map<String, ApplicationDescription.ExternalGrammar> extraFiles = new HashMap<String, ApplicationDescription.ExternalGrammar>();
        final Resolver resolver = this.buildModelAndSchemas(extraFiles);
        previous.map.putAll(extraFiles);
        if (resolver != null) {
            previous.addResolver(resolver);
        }
        return previous;
    }
    
    protected abstract Resolver buildModelAndSchemas(final Map<String, ApplicationDescription.ExternalGrammar> p0);
    
    @Override
    public void attachTypes(final ApplicationDescription introspector) {
        this._delegate.attachTypes(introspector);
        if (introspector != null) {
            for (int i = this._hasTypeWantsName.size(), j = 0; j < i; ++j) {
                final Pair pair = this._hasTypeWantsName.get(j);
                final WantsName nextToProcess = pair.wantsName;
                if (!nextToProcess.isElement()) {
                    AbstractWadlGeneratorGrammarGenerator.LOGGER.info("Type references are not supported as yet");
                }
                final HasType nextType = pair.hasType;
                Class<?> parameterClass = (Class<?>)nextType.getPrimaryClass();
                if (AbstractWadlGeneratorGrammarGenerator.SPECIAL_GENERIC_TYPES.contains(parameterClass)) {
                    final Type type = nextType.getType();
                    if (!ParameterizedType.class.isAssignableFrom(type.getClass()) || !Class.class.isAssignableFrom(((ParameterizedType)type).getActualTypeArguments()[0].getClass())) {
                        AbstractWadlGeneratorGrammarGenerator.LOGGER.info("Couldn't find grammar element due to nested parameterized type " + type);
                        return;
                    }
                    parameterClass = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
                }
                final T name = introspector.resolve(parameterClass, nextType.getMediaType(), this._resolvedType);
                if (name != null) {
                    nextToProcess.setName(name);
                }
                else {
                    AbstractWadlGeneratorGrammarGenerator.LOGGER.info("Couldn't find grammar element for class " + parameterClass.getName());
                }
            }
        }
    }
    
    protected abstract WantsName<T> createParmWantsName(final Param p0);
    
    protected abstract WantsName<T> createRepresentationWantsName(final Representation p0);
    
    static {
        LOGGER = Logger.getLogger(AbstractWadlGeneratorGrammarGenerator.class.getName());
        SPECIAL_GENERIC_TYPES = new HashSet<Class>() {
            {
                ((HashSet<Class<JResponse>>)this).add(JResponse.class);
                ((HashSet<Class<List>>)this).add(List.class);
            }
        };
    }
    
    protected class Pair
    {
        public HasType hasType;
        public WantsName wantsName;
        
        public Pair(final HasType hasType, final WantsName wantsName) {
            this.hasType = hasType;
            this.wantsName = wantsName;
        }
    }
    
    protected interface HasType
    {
        Class getPrimaryClass();
        
        Type getType();
        
        MediaType getMediaType();
    }
    
    protected interface WantsName<T>
    {
        boolean isElement();
        
        void setName(final T p0);
    }
}
