// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.naming.NameParser;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.util.WeakHashMap;
import org.eclipse.jetty.util.log.Logger;
import javax.naming.spi.ObjectFactory;

public class ContextFactory implements ObjectFactory
{
    private static Logger __log;
    private static final WeakHashMap __contextMap;
    private static final ThreadLocal __threadContext;
    
    public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable env) throws Exception {
        Context ctx = ContextFactory.__threadContext.get();
        if (ctx != null) {
            if (ContextFactory.__log.isDebugEnabled()) {
                ContextFactory.__log.debug("Using the Context that is bound on the thread", new Object[0]);
            }
            return ctx;
        }
        ClassLoader loader = null;
        if (ContextHandler.getCurrentContext() != null) {
            loader = ContextHandler.getCurrentContext().getContextHandler().getClassLoader();
        }
        if (loader != null) {
            if (ContextFactory.__log.isDebugEnabled()) {
                ContextFactory.__log.debug("Using classloader of current org.eclipse.jetty.server.handler.ContextHandler", new Object[0]);
            }
        }
        else {
            loader = Thread.currentThread().getContextClassLoader();
            if (ContextFactory.__log.isDebugEnabled()) {
                ContextFactory.__log.debug("Using thread context classloader", new Object[0]);
            }
        }
        ctx = ContextFactory.__contextMap.get(loader);
        if (ctx == null) {
            ctx = this.getParentClassLoaderContext(loader);
            if (ctx == null) {
                final Reference ref = (Reference)obj;
                final StringRefAddr parserAddr = (StringRefAddr)ref.get("parser");
                final String parserClassName = (parserAddr == null) ? null : ((String)parserAddr.getContent());
                final NameParser parser = (NameParser)((parserClassName == null) ? null : loader.loadClass(parserClassName).newInstance());
                ctx = new NamingContext(env, name.get(0), (NamingContext)nameCtx, parser);
                if (ContextFactory.__log.isDebugEnabled()) {
                    ContextFactory.__log.debug("No entry for classloader: " + loader, new Object[0]);
                }
                ContextFactory.__contextMap.put(loader, ctx);
            }
        }
        return ctx;
    }
    
    public Context getParentClassLoaderContext(final ClassLoader loader) {
        Context ctx;
        ClassLoader cl;
        for (ctx = null, cl = loader, cl = cl.getParent(); cl != null && ctx == null; ctx = ContextFactory.__contextMap.get(cl), cl = cl.getParent()) {}
        return ctx;
    }
    
    public static Context setComponentContext(final Context ctx) {
        final Context previous = ContextFactory.__threadContext.get();
        ContextFactory.__threadContext.set(ctx);
        return previous;
    }
    
    public static void resetComponentContext(final Context ctx) {
        ContextFactory.__threadContext.set(ctx);
    }
    
    public static void dump(final Appendable out, final String indent) throws IOException {
        out.append("o.e.j.jndi.ContextFactory@").append(Long.toHexString(ContextFactory.__contextMap.hashCode())).append("\n");
        final int size = ContextFactory.__contextMap.size();
        int i = 0;
        for (final Map.Entry<ClassLoader, NamingContext> entry : ContextFactory.__contextMap.entrySet()) {
            final boolean last = ++i == size;
            final ClassLoader loader = entry.getKey();
            out.append(indent).append(" +- ").append(loader.getClass().getSimpleName()).append("@").append(Long.toHexString(loader.hashCode())).append(": ");
            final NamingContext context = entry.getValue();
            context.dump(out, indent + (last ? "    " : " |  "));
        }
    }
    
    static {
        ContextFactory.__log = NamingUtil.__log;
        __contextMap = new WeakHashMap();
        __threadContext = new ThreadLocal();
    }
}
