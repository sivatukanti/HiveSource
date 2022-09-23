// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.PropertyHelper;
import java.util.Enumeration;
import org.apache.tools.ant.Project;

public class SelectSelector extends BaseSelectorContainer
{
    private Object ifCondition;
    private Object unlessCondition;
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        if (this.hasSelectors()) {
            buf.append("{select");
            if (this.ifCondition != null) {
                buf.append(" if: ");
                buf.append(this.ifCondition);
            }
            if (this.unlessCondition != null) {
                buf.append(" unless: ");
                buf.append(this.unlessCondition);
            }
            buf.append(" ");
            buf.append(super.toString());
            buf.append("}");
        }
        return buf.toString();
    }
    
    private SelectSelector getRef() {
        final Object o = this.getCheckedRef((Class<Object>)this.getClass(), "SelectSelector");
        return (SelectSelector)o;
    }
    
    @Override
    public boolean hasSelectors() {
        if (this.isReference()) {
            return this.getRef().hasSelectors();
        }
        return super.hasSelectors();
    }
    
    @Override
    public int selectorCount() {
        if (this.isReference()) {
            return this.getRef().selectorCount();
        }
        return super.selectorCount();
    }
    
    @Override
    public FileSelector[] getSelectors(final Project p) {
        if (this.isReference()) {
            return this.getRef().getSelectors(p);
        }
        return super.getSelectors(p);
    }
    
    @Override
    public Enumeration<FileSelector> selectorElements() {
        if (this.isReference()) {
            return this.getRef().selectorElements();
        }
        return super.selectorElements();
    }
    
    @Override
    public void appendSelector(final FileSelector selector) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        super.appendSelector(selector);
    }
    
    @Override
    public void verifySettings() {
        final int cnt = this.selectorCount();
        if (cnt < 0 || cnt > 1) {
            this.setError("Only one selector is allowed within the <selector> tag");
        }
    }
    
    public boolean passesConditions() {
        final PropertyHelper ph = PropertyHelper.getPropertyHelper(this.getProject());
        return ph.testIfCondition(this.ifCondition) && ph.testUnlessCondition(this.unlessCondition);
    }
    
    public void setIf(final Object ifProperty) {
        this.ifCondition = ifProperty;
    }
    
    public void setIf(final String ifProperty) {
        this.setIf((Object)ifProperty);
    }
    
    public void setUnless(final Object unlessProperty) {
        this.unlessCondition = unlessProperty;
    }
    
    public void setUnless(final String unlessProperty) {
        this.setUnless((Object)unlessProperty);
    }
    
    @Override
    public boolean isSelected(final File basedir, final String filename, final File file) {
        this.validate();
        if (!this.passesConditions()) {
            return false;
        }
        final Enumeration<FileSelector> e = this.selectorElements();
        if (!e.hasMoreElements()) {
            return true;
        }
        final FileSelector f = e.nextElement();
        return f.isSelected(basedir, filename, file);
    }
}
