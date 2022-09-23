// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.taskdefs.condition.Os;
import java.io.IOException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.PatternSet;
import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class Chmod extends ExecuteOn
{
    private FileSet defaultSet;
    private boolean defaultSetDefined;
    private boolean havePerm;
    
    public Chmod() {
        this.defaultSet = new FileSet();
        this.defaultSetDefined = false;
        this.havePerm = false;
        super.setExecutable("chmod");
        super.setParallel(true);
        super.setSkipEmptyFilesets(true);
    }
    
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        this.defaultSet.setProject(project);
    }
    
    public void setFile(final File src) {
        final FileSet fs = new FileSet();
        fs.setFile(src);
        this.addFileset(fs);
    }
    
    @Override
    public void setDir(final File src) {
        this.defaultSet.setDir(src);
    }
    
    public void setPerm(final String perm) {
        this.createArg().setValue(perm);
        this.havePerm = true;
    }
    
    public PatternSet.NameEntry createInclude() {
        this.defaultSetDefined = true;
        return this.defaultSet.createInclude();
    }
    
    public PatternSet.NameEntry createExclude() {
        this.defaultSetDefined = true;
        return this.defaultSet.createExclude();
    }
    
    public PatternSet createPatternSet() {
        this.defaultSetDefined = true;
        return this.defaultSet.createPatternSet();
    }
    
    public void setIncludes(final String includes) {
        this.defaultSetDefined = true;
        this.defaultSet.setIncludes(includes);
    }
    
    public void setExcludes(final String excludes) {
        this.defaultSetDefined = true;
        this.defaultSet.setExcludes(excludes);
    }
    
    public void setDefaultexcludes(final boolean useDefaultExcludes) {
        this.defaultSetDefined = true;
        this.defaultSet.setDefaultexcludes(useDefaultExcludes);
    }
    
    @Override
    protected void checkConfiguration() {
        if (!this.havePerm) {
            throw new BuildException("Required attribute perm not set in chmod", this.getLocation());
        }
        if (this.defaultSetDefined && this.defaultSet.getDir(this.getProject()) != null) {
            this.addFileset(this.defaultSet);
        }
        super.checkConfiguration();
    }
    
    @Override
    public void execute() throws BuildException {
        if (!this.defaultSetDefined) {
            if (this.defaultSet.getDir(this.getProject()) != null) {
                if (this.isValidOs()) {
                    final Execute execute = this.prepareExec();
                    final Commandline cloned = (Commandline)this.cmdl.clone();
                    cloned.createArgument().setValue(this.defaultSet.getDir(this.getProject()).getPath());
                    try {
                        execute.setCommandline(cloned.getCommandline());
                        this.runExecute(execute);
                    }
                    catch (IOException e) {
                        throw new BuildException("Execute failed: " + e, e, this.getLocation());
                    }
                    finally {
                        this.logFlush();
                    }
                }
                return;
            }
        }
        try {
            super.execute();
        }
        finally {
            if (this.defaultSetDefined && this.defaultSet.getDir(this.getProject()) != null) {
                this.filesets.removeElement(this.defaultSet);
            }
        }
    }
    
    @Override
    public void setExecutable(final String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable attribute", this.getLocation());
    }
    
    @Override
    public void setCommand(final Commandline cmdl) {
        throw new BuildException(this.getTaskType() + " doesn't support the command attribute", this.getLocation());
    }
    
    @Override
    public void setSkipEmptyFilesets(final boolean skip) {
        throw new BuildException(this.getTaskType() + " doesn't support the skipemptyfileset attribute", this.getLocation());
    }
    
    @Override
    public void setAddsourcefile(final boolean b) {
        throw new BuildException(this.getTaskType() + " doesn't support the addsourcefile attribute", this.getLocation());
    }
    
    @Override
    protected boolean isValidOs() {
        return (this.getOs() == null && this.getOsFamily() == null) ? Os.isFamily("unix") : super.isValidOs();
    }
}
