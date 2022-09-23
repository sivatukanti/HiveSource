// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.xml;

import javax.ws.rs.core.Context;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.xml.transform.TransformerFactory;

public class TransformerFactoryProvider extends ThreadLocalSingletonContextProvider<TransformerFactory>
{
    private final boolean disableXmlSecurity;
    
    public TransformerFactoryProvider(@Context final FeaturesAndProperties fps) {
        super(TransformerFactory.class);
        this.disableXmlSecurity = fps.getFeature("com.sun.jersey.config.feature.DisableXmlSecurity");
    }
    
    @Override
    protected TransformerFactory getInstance() {
        final TransformerFactory f = TransformerFactory.newInstance();
        if (!this.disableXmlSecurity) {}
        return f;
    }
}
