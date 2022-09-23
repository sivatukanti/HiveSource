// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.util.Iterator;
import java.net.URL;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;

public class CombinedLocationStrategy implements FileLocationStrategy
{
    private final Collection<FileLocationStrategy> subStrategies;
    
    public CombinedLocationStrategy(final Collection<? extends FileLocationStrategy> subs) {
        if (subs == null) {
            throw new IllegalArgumentException("Collection with sub strategies must not be null!");
        }
        this.subStrategies = Collections.unmodifiableCollection((Collection<? extends FileLocationStrategy>)new ArrayList<FileLocationStrategy>(subs));
        if (this.subStrategies.contains(null)) {
            throw new IllegalArgumentException("Collection with sub strategies contains null entry!");
        }
    }
    
    public Collection<FileLocationStrategy> getSubStrategies() {
        return this.subStrategies;
    }
    
    @Override
    public URL locate(final FileSystem fileSystem, final FileLocator locator) {
        for (final FileLocationStrategy sub : this.getSubStrategies()) {
            final URL url = sub.locate(fileSystem, locator);
            if (url != null) {
                return url;
            }
        }
        return null;
    }
}
