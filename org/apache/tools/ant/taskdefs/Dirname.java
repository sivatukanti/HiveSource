// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Task;

public class Dirname extends Task
{
    private File file;
    private String property;
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute required", this.getLocation());
        }
        if (this.file == null) {
            throw new BuildException("file attribute required", this.getLocation());
        }
        final String value = this.file.getParent();
        this.getProject().setNewProperty(this.property, value);
    }
}
