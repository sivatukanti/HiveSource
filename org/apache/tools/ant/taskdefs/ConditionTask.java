// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class ConditionTask extends ConditionBase
{
    private String property;
    private Object value;
    private Object alternative;
    
    public ConditionTask() {
        super("condition");
        this.property = null;
        this.value = "true";
        this.alternative = null;
    }
    
    public void setProperty(final String p) {
        this.property = p;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public void setValue(final String v) {
        this.setValue((Object)v);
    }
    
    public void setElse(final Object alt) {
        this.alternative = alt;
    }
    
    public void setElse(final String e) {
        this.setElse((Object)e);
    }
    
    public void execute() throws BuildException {
        if (this.countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into <" + this.getTaskName() + ">");
        }
        if (this.countConditions() < 1) {
            throw new BuildException("You must nest a condition into <" + this.getTaskName() + ">");
        }
        if (this.property == null) {
            throw new BuildException("The property attribute is required.");
        }
        final Condition c = this.getConditions().nextElement();
        if (c.eval()) {
            this.log("Condition true; setting " + this.property + " to " + this.value, 4);
            PropertyHelper.getPropertyHelper(this.getProject()).setNewProperty(this.property, this.value);
        }
        else if (this.alternative != null) {
            this.log("Condition false; setting " + this.property + " to " + this.alternative, 4);
            PropertyHelper.getPropertyHelper(this.getProject()).setNewProperty(this.property, this.alternative);
        }
        else {
            this.log("Condition false; not setting " + this.property, 4);
        }
    }
}
