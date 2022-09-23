// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import java.net.URL;

public class AbsoluteNameLocationStrategy implements FileLocationStrategy
{
    @Override
    public URL locate(final FileSystem fileSystem, final FileLocator locator) {
        if (StringUtils.isNotEmpty(locator.getFileName())) {
            final File file = new File(locator.getFileName());
            if (file.isAbsolute() && file.exists()) {
                return FileLocatorUtils.convertFileToURL(file);
            }
        }
        return null;
    }
}
