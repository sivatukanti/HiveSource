// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

public class NotSelector extends NoneSelector
{
    public NotSelector() {
    }
    
    public NotSelector(final FileSelector other) {
        this();
        this.appendSelector(other);
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if (this.hasSelectors()) {
            buf.append("{notselect: ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }
    
    @Override
    public void verifySettings() {
        if (this.selectorCount() != 1) {
            this.setError("One and only one selector is allowed within the <not> tag");
        }
    }
}
