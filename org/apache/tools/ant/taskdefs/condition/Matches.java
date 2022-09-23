// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.ProjectComponent;

public class Matches extends ProjectComponent implements Condition
{
    private String string;
    private boolean caseSensitive;
    private boolean multiLine;
    private boolean singleLine;
    private RegularExpression regularExpression;
    
    public Matches() {
        this.caseSensitive = true;
        this.multiLine = false;
        this.singleLine = false;
    }
    
    public void setString(final String string) {
        this.string = string;
    }
    
    public void setPattern(final String pattern) {
        if (this.regularExpression != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        (this.regularExpression = new RegularExpression()).setPattern(pattern);
    }
    
    public void addRegexp(final RegularExpression regularExpression) {
        if (this.regularExpression != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        this.regularExpression = regularExpression;
    }
    
    public void setCasesensitive(final boolean b) {
        this.caseSensitive = b;
    }
    
    public void setMultiline(final boolean b) {
        this.multiLine = b;
    }
    
    public void setSingleLine(final boolean b) {
        this.singleLine = b;
    }
    
    public boolean eval() throws BuildException {
        if (this.string == null) {
            throw new BuildException("Parameter string is required in matches.");
        }
        if (this.regularExpression == null) {
            throw new BuildException("Missing pattern in matches.");
        }
        final int options = RegexpUtil.asOptions(this.caseSensitive, this.multiLine, this.singleLine);
        final Regexp regexp = this.regularExpression.getRegexp(this.getProject());
        return regexp.matches(this.string, options);
    }
}
