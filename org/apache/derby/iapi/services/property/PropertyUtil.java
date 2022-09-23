// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.property;

import java.util.Enumeration;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.error.StandardException;
import java.io.Serializable;
import org.apache.derby.iapi.services.monitor.ModuleFactory;
import java.util.Properties;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Dictionary;

public class PropertyUtil
{
    public static final String[] servicePropertyList;
    public static final int SET_IN_JVM = 0;
    public static final int SET_IN_DATABASE = 1;
    public static final int SET_IN_APPLICATION = 2;
    public static final int NOT_SET = -1;
    
    static int whereSet(final String s, final Dictionary dictionary) {
        final boolean dbOnly = isDBOnly(dictionary);
        if (!dbOnly && Monitor.getMonitor().getJVMProperty(s) != null) {
            return 0;
        }
        if (dictionary != null && dictionary.get(s) != null) {
            return 1;
        }
        if (!dbOnly && getSystemProperty(s) != null) {
            return 2;
        }
        return -1;
    }
    
    public static boolean isDBOnly(final Dictionary dictionary) {
        if (dictionary == null) {
            return false;
        }
        final String s = dictionary.get("derby.database.propertiesOnly");
        return Boolean.valueOf((s != null) ? s.trim() : s);
    }
    
    public static boolean isDBOnly(final Properties properties) {
        if (properties == null) {
            return false;
        }
        final String property = properties.getProperty("derby.database.propertiesOnly");
        return Boolean.valueOf((property != null) ? property.trim() : property);
    }
    
    public static String getSystemProperty(final String s) {
        return getSystemProperty(s, null);
    }
    
    public static String getSystemProperty(final String key, final String s) {
        final ModuleFactory monitorLite = Monitor.getMonitorLite();
        String s2 = monitorLite.getJVMProperty(key);
        if (s2 == null) {
            final Properties applicationProperties = monitorLite.getApplicationProperties();
            if (applicationProperties != null) {
                s2 = applicationProperties.getProperty(key);
            }
        }
        return (s2 == null) ? s : s2;
    }
    
    public static String getPropertyFromSet(final Properties properties, final String anObject) {
        final boolean b = properties != null && isDBOnly(properties);
        if ("derby.authentication.provider".equals(anObject)) {
            final String propertyFromSet = getPropertyFromSet(true, properties, anObject);
            if (nativeAuthenticationEnabled(propertyFromSet)) {
                return propertyFromSet;
            }
        }
        return getPropertyFromSet(b, properties, anObject);
    }
    
    public static Serializable getPropertyFromSet(final Dictionary dictionary, final String s) {
        return getPropertyFromSet(dictionary != null && isDBOnly(dictionary), dictionary, s);
    }
    
    public static Serializable getPropertyFromSet(final boolean b, final Dictionary dictionary, final String s) {
        if (dictionary != null) {
            if (!b) {
                final String jvmProperty = Monitor.getMonitor().getJVMProperty(s);
                if (jvmProperty != null) {
                    return jvmProperty;
                }
            }
            final Serializable s2 = dictionary.get(s);
            if (s2 != null) {
                return s2;
            }
            if (b) {
                return null;
            }
        }
        return getSystemProperty(s);
    }
    
    public static String getPropertyFromSet(final boolean b, final Properties properties, final String key) {
        if (properties != null) {
            if (!b) {
                final String jvmProperty = Monitor.getMonitor().getJVMProperty(key);
                if (jvmProperty != null) {
                    return jvmProperty;
                }
            }
            final String property = properties.getProperty(key);
            if (property != null) {
                return property;
            }
            if (b) {
                return null;
            }
        }
        return getSystemProperty(key);
    }
    
    public static String getDatabaseProperty(final PersistentSet set, final String s) throws StandardException {
        if (set == null) {
            return null;
        }
        final Serializable property = set.getProperty(s);
        if (property == null) {
            return null;
        }
        return property.toString();
    }
    
    public static String getServiceProperty(final PersistentSet set, final String s, final String s2) throws StandardException {
        final String databaseProperty = getDatabaseProperty(set, "derby.database.propertiesOnly");
        final boolean booleanValue = Boolean.valueOf((databaseProperty != null) ? databaseProperty.trim() : databaseProperty);
        if (!booleanValue) {
            final String jvmProperty = Monitor.getMonitor().getJVMProperty(s);
            if (jvmProperty != null) {
                return jvmProperty;
            }
        }
        final String databaseProperty2 = getDatabaseProperty(set, s);
        if (databaseProperty2 != null) {
            return databaseProperty2;
        }
        if (booleanValue) {
            return s2;
        }
        return getSystemProperty(s, s2);
    }
    
    public static String getServiceProperty(final PersistentSet set, final String s) throws StandardException {
        return getServiceProperty(set, s, null);
    }
    
    public static boolean getSystemBoolean(final String s) {
        return getSystemBoolean(s, false);
    }
    
    public static boolean getSystemBoolean(final String s, final boolean b) {
        final String systemProperty = getSystemProperty(s);
        if (systemProperty == null) {
            return b;
        }
        return Boolean.valueOf(systemProperty.trim());
    }
    
    public static boolean getServiceBoolean(final PersistentSet set, final String s, final boolean b) throws StandardException {
        return booleanProperty(s, getServiceProperty(set, s), b);
    }
    
    public static int getSystemInt(final String s, final int n, final int n2, final int n3) {
        return handleInt(getSystemProperty(s), n, n2, n3);
    }
    
    public static int getServiceInt(final PersistentSet set, final String s, final int n, final int n2, final int n3) throws StandardException {
        return handleInt(getServiceProperty(set, s), n, n2, n3);
    }
    
    public static int getServiceInt(final PersistentSet set, final Properties properties, final String key, final int n, final int n2, final int n3) throws StandardException {
        String s = null;
        if (properties != null) {
            s = properties.getProperty(key);
        }
        if (s == null) {
            s = getServiceProperty(set, key);
        }
        return handleInt(s, n, n2, n3);
    }
    
    public static int getSystemInt(final String s, final int n) {
        return getSystemInt(s, 0, Integer.MAX_VALUE, n);
    }
    
    public static int handleInt(final String s, final int n, final int n2, final int n3) {
        if (s == null) {
            return n3;
        }
        try {
            final int int1 = Integer.parseInt(s);
            if (int1 >= n && int1 <= n2) {
                return int1;
            }
        }
        catch (NumberFormatException ex) {}
        return n3;
    }
    
    public static boolean booleanProperty(final String s, final Serializable s2, final boolean b) throws StandardException {
        if (s2 == null) {
            return b;
        }
        final String trim = ((String)s2).trim();
        if ("TRUE".equals(StringUtil.SQLToUpperCase(trim))) {
            return true;
        }
        if ("FALSE".equals(StringUtil.SQLToUpperCase(trim))) {
            return false;
        }
        throw StandardException.newException("XCY00.S", s, trim);
    }
    
    public static int intPropertyValue(final String s, final Serializable s2, final int n, final int n2, final int n3) throws StandardException {
        if (s2 == null) {
            return n3;
        }
        final String trim = ((String)s2).trim();
        try {
            final int int1 = Integer.parseInt(trim);
            if (int1 < n || int1 > n2) {
                throw StandardException.newException("XCY00.S", s, trim);
            }
            return int1;
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("XCY00.S", s, trim);
        }
    }
    
    public static boolean isServiceProperty(final String s) {
        for (int i = 0; i < PropertyUtil.servicePropertyList.length; ++i) {
            if (s.equals(PropertyUtil.servicePropertyList[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean existsBuiltinUser(final PersistentSet set, final String s) throws StandardException {
        return propertiesContainsBuiltinUser(set.getProperties(), s) || (!Boolean.valueOf(getDatabaseProperty(set, "derby.database.propertiesOnly")) && systemPropertiesExistsBuiltinUser(s));
    }
    
    public static boolean nativeAuthenticationEnabled(final Properties properties) {
        return nativeAuthenticationEnabled(getPropertyFromSet(properties, "derby.authentication.provider"));
    }
    
    private static boolean nativeAuthenticationEnabled(final String s) {
        return s != null && StringUtil.SQLToUpperCase(s).startsWith("NATIVE:");
    }
    
    public static boolean localNativeAuthenticationEnabled(final Properties properties) {
        return nativeAuthenticationEnabled(properties) && StringUtil.SQLToUpperCase(getPropertyFromSet(properties, "derby.authentication.provider")).endsWith(":LOCAL");
    }
    
    private static boolean systemPropertiesExistsBuiltinUser(final String s) {
        final ModuleFactory monitorLite = Monitor.getMonitorLite();
        try {
            if (propertiesContainsBuiltinUser(System.getProperties(), s)) {
                return true;
            }
        }
        catch (SecurityException ex) {
            if (monitorLite.getJVMProperty("derby.user." + IdUtil.SQLIdentifier2CanonicalPropertyUsername(s)) != null) {
                return true;
            }
        }
        return propertiesContainsBuiltinUser(monitorLite.getApplicationProperties(), s);
    }
    
    private static boolean propertiesContainsBuiltinUser(final Properties properties, final String s) {
        if (properties != null) {
            final Enumeration<?> propertyNames = properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                final String s2 = (String)propertyNames.nextElement();
                if (s2.startsWith("derby.user.") && s.equals(StringUtil.normalizeSQLIdentifier(s2.substring("derby.user.".length())))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        servicePropertyList = new String[] { "derby.engineType", "derby.database.noAutoBoot", "derby.storage.tempDirectory", "encryptionProvider", "encryptionAlgorithm", "restoreFrom", "logDevice", "derby.storage.logArchiveMode" };
    }
}
