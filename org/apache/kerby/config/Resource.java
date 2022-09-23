// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.config;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Properties;
import java.util.Map;
import java.net.URL;
import java.io.IOException;
import java.io.File;

public final class Resource
{
    private String name;
    private Object resource;
    private Format format;
    
    public static Resource createXmlResource(final File xmlFile) throws IOException {
        return new Resource(xmlFile.getName(), xmlFile, Format.XML_FILE);
    }
    
    public static Resource createIniResource(final File iniFile) throws IOException {
        return new Resource(iniFile.getName(), iniFile, Format.INI_FILE);
    }
    
    public static Resource createJsonResource(final File jsonFile) throws IOException {
        return new Resource(jsonFile.getName(), jsonFile, Format.JSON_FILE);
    }
    
    public static Resource createXmlResource(final URL xmlUrl) throws IOException {
        return new Resource(xmlUrl, Format.XML_FILE);
    }
    
    public static Resource createIniResource(final URL iniUrl) throws IOException {
        return new Resource(iniUrl, Format.INI_FILE);
    }
    
    public static Resource createJsonResource(final URL jsonUrl) throws IOException {
        return new Resource(jsonUrl, Format.JSON_FILE);
    }
    
    public static Resource createMapResource(final Map<String, Object> mapConfig) {
        return new Resource("mapConfig", mapConfig, Format.MAP);
    }
    
    public static Resource createPropertiesFileResource(final File propFile) throws IOException {
        return new Resource(propFile.getName(), propFile, Format.PROPERTIES_FILE);
    }
    
    public static Resource createPropertiesResource(final Properties propertiesConfig) {
        return new Resource("propConfig", propertiesConfig, Format.PROPERTIES);
    }
    
    private Resource(final String name, final File resourceFile, final Format format) throws IOException {
        this.name = name;
        this.resource = Files.newInputStream(resourceFile.toPath(), new OpenOption[0]);
        this.format = format;
    }
    
    private Resource(final URL resourceUrl, final Format format) throws IOException {
        this(resourceUrl.toString(), resourceUrl.openStream(), format);
    }
    
    private Resource(final String name, final Object resourceStream, final Format format) {
        this.name = name;
        this.resource = resourceStream;
        this.format = format;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getResource() {
        return this.resource;
    }
    
    public Format getFormat() {
        return this.format;
    }
    
    public enum Format
    {
        XML_FILE((Class<? extends ConfigLoader>)XmlConfigLoader.class), 
        INI_FILE((Class<? extends ConfigLoader>)IniConfigLoader.class), 
        JSON_FILE((Class<? extends ConfigLoader>)JsonConfigLoader.class), 
        PROPERTIES_FILE((Class<? extends ConfigLoader>)PropertiesFileConfigLoader.class), 
        MAP((Class<? extends ConfigLoader>)MapConfigLoader.class), 
        PROPERTIES((Class<? extends ConfigLoader>)PropertiesConfigLoader.class);
        
        private Class<? extends ConfigLoader> loaderClass;
        
        private Format(final Class<? extends ConfigLoader> loaderClass) {
            this.loaderClass = loaderClass;
        }
        
        public Class<? extends ConfigLoader> getLoaderClass() {
            return this.loaderClass;
        }
    }
}
