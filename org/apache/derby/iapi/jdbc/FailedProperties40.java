// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.util.Map;
import java.util.Enumeration;
import java.util.Properties;
import java.sql.ClientInfoStatus;
import java.util.HashMap;

public class FailedProperties40
{
    private final HashMap<String, ClientInfoStatus> failedProps_;
    private final String firstKey_;
    private final String firstValue_;
    
    public static Properties makeProperties(final String key, final String value) {
        final Properties properties = new Properties();
        if (key != null || value != null) {
            properties.setProperty(key, value);
        }
        return properties;
    }
    
    public FailedProperties40(final Properties properties) {
        this.failedProps_ = new HashMap<String, ClientInfoStatus>();
        if (properties == null || properties.isEmpty()) {
            this.firstKey_ = null;
            this.firstValue_ = null;
            return;
        }
        final Enumeration<Object> keys = properties.keys();
        this.firstKey_ = keys.nextElement();
        this.firstValue_ = properties.getProperty(this.firstKey_);
        this.failedProps_.put(this.firstKey_, ClientInfoStatus.REASON_UNKNOWN_PROPERTY);
        while (keys.hasMoreElements()) {
            this.failedProps_.put(keys.nextElement(), ClientInfoStatus.REASON_UNKNOWN_PROPERTY);
        }
    }
    
    public Map<String, ClientInfoStatus> getProperties() {
        return this.failedProps_;
    }
    
    public String getFirstKey() {
        return this.firstKey_;
    }
    
    public String getFirstValue() {
        return this.firstValue_;
    }
}
