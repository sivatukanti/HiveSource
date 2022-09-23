// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

public enum CompressionType
{
    NONE(0), 
    SNAPPY(1);
    
    private final int persistentId;
    
    public static CompressionType getCompressionTypeByPersistentId(final int persistentId) {
        for (final CompressionType compressionType : values()) {
            if (compressionType.persistentId == persistentId) {
                return compressionType;
            }
        }
        throw new IllegalArgumentException("Unknown persistentId " + persistentId);
    }
    
    private CompressionType(final int persistentId) {
        this.persistentId = persistentId;
    }
    
    public int persistentId() {
        return this.persistentId;
    }
}
