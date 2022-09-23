// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Vector;
import org.apache.tools.ant.util.regexp.RegexpUtil;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;
import org.apache.tools.ant.util.regexp.RegexpMatcher;

public class RegexpPatternMapper implements FileNameMapper
{
    private static final int DECIMAL = 10;
    protected RegexpMatcher reg;
    protected char[] to;
    protected StringBuffer result;
    private boolean handleDirSep;
    private int regexpOptions;
    
    public RegexpPatternMapper() throws BuildException {
        this.reg = null;
        this.to = null;
        this.result = new StringBuffer();
        this.handleDirSep = false;
        this.regexpOptions = 0;
        this.reg = new RegexpMatcherFactory().newRegexpMatcher();
    }
    
    public void setHandleDirSep(final boolean handleDirSep) {
        this.handleDirSep = handleDirSep;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.regexpOptions = RegexpUtil.asOptions(caseSensitive);
    }
    
    public void setFrom(final String from) throws BuildException {
        if (from != null) {
            try {
                this.reg.setPattern(from);
                return;
            }
            catch (NoClassDefFoundError e) {
                throw new BuildException("Cannot load regular expression matcher", e);
            }
            throw new BuildException("this mapper requires a 'from' attribute");
        }
        throw new BuildException("this mapper requires a 'from' attribute");
    }
    
    public void setTo(final String to) {
        if (to != null) {
            this.to = to.toCharArray();
            return;
        }
        throw new BuildException("this mapper requires a 'to' attribute");
    }
    
    public String[] mapFileName(String sourceFileName) {
        if (this.handleDirSep && sourceFileName.indexOf("\\") != -1) {
            sourceFileName = sourceFileName.replace('\\', '/');
        }
        if (this.reg == null || this.to == null || !this.reg.matches(sourceFileName, this.regexpOptions)) {
            return null;
        }
        return new String[] { this.replaceReferences(sourceFileName) };
    }
    
    protected String replaceReferences(final String source) {
        final Vector v = this.reg.getGroups(source, this.regexpOptions);
        this.result.setLength(0);
        for (int i = 0; i < this.to.length; ++i) {
            if (this.to[i] == '\\') {
                if (++i < this.to.length) {
                    final int value = Character.digit(this.to[i], 10);
                    if (value > -1) {
                        this.result.append(v.elementAt(value));
                    }
                    else {
                        this.result.append(this.to[i]);
                    }
                }
                else {
                    this.result.append('\\');
                }
            }
            else {
                this.result.append(this.to[i]);
            }
        }
        return this.result.substring(0);
    }
}
