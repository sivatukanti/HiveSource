// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.File;

public class UnPackageNameMapper extends GlobPatternMapper
{
    @Override
    protected String extractVariablePart(final String name) {
        final String var = name.substring(this.prefixLength, name.length() - this.postfixLength);
        return var.replace('.', File.separatorChar);
    }
}
