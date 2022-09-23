// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class ScriptCondition extends AbstractScriptComponent implements Condition
{
    private boolean value;
    
    public ScriptCondition() {
        this.value = false;
    }
    
    public boolean eval() throws BuildException {
        this.initScriptRunner();
        this.executeScript("ant_condition");
        return this.getValue();
    }
    
    public boolean getValue() {
        return this.value;
    }
    
    public void setValue(final boolean value) {
        this.value = value;
    }
}
