// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Rmic;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.util.JavaEnvUtils;

public class ForkingSunRmic extends DefaultRmicAdapter
{
    public static final String COMPILER_NAME = "forking";
    
    public boolean execute() throws BuildException {
        final Rmic owner = this.getRmic();
        final Commandline cmd = this.setupRmicCommand();
        final Project project = owner.getProject();
        String executable = owner.getExecutable();
        if (executable == null) {
            executable = JavaEnvUtils.getJdkExecutable(this.getExecutableName());
        }
        cmd.setExecutable(executable);
        final String[] args = cmd.getCommandline();
        try {
            final Execute exe = new Execute(new LogStreamHandler(owner, 2, 1));
            exe.setAntRun(project);
            exe.setWorkingDirectory(project.getBaseDir());
            exe.setCommandline(args);
            exe.execute();
            return !exe.isFailure();
        }
        catch (IOException exception) {
            throw new BuildException("Error running " + this.getExecutableName() + " -maybe it is not on the path", exception);
        }
    }
    
    protected String getExecutableName() {
        return "rmic";
    }
}
