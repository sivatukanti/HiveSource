// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

public class CvsTagEntry
{
    private String filename;
    private String prevRevision;
    private String revision;
    
    public CvsTagEntry(final String filename) {
        this(filename, null, null);
    }
    
    public CvsTagEntry(final String filename, final String revision) {
        this(filename, revision, null);
    }
    
    public CvsTagEntry(final String filename, final String revision, final String prevRevision) {
        this.filename = filename;
        this.revision = revision;
        this.prevRevision = prevRevision;
    }
    
    public String getFile() {
        return this.filename;
    }
    
    public String getRevision() {
        return this.revision;
    }
    
    public String getPreviousRevision() {
        return this.prevRevision;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.filename);
        if (this.revision == null) {
            buffer.append(" was removed");
            if (this.prevRevision != null) {
                buffer.append("; previous revision was ").append(this.prevRevision);
            }
        }
        else if (this.prevRevision == null) {
            buffer.append(" is new; current revision is ").append(this.revision);
        }
        else {
            buffer.append(" has changed from ").append(this.prevRevision).append(" to ").append(this.revision);
        }
        return buffer.toString();
    }
}
