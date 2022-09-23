// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Exit extends Task
{
    private String message;
    private Object ifCondition;
    private Object unlessCondition;
    private NestedCondition nestedCondition;
    private Integer status;
    
    public void setMessage(final String value) {
        this.message = value;
    }
    
    public void setIf(final Object c) {
        this.ifCondition = c;
    }
    
    public void setIf(final String c) {
        this.setIf((Object)c);
    }
    
    public void setUnless(final Object c) {
        this.unlessCondition = c;
    }
    
    public void setUnless(final String c) {
        this.setUnless((Object)c);
    }
    
    public void setStatus(final int i) {
        this.status = new Integer(i);
    }
    
    @Override
    public void execute() throws BuildException {
        final boolean fail = this.nestedConditionPresent() ? this.testNestedCondition() : (this.testIfCondition() && this.testUnlessCondition());
        if (fail) {
            String text = null;
            if (this.message != null && this.message.trim().length() > 0) {
                text = this.message.trim();
            }
            else {
                if (this.ifCondition != null && !"".equals(this.ifCondition) && this.testIfCondition()) {
                    text = "if=" + this.ifCondition;
                }
                if (this.unlessCondition != null && !"".equals(this.unlessCondition) && this.testUnlessCondition()) {
                    if (text == null) {
                        text = "";
                    }
                    else {
                        text += " and ";
                    }
                    text = text + "unless=" + this.unlessCondition;
                }
                if (this.nestedConditionPresent()) {
                    text = "condition satisfied";
                }
                else if (text == null) {
                    text = "No message";
                }
            }
            this.log("failing due to " + text, 4);
            throw (this.status == null) ? new BuildException(text) : new ExitStatusException(text, this.status);
        }
    }
    
    public void addText(final String msg) {
        if (this.message == null) {
            this.message = "";
        }
        this.message += this.getProject().replaceProperties(msg);
    }
    
    public ConditionBase createCondition() {
        if (this.nestedCondition != null) {
            throw new BuildException("Only one nested condition is allowed.");
        }
        return this.nestedCondition = new NestedCondition();
    }
    
    private boolean testIfCondition() {
        return PropertyHelper.getPropertyHelper(this.getProject()).testIfCondition(this.ifCondition);
    }
    
    private boolean testUnlessCondition() {
        return PropertyHelper.getPropertyHelper(this.getProject()).testUnlessCondition(this.unlessCondition);
    }
    
    private boolean testNestedCondition() {
        final boolean result = this.nestedConditionPresent();
        if ((result && this.ifCondition != null) || this.unlessCondition != null) {
            throw new BuildException("Nested conditions not permitted in conjunction with if/unless attributes");
        }
        return result && this.nestedCondition.eval();
    }
    
    private boolean nestedConditionPresent() {
        return this.nestedCondition != null;
    }
    
    private static class NestedCondition extends ConditionBase implements Condition
    {
        public boolean eval() {
            if (this.countConditions() != 1) {
                throw new BuildException("A single nested condition is required.");
            }
            return this.getConditions().nextElement().eval();
        }
    }
}
