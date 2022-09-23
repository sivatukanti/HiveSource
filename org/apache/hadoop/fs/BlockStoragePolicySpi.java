// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface BlockStoragePolicySpi
{
    String getName();
    
    StorageType[] getStorageTypes();
    
    StorageType[] getCreationFallbacks();
    
    StorageType[] getReplicationFallbacks();
    
    boolean isCopyOnCreateFile();
}
