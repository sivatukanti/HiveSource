// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core.servlet;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.spi.scanning.servlet.WebAppResourcesScanner;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.jersey.api.core.ScanningResourceConfig;

public class WebAppResourceConfig extends ScanningResourceConfig
{
    private static final Logger LOGGER;
    
    public WebAppResourceConfig(final Map<String, Object> props, final ServletContext sc) {
        this(getPaths(props), sc);
        this.setPropertiesAndFeatures(props);
    }
    
    public WebAppResourceConfig(final String[] paths, final ServletContext sc) {
        if (paths == null || paths.length == 0) {
            throw new IllegalArgumentException("Array of paths must not be null or empty");
        }
        this.init(paths, sc);
    }
    
    private void init(final String[] paths, final ServletContext sc) {
        if (WebAppResourceConfig.LOGGER.isLoggable(Level.INFO)) {
            final StringBuilder b = new StringBuilder();
            b.append("Scanning for root resource and provider classes in the Web app resource paths:");
            for (final String p : paths) {
                b.append('\n').append("  ").append(p);
            }
            WebAppResourceConfig.LOGGER.log(Level.INFO, b.toString());
        }
        this.init(new WebAppResourcesScanner(paths, sc));
    }
    
    private static String[] getPaths(final Map<String, Object> props) {
        final Object v = props.get("com.sun.jersey.config.property.classpath");
        if (v == null) {
            return new String[] { "/WEB-INF/lib", "/WEB-INF/classes" };
        }
        final String[] paths = getPaths(v);
        if (paths.length == 0) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classpath contains no paths");
        }
        return paths;
    }
    
    private static String[] getPaths(final Object param) {
        if (param instanceof String) {
            return ResourceConfig.getElements(new String[] { (String)param });
        }
        if (param instanceof String[]) {
            return ResourceConfig.getElements((String[])param);
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.classpath must have a property value of type String or String[]");
    }
    
    static {
        LOGGER = Logger.getLogger(WebAppResourceConfig.class.getName());
    }
}
