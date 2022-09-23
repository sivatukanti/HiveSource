// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Enumeration;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URL;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ClassUtil
{
    public static String findContainingJar(final Class<?> clazz) {
        final ClassLoader loader = clazz.getClassLoader();
        final String classFile = clazz.getName().replaceAll("\\.", "/") + ".class";
        try {
            final Enumeration<URL> itr = loader.getResources(classFile);
            while (itr.hasMoreElements()) {
                final URL url = itr.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    String toReturn = url.getPath();
                    if (toReturn.startsWith("file:")) {
                        toReturn = toReturn.substring("file:".length());
                    }
                    toReturn = URLDecoder.decode(toReturn, "UTF-8");
                    return toReturn.replaceAll("!.*$", "");
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
