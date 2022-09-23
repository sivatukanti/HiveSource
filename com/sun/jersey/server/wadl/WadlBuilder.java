// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl;

import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.wadl.generators.WadlGeneratorJAXBGrammarGenerator;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.Parameterized;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.jersey.api.model.AbstractMethod;
import java.util.Collections;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.model.Parameter;
import java.util.List;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Response;
import java.util.Collection;
import com.sun.research.ws.wadl.Method;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.research.ws.wadl.Param;
import java.util.Map;
import com.sun.jersey.server.impl.BuildId;
import javax.xml.namespace.QName;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Resource;
import java.util.Iterator;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Application;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.ext.Providers;
import com.sun.jersey.api.model.AbstractResource;
import java.util.Set;
import javax.ws.rs.core.UriInfo;

public class WadlBuilder
{
    private WadlGenerator _wadlGenerator;
    
    public WadlBuilder() {
        this(createDefaultGenerator());
    }
    
    public WadlBuilder(final WadlGenerator wadlGenerator) {
        this._wadlGenerator = wadlGenerator;
    }
    
    public ApplicationDescription generate(final UriInfo info, final Set<AbstractResource> resources) {
        return this.generate(null, null, info, resources);
    }
    
    public ApplicationDescription generate(final Providers providers, final FeaturesAndProperties fap, final UriInfo info, final Set<AbstractResource> resources) {
        this._wadlGenerator.setEnvironment(new WadlGenerator.Environment().setProviders(providers).setFeaturesAndProperties(fap));
        final Application wadlApplication = this._wadlGenerator.createApplication(info);
        final Resources wadlResources = this._wadlGenerator.createResources();
        for (final AbstractResource r : resources) {
            final Resource wadlResource = this.generateResource(r, null);
            wadlResources.getResource().add(wadlResource);
        }
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        final WadlGenerator.ExternalGrammarDefinition external = this._wadlGenerator.createExternalGrammar();
        final ApplicationDescription description = new ApplicationDescription(wadlApplication, external);
        this._wadlGenerator.attachTypes(description);
        return description;
    }
    
    public Application generate(final Providers providers, final FeaturesAndProperties fap, final UriInfo info, final ApplicationDescription description, final AbstractResource resource) {
        this._wadlGenerator.setEnvironment(new WadlGenerator.Environment().setProviders(providers).setFeaturesAndProperties(fap));
        final Application wadlApplication = this._wadlGenerator.createApplication(info);
        final Resources wadlResources = this._wadlGenerator.createResources();
        final Resource wadlResource = this.generateResource(resource, null);
        wadlResources.getResource().add(wadlResource);
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        this._wadlGenerator.attachTypes(description);
        return wadlApplication;
    }
    
    public Application generate(final Providers providers, final FeaturesAndProperties fap, final UriInfo info, final ApplicationDescription description, final AbstractResource resource, final String path) {
        this._wadlGenerator.setEnvironment(new WadlGenerator.Environment().setProviders(providers).setFeaturesAndProperties(fap));
        final Application wadlApplication = this._wadlGenerator.createApplication(info);
        final Resources wadlResources = this._wadlGenerator.createResources();
        final Resource wadlResource = this.generateSubResource(resource, path);
        wadlResources.getResource().add(wadlResource);
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        this._wadlGenerator.attachTypes(description);
        return wadlApplication;
    }
    
    private void addVersion(final Application wadlApplication) {
        final Doc d = new Doc();
        d.getOtherAttributes().put(new QName("http://jersey.java.net/", "generatedBy", "jersey"), BuildId.getBuildId());
        wadlApplication.getDoc().add(0, d);
    }
    
    private Method generateMethod(final AbstractResource r, final Map<String, Param> wadlResourceParams, final AbstractResourceMethod m) {
        final Method wadlMethod = this._wadlGenerator.createMethod(r, m);
        final Request wadlRequest = this.generateRequest(r, m, wadlResourceParams);
        if (wadlRequest != null) {
            wadlMethod.setRequest(wadlRequest);
        }
        final List<Response> responses = this.generateResponses(r, m);
        if (responses != null) {
            wadlMethod.getResponse().addAll(responses);
        }
        return wadlMethod;
    }
    
    private Request generateRequest(final AbstractResource r, final AbstractResourceMethod m, final Map<String, Param> wadlResourceParams) {
        if (m.getParameters().isEmpty()) {
            return null;
        }
        final Request wadlRequest = this._wadlGenerator.createRequest(r, m);
        for (final Parameter p : m.getParameters()) {
            if (p.getSource() == Parameter.Source.ENTITY) {
                for (final MediaType mediaType : m.getSupportedInputTypes()) {
                    this.setRepresentationForMediaType(r, m, mediaType, wadlRequest);
                }
            }
            else if (p.getAnnotation().annotationType() == FormParam.class) {
                List<MediaType> supportedInputTypes = m.getSupportedInputTypes();
                if (supportedInputTypes.isEmpty() || (supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType())) {
                    supportedInputTypes = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
                }
                for (final MediaType mediaType2 : supportedInputTypes) {
                    final Representation wadlRepresentation = this.setRepresentationForMediaType(r, m, mediaType2, wadlRequest);
                    if (this.getParamByName(wadlRepresentation.getParam(), p.getSourceName()) == null) {
                        final Param wadlParam = this.generateParam(r, m, p);
                        if (wadlParam == null) {
                            continue;
                        }
                        wadlRepresentation.getParam().add(wadlParam);
                    }
                }
            }
            else if (p.getAnnotation().annotationType().getName().equals("com.sun.jersey.multipart.FormDataParam")) {
                List<MediaType> supportedInputTypes = m.getSupportedInputTypes();
                if (supportedInputTypes.isEmpty() || (supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType())) {
                    supportedInputTypes = Collections.singletonList(MediaType.MULTIPART_FORM_DATA_TYPE);
                }
                for (final MediaType mediaType2 : supportedInputTypes) {
                    final Representation wadlRepresentation = this.setRepresentationForMediaType(r, m, mediaType2, wadlRequest);
                    if (this.getParamByName(wadlRepresentation.getParam(), p.getSourceName()) == null) {
                        final Param wadlParam = this.generateParam(r, m, p);
                        if (wadlParam == null) {
                            continue;
                        }
                        wadlRepresentation.getParam().add(wadlParam);
                    }
                }
            }
            else {
                final Param wadlParam2 = this.generateParam(r, m, p);
                if (wadlParam2 == null) {
                    continue;
                }
                if (wadlParam2.getStyle() == ParamStyle.TEMPLATE || wadlParam2.getStyle() == ParamStyle.MATRIX) {
                    wadlResourceParams.put(wadlParam2.getName(), wadlParam2);
                }
                else {
                    wadlRequest.getParam().add(wadlParam2);
                }
            }
        }
        if (wadlRequest.getRepresentation().size() + wadlRequest.getParam().size() == 0) {
            return null;
        }
        return wadlRequest;
    }
    
    private Param getParamByName(final List<Param> params, final String name) {
        for (final Param param : params) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }
    
    private Representation setRepresentationForMediaType(final AbstractResource r, final AbstractResourceMethod m, final MediaType mediaType, final Request wadlRequest) {
        Representation wadlRepresentation = this.getRepresentationByMediaType(wadlRequest.getRepresentation(), mediaType);
        if (wadlRepresentation == null) {
            wadlRepresentation = this._wadlGenerator.createRequestRepresentation(r, m, mediaType);
            wadlRequest.getRepresentation().add(wadlRepresentation);
        }
        return wadlRepresentation;
    }
    
    private Representation getRepresentationByMediaType(final List<Representation> representations, final MediaType mediaType) {
        for (final Representation representation : representations) {
            if (mediaType.toString().equals(representation.getMediaType())) {
                return representation;
            }
        }
        return null;
    }
    
    private Param generateParam(final AbstractResource r, final AbstractMethod m, final Parameter p) {
        if (p.getSource() == Parameter.Source.ENTITY || p.getSource() == Parameter.Source.CONTEXT) {
            return null;
        }
        final Param wadlParam = this._wadlGenerator.createParam(r, m, p);
        return wadlParam;
    }
    
    private Resource generateResource(final AbstractResource r, final String path) {
        return this.generateResource(r, path, Collections.emptySet());
    }
    
    private Resource generateResource(final AbstractResource r, final String path, Set<Class<?>> visitedClasses) {
        final Resource wadlResource = this._wadlGenerator.createResource(r, path);
        if (visitedClasses.contains(r.getResourceClass())) {
            return wadlResource;
        }
        visitedClasses = new HashSet<Class<?>>(visitedClasses);
        visitedClasses.add(r.getResourceClass());
        final Map<String, Param> wadlResourceParams = new HashMap<String, Param>();
        final List<Parameterized> fieldsOrSetters = new LinkedList<Parameterized>();
        if (r.getFields() != null) {
            fieldsOrSetters.addAll(r.getFields());
        }
        if (r.getSetterMethods() != null) {
            fieldsOrSetters.addAll(r.getSetterMethods());
        }
        for (final Parameterized f : fieldsOrSetters) {
            for (final Parameter fp : f.getParameters()) {
                final Param wadlParam = this.generateParam(r, null, fp);
                if (wadlParam != null) {
                    wadlResource.getParam().add(wadlParam);
                }
            }
        }
        for (final AbstractResourceMethod m : r.getResourceMethods()) {
            final Method wadlMethod = this.generateMethod(r, wadlResourceParams, m);
            wadlResource.getMethodOrResource().add(wadlMethod);
        }
        for (final Param wadlParam2 : wadlResourceParams.values()) {
            wadlResource.getParam().add(wadlParam2);
        }
        final Map<String, Resource> wadlSubResources = new HashMap<String, Resource>();
        final Map<String, Map<String, Param>> wadlSubResourcesParams = new HashMap<String, Map<String, Param>>();
        for (final AbstractSubResourceMethod i : r.getSubResourceMethods()) {
            final String template = i.getPath().getValue();
            Resource wadlSubResource = wadlSubResources.get(template);
            Map<String, Param> wadlSubResourceParams = wadlSubResourcesParams.get(template);
            if (wadlSubResource == null) {
                wadlSubResource = new Resource();
                wadlSubResource.setPath(template);
                wadlSubResources.put(template, wadlSubResource);
                wadlSubResourceParams = new HashMap<String, Param>();
                wadlSubResourcesParams.put(template, wadlSubResourceParams);
                wadlResource.getMethodOrResource().add(wadlSubResource);
            }
            final Method wadlMethod2 = this.generateMethod(r, wadlSubResourceParams, i);
            wadlSubResource.getMethodOrResource().add(wadlMethod2);
        }
        for (final Map.Entry<String, Resource> e : wadlSubResources.entrySet()) {
            final String template = e.getKey();
            final Resource wadlSubResource = e.getValue();
            final Map<String, Param> wadlSubResourceParams = wadlSubResourcesParams.get(template);
            for (final Param wadlParam3 : wadlSubResourceParams.values()) {
                wadlSubResource.getParam().add(wadlParam3);
            }
        }
        for (final AbstractSubResourceLocator l : r.getSubResourceLocators()) {
            final AbstractResource subResource = IntrospectionModeller.createResource(l.getMethod().getReturnType());
            final Resource wadlSubResource = this.generateResource(subResource, l.getPath().getValue(), visitedClasses);
            wadlResource.getMethodOrResource().add(wadlSubResource);
            for (final Parameter p : l.getParameters()) {
                final Param wadlParam3 = this.generateParam(r, l, p);
                if (wadlParam3 != null && wadlParam3.getStyle() == ParamStyle.TEMPLATE) {
                    wadlSubResource.getParam().add(wadlParam3);
                }
            }
        }
        return wadlResource;
    }
    
    private Resource generateSubResource(final AbstractResource r, final String path) {
        final Resource wadlResource = new Resource();
        if (r.isRootResource()) {
            final StringBuilder b = new StringBuilder(r.getPath().getValue());
            if (!r.getPath().getValue().endsWith("/") && !path.startsWith("/")) {
                b.append("/");
            }
            b.append(path);
            wadlResource.setPath(b.toString());
        }
        final Map<String, Param> wadlSubResourceParams = new HashMap<String, Param>();
        for (final AbstractSubResourceMethod m : r.getSubResourceMethods()) {
            final String template = m.getPath().getValue();
            if (!template.equals(path) && !template.equals('/' + path)) {
                continue;
            }
            final Method wadlMethod = this.generateMethod(r, wadlSubResourceParams, m);
            wadlResource.getMethodOrResource().add(wadlMethod);
        }
        for (final Param wadlParam : wadlSubResourceParams.values()) {
            wadlResource.getParam().add(wadlParam);
        }
        return wadlResource;
    }
    
    private List<Response> generateResponses(final AbstractResource r, final AbstractResourceMethod m) {
        if (m.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return this._wadlGenerator.createResponses(r, m);
    }
    
    private static WadlGeneratorJAXBGrammarGenerator createDefaultGenerator() throws RuntimeException {
        final WadlGeneratorJAXBGrammarGenerator wadlGeneratorJAXBGrammarGenerator = new WadlGeneratorJAXBGrammarGenerator();
        try {
            wadlGeneratorJAXBGrammarGenerator.init();
        }
        catch (Exception ex) {
            throw new RuntimeException(ImplMessages.ERROR_CREATING_DEFAULT_WADL_GENERATOR(), ex);
        }
        return wadlGeneratorJAXBGrammarGenerator;
    }
}
