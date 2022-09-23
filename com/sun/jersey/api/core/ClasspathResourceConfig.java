// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.FilesScanner;
import java.util.logging.Level;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public class ClasspathResourceConfig extends ScanningResourceConfig
{
    public static final String PROPERTY_CLASSPATH = "com.sun.jersey.config.property.classpath";
    private static final Logger LOGGER;
    
    public ClasspathResourceConfig() {
        this(getPaths());
    }
    
    public ClasspathResourceConfig(final Map<String, Object> props) {
        this(getPaths(props));
        this.setPropertiesAndFeatures(props);
    }
    
    public ClasspathResourceConfig(final String[] paths) {
        if (paths == null || paths.length == 0) {
            throw new IllegalArgumentException("Array of paths must not be null or empty");
        }
        this.init(paths.clone());
    }
    
    private void init(final String[] paths) {
        final File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; ++i) {
            files[i] = new File(paths[i]);
        }
        if (ClasspathResourceConfig.LOGGER.isLoggable(Level.INFO)) {
            final StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the paths:");
            for (final String p : paths) {
                b.append('\n').append("  ").append(p);
            }
            ClasspathResourceConfig.LOGGER.log(Level.INFO, b.toString());
        }
        this.init(new FilesScanner(files));
    }
    
    private static String[] getPaths() {
        final String classPath = System.getProperty("java.class.path");
        return classPath.split(File.pathSeparator);
    }
    
    private static String[] getPaths(final Map<String, Object> props) {
        final Object v = props.get("com.sun.jersey.config.property.classpath");
        if (v == null) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classpath property is missing");
        }
        final String[] paths = getPaths(v);
        if (paths.length == 0) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classpath contains no paths");
        }
        return paths;
    }
    
    private static String[] getPaths(final Object param) {
        if (param instanceof String) {
            return ResourceConfig.getElements(new String[] { (String)param }, " ,;\n");
        }
        if (param instanceof String[]) {
            return ResourceConfig.getElements((String[])param, " ,;\n");
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.classpath must have a property value of type String or String[]");
    }
    
    static {
        LOGGER = Logger.getLogger(ClasspathResourceConfig.class.getName());
    }
}
