// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.io.IOException;

public class OSInfo
{
    public static void main(final String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(getOSName());
                return;
            }
            if ("--arch".equals(args[0])) {
                System.out.print(getArchName());
                return;
            }
        }
        System.out.print(getNativeLibFolderPathForCurrentOS());
    }
    
    public static String getNativeLibFolderPathForCurrentOS() {
        return getOSName() + "/" + getArchName();
    }
    
    public static String getOSName() {
        return translateOSNameToFolderName(System.getProperty("os.name"));
    }
    
    public static String getArchName() {
        final String osArch = System.getProperty("os.arch");
        if (osArch.startsWith("arm") && System.getProperty("os.name").contains("Linux")) {
            final String javaHome = System.getProperty("java.home");
            try {
                final String[] cmdarray = { "/bin/sh", "-c", "find '" + javaHome + "' -name 'libjvm.so' | head -1 | xargs readelf -A | " + "grep 'Tag_ABI_VFP_args: VFP registers'" };
                final int exitCode = Runtime.getRuntime().exec(cmdarray).waitFor();
                if (exitCode == 0) {
                    return "armhf";
                }
            }
            catch (IOException e) {}
            catch (InterruptedException ex) {}
        }
        else if (getOSName().equals("Mac") && (osArch.equals("universal") || osArch.equals("amd64"))) {
            return "x86_64";
        }
        return translateArchNameToFolderName(osArch);
    }
    
    static String translateOSNameToFolderName(final String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        }
        if (osName.contains("Mac")) {
            return "Mac";
        }
        if (osName.contains("Linux")) {
            return "Linux";
        }
        return osName.replaceAll("\\W", "");
    }
    
    static String translateArchNameToFolderName(final String archName) {
        return archName.replaceAll("\\W", "");
    }
}
