// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Task;

public class Basename extends Task
{
    private File file;
    private String property;
    private String suffix;
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.property == null) {
            throw new BuildException("property attribute required", this.getLocation());
        }
        if (this.file == null) {
            throw new BuildException("file attribute required", this.getLocation());
        }
        String value = this.file.getName();
        if (this.suffix != null && value.endsWith(this.suffix)) {
            int pos = value.length() - this.suffix.length();
            if (pos > 0 && this.suffix.charAt(0) != '.' && value.charAt(pos - 1) == '.') {
                --pos;
            }
            value = value.substring(0, pos);
        }
        this.getProject().setNewProperty(this.property, value);
    }
}
