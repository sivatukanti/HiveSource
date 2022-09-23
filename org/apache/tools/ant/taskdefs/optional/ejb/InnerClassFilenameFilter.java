// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FilenameFilter;

public class InnerClassFilenameFilter implements FilenameFilter
{
    private String baseClassName;
    
    InnerClassFilenameFilter(final String baseclass) {
        int extidx = baseclass.lastIndexOf(".class");
        if (extidx == -1) {
            extidx = baseclass.length() - 1;
        }
        this.baseClassName = baseclass.substring(0, extidx);
    }
    
    public boolean accept(final File dir, final String filename) {
        return filename.lastIndexOf(".") == filename.lastIndexOf(".class") && filename.indexOf(this.baseClassName + "$") == 0;
    }
}
