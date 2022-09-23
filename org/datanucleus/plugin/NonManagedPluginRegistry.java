// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.util.jar.Manifest;
import java.util.jar.JarInputStream;
import java.util.jar.JarFile;
import java.io.File;
import org.datanucleus.util.StringUtils;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.ClassConstants;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import org.datanucleus.util.NucleusLogger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import java.io.FilenameFilter;
import org.datanucleus.util.Localiser;

public class NonManagedPluginRegistry implements PluginRegistry
{
    protected static final Localiser LOCALISER;
    private static final String DATANUCLEUS_PKG = "org.datanucleus";
    private static final String PLUGIN_DIR = "/";
    private static final FilenameFilter MANIFEST_FILE_FILTER;
    private static final char JAR_SEPARATOR = '!';
    private final ClassLoaderResolver clr;
    Map<String, ExtensionPoint> extensionPointsByUniqueId;
    Map<String, Bundle> registeredPluginByPluginId;
    ExtensionPoint[] extensionPoints;
    private String bundleCheckType;
    private boolean allowUserBundles;
    
    public NonManagedPluginRegistry(final ClassLoaderResolver clr, final String bundleCheckType, final boolean allowUserBundles) {
        this.extensionPointsByUniqueId = new HashMap<String, ExtensionPoint>();
        this.registeredPluginByPluginId = new HashMap<String, Bundle>();
        this.bundleCheckType = "EXCEPTION";
        this.allowUserBundles = false;
        this.clr = clr;
        this.extensionPoints = new ExtensionPoint[0];
        this.bundleCheckType = ((bundleCheckType != null) ? bundleCheckType : "EXCEPTION");
        this.allowUserBundles = allowUserBundles;
    }
    
    @Override
    public ExtensionPoint getExtensionPoint(final String id) {
        return this.extensionPointsByUniqueId.get(id);
    }
    
    @Override
    public ExtensionPoint[] getExtensionPoints() {
        return this.extensionPoints;
    }
    
    @Override
    public void registerExtensionPoints() {
        this.registerExtensions();
    }
    
    public void registerExtensionsForPlugin(final URL pluginURL, final Bundle bundle) {
        final DocumentBuilder docBuilder = PluginParser.getDocumentBuilder();
        final List[] elements = PluginParser.parsePluginElements(docBuilder, this, pluginURL, bundle, this.clr);
        this.registerExtensionPointsForPluginInternal(elements[0], true);
        for (final Extension extension : elements[1]) {
            final ExtensionPoint exPoint = this.extensionPointsByUniqueId.get(extension.getExtensionPointId());
            if (exPoint == null) {
                NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024002", extension.getExtensionPointId(), extension.getPlugin().getSymbolicName(), extension.getPlugin().getManifestLocation().toString()));
            }
            else {
                extension.setExtensionPoint(exPoint);
                exPoint.addExtension(extension);
            }
        }
    }
    
    @Override
    public void registerExtensions() {
        if (this.extensionPoints.length > 0) {
            return;
        }
        final List registeringExtensions = new ArrayList();
        final DocumentBuilder docBuilder = PluginParser.getDocumentBuilder();
        for (final URL pluginURL : this.getPluginURLs()) {
            final URL manifest = this.getManifestURL(pluginURL);
            if (manifest == null) {
                continue;
            }
            final Bundle bundle = this.registerBundle(manifest);
            if (bundle == null) {
                continue;
            }
            final List[] elements = PluginParser.parsePluginElements(docBuilder, this, pluginURL, bundle, this.clr);
            this.registerExtensionPointsForPluginInternal(elements[0], false);
            registeringExtensions.addAll(elements[1]);
        }
        this.extensionPoints = this.extensionPointsByUniqueId.values().toArray(new ExtensionPoint[this.extensionPointsByUniqueId.values().size()]);
        for (int i = 0; i < registeringExtensions.size(); ++i) {
            final Extension extension = registeringExtensions.get(i);
            final ExtensionPoint exPoint = this.getExtensionPoint(extension.getExtensionPointId());
            if (exPoint == null) {
                if (extension.getPlugin() != null && extension.getPlugin().getSymbolicName() != null && extension.getPlugin().getSymbolicName().startsWith("org.datanucleus")) {
                    NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024002", extension.getExtensionPointId(), extension.getPlugin().getSymbolicName(), extension.getPlugin().getManifestLocation().toString()));
                }
            }
            else {
                extension.setExtensionPoint(exPoint);
                exPoint.addExtension(extension);
            }
        }
        if (this.allowUserBundles) {
            final ExtensionSorter sorter = new ExtensionSorter();
            for (int j = 0; j < this.extensionPoints.length; ++j) {
                final ExtensionPoint pt = this.extensionPoints[j];
                pt.sortExtensions(sorter);
            }
        }
    }
    
    protected void registerExtensionPointsForPluginInternal(final List extPoints, final boolean updateExtensionPointsArray) {
        for (final ExtensionPoint exPoint : extPoints) {
            this.extensionPointsByUniqueId.put(exPoint.getUniqueId(), exPoint);
        }
        if (updateExtensionPointsArray) {
            this.extensionPoints = this.extensionPointsByUniqueId.values().toArray(new ExtensionPoint[this.extensionPointsByUniqueId.values().size()]);
        }
    }
    
    private Set<URL> getPluginURLs() {
        final Set<URL> set = new HashSet<URL>();
        try {
            final Enumeration<URL> paths = this.clr.getResources("/plugin.xml", ClassConstants.NUCLEUS_CONTEXT_LOADER);
            while (paths.hasMoreElements()) {
                set.add(paths.nextElement());
            }
        }
        catch (IOException e) {
            throw new NucleusException("Error loading resource", e).setFatal();
        }
        return set;
    }
    
    protected Bundle registerBundle(final URL manifest) {
        if (manifest == null) {
            throw new IllegalArgumentException(NonManagedPluginRegistry.LOCALISER.msg("024007"));
        }
        InputStream is = null;
        try {
            Manifest mf = null;
            if (manifest.getProtocol().equals("jar") || manifest.getProtocol().equals("zip") || manifest.getProtocol().equals("wsjar")) {
                if (manifest.getPath().startsWith("http://") || manifest.getPath().startsWith("https://")) {
                    final JarURLConnection jarConnection = (JarURLConnection)manifest.openConnection();
                    final URL url = jarConnection.getJarFileURL();
                    mf = jarConnection.getManifest();
                    if (mf == null) {
                        return null;
                    }
                    return this.registerBundle(mf, url);
                }
                else {
                    int begin = 4;
                    if (manifest.getProtocol().equals("wsjar")) {
                        begin = 6;
                    }
                    final String path = StringUtils.getDecodedStringFromURLString(manifest.toExternalForm());
                    final int index = path.indexOf(33);
                    String jarPath = path.substring(begin, index);
                    if (jarPath.startsWith("file:")) {
                        jarPath = jarPath.substring(5);
                    }
                    final File jarFile = new File(jarPath);
                    mf = new JarFile(jarFile).getManifest();
                    if (mf == null) {
                        return null;
                    }
                    return this.registerBundle(mf, jarFile.toURI().toURL());
                }
            }
            else {
                if (manifest.getProtocol().equals("rar") || manifest.getProtocol().equals("war")) {
                    final String path2 = StringUtils.getDecodedStringFromURLString(manifest.toExternalForm());
                    final int index2 = path2.indexOf(33);
                    final String rarPath = path2.substring(4, index2);
                    final File file = new File(rarPath);
                    final URL rarUrl = file.toURI().toURL();
                    final String jarPath2 = path2.substring(index2 + 1, path2.indexOf(33, index2 + 1));
                    final JarFile rarFile = new JarFile(file);
                    final JarInputStream jis = new JarInputStream(rarFile.getInputStream(rarFile.getEntry(jarPath2)));
                    try {
                        mf = jis.getManifest();
                        if (mf == null) {
                            return null;
                        }
                    }
                    finally {
                        jis.close();
                    }
                    return this.registerBundle(mf, rarUrl);
                }
                is = manifest.openStream();
                mf = new Manifest(is);
                return this.registerBundle(mf, manifest);
            }
        }
        catch (IOException e) {
            throw new NucleusException(NonManagedPluginRegistry.LOCALISER.msg("024008", manifest), e).setFatal();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected Bundle registerBundle(final Manifest mf, final URL manifest) {
        final Bundle bundle = PluginParser.parseManifest(mf, manifest);
        if (bundle == null || bundle.getSymbolicName() == null) {
            return null;
        }
        if (!this.allowUserBundles && !bundle.getSymbolicName().startsWith("org.datanucleus")) {
            NucleusLogger.GENERAL.debug("Ignoring bundle " + bundle.getSymbolicName() + " since not DataNucleus, and only loading DataNucleus bundles");
            return null;
        }
        if (this.registeredPluginByPluginId.get(bundle.getSymbolicName()) == null) {
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug("Registering bundle " + bundle.getSymbolicName() + " version " + bundle.getVersion() + " at URL " + bundle.getManifestLocation() + ".");
            }
            this.registeredPluginByPluginId.put(bundle.getSymbolicName(), bundle);
        }
        else {
            final Bundle previousBundle = this.registeredPluginByPluginId.get(bundle.getSymbolicName());
            if (bundle.getSymbolicName().startsWith("org.datanucleus") && !bundle.getManifestLocation().toExternalForm().equals(previousBundle.getManifestLocation().toExternalForm())) {
                final String msg = NonManagedPluginRegistry.LOCALISER.msg("024009", bundle.getSymbolicName(), bundle.getManifestLocation(), previousBundle.getManifestLocation());
                if (this.bundleCheckType.equalsIgnoreCase("EXCEPTION")) {
                    throw new NucleusException(msg);
                }
                if (this.bundleCheckType.equalsIgnoreCase("LOG")) {
                    NucleusLogger.GENERAL.warn(msg);
                }
            }
        }
        return bundle;
    }
    
    private URL getManifestURL(final URL pluginURL) {
        if (pluginURL == null) {
            return null;
        }
        if (pluginURL.toString().startsWith("jar") || pluginURL.toString().startsWith("zip") || pluginURL.toString().startsWith("rar") || pluginURL.toString().startsWith("war") || pluginURL.toString().startsWith("wsjar")) {
            return pluginURL;
        }
        if (pluginURL.toString().startsWith("vfs")) {
            final String urlStr = pluginURL.toString().replace("plugin.xml", "META-INF/MANIFEST.MF");
            try {
                return new URL(urlStr);
            }
            catch (MalformedURLException e) {
                NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024010", urlStr), e);
                return null;
            }
        }
        if (pluginURL.toString().startsWith("jndi")) {
            String urlStr = pluginURL.toString().substring(5);
            urlStr = urlStr.replaceAll("\\.jar/", ".jar!/");
            urlStr = "jar:file:" + urlStr;
            try {
                return new URL(urlStr);
            }
            catch (MalformedURLException e) {
                NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024010", urlStr), e);
                return null;
            }
        }
        if (pluginURL.toString().startsWith("code-source")) {
            String urlStr = pluginURL.toString().substring(12);
            urlStr = "jar:file:" + urlStr;
            try {
                return new URL(urlStr);
            }
            catch (MalformedURLException e) {
                NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024010", urlStr), e);
                return null;
            }
        }
        try {
            final File file = new File(new URI(pluginURL.toString()).getPath());
            final File[] dirs = new File(file.getParent()).listFiles(NonManagedPluginRegistry.MANIFEST_FILE_FILTER);
            if (dirs != null && dirs.length > 0) {
                final File[] files = dirs[0].listFiles(NonManagedPluginRegistry.MANIFEST_FILE_FILTER);
                if (files != null && files.length > 0) {
                    try {
                        return files[0].toURI().toURL();
                    }
                    catch (MalformedURLException e2) {
                        NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024011", pluginURL), e2);
                        return null;
                    }
                }
            }
        }
        catch (URISyntaxException use) {
            use.printStackTrace();
            NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024011", pluginURL), use);
            return null;
        }
        NucleusLogger.GENERAL.warn(NonManagedPluginRegistry.LOCALISER.msg("024012", pluginURL));
        return null;
    }
    
    @Override
    public Object createExecutableExtension(final ConfigurationElement confElm, final String name, final Class[] argsClass, final Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Class cls = this.clr.classForName(confElm.getAttribute(name), ClassConstants.NUCLEUS_CONTEXT_LOADER);
        final Constructor constructor = cls.getConstructor((Class[])argsClass);
        return constructor.newInstance(args);
    }
    
    @Override
    public Class loadClass(final String pluginId, final String className) throws ClassNotFoundException {
        return this.clr.classForName(className, ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    @Override
    public URL resolveURLAsFileURL(final URL url) throws IOException {
        return url;
    }
    
    @Override
    public void resolveConstraints() {
        for (final Bundle bundle : this.registeredPluginByPluginId.values()) {
            final List set = bundle.getRequireBundle();
            for (final Bundle.BundleDescription bd : set) {
                final String symbolicName = bd.getBundleSymbolicName();
                final Bundle requiredBundle = this.registeredPluginByPluginId.get(symbolicName);
                if (requiredBundle == null) {
                    if (bd.getParameter("resolution") != null && bd.getParameter("resolution").equalsIgnoreCase("optional")) {
                        NucleusLogger.GENERAL.debug(NonManagedPluginRegistry.LOCALISER.msg("024013", bundle.getSymbolicName(), symbolicName));
                    }
                    else {
                        NucleusLogger.GENERAL.error(NonManagedPluginRegistry.LOCALISER.msg("024014", bundle.getSymbolicName(), symbolicName));
                    }
                }
                if (bd.getParameter("bundle-version") != null && requiredBundle != null && !this.isVersionInInterval(requiredBundle.getVersion(), bd.getParameter("bundle-version"))) {
                    NucleusLogger.GENERAL.error(NonManagedPluginRegistry.LOCALISER.msg("024015", bundle.getSymbolicName(), symbolicName, bd.getParameter("bundle-version"), bundle.getVersion()));
                }
            }
        }
    }
    
    private boolean isVersionInInterval(final String version, final String interval) {
        final Bundle.BundleVersionRange versionRange = PluginParser.parseVersionRange(version);
        final Bundle.BundleVersionRange intervalRange = PluginParser.parseVersionRange(interval);
        final int compare_floor = versionRange.floor.compareTo(intervalRange.floor);
        boolean result = true;
        if (intervalRange.floor_inclusive) {
            result = (compare_floor >= 0);
        }
        else {
            result = (compare_floor > 0);
        }
        if (intervalRange.ceiling != null) {
            final int compare_ceiling = versionRange.floor.compareTo(intervalRange.ceiling);
            if (intervalRange.ceiling_inclusive) {
                result = (compare_ceiling <= 0);
            }
            else {
                result = (compare_ceiling < 0);
            }
        }
        return result;
    }
    
    @Override
    public Bundle[] getBundles() {
        return this.registeredPluginByPluginId.values().toArray(new Bundle[this.registeredPluginByPluginId.values().size()]);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        MANIFEST_FILE_FILTER = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.equalsIgnoreCase("meta-inf") || (dir.getName().equalsIgnoreCase("meta-inf") && name.equalsIgnoreCase("manifest.mf"));
            }
        };
    }
    
    protected static class ExtensionSorter implements Comparator<Extension>, Serializable
    {
        @Override
        public int compare(final Extension o1, final Extension o2) {
            final String name1 = o1.getPlugin().getSymbolicName();
            final String name2 = o2.getPlugin().getSymbolicName();
            if (name1.startsWith("org.datanucleus") && !name2.startsWith("org.datanucleus")) {
                return -1;
            }
            if (!name1.startsWith("org.datanucleus") && name2.startsWith("org.datanucleus")) {
                return 1;
            }
            return name1.compareTo(name2);
        }
    }
}
