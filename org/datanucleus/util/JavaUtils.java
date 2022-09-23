// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.StringTokenizer;

public class JavaUtils
{
    private static boolean versionInitialised;
    private static int majorVersion;
    private static int minorVersion;
    
    public static boolean isJRE1_5OrAbove() {
        return getJREMajorVersion() != 1 || getJREMinorVersion() >= 5;
    }
    
    public static boolean isJRE1_6OrAbove() {
        return getJREMajorVersion() != 1 || getJREMinorVersion() >= 6;
    }
    
    public static boolean useStackMapFrames() {
        return !isJRE1_6OrBelow();
    }
    
    public static boolean isJRE1_6OrBelow() {
        return getJREMajorVersion() == 1 && getJREMinorVersion() <= 6;
    }
    
    public static boolean isJRE1_7OrAbove() {
        return getJREMajorVersion() != 1 || getJREMinorVersion() >= 7;
    }
    
    public static int getJREMajorVersion() {
        if (!JavaUtils.versionInitialised) {
            initialiseJREVersion();
        }
        return JavaUtils.majorVersion;
    }
    
    public static int getJREMinorVersion() {
        if (!JavaUtils.versionInitialised) {
            initialiseJREVersion();
        }
        return JavaUtils.minorVersion;
    }
    
    private static void initialiseJREVersion() {
        final String version = System.getProperty("java.version");
        final StringTokenizer tokeniser = new StringTokenizer(version, ".");
        String token = tokeniser.nextToken();
        try {
            Integer ver = Integer.valueOf(token);
            JavaUtils.majorVersion = ver;
            token = tokeniser.nextToken();
            ver = Integer.valueOf(token);
            JavaUtils.minorVersion = ver;
        }
        catch (Exception ex) {}
        JavaUtils.versionInitialised = true;
    }
    
    public static boolean isGreaterEqualsThan(final String version) {
        boolean greaterEquals = false;
        final StringTokenizer tokeniser = new StringTokenizer(version, ".");
        String token = tokeniser.nextToken();
        try {
            Integer ver = Integer.valueOf(token);
            final int majorVersion = ver;
            token = tokeniser.nextToken();
            ver = Integer.valueOf(token);
            final int minorVersion = ver;
            if (getJREMajorVersion() >= majorVersion && getJREMinorVersion() >= minorVersion) {
                greaterEquals = true;
            }
        }
        catch (Exception ex) {}
        return greaterEquals;
    }
    
    public static boolean isEqualsThan(final String version) {
        boolean equals = false;
        final StringTokenizer tokeniser = new StringTokenizer(version, ".");
        String token = tokeniser.nextToken();
        try {
            Integer ver = Integer.valueOf(token);
            final int majorVersion = ver;
            token = tokeniser.nextToken();
            ver = Integer.valueOf(token);
            final int minorVersion = ver;
            if (getJREMajorVersion() == majorVersion && getJREMinorVersion() == minorVersion) {
                equals = true;
            }
        }
        catch (Exception ex) {}
        return equals;
    }
    
    static {
        JavaUtils.versionInitialised = false;
        JavaUtils.majorVersion = 1;
        JavaUtils.minorVersion = 0;
    }
}
