// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;

public class Not extends ConditionBase implements Condition
{
    public boolean eval() throws BuildException {
        if (this.countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into <not>");
        }
        if (this.countConditions() < 1) {
            throw new BuildException("You must nest a condition into <not>");
        }
        return !this.getConditions().nextElement().eval();
    }
}
