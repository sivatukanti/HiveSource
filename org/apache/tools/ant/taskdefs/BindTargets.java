// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import org.apache.tools.ant.ProjectHelper;
import java.util.List;
import org.apache.tools.ant.Task;

public class BindTargets extends Task
{
    private String extensionPoint;
    private List<String> targets;
    private ProjectHelper.OnMissingExtensionPoint onMissingExtensionPoint;
    
    public BindTargets() {
        this.targets = new ArrayList<String>();
    }
    
    public void setExtensionPoint(final String extensionPoint) {
        this.extensionPoint = extensionPoint;
    }
    
    public void setOnMissingExtensionPoint(final String onMissingExtensionPoint) {
        try {
            this.onMissingExtensionPoint = ProjectHelper.OnMissingExtensionPoint.valueOf(onMissingExtensionPoint);
        }
        catch (IllegalArgumentException e) {
            throw new BuildException("Invalid onMissingExtensionPoint: " + onMissingExtensionPoint);
        }
    }
    
    public void setOnMissingExtensionPoint(final ProjectHelper.OnMissingExtensionPoint onMissingExtensionPoint) {
        this.onMissingExtensionPoint = onMissingExtensionPoint;
    }
    
    public void setTargets(final String target) {
        final String[] inputs = target.split(",");
        for (int i = 0; i < inputs.length; ++i) {
            final String input = inputs[i].trim();
            if (input.length() > 0) {
                this.targets.add(input);
            }
        }
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.extensionPoint == null) {
            throw new BuildException("extensionPoint required", this.getLocation());
        }
        if (this.getOwningTarget() == null || !"".equals(this.getOwningTarget().getName())) {
            throw new BuildException("bindtargets only allowed as a top-level task");
        }
        if (this.onMissingExtensionPoint == null) {
            this.onMissingExtensionPoint = ProjectHelper.OnMissingExtensionPoint.FAIL;
        }
        final ProjectHelper helper = this.getProject().getReference("ant.projectHelper");
        final Iterator<String> itTarget = this.targets.iterator();
        while (itTarget.hasNext()) {
            helper.getExtensionStack().add(new String[] { this.extensionPoint, itTarget.next(), this.onMissingExtensionPoint.name() });
        }
    }
}
