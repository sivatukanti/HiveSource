// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi.java;

import javax.naming.NamingException;
import javax.naming.CompoundName;
import javax.naming.Name;
import java.util.Properties;
import javax.naming.NameParser;

public class javaNameParser implements NameParser
{
    static Properties syntax;
    
    public Name parse(final String name) throws NamingException {
        return new CompoundName(name, javaNameParser.syntax);
    }
    
    static {
        (javaNameParser.syntax = new Properties()).put("jndi.syntax.direction", "left_to_right");
        javaNameParser.syntax.put("jndi.syntax.separator", "/");
        javaNameParser.syntax.put("jndi.syntax.ignorecase", "false");
    }
}
