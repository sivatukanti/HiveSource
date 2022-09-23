// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Collections;
import java.util.Iterator;

class EmptyStorageStatistics extends StorageStatistics
{
    EmptyStorageStatistics(final String name) {
        super(name);
    }
    
    @Override
    public Iterator<LongStatistic> getLongStatistics() {
        return Collections.emptyIterator();
    }
    
    @Override
    public Long getLong(final String key) {
        return null;
    }
    
    @Override
    public boolean isTracked(final String key) {
        return false;
    }
    
    @Override
    public void reset() {
    }
}
