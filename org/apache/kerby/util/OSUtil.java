// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

public final class OSUtil
{
    private static final String OS;
    
    private OSUtil() {
    }
    
    public static boolean isWindows() {
        return OSUtil.OS.contains("win");
    }
    
    public static boolean isMac() {
        return OSUtil.OS.contains("mac");
    }
    
    public static boolean isUnix() {
        return OSUtil.OS.contains("nix") || OSUtil.OS.contains("nux") || OSUtil.OS.contains("aix");
    }
    
    public static boolean isSolaris() {
        return OSUtil.OS.contains("sunos");
    }
    
    public static void main(final String[] args) {
        System.out.println(OSUtil.OS);
        if (isWindows()) {
            System.out.println("This is Windows");
        }
        else if (isMac()) {
            System.out.println("This is Mac");
        }
        else if (isUnix()) {
            System.out.println("This is Unix or Linux");
        }
        else if (isSolaris()) {
            System.out.println("This is Solaris");
        }
        else {
            System.out.println("Your OS is not support!!");
        }
    }
    
    static {
        OS = System.getProperty("os.name").toLowerCase();
    }
}
