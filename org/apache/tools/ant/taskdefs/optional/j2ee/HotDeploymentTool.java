// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;

public interface HotDeploymentTool
{
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_DEPLOY = "deploy";
    public static final String ACTION_LIST = "list";
    public static final String ACTION_UNDEPLOY = "undeploy";
    public static final String ACTION_UPDATE = "update";
    
    void validateAttributes() throws BuildException;
    
    void deploy() throws BuildException;
    
    void setTask(final ServerDeploy p0);
}
