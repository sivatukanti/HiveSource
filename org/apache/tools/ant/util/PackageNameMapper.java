// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.File;

public class PackageNameMapper extends GlobPatternMapper
{
    @Override
    protected String extractVariablePart(final String name) {
        String var = name.substring(this.prefixLength, name.length() - this.postfixLength);
        if (this.getHandleDirSep()) {
            var = var.replace('/', '.').replace('\\', '.');
        }
        return var.replace(File.separatorChar, '.');
    }
}
