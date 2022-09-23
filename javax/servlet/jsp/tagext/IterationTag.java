// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspException;

public interface IterationTag extends Tag
{
    public static final int EVAL_BODY_AGAIN = 2;
    
    int doAfterBody() throws JspException;
}
