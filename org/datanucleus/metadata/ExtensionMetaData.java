// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import java.io.Serializable;

public class ExtensionMetaData implements Serializable
{
    protected String vendorName;
    protected String key;
    protected String value;
    
    public ExtensionMetaData(final String vendorName, final String key, final String value) {
        this.vendorName = (StringUtils.isWhitespace(vendorName) ? null : vendorName);
        this.key = (StringUtils.isWhitespace(key) ? null : key);
        this.value = (StringUtils.isWhitespace(value) ? null : value);
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String getVendorName() {
        return this.vendorName;
    }
    
    @Override
    public String toString() {
        return "<extension vendor-name=\"" + this.vendorName + "\" key=\"" + this.key + "\" value=\"" + this.value + "\"/>";
    }
}
