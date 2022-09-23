// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;
import java.io.File;
import org.apache.tools.ant.taskdefs.ExecuteOn;

public abstract class AbstractAccessTask extends ExecuteOn
{
    public AbstractAccessTask() {
        super.setParallel(true);
        super.setSkipEmptyFilesets(true);
    }
    
    public void setFile(final File src) {
        final FileSet fs = new FileSet();
        fs.setFile(src);
        this.addFileset(fs);
    }
    
    @Override
    public void setCommand(final Commandline cmdl) {
        throw new BuildException(this.getTaskType() + " doesn't support the command attribute", this.getLocation());
    }
    
    @Override
    public void setSkipEmptyFilesets(final boolean skip) {
        throw new BuildException(this.getTaskType() + " doesn't support the " + "skipemptyfileset attribute", this.getLocation());
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
