// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.launcher;

import org.apache.tools.ant.BuildException;
import java.io.IOException;
import org.apache.tools.ant.types.Commandline;
import java.io.File;
import org.apache.tools.ant.Project;

public class Java13CommandLauncher extends CommandLauncher
{
    @Override
    public Process exec(final Project project, final String[] cmd, final String[] env, final File workingDir) throws IOException {
        try {
            if (project != null) {
                project.log("Execute:Java13CommandLauncher: " + Commandline.describeCommand(cmd), 4);
            }
            return Runtime.getRuntime().exec(cmd, env, workingDir);
        }
        catch (IOException ioex) {
            throw ioex;
        }
        catch (Exception exc) {
            throw new BuildException("Unable to execute command", exc);
        }
    }
}
