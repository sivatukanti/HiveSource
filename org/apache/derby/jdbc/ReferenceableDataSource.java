// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Locale;
import javax.naming.RefAddr;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class ReferenceableDataSource extends EmbeddedBaseDataSource implements ObjectFactory
{
    private static final long serialVersionUID = 1872877359127597176L;
    private static final Class[] STRING_ARG;
    private static final Class[] INT_ARG;
    private static final Class[] BOOLEAN_ARG;
    private static final Class[] SHORT_ARG;
    
    public Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable hashtable) throws Exception {
        Object instance = null;
        if (o instanceof Reference) {
            final Reference reference = (Reference)o;
            final String className = reference.getClassName();
            if (className != null && className.startsWith("org.apache.derby.jdbc.Embedded")) {
                instance = Class.forName(className).newInstance();
                setBeanProperties(instance, reference);
            }
        }
        return instance;
    }
    
    private static void setBeanProperties(final Object obj, final Reference reference) throws Exception {
        final Enumeration<RefAddr> all = reference.getAll();
        while (all.hasMoreElements()) {
            final RefAddr refAddr = all.nextElement();
            final String type = refAddr.getType();
            final String s = (String)refAddr.getContent();
            final String string = "set" + type.substring(0, 1).toUpperCase(Locale.ENGLISH) + type.substring(1);
            Method method;
            Serializable s2;
            try {
                method = obj.getClass().getMethod(string, (Class<?>[])ReferenceableDataSource.STRING_ARG);
                s2 = s;
            }
            catch (NoSuchMethodException ex) {
                try {
                    method = obj.getClass().getMethod(string, (Class<?>[])ReferenceableDataSource.INT_ARG);
                    s2 = Integer.valueOf(s);
                }
                catch (NoSuchMethodException ex2) {
                    try {
                        method = obj.getClass().getMethod(string, (Class<?>[])ReferenceableDataSource.BOOLEAN_ARG);
                        s2 = Boolean.valueOf(s);
                    }
                    catch (NoSuchMethodException ex3) {
                        method = obj.getClass().getMethod(string, (Class<?>[])ReferenceableDataSource.SHORT_ARG);
                        s2 = Short.valueOf(s);
                    }
                }
            }
            method.invoke(obj, s2);
        }
    }
    
    static {
        STRING_ARG = new Class[] { "".getClass() };
        INT_ARG = new Class[] { Integer.TYPE };
        BOOLEAN_ARG = new Class[] { Boolean.TYPE };
        SHORT_ARG = new Class[] { Short.TYPE };
    }
}
