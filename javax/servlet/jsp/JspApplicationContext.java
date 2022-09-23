// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp;

import javax.el.ELContextListener;
import javax.el.ExpressionFactory;
import javax.el.ELResolver;

public interface JspApplicationContext
{
    void addELResolver(final ELResolver p0);
    
    ExpressionFactory getExpressionFactory();
    
    void addELContextListener(final ELContextListener p0);
}
