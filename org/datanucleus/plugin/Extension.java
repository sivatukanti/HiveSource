// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

public class Extension
{
    private ExtensionPoint point;
    private final Bundle plugin;
    private final String pointId;
    ConfigurationElement[] configurationElement;
    
    public Extension(final ExtensionPoint point, final Bundle plugin) {
        this.point = point;
        this.pointId = point.getUniqueId();
        this.plugin = plugin;
        this.configurationElement = new ConfigurationElement[0];
    }
    
    public Extension(final String pointId, final Bundle plugin) {
        this.pointId = pointId;
        this.plugin = plugin;
        this.configurationElement = new ConfigurationElement[0];
    }
    
    public void setExtensionPoint(final ExtensionPoint point) {
        this.point = point;
    }
    
    public String getExtensionPointId() {
        return this.pointId;
    }
    
    public Bundle getPlugin() {
        return this.plugin;
    }
    
    public ExtensionPoint getPoint() {
        return this.point;
    }
    
    public void addConfigurationElement(final ConfigurationElement element) {
        final ConfigurationElement[] elms = new ConfigurationElement[this.configurationElement.length + 1];
        System.arraycopy(this.configurationElement, 0, elms, 0, this.configurationElement.length);
        elms[this.configurationElement.length] = element;
        this.configurationElement = elms;
    }
    
    public ConfigurationElement[] getConfigurationElements() {
        return this.configurationElement;
    }
}
