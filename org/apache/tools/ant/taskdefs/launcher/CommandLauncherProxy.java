// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.launcher;

import java.io.IOException;
import org.apache.tools.ant.Project;

public class CommandLauncherProxy extends CommandLauncher
{
    private final CommandLauncher myLauncher;
    
    protected CommandLauncherProxy(final CommandLauncher launcher) {
        this.myLauncher = launcher;
    }
    
    @Override
    public Process exec(final Project project, final String[] cmd, final String[] env) throws IOException {
        return this.myLauncher.exec(project, cmd, env);
    }
}
