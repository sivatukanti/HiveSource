// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;

public class GlobPatternMapper implements FileNameMapper
{
    protected String fromPrefix;
    protected String fromPostfix;
    protected int prefixLength;
    protected int postfixLength;
    protected String toPrefix;
    protected String toPostfix;
    private boolean fromContainsStar;
    private boolean toContainsStar;
    private boolean handleDirSep;
    private boolean caseSensitive;
    
    public GlobPatternMapper() {
        this.fromPrefix = null;
        this.fromPostfix = null;
        this.toPrefix = null;
        this.toPostfix = null;
        this.fromContainsStar = false;
        this.toContainsStar = false;
        this.handleDirSep = false;
        this.caseSensitive = true;
    }
    
    public void setHandleDirSep(final boolean handleDirSep) {
        this.handleDirSep = handleDirSep;
    }
    
    public boolean getHandleDirSep() {
        return this.handleDirSep;
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public void setFrom(final String from) {
        if (from != null) {
            final int index = from.lastIndexOf("*");
            if (index == -1) {
                this.fromPrefix = from;
                this.fromPostfix = "";
            }
            else {
                this.fromPrefix = from.substring(0, index);
                this.fromPostfix = from.substring(index + 1);
                this.fromContainsStar = true;
            }
            this.prefixLength = this.fromPrefix.length();
            this.postfixLength = this.fromPostfix.length();
            return;
        }
        throw new BuildException("this mapper requires a 'from' attribute");
    }
    
    public void setTo(final String to) {
        if (to != null) {
            final int index = to.lastIndexOf("*");
            if (index == -1) {
                this.toPrefix = to;
                this.toPostfix = "";
            }
            else {
                this.toPrefix = to.substring(0, index);
                this.toPostfix = to.substring(index + 1);
                this.toContainsStar = true;
            }
            return;
        }
        throw new BuildException("this mapper requires a 'to' attribute");
    }
    
    public String[] mapFileName(final String sourceFileName) {
        final String modName = this.modifyName(sourceFileName);
        if (this.fromPrefix == null || sourceFileName.length() < this.prefixLength + this.postfixLength || (!this.fromContainsStar && !modName.equals(this.modifyName(this.fromPrefix))) || (this.fromContainsStar && (!modName.startsWith(this.modifyName(this.fromPrefix)) || !modName.endsWith(this.modifyName(this.fromPostfix))))) {
            return null;
        }
        return new String[] { this.toPrefix + (this.toContainsStar ? (this.extractVariablePart(sourceFileName) + this.toPostfix) : "") };
    }
    
    protected String extractVariablePart(final String name) {
        return name.substring(this.prefixLength, name.length() - this.postfixLength);
    }
    
    private String modifyName(String name) {
        if (!this.caseSensitive) {
            name = name.toLowerCase();
        }
        if (this.handleDirSep && name.indexOf(92) != -1) {
            name = name.replace('\\', '/');
        }
        return name;
    }
}
