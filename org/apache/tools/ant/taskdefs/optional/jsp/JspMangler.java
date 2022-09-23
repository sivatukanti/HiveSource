// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;

public interface JspMangler
{
    String mapJspToJavaName(final File p0);
    
    String mapPath(final String p0);
}
