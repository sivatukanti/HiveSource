// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.osgi;

import java.util.Properties;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.osgi.Stax2OutputFactoryProvider;

public class OutputFactoryProviderImpl implements Stax2OutputFactoryProvider
{
    @Override
    public XMLOutputFactory2 createOutputFactory() {
        return new WstxOutputFactory();
    }
    
    protected Properties getProperties() {
        final Properties props = new Properties();
        props.setProperty("org.codehaus.stax2.implName", CommonConfig.getImplName());
        props.setProperty("org.codehaus.stax2.implVersion", CommonConfig.getImplVersion());
        return props;
    }
}
