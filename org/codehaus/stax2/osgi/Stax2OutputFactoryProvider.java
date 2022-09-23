// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.osgi;

import org.codehaus.stax2.XMLOutputFactory2;

public interface Stax2OutputFactoryProvider
{
    public static final String OSGI_SVC_PROP_IMPL_NAME = "org.codehaus.stax2.implName";
    public static final String OSGI_SVC_PROP_IMPL_VERSION = "org.codehaus.stax2.implVersion";
    
    XMLOutputFactory2 createOutputFactory();
}
