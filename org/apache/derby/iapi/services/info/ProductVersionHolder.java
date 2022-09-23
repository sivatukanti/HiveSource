// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.info;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.util.Properties;
import java.security.PrivilegedAction;

public final class ProductVersionHolder implements PrivilegedAction
{
    private static final int BAD_NUMBER = -1;
    private static final String ALPHA = "alpha";
    private static final String BETA = "beta";
    public static final int MAINT_ENCODING = 1000000;
    private String productVendorName;
    private String productName;
    private String productTechnologyName;
    private int majorVersion;
    private int minorVersion;
    private int maintVersion;
    private int drdaMaintVersion;
    private String buildNumber;
    private Boolean isBeta;
    private String productGenus;
    
    private ProductVersionHolder() {
        this.majorVersion = -1;
        this.minorVersion = -1;
        this.maintVersion = -1;
        this.drdaMaintVersion = -1;
        this.buildNumber = "????";
    }
    
    private ProductVersionHolder(final String s, final String s2, final String s3, final int majorVersion, final int minorVersion, final int maintVersion, final int drdaMaintVersion, final String buildNumber, final Boolean isBeta) {
        this.majorVersion = -1;
        this.minorVersion = -1;
        this.maintVersion = -1;
        this.drdaMaintVersion = -1;
        this.buildNumber = "????";
        if (s != null) {
            this.productVendorName = s.trim();
        }
        if (s2 != null) {
            this.productName = s2.trim();
        }
        if (s3 != null) {
            this.productTechnologyName = s3.trim();
        }
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.maintVersion = maintVersion;
        this.drdaMaintVersion = drdaMaintVersion;
        this.buildNumber = buildNumber;
        this.isBeta = isBeta;
    }
    
    public static ProductVersionHolder getProductVersionHolder(final String s, final String s2, final String s3, final int n, final int n2, final int n3, final int n4, final String s4, final Boolean b) {
        return new ProductVersionHolder(s, s2, s3, n, n2, n3, n4, s4, b);
    }
    
    public static ProductVersionHolder getProductVersionHolderFromMyEnv(final String productGenus) {
        final ProductVersionHolder action = new ProductVersionHolder();
        action.productGenus = productGenus;
        final Properties properties = AccessController.doPrivileged((PrivilegedAction<Properties>)action);
        if (properties == null) {
            return null;
        }
        return getProductVersionHolder(properties);
    }
    
    public static ProductVersionHolder getProductVersionHolderFromMyEnv(final InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        final Properties properties = new Properties();
        try {
            properties.load(inStream);
        }
        catch (IOException ex) {
            System.out.println("IOE " + ex.getMessage());
            return null;
        }
        finally {
            try {
                inStream.close();
            }
            catch (IOException ex2) {}
        }
        return getProductVersionHolder(properties);
    }
    
    public static ProductVersionHolder getProductVersionHolder(final Properties properties) {
        return getProductVersionHolder(properties.getProperty("derby.product.vendor"), properties.getProperty("derby.product.external.name"), properties.getProperty("derby.product.technology.name"), parseInt(properties.getProperty("derby.version.major")), parseInt(properties.getProperty("derby.version.minor")), parseInt(properties.getProperty("derby.version.maint")), parseInt(properties.getProperty("derby.version.drdamaint")), properties.getProperty("derby.build.number"), Boolean.valueOf(properties.getProperty("derby.version.beta")));
    }
    
    public String getProductVendorName() {
        return this.productVendorName;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public String getProductTechnologyName() {
        return this.productTechnologyName;
    }
    
    public int getMajorVersion() {
        return this.majorVersion;
    }
    
    public int getMinorVersion() {
        return this.minorVersion;
    }
    
    public int getMaintVersion() {
        return this.maintVersion;
    }
    
    public int getDrdaMaintVersion() {
        return this.drdaMaintVersion;
    }
    
    public int getFixPackVersion() {
        return this.maintVersion / 1000000;
    }
    
    public boolean isBeta() {
        return this.isBeta;
    }
    
    public boolean isAlpha() {
        return this.majorVersion >= 5 && this.minorVersion > 2 && this.maintVersion / 1000000 == 0;
    }
    
    public String getBuildNumber() {
        return this.buildNumber;
    }
    
    public int getBuildNumberAsInt() {
        if (this.buildNumber == null) {
            return -1;
        }
        boolean b = false;
        int endIndex = this.buildNumber.indexOf(77);
        if (endIndex == -1) {
            endIndex = this.buildNumber.indexOf(58);
        }
        else {
            b = true;
        }
        if (endIndex == -1) {
            endIndex = this.buildNumber.length();
        }
        else {
            b = true;
        }
        try {
            int int1 = Integer.parseInt(this.buildNumber.substring(0, endIndex));
            if (b) {
                int1 = -int1;
            }
            return int1;
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }
    
    private static int parseInt(final String s) {
        int int1 = -1;
        try {
            if (s != null) {
                int1 = Integer.parseInt(s);
            }
        }
        catch (NumberFormatException ex) {}
        if (int1 < 0) {
            int1 = -1;
        }
        return int1;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getProductVendorName());
        sb.append(" - ");
        sb.append(this.getProductName());
        sb.append(" - ");
        sb.append(this.getVersionBuildString(true));
        return sb.toString();
    }
    
    public String getSimpleVersionString() {
        return simpleVersionString(this.majorVersion, this.minorVersion, this.isBeta());
    }
    
    public static String simpleVersionString(final int i, final int j, final boolean b) {
        final StringBuffer sb = new StringBuffer();
        sb.append(i);
        sb.append('.');
        sb.append(j);
        if (b) {
            sb.append(' ');
            sb.append("beta");
        }
        return sb.toString();
    }
    
    public static String fullVersionString(final int i, final int j, final int k, final boolean b, final String str) {
        final StringBuffer sb = new StringBuffer();
        sb.append(i);
        sb.append('.');
        sb.append(j);
        sb.append('.');
        String str2 = null;
        if (i == 5 && j <= 2 && k < 1000000) {
            sb.append(k);
            if (b) {
                str2 = "beta";
            }
        }
        else {
            final int l = k / 1000000;
            final int m = k % 1000000;
            sb.append(l);
            sb.append('.');
            sb.append(m);
            if (l == 0) {
                str2 = "alpha";
            }
            else if (b) {
                str2 = "beta";
            }
        }
        if (str2 != null) {
            sb.append(' ');
            sb.append(str2);
        }
        if (str != null) {
            sb.append(" - (");
            sb.append(str);
            sb.append(')');
        }
        return sb.toString();
    }
    
    public String getVersionBuildString(final boolean b) {
        return fullVersionString(this.majorVersion, this.minorVersion, this.maintVersion, this.isBeta(), b ? this.buildNumber : null);
    }
    
    public final Object run() {
        return this.loadProperties(this.productGenus);
    }
    
    private Properties loadProperties(final String str) {
        final InputStream resourceAsStream = this.getClass().getResourceAsStream("/org/apache/derby/info/" + str + ".properties");
        if (resourceAsStream == null) {
            return null;
        }
        final Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
            return properties;
        }
        catch (IOException ex) {
            return null;
        }
    }
}
