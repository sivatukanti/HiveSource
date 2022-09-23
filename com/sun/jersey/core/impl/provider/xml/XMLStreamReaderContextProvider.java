// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.xml;

import javax.ws.rs.core.Context;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.xml.stream.XMLInputFactory;

public class XMLStreamReaderContextProvider extends ThreadLocalSingletonContextProvider<XMLInputFactory>
{
    private final boolean disableXmlSecurity;
    
    public XMLStreamReaderContextProvider(@Context final FeaturesAndProperties fps) {
        super(XMLInputFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }
    
    @Override
    protected XMLInputFactory getInstance() {
        final XMLInputFactory f = XMLInputFactory.newInstance();
        if (!this.disableXmlSecurity) {
            f.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        }
        return f;
    }
}
