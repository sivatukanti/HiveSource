// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class CallTarget extends Task
{
    private Ant callee;
    private boolean inheritAll;
    private boolean inheritRefs;
    private boolean targetSet;
    
    public CallTarget() {
        this.inheritAll = true;
        this.inheritRefs = false;
        this.targetSet = false;
    }
    
    public void setInheritAll(final boolean inherit) {
        this.inheritAll = inherit;
    }
    
    public void setInheritRefs(final boolean inheritRefs) {
        this.inheritRefs = inheritRefs;
    }
    
    @Override
    public void init() {
        (this.callee = new Ant(this)).init();
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.callee == null) {
            this.init();
        }
        if (!this.targetSet) {
            throw new BuildException("Attribute target or at least one nested target is required.", this.getLocation());
        }
        this.callee.setAntfile(this.getProject().getProperty("ant.file"));
        this.callee.setInheritAll(this.inheritAll);
        this.callee.setInheritRefs(this.inheritRefs);
        this.callee.execute();
    }
    
    public Property createParam() {
        if (this.callee == null) {
            this.init();
        }
        return this.callee.createProperty();
    }
    
    public void addReference(final Ant.Reference r) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.addReference(r);
    }
    
    public void addPropertyset(final PropertySet ps) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.addPropertyset(ps);
    }
    
    public void setTarget(final String target) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.setTarget(target);
        this.targetSet = true;
    }
    
    public void addConfiguredTarget(final Ant.TargetElement t) {
        if (this.callee == null) {
            this.init();
        }
        this.callee.addConfiguredTarget(t);
        this.targetSet = true;
    }
    
    public void handleOutput(final String output) {
        if (this.callee != null) {
            this.callee.handleOutput(output);
        }
        else {
            super.handleOutput(output);
        }
    }
    
    public int handleInput(final byte[] buffer, final int offset, final int length) throws IOException {
        if (this.callee != null) {
            return this.callee.handleInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }
    
    public void handleFlush(final String output) {
        if (this.callee != null) {
            this.callee.handleFlush(output);
        }
        else {
            super.handleFlush(output);
        }
    }
    
    public void handleErrorOutput(final String output) {
        if (this.callee != null) {
            this.callee.handleErrorOutput(output);
        }
        else {
            super.handleErrorOutput(output);
        }
    }
    
    public void handleErrorFlush(final String output) {
        if (this.callee != null) {
            this.callee.handleErrorFlush(output);
        }
        else {
            super.handleErrorFlush(output);
        }
    }
}
