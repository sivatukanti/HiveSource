// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.BuildException;

public class CvsUser
{
    private String userID;
    private String displayName;
    
    public void setDisplayname(final String displayName) {
        this.displayName = displayName;
    }
    
    public void setUserid(final String userID) {
        this.userID = userID;
    }
    
    public String getUserID() {
        return this.userID;
    }
    
    public String getDisplayname() {
        return this.displayName;
    }
    
    public void validate() throws BuildException {
        if (null == this.userID) {
            final String message = "Username attribute must be set.";
            throw new BuildException("Username attribute must be set.");
        }
        if (null == this.displayName) {
            final String message = "Displayname attribute must be set for userID " + this.userID;
            throw new BuildException(message);
        }
    }
}
