// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import java.util.Locale;
import java.lang.reflect.Modifier;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

public class EmbeddedDataSource extends ReferenceableDataSource implements Referenceable
{
    private static final long serialVersionUID = -4945135214995641181L;
    
    public final Reference getReference() throws NamingException {
        final Reference reference = new Reference(this.getClass().getName(), "org.apache.derby.jdbc.ReferenceableDataSource", null);
        addBeanProperties(this, reference);
        return reference;
    }
    
    private static void addBeanProperties(final Object obj, final Reference reference) {
        final Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (method.getParameterTypes().length == 0) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    final String name = method.getName();
                    if (name.length() >= 5) {
                        if (name.startsWith("get")) {
                            final Class<?> returnType = method.getReturnType();
                            if (Integer.TYPE.equals(returnType) || Short.TYPE.equals(returnType) || String.class.equals(returnType) || Boolean.TYPE.equals(returnType)) {
                                final String concat = name.substring(3, 4).toLowerCase(Locale.ENGLISH).concat(name.substring(4));
                                try {
                                    final Object invoke = method.invoke(obj, (Object[])null);
                                    if (invoke != null) {
                                        reference.add(new StringRefAddr(concat, invoke.toString()));
                                    }
                                }
                                catch (IllegalAccessException ex) {}
                                catch (InvocationTargetException ex2) {}
                            }
                        }
                    }
                }
            }
        }
    }
}
