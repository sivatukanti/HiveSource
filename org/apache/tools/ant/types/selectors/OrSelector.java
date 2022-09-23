// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import java.util.Enumeration;
import java.io.File;

public class OrSelector extends BaseSelectorContainer
{
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        if (this.hasSelectors()) {
            buf.append("{orselect: ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }
    
    @Override
    public boolean isSelected(final File basedir, final String filename, final File file) {
        this.validate();
        final Enumeration<FileSelector> e = this.selectorElements();
        while (e.hasMoreElements()) {
            if (e.nextElement().isSelected(basedir, filename, file)) {
                return true;
            }
        }
        return false;
    }
}
