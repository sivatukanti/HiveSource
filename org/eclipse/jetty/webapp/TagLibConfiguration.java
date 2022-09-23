// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.net.URL;
import javax.servlet.Servlet;
import org.eclipse.jetty.xml.XmlParser;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.IOException;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jetty.util.resource.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Collection;
import javax.servlet.ServletContextEvent;
import java.util.List;
import javax.servlet.ServletContextListener;
import org.eclipse.jetty.util.log.Log;
import java.util.EventListener;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.log.Logger;

public class TagLibConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    public static final String TLD_RESOURCES = "org.eclipse.jetty.tlds";
    
    @Override
    public void preConfigure(final WebAppContext context) throws Exception {
        try {
            final Class<?> jsp_page = (Class<?>)Loader.loadClass(WebXmlConfiguration.class, "javax.servlet.jsp.JspPage");
        }
        catch (Exception e) {
            return;
        }
        final TagLibListener tagLibListener = new TagLibListener(context);
        context.addEventListener(tagLibListener);
    }
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
    }
    
    @Override
    public void postConfigure(final WebAppContext context) throws Exception {
    }
    
    @Override
    public void cloneConfigure(final WebAppContext template, final WebAppContext context) throws Exception {
    }
    
    @Override
    public void deconfigure(final WebAppContext context) throws Exception {
    }
    
    static {
        LOG = Log.getLogger(TagLibConfiguration.class);
    }
    
    public class TagLibListener implements ServletContextListener
    {
        private List<EventListener> _tldListeners;
        private WebAppContext _context;
        
        public TagLibListener(final WebAppContext context) {
            this._context = context;
        }
        
        public void contextDestroyed(final ServletContextEvent sce) {
            if (this._tldListeners == null) {
                return;
            }
            for (int i = this._tldListeners.size() - 1; i >= 0; --i) {
                final EventListener l = this._tldListeners.get(i);
                if (l instanceof ServletContextListener) {
                    ((ServletContextListener)l).contextDestroyed(sce);
                }
            }
        }
        
        public void contextInitialized(final ServletContextEvent sce) {
            try {
                try {
                    final Class clazz = this.getClass().getClassLoader().loadClass("org.apache.jasper.compiler.TldLocationsCache");
                    final Collection<Resource> tld_resources = (Collection<Resource>)this._context.getAttribute("org.eclipse.jetty.tlds");
                    final Map<URI, List<String>> tldMap = new HashMap<URI, List<String>>();
                    if (tld_resources != null) {
                        for (final Resource r : tld_resources) {
                            final Resource jarResource = this.extractJarResource(r);
                            if (!tldMap.containsKey(jarResource.getURI())) {
                                tldMap.put(jarResource.getURI(), null);
                            }
                        }
                        sce.getServletContext().setAttribute("com.sun.appserv.tld.map", tldMap);
                    }
                }
                catch (ClassNotFoundException e) {
                    TagLibConfiguration.LOG.ignore(e);
                }
                final Set<Resource> tlds = this.findTldResources();
                final List<TldDescriptor> descriptors = this.parseTlds(tlds);
                this.processTlds(descriptors);
                if (this._tldListeners == null) {
                    return;
                }
                for (final EventListener l : this._tldListeners) {
                    if (l instanceof ServletContextListener) {
                        ((ServletContextListener)l).contextInitialized(sce);
                    }
                    else {
                        this._context.addEventListener(l);
                    }
                }
            }
            catch (Exception e2) {
                TagLibConfiguration.LOG.warn(e2);
            }
        }
        
        private Resource extractJarResource(final Resource r) {
            if (r == null) {
                return null;
            }
            try {
                String url = r.getURI().toURL().toString();
                final int idx = url.lastIndexOf("!/");
                if (idx >= 0) {
                    url = url.substring(0, idx);
                }
                if (url.startsWith("jar:")) {
                    url = url.substring(4);
                }
                return Resource.newResource(url);
            }
            catch (IOException e) {
                TagLibConfiguration.LOG.warn(e);
                return null;
            }
        }
        
        private Set<Resource> findTldResources() throws IOException {
            final Set<Resource> tlds = new HashSet<Resource>();
            if (this._context.getResourceAliases() != null && this._context.getBaseResource() != null && this._context.getBaseResource().exists()) {
                for (String location : this._context.getResourceAliases().values()) {
                    if (location != null && location.toLowerCase().endsWith(".tld")) {
                        if (!location.startsWith("/")) {
                            location = "/WEB-INF/" + location;
                        }
                        final Resource l = this._context.getBaseResource().addPath(location);
                        tlds.add(l);
                    }
                }
            }
            final Resource web_inf = this._context.getWebInf();
            if (web_inf != null) {
                final String[] contents = web_inf.list();
                for (int i = 0; contents != null && i < contents.length; ++i) {
                    if (contents[i] != null && contents[i].toLowerCase().endsWith(".tld")) {
                        final Resource j = web_inf.addPath(contents[i]);
                        tlds.add(j);
                    }
                }
            }
            if (web_inf != null) {
                final Resource web_inf_tlds = this._context.getWebInf().addPath("/tlds/");
                if (web_inf_tlds.exists() && web_inf_tlds.isDirectory()) {
                    final String[] contents2 = web_inf_tlds.list();
                    for (int k = 0; contents2 != null && k < contents2.length; ++k) {
                        if (contents2[k] != null && contents2[k].toLowerCase().endsWith(".tld")) {
                            final Resource m = web_inf_tlds.addPath(contents2[k]);
                            tlds.add(m);
                        }
                    }
                }
            }
            final Collection<Resource> tld_resources = (Collection<Resource>)this._context.getAttribute("org.eclipse.jetty.tlds");
            if (tld_resources != null) {
                tlds.addAll(tld_resources);
            }
            return tlds;
        }
        
        private List<TldDescriptor> parseTlds(final Set<Resource> tlds) {
            final List<TldDescriptor> descriptors = new ArrayList<TldDescriptor>();
            Resource tld = null;
            final Iterator<Resource> iter = tlds.iterator();
            while (iter.hasNext()) {
                try {
                    tld = iter.next();
                    if (TagLibConfiguration.LOG.isDebugEnabled()) {
                        TagLibConfiguration.LOG.debug("TLD=" + tld, new Object[0]);
                    }
                    final TldDescriptor d = new TldDescriptor(tld);
                    d.parse();
                    descriptors.add(d);
                }
                catch (Exception e) {
                    TagLibConfiguration.LOG.warn("Unable to parse TLD: " + tld, e);
                }
            }
            return descriptors;
        }
        
        private void processTlds(final List<TldDescriptor> descriptors) throws Exception {
            final TldProcessor processor = new TldProcessor();
            for (final TldDescriptor d : descriptors) {
                processor.process(this._context, d);
            }
            this._tldListeners = new ArrayList<EventListener>(processor.getListeners());
        }
    }
    
    public static class TldDescriptor extends Descriptor
    {
        protected static XmlParser __parserSingleton;
        
        public TldDescriptor(final Resource xml) {
            super(xml);
        }
        
        @Override
        public void ensureParser() throws ClassNotFoundException {
            if (TldDescriptor.__parserSingleton == null) {
                TldDescriptor.__parserSingleton = this.newParser();
            }
            this._parser = TldDescriptor.__parserSingleton;
        }
        
        public XmlParser newParser() throws ClassNotFoundException {
            final XmlParser parser = new XmlParser(false);
            URL taglib11 = null;
            URL taglib12 = null;
            URL taglib13 = null;
            URL taglib14 = null;
            try {
                final Class<?> jsp_page = (Class<?>)Loader.loadClass(WebXmlConfiguration.class, "javax.servlet.jsp.JspPage");
                taglib11 = jsp_page.getResource("javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd");
                taglib12 = jsp_page.getResource("javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd");
                taglib13 = jsp_page.getResource("javax/servlet/jsp/resources/web-jsptaglibrary_2_0.xsd");
                taglib14 = jsp_page.getResource("javax/servlet/jsp/resources/web-jsptaglibrary_2_1.xsd");
            }
            catch (Exception e) {
                TagLibConfiguration.LOG.ignore(e);
            }
            finally {
                if (taglib11 == null) {
                    taglib11 = Loader.getResource((Class)Servlet.class, "javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd", true);
                }
                if (taglib12 == null) {
                    taglib12 = Loader.getResource((Class)Servlet.class, "javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd", true);
                }
                if (taglib13 == null) {
                    taglib13 = Loader.getResource((Class)Servlet.class, "javax/servlet/jsp/resources/web-jsptaglibrary_2_0.xsd", true);
                }
                if (taglib14 == null) {
                    taglib14 = Loader.getResource((Class)Servlet.class, "javax/servlet/jsp/resources/web-jsptaglibrary_2_1.xsd", true);
                }
            }
            if (taglib11 != null) {
                this.redirect(parser, "web-jsptaglib_1_1.dtd", taglib11);
                this.redirect(parser, "web-jsptaglibrary_1_1.dtd", taglib11);
            }
            if (taglib12 != null) {
                this.redirect(parser, "web-jsptaglib_1_2.dtd", taglib12);
                this.redirect(parser, "web-jsptaglibrary_1_2.dtd", taglib12);
            }
            if (taglib13 != null) {
                this.redirect(parser, "web-jsptaglib_2_0.xsd", taglib13);
                this.redirect(parser, "web-jsptaglibrary_2_0.xsd", taglib13);
            }
            if (taglib14 != null) {
                this.redirect(parser, "web-jsptaglib_2_1.xsd", taglib14);
                this.redirect(parser, "web-jsptaglibrary_2_1.xsd", taglib14);
            }
            parser.setXpath("/taglib/listener/listener-class");
            return parser;
        }
        
        @Override
        public void parse() throws Exception {
            this.ensureParser();
            try {
                this._root = this._parser.parse(this._xml.getInputStream());
            }
            catch (Exception e) {
                this._root = this._parser.parse(this._xml.getURL().toString());
            }
            if (this._root == null) {
                TagLibConfiguration.LOG.warn("No TLD root in {}", this._xml);
            }
        }
    }
    
    public class TldProcessor extends IterativeDescriptorProcessor
    {
        public static final String TAGLIB_PROCESSOR = "org.eclipse.jetty.tagLibProcessor";
        XmlParser _parser;
        List<XmlParser.Node> _roots;
        List<EventListener> _listeners;
        
        public TldProcessor() throws Exception {
            this._roots = new ArrayList<XmlParser.Node>();
            this._listeners = new ArrayList<EventListener>();
            this.registerVisitor("listener", this.getClass().getDeclaredMethod("visitListener", TldProcessor.__signature));
        }
        
        public void visitListener(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
            final String className = node.getString("listener-class", false, true);
            if (TagLibConfiguration.LOG.isDebugEnabled()) {
                TagLibConfiguration.LOG.debug("listener=" + className, new Object[0]);
            }
            try {
                final Class<?> listenerClass = context.loadClass(className);
                final EventListener l = (EventListener)listenerClass.newInstance();
                this._listeners.add(l);
            }
            catch (Exception e) {
                TagLibConfiguration.LOG.warn("Could not instantiate listener " + className + ": " + e, new Object[0]);
                TagLibConfiguration.LOG.debug(e);
            }
            catch (Error e2) {
                TagLibConfiguration.LOG.warn("Could not instantiate listener " + className + ": " + e2, new Object[0]);
                TagLibConfiguration.LOG.debug(e2);
            }
        }
        
        @Override
        public void end(final WebAppContext context, final Descriptor descriptor) {
        }
        
        @Override
        public void start(final WebAppContext context, final Descriptor descriptor) {
        }
        
        public List<EventListener> getListeners() {
            return this._listeners;
        }
    }
}
