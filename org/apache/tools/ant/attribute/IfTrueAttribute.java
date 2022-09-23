// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.attribute;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.UnknownElement;

public class IfTrueAttribute extends BaseIfAttribute
{
    public boolean isEnabled(final UnknownElement el, final String value) {
        return this.convertResult(Project.toBoolean(value));
    }
    
    public static class Unless extends IfTrueAttribute
    {
        public Unless() {
            this.setPositive(false);
        }
    }
}
