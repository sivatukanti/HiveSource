// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.util.regexp.RegexpUtil;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.types.RegularExpression;

public class Name implements ResourceSelector
{
    private String regex;
    private String pattern;
    private boolean cs;
    private boolean handleDirSep;
    private RegularExpression reg;
    private Regexp expression;
    private Project project;
    
    public Name() {
        this.regex = null;
        this.cs = true;
        this.handleDirSep = false;
    }
    
    public void setProject(final Project p) {
        this.project = p;
    }
    
    public void setName(final String n) {
        this.pattern = n;
    }
    
    public String getName() {
        return this.pattern;
    }
    
    public void setRegex(final String r) {
        this.regex = r;
        this.reg = null;
    }
    
    public String getRegex() {
        return this.regex;
    }
    
    public void setCaseSensitive(final boolean b) {
        this.cs = b;
    }
    
    public boolean isCaseSensitive() {
        return this.cs;
    }
    
    public void setHandleDirSep(final boolean handleDirSep) {
        this.handleDirSep = handleDirSep;
    }
    
    public boolean doesHandledirSep() {
        return this.handleDirSep;
    }
    
    public boolean isSelected(final Resource r) {
        final String n = r.getName();
        if (this.matches(n)) {
            return true;
        }
        final String s = r.toString();
        return !s.equals(n) && this.matches(s);
    }
    
    private boolean matches(final String name) {
        if (this.pattern != null) {
            return SelectorUtils.match(this.modify(this.pattern), this.modify(name), this.cs);
        }
        if (this.reg == null) {
            (this.reg = new RegularExpression()).setPattern(this.regex);
            this.expression = this.reg.getRegexp(this.project);
        }
        return this.expression.matches(this.modify(name), RegexpUtil.asOptions(this.cs));
    }
    
    private String modify(final String s) {
        if (s == null || !this.handleDirSep || s.indexOf("\\") == -1) {
            return s;
        }
        return s.replace('\\', '/');
    }
}
