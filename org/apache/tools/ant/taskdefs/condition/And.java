// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import java.util.Enumeration;

public class And extends ConditionBase implements Condition
{
    public boolean eval() throws BuildException {
        final Enumeration e = this.getConditions();
        while (e.hasMoreElements()) {
            final Condition c = e.nextElement();
            if (!c.eval()) {
                return false;
            }
        }
        return true;
    }
}
