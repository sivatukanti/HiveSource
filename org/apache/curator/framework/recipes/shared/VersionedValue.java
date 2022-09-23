// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.shared;

import org.apache.curator.shaded.com.google.common.base.Preconditions;

public class VersionedValue<T>
{
    private final int version;
    private final T value;
    
    VersionedValue(final int version, final T value) {
        this.version = version;
        this.value = Preconditions.checkNotNull(value, (Object)"value cannot be null");
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public T getValue() {
        return this.value;
    }
}
