// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum VersionStrategy
{
    NONE("none"), 
    VERSION_NUMBER("version-number"), 
    DATE_TIME("date-time"), 
    STATE_IMAGE("state-image");
    
    String name;
    
    private VersionStrategy(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static VersionStrategy getVersionStrategy(final String value) {
        if (value == null) {
            return null;
        }
        if (VersionStrategy.NONE.toString().equalsIgnoreCase(value)) {
            return VersionStrategy.NONE;
        }
        if (VersionStrategy.STATE_IMAGE.toString().equalsIgnoreCase(value)) {
            return VersionStrategy.STATE_IMAGE;
        }
        if (VersionStrategy.DATE_TIME.toString().equalsIgnoreCase(value)) {
            return VersionStrategy.DATE_TIME;
        }
        if (VersionStrategy.VERSION_NUMBER.toString().equalsIgnoreCase(value)) {
            return VersionStrategy.VERSION_NUMBER;
        }
        return null;
    }
}
