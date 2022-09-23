// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import java.util.Enumeration;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;

public class ServerDeploy extends Task
{
    private String action;
    private File source;
    private Vector vendorTools;
    
    public ServerDeploy() {
        this.vendorTools = new Vector();
    }
    
    public void addGeneric(final GenericHotDeploymentTool tool) {
        tool.setTask(this);
        this.vendorTools.addElement(tool);
    }
    
    public void addWeblogic(final WebLogicHotDeploymentTool tool) {
        tool.setTask(this);
        this.vendorTools.addElement(tool);
    }
    
    public void addJonas(final JonasHotDeploymentTool tool) {
        tool.setTask(this);
        this.vendorTools.addElement(tool);
    }
    
    @Override
    public void execute() throws BuildException {
        final Enumeration e = this.vendorTools.elements();
        while (e.hasMoreElements()) {
            final HotDeploymentTool tool = e.nextElement();
            tool.validateAttributes();
            tool.deploy();
        }
    }
    
    public String getAction() {
        return this.action;
    }
    
    public void setAction(final String action) {
        this.action = action;
    }
    
    public File getSource() {
        return this.source;
    }
    
    public void setSource(final File source) {
        this.source = source;
    }
}
