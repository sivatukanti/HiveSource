// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.info;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class JVMInfo
{
    public static final int JDK_ID;
    public static final int J2SE_14 = 4;
    public static final int J2SE_142 = 5;
    public static final int J2SE_15 = 6;
    public static final int J2SE_16 = 7;
    public static final int J2SE_17 = 8;
    public static final int J2SE_18 = 9;
    public static final boolean J2ME;
    
    public static int jdbcMajorVersion() {
        switch (JVMInfo.JDK_ID) {
            case 4: {
                return 3;
            }
            case 5: {
                return 3;
            }
            case 6: {
                return 3;
            }
            case 7: {
                return 4;
            }
            case 8: {
                return 4;
            }
            default: {
                return 4;
            }
        }
    }
    
    public static int jdbcMinorVersion() {
        switch (JVMInfo.JDK_ID) {
            case 4: {
                return 0;
            }
            case 5: {
                return 0;
            }
            case 6: {
                return 0;
            }
            case 7: {
                return 0;
            }
            case 8: {
                return 1;
            }
            default: {
                return 2;
            }
        }
    }
    
    private static boolean vmCheck(final String s, final String s2) {
        return s.equals(s2) || s.startsWith(s2 + "_");
    }
    
    public static String derbyVMLevel() {
        final String string = jdbcMajorVersion() + "." + jdbcMinorVersion();
        switch (JVMInfo.JDK_ID) {
            case 4: {
                return JVMInfo.J2ME ? "J2ME - JDBC for CDC/FP 1.1" : ("J2SE 1.4 - JDBC " + string);
            }
            case 5: {
                return "J2SE 1.4.2 - JDBC " + string;
            }
            case 6: {
                return "J2SE 5.0 - JDBC " + string;
            }
            case 7: {
                return "Java SE 6 - JDBC " + string;
            }
            case 8: {
                return "Java SE 7 - JDBC " + string;
            }
            case 9: {
                return "Java SE 8 - JDBC " + string;
            }
            default: {
                return "?-?";
            }
        }
    }
    
    private static String getSystemProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return System.getProperty(s);
            }
        });
    }
    
    public static final boolean isSunJVM() {
        final String systemProperty = getSystemProperty("java.vendor");
        return "Sun Microsystems Inc.".equals(systemProperty) || "Oracle Corporation".equals(systemProperty);
    }
    
    public static final boolean isIBMJVM() {
        return "IBM Corporation".equals(getSystemProperty("java.vendor"));
    }
    
    public static void javaDump() {
        if (isIBMJVM()) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                    private final /* synthetic */ Method val$ibmm = Class.forName("com.ibm.jvm.Dump").getMethod("JavaDump", (Class<?>[])new Class[0]);
                    
                    public Object run() throws IllegalAccessException, MalformedURLException, InstantiationException, InvocationTargetException {
                        return this.val$ibmm.invoke(null, new Object[0]);
                    }
                });
            }
            catch (Exception ex) {}
        }
    }
    
    public static boolean hasJNDI() {
        try {
            Class.forName("javax.naming.Referenceable");
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
        return true;
    }
    
    static {
        String property;
        try {
            property = System.getProperty("java.specification.name");
        }
        catch (SecurityException ex) {
            property = null;
        }
        String property2;
        try {
            property2 = System.getProperty("java.specification.version", "1.4");
        }
        catch (SecurityException ex2) {
            property2 = "1.4";
        }
        int jdk_ID;
        boolean j2ME;
        if (property != null && (property.startsWith("J2ME") || property.startsWith("CDC") || (property.indexOf("Profile") > -1 && property.indexOf("Specification") > -1))) {
            jdk_ID = 4;
            j2ME = true;
        }
        else {
            j2ME = false;
            if (property2.equals("1.4")) {
                final String property3 = System.getProperty("java.version", "1.4.0");
                if (vmCheck(property3, "1.4.0") || vmCheck(property3, "1.4.1")) {
                    jdk_ID = 4;
                }
                else {
                    jdk_ID = 5;
                }
            }
            else if (property2.equals("1.5")) {
                jdk_ID = 6;
            }
            else if (property2.equals("1.6")) {
                jdk_ID = 7;
            }
            else if (property2.equals("1.7")) {
                jdk_ID = 8;
            }
            else if (property2.equals("1.8")) {
                jdk_ID = 9;
            }
            else {
                jdk_ID = 4;
                try {
                    if (Float.parseFloat(property2) > 1.8f) {
                        jdk_ID = 9;
                    }
                }
                catch (NumberFormatException ex3) {}
            }
        }
        JDK_ID = jdk_ID;
        J2ME = j2ME;
    }
}
