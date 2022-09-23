// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;

public interface JspCompilerAdapter
{
    void setJspc(final JspC p0);
    
    boolean execute() throws BuildException;
    
    JspMangler createMangler();
    
    boolean implementsOwnDependencyChecking();
}
