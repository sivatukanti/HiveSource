// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.util.Iterator;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Map;

public class FragmentConfiguration extends AbstractConfiguration
{
    public static final String FRAGMENT_RESOURCES = "org.eclipse.jetty.webFragments";
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        if (!context.isConfigurationDiscovered()) {
            return;
        }
        this.findWebFragments(context, context.getMetaData());
    }
    
    @Override
    public void postConfigure(final WebAppContext context) throws Exception {
        context.setAttribute("org.eclipse.jetty.webFragments", null);
    }
    
    public void findWebFragments(final WebAppContext context, final MetaData metaData) throws Exception {
        final Map<Resource, Resource> frags = (Map<Resource, Resource>)context.getAttribute("org.eclipse.jetty.webFragments");
        if (frags != null) {
            for (final Resource key : frags.keySet()) {
                if (key.isDirectory()) {
                    metaData.addFragment(key, frags.get(key));
                }
                else {
                    metaData.addFragment(key, frags.get(key));
                }
            }
        }
    }
}
