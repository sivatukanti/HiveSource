// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class Gcj extends DefaultCompilerAdapter
{
    private static final String[] CONFLICT_WITH_DASH_C;
    
    public boolean execute() throws BuildException {
        this.attributes.log("Using gcj compiler", 3);
        final Commandline cmd = this.setupGCJCommand();
        final int firstFileName = cmd.size();
        this.logAndAddFilesToCompile(cmd);
        return this.executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
    }
    
    protected Commandline setupGCJCommand() {
        final Commandline cmd = new Commandline();
        final Path classpath = new Path(this.project);
        final Path p = this.getBootClassPath();
        if (p.size() > 0) {
            classpath.append(p);
        }
        if (this.extdirs != null || this.includeJavaRuntime) {
            classpath.addExtdirs(this.extdirs);
        }
        classpath.append(this.getCompileClasspath());
        if (this.compileSourcepath != null) {
            classpath.append(this.compileSourcepath);
        }
        else {
            classpath.append(this.src);
        }
        final String exec = this.getJavac().getExecutable();
        cmd.setExecutable((exec == null) ? "gcj" : exec);
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir);
            if (!this.destDir.exists() && !this.destDir.mkdirs()) {
                throw new BuildException("Can't make output directories. Maybe permission is wrong. ");
            }
        }
        cmd.createArgument().setValue("-classpath");
        cmd.createArgument().setPath(classpath);
        if (this.encoding != null) {
            cmd.createArgument().setValue("--encoding=" + this.encoding);
        }
        if (this.debug) {
            cmd.createArgument().setValue("-g1");
        }
        if (this.optimize) {
            cmd.createArgument().setValue("-O");
        }
        if (!this.isNativeBuild()) {
            cmd.createArgument().setValue("-C");
        }
        if (this.attributes.getSource() != null) {
            final String source = this.attributes.getSource();
            cmd.createArgument().setValue("-fsource=" + source);
        }
        if (this.attributes.getTarget() != null) {
            final String target = this.attributes.getTarget();
            cmd.createArgument().setValue("-ftarget=" + target);
        }
        this.addCurrentCompilerArgs(cmd);
        return cmd;
    }
    
    public boolean isNativeBuild() {
        boolean nativeBuild = false;
        final String[] additionalArguments = this.getJavac().getCurrentCompilerArgs();
        for (int argsLength = 0; !nativeBuild && argsLength < additionalArguments.length; ++argsLength) {
            for (int conflictLength = 0; !nativeBuild && conflictLength < Gcj.CONFLICT_WITH_DASH_C.length; nativeBuild = additionalArguments[argsLength].startsWith(Gcj.CONFLICT_WITH_DASH_C[conflictLength]), ++conflictLength) {}
        }
        return nativeBuild;
    }
    
    static {
        CONFLICT_WITH_DASH_C = new String[] { "-o", "--main=", "-D", "-fjni", "-L" };
    }
}
