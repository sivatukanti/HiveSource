// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

import java.util.Collections;
import java.util.Set;
import java.util.Map;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;

public class BasePropertyStore extends PropertyStore
{
    public void setProperty(final String name, final Object value) {
        this.setPropertyInternal(name, value);
    }
    
    public void dump(final NucleusLogger logger) {
        logger.debug(">> BasePropertyStore : " + StringUtils.mapToString(this.properties));
    }
    
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet((Set<? extends String>)this.properties.keySet());
    }
    
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.properties);
    }
}
