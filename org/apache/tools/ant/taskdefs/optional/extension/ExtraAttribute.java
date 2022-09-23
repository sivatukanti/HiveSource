// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;

public class ExtraAttribute
{
    private String name;
    private String value;
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    String getName() {
        return this.name;
    }
    
    String getValue() {
        return this.value;
    }
    
    public void validate() throws BuildException {
        if (null == this.name) {
            final String message = "Missing name from parameter.";
            throw new BuildException("Missing name from parameter.");
        }
        if (null == this.value) {
            final String message = "Missing value from parameter " + this.name + ".";
            throw new BuildException(message);
        }
    }
}
