// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.util.Map;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Iterator;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public class WebXmlConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        final String defaultsDescriptor = context.getDefaultsDescriptor();
        if (defaultsDescriptor != null && defaultsDescriptor.length() > 0) {
            Resource dftResource = Resource.newSystemResource(defaultsDescriptor);
            if (dftResource == null) {
                dftResource = context.newResource(defaultsDescriptor);
            }
            context.getMetaData().setDefaults(dftResource);
        }
        final Resource webxml = this.findWebXml(context);
        if (webxml != null) {
            context.getMetaData().setWebXml(webxml);
            context.getServletContext().setEffectiveMajorVersion(context.getMetaData().getWebXml().getMajorVersion());
            context.getServletContext().setEffectiveMinorVersion(context.getMetaData().getWebXml().getMinorVersion());
        }
        for (final String overrideDescriptor : context.getOverrideDescriptors()) {
            if (overrideDescriptor != null && overrideDescriptor.length() > 0) {
                Resource orideResource = Resource.newSystemResource(overrideDescriptor);
                if (orideResource == null) {
                    orideResource = context.newResource(overrideDescriptor);
                }
                context.getMetaData().addOverride(orideResource);
            }
        }
    }
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
        if (context.isStarted()) {
            WebXmlConfiguration.LOG.debug("Cannot configure webapp after it is started", new Object[0]);
            return;
        }
        context.getMetaData().addDescriptorProcessor(new StandardDescriptorProcessor());
    }
    
    protected Resource findWebXml(final WebAppContext context) throws IOException, MalformedURLException {
        final String descriptor = context.getDescriptor();
        if (descriptor != null) {
            final Resource web = context.newResource(descriptor);
            if (web.exists() && !web.isDirectory()) {
                return web;
            }
        }
        final Resource web_inf = context.getWebInf();
        if (web_inf != null && web_inf.isDirectory()) {
            final Resource web2 = web_inf.addPath("web.xml");
            if (web2.exists()) {
                return web2;
            }
            if (WebXmlConfiguration.LOG.isDebugEnabled()) {
                WebXmlConfiguration.LOG.debug("No WEB-INF/web.xml in " + context.getWar() + ". Serving files and default/dynamic servlets only", new Object[0]);
            }
        }
        return null;
    }
    
    @Override
    public void deconfigure(final WebAppContext context) throws Exception {
        context.setWelcomeFiles(null);
        if (context.getErrorHandler() instanceof ErrorPageErrorHandler) {
            ((ErrorPageErrorHandler)context.getErrorHandler()).setErrorPages(null);
        }
    }
    
    static {
        LOG = Log.getLogger(WebXmlConfiguration.class);
    }
}
