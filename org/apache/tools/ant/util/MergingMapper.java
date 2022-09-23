// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

public class MergingMapper implements FileNameMapper
{
    protected String[] mergedFile;
    
    public MergingMapper() {
        this.mergedFile = null;
    }
    
    public MergingMapper(final String to) {
        this.mergedFile = null;
        this.setTo(to);
    }
    
    public void setFrom(final String from) {
    }
    
    public void setTo(final String to) {
        this.mergedFile = new String[] { to };
    }
    
    public String[] mapFileName(final String sourceFileName) {
        return this.mergedFile;
    }
}
