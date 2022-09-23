// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;

public class Chgrp extends AbstractAccessTask
{
    private boolean haveGroup;
    
    public Chgrp() {
        this.haveGroup = false;
        super.setExecutable("chgrp");
    }
    
    public void setGroup(final String group) {
        this.createArg().setValue(group);
        this.haveGroup = true;
    }
    
    @Override
    protected void checkConfiguration() {
        if (!this.haveGroup) {
            throw new BuildException("Required attribute group not set in chgrp", this.getLocation());
        }
        super.checkConfiguration();
    }
    
    @Override
    public void setExecutable(final String e) {
        throw new BuildException(this.getTaskType() + " doesn't support the executable" + " attribute", this.getLocation());
    }
}
