// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl;

import java.util.Arrays;
import java.util.Collections;
import javax.ws.rs.Produces;
import java.util.Iterator;
import java.util.ArrayList;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import com.sun.research.ws.wadl.Resource;
import javax.xml.namespace.QName;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Param;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Method;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractResource;
import javax.ws.rs.core.UriInfo;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Application;

public class WadlGeneratorImpl implements WadlGenerator
{
    @Override
    public String getRequiredJaxbContextPath() {
        final String name = Application.class.getName();
        return name.substring(0, name.lastIndexOf(46));
    }
    
    @Override
    public void init() throws Exception {
    }
    
    @Override
    public void setEnvironment(final Environment env) {
    }
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        throw new UnsupportedOperationException("No delegate supported.");
    }
    
    @Override
    public Resources createResources() {
        return new Resources();
    }
    
    @Override
    public Application createApplication(final UriInfo requestInfo) {
        return new Application();
    }
    
    @Override
    public Method createMethod(final AbstractResource r, final AbstractResourceMethod m) {
        final Method wadlMethod = new Method();
        wadlMethod.setName(m.getHttpMethod());
        wadlMethod.setId(m.getMethod().getName());
        return wadlMethod;
    }
    
    @Override
    public Representation createRequestRepresentation(final AbstractResource r, final AbstractResourceMethod m, final MediaType mediaType) {
        final Representation wadlRepresentation = new Representation();
        wadlRepresentation.setMediaType(mediaType.toString());
        return wadlRepresentation;
    }
    
    @Override
    public Request createRequest(final AbstractResource r, final AbstractResourceMethod m) {
        return new Request();
    }
    
    @Override
    public Param createParam(final AbstractResource r, final AbstractMethod m, final Parameter p) {
        if (p.getSource() == Parameter.Source.UNKNOWN) {
            return null;
        }
        final Param wadlParam = new Param();
        wadlParam.setName(p.getSourceName());
        switch (p.getSource()) {
            case FORM: {
                wadlParam.setStyle(ParamStyle.QUERY);
                break;
            }
            case QUERY: {
                wadlParam.setStyle(ParamStyle.QUERY);
                break;
            }
            case MATRIX: {
                wadlParam.setStyle(ParamStyle.MATRIX);
                break;
            }
            case PATH: {
                wadlParam.setStyle(ParamStyle.TEMPLATE);
                break;
            }
            case HEADER: {
                wadlParam.setStyle(ParamStyle.HEADER);
                break;
            }
            case COOKIE: {
                wadlParam.setStyle(ParamStyle.HEADER);
                wadlParam.setName("Cookie");
                wadlParam.setPath(p.getSourceName());
                break;
            }
        }
        if (p.hasDefaultValue()) {
            wadlParam.setDefault(p.getDefaultValue());
        }
        Class<?> pClass = p.getParameterClass();
        if (pClass.isArray()) {
            wadlParam.setRepeating(true);
            pClass = pClass.getComponentType();
        }
        if (pClass.equals(Integer.TYPE) || pClass.equals(Integer.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "int", "xs"));
        }
        else if (pClass.equals(Boolean.TYPE) || pClass.equals(Boolean.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs"));
        }
        else if (pClass.equals(Long.TYPE) || pClass.equals(Long.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "long", "xs"));
        }
        else if (pClass.equals(Short.TYPE) || pClass.equals(Short.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "short", "xs"));
        }
        else if (pClass.equals(Byte.TYPE) || pClass.equals(Byte.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs"));
        }
        else if (pClass.equals(Float.TYPE) || pClass.equals(Float.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "float", "xs"));
        }
        else if (pClass.equals(Double.TYPE) || pClass.equals(Double.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "double", "xs"));
        }
        else {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));
        }
        return wadlParam;
    }
    
    @Override
    public Resource createResource(final AbstractResource r, final String path) {
        final Resource wadlResource = new Resource();
        if (path != null) {
            wadlResource.setPath(path);
        }
        else if (r.isRootResource()) {
            wadlResource.setPath(r.getPath().getValue());
        }
        return wadlResource;
    }
    
    @Override
    public List<Response> createResponses(final AbstractResource r, final AbstractResourceMethod m) {
        final Response response = new Response();
        for (final MediaType mediaType : m.getSupportedOutputTypes()) {
            if (!MediaType.WILDCARD_TYPE.equals(mediaType) || !this.hasEmptyProducibleMediaTypeSet(m)) {
                final Representation wadlRepresentation = this.createResponseRepresentation(r, m, mediaType);
                response.getRepresentation().add(wadlRepresentation);
            }
        }
        final List<Response> responses = new ArrayList<Response>();
        responses.add(response);
        return responses;
    }
    
    private boolean hasEmptyProducibleMediaTypeSet(final AbstractResourceMethod method) {
        final Produces produces = method.getMethod().getAnnotation(Produces.class);
        return produces != null && this.getProducibleMediaTypes(method).isEmpty();
    }
    
    private List<String> getProducibleMediaTypes(final AbstractResourceMethod method) {
        List<String> mediaTypes = Collections.emptyList();
        final Produces produces = method.getMethod().getAnnotation(Produces.class);
        if (produces != null && produces.value() != null) {
            mediaTypes = Arrays.asList(produces.value());
        }
        return mediaTypes;
    }
    
    public Representation createResponseRepresentation(final AbstractResource r, final AbstractResourceMethod m, final MediaType mediaType) {
        final Representation wadlRepresentation = new Representation();
        wadlRepresentation.setMediaType(mediaType.toString());
        return wadlRepresentation;
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        return new ExternalGrammarDefinition();
    }
    
    @Override
    public void attachTypes(final ApplicationDescription egd) {
    }
}
