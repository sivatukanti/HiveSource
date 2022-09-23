// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;

public class SystemConfiguration extends MapConfiguration
{
    private static Log log;
    
    public SystemConfiguration() {
        super(System.getProperties());
    }
    
    public static void setSystemProperties(final String fileName) throws ConfigurationException {
        setSystemProperties(null, fileName);
    }
    
    public static void setSystemProperties(final String basePath, final String fileName) throws ConfigurationException {
        final FileBasedConfiguration config = fileName.endsWith(".xml") ? new XMLPropertiesConfiguration() : new PropertiesConfiguration();
        final FileHandler handler = new FileHandler(config);
        handler.setBasePath(basePath);
        handler.setFileName(fileName);
        handler.load();
        setSystemProperties(config);
    }
    
    public static void setSystemProperties(final Configuration systemConfig) {
        final Iterator<String> iter = systemConfig.getKeys();
        while (iter.hasNext()) {
            final String key = iter.next();
            final String value = (String)systemConfig.getProperty(key);
            if (SystemConfiguration.log.isDebugEnabled()) {
                SystemConfiguration.log.debug("Setting system property " + key + " to " + value);
            }
            System.setProperty(key, value);
        }
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return System.getProperties().stringPropertyNames().iterator();
    }
    
    static {
        SystemConfiguration.log = LogFactory.getLog(SystemConfiguration.class);
    }
}
