// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.xml;

import javax.ws.rs.core.Context;
import com.sun.jersey.core.util.FeaturesAndProperties;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentBuilderFactoryProvider extends ThreadLocalSingletonContextProvider<DocumentBuilderFactory>
{
    private static final Logger LOGGER;
    private final boolean disableXmlSecurity;
    
    public DocumentBuilderFactoryProvider(@Context final FeaturesAndProperties fps) {
        super(DocumentBuilderFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }
    
    @Override
    protected DocumentBuilderFactory getInstance() {
        final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        if (!this.disableXmlSecurity) {
            f.setExpandEntityReferences(false);
        }
        return f;
    }
    
    static {
        LOGGER = Logger.getLogger(DocumentBuilderFactoryProvider.class.getName());
    }
}
