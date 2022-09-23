// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.Properties;

public class PropertyUtil
{
    public static String sortProperties(final Properties properties) {
        return sortProperties(properties, null);
    }
    
    public static String sortProperties(final Properties properties, final String str) {
        int n = (properties == null) ? 0 : properties.size();
        int toIndex = 0;
        String[] a = new String[n];
        if (properties != null) {
            final Enumeration<?> propertyNames = properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                if (toIndex == n) {
                    n *= 2;
                    final String[] array = new String[n];
                    System.arraycopy(a, 0, array, 0, toIndex);
                    a = array;
                }
                a[toIndex++] = (String)propertyNames.nextElement();
            }
            Arrays.sort(a, 0, toIndex);
        }
        final StringBuffer sb = new StringBuffer();
        if (str == null) {
            sb.append("{ ");
        }
        for (int i = 0; i < toIndex; ++i) {
            if (i > 0 && str == null) {
                sb.append(", ");
            }
            final String s = a[i];
            if (str != null) {
                sb.append(str);
            }
            sb.append(s);
            sb.append("=");
            sb.append(properties.getProperty(s, "MISSING_VALUE"));
            if (str != null) {
                sb.append("\n");
            }
        }
        if (str == null) {
            sb.append(" }");
        }
        return sb.toString();
    }
    
    public static void copyProperties(final Properties properties, final Properties properties2) {
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final Object nextElement = propertyNames.nextElement();
            properties2.put(nextElement, properties.get(nextElement));
        }
    }
    
    public static void loadWithTrimmedValues(final InputStream inStream, final Properties properties) throws IOException {
        if (inStream == null || properties == null) {
            return;
        }
        final Properties properties2 = new Properties();
        properties2.load(inStream);
        final Enumeration<?> propertyNames = properties2.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            properties.put(s, properties2.getProperty(s).trim());
        }
    }
}
