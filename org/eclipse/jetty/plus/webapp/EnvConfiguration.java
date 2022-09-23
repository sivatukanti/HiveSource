// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.webapp;

import org.eclipse.jetty.util.log.Log;
import javax.naming.NamingException;
import javax.naming.Name;
import org.eclipse.jetty.jndi.NamingUtil;
import org.eclipse.jetty.plus.jndi.NamingEntry;
import javax.naming.NameParser;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.plus.jndi.NamingEntryUtil;
import java.util.Iterator;
import javax.naming.NameNotFoundException;
import java.util.Collections;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import javax.naming.Binding;
import java.util.List;
import org.eclipse.jetty.jndi.NamingContext;
import java.util.ArrayList;
import org.eclipse.jetty.jndi.local.localContextRoot;
import org.eclipse.jetty.webapp.WebAppContext;
import java.net.URL;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.AbstractConfiguration;

public class EnvConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    private static final String JETTY_ENV_BINDINGS = "org.eclipse.jetty.jndi.EnvConfiguration";
    private URL jettyEnvXmlUrl;
    
    public void setJettyEnvXml(final URL url) {
        this.jettyEnvXmlUrl = url;
    }
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        this.createEnvContext(context);
    }
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
        if (EnvConfiguration.LOG.isDebugEnabled()) {
            EnvConfiguration.LOG.debug("Created java:comp/env for webapp " + context.getContextPath(), new Object[0]);
        }
        if (this.jettyEnvXmlUrl == null) {
            final Resource web_inf = context.getWebInf();
            if (web_inf != null && web_inf.isDirectory()) {
                final Resource jettyEnv = web_inf.addPath("jetty-env.xml");
                if (jettyEnv.exists()) {
                    this.jettyEnvXmlUrl = jettyEnv.getURL();
                }
            }
        }
        if (this.jettyEnvXmlUrl != null) {
            synchronized (localContextRoot.getRoot()) {
                final List<Bound> bindings = new ArrayList<Bound>();
                final NamingContext.Listener listener = new NamingContext.Listener() {
                    public void unbind(final NamingContext ctx, final Binding binding) {
                    }
                    
                    public Binding bind(final NamingContext ctx, final Binding binding) {
                        bindings.add(new Bound(ctx, binding.getName()));
                        return binding;
                    }
                };
                try {
                    localContextRoot.getRoot().addListener(listener);
                    final XmlConfiguration configuration = new XmlConfiguration(this.jettyEnvXmlUrl);
                    configuration.configure(context);
                }
                finally {
                    localContextRoot.getRoot().removeListener(listener);
                    context.setAttribute("org.eclipse.jetty.jndi.EnvConfiguration", bindings);
                }
            }
        }
        this.bindEnvEntries(context);
    }
    
    @Override
    public void deconfigure(final WebAppContext context) throws Exception {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(context.getClassLoader());
        try {
            final Context ic = new InitialContext();
            final Context compCtx = (Context)ic.lookup("java:comp");
            compCtx.destroySubcontext("env");
            final List<Bound> bindings = (List<Bound>)context.getAttribute("org.eclipse.jetty.jndi.EnvConfiguration");
            context.setAttribute("org.eclipse.jetty.jndi.EnvConfiguration", null);
            if (bindings != null) {
                Collections.reverse(bindings);
                for (final Bound b : bindings) {
                    b._context.destroySubcontext(b._name);
                }
            }
        }
        catch (NameNotFoundException e) {
            EnvConfiguration.LOG.warn(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
    
    @Override
    public void destroy(final WebAppContext context) throws Exception {
        try {
            final NamingContext scopeContext = (NamingContext)NamingEntryUtil.getContextForScope(context);
            scopeContext.getParent().destroySubcontext(scopeContext.getName());
        }
        catch (NameNotFoundException e) {
            EnvConfiguration.LOG.ignore(e);
            EnvConfiguration.LOG.debug("No naming entries configured in environment for webapp " + context, new Object[0]);
        }
    }
    
    public void bindEnvEntries(final WebAppContext context) throws NamingException {
        EnvConfiguration.LOG.debug("Binding env entries from the jvm scope", new Object[0]);
        final InitialContext ic = new InitialContext();
        final Context envCtx = (Context)ic.lookup("java:comp/env");
        Object scope = null;
        List<Object> list = NamingEntryUtil.lookupNamingEntries(scope, EnvEntry.class);
        for (final EnvEntry ee : list) {
            ee.bindToENC(ee.getJndiName());
            final Name namingEntryName = NamingEntryUtil.makeNamingEntryName(null, ee);
            NamingUtil.bind(envCtx, namingEntryName.toString(), ee);
        }
        EnvConfiguration.LOG.debug("Binding env entries from the server scope", new Object[0]);
        scope = context.getServer();
        list = NamingEntryUtil.lookupNamingEntries(scope, EnvEntry.class);
        for (final EnvEntry ee : list) {
            ee.bindToENC(ee.getJndiName());
            final Name namingEntryName = NamingEntryUtil.makeNamingEntryName(null, ee);
            NamingUtil.bind(envCtx, namingEntryName.toString(), ee);
        }
        EnvConfiguration.LOG.debug("Binding env entries from the context scope", new Object[0]);
        scope = context;
        list = NamingEntryUtil.lookupNamingEntries(scope, EnvEntry.class);
        for (final EnvEntry ee : list) {
            ee.bindToENC(ee.getJndiName());
            final Name namingEntryName = NamingEntryUtil.makeNamingEntryName(null, ee);
            NamingUtil.bind(envCtx, namingEntryName.toString(), ee);
        }
    }
    
    protected void createEnvContext(final WebAppContext wac) throws NamingException {
        final ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(wac.getClassLoader());
        try {
            final Context context = new InitialContext();
            final Context compCtx = (Context)context.lookup("java:comp");
            compCtx.createSubcontext("env");
        }
        finally {
            Thread.currentThread().setContextClassLoader(old_loader);
        }
    }
    
    static {
        LOG = Log.getLogger(EnvConfiguration.class);
    }
    
    private static class Bound
    {
        final NamingContext _context;
        final String _name;
        
        Bound(final NamingContext context, final String name) {
            this._context = context;
            this._name = name;
        }
    }
}
