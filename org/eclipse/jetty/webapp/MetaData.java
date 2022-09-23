// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Collection;
import org.eclipse.jetty.util.resource.EmptyResource;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public class MetaData
{
    private static final Logger LOG;
    public static final String ORDERED_LIBS = "javax.servlet.context.orderedLibs";
    public static final Resource NON_FRAG_RESOURCE;
    protected Map<String, OriginInfo> _origins;
    protected WebDescriptor _webDefaultsRoot;
    protected WebDescriptor _webXmlRoot;
    protected final List<WebDescriptor> _webOverrideRoots;
    protected boolean _metaDataComplete;
    protected final List<DescriptorProcessor> _descriptorProcessors;
    protected final List<FragmentDescriptor> _webFragmentRoots;
    protected final Map<String, FragmentDescriptor> _webFragmentNameMap;
    protected final Map<Resource, FragmentDescriptor> _webFragmentResourceMap;
    protected final Map<Resource, List<DiscoveredAnnotation>> _annotations;
    protected final List<Resource> _webInfClasses;
    protected final List<Resource> _webInfJars;
    protected final List<Resource> _orderedContainerResources;
    protected final List<Resource> _orderedWebInfResources;
    protected Ordering _ordering;
    protected boolean allowDuplicateFragmentNames;
    
    public MetaData() {
        this._origins = new HashMap<String, OriginInfo>();
        this._webOverrideRoots = new ArrayList<WebDescriptor>();
        this._descriptorProcessors = new ArrayList<DescriptorProcessor>();
        this._webFragmentRoots = new ArrayList<FragmentDescriptor>();
        this._webFragmentNameMap = new HashMap<String, FragmentDescriptor>();
        this._webFragmentResourceMap = new HashMap<Resource, FragmentDescriptor>();
        this._annotations = new HashMap<Resource, List<DiscoveredAnnotation>>();
        this._webInfClasses = new ArrayList<Resource>();
        this._webInfJars = new ArrayList<Resource>();
        this._orderedContainerResources = new ArrayList<Resource>();
        this._orderedWebInfResources = new ArrayList<Resource>();
        this.allowDuplicateFragmentNames = false;
    }
    
    public void clear() {
        this._webDefaultsRoot = null;
        this._origins.clear();
        this._webXmlRoot = null;
        this._webOverrideRoots.clear();
        this._metaDataComplete = false;
        this._annotations.clear();
        this._descriptorProcessors.clear();
        this._webFragmentRoots.clear();
        this._webFragmentNameMap.clear();
        this._webFragmentResourceMap.clear();
        this._annotations.clear();
        this._webInfJars.clear();
        this._orderedWebInfResources.clear();
        this._orderedContainerResources.clear();
        this._ordering = null;
        this.allowDuplicateFragmentNames = false;
    }
    
    public void setDefaults(final Resource webDefaults) throws Exception {
        (this._webDefaultsRoot = new DefaultsDescriptor(webDefaults)).parse();
        if (this._webDefaultsRoot.isOrdered()) {
            Ordering ordering = this.getOrdering();
            if (ordering == null) {
                ordering = new Ordering.AbsoluteOrdering(this);
            }
            final List<String> order = this._webDefaultsRoot.getOrdering();
            for (final String s : order) {
                if (s.equalsIgnoreCase("others")) {
                    ((Ordering.AbsoluteOrdering)ordering).addOthers();
                }
                else {
                    ((Ordering.AbsoluteOrdering)ordering).add(s);
                }
            }
            this.setOrdering(ordering);
        }
    }
    
    public void setWebXml(final Resource webXml) throws Exception {
        (this._webXmlRoot = new WebDescriptor(webXml)).parse();
        this._metaDataComplete = (this._webXmlRoot.getMetaDataComplete() == MetaDataComplete.True);
        if (this._webXmlRoot.isOrdered()) {
            Ordering ordering = this.getOrdering();
            if (ordering == null) {
                ordering = new Ordering.AbsoluteOrdering(this);
            }
            final List<String> order = this._webXmlRoot.getOrdering();
            for (final String s : order) {
                if (s.equalsIgnoreCase("others")) {
                    ((Ordering.AbsoluteOrdering)ordering).addOthers();
                }
                else {
                    ((Ordering.AbsoluteOrdering)ordering).add(s);
                }
            }
            this.setOrdering(ordering);
        }
    }
    
    public void addOverride(final Resource override) throws Exception {
        final OverrideDescriptor webOverrideRoot = new OverrideDescriptor(override);
        webOverrideRoot.setValidating(false);
        webOverrideRoot.parse();
        switch (webOverrideRoot.getMetaDataComplete()) {
            case True: {
                this._metaDataComplete = true;
                break;
            }
            case False: {
                this._metaDataComplete = false;
                break;
            }
        }
        if (webOverrideRoot.isOrdered()) {
            Ordering ordering = this.getOrdering();
            if (ordering == null) {
                ordering = new Ordering.AbsoluteOrdering(this);
            }
            final List<String> order = webOverrideRoot.getOrdering();
            for (final String s : order) {
                if (s.equalsIgnoreCase("others")) {
                    ((Ordering.AbsoluteOrdering)ordering).addOthers();
                }
                else {
                    ((Ordering.AbsoluteOrdering)ordering).add(s);
                }
            }
            this.setOrdering(ordering);
        }
        this._webOverrideRoots.add(webOverrideRoot);
    }
    
    public void addFragment(final Resource jarResource, final Resource xmlResource) throws Exception {
        if (this._metaDataComplete) {
            return;
        }
        final FragmentDescriptor descriptor = new FragmentDescriptor(xmlResource);
        this._webFragmentResourceMap.put(jarResource, descriptor);
        this._webFragmentRoots.add(descriptor);
        descriptor.parse();
        if (descriptor.getName() != null) {
            final Descriptor existing = this._webFragmentNameMap.get(descriptor.getName());
            if (existing != null && !this.isAllowDuplicateFragmentNames()) {
                throw new IllegalStateException("Duplicate fragment name: " + descriptor.getName() + " for " + existing.getResource() + " and " + descriptor.getResource());
            }
            this._webFragmentNameMap.put(descriptor.getName(), descriptor);
        }
        if (this._ordering == null && descriptor.isOrdered()) {
            this.setOrdering(new Ordering.RelativeOrdering(this));
            return;
        }
        this.orderFragments();
    }
    
    public void addDiscoveredAnnotations(final List<DiscoveredAnnotation> annotations) {
        if (annotations == null) {
            return;
        }
        for (final DiscoveredAnnotation a : annotations) {
            this.addDiscoveredAnnotation(a);
        }
    }
    
    public synchronized void addDiscoveredAnnotation(final DiscoveredAnnotation annotation) {
        if (annotation == null) {
            return;
        }
        Resource resource = annotation.getResource();
        if (resource == null || !this._webInfJars.contains(resource)) {
            resource = EmptyResource.INSTANCE;
        }
        List<DiscoveredAnnotation> list = this._annotations.get(resource);
        if (list == null) {
            list = new ArrayList<DiscoveredAnnotation>();
            this._annotations.put(resource, list);
        }
        list.add(annotation);
    }
    
    public void addDescriptorProcessor(final DescriptorProcessor p) {
        this._descriptorProcessors.add(p);
    }
    
    public void removeDescriptorProcessor(final DescriptorProcessor p) {
        this._descriptorProcessors.remove(p);
    }
    
    public void orderFragments() {
        this._orderedWebInfResources.clear();
        if (this.getOrdering() != null) {
            this._orderedWebInfResources.addAll(this.getOrdering().order(this._webInfJars));
        }
    }
    
    public void resolve(final WebAppContext context) throws Exception {
        MetaData.LOG.debug("metadata resolve {}", context);
        this._origins.clear();
        List<Resource> orderedWebInfJars = null;
        if (this.getOrdering() != null) {
            orderedWebInfJars = this.getOrderedWebInfJars();
            final List<String> orderedLibs = new ArrayList<String>();
            for (final Resource webInfJar : orderedWebInfJars) {
                final String fullname = webInfJar.getName();
                final int i = fullname.indexOf(".jar");
                final int j = fullname.lastIndexOf("/", i);
                orderedLibs.add(fullname.substring(j + 1, i + 4));
            }
            context.setAttribute("javax.servlet.context.orderedLibs", orderedLibs);
        }
        if (this._webXmlRoot != null) {
            context.getServletContext().setEffectiveMajorVersion(this._webXmlRoot.getMajorVersion());
            context.getServletContext().setEffectiveMinorVersion(this._webXmlRoot.getMinorVersion());
        }
        for (final DescriptorProcessor p : this._descriptorProcessors) {
            p.process(context, this.getWebDefault());
            p.process(context, this.getWebXml());
            for (final WebDescriptor wd : this.getOverrideWebs()) {
                MetaData.LOG.debug("process {} {}", context, wd);
                p.process(context, wd);
            }
        }
        final List<DiscoveredAnnotation> nonFragAnnotations = this._annotations.get(MetaData.NON_FRAG_RESOURCE);
        if (nonFragAnnotations != null) {
            for (final DiscoveredAnnotation a : nonFragAnnotations) {
                MetaData.LOG.debug("apply {}", a);
                a.apply();
            }
        }
        List<Resource> resources = null;
        if (this.getOrdering() != null) {
            resources = orderedWebInfJars;
        }
        else {
            resources = this.getWebInfJars();
        }
        for (final Resource r : resources) {
            final FragmentDescriptor fd = this._webFragmentResourceMap.get(r);
            if (fd != null) {
                for (final DescriptorProcessor p2 : this._descriptorProcessors) {
                    MetaData.LOG.debug("process {} {}", context, fd);
                    p2.process(context, fd);
                }
            }
            final List<DiscoveredAnnotation> fragAnnotations = this._annotations.get(r);
            if (fragAnnotations != null) {
                for (final DiscoveredAnnotation a2 : fragAnnotations) {
                    MetaData.LOG.debug("apply {}", a2);
                    a2.apply();
                }
            }
        }
    }
    
    public boolean isDistributable() {
        boolean distributable = (this._webDefaultsRoot != null && this._webDefaultsRoot.isDistributable()) || (this._webXmlRoot != null && this._webXmlRoot.isDistributable());
        for (final WebDescriptor d : this._webOverrideRoots) {
            distributable &= d.isDistributable();
        }
        if (this.getOrdering() != null) {
            final List<Resource> orderedResources = this.getOrderedWebInfJars();
            for (final Resource r : orderedResources) {
                final FragmentDescriptor d2 = this._webFragmentResourceMap.get(r);
                if (d2 != null) {
                    distributable = (distributable && d2.isDistributable());
                }
            }
        }
        return distributable;
    }
    
    public WebDescriptor getWebXml() {
        return this._webXmlRoot;
    }
    
    public List<WebDescriptor> getOverrideWebs() {
        return this._webOverrideRoots;
    }
    
    public WebDescriptor getWebDefault() {
        return this._webDefaultsRoot;
    }
    
    public List<FragmentDescriptor> getFragments() {
        return this._webFragmentRoots;
    }
    
    public List<Resource> getOrderedWebInfJars() {
        return this._orderedWebInfResources;
    }
    
    public List<FragmentDescriptor> getOrderedFragments() {
        final List<FragmentDescriptor> list = new ArrayList<FragmentDescriptor>();
        if (this.getOrdering() == null) {
            return list;
        }
        for (final Resource r : this.getOrderedWebInfJars()) {
            final FragmentDescriptor fd = this._webFragmentResourceMap.get(r);
            if (fd != null) {
                list.add(fd);
            }
        }
        return list;
    }
    
    public Ordering getOrdering() {
        return this._ordering;
    }
    
    public void setOrdering(final Ordering o) {
        this._ordering = o;
        this.orderFragments();
    }
    
    public FragmentDescriptor getFragment(final Resource jar) {
        return this._webFragmentResourceMap.get(jar);
    }
    
    public FragmentDescriptor getFragment(final String name) {
        return this._webFragmentNameMap.get(name);
    }
    
    public Resource getJarForFragment(final String name) {
        final FragmentDescriptor f = this.getFragment(name);
        if (f == null) {
            return null;
        }
        Resource jar = null;
        for (final Resource r : this._webFragmentResourceMap.keySet()) {
            if (this._webFragmentResourceMap.get(r).equals(f)) {
                jar = r;
            }
        }
        return jar;
    }
    
    public Map<String, FragmentDescriptor> getNamedFragments() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends FragmentDescriptor>)this._webFragmentNameMap);
    }
    
    public Origin getOrigin(final String name) {
        final OriginInfo x = this._origins.get(name);
        if (x == null) {
            return Origin.NotSet;
        }
        return x.getOriginType();
    }
    
    public OriginInfo getOriginInfo(final String name) {
        final OriginInfo x = this._origins.get(name);
        if (x == null) {
            return null;
        }
        return x;
    }
    
    public Descriptor getOriginDescriptor(final String name) {
        final OriginInfo o = this._origins.get(name);
        if (o == null) {
            return null;
        }
        return o.getDescriptor();
    }
    
    public void setOrigin(final String name, final Descriptor d) {
        final OriginInfo x = new OriginInfo(name, d);
        this._origins.put(name, x);
    }
    
    public void setOrigin(final String name, final Annotation annotation, final Class<?> annotated) {
        if (name == null) {
            return;
        }
        final OriginInfo x = new OriginInfo(name, annotation, annotated);
        this._origins.put(name, x);
    }
    
    public void setOriginAPI(final String name) {
        if (name == null) {
            return;
        }
        final OriginInfo x = new OriginInfo(name);
        this._origins.put(name, x);
    }
    
    public boolean isMetaDataComplete() {
        return this._metaDataComplete;
    }
    
    public void addWebInfJar(final Resource newResource) {
        this._webInfJars.add(newResource);
    }
    
    public List<Resource> getWebInfJars() {
        return Collections.unmodifiableList((List<? extends Resource>)this._webInfJars);
    }
    
    public List<Resource> getContainerResources() {
        return this._orderedContainerResources;
    }
    
    public void addContainerResource(final Resource jar) {
        this._orderedContainerResources.add(jar);
    }
    
    public void setWebInfClassesDirs(final List<Resource> dirs) {
        this._webInfClasses.addAll(dirs);
    }
    
    public List<Resource> getWebInfClassesDirs() {
        return this._webInfClasses;
    }
    
    public boolean isAllowDuplicateFragmentNames() {
        return this.allowDuplicateFragmentNames;
    }
    
    public void setAllowDuplicateFragmentNames(final boolean allowDuplicateFragmentNames) {
        this.allowDuplicateFragmentNames = allowDuplicateFragmentNames;
    }
    
    public Map<String, OriginInfo> getOrigins() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends OriginInfo>)this._origins);
    }
    
    static {
        LOG = Log.getLogger(MetaData.class);
        NON_FRAG_RESOURCE = EmptyResource.INSTANCE;
    }
    
    public static class OriginInfo
    {
        private final String name;
        private final Origin origin;
        private final Descriptor descriptor;
        private final Annotation annotation;
        private final Class<?> annotated;
        
        public OriginInfo(final String n, final Annotation a, final Class<?> ac) {
            this.name = n;
            this.origin = Origin.Annotation;
            this.descriptor = null;
            this.annotation = a;
            this.annotated = ac;
        }
        
        public OriginInfo(final String n, final Descriptor d) {
            this.name = n;
            this.descriptor = d;
            this.annotation = null;
            this.annotated = null;
            if (d == null) {
                throw new IllegalArgumentException("No descriptor");
            }
            if (d instanceof FragmentDescriptor) {
                this.origin = Origin.WebFragment;
            }
            else if (d instanceof OverrideDescriptor) {
                this.origin = Origin.WebOverride;
            }
            else if (d instanceof DefaultsDescriptor) {
                this.origin = Origin.WebDefaults;
            }
            else {
                this.origin = Origin.WebXml;
            }
        }
        
        public OriginInfo(final String n) {
            this.name = n;
            this.origin = Origin.API;
            this.annotation = null;
            this.descriptor = null;
            this.annotated = null;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Origin getOriginType() {
            return this.origin;
        }
        
        public Descriptor getDescriptor() {
            return this.descriptor;
        }
        
        @Override
        public String toString() {
            if (this.descriptor != null) {
                return this.descriptor.toString();
            }
            if (this.annotation != null) {
                return "@" + this.annotation.annotationType().getSimpleName() + "(" + this.annotated.getName() + ")";
            }
            return this.origin.toString();
        }
    }
}
