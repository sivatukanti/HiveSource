// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi.java;

import org.eclipse.jetty.util.log.Log;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import org.eclipse.jetty.util.log.Logger;
import javax.naming.spi.ObjectFactory;

public class javaURLContextFactory implements ObjectFactory
{
    private static final Logger LOG;
    
    public Object getObjectInstance(final Object url, final Name name, final Context ctx, final Hashtable env) throws Exception {
        if (url == null) {
            if (javaURLContextFactory.LOG.isDebugEnabled()) {
                javaURLContextFactory.LOG.debug(">>> new root context requested ", new Object[0]);
            }
            return new javaRootURLContext(env);
        }
        if (url instanceof String) {
            if (javaURLContextFactory.LOG.isDebugEnabled()) {
                javaURLContextFactory.LOG.debug(">>> resolution of url " + url + " requested", new Object[0]);
            }
            final Context rootctx = new javaRootURLContext(env);
            return rootctx.lookup((String)url);
        }
        if (!(url instanceof String[])) {
            if (javaURLContextFactory.LOG.isDebugEnabled()) {
                javaURLContextFactory.LOG.debug(">>> No idea what to do, so return a new root context anyway", new Object[0]);
            }
            return new javaRootURLContext(env);
        }
        if (javaURLContextFactory.LOG.isDebugEnabled()) {
            javaURLContextFactory.LOG.debug(">>> resolution of array of urls requested", new Object[0]);
        }
        final String[] urls = (String[])url;
        final Context rootctx2 = new javaRootURLContext(env);
        Object object = null;
        NamingException e = null;
        for (int i = 0; i < urls.length && object == null; ++i) {
            try {
                object = rootctx2.lookup(urls[i]);
            }
            catch (NamingException x) {
                e = x;
            }
        }
        if (object == null) {
            throw e;
        }
        return object;
    }
    
    static {
        LOG = Log.getLogger(javaURLContextFactory.class);
    }
}
