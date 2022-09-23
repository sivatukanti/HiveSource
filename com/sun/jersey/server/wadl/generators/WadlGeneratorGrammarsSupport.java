// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators;

import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Param;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Method;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.research.ws.wadl.Include;
import com.sun.research.ws.wadl.Doc;
import java.util.Collection;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import com.sun.research.ws.wadl.Grammars;
import java.io.InputStream;
import java.io.File;
import java.util.logging.Logger;
import com.sun.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorGrammarsSupport implements WadlGenerator
{
    private static final Logger LOG;
    private WadlGenerator _delegate;
    private File _grammarsFile;
    private InputStream _grammarsStream;
    private Grammars _grammars;
    private Boolean overrideGrammars;
    
    public WadlGeneratorGrammarsSupport() {
        this.overrideGrammars = false;
    }
    
    public WadlGeneratorGrammarsSupport(final WadlGenerator delegate, final Grammars grammars) {
        this.overrideGrammars = false;
        this._delegate = delegate;
        this._grammars = grammars;
    }
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        this._delegate = delegate;
    }
    
    @Override
    public void setEnvironment(final Environment env) {
        this._delegate.setEnvironment(env);
    }
    
    public void setOverrideGrammars(final Boolean overrideGrammars) {
        this.overrideGrammars = overrideGrammars;
    }
    
    @Override
    public String getRequiredJaxbContextPath() {
        return this._delegate.getRequiredJaxbContextPath();
    }
    
    public void setGrammarsFile(final File grammarsFile) {
        if (this._grammarsStream != null) {
            throw new IllegalStateException("The grammarsStream property is already set, therefore you cannot set the grammarsFile property. Only one of both can be set at a time.");
        }
        this._grammarsFile = grammarsFile;
    }
    
    public void setGrammarsStream(final InputStream grammarsStream) {
        if (this._grammarsFile != null) {
            throw new IllegalStateException("The grammarsFile property is already set, therefore you cannot set the grammarsStream property. Only one of both can be set at a time.");
        }
        this._grammarsStream = grammarsStream;
    }
    
    @Override
    public void init() throws Exception {
        if (this._grammarsFile == null && this._grammarsStream == null) {
            throw new IllegalStateException("Neither the grammarsFile nor the grammarsStream is set, one of both is required.");
        }
        this._delegate.init();
        final JAXBContext c = JAXBContext.newInstance(Grammars.class);
        final Unmarshaller m = c.createUnmarshaller();
        final Object obj = (this._grammarsFile != null) ? m.unmarshal(this._grammarsFile) : m.unmarshal(this._grammarsStream);
        this._grammars = Grammars.class.cast(obj);
    }
    
    @Override
    public Application createApplication(final UriInfo requestInfo) {
        final Application result = this._delegate.createApplication(requestInfo);
        if (result.getGrammars() != null && !this.overrideGrammars) {
            WadlGeneratorGrammarsSupport.LOG.info("The wadl application created by the delegate (" + this._delegate + ") already contains a grammars element," + " we're adding elements of the provided grammars file.");
            if (!this._grammars.getAny().isEmpty()) {
                result.getGrammars().getAny().addAll(this._grammars.getAny());
            }
            if (!this._grammars.getDoc().isEmpty()) {
                result.getGrammars().getDoc().addAll(this._grammars.getDoc());
            }
            if (!this._grammars.getInclude().isEmpty()) {
                result.getGrammars().getInclude().addAll(this._grammars.getInclude());
            }
        }
        else {
            result.setGrammars(this._grammars);
        }
        return result;
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
        return this._delegate.createParam(ar, am, p);
    }
    
    @Override
    public Representation createRequestRepresentation(final AbstractResource ar, final AbstractResourceMethod arm, final MediaType mt) {
        return this._delegate.createRequestRepresentation(ar, arm, mt);
    }
    
    @Override
    public Resource createResource(final AbstractResource ar, final String path) {
        return this._delegate.createResource(ar, path);
    }
    
    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }
    
    @Override
    public List<Response> createResponses(final AbstractResource ar, final AbstractResourceMethod arm) {
        return this._delegate.createResponses(ar, arm);
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        if (this.overrideGrammars) {
            return new ExternalGrammarDefinition();
        }
        return this._delegate.createExternalGrammar();
    }
    
    @Override
    public void attachTypes(final ApplicationDescription egd) {
        this._delegate.attachTypes(egd);
    }
    
    static {
        LOG = Logger.getLogger(WadlGeneratorGrammarsSupport.class.getName());
    }
}
