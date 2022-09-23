// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public interface Tag extends JspTag
{
    public static final int SKIP_BODY = 0;
    public static final int EVAL_BODY_INCLUDE = 1;
    public static final int SKIP_PAGE = 5;
    public static final int EVAL_PAGE = 6;
    
    void setPageContext(final PageContext p0);
    
    void setParent(final Tag p0);
    
    Tag getParent();
    
    int doStartTag() throws JspException;
    
    int doEndTag() throws JspException;
    
    void release();
}
