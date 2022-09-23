// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.osgi;

import org.codehaus.stax2.osgi.Stax2ValidationSchemaFactoryProvider;
import org.codehaus.stax2.osgi.Stax2OutputFactoryProvider;
import java.util.Dictionary;
import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

public class WstxBundleActivator implements BundleActivator
{
    public void start(final BundleContext ctxt) {
        final InputFactoryProviderImpl inputP = new InputFactoryProviderImpl();
        ctxt.registerService(Stax2InputFactoryProvider.class.getName(), (Object)inputP, (Dictionary)inputP.getProperties());
        final OutputFactoryProviderImpl outputP = new OutputFactoryProviderImpl();
        ctxt.registerService(Stax2OutputFactoryProvider.class.getName(), (Object)outputP, (Dictionary)outputP.getProperties());
        final ValidationSchemaFactoryProviderImpl[] impls = ValidationSchemaFactoryProviderImpl.createAll();
        for (int i = 0, len = impls.length; i < len; ++i) {
            final ValidationSchemaFactoryProviderImpl impl = impls[i];
            ctxt.registerService(Stax2ValidationSchemaFactoryProvider.class.getName(), (Object)impl, (Dictionary)impl.getProperties());
        }
    }
    
    public void stop(final BundleContext ctxt) {
    }
}
