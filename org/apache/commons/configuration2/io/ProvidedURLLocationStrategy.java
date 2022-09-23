// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.net.URL;

public class ProvidedURLLocationStrategy implements FileLocationStrategy
{
    @Override
    public URL locate(final FileSystem fileSystem, final FileLocator locator) {
        return locator.getSourceURL();
    }
}
