// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import javax.naming.NamingException;
import javax.naming.CompoundName;
import javax.naming.Name;
import java.util.Properties;
import javax.naming.NameParser;
import org.eclipse.jetty.jndi.local.localContextRoot;
import javax.naming.Context;
import java.util.Hashtable;
import org.eclipse.jetty.util.log.Logger;

public class InitialContextFactory implements javax.naming.spi.InitialContextFactory
{
    private static Logger __log;
    
    public Context getInitialContext(final Hashtable env) {
        InitialContextFactory.__log.debug("InitialContextFactory.getInitialContext()", new Object[0]);
        final Context ctx = new localContextRoot(env);
        if (InitialContextFactory.__log.isDebugEnabled()) {
            InitialContextFactory.__log.debug("Created initial context delegate for local namespace:" + ctx, new Object[0]);
        }
        return ctx;
    }
    
    static {
        InitialContextFactory.__log = NamingUtil.__log;
    }
    
    public static class DefaultParser implements NameParser
    {
        static Properties syntax;
        
        public Name parse(final String name) throws NamingException {
            return new CompoundName(name, DefaultParser.syntax);
        }
        
        static {
            (DefaultParser.syntax = new Properties()).put("jndi.syntax.direction", "left_to_right");
            DefaultParser.syntax.put("jndi.syntax.separator", "/");
            DefaultParser.syntax.put("jndi.syntax.ignorecase", "false");
        }
    }
}
