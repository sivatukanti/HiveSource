// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.property.LocalProperties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Local extends Task
{
    private String name;
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public void execute() {
        if (this.name == null) {
            throw new BuildException("Missing attribute name");
        }
        LocalProperties.get(this.getProject()).addLocal(this.name);
    }
}
