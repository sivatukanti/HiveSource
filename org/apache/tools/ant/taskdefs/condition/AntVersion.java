// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.DeweyDecimal;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntVersion extends Task implements Condition
{
    private String atLeast;
    private String exactly;
    private String propertyname;
    
    public AntVersion() {
        this.atLeast = null;
        this.exactly = null;
        this.propertyname = null;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.propertyname == null) {
            throw new BuildException("'property' must be set.");
        }
        if (this.atLeast != null || this.exactly != null) {
            if (this.eval()) {
                this.getProject().setNewProperty(this.propertyname, this.getVersion().toString());
            }
        }
        else {
            this.getProject().setNewProperty(this.propertyname, this.getVersion().toString());
        }
    }
    
    public boolean eval() throws BuildException {
        this.validate();
        final DeweyDecimal actual = this.getVersion();
        if (null != this.atLeast) {
            return actual.isGreaterThanOrEqual(new DeweyDecimal(this.atLeast));
        }
        return null != this.exactly && actual.isEqual(new DeweyDecimal(this.exactly));
    }
    
    private void validate() throws BuildException {
        if (this.atLeast != null && this.exactly != null) {
            throw new BuildException("Only one of atleast or exactly may be set.");
        }
        if (null == this.atLeast && null == this.exactly) {
            throw new BuildException("One of atleast or exactly must be set.");
        }
        if (this.atLeast != null) {
            try {
                new DeweyDecimal(this.atLeast);
                return;
            }
            catch (NumberFormatException e) {
                throw new BuildException("The 'atleast' attribute is not a Dewey Decimal eg 1.1.0 : " + this.atLeast);
            }
        }
        try {
            new DeweyDecimal(this.exactly);
        }
        catch (NumberFormatException e) {
            throw new BuildException("The 'exactly' attribute is not a Dewey Decimal eg 1.1.0 : " + this.exactly);
        }
    }
    
    private DeweyDecimal getVersion() {
        final Project p = new Project();
        p.init();
        final char[] versionString = p.getProperty("ant.version").toCharArray();
        final StringBuffer sb = new StringBuffer();
        boolean foundFirstDigit = false;
        for (int i = 0; i < versionString.length; ++i) {
            if (Character.isDigit(versionString[i])) {
                sb.append(versionString[i]);
                foundFirstDigit = true;
            }
            if (versionString[i] == '.' && foundFirstDigit) {
                sb.append(versionString[i]);
            }
            if (Character.isLetter(versionString[i]) && foundFirstDigit) {
                break;
            }
        }
        return new DeweyDecimal(sb.toString());
    }
    
    public String getAtLeast() {
        return this.atLeast;
    }
    
    public void setAtLeast(final String atLeast) {
        this.atLeast = atLeast;
    }
    
    public String getExactly() {
        return this.exactly;
    }
    
    public void setExactly(final String exactly) {
        this.exactly = exactly;
    }
    
    public String getProperty() {
        return this.propertyname;
    }
    
    public void setProperty(final String propertyname) {
        this.propertyname = propertyname;
    }
}
