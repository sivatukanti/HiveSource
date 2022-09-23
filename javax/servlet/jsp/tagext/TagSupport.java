// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

import java.util.Enumeration;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Hashtable;
import java.io.Serializable;

public class TagSupport implements IterationTag, Serializable
{
    private Tag parent;
    private Hashtable<String, Object> values;
    protected String id;
    protected PageContext pageContext;
    
    public static final Tag findAncestorWithClass(Tag from, final Class klass) {
        boolean isInterface = false;
        if (from == null || klass == null || (!Tag.class.isAssignableFrom(klass) && !(isInterface = klass.isInterface()))) {
            return null;
        }
        while (true) {
            final Tag tag = from.getParent();
            if (tag == null) {
                return null;
            }
            if ((isInterface && klass.isInstance(tag)) || klass.isAssignableFrom(tag.getClass())) {
                return tag;
            }
            from = tag;
        }
    }
    
    public int doStartTag() throws JspException {
        return 0;
    }
    
    public int doEndTag() throws JspException {
        return 6;
    }
    
    public int doAfterBody() throws JspException {
        return 0;
    }
    
    public void release() {
        this.parent = null;
        this.id = null;
        if (this.values != null) {
            this.values.clear();
        }
        this.values = null;
    }
    
    public void setParent(final Tag t) {
        this.parent = t;
    }
    
    public Tag getParent() {
        return this.parent;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setPageContext(final PageContext pageContext) {
        this.pageContext = pageContext;
    }
    
    public void setValue(final String k, final Object o) {
        if (this.values == null) {
            this.values = new Hashtable<String, Object>();
        }
        this.values.put(k, o);
    }
    
    public Object getValue(final String k) {
        if (this.values == null) {
            return null;
        }
        return this.values.get(k);
    }
    
    public void removeValue(final String k) {
        if (this.values != null) {
            this.values.remove(k);
        }
    }
    
    public Enumeration<String> getValues() {
        if (this.values == null) {
            return null;
        }
        return this.values.keys();
    }
}
