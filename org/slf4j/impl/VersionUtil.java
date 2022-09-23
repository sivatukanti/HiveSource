// 
// Decompiled by Procyon v0.5.36
// 

package org.slf4j.impl;

import java.lang.reflect.Method;
import org.slf4j.helpers.Util;

public class VersionUtil
{
    static final int MINIMAL_VERSION = 5;
    
    public static int getJavaMajorVersion() {
        final String javaVersionString = Util.safeGetSystemProperty("java.version");
        return getJavaMajorVersion(javaVersionString);
    }
    
    static int getJavaMajorVersion(final String versionString) {
        if (versionString == null) {
            return 5;
        }
        if (versionString.startsWith("1.")) {
            return versionString.charAt(2) - '0';
        }
        try {
            final Method versionMethod = Runtime.class.getMethod("version", (Class<?>[])new Class[0]);
            final Object versionObj = versionMethod.invoke(null, new Object[0]);
            final Method majorMethod = versionObj.getClass().getMethod("major", (Class<?>[])new Class[0]);
            final Integer resultInteger = (Integer)majorMethod.invoke(versionObj, new Object[0]);
            return resultInteger;
        }
        catch (Exception e) {
            return 5;
        }
    }
}
