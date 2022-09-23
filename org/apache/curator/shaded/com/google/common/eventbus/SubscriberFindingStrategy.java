// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.eventbus;

import org.apache.curator.shaded.com.google.common.collect.Multimap;

interface SubscriberFindingStrategy
{
    Multimap<Class<?>, EventSubscriber> findAllSubscribers(final Object p0);
}
