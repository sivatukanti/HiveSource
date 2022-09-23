// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.attribute;

import org.apache.tools.ant.UnknownElement;

public class IfBlankAttribute extends BaseIfAttribute
{
    public boolean isEnabled(final UnknownElement el, final String value) {
        return this.convertResult(value == null || "".equals(value));
    }
    
    public static class Unless extends IfBlankAttribute
    {
        public Unless() {
            this.setPositive(false);
        }
    }
}
