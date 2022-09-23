// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class DefaultExcludes extends Task
{
    private String add;
    private String remove;
    private boolean defaultrequested;
    private boolean echo;
    private int logLevel;
    
    public DefaultExcludes() {
        this.add = "";
        this.remove = "";
        this.defaultrequested = false;
        this.echo = false;
        this.logLevel = 1;
    }
    
    @Override
    public void execute() throws BuildException {
        if (!this.defaultrequested && this.add.equals("") && this.remove.equals("") && !this.echo) {
            throw new BuildException("<defaultexcludes> task must set at least one attribute (echo=\"false\" doesn't count since that is the default");
        }
        if (this.defaultrequested) {
            DirectoryScanner.resetDefaultExcludes();
        }
        if (!this.add.equals("")) {
            DirectoryScanner.addDefaultExclude(this.add);
        }
        if (!this.remove.equals("")) {
            DirectoryScanner.removeDefaultExclude(this.remove);
        }
        if (this.echo) {
            final StringBuffer message = new StringBuffer("Current Default Excludes:");
            message.append(StringUtils.LINE_SEP);
            final String[] excludes = DirectoryScanner.getDefaultExcludes();
            for (int i = 0; i < excludes.length; ++i) {
                message.append("  ");
                message.append(excludes[i]);
                message.append(StringUtils.LINE_SEP);
            }
            this.log(message.toString(), this.logLevel);
        }
    }
    
    public void setDefault(final boolean def) {
        this.defaultrequested = def;
    }
    
    public void setAdd(final String add) {
        this.add = add;
    }
    
    public void setRemove(final String remove) {
        this.remove = remove;
    }
    
    public void setEcho(final boolean echo) {
        this.echo = echo;
    }
}
