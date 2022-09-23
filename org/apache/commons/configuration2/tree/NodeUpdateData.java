// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;
import java.util.Map;

public class NodeUpdateData<T>
{
    private final Map<QueryResult<T>, Object> changedValues;
    private final Collection<Object> newValues;
    private final Collection<QueryResult<T>> removedNodes;
    private final String key;
    
    public NodeUpdateData(final Map<QueryResult<T>, Object> changedValues, final Collection<Object> newValues, final Collection<QueryResult<T>> removedNodes, final String key) {
        this.changedValues = copyMap((Map<? extends QueryResult<T>, ?>)changedValues);
        this.newValues = copyCollection((Collection<?>)newValues);
        this.removedNodes = copyCollection((Collection<? extends QueryResult<T>>)removedNodes);
        this.key = key;
    }
    
    public Map<QueryResult<T>, Object> getChangedValues() {
        return this.changedValues;
    }
    
    public Collection<Object> getNewValues() {
        return this.newValues;
    }
    
    public Collection<QueryResult<T>> getRemovedNodes() {
        return this.removedNodes;
    }
    
    public String getKey() {
        return this.key;
    }
    
    private static <K, V> Map<K, V> copyMap(final Map<? extends K, ? extends V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)new HashMap<K, V>(map));
    }
    
    private static <T> Collection<T> copyCollection(final Collection<? extends T> col) {
        if (col == null) {
            return (Collection<T>)Collections.emptySet();
        }
        return Collections.unmodifiableCollection((Collection<? extends T>)new ArrayList<T>(col));
    }
}
