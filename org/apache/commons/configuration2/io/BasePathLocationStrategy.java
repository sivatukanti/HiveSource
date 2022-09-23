// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import java.net.URL;

public class BasePathLocationStrategy implements FileLocationStrategy
{
    @Override
    public URL locate(final FileSystem fileSystem, final FileLocator locator) {
        if (StringUtils.isNotEmpty(locator.getFileName())) {
            final File file = FileLocatorUtils.constructFile(locator.getBasePath(), locator.getFileName());
            if (file.isFile()) {
                return FileLocatorUtils.convertFileToURL(file);
            }
        }
        return null;
    }
}
