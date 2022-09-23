// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class PackagesResourceConfig extends ScanningResourceConfig
{
    public static final String PROPERTY_PACKAGES = "com.sun.jersey.config.property.packages";
    private static final Logger LOGGER;
    
    public PackagesResourceConfig(final String... packages) {
        if (packages == null || packages.length == 0) {
            throw new IllegalArgumentException("Array of packages must not be null or empty");
        }
        this.init(packages.clone());
    }
    
    public PackagesResourceConfig(final Map<String, Object> props) {
        this(getPackages(props));
        this.setPropertiesAndFeatures(props);
    }
    
    private void init(final String[] packages) {
        if (PackagesResourceConfig.LOGGER.isLoggable(Level.INFO)) {
            final StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the packages:");
            for (final String p : packages) {
                b.append('\n').append("  ").append(p);
            }
            PackagesResourceConfig.LOGGER.log(Level.INFO, b.toString());
        }
        this.init(new PackageNamesScanner(packages));
    }
    
    private static String[] getPackages(final Map<String, Object> props) {
        final Object v = props.get("com.sun.jersey.config.property.packages");
        if (v == null) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.packages property is missing");
        }
        final String[] packages = getPackages(v);
        if (packages.length == 0) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.packages contains no packages");
        }
        return packages;
    }
    
    private static String[] getPackages(final Object param) {
        if (param instanceof String) {
            return ResourceConfig.getElements(new String[] { (String)param }, " ,;\n");
        }
        if (param instanceof String[]) {
            return ResourceConfig.getElements((String[])param, " ,;\n");
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.packages must have a property value of type String or String[]");
    }
    
    static {
        LOGGER = Logger.getLogger(PackagesResourceConfig.class.getName());
    }
}
