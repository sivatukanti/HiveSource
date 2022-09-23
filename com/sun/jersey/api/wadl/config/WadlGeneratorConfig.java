// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.wadl.config;

import java.util.Properties;
import java.util.ArrayList;
import com.sun.jersey.server.wadl.WadlGenerator;
import java.util.List;

public abstract class WadlGeneratorConfig
{
    public abstract List<WadlGeneratorDescription> configure();
    
    public WadlGenerator createWadlGenerator() {
        WadlGenerator wadlGenerator = null;
        final List<WadlGeneratorDescription> wadlGeneratorDescriptions = this.configure();
        try {
            wadlGenerator = WadlGeneratorLoader.loadWadlGeneratorDescriptions(wadlGeneratorDescriptions);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load wadl generators from wadlGeneratorDescriptions.", e);
        }
        return wadlGenerator;
    }
    
    public static WadlGeneratorConfigDescriptionBuilder generator(final Class<? extends WadlGenerator> generatorClass) {
        return new WadlGeneratorConfigDescriptionBuilder().generator(generatorClass);
    }
    
    public static class WadlGeneratorConfigDescriptionBuilder
    {
        private List<WadlGeneratorDescription> _descriptions;
        private WadlGeneratorDescription _description;
        
        public WadlGeneratorConfigDescriptionBuilder() {
            this._descriptions = new ArrayList<WadlGeneratorDescription>();
        }
        
        public WadlGeneratorConfigDescriptionBuilder generator(final Class<? extends WadlGenerator> generatorClass) {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            (this._description = new WadlGeneratorDescription()).setGeneratorClass(generatorClass);
            return this;
        }
        
        public WadlGeneratorConfigDescriptionBuilder prop(final String propName, final Object propValue) {
            if (this._description.getProperties() == null) {
                this._description.setProperties(new Properties());
            }
            this._description.getProperties().put(propName, propValue);
            return this;
        }
        
        public List<WadlGeneratorDescription> descriptions() {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            return this._descriptions;
        }
        
        public WadlGeneratorConfig build() {
            if (this._description != null) {
                this._descriptions.add(this._description);
            }
            return new WadlGeneratorConfigImpl(this._descriptions);
        }
    }
    
    static class WadlGeneratorConfigImpl extends WadlGeneratorConfig
    {
        public List<WadlGeneratorDescription> _descriptions;
        
        public WadlGeneratorConfigImpl(final List<WadlGeneratorDescription> descriptions) {
            this._descriptions = descriptions;
        }
        
        @Override
        public List<WadlGeneratorDescription> configure() {
            return this._descriptions;
        }
    }
}
