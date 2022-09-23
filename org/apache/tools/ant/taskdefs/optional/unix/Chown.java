// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;

public class Chown extends AbstractAccessTask
{
    private boolean haveOwner;
    
    public Chown() {
        this.haveOwner = false;
        super.setExecutable("chown");
    }
    
    public void setOwner(final String owner) {
        this.createArg().setValue(owner);
        this.haveOwner = true;
    }
    
    @Override
    protected void checkConfiguration() {
        if (!this.haveOwner) {
            throw new BuildException("Required attribute owner not set in chown", this.getLocation());
        }
        super.checkConfiguration();
    }
    
    @Override
    public void setExecutable(final String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable" + " attribute", this.getLocation());
    }
}
