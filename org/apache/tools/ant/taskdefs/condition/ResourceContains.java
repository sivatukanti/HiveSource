// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import org.apache.tools.ant.util.FileUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.Project;

public class ResourceContains implements Condition
{
    private Project project;
    private String substring;
    private Resource resource;
    private String refid;
    private boolean casesensitive;
    
    public ResourceContains() {
        this.casesensitive = true;
    }
    
    public void setProject(final Project project) {
        this.project = project;
    }
    
    public Project getProject() {
        return this.project;
    }
    
    public void setResource(final String r) {
        this.resource = new FileResource(new File(r));
    }
    
    public void setRefid(final String refid) {
        this.refid = refid;
    }
    
    private void resolveRefid() {
        try {
            if (this.getProject() == null) {
                throw new BuildException("Cannot retrieve refid; project unset");
            }
            Object o = this.getProject().getReference(this.refid);
            if (!(o instanceof Resource)) {
                if (!(o instanceof ResourceCollection)) {
                    throw new BuildException("Illegal value at '" + this.refid + "': " + String.valueOf(o));
                }
                final ResourceCollection rc = (ResourceCollection)o;
                if (rc.size() == 1) {
                    o = rc.iterator().next();
                }
            }
            this.resource = (Resource)o;
        }
        finally {
            this.refid = null;
        }
    }
    
    public void setSubstring(final String substring) {
        this.substring = substring;
    }
    
    public void setCasesensitive(final boolean casesensitive) {
        this.casesensitive = casesensitive;
    }
    
    private void validate() {
        if (this.resource != null && this.refid != null) {
            throw new BuildException("Cannot set both resource and refid");
        }
        if (this.resource == null && this.refid != null) {
            this.resolveRefid();
        }
        if (this.resource == null || this.substring == null) {
            throw new BuildException("both resource and substring are required in <resourcecontains>");
        }
    }
    
    public synchronized boolean eval() throws BuildException {
        this.validate();
        if (this.substring.length() == 0) {
            if (this.getProject() != null) {
                this.getProject().log("Substring is empty; returning true", 3);
            }
            return true;
        }
        if (this.resource.getSize() == 0L) {
            return false;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(this.resource.getInputStream()));
            String contents = FileUtils.safeReadFully(reader);
            String sub = this.substring;
            if (!this.casesensitive) {
                contents = contents.toLowerCase();
                sub = sub.toLowerCase();
            }
            return contents.indexOf(sub) >= 0;
        }
        catch (IOException e) {
            throw new BuildException("There was a problem accessing resource : " + this.resource);
        }
        finally {
            FileUtils.close(reader);
        }
    }
}
