// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class TagAdapter implements Tag
{
    private SimpleTag simpleTagAdaptee;
    private Tag parent;
    private boolean parentDetermined;
    
    public TagAdapter(final SimpleTag adaptee) {
        if (adaptee == null) {
            throw new IllegalArgumentException();
        }
        this.simpleTagAdaptee = adaptee;
    }
    
    public void setPageContext(final PageContext pc) {
        throw new UnsupportedOperationException("Illegal to invoke setPageContext() on TagAdapter wrapper");
    }
    
    public void setParent(final Tag parentTag) {
        throw new UnsupportedOperationException("Illegal to invoke setParent() on TagAdapter wrapper");
    }
    
    public Tag getParent() {
        if (!this.parentDetermined) {
            final JspTag adapteeParent = this.simpleTagAdaptee.getParent();
            if (adapteeParent != null) {
                if (adapteeParent instanceof Tag) {
                    this.parent = (Tag)adapteeParent;
                }
                else {
                    this.parent = new TagAdapter((SimpleTag)adapteeParent);
                }
            }
            this.parentDetermined = true;
        }
        return this.parent;
    }
    
    public JspTag getAdaptee() {
        return this.simpleTagAdaptee;
    }
    
    public int doStartTag() throws JspException {
        throw new UnsupportedOperationException("Illegal to invoke doStartTag() on TagAdapter wrapper");
    }
    
    public int doEndTag() throws JspException {
        throw new UnsupportedOperationException("Illegal to invoke doEndTag() on TagAdapter wrapper");
    }
    
    public void release() {
        throw new UnsupportedOperationException("Illegal to invoke release() on TagAdapter wrapper");
    }
}
