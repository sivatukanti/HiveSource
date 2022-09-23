// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.ext.Providers;
import com.sun.research.ws.wadl.Param;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Method;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.research.ws.wadl.Resource;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;

public interface WadlGenerator
{
    void setWadlGeneratorDelegate(final WadlGenerator p0);
    
    void init() throws Exception;
    
    String getRequiredJaxbContextPath();
    
    void setEnvironment(final Environment p0);
    
    Application createApplication(final UriInfo p0);
    
    Resources createResources();
    
    Resource createResource(final AbstractResource p0, final String p1);
    
    Method createMethod(final AbstractResource p0, final AbstractResourceMethod p1);
    
    Request createRequest(final AbstractResource p0, final AbstractResourceMethod p1);
    
    Representation createRequestRepresentation(final AbstractResource p0, final AbstractResourceMethod p1, final MediaType p2);
    
    List<Response> createResponses(final AbstractResource p0, final AbstractResourceMethod p1);
    
    Param createParam(final AbstractResource p0, final AbstractMethod p1, final Parameter p2);
    
    ExternalGrammarDefinition createExternalGrammar();
    
    void attachTypes(final ApplicationDescription p0);
    
    public static class Environment
    {
        private Providers providers;
        private FeaturesAndProperties fap;
        
        public Environment setProviders(final Providers providers) {
            this.providers = providers;
            return this;
        }
        
        public Providers getProviders() {
            return this.providers;
        }
        
        public Environment setFeaturesAndProperties(final FeaturesAndProperties fap) {
            this.fap = fap;
            return this;
        }
        
        public FeaturesAndProperties getFeaturesAndProperties() {
            return this.fap;
        }
    }
    
    public static class ExternalGrammarDefinition
    {
        public final Map<String, ApplicationDescription.ExternalGrammar> map;
        private List<Resolver> typeResolvers;
        
        public ExternalGrammarDefinition() {
            this.map = new LinkedHashMap<String, ApplicationDescription.ExternalGrammar>();
            this.typeResolvers = new ArrayList<Resolver>();
        }
        
        public void addResolver(final Resolver resolver) {
            assert !this.typeResolvers.contains(resolver) : "Already in list";
            this.typeResolvers.add(0, resolver);
        }
        
        public <T> T resolve(final Class type, final MediaType mt, final Class<T> resolvedType) {
            T name = null;
            for (final Resolver resolver : this.typeResolvers) {
                name = resolver.resolve(type, mt, resolvedType);
                if (name != null) {
                    break;
                }
            }
            return name;
        }
    }
    
    public interface Resolver
    {
         <T> T resolve(final Class p0, final MediaType p1, final Class<T> p2);
    }
}
