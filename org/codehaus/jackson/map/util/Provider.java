// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map.util;

import java.util.Collection;

public interface Provider<T>
{
    Collection<T> provide();
}
