// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import org.datanucleus.ClassLoaderResolverImpl;
import java.util.Map;
import java.io.IOException;
import java.net.URL;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.datanucleus.ClassLoaderResolver;

public class PluginManager
{
    private PluginRegistry registry;
    
    public PluginManager(final String registryClassName, final ClassLoaderResolver clr, final Properties props) {
        String bundleCheckAction = "EXCEPTION";
        if (props.containsKey("bundle-check-action")) {
            bundleCheckAction = props.getProperty("bundle-check-action");
        }
        final String allowUserBundles = props.getProperty("allow-user-bundles");
        final boolean userBundles = allowUserBundles == null || Boolean.valueOf(allowUserBundles);
        (this.registry = PluginRegistryFactory.newPluginRegistry(registryClassName, bundleCheckAction, userBundles, clr)).registerExtensionPoints();
        this.registry.registerExtensions();
        final String validateStr = props.containsKey("validate-plugins") ? props.getProperty("validate-plugins") : "false";
        if (validateStr.equalsIgnoreCase("true")) {
            this.registry.resolveConstraints();
        }
    }
    
    public String getRegistryClassName() {
        return this.registry.getClass().getName();
    }
    
    public ExtensionPoint getExtensionPoint(final String id) {
        return this.registry.getExtensionPoint(id);
    }
    
    public ConfigurationElement getConfigurationElementForExtension(final String extensionPointName, final String discrimAttrName, final String discrimAttrValue) {
        return this.getConfigurationElementForExtension(extensionPointName, (discrimAttrName != null) ? new String[] { discrimAttrName } : new String[0], (discrimAttrValue != null) ? new String[] { discrimAttrValue } : new String[0]);
    }
    
    public ConfigurationElement[] getConfigurationElementsForExtension(final String extensionPointName, final String discrimAttrName, final String discrimAttrValue) {
        final List<ConfigurationElement> elems = (List<ConfigurationElement>)this.getConfigurationElementsForExtension(extensionPointName, (discrimAttrName != null) ? new String[] { discrimAttrName } : new String[0], (discrimAttrValue != null) ? new String[] { discrimAttrValue } : new String[0]);
        if (!elems.isEmpty()) {
            return elems.toArray(new ConfigurationElement[elems.size()]);
        }
        return null;
    }
    
    public ConfigurationElement getConfigurationElementForExtension(final String extensionPointName, final String[] discrimAttrName, final String[] discrimAttrValue) {
        final List matchingConfigElements = this.getConfigurationElementsForExtension(extensionPointName, discrimAttrName, discrimAttrValue);
        if (!matchingConfigElements.isEmpty()) {
            return matchingConfigElements.get(0);
        }
        return null;
    }
    
    private List getConfigurationElementsForExtension(final String extensionPointName, final String[] discrimAttrName, final String[] discrimAttrValue) {
        final List matchingConfigElements = new LinkedList();
        final ExtensionPoint extensionPoint = this.getExtensionPoint(extensionPointName);
        if (extensionPoint != null) {
            final Extension[] ex = extensionPoint.getExtensions();
            for (int i = 0; i < ex.length; ++i) {
                final ConfigurationElement[] confElm = ex[i].getConfigurationElements();
                for (int j = 0; j < confElm.length; ++j) {
                    boolean equals = true;
                    for (int k = 0; k < discrimAttrName.length; ++k) {
                        if (discrimAttrValue[k] == null) {
                            if (confElm[j].getAttribute(discrimAttrName[k]) != null) {
                                equals = false;
                                break;
                            }
                        }
                        else {
                            if (confElm[j].getAttribute(discrimAttrName[k]) == null) {
                                equals = false;
                                break;
                            }
                            if (!confElm[j].getAttribute(discrimAttrName[k]).equalsIgnoreCase(discrimAttrValue[k])) {
                                equals = false;
                                break;
                            }
                        }
                    }
                    if (equals) {
                        matchingConfigElements.add(confElm[j]);
                    }
                }
            }
        }
        Collections.sort((List<Object>)matchingConfigElements, (Comparator<? super Object>)new ConfigurationElementPriorityComparator());
        return matchingConfigElements;
    }
    
    public String getAttributeValueForExtension(final String extensionPoint, final String discrimAttrName, final String discrimAttrValue, final String attributeName) {
        final ConfigurationElement elem = this.getConfigurationElementForExtension(extensionPoint, discrimAttrName, discrimAttrValue);
        if (elem != null) {
            return elem.getAttribute(attributeName);
        }
        return null;
    }
    
    public String[] getAttributeValuesForExtension(final String extensionPoint, final String discrimAttrName, final String discrimAttrValue, final String attributeName) {
        final ConfigurationElement[] elems = this.getConfigurationElementsForExtension(extensionPoint, discrimAttrName, discrimAttrValue);
        if (elems != null) {
            final String[] attrValues = new String[elems.length];
            for (int i = 0; i < elems.length; ++i) {
                attrValues[i] = elems[i].getAttribute(attributeName);
            }
            return attrValues;
        }
        return null;
    }
    
    public String getAttributeValueForExtension(final String extensionPoint, final String[] discrimAttrName, final String[] discrimAttrValue, final String attributeName) {
        final ConfigurationElement elem = this.getConfigurationElementForExtension(extensionPoint, discrimAttrName, discrimAttrValue);
        if (elem != null) {
            return elem.getAttribute(attributeName);
        }
        return null;
    }
    
    public Object createExecutableExtension(final String extensionPoint, final String discrimAttrName, final String discrimAttrValue, final String attributeName, final Class[] argsClass, final Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final ConfigurationElement elem = this.getConfigurationElementForExtension(extensionPoint, discrimAttrName, discrimAttrValue);
        if (elem != null) {
            return this.registry.createExecutableExtension(elem, attributeName, argsClass, args);
        }
        return null;
    }
    
    public Object createExecutableExtension(final String extensionPoint, final String[] discrimAttrName, final String[] discrimAttrValue, final String attributeName, final Class[] argsClass, final Object[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final ConfigurationElement elem = this.getConfigurationElementForExtension(extensionPoint, discrimAttrName, discrimAttrValue);
        if (elem != null) {
            return this.registry.createExecutableExtension(elem, attributeName, argsClass, args);
        }
        return null;
    }
    
    public Class loadClass(final String pluginId, final String className) throws ClassNotResolvedException {
        try {
            return this.registry.loadClass(pluginId, className);
        }
        catch (ClassNotFoundException ex) {
            throw new ClassNotResolvedException(ex.getMessage(), ex);
        }
    }
    
    public URL resolveURLAsFileURL(final URL url) throws IOException {
        return this.registry.resolveURLAsFileURL(url);
    }
    
    public String getVersionForBundle(final String bundleName) {
        final Bundle[] bundles = this.registry.getBundles();
        if (bundles != null) {
            for (int i = 0; i < bundles.length; ++i) {
                final Bundle bundle = bundles[i];
                if (bundle.getSymbolicName().equals(bundleName)) {
                    return bundle.getVersion();
                }
            }
        }
        return null;
    }
    
    public static PluginManager createPluginManager(final Map props, final ClassLoader loader) {
        final ClassLoaderResolver clr = (loader != null) ? new ClassLoaderResolverImpl(loader) : new ClassLoaderResolverImpl();
        if (props != null) {
            clr.registerUserClassLoader(props.get("datanucleus.primaryClassLoader"));
        }
        final Properties pluginProps = new Properties();
        String registryClassName = null;
        if (props != null) {
            registryClassName = (String)props.get("datanucleus.plugin.pluginRegistryClassName");
            if (props.containsKey("datanucleus.plugin.pluginRegistryBundleCheck")) {
                pluginProps.setProperty("bundle-check-action", (String)props.get("datanucleus.plugin.pluginRegistryBundleCheck"));
            }
            if (props.containsKey("datanucleus.plugin.allowUserBundles")) {
                pluginProps.setProperty("allow-user-bundles", (String)props.get("datanucleus.plugin.allowUserBundles"));
            }
            if (props.containsKey("datanucleus.plugin.validatePlugins")) {
                pluginProps.setProperty("validate-plugins", (String)props.get("datanucleus.plugin.validatePlugins"));
            }
        }
        return new PluginManager(registryClassName, clr, pluginProps);
    }
    
    private static final class ConfigurationElementPriorityComparator implements Comparator<ConfigurationElement>
    {
        @Override
        public int compare(final ConfigurationElement elm1, final ConfigurationElement elm2) {
            final String pri1 = elm1.getAttribute("priority");
            final String pri2 = elm2.getAttribute("priority");
            return ((pri2 == null) ? 0 : Integer.parseInt(pri2)) - ((pri1 == null) ? 0 : Integer.parseInt(pri1));
        }
    }
}
