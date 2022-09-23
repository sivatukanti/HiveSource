// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.File;

public interface XSLTLiaison
{
    public static final String FILE_PROTOCOL_PREFIX = "file://";
    
    void setStylesheet(final File p0) throws Exception;
    
    void addParam(final String p0, final String p1) throws Exception;
    
    void transform(final File p0, final File p1) throws Exception;
}
