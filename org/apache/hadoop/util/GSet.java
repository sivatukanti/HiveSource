// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public interface GSet<K, E extends K> extends Iterable<E>
{
    public static final Logger LOG = LoggerFactory.getLogger(GSet.class);
    
    int size();
    
    boolean contains(final K p0);
    
    E get(final K p0);
    
    E put(final E p0);
    
    E remove(final K p0);
    
    void clear();
    
    Collection<E> values();
}
