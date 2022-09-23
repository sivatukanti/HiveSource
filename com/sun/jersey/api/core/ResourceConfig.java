// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.regex.Pattern;
import java.util.LinkedList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashSet;
import com.sun.jersey.api.uri.UriComponent;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.sun.jersey.core.header.LanguageTag;
import java.lang.annotation.Annotation;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Collections;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.jersey.core.util.FeaturesAndProperties;
import javax.ws.rs.core.Application;

public abstract class ResourceConfig extends Application implements FeaturesAndProperties
{
    private static final Logger LOGGER;
    public static final String FEATURE_NORMALIZE_URI = "com.sun.jersey.config.feature.NormalizeURI";
    public static final String FEATURE_CANONICALIZE_URI_PATH = "com.sun.jersey.config.feature.CanonicalizeURIPath";
    public static final String FEATURE_REDIRECT = "com.sun.jersey.config.feature.Redirect";
    public static final String FEATURE_MATCH_MATRIX_PARAMS = "com.sun.jersey.config.feature.IgnoreMatrixParams";
    public static final String FEATURE_IMPLICIT_VIEWABLES = "com.sun.jersey.config.feature.ImplicitViewables";
    public static final String FEATURE_DISABLE_WADL = "com.sun.jersey.config.feature.DisableWADL";
    public static final String FEATURE_TRACE = "com.sun.jersey.config.feature.Trace";
    public static final String FEATURE_TRACE_PER_REQUEST = "com.sun.jersey.config.feature.TracePerRequest";
    public static final String PROPERTY_MEDIA_TYPE_MAPPINGS = "com.sun.jersey.config.property.MediaTypeMappings";
    public static final String PROPERTY_LANGUAGE_MAPPINGS = "com.sun.jersey.config.property.LanguageMappings";
    public static final String PROPERTY_DEFAULT_RESOURCE_COMPONENT_PROVIDER_FACTORY_CLASS = "com.sun.jersey.config.property.DefaultResourceComponentProviderFactoryClass";
    public static final String PROPERTY_CONTAINER_NOTIFIER = "com.sun.jersey.spi.container.ContainerNotifier";
    public static final String PROPERTY_CONTAINER_REQUEST_FILTERS = "com.sun.jersey.spi.container.ContainerRequestFilters";
    public static final String PROPERTY_CONTAINER_RESPONSE_FILTERS = "com.sun.jersey.spi.container.ContainerResponseFilters";
    public static final String PROPERTY_RESOURCE_FILTER_FACTORIES = "com.sun.jersey.spi.container.ResourceFilters";
    public static final String PROPERTY_WADL_GENERATOR_CONFIG = "com.sun.jersey.config.property.WadlGeneratorConfig";
    public static final String COMMON_DELIMITERS = " ,;\n";
    
    @Override
    public abstract Map<String, Boolean> getFeatures();
    
    @Override
    public abstract boolean getFeature(final String p0);
    
    @Override
    public abstract Map<String, Object> getProperties();
    
    @Override
    public abstract Object getProperty(final String p0);
    
    public Map<String, MediaType> getMediaTypeMappings() {
        return Collections.emptyMap();
    }
    
    public Map<String, String> getLanguageMappings() {
        return Collections.emptyMap();
    }
    
    public Map<String, Object> getExplicitRootResources() {
        return Collections.emptyMap();
    }
    
    public void validate() {
        final Iterator<Class<?>> i = this.getClasses().iterator();
        while (i.hasNext()) {
            final Class<?> c = i.next();
            for (final Object o : this.getSingletons()) {
                if (c.isInstance(o)) {
                    i.remove();
                    ResourceConfig.LOGGER.log(Level.WARNING, "Class " + c.getName() + " is ignored as an instance is registered in the set of singletons");
                }
            }
        }
        final Set<Class<?>> objectClassSet = new HashSet<Class<?>>();
        final Set<Class<?>> conflictSet = new HashSet<Class<?>>();
        for (final Object o2 : this.getSingletons()) {
            if (o2.getClass().isAnnotationPresent(Path.class)) {
                if (objectClassSet.contains(o2.getClass())) {
                    conflictSet.add(o2.getClass());
                }
                else {
                    objectClassSet.add(o2.getClass());
                }
            }
        }
        if (!conflictSet.isEmpty()) {
            for (final Class<?> c2 : conflictSet) {
                ResourceConfig.LOGGER.log(Level.SEVERE, "Root resource class " + c2.getName() + " is instantiated more than once in the set of registered singletons");
            }
            throw new IllegalArgumentException("The set of registered singletons contains more than one instance of the same root resource class");
        }
        this.parseAndValidateMappings("com.sun.jersey.config.property.MediaTypeMappings", this.getMediaTypeMappings(), new TypeParser<MediaType>() {
            @Override
            public MediaType valueOf(final String value) {
                return MediaType.valueOf(value);
            }
        });
        this.parseAndValidateMappings("com.sun.jersey.config.property.LanguageMappings", this.getLanguageMappings(), new TypeParser<String>() {
            @Override
            public String valueOf(final String value) {
                return LanguageTag.valueOf(value).toString();
            }
        });
        this.encodeKeys(this.getMediaTypeMappings());
        this.encodeKeys(this.getLanguageMappings());
    }
    
    private <T> void parseAndValidateMappings(final String property, final Map<String, T> mappingsMap, final TypeParser<T> parser) {
        final Object mappings = this.getProperty(property);
        if (mappings == null) {
            return;
        }
        if (mappings instanceof String) {
            this.parseMappings(property, (String)mappings, mappingsMap, parser);
        }
        else {
            if (!(mappings instanceof String[])) {
                throw new IllegalArgumentException("Provided " + property + " mappings is invalid. Acceptable types are String" + " and String[].");
            }
            final String[] mappingsArray = (String[])mappings;
            for (int i = 0; i < mappingsArray.length; ++i) {
                this.parseMappings(property, mappingsArray[i], mappingsMap, parser);
            }
        }
    }
    
    private <T> void parseMappings(final String property, final String mappings, final Map<String, T> mappingsMap, final TypeParser<T> parser) {
        if (mappings == null) {
            return;
        }
        final String[] records = mappings.split(",");
        for (int i = 0; i < records.length; ++i) {
            final String[] record = records[i].split(":");
            if (record.length != 2) {
                throw new IllegalArgumentException("Provided " + property + " mapping \"" + mappings + "\" is invalid. It " + "should contain two parts, key and value, separated by ':'.");
            }
            final String trimmedSegment = record[0].trim();
            final String trimmedValue = record[1].trim();
            if (trimmedSegment.length() == 0) {
                throw new IllegalArgumentException("The key in " + property + " mappings record \"" + records[i] + "\" is empty.");
            }
            if (trimmedValue.length() == 0) {
                throw new IllegalArgumentException("The value in " + property + " mappings record \"" + records[i] + "\" is empty.");
            }
            mappingsMap.put(trimmedSegment, parser.valueOf(trimmedValue));
        }
    }
    
    private <T> void encodeKeys(final Map<String, T> map) {
        final Map<String, T> tempMap = new HashMap<String, T>();
        for (final Map.Entry<String, T> entry : map.entrySet()) {
            tempMap.put(UriComponent.contextualEncode(entry.getKey(), UriComponent.Type.PATH_SEGMENT), entry.getValue());
        }
        map.clear();
        map.putAll((Map<? extends String, ? extends T>)tempMap);
    }
    
    public Set<Class<?>> getRootResourceClasses() {
        final Set<Class<?>> s = new LinkedHashSet<Class<?>>();
        for (final Class<?> c : this.getClasses()) {
            if (isRootResourceClass(c)) {
                s.add(c);
            }
        }
        return s;
    }
    
    public Set<Class<?>> getProviderClasses() {
        final Set<Class<?>> s = new LinkedHashSet<Class<?>>();
        for (final Class<?> c : this.getClasses()) {
            if (!isRootResourceClass(c)) {
                s.add(c);
            }
        }
        return s;
    }
    
    public Set<Object> getRootResourceSingletons() {
        final Set<Object> s = new LinkedHashSet<Object>();
        for (final Object o : this.getSingletons()) {
            if (isRootResourceClass(o.getClass())) {
                s.add(o);
            }
        }
        return s;
    }
    
    public Set<Object> getProviderSingletons() {
        final Set<Object> s = new LinkedHashSet<Object>();
        for (final Object o : this.getSingletons()) {
            if (!isRootResourceClass(o.getClass())) {
                s.add(o);
            }
        }
        return s;
    }
    
    public static boolean isRootResourceClass(final Class<?> c) {
        if (c == null) {
            return false;
        }
        if (c.isAnnotationPresent(Path.class)) {
            return true;
        }
        for (final Class i : c.getInterfaces()) {
            if (i.isAnnotationPresent(Path.class)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isProviderClass(final Class<?> c) {
        return c != null && c.isAnnotationPresent(Provider.class);
    }
    
    public List getContainerRequestFilters() {
        return this.getFilterList("com.sun.jersey.spi.container.ContainerRequestFilters");
    }
    
    public List getContainerResponseFilters() {
        return this.getFilterList("com.sun.jersey.spi.container.ContainerResponseFilters");
    }
    
    public List getResourceFilterFactories() {
        return this.getFilterList("com.sun.jersey.spi.container.ResourceFilters");
    }
    
    private List getFilterList(final String propertyName) {
        final Object o = this.getProperty(propertyName);
        if (o == null) {
            final List l = new ArrayList();
            this.getProperties().put(propertyName, l);
            return l;
        }
        if (o instanceof List) {
            return (List)o;
        }
        final List l = new ArrayList();
        l.add(o);
        this.getProperties().put(propertyName, l);
        return l;
    }
    
    public void setPropertiesAndFeatures(final Map<String, Object> entries) {
        for (final Map.Entry<String, Object> e : entries.entrySet()) {
            if (!this.getProperties().containsKey(e.getKey())) {
                this.getProperties().put(e.getKey(), e.getValue());
            }
            if (!this.getFeatures().containsKey(e.getKey())) {
                final Object v = e.getValue();
                if (v instanceof String) {
                    final String sv = ((String)v).trim();
                    if (sv.equalsIgnoreCase("true")) {
                        this.getFeatures().put(e.getKey(), true);
                    }
                    else {
                        if (!sv.equalsIgnoreCase("false")) {
                            continue;
                        }
                        this.getFeatures().put(e.getKey(), false);
                    }
                }
                else {
                    if (!(v instanceof Boolean)) {
                        continue;
                    }
                    this.getFeatures().put(e.getKey(), (Boolean)v);
                }
            }
        }
    }
    
    public void add(final Application app) {
        if (app.getClasses() != null) {
            this.addAllFirst(this.getClasses(), app.getClasses());
        }
        if (app.getSingletons() != null) {
            this.addAllFirst(this.getSingletons(), app.getSingletons());
        }
        if (app instanceof ResourceConfig) {
            final ResourceConfig rc = (ResourceConfig)app;
            this.getExplicitRootResources().putAll(rc.getExplicitRootResources());
            this.getLanguageMappings().putAll(rc.getLanguageMappings());
            this.getMediaTypeMappings().putAll(rc.getMediaTypeMappings());
            this.getFeatures().putAll(rc.getFeatures());
            this.getProperties().putAll(rc.getProperties());
        }
    }
    
    private <T> void addAllFirst(final Set<T> a, final Set<T> b) {
        final Set<T> x = new LinkedHashSet<T>();
        x.addAll((Collection<? extends T>)b);
        x.addAll((Collection<? extends T>)a);
        a.clear();
        a.addAll((Collection<? extends T>)x);
    }
    
    public ResourceConfig clone() {
        final ResourceConfig that = new DefaultResourceConfig();
        that.getClasses().addAll(this.getClasses());
        that.getSingletons().addAll(this.getSingletons());
        that.getExplicitRootResources().putAll(this.getExplicitRootResources());
        that.getLanguageMappings().putAll(this.getLanguageMappings());
        that.getMediaTypeMappings().putAll(this.getMediaTypeMappings());
        that.getFeatures().putAll(this.getFeatures());
        that.getProperties().putAll(this.getProperties());
        return that;
    }
    
    public static String[] getElements(final String[] elements) {
        return getElements(elements, ";");
    }
    
    public static String[] getElements(final String[] elements, final String delimiters) {
        final List<String> es = new LinkedList<String>();
        for (String element : elements) {
            if (element != null) {
                element = element.trim();
                if (element.length() != 0) {
                    for (final String subElement : getElements(element, delimiters)) {
                        if (subElement != null) {
                            if (subElement.length() != 0) {
                                es.add(subElement);
                            }
                        }
                    }
                }
            }
        }
        return es.toArray(new String[es.size()]);
    }
    
    private static String[] getElements(final String elements, final String delimiters) {
        String regex = "[";
        for (final char c : delimiters.toCharArray()) {
            regex += Pattern.quote(String.valueOf(c));
        }
        regex += "]";
        final String[] es = elements.split(regex);
        for (int i = 0; i < es.length; ++i) {
            es[i] = es[i].trim();
        }
        return es;
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceConfig.class.getName());
    }
    
    private interface TypeParser<T>
    {
        T valueOf(final String p0);
    }
}
