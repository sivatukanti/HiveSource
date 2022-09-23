// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;

public class Jvc extends DefaultCompilerAdapter
{
    public boolean execute() throws BuildException {
        this.attributes.log("Using jvc compiler", 3);
        final Path classpath = new Path(this.project);
        final Path p = this.getBootClassPath();
        if (p.size() > 0) {
            classpath.append(p);
        }
        if (this.includeJavaRuntime) {
            classpath.addExtdirs(this.extdirs);
        }
        classpath.append(this.getCompileClasspath());
        if (this.compileSourcepath != null) {
            classpath.append(this.compileSourcepath);
        }
        else {
            classpath.append(this.src);
        }
        final Commandline cmd = new Commandline();
        final String exec = this.getJavac().getExecutable();
        cmd.setExecutable((exec == null) ? "jvc" : exec);
        if (this.destDir != null) {
            cmd.createArgument().setValue("/d");
            cmd.createArgument().setFile(this.destDir);
        }
        cmd.createArgument().setValue("/cp:p");
        cmd.createArgument().setPath(classpath);
        boolean msExtensions = true;
        final String mse = this.getProject().getProperty("build.compiler.jvc.extensions");
        if (mse != null) {
            msExtensions = Project.toBoolean(mse);
        }
        if (msExtensions) {
            cmd.createArgument().setValue("/x-");
            cmd.createArgument().setValue("/nomessage");
        }
        cmd.createArgument().setValue("/nologo");
        if (this.debug) {
            cmd.createArgument().setValue("/g");
        }
        if (this.optimize) {
            cmd.createArgument().setValue("/O");
        }
        if (this.verbose) {
            cmd.createArgument().setValue("/verbose");
        }
        this.addCurrentCompilerArgs(cmd);
        final int firstFileName = cmd.size();
        this.logAndAddFilesToCompile(cmd);
        return this.executeExternalCompile(cmd.getCommandline(), firstFileName, false) == 0;
    }
}
