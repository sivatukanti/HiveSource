// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;

public class JavacExternal extends DefaultCompilerAdapter
{
    public boolean execute() throws BuildException {
        this.attributes.log("Using external javac compiler", 3);
        final Commandline cmd = new Commandline();
        cmd.setExecutable(this.getJavac().getJavacExecutable());
        if (!this.assumeJava11() && !this.assumeJava12()) {
            this.setupModernJavacCommandlineSwitches(cmd);
        }
        else {
            this.setupJavacCommandlineSwitches(cmd, true);
        }
        final int firstFileName = this.assumeJava11() ? -1 : cmd.size();
        this.logAndAddFilesToCompile(cmd);
        if (Os.isFamily("openvms")) {
            return this.execOnVMS(cmd, firstFileName);
        }
        return this.executeExternalCompile(cmd.getCommandline(), firstFileName, true) == 0;
    }
    
    private boolean execOnVMS(final Commandline cmd, final int firstFileName) {
        File vmsFile = null;
        try {
            vmsFile = JavaEnvUtils.createVmsJavaOptionFile(cmd.getArguments());
            final String[] commandLine = { cmd.getExecutable(), "-V", vmsFile.getPath() };
            return 0 == this.executeExternalCompile(commandLine, firstFileName, true);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create a temporary file for \"-V\" switch");
        }
        finally {
            FileUtils.delete(vmsFile);
        }
    }
}
