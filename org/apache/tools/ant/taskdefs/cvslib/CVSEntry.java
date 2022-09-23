// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import java.util.Vector;
import java.util.Date;

public class CVSEntry
{
    private Date date;
    private String author;
    private final String comment;
    private final Vector files;
    
    public CVSEntry(final Date date, final String author, final String comment) {
        this.files = new Vector();
        this.date = date;
        this.author = author;
        this.comment = comment;
    }
    
    public void addFile(final String file, final String revision) {
        this.files.addElement(new RCSFile(file, revision));
    }
    
    public void addFile(final String file, final String revision, final String previousRevision) {
        this.files.addElement(new RCSFile(file, revision, previousRevision));
    }
    
    public Date getDate() {
        return this.date;
    }
    
    public void setAuthor(final String author) {
        this.author = author;
    }
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public Vector getFiles() {
        return this.files;
    }
    
    @Override
    public String toString() {
        return this.getAuthor() + "\n" + this.getDate() + "\n" + this.getFiles() + "\n" + this.getComment();
    }
}
