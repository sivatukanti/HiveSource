// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.Iterator;
import com.sun.jersey.spi.scanning.AnnotationScannerListener;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import javax.ws.rs.Path;
import java.util.logging.Level;
import java.util.Collection;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.spi.scanning.PathProviderScannerListener;
import java.util.HashSet;
import java.util.Set;
import com.sun.jersey.core.spi.scanning.Scanner;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.ReloadListener;

public class ScanningResourceConfig extends DefaultResourceConfig implements ReloadListener
{
    private static final Logger LOGGER;
    private Scanner scanner;
    private final Set<Class<?>> cachedClasses;
    
    public ScanningResourceConfig() {
        this.cachedClasses = new HashSet<Class<?>>();
    }
    
    public void init(final Scanner scanner) {
        this.scanner = scanner;
        final AnnotationScannerListener asl = new PathProviderScannerListener();
        scanner.scan(asl);
        this.getClasses().addAll(asl.getAnnotatedClasses());
        if (ScanningResourceConfig.LOGGER.isLoggable(Level.INFO) && !this.getClasses().isEmpty()) {
            final Set<Class> rootResourceClasses = this.get(Path.class);
            if (rootResourceClasses.isEmpty()) {
                ScanningResourceConfig.LOGGER.log(Level.INFO, "No root resource classes found.");
            }
            else {
                this.logClasses("Root resource classes found:", rootResourceClasses);
            }
            final Set<Class> providerClasses = this.get(Provider.class);
            if (providerClasses.isEmpty()) {
                ScanningResourceConfig.LOGGER.log(Level.INFO, "No provider classes found.");
            }
            else {
                this.logClasses("Provider classes found:", providerClasses);
            }
        }
        this.cachedClasses.clear();
        this.cachedClasses.addAll(this.getClasses());
    }
    
    @Deprecated
    public void reload() {
        this.onReload();
    }
    
    @Override
    public void onReload() {
        final Set<Class<?>> classesToRemove = new HashSet<Class<?>>();
        final Set<Class<?>> classesToAdd = new HashSet<Class<?>>();
        for (final Class c : this.getClasses()) {
            if (!this.cachedClasses.contains(c)) {
                classesToAdd.add(c);
            }
        }
        for (final Class c : this.cachedClasses) {
            if (!this.getClasses().contains(c)) {
                classesToRemove.add(c);
            }
        }
        this.getClasses().clear();
        this.init(this.scanner);
        this.getClasses().addAll(classesToAdd);
        this.getClasses().removeAll(classesToRemove);
    }
    
    private Set<Class> get(final Class<? extends Annotation> ac) {
        final Set<Class> s = new HashSet<Class>();
        for (final Class c : this.getClasses()) {
            if (c.isAnnotationPresent(ac)) {
                s.add(c);
            }
        }
        return s;
    }
    
    private void logClasses(final String s, final Set<Class> classes) {
        final StringBuilder b = new StringBuilder();
        b.append(s);
        for (final Class c : classes) {
            b.append('\n').append("  ").append(c);
        }
        ScanningResourceConfig.LOGGER.log(Level.INFO, b.toString());
    }
    
    static {
        LOGGER = Logger.getLogger(ScanningResourceConfig.class.getName());
    }
}
