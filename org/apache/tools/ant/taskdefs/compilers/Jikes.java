// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline;

public class Jikes extends DefaultCompilerAdapter
{
    public boolean execute() throws BuildException {
        this.attributes.log("Using jikes compiler", 3);
        final Commandline cmd = new Commandline();
        Path sourcepath = null;
        if (this.compileSourcepath != null) {
            sourcepath = this.compileSourcepath;
        }
        else {
            sourcepath = this.src;
        }
        if (sourcepath.size() > 0) {
            cmd.createArgument().setValue("-sourcepath");
            cmd.createArgument().setPath(sourcepath);
        }
        final Path classpath = new Path(this.project);
        if (this.bootclasspath == null || this.bootclasspath.size() == 0) {
            this.includeJavaRuntime = true;
        }
        classpath.append(this.getCompileClasspath());
        final String jikesPath = System.getProperty("jikes.class.path");
        if (jikesPath != null) {
            classpath.append(new Path(this.project, jikesPath));
        }
        if (this.extdirs != null && this.extdirs.size() > 0) {
            cmd.createArgument().setValue("-extdirs");
            cmd.createArgument().setPath(this.extdirs);
        }
        final String exec = this.getJavac().getExecutable();
        cmd.setExecutable((exec == null) ? "jikes" : exec);
        if (this.deprecation) {
            cmd.createArgument().setValue("-deprecation");
        }
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir);
        }
        cmd.createArgument().setValue("-classpath");
        cmd.createArgument().setPath(classpath);
        if (this.encoding != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(this.encoding);
        }
        if (this.debug) {
            final String debugLevel = this.attributes.getDebugLevel();
            if (debugLevel != null) {
                cmd.createArgument().setValue("-g:" + debugLevel);
            }
            else {
                cmd.createArgument().setValue("-g");
            }
        }
        else {
            cmd.createArgument().setValue("-g:none");
        }
        if (this.optimize) {
            cmd.createArgument().setValue("-O");
        }
        if (this.verbose) {
            cmd.createArgument().setValue("-verbose");
        }
        if (this.depend) {
            cmd.createArgument().setValue("-depend");
        }
        if (this.target != null) {
            cmd.createArgument().setValue("-target");
            cmd.createArgument().setValue(this.target);
        }
        this.addPropertyParams(cmd);
        if (this.attributes.getSource() != null) {
            cmd.createArgument().setValue("-source");
            final String source = this.attributes.getSource();
            if (source.equals("1.1") || source.equals("1.2")) {
                this.attributes.log("Jikes doesn't support '-source " + source + "', will use '-source 1.3' instead");
                cmd.createArgument().setValue("1.3");
            }
            else {
                cmd.createArgument().setValue(source);
            }
        }
        this.addCurrentCompilerArgs(cmd);
        final int firstFileName = cmd.size();
        final Path boot = this.getBootClassPath();
        if (boot.size() > 0) {
            cmd.createArgument().setValue("-bootclasspath");
            cmd.createArgument().setPath(boot);
        }
        this.logAndAddFilesToCompile(cmd);
        return this.executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
    }
    
    private void addPropertyParams(final Commandline cmd) {
        final String emacsProperty = this.project.getProperty("build.compiler.emacs");
        if (emacsProperty != null && Project.toBoolean(emacsProperty)) {
            cmd.createArgument().setValue("+E");
        }
        final String warningsProperty = this.project.getProperty("build.compiler.warnings");
        if (warningsProperty != null) {
            this.attributes.log("!! the build.compiler.warnings property is deprecated. !!", 1);
            this.attributes.log("!! Use the nowarn attribute instead. !!", 1);
            if (!Project.toBoolean(warningsProperty)) {
                cmd.createArgument().setValue("-nowarn");
            }
        }
        if (this.attributes.getNowarn()) {
            cmd.createArgument().setValue("-nowarn");
        }
        final String pedanticProperty = this.project.getProperty("build.compiler.pedantic");
        if (pedanticProperty != null && Project.toBoolean(pedanticProperty)) {
            cmd.createArgument().setValue("+P");
        }
        final String fullDependProperty = this.project.getProperty("build.compiler.fulldepend");
        if (fullDependProperty != null && Project.toBoolean(fullDependProperty)) {
            cmd.createArgument().setValue("+F");
        }
    }
}
