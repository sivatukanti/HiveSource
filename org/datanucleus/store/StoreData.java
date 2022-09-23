// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import java.util.HashMap;
import org.datanucleus.metadata.MetaData;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class StoreData
{
    protected static final Localiser LOCALISER;
    public static final int FCO_TYPE = 1;
    public static final int SCO_TYPE = 2;
    protected final String name;
    protected final int type;
    protected Map properties;
    
    public StoreData(final String name, final int type) {
        this(name, null, type, null);
    }
    
    public StoreData(final String name, final MetaData metadata, final int type, final String interfaceName) {
        this.properties = new HashMap();
        this.name = name;
        this.type = type;
        if (metadata != null) {
            this.addProperty("metadata", metadata);
        }
        if (interfaceName != null) {
            this.addProperty("interface-name", interfaceName);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public MetaData getMetaData() {
        return this.properties.get("metadata");
    }
    
    public void setMetaData(final MetaData md) {
        this.addProperty("metadata", md);
    }
    
    public boolean isFCO() {
        return this.type == 1;
    }
    
    public boolean isSCO() {
        return this.type == 2;
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getInterfaceName() {
        return this.properties.get("interface-name");
    }
    
    public void addProperty(final String key, final Object value) {
        this.properties.put(key, value);
    }
    
    public Map getProperties() {
        return this.properties;
    }
    
    @Override
    public String toString() {
        final MetaData metadata = this.getMetaData();
        if (metadata instanceof ClassMetaData) {
            final ClassMetaData cmd = (ClassMetaData)metadata;
            return StoreData.LOCALISER.msg("035004", this.name, "(none)", cmd.getInheritanceMetaData().getStrategy().toString());
        }
        if (metadata instanceof AbstractMemberMetaData) {
            return StoreData.LOCALISER.msg("035003", this.name, null);
        }
        return StoreData.LOCALISER.msg("035002", this.name, null);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
