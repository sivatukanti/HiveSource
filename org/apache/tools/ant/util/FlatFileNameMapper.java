// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.File;

public class FlatFileNameMapper implements FileNameMapper
{
    public void setFrom(final String from) {
    }
    
    public void setTo(final String to) {
    }
    
    public String[] mapFileName(final String sourceFileName) {
        return new String[] { new File(sourceFileName).getName() };
    }
}
