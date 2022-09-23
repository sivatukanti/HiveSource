// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;

public class BodyTagSupport extends TagSupport implements BodyTag
{
    protected BodyContent bodyContent;
    
    @Override
    public int doStartTag() throws JspException {
        return 2;
    }
    
    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }
    
    public void setBodyContent(final BodyContent b) {
        this.bodyContent = b;
    }
    
    public void doInitBody() throws JspException {
    }
    
    @Override
    public int doAfterBody() throws JspException {
        return 0;
    }
    
    @Override
    public void release() {
        this.bodyContent = null;
        super.release();
    }
    
    public BodyContent getBodyContent() {
        return this.bodyContent;
    }
    
    public JspWriter getPreviousOut() {
        return this.bodyContent.getEnclosingWriter();
    }
}
