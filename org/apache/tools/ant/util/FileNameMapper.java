// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

public interface FileNameMapper
{
    void setFrom(final String p0);
    
    void setTo(final String p0);
    
    String[] mapFileName(final String p0);
}
