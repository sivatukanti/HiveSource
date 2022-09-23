// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Iterator;

public class FirstMatchMapper extends ContainerMapper
{
    public String[] mapFileName(final String sourceFileName) {
        for (final FileNameMapper mapper : this.getMappers()) {
            if (mapper != null) {
                final String[] mapped = mapper.mapFileName(sourceFileName);
                if (mapped != null) {
                    return mapped;
                }
                continue;
            }
        }
        return null;
    }
}
