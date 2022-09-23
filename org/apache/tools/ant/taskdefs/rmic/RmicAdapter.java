// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Rmic;

public interface RmicAdapter
{
    void setRmic(final Rmic p0);
    
    boolean execute() throws BuildException;
    
    FileNameMapper getMapper();
    
    Path getClasspath();
}
