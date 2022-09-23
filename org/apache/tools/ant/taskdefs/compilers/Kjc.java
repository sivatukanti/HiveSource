// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.ExecuteJava;

public class Kjc extends DefaultCompilerAdapter
{
    public boolean execute() throws BuildException {
        this.attributes.log("Using kjc compiler", 3);
        final Commandline cmd = this.setupKjcCommand();
        cmd.setExecutable("at.dms.kjc.Main");
        final ExecuteJava ej = new ExecuteJava();
        ej.setJavaCommand(cmd);
        return ej.fork(this.getJavac()) == 0;
    }
    
    protected Commandline setupKjcCommand() {
        final Commandline cmd = new Commandline();
        final Path classpath = this.getCompileClasspath();
        if (this.deprecation) {
            cmd.createArgument().setValue("-deprecation");
        }
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir);
        }
        cmd.createArgument().setValue("-classpath");
        final Path cp = new Path(this.project);
        final Path p = this.getBootClassPath();
        if (p.size() > 0) {
            cp.append(p);
        }
        if (this.extdirs != null) {
            cp.addExtdirs(this.extdirs);
        }
        cp.append(classpath);
        if (this.compileSourcepath != null) {
            cp.append(this.compileSourcepath);
        }
        else {
            cp.append(this.src);
        }
        cmd.createArgument().setPath(cp);
        if (this.encoding != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(this.encoding);
        }
        if (this.debug) {
            cmd.createArgument().setValue("-g");
        }
        if (this.optimize) {
            cmd.createArgument().setValue("-O2");
        }
        if (this.verbose) {
            cmd.createArgument().setValue("-verbose");
        }
        this.addCurrentCompilerArgs(cmd);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }
}
