// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;

public interface CompilerAdapter
{
    void setJavac(final Javac p0);
    
    boolean execute() throws BuildException;
}
