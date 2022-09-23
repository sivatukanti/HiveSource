// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.version.Info;

public class Version implements Info
{
    @Deprecated
    public static int getRevision() {
        return -1;
    }
    
    public static String getRevisionHash() {
        return "2d71af4dbe22557fda74f9a9b4309b15a7487f03";
    }
    
    public static String getBuildDate() {
        return "06/29/2018 00:39 GMT";
    }
    
    public static String getVersion() {
        return "3.4.13" + ((Version.QUALIFIER == null) ? "" : ("-" + Version.QUALIFIER));
    }
    
    public static String getVersionRevision() {
        return getVersion() + "-" + getRevisionHash();
    }
    
    public static String getFullVersion() {
        return getVersionRevision() + ", built on " + getBuildDate();
    }
    
    public static void printUsage() {
        System.out.print("Usage:\tjava -cp ... org.apache.zookeeper.Version [--full | --short | --revision],\n\tPrints --full version info if no arg specified.");
        System.exit(1);
    }
    
    public static void main(final String[] args) {
        if (args.length > 1) {
            printUsage();
        }
        if (args.length == 0 || (args.length == 1 && args[0].equals("--full"))) {
            System.out.println(getFullVersion());
            System.exit(0);
        }
        if (args[0].equals("--short")) {
            System.out.println(getVersion());
        }
        else if (args[0].equals("--revision")) {
            System.out.println(getVersionRevision());
        }
        else {
            printUsage();
        }
        System.exit(0);
    }
}
