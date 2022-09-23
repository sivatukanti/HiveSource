// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import java.util.Enumeration;

public class Xor extends ConditionBase implements Condition
{
    public boolean eval() throws BuildException {
        final Enumeration e = this.getConditions();
        boolean state = false;
        while (e.hasMoreElements()) {
            final Condition c = e.nextElement();
            state ^= c.eval();
        }
        return state;
    }
}
