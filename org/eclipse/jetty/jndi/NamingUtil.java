// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import org.eclipse.jetty.util.log.Log;
import javax.naming.NameParser;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.Context;
import org.eclipse.jetty.util.log.Logger;

public class NamingUtil
{
    public static final Logger __log;
    
    public static Context bind(final Context ctx, final String nameStr, final Object obj) throws NamingException {
        final Name name = ctx.getNameParser("").parse(nameStr);
        if (name.size() == 0) {
            return null;
        }
        Context subCtx = ctx;
        for (int i = 0; i < name.size() - 1; ++i) {
            try {
                subCtx = (Context)subCtx.lookup(name.get(i));
                if (NamingUtil.__log.isDebugEnabled()) {
                    NamingUtil.__log.debug("Subcontext " + name.get(i) + " already exists", new Object[0]);
                }
            }
            catch (NameNotFoundException e) {
                subCtx = subCtx.createSubcontext(name.get(i));
                if (NamingUtil.__log.isDebugEnabled()) {
                    NamingUtil.__log.debug("Subcontext " + name.get(i) + " created", new Object[0]);
                }
            }
        }
        subCtx.rebind(name.get(name.size() - 1), obj);
        if (NamingUtil.__log.isDebugEnabled()) {
            NamingUtil.__log.debug("Bound object to " + name.get(name.size() - 1), new Object[0]);
        }
        return subCtx;
    }
    
    public static void unbind(final Context ctx) throws NamingException {
        final NamingEnumeration ne = ctx.listBindings(ctx.getNameInNamespace());
        while (ne.hasMoreElements()) {
            final Binding b = (Binding)ne.nextElement();
            if (b.getObject() instanceof Context) {
                unbind((Context)b.getObject());
            }
            else {
                ctx.unbind(b.getName());
            }
        }
    }
    
    public static Map flattenBindings(final Context ctx, final String name) throws NamingException {
        final HashMap map = new HashMap();
        final Context c = (Context)ctx.lookup(name);
        final NameParser parser = c.getNameParser("");
        final NamingEnumeration enm = ctx.listBindings(name);
        while (enm.hasMore()) {
            final Binding b = enm.next();
            if (b.getObject() instanceof Context) {
                map.putAll(flattenBindings(c, b.getName()));
            }
            else {
                final Name compoundName = parser.parse(c.getNameInNamespace());
                compoundName.add(b.getName());
                map.put(compoundName.toString(), b.getObject());
            }
        }
        return map;
    }
    
    static {
        __log = Log.getLogger("jndi");
    }
}
