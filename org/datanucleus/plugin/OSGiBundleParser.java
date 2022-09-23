// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import org.datanucleus.ClassConstants;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import java.util.StringTokenizer;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import java.util.List;
import java.net.URL;
import org.osgi.framework.Bundle;
import javax.xml.parsers.DocumentBuilderFactory;
import org.datanucleus.util.Localiser;

public class OSGiBundleParser
{
    protected static final Localiser LOCALISER;
    static DocumentBuilderFactory dbFactory;
    
    public static org.datanucleus.plugin.Bundle parseManifest(final Bundle osgiBundle) {
        final Dictionary<String, String> headers = (Dictionary<String, String>)osgiBundle.getHeaders();
        org.datanucleus.plugin.Bundle bundle = null;
        try {
            final String symbolicName = getBundleSymbolicName(headers, null);
            final String bundleVersion = getBundleVersion(headers, null);
            final String bundleName = getBundleName(headers, null);
            final String bundleVendor = getBundleVendor(headers, null);
            bundle = new org.datanucleus.plugin.Bundle(symbolicName, bundleName, bundleVendor, bundleVersion, null);
            bundle.setRequireBundle(getRequireBundle(headers));
        }
        catch (NucleusException ne) {
            NucleusLogger.GENERAL.warn("Plugin at bundle " + osgiBundle.getSymbolicName() + " (" + osgiBundle.getBundleId() + ") failed to parse so is being ignored", ne);
            return null;
        }
        return bundle;
    }
    
    private static List<org.datanucleus.plugin.Bundle.BundleDescription> getRequireBundle(final Dictionary<String, String> headers) {
        final String str = headers.get("Require-Bundle");
        if (str == null || str.length() < 1) {
            return Collections.emptyList();
        }
        final PluginParser.Parser p = new PluginParser.Parser(str);
        final List<org.datanucleus.plugin.Bundle.BundleDescription> requiredBundle = new ArrayList<org.datanucleus.plugin.Bundle.BundleDescription>();
        String bundleSymbolicName = p.parseSymbolicName();
        while (bundleSymbolicName != null) {
            final org.datanucleus.plugin.Bundle.BundleDescription bd = new org.datanucleus.plugin.Bundle.BundleDescription();
            bd.setBundleSymbolicName(bundleSymbolicName);
            bd.setParameters(p.parseParameters());
            bundleSymbolicName = p.parseSymbolicName();
            requiredBundle.add(bd);
        }
        return requiredBundle;
    }
    
    private static List<ExtensionPoint> parseExtensionPoints(final Element rootElement, final org.datanucleus.plugin.Bundle plugin, final Bundle osgiBundle) {
        final List<ExtensionPoint> extensionPoints = new ArrayList<ExtensionPoint>();
        try {
            final NodeList elements = rootElement.getElementsByTagName("extension-point");
            for (int i = 0; i < elements.getLength(); ++i) {
                final Element element = (Element)elements.item(i);
                final String id = element.getAttribute("id").trim();
                final String name = element.getAttribute("name");
                final String schema = element.getAttribute("schema");
                extensionPoints.add(new ExtensionPoint(id, name, osgiBundle.getEntry(schema), plugin));
            }
        }
        catch (NucleusException ex) {
            throw ex;
        }
        return extensionPoints;
    }
    
    private static List<Extension> parseExtensions(final Element rootElement, final org.datanucleus.plugin.Bundle plugin, final Bundle osgiBundle) {
        final List<Extension> extensions = new ArrayList<Extension>();
        try {
            final NodeList elements = rootElement.getElementsByTagName("extension");
            for (int i = 0; i < elements.getLength(); ++i) {
                final Element element = (Element)elements.item(i);
                final Extension ex = new Extension(element.getAttribute("point"), plugin);
                final NodeList elms = element.getChildNodes();
                extensions.add(ex);
                for (int e = 0; e < elms.getLength(); ++e) {
                    if (elms.item(e) instanceof Element) {
                        ex.addConfigurationElement(PluginParser.parseConfigurationElement(ex, (Element)elms.item(e), null));
                    }
                }
            }
        }
        catch (NucleusException ex2) {
            throw ex2;
        }
        return extensions;
    }
    
    private static String getHeaderValue(final Dictionary<String, String> headers, final String key, final String defaultValue) {
        if (headers == null) {
            return defaultValue;
        }
        final String name = headers.get(key);
        if (name == null) {
            return defaultValue;
        }
        return name;
    }
    
    private static String getBundleSymbolicName(final Dictionary<String, String> headers, final String defaultValue) {
        final String name = getHeaderValue(headers, "Bundle-SymbolicName", defaultValue);
        final StringTokenizer token = new StringTokenizer(name, ";");
        return token.nextToken().trim();
    }
    
    private static String getBundleName(final Dictionary<String, String> headers, final String defaultValue) {
        return getHeaderValue(headers, "Bundle-Name", defaultValue);
    }
    
    private static String getBundleVendor(final Dictionary<String, String> headers, final String defaultValue) {
        return getHeaderValue(headers, "Bundle-Vendor", defaultValue);
    }
    
    private static String getBundleVersion(final Dictionary<String, String> headers, final String defaultValue) {
        return getHeaderValue(headers, "Bundle-Version", defaultValue);
    }
    
    public static List[] parsePluginElements(final DocumentBuilder db, final PluginRegistry mgr, final URL fileUrl, final org.datanucleus.plugin.Bundle plugin, final Bundle osgiBundle) {
        List<ExtensionPoint> extensionPoints = Collections.emptyList();
        List<Extension> extensions = Collections.emptyList();
        InputStream is = null;
        try {
            is = fileUrl.openStream();
            final Element rootElement = db.parse(new InputSource(new InputStreamReader(is))).getDocumentElement();
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug(OSGiBundleParser.LOCALISER.msg("024003", fileUrl.toString()));
            }
            extensionPoints = parseExtensionPoints(rootElement, plugin, osgiBundle);
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug(OSGiBundleParser.LOCALISER.msg("024004", fileUrl.toString()));
            }
            extensions = parseExtensions(rootElement, plugin, osgiBundle);
        }
        catch (NucleusException ex) {
            throw ex;
        }
        catch (Exception e) {
            NucleusLogger.GENERAL.error(OSGiBundleParser.LOCALISER.msg("024000", fileUrl.getFile()));
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ex2) {}
            }
        }
        return new List[] { extensionPoints, extensions };
    }
    
    public static DocumentBuilder getDocumentBuilder() {
        try {
            if (OSGiBundleParser.dbFactory == null) {
                OSGiBundleParser.dbFactory = DocumentBuilderFactory.newInstance();
            }
            return OSGiBundleParser.dbFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e1) {
            throw new NucleusException(OSGiBundleParser.LOCALISER.msg("024016", e1.getMessage()));
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        OSGiBundleParser.dbFactory = null;
    }
}
