// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.launcher.CommandLauncher;
import org.apache.tools.ant.Task;

public class CommandLauncherTask extends Task
{
    private boolean vmLauncher;
    private CommandLauncher commandLauncher;
    
    public synchronized void addConfigured(final CommandLauncher commandLauncher) {
        if (this.commandLauncher != null) {
            throw new BuildException("Only one CommandLauncher can be installed");
        }
        this.commandLauncher = commandLauncher;
    }
    
    @Override
    public void execute() {
        if (this.commandLauncher != null) {
            if (this.vmLauncher) {
                CommandLauncher.setVMLauncher(this.getProject(), this.commandLauncher);
            }
            else {
                CommandLauncher.setShellLauncher(this.getProject(), this.commandLauncher);
            }
        }
    }
    
    public void setVmLauncher(final boolean vmLauncher) {
        this.vmLauncher = vmLauncher;
    }
}
