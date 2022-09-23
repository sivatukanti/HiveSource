// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.ArrayList;
import org.apache.hadoop.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public enum StorageType
{
    RAM_DISK(true), 
    SSD(false), 
    DISK(false), 
    ARCHIVE(false), 
    PROVIDED(false);
    
    private final boolean isTransient;
    public static final StorageType DEFAULT;
    public static final StorageType[] EMPTY_ARRAY;
    private static final StorageType[] VALUES;
    
    private StorageType(final boolean isTransient) {
        this.isTransient = isTransient;
    }
    
    public boolean isTransient() {
        return this.isTransient;
    }
    
    public boolean supportTypeQuota() {
        return !this.isTransient;
    }
    
    public boolean isMovable() {
        return !this.isTransient;
    }
    
    public static List<StorageType> asList() {
        return Arrays.asList(StorageType.VALUES);
    }
    
    public static List<StorageType> getMovableTypes() {
        return getNonTransientTypes();
    }
    
    public static List<StorageType> getTypesSupportingQuota() {
        return getNonTransientTypes();
    }
    
    public static StorageType parseStorageType(final int i) {
        return StorageType.VALUES[i];
    }
    
    public static StorageType parseStorageType(final String s) {
        return valueOf(StringUtils.toUpperCase(s));
    }
    
    private static List<StorageType> getNonTransientTypes() {
        final List<StorageType> nonTransientTypes = new ArrayList<StorageType>();
        for (final StorageType t : StorageType.VALUES) {
            if (!t.isTransient) {
                nonTransientTypes.add(t);
            }
        }
        return nonTransientTypes;
    }
    
    static {
        DEFAULT = StorageType.DISK;
        EMPTY_ARRAY = new StorageType[0];
        VALUES = values();
    }
}
