// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.HashMap;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import javax.servlet.Servlet;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.api.core.DefaultResourceConfig;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.servlet.ServletRegistration;
import java.util.Collections;
import javax.servlet.ServletContext;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Path;
import javax.servlet.annotation.HandlesTypes;
import javax.servlet.ServletContainerInitializer;

@HandlesTypes({ Path.class, Provider.class, Application.class, ApplicationPath.class })
public class JerseyServletContainerInitializer implements ServletContainerInitializer
{
    private static final Logger LOGGER;
    
    @Override
    public void onStartup(Set<Class<?>> classes, final ServletContext sc) {
        if (classes == null) {
            classes = Collections.emptySet();
        }
        final int nOfRegisterations = sc.getServletRegistrations().size();
        for (final Class<? extends Application> a : this.getApplicationClasses(classes)) {
            final ServletRegistration appReg = sc.getServletRegistration(a.getName());
            if (appReg != null) {
                this.addServletWithExistingRegistration(sc, appReg, a, classes);
            }
            else {
                final List<ServletRegistration> srs = this.getInitParamDeclaredRegistrations(sc, a);
                if (!srs.isEmpty()) {
                    for (final ServletRegistration sr : srs) {
                        this.addServletWithExistingRegistration(sc, sr, a, classes);
                    }
                }
                else {
                    this.addServletWithApplication(sc, a, classes);
                }
            }
        }
        if (nOfRegisterations == sc.getServletRegistrations().size()) {
            this.addServletWithDefaultConfiguration(sc, classes);
        }
    }
    
    private List<ServletRegistration> getInitParamDeclaredRegistrations(final ServletContext sc, final Class<? extends Application> a) {
        final List<ServletRegistration> srs = new ArrayList<ServletRegistration>(1);
        for (final ServletRegistration sr : sc.getServletRegistrations().values()) {
            final Map<String, String> ips = sr.getInitParameters();
            if (ips.containsKey("javax.ws.rs.Application")) {
                if (!ips.get("javax.ws.rs.Application").equals(a.getName()) || sr.getClassName() != null) {
                    continue;
                }
                srs.add(sr);
            }
            else {
                if (!ips.containsKey("com.sun.jersey.config.property.resourceConfigClass") || !ips.get("com.sun.jersey.config.property.resourceConfigClass").equals(a.getName()) || sr.getClassName() != null) {
                    continue;
                }
                srs.add(sr);
            }
        }
        return srs;
    }
    
    private void addServletWithDefaultConfiguration(final ServletContext sc, final Set<Class<?>> classes) {
        ServletRegistration appReg = sc.getServletRegistration(Application.class.getName());
        if (appReg != null && appReg.getClassName() == null) {
            final Set<Class<?>> x = this.getRootResourceAndProviderClasses(classes);
            final ServletContainer s = new ServletContainer(new DefaultResourceConfig(x));
            appReg = sc.addServlet(appReg.getName(), s);
            if (appReg.getMappings().isEmpty()) {
                JerseyServletContainerInitializer.LOGGER.severe("The Jersey servlet application, named " + appReg.getName() + ", has no servlet mapping");
            }
            else {
                JerseyServletContainerInitializer.LOGGER.info("Registering the Jersey servlet application, named " + appReg.getName() + ", with the following root resource and provider classes: " + x);
            }
        }
    }
    
    private void addServletWithApplication(final ServletContext sc, final Class<? extends Application> a, final Set<Class<?>> classes) {
        final ApplicationPath ap = a.getAnnotation(ApplicationPath.class);
        if (ap != null) {
            final ServletContainer s = new ServletContainer(new DeferredResourceConfig(a, this.getRootResourceAndProviderClasses(classes)));
            final String mapping = this.createMappingPath(ap);
            if (!this.mappingExists(sc, mapping)) {
                sc.addServlet(a.getName(), s).addMapping(mapping);
                JerseyServletContainerInitializer.LOGGER.info("Registering the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ", with the Application class of the same name");
            }
            else {
                JerseyServletContainerInitializer.LOGGER.severe("Mapping conflict. A Servlet declaration exists with same mapping as the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ". The Jersey servlet is not deployed.");
            }
        }
    }
    
    private void addServletWithExistingRegistration(final ServletContext sc, ServletRegistration sr, final Class<? extends Application> a, final Set<Class<?>> classes) {
        if (sr.getClassName() == null) {
            final ResourceConfig rc = new DeferredResourceConfig(a, this.getRootResourceAndProviderClasses(classes));
            final Map<String, Object> initParams = new HashMap<String, Object>();
            for (final Map.Entry<String, String> entry : sr.getInitParameters().entrySet()) {
                initParams.put(entry.getKey(), entry.getValue());
            }
            rc.setPropertiesAndFeatures(initParams);
            final ServletContainer s = new ServletContainer(rc);
            sr = sc.addServlet(a.getName(), s);
            if (sr.getMappings().isEmpty()) {
                final ApplicationPath ap = a.getAnnotation(ApplicationPath.class);
                if (ap != null) {
                    final String mapping = this.createMappingPath(ap);
                    if (!this.mappingExists(sc, mapping)) {
                        sr.addMapping(mapping);
                        JerseyServletContainerInitializer.LOGGER.info("Registering the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ", with the Application class of the same name");
                    }
                    else {
                        JerseyServletContainerInitializer.LOGGER.severe("Mapping conflict. A Servlet registration exists with same mapping as the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ". The Jersey servlet is not deployed.");
                    }
                }
                else {
                    JerseyServletContainerInitializer.LOGGER.severe("The Jersey servlet application, named " + a.getName() + ", is not annotated with " + ApplicationPath.class.getSimpleName() + " " + "and has no servlet mapping");
                }
            }
            else {
                JerseyServletContainerInitializer.LOGGER.info("Registering the Jersey servlet application, named " + a.getName() + ", with the Application class of the same name");
            }
        }
    }
    
    private boolean mappingExists(final ServletContext sc, final String mapping) {
        for (final ServletRegistration sr : sc.getServletRegistrations().values()) {
            for (final String declaredMapping : sr.getMappings()) {
                if (mapping.equals(declaredMapping)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private String createMappingPath(final ApplicationPath ap) {
        String path = ap.value();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/*")) {
            if (path.endsWith("/")) {
                path += "*";
            }
            else {
                path += "/*";
            }
        }
        return path;
    }
    
    private Set<Class<? extends Application>> getApplicationClasses(final Set<Class<?>> classes) {
        final Set<Class<? extends Application>> s = new LinkedHashSet<Class<? extends Application>>();
        for (final Class<?> c : classes) {
            if (Application.class != c && Application.class.isAssignableFrom(c)) {
                s.add(c.asSubclass(Application.class));
            }
        }
        return s;
    }
    
    private Set<Class<?>> getRootResourceAndProviderClasses(final Set<Class<?>> classes) {
        final Set<Class<?>> s = new LinkedHashSet<Class<?>>();
        for (final Class<?> c : classes) {
            if (c.isAnnotationPresent(Path.class) || c.isAnnotationPresent(Provider.class)) {
                s.add(c);
            }
        }
        return s;
    }
    
    static {
        LOGGER = Logger.getLogger(JerseyServletContainerInitializer.class.getName());
    }
}
