// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.event;

public interface EventSource
{
     <T extends Event> void addEventListener(final EventType<T> p0, final EventListener<? super T> p1);
    
     <T extends Event> boolean removeEventListener(final EventType<T> p0, final EventListener<? super T> p1);
}
