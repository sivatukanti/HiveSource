// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.xml.sax.Attributes;

public class BaseConfigurationXMLReader extends ConfigurationXMLReader
{
    private Configuration config;
    
    public BaseConfigurationXMLReader() {
    }
    
    public BaseConfigurationXMLReader(final Configuration conf) {
        this();
        this.setConfiguration(conf);
    }
    
    public Configuration getConfiguration() {
        return this.config;
    }
    
    public void setConfiguration(final Configuration conf) {
        this.config = conf;
    }
    
    @Override
    public Configuration getParsedConfiguration() {
        return this.getConfiguration();
    }
    
    @Override
    protected void processKeys() {
        this.fireElementStart(this.getRootName(), null);
        new SAXConverter().process(this.getConfiguration());
        this.fireElementEnd(this.getRootName());
    }
    
    class SAXConverter extends HierarchicalConfigurationConverter
    {
        @Override
        protected void elementStart(final String name, final Object value) {
            BaseConfigurationXMLReader.this.fireElementStart(name, null);
            if (value != null) {
                BaseConfigurationXMLReader.this.fireCharacters(value.toString());
            }
        }
        
        @Override
        protected void elementEnd(final String name) {
            BaseConfigurationXMLReader.this.fireElementEnd(name);
        }
    }
}
