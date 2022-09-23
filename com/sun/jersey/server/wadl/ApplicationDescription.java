// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl;

import java.util.Set;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Application;

public class ApplicationDescription
{
    private Application _application;
    private WadlGenerator.ExternalGrammarDefinition _externalGrammarDefiniton;
    
    ApplicationDescription(final Application application, final WadlGenerator.ExternalGrammarDefinition externalGrammarDefiniton) {
        this._application = application;
        this._externalGrammarDefiniton = externalGrammarDefiniton;
    }
    
    public Application getApplication() {
        return this._application;
    }
    
    public <T> T resolve(final Class type, final MediaType mt, final Class<T> resolvedType) {
        return this._externalGrammarDefiniton.resolve(type, mt, resolvedType);
    }
    
    public ExternalGrammar getExternalGrammar(final String path) {
        return this._externalGrammarDefiniton.map.get(path);
    }
    
    public Set<String> getExternalMetadataKeys() {
        return this._externalGrammarDefiniton.map.keySet();
    }
    
    public static class ExternalGrammar
    {
        private MediaType _type;
        private byte[] _content;
        private boolean _includedInGrammar;
        
        public ExternalGrammar(final MediaType type, final byte[] content, final boolean includedInGrammar) {
            this._type = type;
            this._content = content;
            this._includedInGrammar = includedInGrammar;
        }
        
        public MediaType getType() {
            return this._type;
        }
        
        public byte[] getContent() {
            return this._content.clone();
        }
        
        public boolean isIncludedInGrammar() {
            return this._includedInGrammar;
        }
    }
}
