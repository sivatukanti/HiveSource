// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.framework.listen.ListenerContainer;

public interface SharedValueReader
{
    byte[] getValue();
    
    VersionedValue<byte[]> getVersionedValue();
    
    ListenerContainer<SharedValueListener> getListenable();
}
