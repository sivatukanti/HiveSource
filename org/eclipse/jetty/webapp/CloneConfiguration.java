// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

public class CloneConfiguration extends AbstractConfiguration
{
    final WebAppContext _template;
    
    CloneConfiguration(final WebAppContext template) {
        this._template = template;
    }
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
        for (final Configuration configuration : this._template.getConfigurations()) {
            configuration.cloneConfigure(this._template, context);
        }
    }
    
    @Override
    public void deconfigure(final WebAppContext context) throws Exception {
        for (final Configuration configuration : this._template.getConfigurations()) {
            configuration.deconfigure(context);
        }
    }
}
