// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.osgi;

import java.util.Properties;
import com.ctc.wstx.stax.WstxInputFactory;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;

public class InputFactoryProviderImpl implements Stax2InputFactoryProvider
{
    @Override
    public XMLInputFactory2 createInputFactory() {
        return new WstxInputFactory();
    }
    
    protected Properties getProperties() {
        final Properties props = new Properties();
        props.setProperty("org.codehaus.stax2.implName", CommonConfig.getImplName());
        props.setProperty("org.codehaus.stax2.implVersion", CommonConfig.getImplVersion());
        return props;
    }
}
