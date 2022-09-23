// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class RegexpFactory extends RegexpMatcherFactory
{
    public Regexp newRegexp() throws BuildException {
        return this.newRegexp(null);
    }
    
    public Regexp newRegexp(final Project p) throws BuildException {
        String systemDefault = null;
        if (p == null) {
            systemDefault = System.getProperty("ant.regexp.regexpimpl");
        }
        else {
            systemDefault = p.getProperty("ant.regexp.regexpimpl");
        }
        if (systemDefault != null) {
            return this.createRegexpInstance(systemDefault);
        }
        return new Jdk14RegexpRegexp();
    }
    
    protected Regexp createRegexpInstance(final String classname) throws BuildException {
        return (Regexp)ClasspathUtils.newInstance(classname, RegexpFactory.class.getClassLoader(), Regexp.class);
    }
}
