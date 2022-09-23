// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.tools;

import java.io.PrintWriter;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import org.apache.derby.impl.tools.sysinfo.Main;

public class sysinfo
{
    public static final String DBMS = "DBMS";
    public static final String TOOLS = "tools";
    public static final String NET = "net";
    public static final String CLIENT = "dnc";
    
    public static void main(final String[] array) {
        Main.main(array);
    }
    
    private sysinfo() {
    }
    
    public static int getMajorVersion() {
        return getMajorVersion("DBMS");
    }
    
    public static int getMajorVersion(final String s) {
        final ProductVersionHolder productVersionHolderFromMyEnv = ProductVersionHolder.getProductVersionHolderFromMyEnv(s);
        if (productVersionHolderFromMyEnv == null) {
            return -1;
        }
        return productVersionHolderFromMyEnv.getMajorVersion();
    }
    
    public static int getMinorVersion() {
        return getMinorVersion("DBMS");
    }
    
    public static int getMinorVersion(final String s) {
        final ProductVersionHolder productVersionHolderFromMyEnv = ProductVersionHolder.getProductVersionHolderFromMyEnv(s);
        if (productVersionHolderFromMyEnv == null) {
            return -1;
        }
        return productVersionHolderFromMyEnv.getMinorVersion();
    }
    
    public static String getBuildNumber() {
        return getBuildNumber("DBMS");
    }
    
    public static String getBuildNumber(final String s) {
        final ProductVersionHolder productVersionHolderFromMyEnv = ProductVersionHolder.getProductVersionHolderFromMyEnv(s);
        if (productVersionHolderFromMyEnv == null) {
            return "????";
        }
        return productVersionHolderFromMyEnv.getBuildNumber();
    }
    
    public static String getProductName() {
        return getProductName("DBMS");
    }
    
    public static String getProductName(final String s) {
        final ProductVersionHolder productVersionHolderFromMyEnv = ProductVersionHolder.getProductVersionHolderFromMyEnv(s);
        if (productVersionHolderFromMyEnv == null) {
            return Main.getTextMessage("SIF01.K");
        }
        return productVersionHolderFromMyEnv.getProductName();
    }
    
    public static String getVersionString() {
        return getVersionString("DBMS");
    }
    
    public static String getVersionString(final String s) {
        final ProductVersionHolder productVersionHolderFromMyEnv = ProductVersionHolder.getProductVersionHolderFromMyEnv(s);
        if (productVersionHolderFromMyEnv == null) {
            return Main.getTextMessage("SIF01.K");
        }
        return productVersionHolderFromMyEnv.getVersionBuildString(false);
    }
    
    public static void getInfo(final PrintWriter printWriter) {
        Main.getMainInfo(printWriter, false);
    }
}
