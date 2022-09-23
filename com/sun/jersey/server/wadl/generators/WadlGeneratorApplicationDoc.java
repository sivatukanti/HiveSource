// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators;

import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Param;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Method;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.research.ws.wadl.Doc;
import java.util.Collection;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.io.File;
import com.sun.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorApplicationDoc implements WadlGenerator
{
    private WadlGenerator _delegate;
    private File _applicationDocsFile;
    private InputStream _applicationDocsStream;
    private ApplicationDocs _applicationDocs;
    
    public WadlGeneratorApplicationDoc() {
    }
    
    public WadlGeneratorApplicationDoc(final WadlGenerator wadlGenerator, final ApplicationDocs applicationDocs) {
        this._delegate = wadlGenerator;
        this._applicationDocs = applicationDocs;
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
    public void setEnvironment(final Environment env) {
        this._delegate.setEnvironment(env);
    }
    
    public void setApplicationDocsFile(final File applicationDocsFile) {
        if (this._applicationDocsStream != null) {
            throw new IllegalStateException("The applicationDocsStream property is already set, therefore you cannot set the applicationDocsFile property. Only one of both can be set at a time.");
        }
        this._applicationDocsFile = applicationDocsFile;
    }
    
    public void setApplicationDocsStream(final InputStream applicationDocsStream) {
        if (this._applicationDocsFile != null) {
            throw new IllegalStateException("The applicationDocsFile property is already set, therefore you cannot set the applicationDocsStream property. Only one of both can be set at a time.");
        }
        this._applicationDocsStream = applicationDocsStream;
    }
    
    @Override
    public void init() throws Exception {
        if (this._applicationDocsFile == null && this._applicationDocsStream == null) {
            throw new IllegalStateException("Neither the applicationDocsFile nor the applicationDocsStream is set, one of both is required.");
        }
        this._delegate.init();
        String name = ApplicationDocs.class.getName();
        final int i = name.lastIndexOf(46);
        name = ((i != -1) ? name.substring(0, i) : "");
        final JAXBContext c = JAXBContext.newInstance(name, Thread.currentThread().getContextClassLoader());
        final Unmarshaller m = c.createUnmarshaller();
        final Object obj = (this._applicationDocsFile != null) ? m.unmarshal(this._applicationDocsFile) : m.unmarshal(this._applicationDocsStream);
        this._applicationDocs = ApplicationDocs.class.cast(obj);
    }
    
    @Override
    public Application createApplication(final UriInfo requestInfo) {
        final Application result = this._delegate.createApplication(requestInfo);
        if (this._applicationDocs != null && this._applicationDocs.getDocs() != null && !this._applicationDocs.getDocs().isEmpty()) {
            result.getDoc().addAll(this._applicationDocs.getDocs());
        }
        return result;
    }
    
    @Override
    public Method createMethod(final AbstractResource r, final AbstractResourceMethod m) {
        return this._delegate.createMethod(r, m);
    }
    
    @Override
    public Representation createRequestRepresentation(final AbstractResource r, final AbstractResourceMethod m, final MediaType mediaType) {
        return this._delegate.createRequestRepresentation(r, m, mediaType);
    }
    
    @Override
    public Request createRequest(final AbstractResource r, final AbstractResourceMethod m) {
        return this._delegate.createRequest(r, m);
    }
    
    @Override
    public Param createParam(final AbstractResource r, final AbstractMethod m, final Parameter p) {
        return this._delegate.createParam(r, m, p);
    }
    
    @Override
    public Resource createResource(final AbstractResource r, final String path) {
        return this._delegate.createResource(r, path);
    }
    
    @Override
    public List<Response> createResponses(final AbstractResource r, final AbstractResourceMethod m) {
        return this._delegate.createResponses(r, m);
    }
    
    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        return this._delegate.createExternalGrammar();
    }
    
    @Override
    public void attachTypes(final ApplicationDescription egd) {
        this._delegate.attachTypes(egd);
    }
}
