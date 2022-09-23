// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationElement
{
    private final ConfigurationElement parent;
    private ConfigurationElement[] children;
    private final Map attributes;
    private String[] attributeNames;
    private String name;
    private String text;
    private Extension extension;
    
    public ConfigurationElement(final Extension extension, final String name, final ConfigurationElement parent) {
        this.attributes = new HashMap();
        this.extension = extension;
        this.name = name;
        this.parent = parent;
        this.attributeNames = new String[0];
        this.children = new ConfigurationElement[0];
    }
    
    public String getName() {
        return this.name;
    }
    
    public ConfigurationElement getParent() {
        return this.parent;
    }
    
    public ConfigurationElement[] getChildren() {
        return this.children;
    }
    
    public String getAttribute(final String name) {
        return this.attributes.get(name);
    }
    
    public void putAttribute(final String name, final String value) {
        final String[] names = new String[this.attributeNames.length + 1];
        System.arraycopy(this.attributeNames, 0, names, 0, this.attributeNames.length);
        names[this.attributeNames.length] = name;
        this.attributeNames = names;
        this.attributes.put(name, value);
    }
    
    public void addConfigurationElement(final ConfigurationElement confElm) {
        final ConfigurationElement[] elm = new ConfigurationElement[this.children.length + 1];
        System.arraycopy(this.children, 0, elm, 0, this.children.length);
        elm[this.children.length] = confElm;
        this.children = elm;
    }
    
    public String[] getAttributeNames() {
        return this.attributeNames;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
    
    public Extension getExtension() {
        return this.extension;
    }
    
    @Override
    public String toString() {
        return this.name + " " + this.attributes;
    }
}
