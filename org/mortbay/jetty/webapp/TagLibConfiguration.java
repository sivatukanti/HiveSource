// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.webapp;

import java.util.Enumeration;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.EventListener;
import org.mortbay.util.Loader;
import org.mortbay.xml.XmlParser;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import org.mortbay.resource.Resource;
import org.mortbay.log.Log;
import java.net.URLClassLoader;
import java.util.regex.Pattern;
import java.util.HashSet;

public class TagLibConfiguration implements Configuration
{
    WebAppContext _context;
    
    public void setWebAppContext(final WebAppContext context) {
        this._context = context;
    }
    
    public WebAppContext getWebAppContext() {
        return this._context;
    }
    
    public void configureClassLoader() throws Exception {
    }
    
    public void configureDefaults() throws Exception {
    }
    
    public void configureWebApp() throws Exception {
        final Set tlds = new HashSet();
        final Set jars = new HashSet();
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
                    final Resource j = this._context.getWebInf().addPath(contents[i]);
                    tlds.add(j);
                }
            }
        }
        final String no_TLD_attr = this._context.getInitParameter("org.mortbay.jetty.webapp.NoTLDJarPattern");
        final Pattern no_TLD_pattern = (no_TLD_attr == null) ? null : Pattern.compile(no_TLD_attr);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        boolean parent = false;
        while (loader != null) {
            if (loader instanceof URLClassLoader) {
                final URL[] urls = ((URLClassLoader)loader).getURLs();
                if (urls != null) {
                    for (int k = 0; k < urls.length; ++k) {
                        if (urls[k].toString().toLowerCase().endsWith(".jar")) {
                            String jar = urls[k].toString();
                            final int slash = jar.lastIndexOf(47);
                            jar = jar.substring(slash + 1);
                            if (parent) {
                                if (!this._context.isParentLoaderPriority() && jars.contains(jar)) {
                                    continue;
                                }
                                if (no_TLD_pattern != null && no_TLD_pattern.matcher(jar).matches()) {
                                    continue;
                                }
                            }
                            jars.add(jar);
                            Log.debug("TLD search of {}", urls[k]);
                            final File file = Resource.newResource(urls[k]).getFile();
                            if (file != null && file.exists()) {
                                if (file.canRead()) {
                                    JarFile jarfile = null;
                                    try {
                                        jarfile = new JarFile(file);
                                        final Enumeration e = jarfile.entries();
                                        while (e.hasMoreElements()) {
                                            final ZipEntry entry = e.nextElement();
                                            final String name = entry.getName();
                                            if (name.startsWith("META-INF/") && name.toLowerCase().endsWith(".tld")) {
                                                final Resource tld = Resource.newResource("jar:" + urls[k] + "!/" + name);
                                                tlds.add(tld);
                                                Log.debug("TLD found {}", tld);
                                            }
                                        }
                                    }
                                    catch (Exception e2) {
                                        Log.warn("Failed to read file: " + file, e2);
                                    }
                                    finally {
                                        if (jarfile != null) {
                                            jarfile.close();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            loader = loader.getParent();
            parent = true;
        }
        final XmlParser parser = new XmlParser(false);
        parser.redirectEntity("web-jsptaglib_1_1.dtd", Loader.getResource(TagLibConfiguration.class, "javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd", false));
        parser.redirectEntity("web-jsptaglib_1_2.dtd", Loader.getResource(TagLibConfiguration.class, "javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd", false));
        parser.redirectEntity("web-jsptaglib_2_0.xsd", Loader.getResource(TagLibConfiguration.class, "javax/servlet/jsp/resources/web-jsptaglibrary_2_0.xsd", false));
        parser.redirectEntity("web-jsptaglibrary_1_1.dtd", Loader.getResource(TagLibConfiguration.class, "javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd", false));
        parser.redirectEntity("web-jsptaglibrary_1_2.dtd", Loader.getResource(TagLibConfiguration.class, "javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd", false));
        parser.redirectEntity("web-jsptaglibrary_2_0.xsd", Loader.getResource(TagLibConfiguration.class, "javax/servlet/jsp/resources/web-jsptaglibrary_2_0.xsd", false));
        parser.setXpath("/taglib/listener/listener-class");
        final Iterator iter2 = tlds.iterator();
        while (iter2.hasNext()) {
            try {
                final Resource tld2 = iter2.next();
                if (Log.isDebugEnabled()) {
                    Log.debug("TLD=" + tld2);
                }
                XmlParser.Node root;
                try {
                    root = parser.parse(tld2.getInputStream());
                }
                catch (Exception e6) {
                    root = parser.parse(tld2.getURL().toString());
                }
                if (root == null) {
                    Log.warn("No TLD root in {}", tld2);
                }
                else {
                    for (int m = 0; m < root.size(); ++m) {
                        final Object o = root.get(m);
                        if (o instanceof XmlParser.Node) {
                            final XmlParser.Node node = (XmlParser.Node)o;
                            if ("listener".equals(node.getTag())) {
                                final String className = node.getString("listener-class", false, true);
                                if (Log.isDebugEnabled()) {
                                    Log.debug("listener=" + className);
                                }
                                try {
                                    final Class listenerClass = this.getWebAppContext().loadClass(className);
                                    final EventListener l2 = listenerClass.newInstance();
                                    this._context.addEventListener(l2);
                                }
                                catch (Exception e3) {
                                    Log.warn("Could not instantiate listener " + className + ": " + e3);
                                    Log.debug(e3);
                                }
                                catch (Error e4) {
                                    Log.warn("Could not instantiate listener " + className + ": " + e4);
                                    Log.debug(e4);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e5) {
                Log.warn(e5);
            }
        }
    }
    
    public void deconfigureWebApp() throws Exception {
    }
}
