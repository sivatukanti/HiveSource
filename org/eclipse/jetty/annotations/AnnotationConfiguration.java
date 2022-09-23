// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.AbstractConfiguration;

public class AnnotationConfiguration extends AbstractConfiguration
{
    @Override
    public void configure(final WebAppContext context) throws Exception {
        context.addDecorator(new AnnotationDecorator(context));
    }
    
    @Override
    public void cloneConfigure(final WebAppContext template, final WebAppContext context) throws Exception {
        context.addDecorator(new AnnotationDecorator(context));
    }
}
