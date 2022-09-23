// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

public interface EventListener<T extends Event>
{
    void onEvent(final T p0);
}
