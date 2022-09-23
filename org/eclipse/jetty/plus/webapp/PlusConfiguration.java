// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.webapp;

import org.eclipse.jetty.util.log.Log;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Random;
import javax.naming.NameNotFoundException;
import org.eclipse.jetty.plus.jndi.Transaction;
import org.eclipse.jetty.webapp.DescriptorProcessor;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.AbstractConfiguration;

public class PlusConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    private Integer _key;
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        context.addDecorator(new PlusDecorator(context));
    }
    
    @Override
    public void cloneConfigure(final WebAppContext template, final WebAppContext context) throws Exception {
        context.addDecorator(new PlusDecorator(context));
    }
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
        this.bindUserTransaction(context);
        context.getMetaData().addDescriptorProcessor(new PlusDescriptorProcessor());
    }
    
    @Override
    public void postConfigure(final WebAppContext context) throws Exception {
        this.lockCompEnv(context);
    }
    
    @Override
    public void deconfigure(final WebAppContext context) throws Exception {
        this.unlockCompEnv(context);
        this._key = null;
        context.setAttribute("org.eclipse.jetty.injectionCollection", null);
        context.setAttribute("org.eclipse.jetty.lifecyleCallbackCollection", null);
    }
    
    public void bindUserTransaction(final WebAppContext context) throws Exception {
        try {
            Transaction.bindToENC();
        }
        catch (NameNotFoundException e) {
            PlusConfiguration.LOG.info("No Transaction manager found - if your webapp requires one, please configure one.", new Object[0]);
        }
    }
    
    protected void lockCompEnv(final WebAppContext wac) throws Exception {
        final ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(wac.getClassLoader());
        try {
            final Random random = new Random();
            this._key = new Integer(random.nextInt());
            final Context context = new InitialContext();
            final Context compCtx = (Context)context.lookup("java:comp");
            compCtx.addToEnvironment("org.eclipse.jndi.lock", this._key);
        }
        finally {
            Thread.currentThread().setContextClassLoader(old_loader);
        }
    }
    
    protected void unlockCompEnv(final WebAppContext wac) throws Exception {
        if (this._key != null) {
            final ClassLoader old_loader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(wac.getClassLoader());
            try {
                final Context context = new InitialContext();
                final Context compCtx = (Context)context.lookup("java:comp");
                compCtx.addToEnvironment("org.eclipse.jndi.unlock", this._key);
            }
            finally {
                Thread.currentThread().setContextClassLoader(old_loader);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(PlusConfiguration.class);
    }
}
