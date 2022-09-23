// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.attribute;

import org.apache.tools.ant.UnknownElement;

public class IfSetAttribute extends BaseIfAttribute
{
    public boolean isEnabled(final UnknownElement el, final String value) {
        return this.convertResult(this.getProject().getProperty(value) != null);
    }
    
    public static class Unless extends IfSetAttribute
    {
        public Unless() {
            this.setPositive(false);
        }
    }
}
