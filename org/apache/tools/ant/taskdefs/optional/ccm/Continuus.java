// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.taskdefs.LogStreamHandler;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public abstract class Continuus extends Task
{
    private String ccmDir;
    private String ccmAction;
    private static final String CCM_EXE = "ccm";
    public static final String COMMAND_CREATE_TASK = "create_task";
    public static final String COMMAND_CHECKOUT = "co";
    public static final String COMMAND_CHECKIN = "ci";
    public static final String COMMAND_RECONFIGURE = "reconfigure";
    public static final String COMMAND_DEFAULT_TASK = "default_task";
    
    public Continuus() {
        this.ccmDir = "";
        this.ccmAction = "";
    }
    
    public String getCcmAction() {
        return this.ccmAction;
    }
    
    public void setCcmAction(final String v) {
        this.ccmAction = v;
    }
    
    public final void setCcmDir(final String dir) {
        this.ccmDir = FileUtils.translatePath(dir);
    }
    
    protected final String getCcmCommand() {
        String toReturn = this.ccmDir;
        if (!toReturn.equals("") && !toReturn.endsWith("/")) {
            toReturn += "/";
        }
        toReturn += "ccm";
        return toReturn;
    }
    
    protected int run(final Commandline cmd, final ExecuteStreamHandler handler) {
        try {
            final Execute exe = new Execute(handler);
            exe.setAntRun(this.getProject());
            exe.setWorkingDirectory(this.getProject().getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            return exe.execute();
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
    
    protected int run(final Commandline cmd) {
        return this.run(cmd, new LogStreamHandler(this, 3, 1));
    }
}
