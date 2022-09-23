// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import org.apache.commons.lang3.StringUtils;
import java.net.URL;

public class ClasspathLocationStrategy implements FileLocationStrategy
{
    @Override
    public URL locate(final FileSystem fileSystem, final FileLocator locator) {
        return StringUtils.isEmpty(locator.getFileName()) ? null : FileLocatorUtils.locateFromClasspath(locator.getFileName());
    }
}
