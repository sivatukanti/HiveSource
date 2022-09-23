// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Diagnostics;
import org.apache.tools.ant.Task;

public class DiagnosticsTask extends Task
{
    private static final String[] ARGS;
    
    @Override
    public void execute() throws BuildException {
        Diagnostics.main(DiagnosticsTask.ARGS);
    }
    
    static {
        ARGS = new String[0];
    }
}
