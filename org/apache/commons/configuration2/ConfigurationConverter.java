// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import java.util.Properties;

public final class ConfigurationConverter
{
    private static final char DEFAULT_SEPARATOR = ',';
    
    private ConfigurationConverter() {
    }
    
    public static Configuration getConfiguration(final Properties props) {
        return new MapConfiguration(props);
    }
    
    public static Properties getProperties(final Configuration config) {
        final Properties props = new Properties();
        ListDelimiterHandler listHandler;
        boolean useDelimiterHandler;
        if (config instanceof AbstractConfiguration) {
            listHandler = ((AbstractConfiguration)config).getListDelimiterHandler();
            useDelimiterHandler = true;
        }
        else {
            listHandler = null;
            useDelimiterHandler = false;
        }
        final Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final List<Object> list = config.getList(key);
            String propValue;
            if (useDelimiterHandler) {
                try {
                    propValue = String.valueOf(listHandler.escapeList(list, ListDelimiterHandler.NOOP_TRANSFORMER));
                }
                catch (Exception ex) {
                    useDelimiterHandler = false;
                    propValue = listToString(list);
                }
            }
            else {
                propValue = listToString(list);
            }
            props.setProperty(key, propValue);
        }
        return props;
    }
    
    public static Map<Object, Object> getMap(final Configuration config) {
        return new ConfigurationMap(config);
    }
    
    private static String listToString(final List<?> list) {
        return StringUtils.join(list, ',');
    }
}
